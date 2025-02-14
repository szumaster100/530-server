package content.minigame.allfiredup

import org.rs.consts.Components
import core.game.interaction.InterfaceListener

class BeaconMapInterface : InterfaceListener {

    companion object {
        const val BEACON_MAP_575 = Components.BEACON_MAP_575
    }

    override fun defineInterfaceListeners() {
        on(BEACON_MAP_575) { _, _, _, _, _, _ ->
            return@on true
        }
    }
}