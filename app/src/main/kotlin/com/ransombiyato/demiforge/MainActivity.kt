package com.ransombiyato.demiforge

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.documentfile.provider.DocumentFile
import com.ransombiyato.demiforge.core.ModManager
import com.ransombiyato.demiforge.core.adapters.DeltaruneAdapter
import com.ransombiyato.demiforge.core.adapters.GameInspection
import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.InstalledMod
import com.ransombiyato.demiforge.core.recovery.BackupManager
import com.ransombiyato.demiforge.core.storage.FileTree
import com.ransombiyato.demiforge.core.storage.ModPackageArchive
import com.ransombiyato.demiforge.core.storage.ModStorage
import java.nio.file.Files
import java.nio.file.Path

class MainActivity : Activity() {
    private val backgroundColor = Color.rgb(16, 20, 28)
    private val surface = Color.rgb(25, 33, 45)
    private val primary = Color.rgb(93, 219, 148)
    private val warning = Color.rgb(255, 202, 92)
    private val danger = Color.rgb(246, 120, 120)
    private val textColor = Color.rgb(245, 247, 251)
    private val muted = Color.rgb(174, 184, 200)

    private lateinit var content: LinearLayout
    private lateinit var log: EventLog
    private lateinit var storage: ModStorage
    private lateinit var manager: ModManager
    private lateinit var hadrian: HadrianWorkspaceService
    private val deltaruneAdapter = DeltaruneAdapter()

