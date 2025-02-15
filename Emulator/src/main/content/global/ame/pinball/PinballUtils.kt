package content.global.ame.pinball

import content.data.GameAttributes
import content.data.RandomEvent
import core.api.*
import core.api.ui.restoreTabs
import core.api.ui.setMinimapState
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.system.timer.impl.AntiMacro
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import org.rs.consts.NPCs
import org.rs.consts.Sounds

object PinballUtils {
    val PINBALL_EVENT_LOCATION = Location.create(1972, 5046, 0)
    val PINBALL_EVENT_ZONE_BORDERS = ZoneBorders(1961, 5033, 1982, 5054)
    val PINBALL_EVENT_WRONG_SCENERY_ID = intArrayOf(15001, 15003, 15005, 15007, 15009)
    val PINBALL_EVENT_SCENERY_ID = intArrayOf(15000, 15001, 15002, 15004, 15005, 15006, 15007, 15008, 15009)
    val PINBALL_EVENT_CAVE_EXIT_SCENERY_ID = 15010
    val PINBALL_EVENT_GUARD_NPC = intArrayOf(NPCs.FLIPPA_3912, NPCs.TILT_3913)
    val PINBALL_EVENT_MYSTERIOUS_OLD_MAN = NPC(NPCs.MYSTERIOUS_OLD_MAN_410, Location.create(1971, 5046, 0))

    fun exitRandomEventArea(player: Player) {
        player.properties.teleportLocation = getAttribute(player, RandomEvent.save(), null)
        clearLogoutListener(player, RandomEvent.logout())
        removeAttributes(
            player,
            RandomEvent.logout(),
            RandomEvent.save(),
            GameAttributes.RE_PINBALL_START,
            GameAttributes.RE_PINBALL_OBJ,
            GameAttributes.RE_PINBALL_INTER,
        )
        restoreTabs(player)
        closeOverlay(player)
        setMinimapState(player, 0)
        PINBALL_EVENT_MYSTERIOUS_OLD_MAN.clear()
        setVarbit(player, 2121, 0)
        AntiMacro.terminateEventNpc(player)
    }

    private val PILLAR_MAP =
        arrayOf(
            Scenery(15001, Location(1967, 5046, 0)),
            Scenery(15003, Location(1969, 5049, 0)),
            Scenery(15005, Location(1972, 5050, 0)),
            Scenery(15007, Location(1975, 5049, 0)),
            Scenery(15009, Location(1977, 5046, 0)),
        )

    fun generateTag(player: Player): Boolean {
        val score = getAttribute(player, GameAttributes.RE_PINBALL_OBJ, -1)
        if (score >= 10) return true
        for (i in 0..4) {
            if (getAttribute(player, GameAttributes.RE_PINBALL_OBJ, -1) == i) {
                replaceScenery(PILLAR_MAP[i], PILLAR_MAP[i].id - 1, -1)
                setAttribute(player, GameAttributes.RE_PINBALL_INTER, i)
                playAudio(player, Sounds.PILLARTAG_PINBALL_2278)
            }
        }
        return false
    }

    fun replaceTag(player: Player) {
        for (i in 0..4) {
            if (getAttribute(player, GameAttributes.RE_PINBALL_INTER, -1) == i) {
                when (i) {
                    0 ->
                        replaceScenery(
                            Scenery(
                                15000,
                                Location(1967, 5046, 0),
                            ),
                            15001,
                            -1,
                            Location(1967, 5046, 0),
                        )

                    1 ->
                        replaceScenery(
                            Scenery(
                                15002,
                                Location(1969, 5049, 0),
                            ),
                            15003,
                            -1,
                            Location(1969, 5049, 0),
                        )

                    2 ->
                        replaceScenery(
                            Scenery(
                                15004,
                                Location(1972, 5050, 0),
                            ),
                            15005,
                            -1,
                            Location(1972, 5050, 0),
                        )

                    3 ->
                        replaceScenery(
                            Scenery(
                                15006,
                                Location(1975, 5049, 0),
                            ),
                            15007,
                            -1,
                            Location(1975, 5049, 0),
                        )

                    4 ->
                        replaceScenery(
                            Scenery(
                                15008,
                                Location(1977, 5046, 0),
                            ),
                            15009,
                            -1,
                            Location(1977, 5046, 0),
                        )
                }
            }
        }
    }
}
