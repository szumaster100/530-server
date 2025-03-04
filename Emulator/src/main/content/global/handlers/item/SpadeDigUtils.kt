package content.global.handlers.item

import core.game.node.entity.player.Player
import core.game.world.map.Location

object SpadeDigUtils {
    val listeners = HashMap<Location, (Player) -> Unit>()

    fun registerListener(
        location: Location,
        method: (Player) -> Unit,
    ) {
        listeners.putIfAbsent(location, method)
    }

    @JvmStatic
    fun runListener(
        location: Location,
        player: Player,
    ): Boolean {
        if (listeners.containsKey(location)) {
            listeners[location]?.invoke(player)
            return true
        }
        return false
    }
}
