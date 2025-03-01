package content.region.fremennik.quest.horror.handlers

import core.api.*
import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.api.quest.setQuestStage
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.impl.ForceMovement
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.map.Direction
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Quests
import org.rs.consts.Scenery

class LighthouseBridgeListener : InteractionListener {
    override fun defineListeners() {
        onUseWith(IntType.SCENERY, Items.PLANK_960, *BROKEN_BRIDGE) { player, _, bridge ->
            if (getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) in
                if (bridge.id == BROKEN_BRIDGE_1) 5..10 else 10..20
            ) {
                sendDialogue(player, "That won't help fix the bridge.")
                return@onUseWith false
            }
            if (bridge.id == BROKEN_BRIDGE_1 && getAttribute(player, HorrorFromTheDeepUtils.UNLOCK_BRIDGE, 0) == 1) {
                sendDialogue(player, "You have already fixed this half of the bridge.")
                return@onUseWith false
            }
            if (bridge.id == BROKEN_BRIDGE_2 && getAttribute(player, HorrorFromTheDeepUtils.UNLOCK_BRIDGE, 0) < 1) {
                sendMessage(player, "I might be able to make to the other side.")
                return@onUseWith false
            }
            if (!inInventory(player, Items.HAMMER_2347, 1) &&
                inInventory(player, Items.PLANK_960, 1) &&
                inInventory(
                    player,
                    Items.STEEL_NAILS_1539,
                )
            ) {
                sendDialogue(player, "You need a hammer to force the nails in with.")
                return@onUseWith false
            }
            if (amountInInventory(player, Items.STEEL_NAILS_1539) < 30) {
                sendDialogue(player, "You need 30 steel nails to attach the plank with.")
                return@onUseWith false
            }

            if (removeItem(player, Item(Items.STEEL_NAILS_1539, 30)) && removeItem(player, Item(Items.PLANK_960, 1))) {
                player.lock()
                queueScript(player, 1, QueueStrength.SOFT) { stage: Int ->
                    when (stage) {
                        0 -> {
                            animate(player, Animations.SMITH_HAMMER_898)
                            return@queueScript delayScript(player, animationCycles(Animations.SMITH_HAMMER_898))
                        }

                        1 -> {
                            setAttribute(
                                player,
                                HorrorFromTheDeepUtils.UNLOCK_BRIDGE,
                                if (bridge.id == BROKEN_BRIDGE_1) 1 else 2,
                            )
                            setQuestStage(
                                player,
                                Quests.HORROR_FROM_THE_DEEP,
                                if (bridge.id == BROKEN_BRIDGE_1) 10 else 20,
                            )
                            sendDialogue(
                                player,
                                if (bridge.id ==
                                    BROKEN_BRIDGE_1
                                ) {
                                    "You create half a makeshift walkway out of the plank."
                                } else {
                                    "You have now made a makeshift walkway over the bridge."
                                },
                            )
                            sendMessage(
                                player,
                                if (bridge.id ==
                                    BROKEN_BRIDGE_1
                                ) {
                                    "You create half a makeshift walkway out of the plank."
                                } else {
                                    "You have now made a makeshift walkway over the bridge."
                                },
                            )
                            player.unlock()
                            return@queueScript stopExecuting(player)
                        }

                        else -> return@queueScript stopExecuting(player)
                    }
                }
            }
            return@onUseWith true
        }

        on(BROKEN_BRIDGE_1, IntType.SCENERY, "Cross") { player, _ ->
            if (getAttribute(player, HorrorFromTheDeepUtils.UNLOCK_BRIDGE, 0) == 1) {
                lock(player, 6)
                submitIndividualPulse(
                    player,
                    object : Pulse() {
                        var count = 0

                        override fun pulse(): Boolean {
                            when (count++) {
                                0 ->
                                    forceMove(
                                        player,
                                        Location(2595, 3608, 0),
                                        Location(2596, 3608, 0),
                                        0,
                                        60,
                                        Direction.EAST,
                                    )

                                2 -> animate(player, Animations.JUMP_BRIDGE_769)
                                3 -> teleport(player, Location(2598, 3608, 0))
                                5 -> {
                                    forceWalk(player, Location(2599, 3608, 0), "smart")
                                    return true
                                }
                            }
                            return false
                        }
                    },
                )
            } else if (getAttribute(player, HorrorFromTheDeepUtils.UNLOCK_BRIDGE, 0) == 2 ||
                isQuestComplete(
                    player,
                    Quests.HORROR_FROM_THE_DEEP,
                )
            ) {
                lock(player, 4)
                submitIndividualPulse(
                    player,
                    object : Pulse() {
                        var count = 0

                        override fun pulse(): Boolean {
                            when (count++) {
                                0 ->
                                    ForceMovement.run(
                                        player,
                                        Location(2596, 3608, 0),
                                        Location(2597, 3608, 0),
                                        Animation(753),
                                        Animation(757),
                                        Direction.EAST,
                                    )

                                1 ->
                                    ForceMovement.run(
                                        player,
                                        Location(2597, 3608, 0),
                                        Location(2598, 3608, 0),
                                        Animation(756),
                                        Animation(756),
                                        Direction.EAST,
                                    )

                                3 ->
                                    ForceMovement
                                        .run(
                                            player,
                                            Location(2598, 3608, 0),
                                            Location(2599, 3608, 0),
                                            Animation(757),
                                            Animation(759),
                                            Direction.EAST,
                                        ).also { return true }
                            }
                            return false
                        }
                    },
                )
            } else {
                sendMessage(player, "I might be able to make to the other side.")
            }
            return@on true
        }

        on(BROKEN_BRIDGE_2, IntType.SCENERY, "Cross") { player, _ ->
            if (getAttribute(player, HorrorFromTheDeepUtils.UNLOCK_BRIDGE, 0) == 2 ||
                isQuestComplete(
                    player,
                    Quests.HORROR_FROM_THE_DEEP,
                )
            ) {
                lock(player, 4)
                submitIndividualPulse(
                    player,
                    object : Pulse() {
                        var count = 0

                        override fun pulse(): Boolean {
                            when (count++) {
                                0 ->
                                    ForceMovement.run(
                                        player,
                                        Location(2598, 3608, 0),
                                        Location(2597, 3608, 0),
                                        Animation(752),
                                        Animation(755),
                                        Direction.WEST,
                                    )

                                1 ->
                                    ForceMovement.run(
                                        player,
                                        Location(2597, 3608, 0),
                                        Location(2596, 3608, 0),
                                        Animation(754),
                                        Animation(754),
                                        Direction.WEST,
                                    )

                                3 ->
                                    ForceMovement
                                        .run(
                                            player,
                                            Location(2596, 3608, 0),
                                            Location(2595, 3608, 0),
                                            Animation(755),
                                            Animation(758),
                                            Direction.WEST,
                                        ).also { return true }
                            }
                            return false
                        }
                    },
                )
            } else {
                sendMessage(player, "I might be able to make to the other side.")
            }
            return@on true
        }
    }

    companion object {
        private const val BROKEN_BRIDGE_1 = Scenery.BROKEN_BRIDGE_4615
        private const val BROKEN_BRIDGE_2 = Scenery.BROKEN_BRIDGE_4616
        private val BROKEN_BRIDGE = intArrayOf(Scenery.BROKEN_BRIDGE_4615, Scenery.BROKEN_BRIDGE_4616)
    }
}
