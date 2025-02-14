package content.global.skill.construction.decoration.costume

import org.rs.consts.Scenery
import content.global.handlers.iface.DiangoReclaimInterface
import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class ToyBoxListener : InteractionListener {

    override fun defineListeners() {
        on(Scenery.TOY_BOX_18802, IntType.SCENERY, "open") { player, _ ->
            DiangoReclaimInterface.open(player)
            return@on true
        }
    }
}