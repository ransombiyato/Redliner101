package com.ransombiyato.demiforge.core.storage

import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

data class ApkPayloadEntry(val path: String, val sizeBytes: Long)

/**
 * ZIP-level APK manipulation only. It does not sign, install, execute, decrypt, or otherwise
 * inspect game data. Callers must sign the rebuilt archive before Android can install it.
 */
object ApkArchive {
    private const val ASSETS_PREFIX = "assets/"

    fun listPayloads(apk: Path): List<ApkPayloadEntry> {
        require(Files.isRegularFile(apk)) { "APK does not exist: $apk" }
        ZipFile(apk.toFile()).use { archive ->
            require(archive.getEntry("AndroidManifest.xml") != null) { "Selected file is not an Android APK." }
            return archive.entries().asSequence()
                .filter { !it.isDirectory && isPayloadPath(it.name) }
                .map { ApkPayloadEntry(it.name, it.size.coerceAtLeast(0)) }
                .sortedBy { it.path.lowercase() }
                .toList()
        }
    }

    fun rebuildWithReplacements(inputApk: Path, outputApk: Path, replacements: Map<String, Path>) {
        val payloads = listPayloads(inputApk).map { it.path }.toSet()
        require(replacements.isNotEmpty()) { "At least one payload replacement is required." }
        replacements.forEach { (target, source) ->
            require(target in payloads) { "Target is not a recognised APK assets payload: $target" }
            require(Files.isRegularFile(source)) { "Replacement file is missing: $source" }
        }
        Files.createDirectories(outputApk.parent)
        val temporary = outputApk.resolveSibling(".${outputApk.fileName}.building")
        Files.deleteIfExists(temporary)
        try {
            ZipFile(inputApk.toFile()).use { archive ->
                ZipOutputStream(Files.newOutputStream(temporary)).use { output ->
                    archive.entries().asSequence().forEach { entry ->
                        if (isStaleSignatureEntry(entry.name)) return@forEach
                        val rebuilt = ZipEntry(entry.name).apply {
                            time = entry.time
                            comment = entry.comment
                        }
                        output.putNextEntry(rebuilt)
                        val replacement = replacements[entry.name]
                        if (entry.isDirectory) {
                            // Directories have no body.
                        } else if (replacement != null) {
                            Files.newInputStream(replacement).use { input -> input.copyTo(output) }
                        } else {
                            archive.getInputStream(entry).use { input -> BufferedInputStream(input).copyTo(output) }
                        }
                        output.closeEntry()
                    }
                }
            }
            Files.move(temporary, outputApk, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (exception: Exception) {
            Files.deleteIfExists(temporary)
            throw exception
        }
    }

    fun isPayloadPath(path: String): Boolean {
        if (!path.startsWith(ASSETS_PREFIX) || path.endsWith('/')) return false
        val name = path.substringAfterLast('/')
        return name.equals("game.droid", ignoreCase = true) ||
            name.equals("data.droid", ignoreCase = true) ||
            name.endsWith(".wad", ignoreCase = true)
    }

    private fun isStaleSignatureEntry(path: String): Boolean {
        if (!path.startsWith("META-INF/") || path.substringAfter("META-INF/").contains('/')) return false
        val file = path.substringAfter("META-INF/").uppercase()
        return file == "MANIFEST.MF" || file.endsWith(".SF") || file.endsWith(".RSA") || file.endsWith(".DSA") || file.endsWith(".EC")
    }
}
