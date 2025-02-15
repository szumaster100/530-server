package content.global.skill.construction.decoration.chapel

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.skill.Skills
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import org.rs.consts.Sounds

class BlessSpiritShieldListener : InteractionListener {
    override fun defineListeners() {
        onUseWith(IntType.SCENERY, ingredientIDs, *sceneryIDs) { player, used, with ->
            if (player.ironmanManager.isIronman && !player.houseManager.isInHouse(player)) {
                sendMessage(player, "You cannot do this on someone else's altar.")
                return@onUseWith false
            }
            if (getStatLevel(player, Skills.PRAYER) < 85) {
                sendMessage(player, "You need 85 prayer to do this.")
                return@onUseWith false
            }

            animate(player, Animations.HUMAN_COOKING_RANGE_896)
            playAudio(player, Sounds.POH_OFFER_BONES_958)
            if (removeItem(player, used.asItem()) && removeItem(player, with.asItem())) {
                addItem(player, Items.BLESSED_SPIRIT_SHIELD_13736)
            }
            return@onUseWith true
        }
    }

    companion object {
        val ingredientIDs = intArrayOf(Items.SPIRIT_SHIELD_13734, Items.HOLY_ELIXIR_13754)
        val sceneryIDs =
            intArrayOf(
                Scenery.ALTAR_13185,
                Scenery.ALTAR_13188,
                Scenery.ALTAR_13191,
                Scenery.ALTAR_13194,
                Scenery.ALTAR_13197,
            )
    }
}
