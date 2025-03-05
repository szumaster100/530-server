package content.data

import core.ServerConstants
import core.game.world.map.Location

enum class RespawnPoint(
    val location: Location,
) {
    LUMBRIDGE(ServerConstants.HOME_LOCATION!!.location),
    FALADOR(Location(2972, 3337, 0)),
    CAMELOT(Location(2757, 3477, 0)),
}
