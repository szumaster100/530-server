package core.game.world.update

import core.game.node.entity.player.Player
import core.game.world.map.RegionChunk
import core.net.packet.PacketRepository
import core.net.packet.context.ClearChunkContext
import core.net.packet.out.ClearRegionChunk

object MapChunkRenderer {
    @JvmStatic
    fun render(player: Player) {
        val v = player.viewport
        val last = player.playerFlags.lastViewport
        val updated: MutableList<RegionChunk> = ArrayList()
        val current = v.chunks
        var sizeX = last.size
        for (x in 0 until sizeX) {
            val sizeY: Int = last[x].size
            for (y in 0 until sizeY) {
                val previous = last[x][y] ?: continue
                if (!containsChunk(current, previous)) {
                    PacketRepository.send(
                        ClearRegionChunk::class.java,
                        ClearChunkContext(player, previous),
                    )
                } else {
                    updated.add(previous)
                }
            }
        }
        sizeX = current.size
        for (x in 0 until sizeX) {
            val sizeY: Int = current[x].size
            for (y in 0 until sizeY) {
                val chunk = current[x][y]
                if (!updated.contains(chunk)) {
                    chunk.synchronize(player)
                } else {
                    chunk.update(player)
                }
                last[x][y] = current[x][y]
            }
        }
    }

    private fun containsChunk(
        list: Array<Array<RegionChunk>>,
        c: RegionChunk,
    ): Boolean {
        val sizeList = list.size
        for (x in 0 until sizeList) {
            val chunkSize: Int = list[x].size
            for (y in 0 until chunkSize) {
                if (list[x][y] === c) {
                    return true
                }
            }
        }
        return false
    }
}
