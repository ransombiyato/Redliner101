package com.ransombiyato.demiforge

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.ransombiyato.demiforge.core.ModManager
import com.ransombiyato.demiforge.core.adapters.DeltaruneAdapter
import com.ransombiyato.demiforge.core.adapters.DeltarunePayload
import com.ransombiyato.demiforge.core.adapters.DeltaruneWorkspace
import com.ransombiyato.demiforge.core.adapters.DeltaruneWorkspaceInspector
import com.ransombiyato.demiforge.core.adapters.GameInspection
import com.ransombiyato.demiforge.core.adapters.IntegrationStatus
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.PatchMode
import com.ransombiyato.demiforge.core.storage.FileTree
import com.ransombiyato.demiforge.core.storage.ModStorage
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import java.util.Properties

data class HadrianDocumentPayload(
    val relativePath: String,
    val sizeBytes: Long,
    val document: DocumentFile,
    val corePayload: DeltarunePayload,
)

data class HadrianDocumentWorkspace(
    val uri: Uri,
    val root: DocumentFile,
    val payloads: List<HadrianDocumentPayload>,
    val layoutFingerprint: String,
    val contentFingerprint: String,
) {
    val recognised: Boolean get() = payloads.isNotEmpty()
}

data class HadrianPatchAction(
    val modId: String,
    val modName: String,
    val source: Path,
    val targetPath: String,
    val target: DocumentFile,
    val sourceBytes: Long,
    val backupBytes: Long,
)

data class HadrianPreflight(
    val workspace: HadrianDocumentWorkspace,
    val actions: List<HadrianPatchAction>,
    val issues: List<String>,
) {
    val ready: Boolean get() = issues.isEmpty() && actions.isNotEmpty()
    val backupBytes: Long get() = actions.sumOf { it.backupBytes }
    val installBytes: Long get() = actions.sumOf { it.sourceBytes }
}

data class HadrianBackupSummary(
    val id: String,
    val createdAt: String,
    val actionCount: Int,
)

/**
 * Real document-tree patcher for a user-selected Hadrian Android-port workspace. The service has
 * no broad storage permission and never attempts to access an installed APK or private app data.
 */
class HadrianWorkspaceService(private val context: Context) {
    private val resolver = context.contentResolver
    private val preferences = context.getSharedPreferences("hadrian-workspace", Context.MODE_PRIVATE)
    private val backupsRoot: Path = context.filesDir.toPath().resolve("demiforge/hadrian-backups")

    fun selectedWorkspace(): HadrianDocumentWorkspace? {
        val rawUri = preferences.getString(KEY_URI, null) ?: return null
        return runCatching { inspect(Uri.parse(rawUri)) }.getOrNull()
    }

    fun select(uri: Uri, grantedFlags: Int): HadrianDocumentWorkspace {
        val flags = grantedFlags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        require(flags and Intent.FLAG_GRANT_READ_URI_PERMISSION != 0) { "The selected workspace did not grant read access." }
        require(flags and Intent.FLAG_GRANT_WRITE_URI_PERMISSION != 0) { "The selected workspace did not grant write access." }
        resolver.takePersistableUriPermission(uri, flags)
        val workspace = inspect(uri)
        require(workspace.recognised) { "No game.droid, data.droid, or WAD payload was found in the selected directory." }
        preferences.edit()
            .putString(KEY_URI, uri.toString())
            .remove(KEY_TRUST_URI)
            .remove(KEY_TRUST_LAYOUT)
            .apply()
        return workspace
    }

