package content.minigame.gnomecook

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.tools.RandomFunction
import org.rs.consts.Items

class GCRewardTokenListener : InteractionListener {

    private val gnomeItems = arrayOf(
        Items.FRUIT_BATTA_2277,
        Items.TOAD_BATTA_2255,
        Items.CHEESE_PLUSTOM_BATTA_2259,
        Items.WORM_BATTA_2253,
        Items.VEGETABLE_BATTA_2281,
        Items.CHOCOLATE_BOMB_2185,
        Items.VEG_BALL_2195,
        Items.TANGLED_TOADS_LEGS_2187,
        Items.WORM_HOLE_2191,
        Items.TOAD_CRUNCHIES_2217,
        Items.WORM_CRUNCHIES_2205,
        Items.CHOCCHIP_CRUNCHIES_2209,
        Items.SPICY_CRUNCHIES_2213,
    )

    override fun defineListeners() {
        /*
         * Handles check option on the GC Reward Token item.
         */

        on(Items.REWARD_TOKEN_9474, IntType.ITEM, "check") { player, _ ->
            val charges = player.getAttribute("$GC_BASE_ATTRIBUTE:$GC_REDEEMABLE_FOOD", 0)
            sendDialogueLines(player, "You have $charges redeemable charges.")
            return@on true
        }

        /*
         * Handles activate option on the GC Reward Token item.
         */

        on(Items.REWARD_TOKEN_9474, IntType.ITEM, "activate") { player, _ ->
            sendDialogueOptions(player, "How many charges?", "1", "5", "10")
            addDialogueAction(player) { player, button ->
                when (button) {
                    2 -> sendCharges(1, player)
                    3 -> sendCharges(5, player)
                    4 -> sendCharges(10, player)

                }
            }
            return@on true
        }
    }

    fun sendCharges(amount: Int, player: Player) {
        val playerCharges = getAttribute(player, "$GC_BASE_ATTRIBUTE:$GC_REDEEMABLE_FOOD", 0)
        if (playerCharges < amount) {
            sendDialogue(player, "You don't have that many charges.")
            return
        }

        if (freeSlots(player) < amount) {
            sendDialogue(player, "You don't have enough space in your inventory.")
            return
        }

        val itemList = ArrayList<Item>()
        for (charge in 0 until amount) {
            itemList.add(Item(gnomeItems.random()))
        }

        sendDialogue(player, "You put in for delivery of $amount items. Wait a bit...")
        GameWorld.Pulser.submit(DeliveryPulse(player, itemList))
        setAttribute(player, "/save:$GC_BASE_ATTRIBUTE:$GC_REDEEMABLE_FOOD", playerCharges - amount)
    }

    class DeliveryPulse(val player: Player, val items: ArrayList<Item>) : Pulse(RandomFunction.random(15, 30)) {
        override fun pulse(): Boolean {
            player.inventory.add(*items.toTypedArray())
            sendDialogue(player, "Your food delivery has arrived!")
            return true
        }
    }
}
