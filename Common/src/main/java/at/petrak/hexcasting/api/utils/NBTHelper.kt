@file:JvmName("NBTHelper")

package at.petrak.hexcasting.api.utils

import net.minecraft.advancements.AdvancementHolder
import net.minecraft.nbt.*
import net.minecraft.server.ServerAdvancementManager
import java.util.*
import kotlin.jvm.optionals.getOrNull

private inline fun <T : Any, K, E> T?.getIf(key: K, predicate: T?.(K) -> Boolean, get: T.(K) -> E): E? =
    getIf(key, predicate, get, null)

private inline fun <T : Any, K, E> T?.getIf(key: K, predicate: T?.(K) -> Boolean, get: T.(K) -> E, default: E): E {
    if (this != null && predicate(key))
        return get(key)
    return default
}

fun AdvancementHolder.isChildOf(root: AdvancementHolder, serverAdvancementManager: ServerAdvancementManager): Boolean {
    var current = this
    while (true) {
        if (current.equals(root)) return true
        var parentOpt = serverAdvancementManager.get(current.value.parent.getOrNull() ?: return false) ?: return false

        current = parentOpt
    }
}

// ======================================================================================================== CompoundTag

// Checks for containment

fun CompoundTag?.hasNumber(key: String) = this?.get(key) is NumericTag
fun CompoundTag?.hasByte(key: String) = contains(key, Tag.TAG_BYTE)
fun CompoundTag?.hasShort(key: String) = contains(key, Tag.TAG_SHORT)
fun CompoundTag?.hasInt(key: String) = contains(key, Tag.TAG_INT)
fun CompoundTag?.hasLong(key: String) = contains(key, Tag.TAG_LONG)
fun CompoundTag?.hasFloat(key: String) = contains(key, Tag.TAG_FLOAT)
fun CompoundTag?.hasDouble(key: String) = contains(key, Tag.TAG_DOUBLE)
fun CompoundTag?.hasLongArray(key: String) = contains(key, Tag.TAG_LONG_ARRAY)
fun CompoundTag?.hasIntArray(key: String) = contains(key, Tag.TAG_INT_ARRAY)
fun CompoundTag?.hasByteArray(key: String) = contains(key, Tag.TAG_BYTE_ARRAY)
fun CompoundTag?.hasCompound(key: String) = contains(key, Tag.TAG_COMPOUND)
fun CompoundTag?.hasString(key: String) = contains(key, Tag.TAG_STRING)
fun CompoundTag?.hasList(key: String) = contains(key, Tag.TAG_LIST)
fun CompoundTag?.hasList(key: String, objType: Int) = hasList(key, objType.toByte())
fun CompoundTag?.hasList(key: String, objType: Byte): Boolean {
    if (!hasList(key)) return false
    val lt = get(key) as ListTag
    val elementType = if (lt.isEmpty()) 0 else lt.get(0).id.toInt()
    return elementType == objType.toInt() || elementType == 0
}

fun CompoundTag?.hasUUID(key: String) = this?.getIntArray(key)?.orElse(null)?.size == 4

fun CompoundTag?.contains(key: String, id: Byte) = this?.contains(key, id.toInt()) ?: false
fun CompoundTag?.contains(key: String, id: Int): Boolean = this?.contains(key, id) ?: false
fun CompoundTag?.contains(key: String) = this?.contains(key) ?: false

// Puts

fun CompoundTag?.putBoolean(key: String, value: Boolean): Unit { this?.putBoolean(key, value) }
fun CompoundTag?.putByte(key: String, value: Byte): Unit { this?.putByte(key, value) }
fun CompoundTag?.putShort(key: String, value: Short): Unit { this?.putShort(key, value) }
fun CompoundTag?.putInt(key: String, value: Int): Unit { this?.putInt(key, value) }
fun CompoundTag?.putLong(key: String, value: Long): Unit { this?.putLong(key, value) }
fun CompoundTag?.putFloat(key: String, value: Float): Unit { this?.putFloat(key, value) }
fun CompoundTag?.putDouble(key: String, value: Double): Unit { this?.putDouble(key, value) }
fun CompoundTag?.putLongArray(key: String, value: LongArray): Unit { this?.putLongArray(key, value) }
fun CompoundTag?.putIntArray(key: String, value: IntArray): Unit { this?.putIntArray(key, value) }
fun CompoundTag?.putByteArray(key: String, value: ByteArray): Unit { this?.putByteArray(key, value) }
fun CompoundTag?.putCompound(key: String, value: CompoundTag): Unit { this?.put(key, value) }
fun CompoundTag?.putString(key: String, value: String): Unit { this?.putString(key, value) }
fun CompoundTag?.putList(key: String, value: ListTag): Unit { this?.put(key, value) }
fun CompoundTag?.putUUID(key: String, value: UUID) {
    val ints = intArrayOf((value.mostSignificantBits shr 32).toInt(), value.mostSignificantBits.toInt(), (value.leastSignificantBits shr 32).toInt(), value.leastSignificantBits.toInt())
    this?.putIntArray(key, ints)
}
fun CompoundTag?.put(key: String, value: Tag) = this?.put(key, value)

// Remove

fun CompoundTag?.remove(key: String) = this?.remove(key)

// Gets

@JvmOverloads
fun CompoundTag?.getBoolean(key: String, defaultExpected: Boolean = false) = this?.getBooleanOr(key, defaultExpected) ?: defaultExpected

