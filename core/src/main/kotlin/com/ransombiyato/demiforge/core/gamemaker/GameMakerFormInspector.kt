package com.ransombiyato.demiforge.core.gamemaker

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

data class GameMakerChunk(
    val name: String,
    val payloadOffset: Long,
    val payloadSize: Long,
) {
    val endOffset: Long get() = payloadOffset + payloadSize
    val category: GameMakerResourceCategory get() = GameMakerResourceCategory.fromChunk(name)
}

enum class GameMakerResourceCategory(val label: String) {
    GENERAL("General information"),
    SPRITES("Sprites"),
    STRINGS("Strings"),
    OBJECTS("Game objects"),
    ROOMS("Rooms"),
    SCRIPTS("Scripts"),
    CODE("Code"),
    TEXTURES("Texture pages"),
    AUDIO("Audio"),
    OTHER("Other resources");

    companion object {
        fun fromChunk(name: String) = when (name) {
            "GEN8" -> GENERAL
            "SPRT" -> SPRITES
            "STRG" -> STRINGS
            "OBJT" -> OBJECTS
            "ROOM" -> ROOMS
            "SCPT" -> SCRIPTS
            "CODE" -> CODE
            "TPAG", "TXTR", "TGIN" -> TEXTURES
            "SOND", "AUDO", "AGRP" -> AUDIO
            else -> OTHER
        }
    }
}

data class GameMakerFormIndex(
    val fileSize: Long,
    val declaredPayloadSize: Long,
    val chunks: List<GameMakerChunk>,
) {
    fun chunksFor(category: GameMakerResourceCategory): List<GameMakerChunk> = chunks.filter { it.category == category }
    fun requireChunk(name: String): GameMakerChunk = chunks.firstOrNull { it.name == name }
        ?: throw IllegalArgumentException("GameMaker payload does not contain $name.")
}

data class GameMakerStringEntry(
    val index: Int,
    val offset: Long,
    val originalByteLength: Int,
    val content: String,
)

data class GameMakerNamedResource(
    val chunkName: String,
    val index: Int,
    val offset: Long,
    val nameStringOffset: Long,
    val name: String?,
)

/**
 * Read-only structural parser for the GameMaker `FORM` container. It is deliberately narrow: it
 * recognises, bounds-checks, and indexes chunk regions before higher-level resource parsers are
 * permitted to interpret or edit them.
 */
object GameMakerFormInspector {
    private const val HEADER_SIZE = 8L
    private const val MAX_CHUNKS = 10_000
    private const val MAX_STRINGS = 200_000
    private const val MAX_STRING_BYTES = 16 * 1024 * 1024

    fun inspect(file: Path): GameMakerFormIndex {
        require(Files.isRegularFile(file)) { "GameMaker payload does not exist: $file" }
        FileChannel.open(file, StandardOpenOption.READ).use { channel ->
            val size = channel.size()
            require(size >= HEADER_SIZE) { "GameMaker payload is too short for a FORM header." }
            val header = read(channel, 0, HEADER_SIZE.toInt())
            val formName = ascii(header, 0)
            require(formName == "FORM") { "Expected GameMaker FORM header, found $formName." }
            val declaredSize = unsignedLittleEndianInt(header, 4)
            val formEnd = HEADER_SIZE + declaredSize
            require(formEnd <= size) { "FORM declares $declaredSize bytes but the payload ends at $size." }
            require(formEnd == size) { "FORM has trailing bytes outside its declared container." }

            val chunks = mutableListOf<GameMakerChunk>()
            var cursor = HEADER_SIZE
            while (cursor < formEnd) {
                require(chunks.size < MAX_CHUNKS) { "GameMaker payload has too many chunks." }
                require(cursor + HEADER_SIZE <= formEnd) { "Truncated chunk header at byte $cursor." }
                val chunkHeader = read(channel, cursor, HEADER_SIZE.toInt())
                val name = ascii(chunkHeader, 0)
                require(name.all { it in 'A'..'Z' || it in '0'..'9' || it == '_' }) { "Invalid chunk name $name at byte $cursor." }
                val chunkSize = unsignedLittleEndianInt(chunkHeader, 4)
                val payloadOffset = cursor + HEADER_SIZE
                val endOffset = payloadOffset + chunkSize
                require(endOffset >= payloadOffset && endOffset <= formEnd) { "Chunk $name exceeds FORM bounds." }
                chunks += GameMakerChunk(name, payloadOffset, chunkSize)
                cursor = endOffset
            }
            require(cursor == formEnd) { "GameMaker chunks did not end at the FORM boundary." }
            return GameMakerFormIndex(size, declaredSize, chunks)
        }
    }

    /**
     * Reads the standard STRG pointer list and its null-terminated UTF-8 values. This is read-only
     * until a higher-level editor can round-trip a verified payload version.
     */
    fun stringCount(file: Path, index: GameMakerFormIndex = inspect(file)): Int {
        val strg = index.requireChunk("STRG")
        require(strg.payloadSize >= 4) { "STRG chunk is too short for a string count." }
        FileChannel.open(file, StandardOpenOption.READ).use { channel ->
            val count = unsignedLittleEndianInt(read(channel, strg.payloadOffset, 4), 0)
            require(count <= MAX_STRINGS) { "STRG claims too many strings: $count." }
            require(4L + count * 4L <= strg.payloadSize) { "STRG pointer table exceeds its chunk boundary." }
            return count.toInt()
        }
    }

