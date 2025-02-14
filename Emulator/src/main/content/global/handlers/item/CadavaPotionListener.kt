package content.global.handlers.item

import org.rs.consts.Items
import core.api.getUsedOption
import core.api.sendItemDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class CadavaPotionListener : InteractionListener {

    override fun defineListeners() {

        on(Items.CADAVA_POTION_756, IntType.ITEM, "drink", "look-at") { player, node ->
            val option = getUsedOption(player)
            sendItemDialogue(
                player,
                node.id,
                if (option == "drink") "You dare not drink." else "This looks very colourful."
            )
            return@on true
        }
    }
}