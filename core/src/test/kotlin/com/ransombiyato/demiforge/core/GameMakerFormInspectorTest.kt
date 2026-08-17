package com.ransombiyato.demiforge.core

import com.ransombiyato.demiforge.core.gamemaker.GameMakerFormInspector
import com.ransombiyato.demiforge.core.gamemaker.GameMakerResourceCategory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.file.Files
import java.nio.file.Path
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
}
