package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.adapters.GameAdapter
import com.ransombiyato.demiforge.core.adapters.GameInspection
import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.ModIssue
import com.ransombiyato.demiforge.core.model.ModManifest
import com.ransombiyato.demiforge.core.model.ResolutionResult
import com.ransombiyato.demiforge.core.recovery.BackupManager
import com.ransombiyato.demiforge.core.recovery.PatchTransaction
import com.ransombiyato.demiforge.core.resolution.DependencyResolver
import com.ransombiyato.demiforge.core.storage.ModStorage
import java.nio.file.Path

data class ApplyResult(val applied: Boolean, val backups: List<String>, val issues: List<ModIssue> = emptyList(), val message: String)

class ModManager(
    private val storage: ModStorage,
    private val backupManager: BackupManager,
    val log: EventLog,
) {
    private val resolver = DependencyResolver()
    var safeMode: Boolean = false
        private set

    fun setSafeMode(enabled: Boolean) {
        safeMode = enabled
        log.warn("safe-mode", if (enabled) "Safe mode enabled: patch operations are disabled." else "Safe mode disabled.")
    }

    fun resolve(adapter: GameAdapter, inspection: GameInspection): ResolutionResult {
        val enabled = storage.discover().filter { it.enabled }.map { it.manifest }
        val result = resolver.resolve(enabled, adapter.gameId, inspection.version ?: "0.0.0")
        if (result.valid) log.info("resolution", "Resolved ${result.loadOrder.size} enabled mods.")
        else result.issues.forEach { log.error("resolution", it.message) }
        return result
    }

    fun apply(adapter: GameAdapter, inspection: GameInspection): ApplyResult {
        if (safeMode) return ApplyResult(false, emptyList(), message = "Safe mode is enabled; no changes were made.")
        if (!adapter.supportsPatching(inspection)) return ApplyResult(false, emptyList(), message = inspection.message)
        val resolution = resolve(adapter, inspection)
        if (!resolution.valid) return ApplyResult(false, emptyList(), resolution.issues, "Mod resolution failed; no changes were made.")
        val target = requireNotNull(adapter.patchRoot(inspection))
        val transaction = PatchTransaction(backupManager, log)
        val backups = mutableListOf<String>()
        try {
            resolution.loadOrder.forEach { manifest: ModManifest ->
                if (manifest.patches.isNotEmpty()) {
                    val record = transaction.apply(storage.sourceFor(manifest), target, manifest.patches)
                    backups += record.id
                }
            }
        } catch (exception: Exception) {
            return ApplyResult(false, backups, message = "Patch operation failed and was rolled back: ${exception.message}")
        }
        return ApplyResult(true, backups, message = "Applied ${resolution.loadOrder.sumOf { it.patches.size }} patch operations safely.")
    }
}