    fun forgetWorkspace() {
        val uri = preferences.getString(KEY_URI, null)?.let(Uri::parse)
        if (uri != null) runCatching {
            resolver.releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        preferences.edit()
            .remove(KEY_URI)
            .remove(KEY_TRUST_URI)
            .remove(KEY_TRUST_LAYOUT)
            .apply()
    }

    fun trust(workspace: HadrianDocumentWorkspace) {
        require(workspace.recognised) { "Cannot trust a workspace without an Android Deltarune payload." }
        preferences.edit()
            .putString(KEY_TRUST_URI, workspace.uri.toString())
            .putString(KEY_TRUST_LAYOUT, workspace.layoutFingerprint)
            .apply()
    }

    fun isTrusted(workspace: HadrianDocumentWorkspace): Boolean =
        preferences.getString(KEY_TRUST_URI, null) == workspace.uri.toString() &&
            preferences.getString(KEY_TRUST_LAYOUT, null) == workspace.layoutFingerprint

    fun gameInspection(): GameInspection {
        val workspace = selectedWorkspace() ?: return GameInspection(
            gameId = DeltaruneAdapter().gameId,
            displayName = DeltaruneAdapter().displayName,
            status = IntegrationStatus.NOT_CONFIGURED,
            message = "Select the externally accessible Hadrian-port workspace to begin."
        )
        return when {
            !workspace.recognised -> GameInspection(
                DeltaruneAdapter().gameId,
                DeltaruneAdapter().displayName,
                IntegrationStatus.LIMITED,
                message = "The selected folder no longer contains a recognised Android Deltarune payload."
            )
            !isTrusted(workspace) -> GameInspection(
                DeltaruneAdapter().gameId,
                DeltaruneAdapter().displayName,
                IntegrationStatus.LIMITED,
                version = DeltaruneWorkspaceInspector.HADRIAN_VERSION_ID,
                message = "Found ${workspace.payloads.size} payload candidate(s). Review and trust workspace ${workspace.layoutFingerprint} before any mod can write."
            )
            else -> GameInspection(
                DeltaruneAdapter().gameId,
                DeltaruneAdapter().displayName,
                IntegrationStatus.READY,
                version = DeltaruneWorkspaceInspector.HADRIAN_VERSION_ID,
                message = "Verified Hadrian-port workspace with ${workspace.payloads.size} payload candidate(s)."
            )
        }
    }

    fun preflight(storage: ModStorage, manager: ModManager, adapter: DeltaruneAdapter): HadrianPreflight {
        val workspace = requireNotNull(selectedWorkspace()) { "No Hadrian-port workspace has been selected." }
        val issues = mutableListOf<String>()
        if (!workspace.recognised) issues += "The selected workspace contains no supported Android Deltarune payload."
        if (!isTrusted(workspace)) issues += "Review the detected payload paths and trust this workspace before applying mods."
        if (manager.safeMode) issues += "Safe mode is on; no real game files may be modified."

        val inspection = gameInspection()
        val resolution = manager.resolve(adapter, inspection)
        resolution.issues.forEach { issues += it.message }
        val knownTargets = workspace.payloads.associateBy { it.relativePath }
        val usedTargets = mutableSetOf<String>()
        val actions = mutableListOf<HadrianPatchAction>()

        if (issues.isEmpty()) {
            resolution.loadOrder.forEach { manifest ->
                validateManifestForHadrian(manifest, workspace, knownTargets, usedTargets, storage, issues, actions)
            }
        }
        if (actions.isEmpty() && issues.isEmpty()) issues += "No patch operations are enabled."
        return HadrianPreflight(workspace, actions, issues.distinct())
    }

    fun apply(preflight: HadrianPreflight): HadrianBackupSummary {
        require(preflight.ready) { "Preflight is not ready: ${preflight.issues.joinToString(" ")}" }
        require(isTrusted(preflight.workspace)) { "The workspace trust marker changed; review it again before writing." }
        val record = createBackup(preflight)
        try {
            preflight.actions.forEach { action ->
                Files.newInputStream(action.source).use { input -> writeDocument(action.target, input) }
            }
        } catch (exception: Exception) {
            runCatching { restore(record, preflight.workspace) }
            throw IllegalStateException("Write failed; the backup was restored. ${exception.message}", exception)
        }
        preferences.edit().putString(KEY_LAST_BACKUP, record.id).apply()
        return record
    }

    fun latestBackup(): HadrianBackupSummary? {
        val id = preferences.getString(KEY_LAST_BACKUP, null) ?: return null
        val metadata = backupDirectory(id).resolve("record.properties")
        if (!Files.isRegularFile(metadata)) return null
        return readBackupSummary(id, metadata)
    }

    fun restoreLatest(workspace: HadrianDocumentWorkspace = requireNotNull(selectedWorkspace())): HadrianBackupSummary {
        require(isTrusted(workspace)) { "The selected workspace has not been reviewed or has changed." }
        val id = requireNotNull(preferences.getString(KEY_LAST_BACKUP, null)) { "No Hadrian-port backup exists yet." }
        val summary = readBackupSummary(id, backupDirectory(id).resolve("record.properties"))
        restore(summary, workspace)
        return summary
    }

    private fun validateManifestForHadrian(
        manifest: ModManifest,
        workspace: HadrianDocumentWorkspace,
        knownTargets: Map<String, HadrianDocumentPayload>,
        usedTargets: MutableSet<String>,
        storage: ModStorage,
        issues: MutableList<String>,
        actions: MutableList<HadrianPatchAction>,
    ) {
        if (manifest.targetGame != DeltaruneAdapter().gameId) {
            issues += "${manifest.id} targets ${manifest.targetGame}, not ${DeltaruneAdapter().gameId}."
            return
        }
        manifest.patches.forEach { patch ->
            if (patch.mode != PatchMode.COPY) {
                issues += "${manifest.id}: overlay mode is not supported for a real Hadrian-port workspace."
                return@forEach
            }
            val target = knownTargets[patch.target]
            if (target == null) {
                issues += "${manifest.id}: ${patch.target} is not a discovered game payload in this workspace."
                return@forEach
            }
            if (!usedTargets.add(patch.target)) {
                issues += "Multiple enabled mods replace ${patch.target}; resolve this conflict explicitly before applying."
                return@forEach
            }
            val source = runCatching { FileTree.safeChild(storage.sourceFor(manifest), patch.source) }.getOrElse {
                issues += "${manifest.id}: unsafe source path ${patch.source}."
                return@forEach
            }
            if (!Files.isRegularFile(source)) {
                issues += "${manifest.id}: replacement payload ${patch.source} is missing."
                return@forEach
            }
            if (!target.document.canWrite()) {
                issues += "${manifest.id}: ${patch.target} is not writable through Android's selected-directory grant."
                return@forEach
            }
            actions += HadrianPatchAction(
                modId = manifest.id,
                modName = manifest.name,
                source = source,
                targetPath = target.relativePath,
                target = target.document,
                sourceBytes = Files.size(source),
                backupBytes = target.sizeBytes,
            )
        }
    }

    private fun inspect(uri: Uri): HadrianDocumentWorkspace {
        val root = requireNotNull(DocumentFile.fromTreeUri(context, uri)) { "The selected directory is unavailable." }
        val payloads = mutableListOf<HadrianDocumentPayload>()
        inventory(root, "", 0, payloads)
        val corePayloads = payloads.map { it.corePayload }
        return HadrianDocumentWorkspace(
            uri = uri,
            root = root,
            payloads = payloads.sortedBy { it.relativePath.lowercase() },
            layoutFingerprint = DeltaruneWorkspaceInspector.fingerprint(corePayloads, includeSize = false),
            contentFingerprint = DeltaruneWorkspaceInspector.fingerprint(corePayloads, includeSize = true),
        )
    }

    private fun inventory(directory: DocumentFile, prefix: String, depth: Int, output: MutableList<HadrianDocumentPayload>) {
        if (depth > MAX_DEPTH || !directory.isDirectory) return
        directory.listFiles().forEach { child ->
            val name = child.name ?: return@forEach
            if (name.isBlank() || name.contains('/') || name.contains('\\') || name == "." || name == "..") return@forEach
            val relative = if (prefix.isBlank()) name else "$prefix/$name"
            when {
                child.isDirectory -> inventory(child, relative, depth + 1, output)
                child.isFile -> DeltaruneWorkspaceInspector.payloadKind(name)?.let { kind ->
                    output += HadrianDocumentPayload(
                        relativePath = relative,
                        sizeBytes = child.length(),
                        document = child,
                        corePayload = DeltarunePayload(relative, child.length(), kind),
                    )
                }
            }
        }
    }

    private fun createBackup(preflight: HadrianPreflight): HadrianBackupSummary {
        Files.createDirectories(backupsRoot)
        val id = "hadrian-${System.currentTimeMillis()}"
        val root = backupDirectory(id)
        Files.createDirectories(root.resolve("original"))
        preflight.actions.forEach { action ->
            val destination = FileTree.safeChild(root.resolve("original"), action.targetPath)
            Files.createDirectories(destination.parent)
            resolver.openInputStream(action.target.uri).use { input ->
                requireNotNull(input) { "Cannot read ${action.targetPath} for backup." }
                Files.newOutputStream(destination).use { output -> input.copyTo(output) }
            }
        }
        val summary = HadrianBackupSummary(id, Instant.now().toString(), preflight.actions.size)
        Properties().apply {
            setProperty("id", summary.id)
            setProperty("createdAt", summary.createdAt)
            setProperty("actionCount", summary.actionCount.toString())
            setProperty("workspaceUri", preflight.workspace.uri.toString())
            setProperty("layoutFingerprint", preflight.workspace.layoutFingerprint)
            setProperty("contentFingerprintBeforeApply", preflight.workspace.contentFingerprint)
            setProperty("targets", preflight.actions.joinToString("\n") { it.targetPath })
        }.also { properties ->
            Files.newOutputStream(root.resolve("record.properties")).use { properties.store(it, "DemiForge Hadrian port backup") }
        }
        return summary
    }

    private fun restore(summary: HadrianBackupSummary, workspace: HadrianDocumentWorkspace) {
        val root = backupDirectory(summary.id)
        val propertiesFile = root.resolve("record.properties")
        val properties = Properties().apply { Files.newInputStream(propertiesFile).use(::load) }
        require(properties.getProperty("workspaceUri") == workspace.uri.toString()) { "That backup belongs to a different selected workspace." }
        require(properties.getProperty("layoutFingerprint") == workspace.layoutFingerprint) { "The workspace payload layout changed; refusing to restore into different files." }
        properties.getProperty("targets", "").lineSequence().filter { it.isNotBlank() }.forEach { targetPath ->
            val original = FileTree.safeChild(root.resolve("original"), targetPath)
            require(Files.isRegularFile(original)) { "Backup is missing original $targetPath." }
            val target = findDocument(workspace.root, targetPath)
                ?: throw IllegalStateException("Workspace no longer contains $targetPath.")
            Files.newInputStream(original).use { input -> writeDocument(target, input) }
        }
    }

    private fun findDocument(root: DocumentFile, relativePath: String): DocumentFile? {
        require(relativePath.isNotBlank() && !relativePath.startsWith('/') && !relativePath.contains("..")) { "Unsafe target path." }
        return relativePath.split('/').fold(root) { current, segment -> current.findFile(segment) ?: return null }
    }

    private fun writeDocument(target: DocumentFile, input: java.io.InputStream) {
        resolver.openOutputStream(target.uri, "wt").use { output ->
            requireNotNull(output) { "Cannot write ${target.name ?: "selected payload"}." }
            input.copyTo(output)
        }
    }

    private fun backupDirectory(id: String): Path = FileTree.safeChild(backupsRoot, id)

    private fun readBackupSummary(id: String, metadata: Path): HadrianBackupSummary {
        require(Files.isRegularFile(metadata)) { "Backup metadata is unavailable." }
        val properties = Properties().apply { Files.newInputStream(metadata).use(::load) }
        return HadrianBackupSummary(
            id = properties.getProperty("id", id),
            createdAt = properties.getProperty("createdAt", "unknown"),
            actionCount = properties.getProperty("actionCount", "0").toIntOrNull() ?: 0,
        )
    }

    companion object {
        private const val MAX_DEPTH = 6
        private const val KEY_URI = "workspace_uri"
        private const val KEY_TRUST_URI = "trusted_workspace_uri"
        private const val KEY_TRUST_LAYOUT = "trusted_workspace_layout"
        private const val KEY_LAST_BACKUP = "last_backup"
    }
}
