package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.storage.ApkArchive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ApkArchiveTest {
    @TempDir lateinit var temp: Path

    @Test fun `lists Android payloads and rebuilds only a selected asset while dropping stale signatures`() {
        val original = temp.resolve("original.apk")
        writeArchive(original, mapOf(
            "AndroidManifest.xml" to "manifest",
            "assets/chapter5_windows/game.droid" to "original payload",
            "assets/readme.txt" to "leave untouched",
            "META-INF/CERT.RSA" to "old signature",
            "META-INF/CERT.SF" to "old signature metadata",
        ))
        val replacement = temp.resolve("replacement.droid")
        Files.writeString(replacement, "modded payload")
        val rebuilt = temp.resolve("rebuilt-unsigned.apk")

        assertEquals(listOf("assets/chapter5_windows/game.droid"), ApkArchive.listPayloads(original).map { it.path })
        ApkArchive.rebuildWithReplacements(original, rebuilt, mapOf("assets/chapter5_windows/game.droid" to replacement))

        ZipFile(rebuilt.toFile()).use { archive ->
            assertEquals("modded payload", archive.getInputStream(archive.getEntry("assets/chapter5_windows/game.droid")).reader().readText())
            assertEquals("leave untouched", archive.getInputStream(archive.getEntry("assets/readme.txt")).reader().readText())
            assertTrue(archive.getEntry("META-INF/CERT.RSA") == null)
            assertTrue(archive.getEntry("META-INF/CERT.SF") == null)
        }
    }

    @Test fun `rejects target paths that are not recognised assets payloads`() {
        val original = temp.resolve("original.apk")
        writeArchive(original, mapOf("AndroidManifest.xml" to "manifest", "assets/game.droid" to "payload"))
        val replacement = temp.resolve("replacement.droid")
        Files.writeString(replacement, "modded")

        assertFailsWith<IllegalArgumentException> {
            ApkArchive.rebuildWithReplacements(original, temp.resolve("output.apk"), mapOf("classes.dex" to replacement))
        }
    }

    private fun writeArchive(path: Path, entries: Map<String, String>) {
        ZipOutputStream(Files.newOutputStream(path)).use { archive ->
            entries.forEach { (name, content) ->
                archive.putNextEntry(ZipEntry(name))
                archive.write(content.toByteArray())
                archive.closeEntry()
            }
        }
    }
}
