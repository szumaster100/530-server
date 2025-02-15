package core.cache.def.impl

import core.cache.misc.buffer.ByteBufferUtils
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.*

class ImageSprite(
    private val spriteWidth: Int,
    private val spriteHeight: Int,
    size: Int = 1,
) {
    companion object {
        const val VERTICAL_FLAG = 0x01
        const val ALPHA_FLAG = 0x02

        fun decode(buffer: ByteBuffer): ImageSprite? {
            try {
                buffer.position(buffer.limit() - 2)
                val size = buffer.short.toInt() and 0xFFFF
                val offsetsX = IntArray(size)
                val offsetsY = IntArray(size)
                val subWidths = IntArray(size)
                val subHeights = IntArray(size)
                buffer.position(buffer.limit() - size * 8 - 7)
                val width = buffer.short.toInt() and 0xFFFF
                val height = buffer.short.toInt() and 0xFFFF
                val palette = IntArray((buffer.get().toInt() and 0xFF) + 1)

                val sprite = ImageSprite(width, height, size)

                repeat(size) { offsetsX[it] = buffer.short.toInt() and 0xFFFF }
                repeat(size) { offsetsY[it] = buffer.short.toInt() and 0xFFFF }
                repeat(size) { subWidths[it] = buffer.short.toInt() and 0xFFFF }
                repeat(size) { subHeights[it] = buffer.short.toInt() and 0xFFFF }
                buffer.position(buffer.limit() - size * 8 - 7 - (palette.size - 1) * 3)
                palette[0] = 0
                for (index in 1 until palette.size) {
                    palette[index] = ByteBufferUtils.getMedium(buffer)
                    if (palette[index] == 0) palette[index] = 1
                }
                buffer.position(0)
                repeat(size) { id ->
                    val subWidth = subWidths[id]
                    val subHeight = subHeights[id]
                    val offsetX = offsetsX[id]
                    val offsetY = offsetsY[id]
                    if (subWidth > 1000 || subHeight > 1000 || width > 1000 || height > 1000) return@repeat

                    sprite.frames[id] = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

                    val image = sprite.frames[id]
                    val indices = Array(subWidth) { IntArray(subHeight) }
                    val flags = buffer.get().toInt() and 0xFF

                    if (image != null) {
                        if (flags and VERTICAL_FLAG != 0) {
                            repeat(subWidth) { x ->
                                repeat(subHeight) { y ->
                                    indices[x][y] = buffer.get().toInt() and 0xFF
                                }
                            }
                        } else {
                            repeat(subHeight) { y ->
                                repeat(subWidth) { x ->
                                    try {
                                        indices[x][y] = buffer.get().toInt() and 0xFF
                                    } catch (ex: Exception) {
                                    }
                                }
                            }
                        }

                        if (flags and ALPHA_FLAG != 0) {
                            if (flags and VERTICAL_FLAG != 0) {
                                repeat(subWidth) { x ->
                                    repeat(subHeight) { y ->
                                        val alpha = buffer.get().toInt() and 0xFF
                                        image.setRGB(x + offsetX, y + offsetY, alpha shl 24 or palette[indices[x][y]])
                                    }
                                }
                            } else {
                                repeat(subHeight) { y ->
                                    repeat(subWidth) { x ->
                                        val alpha = buffer.get().toInt() and 0xFF
                                        try {
                                            image.setRGB(
                                                x + offsetX,
                                                y + offsetY,
                                                alpha shl 24 or palette[indices[x][y]],
                                            )
                                        } catch (e: Exception) {
                                        }
                                    }
                                }
                            }
                        } else {
                            repeat(subWidth) { x ->
                                repeat(subHeight) { y ->
                                    val index = indices[x][y]
                                    if (index == 0) {
                                        image.setRGB(x + offsetX, y + offsetY, 0)
                                    } else {
                                        image.setRGB(
                                            x + offsetX,
                                            y + offsetY,
                                            (0xFF000000 or palette[index].toLong()).toInt(),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                return sprite
            } catch (ex: Exception) {
                return null
            }
        }
    }

    private val frames: Array<BufferedImage> =
        Array(1) { BufferedImage(spriteWidth, spriteHeight, BufferedImage.TYPE_INT_ARGB) }

    @Throws(IOException::class)
    fun encode(): ByteBuffer {
        val bout = ByteArrayOutputStream()
        val os = DataOutputStream(bout)
        os.use { os ->

            val palette = mutableListOf(0)
            frames.forEach { image ->
                if (image.width != spriteWidth || image.height != spriteHeight) {
                    throw IOException("All frames must be the same size.")
                }

                var flags = VERTICAL_FLAG
                for (x in 0 until spriteWidth) {
                    for (y in 0 until spriteHeight) {
                        val argb = image.getRGB(x, y)
                        val alpha = (argb shr 24) and 0xFF
                        var rgb = argb and 0xFFFFFF
                        if (rgb == 0) rgb = 1

                        if (alpha != 0 && alpha != 255) flags = flags or ALPHA_FLAG

                        if (!palette.contains(rgb)) {
                            if (palette.size >= 256) throw IOException("Too many colors in this sprite!")
                            palette.add(rgb)
                        }
                    }
                }
                os.write(flags)
                for (x in 0 until spriteWidth) {
                    for (y in 0 until spriteHeight) {
                        val argb = image.getRGB(x, y)
                        val alpha = (argb shr 24) and 0xFF
                        var rgb = argb and 0xFFFFFF
                        if (rgb == 0) rgb = 1

                        if (flags and ALPHA_FLAG == 0 && alpha == 0) {
                            os.write(0)
                        } else {
                            os.write(palette.indexOf(rgb))
                        }
                    }
                }
                if (flags and ALPHA_FLAG != 0) {
                    for (x in 0 until spriteWidth) {
                        for (y in 0 until spriteHeight) {
                            val alpha = (image.getRGB(x, y) shr 24) and 0xFF
                            os.write(alpha)
                        }
                    }
                }
            }
            for (i in 1 until palette.size) {
                val rgb = palette[i]
                os.write((rgb shr 16) and 0xFF)
                os.write((rgb shr 8) and 0xFF)
                os.write(rgb and 0xFF)
            }
            os.writeShort(spriteWidth)
            os.writeShort(spriteHeight)
            os.write(palette.size - 1)
            frames.indices.forEach { _ ->
                os.writeShort(0)
                os.writeShort(0)
                os.writeShort(spriteWidth)
                os.writeShort(spriteHeight)
            }
            os.writeShort(frames.size)
            return ByteBuffer.wrap(bout.toByteArray())
        }
    }
}
