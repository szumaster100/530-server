package content.region.asgarnia.handlers

import core.api.*
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.map.Location
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import org.rs.consts.Sounds

class FaladorListener : InteractionListener {
    override fun defineListeners() {
        on(POSTER, IntType.SCENERY, "look-at") { player, _ ->
            sendDialogue(player, "Looks like a generic wanted poster.")
            return@on true
        }

        on(DOORS, IntType.SCENERY, "close") { player, node ->
            DoorActionHandler.handleDoor(player, node.asScenery())
            return@on true
        }

        on(CUPBOARD_CLOSED, IntType.SCENERY, "open") { player, node ->
            face(player, node)
            animate(player, Animations.OPEN_WARDROBE_542)
            playAudio(player, Sounds.CUPBOARD_OPEN_58)
            replaceScenery(node.asScenery(), CUPBOARD_OPEN, -1)
            return@on true
        }

        on(CUPBOARD_OPEN, IntType.SCENERY, "search", "shut") { player, node ->
            when (getUsedOption(player)) {
                "shut" -> {
                    face(player, node)
                    animate(player, Animations.CLOSE_CUPBOARD_543)
                    playAudio(player, Sounds.CUPBOARD_CLOSE_57)
                    replaceScenery(node.asScenery(), CUPBOARD_CLOSED, -1)
                    return@on true
                }

                "search" -> {
                    if (!inInventory(player, QUEST_ITEM)) {
                        sendItemDialogue(player, QUEST_ITEM, "You find a small portrait in here which you take.")
                        addItem(player, QUEST_ITEM)
                    } else {
                        sendDialogue(player, "There is just a load of junk in here.")
                    }
                }
            }
            return@on true
        }

        on(CASTLE_STAIRS, IntType.SCENERY, "climb-up", "climb-down") { player, node ->
            when (node.id) {
                11729 ->
                    if (getUsedOption(player) == "climb-up") {
                        when (player.location.z) {
                            0 -> teleport(player, Location(2956, 3338, 1))
                            1 -> teleport(player, Location(2959, 3339, 2))
                            2 -> teleport(player, Location(2959, 3338, 3))
                        }
                    }

                11731 ->
                    if (getUsedOption(player) == "climb-down") {
                        when (player.location.z) {
                            3 -> teleport(player, Location(2959, 3338, 2))
                            2 -> teleport(player, Location(2959, 3339, 1))
                            1 -> teleport(player, Location(2956, 3338, 0))
                        }
                    }
            }
            return@on true
        }
    }

    companion object {
        private const val CUPBOARD_CLOSED = Scenery.CUPBOARD_2271
        private const val CUPBOARD_OPEN = Scenery.CUPBOARD_2272
        private const val DOORS = Scenery.DOOR_11708
        private const val POSTER = Scenery.POSTER_40992
        private const val QUEST_ITEM = Items.PORTRAIT_666
        val CASTLE_STAIRS = intArrayOf(Scenery.STAIRCASE_11729, Scenery.STAIRCASE_11731)
    }
}
