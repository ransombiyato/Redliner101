package com.ransombiyato.demiforge.core.recovery

import com.ransombiyato.demiforge.core.logging.EventLog
import com.ransombiyato.demiforge.core.model.PatchMode
import com.ransombiyato.demiforge.core.model.PatchOperation
import com.ransombiyato.demiforge.core.storage.FileTree
import java.nio.file.Files
import java.nio.file.Path

class PatchTransaction(private val backupManager: BackupManager, private val log: EventLog) {
    fun apply(sourceRoot: Path, targetRoot: Path, patches: List<PatchOperation>): BackupRecord {
        val copyPatches = patches.filter { it.mode == PatchMode.COPY }
        val record = backupManager.backup(targetRoot, copyPatches.map { it.target })
        try {
            patches.forEach { patch ->
                val source = FileTree.safeChild(sourceRoot, patch.source)
                require(Files.isRegularFile(source)) { "Patch source does not exist: ${patch.source}" }
                val destinationRoot = if (patch.mode == PatchMode.OVERLAY) targetRoot.resolve(".demiforge-overlay") else targetRoot
                val target = FileTree.safeChild(destinationRoot, patch.target)
                Files.createDirectories(target.parent)
                Files.copy(source, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
            }
            log.info("patch", "Applied ${patches.size} patch operations using backup ${record.id}")
            return record
        } catch (exception: Exception) {
            backupManager.restore(targetRoot, record)
            log.error("patch", "Patch transaction failed and was rolled back: ${exception.message}")
            throw exception
        }
    }
}
