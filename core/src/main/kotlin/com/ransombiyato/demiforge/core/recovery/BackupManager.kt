package com.ransombiyato.demiforge.core.recovery

import com.ransombiyato.demiforge.core.storage.FileTree
import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant

data class BackupRecord(val id: String, val directory: Path, val originalPaths: List<String>)

class BackupManager(private val backupRoot: Path) {
    init { Files.createDirectories(backupRoot) }

    fun backup(root: Path, relativeTargets: Collection<String>): BackupRecord {
        val id = "backup-${Instant.now().toEpochMilli()}"
        val directory = backupRoot.resolve(id)
        val originals = directory.resolve("originals")
        Files.createDirectories(originals)
        val paths = relativeTargets.distinct().sorted()
        paths.forEach { relative ->
            val source = FileTree.safeChild(root, relative)
            if (Files.exists(source)) FileTree.copy(source, originals.resolve(relative))
        }
        Files.writeString(directory.resolve("paths.txt"), paths.joinToString("\n"))
        return BackupRecord(id, directory, paths)
    }

    fun restore(root: Path, record: BackupRecord) {
        val originals = record.directory.resolve("originals")
        record.originalPaths.forEach { relative ->
            val target = FileTree.safeChild(root, relative)
            FileTree.delete(target)
            val source = originals.resolve(relative)
            if (Files.exists(source)) FileTree.copy(source, target)
        }
    }
}
