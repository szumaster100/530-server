package content.region.kandarin.handlers

import core.api.sendDialogueLines
import core.api.sendMessage
import core.cache.def.impl.SceneryDefinition
import core.game.interaction.NodeUsageEvent
import core.game.interaction.OptionHandler
import core.game.interaction.UseWithHandler
import core.game.node.Node
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.map.Direction
import core.game.world.map.Location
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items

@Initializable
class BeehivePlugin : OptionHandler() {
    override fun handle(
        player: Player,
        node: Node,
        option: String,
    ): Boolean {
        if (!player.inventory.containsItem(REPELLANT)) {
            sendMessage(player, "The bees fly out of the hive and sting you!")
            player.impactHandler.manualHit(player, 2, ImpactHandler.HitsplatType.NORMAL, 1)
            Pulser.submit(
                object : Pulse(2, player) {
                    override fun pulse(): Boolean {
                        sendMessage(player, "Maybe you can clear them out somehow.")
                        return true
                    }
                },
            )
        } else {
            when (option) {
                "take-from" ->
                    if (!player.inventory.containsItem(BUCKET)) {
                        sendMessage(player, "You need a bucket to do that.")
                    } else {
                        player.inventory.remove(BUCKET)
                        player.inventory.add(BUCKET_OF_WAX)
                        sendMessage(player, "You fill your bucket with wax from the hive.")
                    }

                "take-honey" ->
                    if (!player.inventory.hasSpaceFor(HONEYCOMB)) {
                        sendMessage(player, "You don't have enough space in your inventory.")
                    } else {
                        player.inventory.add(HONEYCOMB)
                        sendMessage(player, "You take a chunk of honeycomb from the hive.")
                    }
            }
        }
        return true
    }

    override fun newInstance(arg: Any?): Plugin<Any> {
        SceneryDefinition.forId(68).handlers["option:take-from"] = this
        SceneryDefinition.forId(68).handlers["option:take-honey"] = this
        definePlugin(MerlinCrystalItemHandler())
        return this
    }

    override fun getDestination(
        mover: Node,
        node: Node,
    ): Location {
        val west = node.centerLocation.transform(Direction.WEST, 1)
        val east = node.centerLocation.transform(Direction.EAST, 1)
        return if (mover.location.getDistance(east) <= mover.location.getDistance(west)) {
            east
        } else {
            west
        }
    }

    private inner class MerlinCrystalItemHandler : UseWithHandler(REPELLANT.id, BUCKET.id) {
        private val OBJECTS = intArrayOf(68)

        override fun newInstance(arg: Any?): Plugin<Any> {
            for (id in OBJECTS) {
                addHandler(id, OBJECT_TYPE, this)
            }
            return this
        }

        override fun handle(event: NodeUsageEvent): Boolean {
            val player = event.player
            val useditem = event.usedItem
            val scenery = event.usedWith as Scenery

            if (useditem != null &&
                player.getAttribute(
                    "cleared-beehives",
                    false,
                ) &&
                useditem.id == REPELLANT.id &&
                scenery.id == 68
            ) {
                sendDialogueLines(
                    player,
                    "You have already cleared the hive of its bees. You can now safely collect wax from the hive.",
                )
            }

            if (useditem != null &&
                useditem.id == REPELLANT.id &&
                scenery.id == 68 &&
                !player.getAttribute(
                    "cleared-beehives",
                    false,
                )
            ) {
                sendDialogueLines(
                    player,
                    "You pour insect repellent on the beehive. You see the bees leaving the",
                    "hive.",
                )
                player.setAttribute("cleared-beehives", true)
            }

            if (useditem != null && useditem.id == BUCKET.id && player.getAttribute("cleared-beehives", false)) {
                player.dialogueInterpreter.sendDialogue("You get some wax from the beehive.")
                player.inventory.remove(Item(BUCKET.id, 1))
                player.inventory.add(Item(BUCKET_OF_WAX.id, 1))
            } else if (useditem != null && useditem.id == BUCKET.id && !player.getAttribute("cleared-beehives", false)
            ) {
                sendDialogueLines(
                    player,
                    "It would be dangerous to stick the bucket into the hive while the bees",
                    "are still in it. Perhaps you can clear them out somehow.",
                )
            }

            return true
        }
    }

    companion object {
        private val REPELLANT = Item(Items.INSECT_REPELLENT_28)
        private val BUCKET = Item(Items.BUCKET_1925)
        private val BUCKET_OF_WAX = Item(Items.BUCKET_OF_WAX_30)
        private val HONEYCOMB = Item(Items.HONEYCOMB_12156)
    }
}
