package core.cache

import java.nio.ByteBuffer

class StoreFile(
    data: ByteArray,
) {
    var isDynamic: Boolean = false
    private var data: ByteArray = data

    fun put(buffer: ByteBuffer) {
        val data = ByteArray(buffer.remaining())
        buffer[data]
        this.data = data
    }

    fun data(): ByteBuffer {
        return ByteBuffer.wrap(data)
    }

    fun setData(data: ByteArray) {
        this.data = data
    }
}
