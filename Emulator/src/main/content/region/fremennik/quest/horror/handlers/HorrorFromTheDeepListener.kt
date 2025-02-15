package content.region.fremennik.quest.horror.handlers

import content.region.fremennik.quest.horror.dialogue.JossikDialogueFile
import core.api.*
import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.api.quest.setQuestStage
import core.game.dialogue.FaceAnim
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.system.task.Pulse
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests
import org.rs.consts.Scenery

class HorrorFromTheDeepListener : InteractionListener {
    override fun defineListeners() {
        handleJossikInteraction()
        handleLighthouseDoorInteraction()
        handleLadderClimbInteraction()
        handleLighthouseMechanismFix()
    }

    private fun handleJossikInteraction() {
        on(NPCs.JOSSIK_1335, IntType.NPC, "talk-to") { player, _ ->
            openDialogue(player, JossikDialogueFile())
            return@on true
        }
    }

    private fun handleLighthouseDoorInteraction() {
        on(LIGHTHOUSE_DOORWAY, IntType.SCENERY, "walk-through") { player, node ->
            when {
                isQuestComplete(player, Quests.HORROR_FROM_THE_DEEP) -> {
                    DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
                }

                getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) >= 20 -> {
                    handleDoorUnlock(player, node)
                }

                getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) < 20 -> {
                    sendNPCDialogue(
                        player,
                        NPCs.LARRISSA_1336,
                        "Please adventurer... We are both curious as to what has happened in that lighthouse, but you need to fix the bridge for me!",
                    )
                }

                else -> {
                    sendDialogue(player, "You can't see any way to open the door.")
                }
            }
            return@on true
        }
    }

    private fun handleDoorUnlock(
        player: Player,
        node: Node,
    ) {
        submitIndividualPulse(
            player,
            object : Pulse(2) {
                override fun pulse(): Boolean {
                    if (inBorders(player, 2508, 3634, 2510, 3635)) {
                        sendMessage(player, "You unlock the Lighthouse front door.")
                        DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
                        runTask(player, 3) { teleport(player, location(2445, 4596, 0)) }
                    } else {
                        DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
                        runTask(player, 3) { teleport(player, location(2509, 3635, 0)) }
                    }
                    return true
                }
            },
        )
    }

    private fun handleLadderClimbInteraction() {
        on(Scenery.IRON_LADDER_4383, IntType.SCENERY, "climb") { player, _ ->
            animate(player, 827)
            queueScript(player, 1, QueueStrength.SOFT) {
                when {
                    isQuestComplete(player, Quests.HORROR_FROM_THE_DEEP) -> {
                        teleport(player, location(2519, 9994, 1))
                    }

                    getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) >= 40 -> {
                        teleport(player, location(2519, 4618, 1))
                    }

                    getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) < 40 -> {
                        sendPlayerDialogue(player, "I have no reason to go down there.", FaceAnim.HALF_THINKING)
                    }

                    else -> {
                        sendNPCDialogue(
                            player,
                            NPCs.LARRISSA_1336,
                            "Please adventurer, do not let curiosity get the better of you! We have to fix the lighthouse before any accidents happen!",
                        )
                    }
                }
                return@queueScript stopExecuting(player)
            }
            return@on true
        }
    }

    private fun handleLighthouseMechanismFix() {
        onUseWith(IntType.SCENERY, Items.SWAMP_TAR_1939, Scenery.LIGHTING_MECHANISM_4588) { player, _, _ ->
            if (removeItem(player, Items.SWAMP_TAR_1939)) {
                sendMessage(player, "You use the swamp tar to make the torch flammable again.")
                setAttribute(player, HorrorFromTheDeepUtils.LIGHTHOUSE_MECHANISM, 1)
            }
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, Items.TINDERBOX_590, Scenery.LIGHTING_MECHANISM_4588) { player, _, _ ->
            sendMessage(player, "You light the torch with your tinderbox.")
            player.incrementAttribute(HorrorFromTheDeepUtils.LIGHTHOUSE_MECHANISM)
            return@onUseWith true
        }

        onUseWith(
            IntType.SCENERY,
            Items.MOLTEN_GLASS_1775,
            Scenery.LIGHTING_MECHANISM_4588,
        ) { player, item, mechanism ->
            if (getAttribute(player, HorrorFromTheDeepUtils.LIGHTHOUSE_MECHANISM, 0) == 2 &&
                removeItem(
                    player,
                    item.asItem(),
                )
            ) {
                replaceScenery(mechanism.asScenery(), Scenery.LIGHTING_MECHANISM_4587, 80)
                setQuestStage(player, Quests.HORROR_FROM_THE_DEEP, 40)
                sendMessage(player, "You use the molten glass to repair the lens.")
                sendMessage(player, "You have managed to repair the lighthouse torch!")
            }
            return@onUseWith true
        }
    }

    companion object {
        private const val LIGHTHOUSE_DOORWAY = Scenery.DOORWAY_4577
    }
}