    private val importModDirectoryRequest = 4101
    private val selectHadrianWorkspaceRequest = 4102
    private val importModArchiveRequest = 4103

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        setupCore()
        setContentView(buildRoot())
        showDashboard()
    }

    @Deprecated("Storage Access Framework callback retained to avoid an additional activity-result dependency")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return
        val uri = data?.data ?: return
        try {
            when (requestCode) {
                importModDirectoryRequest -> {
                    val flags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    contentResolver.takePersistableUriPermission(uri, flags and Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    importModTree(uri)
                    log.info("import", "Imported a mod from a user-selected directory")
                    showMods()
                }
                importModArchiveRequest -> {
                    importModArchive(uri)
                    log.info("import", "Imported a mod from a user-selected ZIP archive")
                    showMods()
                }
                selectHadrianWorkspaceRequest -> {
                    val workspace = hadrian.select(uri, data.flags)
                    log.info("workspace", "Selected Hadrian-port workspace with ${workspace.payloads.size} payload candidate(s); review required.")
                    showWorkspace()
                }
            }
        } catch (exception: Exception) {
            log.error("workspace", "Could not use selected directory: ${exception.message}")
            showLogs()
        }
    }

    private fun setupCore() {
        val stateRoot = filesDir.toPath().resolve("demiforge")
        log = EventLog()
        storage = ModStorage(stateRoot, log)
        manager = ModManager(storage, BackupManager(stateRoot.resolve("backups")), log)
        hadrian = HadrianWorkspaceService(this)
        if (storage.discover().any { it.manifest.id == "sample-overlay" }) {
            storage.remove("sample-overlay")
            log.info("migration", "Removed the old dummy-environment sample mod. Import a real Hadrian-compatible mod package instead.")
        }
    }

    private fun buildRoot(): View {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
            setPadding(dp(16), dp(12), dp(16), dp(12))
        }
        root.addView(TextView(this).apply {
            text = "DEMI FORGE"
            setTextColor(primary)
            textSize = 21f
            setTypeface(null, 1)
            letterSpacing = 0.08f
        })
        root.addView(TextView(this).apply {
            text = "Hadrian Android port mod manager"
            setTextColor(textColor)
            textSize = 13f
            setPadding(0, dp(2), 0, dp(12))
        })
        root.addView(LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            addView(tabButton("Home") { showDashboard() }, LinearLayout.LayoutParams(0, dp(42), 1f))
            addView(tabButton("Mods") { showMods() }, LinearLayout.LayoutParams(0, dp(42), 1f))
            addView(tabButton("Workspace") { showWorkspace() }, LinearLayout.LayoutParams(0, dp(42), 1.25f))
            addView(tabButton("Logs") { showLogs() }, LinearLayout.LayoutParams(0, dp(42), 1f))
        })
        root.addView(ScrollView(this).apply {
            setPadding(0, dp(12), 0, 0)
            content = LinearLayout(this@MainActivity).apply { orientation = LinearLayout.VERTICAL }
            addView(content)
        }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f))
        return root
    }

    private fun showDashboard() {
        content.removeAllViews()
        val inspection = hadrian.gameInspection()
        val workspace = hadrian.selectedWorkspace()
        content.addView(title("Hadrian port status"))
        content.addView(card().apply {
            addView(line(inspection.displayName, inspection.status.name.replace('_', ' ')))
            addView(body(inspection.message))
            addView(line("Workspace", workspace?.let { "${it.payloads.size} payload(s)" } ?: "Not selected"))
            addView(line("Installed mods", storage.discover().size.toString()))
            if (workspace != null) addView(line("Layout ID", workspace.layoutFingerprint))
        })

        content.addView(title("Real modding workflow"))
        if (workspace == null) {
            content.addView(primaryButton("Select Hadrian port workspace") { chooseHadrianWorkspace() })
            content.addView(body("Pick the externally accessible folder used by your installed Hadrian port. DemiForge will only inspect that folder; it never scans apps or modifies the APK."))
        } else {
            content.addView(primaryButton("Review workspace and payloads") { showWorkspace() })
            content.addView(secondaryButton("Preview enabled mod changes") { showPreflight() })
            hadrian.latestBackup()?.let { backup ->
                content.addView(secondaryButton("Restore latest backup (${backup.actionCount} file(s))") { restoreLatest() })
            }
            content.addView(body("DemiForge can replace only the detected game.droid, data.droid, or WAD payload paths you review. Each apply operation creates an app-private backup first."))
        }

        content.addView(title("Safety"))
        content.addView(card().apply {
            addView(line("Safe mode", if (manager.safeMode) "On" else "Off"))
            addView(secondaryButton(if (manager.safeMode) "Disable safe mode" else "Enable safe mode") {
                manager.setSafeMode(!manager.safeMode)
                showDashboard()
            })
        })
    }

    private fun showWorkspace() {
        content.removeAllViews()
        val workspace = hadrian.selectedWorkspace()
        content.addView(title("Port workspace"))
        if (workspace == null) {
            content.addView(body("No workspace is selected. Use the Android system picker to select the folder that contains the port's externally accessible payload files."))
            content.addView(primaryButton("Select workspace") { chooseHadrianWorkspace() })
            return
        }
        val trusted = hadrian.isTrusted(workspace)
        content.addView(card().apply {
            addView(line("Payload candidates", workspace.payloads.size.toString()))
            addView(line("Workspace review", if (trusted) "Trusted" else "Required"))
            addView(line("Layout ID", workspace.layoutFingerprint))
            addView(line("Content inventory", workspace.contentFingerprint))
            addView(body("Trusting confirms that this is the external workspace used by your Hadrian installation. New, removed, or renamed payload files invalidate trust and require another review."))
        })
        workspace.payloads.forEach { payload ->
            content.addView(card().apply {
                addView(line(payload.relativePath, payload.corePayload.kind.name.replace('_', ' ')))
                addView(body("${formatBytes(payload.sizeBytes)} · replacement target only"))
            })
        }
        if (!trusted) {
            content.addView(primaryButton("I reviewed these paths — trust workspace") {
                hadrian.trust(workspace)
                log.info("workspace", "Trusted Hadrian-port workspace ${workspace.layoutFingerprint}")
                showWorkspace()
            })
        } else {
            content.addView(primaryButton("Preview enabled mods") { showPreflight() })
        }
        content.addView(dangerButton("Forget workspace access") {
            hadrian.forgetWorkspace()
            log.warn("workspace", "Forgot the selected Hadrian-port directory and released its persisted access grant.")
            showDashboard()
        })
    }

    private fun showPreflight() {
        content.removeAllViews()
        content.addView(title("Patch preflight"))
        val preflight = try {
            hadrian.preflight(storage, manager, deltaruneAdapter)
        } catch (exception: Exception) {
            content.addView(body("Preflight could not start: ${exception.message}"))
            content.addView(secondaryButton("Back to workspace") { showWorkspace() })
            return
        }
        content.addView(card().apply {
            addView(line("Replacement files", preflight.actions.size.toString()))
            addView(line("Originals to back up", formatBytes(preflight.backupBytes)))
            addView(line("New payload data", formatBytes(preflight.installBytes)))
            addView(line("Workspace layout", preflight.workspace.layoutFingerprint))
        })
        if (preflight.issues.isNotEmpty()) {
            content.addView(title("Blocked"))
            preflight.issues.forEach { issue -> content.addView(warningCard(issue)) }
        } else {
            content.addView(title("Planned replacements"))
            preflight.actions.forEach { action ->
                content.addView(card().apply {
                    addView(line(action.targetPath, action.modName))
                    addView(body("${action.modId} · ${formatBytes(action.backupBytes)} backup → ${formatBytes(action.sourceBytes)} replacement"))
                })
            }
            content.addView(primaryButton("Apply ${preflight.actions.size} replacement(s) and create backup") { applyPreflight(preflight) })
        }
        content.addView(secondaryButton("Back to workspace") { showWorkspace() })
    }

    private fun applyPreflight(preflight: HadrianPreflight) {
        try {
            val backup = hadrian.apply(preflight)
            log.info("hadrian-apply", "Applied ${backup.actionCount} real port replacement(s) with backup ${backup.id}.")
            showDashboard()
        } catch (exception: Exception) {
            log.error("hadrian-apply", "No completed mod operation: ${exception.message}")
            showLogs()
        }
    }

    private fun restoreLatest() {
        try {
            val backup = hadrian.restoreLatest()
            log.info("hadrian-restore", "Restored ${backup.actionCount} original file(s) from ${backup.id}.")
            showDashboard()
        } catch (exception: Exception) {
            log.error("hadrian-restore", "Restore was blocked or failed: ${exception.message}")
            showLogs()
        }
    }

    private fun showMods() {
        content.removeAllViews()
        content.addView(title("Installed mod packages"))
        content.addView(primaryButton("Import mod directory") { chooseModDirectory() })
        content.addView(secondaryButton("Import .zip mod package") { chooseModArchive() })
        content.addView(body("A Hadrian-compatible package must set targetGame to deltarune-hadrian-android and use COPY patches targeted at a payload path listed in Workspace. DemiForge never supplies game files."))
        val mods = storage.discover()
        if (mods.isEmpty()) content.addView(body("No valid mod packages are installed."))
        mods.forEach { mod -> content.addView(modCard(mod)) }
    }

    private fun showDetails(mod: InstalledMod) {
        content.removeAllViews()
        content.addView(title(mod.manifest.name))
        content.addView(card().apply {
            addView(line("Author", mod.manifest.author))
            addView(line("Version", mod.manifest.version))
            addView(line("Target", mod.manifest.targetGame))
            addView(line("State", if (mod.enabled) "Enabled" else "Disabled"))
            addView(body(mod.manifest.description))
            addView(line("Dependencies", mod.manifest.dependencies.joinToString { it.id }.ifBlank { "None" }))
            addView(line("Conflicts", mod.manifest.conflicts.joinToString { it.id }.ifBlank { "None" }))
            addView(line("Patches", mod.manifest.patches.size.toString()))
        })
        content.addView(primaryButton(if (mod.enabled) "Disable mod" else "Enable mod") {
            storage.setEnabled(mod.manifest.id, !mod.enabled)
            showDetails(storage.discover().first { it.manifest.id == mod.manifest.id })
        })
        content.addView(secondaryButton("Remove mod package") {
            storage.remove(mod.manifest.id)
            log.info("mods", "Removed ${mod.manifest.id}")
            showMods()
        })
        content.addView(secondaryButton("Back to mods") { showMods() })
    }

    private fun showLogs() {
        content.removeAllViews()
        content.addView(title("Diagnostic log"))
        val entries = log.snapshot()
        if (entries.isEmpty()) content.addView(body("No events have been recorded yet."))
        entries.asReversed().forEach { entry ->
            content.addView(card().apply {
                addView(line("${entry.severity} · ${entry.category}", entry.timestamp.toString()))
                addView(body(entry.message))
            })
        }
        content.addView(secondaryButton("Clear logs") { log.clear(); showLogs() })
    }

    private fun modCard(mod: InstalledMod): View = card().apply {
        addView(line(mod.manifest.name, "${mod.manifest.version} · ${if (mod.enabled) "Enabled" else "Disabled"}"))
        addView(body(mod.manifest.description))
        addView(secondaryButton("View details") { showDetails(mod) })
    }

    private fun chooseHadrianWorkspace() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        }
        startActivityForResult(intent, selectHadrianWorkspaceRequest)
    }

    private fun chooseModDirectory() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, importModDirectoryRequest)
    }

    private fun chooseModArchive() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/zip"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivityForResult(intent, importModArchiveRequest)
    }

    private fun importModTree(uri: Uri) {
        val root = requireNotNull(DocumentFile.fromTreeUri(this, uri)) { "Selected directory is unavailable" }
        val staging = cacheDir.toPath().resolve("import-${System.currentTimeMillis()}")
        copyDocumentTree(root, staging)
        val result = storage.install(staging)
        FileTree.delete(staging)
        if (!result.valid) throw IllegalArgumentException(result.issues.joinToString { it.message })
    }

    private fun importModArchive(uri: Uri) {
        val staging = cacheDir.toPath().resolve("archive-import-${System.currentTimeMillis()}")
        try {
            val packageRoot = contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input) { "Cannot read the selected archive." }
                ModPackageArchive.extract(input, staging)
            }
            val result = storage.install(packageRoot)
            if (!result.valid) throw IllegalArgumentException(result.issues.joinToString { it.message })
        } finally {
            FileTree.delete(staging)
        }
    }

    private fun copyDocumentTree(source: DocumentFile, destination: Path) {
        if (source.isDirectory) {
            Files.createDirectories(destination)
            source.listFiles().forEach { child -> copyDocumentTree(child, destination.resolve(child.name ?: "unnamed")) }
        } else {
            Files.createDirectories(destination.parent)
            contentResolver.openInputStream(source.uri).use { input ->
                requireNotNull(input) { "Cannot read ${source.name}" }
                Files.newOutputStream(destination).use { output -> input.copyTo(output) }
            }
        }
    }

    private fun title(value: String) = TextView(this).apply { text = value; textSize = 19f; setTextColor(textColor); setTypeface(null, 1); setPadding(0, dp(8), 0, dp(8)) }
    private fun body(value: String) = TextView(this).apply { text = value; textSize = 14f; setTextColor(muted); setLineSpacing(dp(3).toFloat(), 1f); setPadding(0, dp(4), 0, dp(4)) }
    private fun line(left: String, right: String) = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL; setPadding(0, dp(4), 0, dp(4))
        addView(TextView(this@MainActivity).apply { text = left; textSize = 14f; setTextColor(textColor) }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        addView(TextView(this@MainActivity).apply { text = right; textSize = 12f; setTextColor(muted); gravity = Gravity.END }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
    }
    private fun card() = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(surface); setPadding(dp(14), dp(10), dp(14), dp(10)); val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT); params.bottomMargin = dp(10); layoutParams = params }
    private fun warningCard(value: String) = card().apply { setBackgroundColor(Color.rgb(73, 57, 32)); addView(TextView(this@MainActivity).apply { text = value; textSize = 14f; setTextColor(warning); setPadding(0, dp(3), 0, dp(3)) }) }
    private fun tabButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; textSize = 10f; setTextColor(textColor); setOnClickListener { listener() } }
    private fun primaryButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(Color.BLACK); setBackgroundColor(primary); setOnClickListener { listener() }; setPadding(0, dp(3), 0, dp(3)) }
    private fun secondaryButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(textColor); setOnClickListener { listener() } }
    private fun dangerButton(label: String, listener: () -> Unit) = Button(this).apply { text = label; setTextColor(danger); setOnClickListener { listener() } }
    private fun formatBytes(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)} KiB"
        else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MiB"
    }
    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