    fun readStrings(file: Path, index: GameMakerFormIndex = inspect(file), maxEntries: Int = Int.MAX_VALUE): List<GameMakerStringEntry> {
        require(maxEntries >= 0) { "String preview limit cannot be negative." }
        val strg = index.requireChunk("STRG")
        require(strg.payloadSize >= 4) { "STRG chunk is too short for a string count." }
        FileChannel.open(file, StandardOpenOption.READ).use { channel ->
            val count = unsignedLittleEndianInt(read(channel, strg.payloadOffset, 4), 0)
            require(count <= MAX_STRINGS) { "STRG claims too many strings: $count." }
            val tableBytes = 4L + count * 4L
            require(tableBytes <= strg.payloadSize) { "STRG pointer table exceeds its chunk boundary." }
            val pointerTable = read(channel, strg.payloadOffset + 4, (count * 4L).toInt())
            return buildList {
                repeat(minOf(count.toInt(), maxEntries)) { entryIndex ->
                    val offset = unsignedLittleEndianInt(pointerTable, entryIndex * 4)
                    if (offset == 0L) return@repeat
                    val value = readStringAt(channel, offset, index.fileSize, "STRG entry $entryIndex")
                    add(GameMakerStringEntry(entryIndex, offset, value.byteLength, value.content))
                }
            }
        }
    }

    /**
     * Enumerates pointer-list resource names without assuming a particular Deltarune naming scheme.
     * Supported chunks use the standard GameMaker named-resource layout with a string pointer first.
     */
    fun readNamedResources(
        file: Path,
        chunkName: String,
        index: GameMakerFormIndex = inspect(file),
        maxEntries: Int = 10_000,
    ): List<GameMakerNamedResource> {
        require(chunkName in setOf("SPRT", "OBJT", "ROOM", "SCPT", "CODE", "BGND", "FONT", "PATH", "SHDR", "TMLN")) {
            "$chunkName is not a supported named-resource chunk."
        }
        require(maxEntries in 1..MAX_STRINGS) { "Resource preview limit is out of bounds." }
        val chunk = index.requireChunk(chunkName)
        require(chunk.payloadSize >= 4) { "$chunkName chunk is too short for a resource count." }
        FileChannel.open(file, StandardOpenOption.READ).use { channel ->
            val count = unsignedLittleEndianInt(read(channel, chunk.payloadOffset, 4), 0)
            require(count <= MAX_STRINGS) { "$chunkName claims too many resources: $count." }
            require(4L + count * 4L <= chunk.payloadSize) { "$chunkName pointer table exceeds its chunk boundary." }
            val table = read(channel, chunk.payloadOffset + 4, (count * 4L).toInt())
            return buildList {
                repeat(minOf(count.toInt(), maxEntries)) { entryIndex ->
                    val offset = unsignedLittleEndianInt(table, entryIndex * 4)
                    if (offset == 0L) return@repeat
                    require(offset + 4 <= index.fileSize) { "$chunkName resource #$entryIndex points outside the payload." }
                    val nameOffset = unsignedLittleEndianInt(read(channel, offset, 4), 0)
                    val name = if (nameOffset == 0L) null else readStringAt(channel, nameOffset, index.fileSize, "$chunkName resource #$entryIndex name").content
                    add(GameMakerNamedResource(chunkName, entryIndex, offset, nameOffset, name))
                }
            }
        }
    }

    private fun read(channel: FileChannel, offset: Long, length: Int): ByteBuffer {
        val buffer = ByteBuffer.allocate(length)
        channel.position(offset)
        while (buffer.hasRemaining()) require(channel.read(buffer) >= 0) { "Unexpected end of GameMaker payload." }
        return buffer.apply { flip() }
    }

    private fun bytesToUtf8(buffer: ByteBuffer): String {
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return bytes.toString(Charsets.UTF_8)
    }

    private fun readStringAt(channel: FileChannel, offset: Long, fileSize: Long, label: String): DecodedString {
        require(offset + 5 <= fileSize) { "$label points outside the payload." }
        val length = unsignedLittleEndianInt(read(channel, offset, 4), 0)
        require(length <= MAX_STRING_BYTES) { "$label is too large." }
        require(offset + 4 + length + 1 <= fileSize) { "$label exceeds the payload." }
        val bytes = read(channel, offset + 4, length.toInt())
        val terminator = read(channel, offset + 4 + length, 1).get(0)
        require(terminator == 0.toByte()) { "$label is not null terminated." }
        return DecodedString(length.toInt(), bytesToUtf8(bytes))
    }

    private data class DecodedString(val byteLength: Int, val content: String)

    private fun ascii(buffer: ByteBuffer, offset: Int): String = CharArray(4) { index -> buffer.get(offset + index).toInt().toChar() }.concatToString()

    private fun unsignedLittleEndianInt(buffer: ByteBuffer, offset: Int): Long = buffer.duplicate()
        .order(ByteOrder.LITTLE_ENDIAN)
        .getInt(offset)
        .toLong() and 0xffffffffL
}