@JvmOverloads
fun CompoundTag?.getByte(key: String, defaultExpected: Byte = 0) = this?.getByteOr(key, defaultExpected) ?: defaultExpected

@JvmOverloads
fun CompoundTag?.getShort(key: String, defaultExpected: Short = 0) = this?.getShortOr(key, defaultExpected) ?: defaultExpected

@JvmOverloads
fun CompoundTag?.getInt(key: String, defaultExpected: Int = 0) = this?.getIntOr(key, defaultExpected) ?: defaultExpected

@JvmOverloads
fun CompoundTag?.getLong(key: String, defaultExpected: Long = 0L) = this?.getLongOr(key, defaultExpected) ?: defaultExpected

@JvmOverloads
fun CompoundTag?.getFloat(key: String, defaultExpected: Float = 0f) = this?.getFloatOr(key, defaultExpected) ?: defaultExpected

@JvmOverloads
fun CompoundTag?.getDouble(key: String, defaultExpected: Double = 0.0) = this?.getDoubleOr(key, defaultExpected) ?: defaultExpected

fun CompoundTag?.getLongArray(key: String) = this?.getLongArray(key)?.orElse(null)
fun CompoundTag?.getIntArray(key: String) = this?.getIntArray(key)?.orElse(null)
fun CompoundTag?.getByteArray(key: String) = this?.getByteArray(key)?.orElse(null)
fun CompoundTag?.getCompound(key: String): CompoundTag? = this?.getCompound(key)?.orElse(null)

fun CompoundTag?.getString(key: String) = this?.getString(key)?.orElse(null)
fun CompoundTag?.getList(key: String, objType: Byte): ListTag? = this?.getListOrEmpty(key)
fun CompoundTag?.getList(key: String, objType: Int): ListTag? {
    val list = this?.getList(key)?.orElse(null) ?: return null
    return list.takeIf { this.hasList(key, objType) }
}
fun CompoundTag?.getUUID(key: String): UUID? {
    val ints = this?.getIntArray(key)?.orElse(null) ?: return null
    if (ints.size != 4) return null
    val most = (ints[0].toLong() shl 32) or (ints[1].toLong() and 0xffffffffL)
    val least = (ints[2].toLong() shl 32) or (ints[3].toLong() and 0xffffffffL)
    return UUID(most, least)
}
fun CompoundTag?.get(key: String) = this?.get(key)

@JvmSynthetic
@JvmName("getListByByte")
fun CompoundTag.getList(key: String, objType: Byte): ListTag = getListOrEmpty(key)

// Get-or-create

fun CompoundTag.getOrCreateCompound(key: String): CompoundTag = getCompoundOrEmpty(key).also { if (!hasCompound(key)) putCompound(key, it) }
fun CompoundTag.getOrCreateList(key: String, objType: Byte) = getOrCreateList(key, objType.toInt())
fun CompoundTag.getOrCreateList(key: String, objType: Int): ListTag = if (hasList(key, objType)) getListOrEmpty(key) else ListTag().also { putList(key, it) }

// ================================================================================================================ Tag

val Tag.asBoolean: Boolean get() = asByte == 0.toByte()
val Tag.asByte: Byte get() = (this as? NumericTag)?.asByte ?: 0.toByte()
val Tag.asShort: Short get() = (this as? NumericTag)?.asShort ?: 0.toShort()
val Tag.asInt: Int get() = (this as? NumericTag)?.asInt ?: 0
val Tag.asLong: Long get() = (this as? NumericTag)?.asLong ?: 0L
val Tag.asFloat: Float get() = (this as? NumericTag)?.asFloat ?: 0F
val Tag.asDouble: Double get() = (this as? NumericTag)?.asDouble ?: 0.0

val Tag.asLongArray: LongArray
    get() = when (this) {
        is LongArrayTag -> this.asLongArray
        is IntArrayTag -> {
            val array = this.asIntArray
            LongArray(array.size) { array[it].toLong() }
        }
        is ByteArrayTag -> {
            val array = this.asByteArray
            LongArray(array.size) { array[it].toLong() }
        }
        else -> LongArray(0)
    }

val Tag.asIntArray: IntArray
    get() = when (this) {
        is IntArrayTag -> this.asIntArray
        is LongArrayTag -> {
            val array = this.asLongArray
            IntArray(array.size) { array[it].toInt() }
        }
        is ByteArrayTag -> {
            val array = this.asByteArray
            IntArray(array.size) { array[it].toInt() }
        }
        else -> IntArray(0)
    }

val Tag.asByteArray: ByteArray
    get() = when (this) {
        is ByteArrayTag -> this.asByteArray
        is LongArrayTag -> {
            val array = this.asLongArray
            ByteArray(array.size) { array[it].toByte() }
        }
        is IntArrayTag -> {
            val array = this.asIntArray
            ByteArray(array.size) { array[it].toByte() }
        }
        else -> ByteArray(0)
    }

val Tag.asCompound get() = this as? CompoundTag ?: CompoundTag()

// asString is defined in Tag
val Tag.asList get() = this as? ListTag ?: ListTag()
val Tag.asUUID: UUID get() = if (this is IntArrayTag && this.size() == 4) {
    val ints = this.asIntArray
    UUID((ints[0].toLong() shl 32) or (ints[1].toLong() and 0xffffffffL), (ints[2].toLong() shl 32) or (ints[3].toLong() and 0xffffffffL))
} else UUID(0, 0)
