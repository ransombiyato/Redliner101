package com.ransombiyato.demiforge.core.storage

import java.io.BufferedInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

/**
 * Extracts a user-selected mod archive without executing any archive content. The size and entry
 * limits keep an accidental or hostile ZIP from exhausting the device before manifest validation.
 */
object ModPackageArchive {
    const val MAX_ENTRIES = 512
    const val MAX_UNCOMPRESSED_BYTES = 512L * 1024L * 1024L
    private const val MANIFEST_NAME = "manifest.json"

    fun extract(input: InputStream, destination: Path): Path {
        FileTree.delete(destination)
        Files.createDirectories(destination)
        var entries = 0
        var writtenBytes = 0L
        try {
            ZipInputStream(BufferedInputStream(input)).use { archive ->
                while (true) {
                    val entry = archive.nextEntry ?: break
                    entries += 1
                    require(entries <= MAX_ENTRIES) { "Archive contains more than $MAX_ENTRIES entries." }
                    val safeName = entry.name.replace('\\', '/')
                    require(safeName.isNotBlank()) { "Archive contains an unnamed entry." }
                    val output = FileTree.safeChild(destination, safeName)
                    if (entry.isDirectory) {
                        Files.createDirectories(output)
                    } else {
                        Files.createDirectories(output.parent)
                        Files.newOutputStream(output).use { stream ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            while (true) {
                                val read = archive.read(buffer)
                                if (read < 0) break
                                writtenBytes += read
                                require(writtenBytes <= MAX_UNCOMPRESSED_BYTES) {
                                    "Archive expands beyond ${MAX_UNCOMPRESSED_BYTES / (1024 * 1024)} MiB."
                                }
                                stream.write(buffer, 0, read)
                            }
                        }
                    }
                    archive.closeEntry()
                }
            }
            return packageRoot(destination)
        } catch (exception: Exception) {
            FileTree.delete(destination)
            throw exception
        }
    }

    private fun packageRoot(destination: Path): Path {
        if (Files.isRegularFile(destination.resolve(MANIFEST_NAME))) return destination
        val manifests = Files.walk(destination, 3).use { paths ->
            paths.filter { path -> Files.isRegularFile(path) && path.fileName.toString() == MANIFEST_NAME }.toList()
        }
        require(manifests.size == 1) { "Archive must contain exactly one manifest.json at its root or one enclosing folder." }
        return requireNotNull(manifests.single().parent)
    }
}
