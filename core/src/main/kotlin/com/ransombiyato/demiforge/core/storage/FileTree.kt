package com.ransombiyato.demiforge.core.storage

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.attribute.BasicFileAttributes

object FileTree {
    fun copy(source: Path, destination: Path) {
        require(Files.exists(source)) { "Source does not exist: $source" }
        Files.walk(source).use { paths ->
            paths.forEach { current ->
                val target = destination.resolve(source.relativize(current).toString())
                if (Files.isDirectory(current)) Files.createDirectories(target)
                else {
                    Files.createDirectories(target.parent)
                    Files.copy(current, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES)
                }
            }
        }
    }

    fun delete(path: Path) {
        if (!Files.exists(path)) return
        Files.walkFileTree(path, object : java.nio.file.SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path, attributes: BasicFileAttributes): java.nio.file.FileVisitResult {
                Files.deleteIfExists(file)
                return java.nio.file.FileVisitResult.CONTINUE
            }
            override fun postVisitDirectory(directory: Path, exception: java.io.IOException?): java.nio.file.FileVisitResult {
                if (exception != null) throw exception
                Files.deleteIfExists(directory)
                return java.nio.file.FileVisitResult.CONTINUE
            }
        })
    }

    fun safeChild(root: Path, relative: String): Path {
        require(relative.isNotBlank() && !relative.startsWith("/") && !relative.contains("..")) { "Unsafe relative path: $relative" }
        val candidate = root.resolve(relative).normalize()
        require(candidate.startsWith(root.normalize())) { "Resolved outside root: $relative" }
        return candidate
    }
}
