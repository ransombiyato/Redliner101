package com.ransombiyato.demiforge.core.storage

import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.InstalledMod
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.ValidationResult
import com.ransombiyato.demiforge.core.mods.ModManifestCodec
import com.ransombiyato.demiforge.core.mods.ModValidator
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class ModStorage(private val root: Path, private val log: EventLog) {
    private val validator = ModValidator()
    private val modsRoot = root.resolve("mods")
    private val stateFile = root.resolve("enabled-mods.txt")

    init { Files.createDirectories(modsRoot) }

    fun install(sourceDirectory: Path): ValidationResult {
        val manifestPath = sourceDirectory.resolve("manifest.json")
        val result = if (Files.isRegularFile(manifestPath)) validator.validateText(Files.readString(manifestPath))
        else ValidationResult(null, listOf(com.ransombiyato.demiforge.core.model.ModIssue(com.ransombiyato.demiforge.core.model.IssueSeverity.ERROR, "missing_manifest", "manifest.json is required")))
        if (!result.valid) {
            log.error("install", "Rejected invalid mod from $sourceDirectory")
            return result
        }
        val manifest = requireNotNull(result.manifest)
        val staging = modsRoot.resolve(".${manifest.id}.staging")
        val target = modsRoot.resolve(manifest.id)
        FileTree.delete(staging)
        FileTree.copy(sourceDirectory, staging)
        FileTree.delete(target)
        Files.move(staging, target, StandardCopyOption.ATOMIC_MOVE)
        log.info("install", "Installed ${manifest.id} ${manifest.version}")
        return result
    }

    fun discover(): List<InstalledMod> {
        if (!Files.exists(modsRoot)) return emptyList()
        val enabled = enabledIds()
        return Files.list(modsRoot).use { paths ->
            paths.iterator().asSequence().filter { Files.isDirectory(it) }.sorted().mapNotNull { directory: Path ->
                val manifestFile = directory.resolve("manifest.json")
                if (!Files.isRegularFile(manifestFile)) {
                    log.warn("discovery", "Skipped ${directory.fileName}: manifest.json is missing")
                    null
                } else {
                    val result = validator.validateText(Files.readString(manifestFile))
                    if (result.valid) InstalledMod(requireNotNull(result.manifest), directory.toString(), result.manifest.id in enabled)
                    else {
                        log.warn("discovery", "Skipped ${directory.fileName}: invalid manifest")
                        null
                    }
                }
            }.toList()
        }
    }

    fun setEnabled(modId: String, enabled: Boolean) {
        require(discover().any { it.manifest.id == modId }) { "Mod not installed: $modId" }
        val ids = enabledIds().toMutableSet()
        if (enabled) ids += modId else ids -= modId
        Files.writeString(stateFile, ids.sorted().joinToString("\n", postfix = if (ids.isEmpty()) "" else "\n"))
        log.info("state", "${if (enabled) "Enabled" else "Disabled"} $modId")
    }

    fun remove(modId: String) {
        FileTree.delete(modsRoot.resolve(modId))
        val ids = enabledIds().toMutableSet().apply { remove(modId) }
        Files.writeString(stateFile, ids.sorted().joinToString("\n", postfix = if (ids.isEmpty()) "" else "\n"))
        log.info("remove", "Removed $modId")
    }

    fun sourceFor(manifest: ModManifest): Path = modsRoot.resolve(manifest.id)

    private fun enabledIds(): Set<String> = if (Files.isRegularFile(stateFile)) {
        Files.readAllLines(stateFile).map { it.trim() }.filter { it.isNotBlank() }.toSet()
    } else emptySet()
}
