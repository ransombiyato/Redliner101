package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.storage.ModPackageArchive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ModPackageArchiveTest {
    @TempDir lateinit var temp: Path

    @Test fun `extracts a single enclosing mod folder without executing archive contents`() {
        val root = ModPackageArchive.extract(
            ByteArrayInputStream(zipOf(
                "my-mod/manifest.json" to "{\"id\":\"my-mod\"}",
                "my-mod/payload/game.droid" to "replacement payload",
            )),
            temp.resolve("extract"),
        )

        assertEquals("my-mod", root.fileName.toString())
        assertTrue(Files.isRegularFile(root.resolve("manifest.json")))
        assertEquals("replacement payload", Files.readString(root.resolve("payload/game.droid")))
    }

    @Test fun `rejects archive traversal and clears the partial extraction`() {
        val destination = temp.resolve("unsafe")

        assertFailsWith<IllegalArgumentException> {
            ModPackageArchive.extract(ByteArrayInputStream(zipOf("../escape.txt" to "nope")), destination)
        }

        assertTrue(!Files.exists(destination))
        assertTrue(!Files.exists(temp.resolve("escape.txt")))
    }

    private fun zipOf(vararg files: Pair<String, String>): ByteArray {
        val bytes = ByteArrayOutputStream()
        ZipOutputStream(bytes).use { archive ->
            files.forEach { (name, content) ->
                archive.putNextEntry(ZipEntry(name))
                archive.write(content.toByteArray())
                archive.closeEntry()
            }
        }
        return bytes.toByteArray()
    }
}
