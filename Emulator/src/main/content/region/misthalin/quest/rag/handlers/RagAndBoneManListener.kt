package content.region.misthalin.quest.rag.handlers

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.impl.PulseType
import core.game.system.task.Pulse
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery

class RagAndBoneManListener : InteractionListener {
    override fun defineListeners() {
        onUseWith(IntType.ITEM, Items.JUG_OF_VINEGAR_7810, Items.EMPTY_POT_1931) { player, _, _ ->
            if (removeItem(player, Items.JUG_OF_VINEGAR_7810) && (removeItem(player, Items.EMPTY_POT_1931))) {
                addItem(player, Items.POT_OF_VINEGAR_7811)
                addItem(player, Items.JUG_1935)
                sendMessage(player, "You pour the vinegar into the pot.")
            }
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, BoneBoiler.boneList, Items.POT_OF_VINEGAR_7811) { player, used, with ->
            if (removeItem(player, used) && (removeItem(player, with))) {
                addItem(player, BoneBoiler.forBone(used.id)!!.boneInVinegar)
                sendMessage(player, "You add the bone to the pot of vinegar.")
            }
            return@onUseWith true
        }

        onUseWith(
            IntType.SCENERY,
            intArrayOf(
                Items.LOGS_1511,
                Items.OAK_LOGS_1521,
                Items.WILLOW_LOGS_1519,
                Items.MAPLE_LOGS_1517,
                Items.YEW_LOGS_1515,
                Items.MAGIC_LOGS_1513,
            ),
            Scenery.POT_BOILER_14006,
        ) { player, used, _ ->
            if (removeItem(player, used)) {
                sendMessage(player, "You place the logs into the grate.")
                setVarbit(player, 2046, 1)
            }
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, BoneBoiler.boneInVinegarList, Scenery.POT_BOILER_14005) { player, used, _ ->
            val potOfVinegar = used.id
            if ((removeItem(player, potOfVinegar))) {
                setAttribute(player, ATTRIBUTE_ACTIVE_POT_OF_VINEGAR, potOfVinegar)
                sendMessage(player, "You place the pot on the pot boiler.")
                setVarbit(player, 2046, 2)
            }
            return@onUseWith true
        }

        on(Scenery.POT_BOILER_14007, SCENERY, "remove-pot") { player, _ ->
            val potOfVinegar = getAttribute(player, ATTRIBUTE_ACTIVE_POT_OF_VINEGAR, 0)
            if (BoneBoiler.forBoneInVinegar(potOfVinegar) != null) {
                setAttribute(player, ATTRIBUTE_ACTIVE_POT_OF_VINEGAR, 0)
                addItemOrDrop(player, potOfVinegar)
                sendMessage(player, "You remove the pot from the pot boiler.")
            }
            setVarbit(player, 2046, 1)
            return@on true
        }

        onUseWith(IntType.SCENERY, Items.TINDERBOX_590, Scenery.POT_BOILER_14007) { player, _, _ ->
            sendMessage(player, "You light the logs under the pot.")
            animate(player, Animations.HUMAN_LIGHT_FIRE_WITH_TINDERBOX_733)
            runTask(player, 3) {
                animate(player, -1, true)
                setVarbit(player, 2046, 3)

                player.pulseManager.run(
                    object : Pulse(20) {
                        override fun pulse(): Boolean {
                            setVarbit(player, 2046, 4)
                            return true
                        }
                    },
                    PulseType.CUSTOM_1,
                )
            }
            return@onUseWith true
        }

        on(Scenery.POT_BOILER_14009, SCENERY, "remove-bone") { player, _ ->
            val potOfVinegar = getAttribute(player, ATTRIBUTE_ACTIVE_POT_OF_VINEGAR, 0)
            if (BoneBoiler.forBoneInVinegar(potOfVinegar) != null) {
                val boneBoiler = BoneBoiler.forBoneInVinegar(potOfVinegar)!!
                setAttribute(player, ATTRIBUTE_ACTIVE_POT_OF_VINEGAR, 0)
                addItemOrDrop(player, boneBoiler.polishedBone)
                addItemOrDrop(player, Items.EMPTY_POT_1931)
                sendMessage(player, "You retrieve a polished " + boneBoiler.boneDescription + " from the pot.")
            }
            setVarbit(player, 2046, 0)
            return@on true
        }
    }

    companion object {
        const val ATTRIBUTE_ACTIVE_POT_OF_VINEGAR = "rag:potofvinegar"
    }
}
