package content.region.fremennik.quest.horror.handlers

import core.api.*
import core.api.quest.getQuestStage
import core.api.quest.setQuestStage
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.interaction.QueueStrength
import org.rs.consts.*

class StrangeWallListener :
    InteractionListener,
    InterfaceListener {
    override fun defineListeners() {
        on(STRANGE_WALL, IntType.SCENERY, "study") { player, _ ->
            when (player.location.y) {
                4626 -> {
                    if (getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) >= 50) {
                        openInterface(player, Components.HORROR_METALDOOR_142)
                    } else {
                        openInterface(player, Components.HORROR_METALDOOR_142)
                        setAttribute(player, HorrorFromTheDeepUtils.STRANGE_WALL_DISCOVER, true)
                    }
                }

                10002 -> openInterface(player, Components.HORROR_METALDOOR_142)
                4627, 10003 -> sendMessage(player, "You cannot see anything unusual about the wall from this side.")
            }
            return@on true
        }

        onUseWith(IntType.SCENERY, STRANGE_W_REQ_ITEMS, *STRANGE_DOOR) { player, used, _ ->
            openDialogue(player, StrangeWallDialogue(items = used.id))
            return@onUseWith true
        }

        on(STRANGE_DOOR, IntType.SCENERY, "open") { player, node ->
            if (getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) >= 50) {
                queueScript(player, 1, QueueStrength.SOFT) { stage: Int ->
                    if (stage == 0) {
                        DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
                        playAudio(player, Sounds.STRANGEDOOR_OPEN_1626)
                        return@queueScript keepRunning(player)
                    }
                    if (stage == 1) {
                        playAudio(player, Sounds.STRANGEDOOR_CLOSE_1625)
                        setQuestStage(player, Quests.HORROR_FROM_THE_DEEP, 55)
                        removeAttribute(player, HorrorFromTheDeepUtils.STRANGE_WALL_DISCOVER)
                        return@queueScript stopExecuting(player)
                    }
                    return@queueScript stopExecuting(player)
                }
            } else {
                when (player.location.y) {
                    4626,
                    10002,
                    -> sendMessage(player, "You cannot see any way to move this part of the wall....")

                    4627,
                    10003,
                    -> sendMessage(player, "You cannot see anything unusual about the wall from this side.")
                }
            }
            return@on true
        }
    }

    override fun defineInterfaceListeners() {
        onOpen(Components.HORROR_METALDOOR_142) { player, _ ->
            setComponentVisibility(
                player,
                Components.HORROR_METALDOOR_142,
                2,
                getAttribute(player, HorrorFromTheDeepUtils.USE_FIRE_RUNE, 0) != 1,
            )
            setComponentVisibility(
                player,
                Components.HORROR_METALDOOR_142,
                3,
                getAttribute(player, HorrorFromTheDeepUtils.USE_AIR_RUNE, 0) != 1,
            )
            setComponentVisibility(
                player,
                Components.HORROR_METALDOOR_142,
                4,
                getAttribute(player, HorrorFromTheDeepUtils.USE_EARTH_RUNE, 0) != 1,
            )
            setComponentVisibility(
                player,
                Components.HORROR_METALDOOR_142,
                5,
                getAttribute(player, HorrorFromTheDeepUtils.USE_WATER_RUNE, 0) != 1,
            )
            setComponentVisibility(
                player,
                Components.HORROR_METALDOOR_142,
                6,
                getAttribute(player, HorrorFromTheDeepUtils.USE_ARROW, 0) != 1,
            )
            setComponentVisibility(
                player,
                Components.HORROR_METALDOOR_142,
                7,
                getAttribute(player, HorrorFromTheDeepUtils.USE_SWORD, 0) != 1,
            )

            if (getAttribute(player, HorrorFromTheDeepUtils.UNLOCK_DOOR, 0) > 5) {
                closeInterface(player)
                queueScript(player, 1, QueueStrength.SOFT) {
                    sendMessage(player, "You hear the sound of something moving within the wall.")
                    playAudio(player, Sounds.STRANGEDOOR_SOUND_1627)
                    setQuestStage(player, Quests.HORROR_FROM_THE_DEEP, 50)
                    return@queueScript stopExecuting(player)
                }
            }
            return@onOpen true
        }
    }

    companion object {
        private val STRANGE_WALL = intArrayOf(Scenery.STRANGE_WALL_4545, Scenery.STRANGE_WALL_4546)
        private val STRANGE_DOOR = intArrayOf(Scenery.STRANGE_WALL_4544, Scenery.STRANGE_WALL_4543)
        private val STRANGE_W_REQ_ITEMS =
            intArrayOf(
                Items.BRONZE_ARROW_882,
                Items.BRONZE_SWORD_1277,
                Items.AIR_RUNE_556,
                Items.FIRE_RUNE_554,
                Items.EARTH_RUNE_557,
                Items.WATER_RUNE_555,
            )
    }
}
