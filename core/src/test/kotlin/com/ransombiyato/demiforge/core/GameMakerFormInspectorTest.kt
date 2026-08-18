package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.gamemaker.GameMakerFormInspector
import com.ransombiyato.demiforge.core.gamemaker.GameMakerResourceCategory
import com.ransombiyato.demiforge.core.gamemaker.GameMakerStringEdit
import com.ransombiyato.demiforge.core.gamemaker.GameMakerStringEditor
import com.ransombiyato.demiforge.core.gamemaker.GameMakerObjectSpriteAlias
import com.ransombiyato.demiforge.core.gamemaker.GameMakerObjectSpriteEditor
import com.ransombiyato.demiforge.core.storage.ApkArchive
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GameMakerFormInspectorTest {
    @TempDir lateinit var temp: Path

    @Test fun `indexes valid GameMaker chunks and assigns editor categories`() {
        val file = temp.resolve("chapter5.droid")
        Files.write(file, form("GEN8" to byteArrayOf(1, 2), "SPRT" to byteArrayOf(3), "STRG" to byteArrayOf(4, 5, 6)))

        val index = GameMakerFormInspector.inspect(file)

        assertEquals(listOf("GEN8", "SPRT", "STRG"), index.chunks.map { it.name })
        assertEquals(GameMakerResourceCategory.SPRITES, index.requireChunk("SPRT").category)
        assertEquals(GameMakerResourceCategory.STRINGS, index.requireChunk("STRG").category)
        assertEquals(1, index.chunksFor(GameMakerResourceCategory.SPRITES).size)
    }

    @Test fun `rejects truncated declared container and out of bounds chunk`() {
        val truncated = temp.resolve("truncated.droid")
        Files.write(truncated, ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray())
            putInt(40)
        }.array())
        assertFailsWith<IllegalArgumentException> { GameMakerFormInspector.inspect(truncated) }

        val malformed = temp.resolve("malformed.droid")
        Files.write(malformed, ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray())
            putInt(8)
            put("SPRT".toByteArray())
            putInt(100)
        }.array())
        assertFailsWith<IllegalArgumentException> { GameMakerFormInspector.inspect(malformed) }
    }

    @Test fun `reads standard STRG pointer entries as UTF-8 text resources`() {
        val file = temp.resolve("strings.droid")
        val first = "Kris".toByteArray()
        val second = "Flowey".toByteArray()
        val stringPayloadSize = 4 + 8 + 4 + first.size + 1 + 4 + second.size + 1
        val contentSize = 8 + stringPayloadSize
        val firstOffset = 8 + 8 + 4 + 8
        val secondOffset = firstOffset + 4 + first.size + 1
        Files.write(file, ByteBuffer.allocate(8 + contentSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray())
            putInt(contentSize)
            put("STRG".toByteArray())
            putInt(stringPayloadSize)
            putInt(2)
            putInt(firstOffset)
            putInt(secondOffset)
            putInt(first.size); put(first); put(0)
            putInt(second.size); put(second); put(0)
        }.array())

        assertEquals(listOf("Kris", "Flowey"), GameMakerFormInspector.readStrings(file).map { it.content })
    }

    @Test fun `edits string entries without moving GameMaker resource offsets`() {
        val source = temp.resolve("source.droid")
        val destination = temp.resolve("edited.droid")
        Files.write(source, formWithStrings("Kris", "Flowey"))
        val before = GameMakerFormInspector.readStrings(source)

        val after = GameMakerStringEditor.applySameOrShorterEdits(source, destination, listOf(GameMakerStringEdit(0, "Flo")))

        assertEquals(listOf("Flo", "Flowey"), after.map { it.content })
        assertEquals(before.map { it.offset }, after.map { it.offset })
        assertFailsWith<IllegalArgumentException> {
            GameMakerStringEditor.applySameOrShorterEdits(source, temp.resolve("too-long.droid"), listOf(GameMakerStringEdit(0, "Longer Than Kris")))
        }
    }

    @Test fun `inserts an edited GameMaker draft into an APK asset without unrelated archive changes`() {
        val originalPayload = temp.resolve("original.droid")
        val editedPayload = temp.resolve("edited.droid")
        val originalApk = temp.resolve("hadrian-original.apk")
        val rebuiltApk = temp.resolve("hadrian-modded.apk")
        Files.write(originalPayload, formWithStrings("Kris", "Flowey"))
        GameMakerStringEditor.applySameOrShorterEdits(originalPayload, editedPayload, listOf(GameMakerStringEdit(0, "Flo")))
        ZipOutputStream(Files.newOutputStream(originalApk)).use { zip ->
            zip.putNextEntry(java.util.zip.ZipEntry("AndroidManifest.xml")); zip.write(byteArrayOf(1)); zip.closeEntry()
            zip.putNextEntry(java.util.zip.ZipEntry("assets/game.droid")); zip.write(Files.readAllBytes(originalPayload)); zip.closeEntry()
            zip.putNextEntry(java.util.zip.ZipEntry("assets/untouched.bin")); zip.write(byteArrayOf(7, 8)); zip.closeEntry()
        }

        ApkArchive.rebuildWithReplacements(originalApk, rebuiltApk, mapOf("assets/game.droid" to editedPayload))

        val extracted = temp.resolve("rebuilt.droid")
        ZipFile(rebuiltApk.toFile()).use { zip ->
            Files.newOutputStream(extracted).use { output -> zip.getInputStream(zip.getEntry("assets/game.droid")).copyTo(output) }
            assertEquals(byteArrayOf(7, 8).toList(), zip.getInputStream(zip.getEntry("assets/untouched.bin")).readBytes().toList())
        }
        assertEquals(listOf("Flo", "Flowey"), GameMakerFormInspector.readStrings(extracted).map { it.content })
    }

    @Test fun `indexes named sprite resources through their real string pointers`() {
        val file = temp.resolve("sprites.droid")
        Files.write(file, formWithNamedSprite("spr_kris"))

        val sprites = GameMakerFormInspector.readNamedResources(file, "SPRT")

        assertEquals(1, sprites.size)
        assertEquals("spr_kris", sprites.single().name)
    }

    @Test fun `aliases an object to an existing sprite without moving GameMaker resources`() {
        val source = temp.resolve("objects.droid")
        val destination = temp.resolve("flowey-kris.droid")
        Files.write(source, formWithSpriteAndObject("spr_flowey", "obj_kris", 0))

        val before = GameMakerObjectSpriteEditor.readObjects(source)
        val after = GameMakerObjectSpriteEditor.applyAliases(source, destination, listOf(GameMakerObjectSpriteAlias(0, 0)))

        assertEquals("obj_kris", before.single().name)
        assertEquals(0, after.single().spriteIndex)
        assertEquals(before.single().offset, after.single().offset)
        assertFailsWith<IllegalArgumentException> {
            GameMakerObjectSpriteEditor.applyAliases(source, temp.resolve("invalid-sprite.droid"), listOf(GameMakerObjectSpriteAlias(0, 9)))
        }
    }

    private fun form(vararg chunks: Pair<String, ByteArray>): ByteArray {
        val contentSize = chunks.sumOf { 8 + it.second.size }
        return ByteBuffer.allocate(8 + contentSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray())
            putInt(contentSize)
            chunks.forEach { (name, payload) ->
                put(name.toByteArray())
                putInt(payload.size)
                put(payload)
            }
        }.array()
    }

    private fun formWithStrings(firstString: String, secondString: String): ByteArray {
        val first = firstString.toByteArray()
        val second = secondString.toByteArray()
        val stringPayloadSize = 4 + 8 + 4 + first.size + 1 + 4 + second.size + 1
        val contentSize = 8 + stringPayloadSize
        val firstOffset = 8 + 8 + 4 + 8
        val secondOffset = firstOffset + 4 + first.size + 1
        return ByteBuffer.allocate(8 + contentSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray())
            putInt(contentSize)
            put("STRG".toByteArray())
            putInt(stringPayloadSize)
            putInt(2)
            putInt(firstOffset)
            putInt(secondOffset)
            putInt(first.size); put(first); put(0)
            putInt(second.size); put(second); put(0)
        }.array()
    }

    private fun formWithNamedSprite(name: String): ByteArray {
        val nameBytes = name.toByteArray()
        val strgPayloadSize = 4 + 4 + 4 + nameBytes.size + 1
        val sprtPayloadSize = 4 + 4 + 4
        val contentSize = 8 + strgPayloadSize + 8 + sprtPayloadSize
        val stringOffset = 8 + 8 + 4 + 4
        val spriteOffset = 8 + 8 + strgPayloadSize + 8 + 4 + 4
        return ByteBuffer.allocate(8 + contentSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray()); putInt(contentSize)
            put("STRG".toByteArray()); putInt(strgPayloadSize)
            putInt(1); putInt(stringOffset); putInt(nameBytes.size); put(nameBytes); put(0)
            put("SPRT".toByteArray()); putInt(sprtPayloadSize)
            putInt(1); putInt(spriteOffset); putInt(stringOffset)
        }.array()
    }

    private fun formWithSpriteAndObject(spriteName: String, objectName: String, spriteId: Int): ByteArray {
        val spriteBytes = spriteName.toByteArray()
        val objectBytes = objectName.toByteArray()
        val strgPayload = 4 + 8 + 4 + spriteBytes.size + 1 + 4 + objectBytes.size + 1
        val sprtPayload = 4 + 4 + 4
        val objtPayload = 4 + 4 + 8
        val contentSize = 8 + strgPayload + 8 + sprtPayload + 8 + objtPayload
        val spriteNameOffset = 8 + 8 + 4 + 8
        val objectNameOffset = spriteNameOffset + 4 + spriteBytes.size + 1
        val spriteObjectOffset = 8 + 8 + strgPayload + 8 + 4 + 4
        val gameObjectOffset = 8 + 8 + strgPayload + 8 + sprtPayload + 8 + 4 + 4
        return ByteBuffer.allocate(8 + contentSize).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("FORM".toByteArray()); putInt(contentSize)
            put("STRG".toByteArray()); putInt(strgPayload)
            putInt(2); putInt(spriteNameOffset); putInt(objectNameOffset)
            putInt(spriteBytes.size); put(spriteBytes); put(0)
            putInt(objectBytes.size); put(objectBytes); put(0)
            put("SPRT".toByteArray()); putInt(sprtPayload); putInt(1); putInt(spriteObjectOffset); putInt(spriteNameOffset)
            put("OBJT".toByteArray()); putInt(objtPayload); putInt(1); putInt(gameObjectOffset); putInt(objectNameOffset); putInt(spriteId)
        }.array()
    }
}
