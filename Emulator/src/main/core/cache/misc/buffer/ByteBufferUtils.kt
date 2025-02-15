package core.cache.misc.buffer

import java.io.ObjectInputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

object ByteBufferUtils {
    /*
     * Get a string from the buffer.
     */

    @JvmStatic
    fun getString(buffer: ByteBuffer): String {
        val sb = StringBuilder()
        var b: Byte
        while (buffer.hasRemaining()) {
            b = buffer.get()
            if (b.toInt() == 0) break
            sb.append(b.toChar())
        }
        return sb.toString()
    }

    /*
     * Put a string into the buffer.
     */

    @JvmStatic
    fun putString(
        s: String,
        buffer: ByteBuffer,
    ) {
        buffer.put(s.toByteArray(StandardCharsets.UTF_8)).put(0.toByte())
    }

    /*
     * Put a GJ2 string into the buffer.
     */

    @JvmStatic
    fun putGJ2String(
        s: String,
        buffer: ByteBuffer,
    ): ByteBuffer {
        val packed = ByteArray(256)
        val length = packGJString2(0, packed, s)
        return buffer.put(0.toByte()).put(packed, 0, length).put(0.toByte())
    }

    /*
     * Pack a GJ string (used for string compression).
     */

    @JvmStatic
    fun packGJString2(
        position: Int,
        buffer: ByteArray,
        string: String,
    ): Int {
        val length = string.length
        var offset = position
        for (i in 0 until length) {
            val character = string[i].toInt()
            if (character > 127) {
                if (character > 2047) {
                    buffer[offset++] = ((character or 919275) shr 12).toByte()
                    buffer[offset++] = (128 or ((character shr 6) and 63)).toByte()
                    buffer[offset++] = (128 or (character and 63)).toByte()
                } else {
                    buffer[offset++] = ((character or 12309) shr 6).toByte()
                    buffer[offset++] = (128 or (character and 63)).toByte()
                }
            } else {
                buffer[offset++] = character.toByte()
            }
        }
        return offset - position
    }

    /*
     * Get a medium-sized value from the buffer.
     */

    @JvmStatic
    fun getMedium(buffer: ByteBuffer): Int {
        return ((buffer.get().toInt() and 0xFF) shl 16) +
            ((buffer.get().toInt() and 0xFF) shl 8) +
            (buffer.get().toInt() and 0xFF)
    }

    /*
     * Get a smart-sized value from the buffer.
     */

    @JvmStatic
    fun getSmart(buffer: ByteBuffer): Int {
        val peek = buffer.get().toInt() and 0xFF
        return if (peek <= Byte.MAX_VALUE.toInt()) {
            peek
        } else {
            ((peek shl 8) or (buffer.get().toInt() and 0xFF)) - 32768
        }
    }

    /*
     * Get a big-smart-sized value from the buffer.
     */

    @JvmStatic
    fun getBigSmart(buffer: ByteBuffer): Int {
        var value = 0
        var current = getSmart(buffer)
        while (current == 32767) {
            current = getSmart(buffer)
            value += 32767
        }
        value += current
        return value
    }

    /*
     * Get an object from the buffer.
     */

    @JvmStatic
    fun getObject(buffer: ByteBuffer): Any? {
        val length = buffer.getInt()
        return if (length > 0) {
            val bytes = ByteArray(length)
            buffer.get(bytes)
            try {
                ObjectInputStream(BufferInputStream(ByteBuffer.wrap(bytes))).use { stream ->
                    stream.readObject()
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                null
            }
        } else {
            null
        }
    }
}
