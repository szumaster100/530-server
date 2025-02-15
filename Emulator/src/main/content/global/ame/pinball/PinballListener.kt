package content.global.ame.pinball

import content.data.GameAttributes
import core.api.*
import core.api.MapArea
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.map.Direction
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneRestriction
import core.tools.BLUE
import org.rs.consts.Animations
import org.rs.consts.Components

class PinballListener :
    InteractionListener,
    MapArea {
    init {
        PinballUtils.PINBALL_EVENT_MYSTERIOUS_OLD_MAN.init()
        PinballUtils.PINBALL_EVENT_MYSTERIOUS_OLD_MAN.isWalks = false
        PinballUtils.PINBALL_EVENT_MYSTERIOUS_OLD_MAN.isInvisible = false
        PinballUtils.PINBALL_EVENT_MYSTERIOUS_OLD_MAN.direction = Direction.EAST
    }

    override fun defineListeners() {
        on(PinballUtils.PINBALL_EVENT_SCENERY_ID, IntType.SCENERY, "tag") { player, node ->
            val score = getVarbit(player, 2121)
            if (score > 9) {
                sendUnclosablePlainDialogue(player, true, "", "Congratulations - you can now leave the arena.")
                sendString(player, "Score: $score", Components.PINBALL_INTERFACE_263, 1)
                return@on true
            }

            if (node.id in PinballUtils.PINBALL_EVENT_WRONG_SCENERY_ID.indices) {
                setVarbit(player, score, 0)
                sendString(player, "Score: $score", Components.PINBALL_INTERFACE_263, 1)
                sendUnclosablePlainDialogue(
                    player,
                    true,
                    "",
                    "Wrong post! Your score has been reset.",
                    "Tag the post with the " + BLUE + "flashing rings" + ".",
                )
            } else {
                setVarbit(player, score, score + 1)
                sendString(player, "Score: $score", Components.PINBALL_INTERFACE_263, 1)
                sendUnclosablePlainDialogue(player, true, "", "Well done! Now tag the next post.")
            }

            lock(player, 3)
            val random = (0..4).random()
            animate(player, Animations.HUMAN_MULTI_USE_832)

            if (score < 10) {
                setAttribute(player, GameAttributes.RE_PINBALL_OBJ, random)
                PinballUtils.replaceTag(player)
                PinballUtils.generateTag(player)
            } else {
                PinballUtils.replaceTag(player)
                sendUnclosablePlainDialogue(player, true, "", "Congratulations - you can now leave the arena.")
            }
            return@on true
        }

        on(PinballUtils.PINBALL_EVENT_CAVE_EXIT_SCENERY_ID, IntType.SCENERY, "exit") { player, _ ->
            PinballUtils.exitRandomEventArea(player)
            return@on true
        }

        on(PinballUtils.PINBALL_EVENT_GUARD_NPC, IntType.NPC, "Talk-to") { player, npc ->
            openDialogue(player, PinballGuardDialogue(), npc)
            return@on true
        }
    }

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(PinballUtils.PINBALL_EVENT_ZONE_BORDERS)
    }

    override fun getRestrictions(): Array<ZoneRestriction> {
        return arrayOf(
            ZoneRestriction.CANNON,
            ZoneRestriction.TELEPORT,
            ZoneRestriction.FOLLOWERS,
            ZoneRestriction.FIRES,
            ZoneRestriction.RANDOM_EVENTS,
        )
    }
}
