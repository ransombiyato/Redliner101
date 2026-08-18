package com.ransombiyato.demiforge.core.gamemaker

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption

data class GameMakerObjectResource(
    val index: Int,
    val offset: Long,
    val name: String?,
    val spriteIndex: Int,
)

data class GameMakerObjectSpriteAlias(
    val objectIndex: Int,
    val spriteIndex: Int,
)

/**
 * GameMaker objects store their sprite resource ID immediately after the object name pointer. This
 * editor changes only that 32-bit ID, which preserves all chunk addresses and texture data.
 */
object GameMakerObjectSpriteEditor {
    fun readObjects(file: Path, index: GameMakerFormIndex = GameMakerFormInspector.inspect(file)): List<GameMakerObjectResource> {
        val objects = GameMakerFormInspector.readNamedResources(file, "OBJT", index)
        val spriteCount = GameMakerFormInspector.readNamedResources(file, "SPRT", index).size
        FileChannel.open(file, StandardOpenOption.READ).use { channel ->
            return objects.map { resource ->
                require(resource.offset + 8 <= index.fileSize) { "Object #${resource.index} is truncated before its sprite ID." }
                val spriteIndex = readInt(channel, resource.offset + 4)
                require(spriteIndex in -1 until spriteCount) { "Object #${resource.index} references invalid sprite ID $spriteIndex." }
                GameMakerObjectResource(resource.index, resource.offset, resource.name, spriteIndex)
            }
        }
    }

    fun applyAliases(source: Path, destination: Path, aliases: List<GameMakerObjectSpriteAlias>): List<GameMakerObjectResource> {
        require(aliases.isNotEmpty()) { "Choose at least one object-to-sprite alias." }
        require(aliases.map { it.objectIndex }.distinct().size == aliases.size) { "An object can only be changed once per save." }
        val index = GameMakerFormInspector.inspect(source)
        val objects = readObjects(source, index).associateBy { it.index }
        val spriteCount = GameMakerFormInspector.readNamedResources(source, "SPRT", index).size
        aliases.forEach { alias ->
            requireNotNull(objects[alias.objectIndex]) { "Object #${alias.objectIndex} does not exist." }
            require(alias.spriteIndex in 0 until spriteCount) { "Sprite #${alias.spriteIndex} does not exist." }
        }
        Files.createDirectories(destination.parent)
        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING)
        FileChannel.open(destination, StandardOpenOption.WRITE).use { channel ->
            aliases.forEach { alias -> writeFully(channel, intBuffer(alias.spriteIndex), requireNotNull(objects[alias.objectIndex]).offset + 4) }
        }
        return readObjects(destination)
    }

    private fun readInt(channel: FileChannel, offset: Long): Int {
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
        channel.position(offset)
        while (buffer.hasRemaining()) require(channel.read(buffer) >= 0) { "Unexpected end of GameMaker payload." }
        return buffer.flip().order(ByteOrder.LITTLE_ENDIAN).int
    }

    private fun intBuffer(value: Int): ByteBuffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).apply { flip() }

    private fun writeFully(channel: FileChannel, buffer: ByteBuffer, offset: Long) {
        var cursor = offset
        while (buffer.hasRemaining()) {
            val written = channel.write(buffer, cursor)
            require(written > 0) { "Could not finish writing the GameMaker object sprite ID." }
            cursor += written
        }
    }
}
