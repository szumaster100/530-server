package content.global.handlers.item.withobject

import org.rs.consts.Scenery
import core.api.sendDialogue
import core.cache.def.impl.ItemDefinition
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.api.EquipmentSlot

class HatStandListener : InteractionListener {

    val hats = ItemDefinition.getDefinitions().values
        .filter {
            it.getConfiguration("equipment_slot", 0) == EquipmentSlot.HEAD.ordinal
        }
        .map { it.id }
        .toIntArray()

    override fun defineListeners() {
        onUseWith(IntType.SCENERY, hats, Scenery.HAT_STAND_374) { player, _, _ ->
            sendDialogue(player, "It'd probably fall off if I tried to do that.")
            return@onUseWith true
        }
    }
}