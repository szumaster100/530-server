package content.minigame.templetrekking.events.combat

import org.rs.consts.Scenery
import core.api.getUsedOption
import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class TentacleCombatActivityListener : InteractionListener {

    override fun defineListeners() {
        on(Scenery.BOAT_13864, IntType.SCENERY, "escape-event", "continue-trek") { player, _ ->
            when (getUsedOption(player)) {
                "escape-event" -> TentacleCombatActivity().areaLeave(player, false)
            }
            return@on true
        }
    }
}