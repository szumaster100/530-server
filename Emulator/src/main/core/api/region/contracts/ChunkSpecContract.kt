package core.api.region.contracts

import core.game.world.map.build.DynamicRegion

interface ChunkSpecContract {
    fun populateChunks(dyn: DynamicRegion)
}
