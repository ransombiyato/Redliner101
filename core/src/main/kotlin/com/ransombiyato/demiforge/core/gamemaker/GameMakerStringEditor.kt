package com.ransombiyato.demiforge.core.gamemaker

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

data class GameMakerStringEdit(
    val index: Int,
    val replacement: String,
)

/**
 * Applies safe, size-preserving STRG edits. Every replacement must be no larger than its original
 * UTF-8 byte region, so no GameMaker pointer or resource offset moves. More ambitious edits require
 * a complete version-aware serializer and are intentionally not attempted here.
 */
object GameMakerStringEditor {
    fun applySameOrShorterEdits(source: Path, destination: Path, edits: List<GameMakerStringEdit>): List<GameMakerStringEntry> {
        require(edits.isNotEmpty()) { "Choose at least one string edit." }
        val index = GameMakerFormInspector.inspect(source)
        val originals = GameMakerFormInspector.readStrings(source, index).associateBy { it.index }
        require(edits.map { it.index }.distinct().size == edits.size) { "A string can only be edited once per save." }
        val checked = edits.map { edit ->
            val original = requireNotNull(originals[edit.index]) { "String #${edit.index} does not exist." }
            val bytes = edit.replacement.toByteArray(Charsets.UTF_8)
            require(bytes.size <= original.originalByteLength) {
                "String #${edit.index} uses ${bytes.size} bytes but only ${original.originalByteLength} are available."
            }
            Triple(original, edit.replacement, bytes)
        }
        Files.createDirectories(destination.parent)
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
        FileChannel.open(destination, StandardOpenOption.WRITE).use { channel ->
            checked.forEach { (original, _, bytes) ->
                writeFully(channel, intBuffer(bytes.size), original.offset)
                writeFully(channel, ByteBuffer.wrap(bytes), original.offset + 4)
                writeFully(channel, ByteBuffer.wrap(byteArrayOf(0)), original.offset + 4 + bytes.size)
                val unusedBytes = original.originalByteLength - bytes.size
                if (unusedBytes > 0) writeFully(channel, ByteBuffer.allocate(unusedBytes), original.offset + 5 + bytes.size)
            }
        }
        return GameMakerFormInspector.readStrings(destination)
    }

    private fun intBuffer(value: Int): ByteBuffer = ByteBuffer.allocate(4)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putInt(value)
        .apply { flip() }

    private fun writeFully(channel: FileChannel, buffer: ByteBuffer, offset: Long) {
        var cursor = offset
        while (buffer.hasRemaining()) {
            val written = channel.write(buffer, cursor)
            require(written > 0) { "Could not finish writing the GameMaker payload." }
            cursor += written
        }
    }
}
