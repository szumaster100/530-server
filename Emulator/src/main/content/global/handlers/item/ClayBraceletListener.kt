package content.global.handlers.item

import org.rs.consts.Items
import core.api.getCharge
import core.api.sendMessage
import core.api.setCharge
import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class ClayBraceletListener : InteractionListener {

    override fun defineListeners() {

        on(Items.BRACELET_OF_CLAY_11074, IntType.ITEM, "operate") { player, node ->
            var charge = getCharge(node)
            if (charge > 28) setCharge(node, 28).also { charge = 28 }
            sendMessage(player, "You have $charge uses left.")
            return@on true
        }
    }
}