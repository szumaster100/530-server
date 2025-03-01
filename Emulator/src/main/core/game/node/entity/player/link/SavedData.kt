package core.game.node.entity.player.link

import core.game.node.entity.player.Player
import it.unimi.dsi.fastutil.booleans.BooleanArrayList
import it.unimi.dsi.fastutil.doubles.DoubleArrayList
import it.unimi.dsi.fastutil.ints.IntArrayList
import java.nio.ByteBuffer

class SavedData(
    val player: Player,
) {
    @JvmField
    val globalData: GlobalData = GlobalData()

    @JvmField
    val activityData: ActivityData = ActivityData()

    @JvmField
    val questData: QuestData = QuestData()

    companion object {
        fun save(
            buffer: ByteBuffer,
            value: Any?,
            index: Int,
        ) {
            if (isNonDefault(value)) {
                buffer.put(index.toByte())
                when (value) {
                    is Int -> buffer.putInt(value)
                    is Byte -> buffer.put(value)
                    is Short -> buffer.putShort(value)
                    is Long -> buffer.putLong(value)
                    is Boolean -> buffer.put(if (value) 1 else 0)
                    is Double -> buffer.putDouble(value)
                    is DoubleArray -> value.forEach { buffer.putDouble(it) }
                    is BooleanArray -> value.forEach { buffer.put(if (it) 1 else 0) }
                    is IntArray -> value.forEach { buffer.putInt(it) }
                    is IntArrayList -> value.forEach { buffer.putInt(it) }
                    is DoubleArrayList -> value.forEach { buffer.putDouble(it) }
                    is BooleanArrayList -> value.forEach { buffer.put(if (it) 1 else 0) }
                    else -> throw IllegalArgumentException("Unsupported type: ${value!!::class}")
                }
            }
        }

        private fun isNonDefault(value: Any?): Boolean {
            return when (value) {
                is Int -> value != 0
                is Double -> value != 0.0
                is Byte -> value != 0.toByte()
                is Short -> value != 0.toShort()
                is Long -> value != 0L
                is Boolean -> value != false
                is BooleanArray -> value.any { it }
                is IntArray -> value.any { it != 0 }
                is DoubleArray -> value.any { it != 0.0 }
                is IntArrayList -> value.any { it != 0 }
                is DoubleArrayList -> value.any { it != 0.0 }
                is BooleanArrayList -> value.any { it }
                else -> value != null
            }
        }

        fun getBoolean(value: Byte): Boolean = value.toInt() == 1

        fun getBoolean(buffer: ByteBuffer): Boolean = getBoolean(buffer.get())
    }
}
