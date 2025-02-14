package content.global.skill.crafting.items.armour.snakeskin

import org.rs.consts.Components
import org.rs.consts.Items
import core.api.*
import core.api.skill.sendSkillDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class SnakeskinCraftingListener : InteractionListener {

    override fun defineListeners() {

        onUseWith(IntType.ITEM, Items.NEEDLE_1733, Items.SNAKESKIN_6289) { player, used, _ ->
            sendString(player, "Which snakeskin item would you like to make?", Components.SKILL_MAKE_306, 27)
            sendSkillDialogue(player) {
                withItems(
                    Items.SNAKESKIN_BODY_6322,
                    Items.SNAKESKIN_CHAPS_6324,
                    Items.SNAKESKIN_VBRACE_6330,
                    Items.SNAKESKIN_BANDANA_6326,
                    Items.SNAKESKIN_BOOTS_6328,
                )
                create { id, amount ->
                    val item = Snakeskin.forId(id)
                    item?.let {
                        submitIndividualPulse(
                            entity = player,
                            pulse = SnakeskinCraftingPulse(player, null, amount, it)
                        )
                    } ?: sendMessage(player, "Invalid snakeskin item selected.")
                }
                calculateMaxAmount {
                    amountInInventory(player, used.id)
                }
            }
            return@onUseWith true
        }
    }
}