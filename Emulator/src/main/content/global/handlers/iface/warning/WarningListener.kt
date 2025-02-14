package content.global.handlers.iface.warning

import org.rs.consts.*
import core.api.*
import core.api.quest.getQuestStage
import core.game.global.action.ClimbActionHandler.climb
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.map.RegionManager.getLocalNpcs
import core.game.world.update.flag.context.Animation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WarningListener : InteractionListener {

    override fun defineListeners() {

        on(NPCs.DOOMSAYER_3777, IntType.NPC, "toggle-warnings") { player, _ ->
            openInterface(player, Components.CWS_DOOMSAYER_583)
            return@on true
        }

        on(intArrayOf(Scenery.PASSAGE_37929, Scenery.PASSAGE_38811), IntType.SCENERY, "go-through") { player, node ->
            if (player.location.x < node.location.x) {
                if (WarningManager.check(Components.CWS_WARNING_30_650)) {
                    if (getAttribute(player, "corp-beast-cave-delay", 0) <= GameWorld.ticks) {
                        teleport(player, player.location.transform(4, 0, 0))
                        setAttribute(player, "corp-beast-cave-delay", GameWorld.ticks + 5)
                    }
                } else {
                    openInterface(player, Components.CWS_WARNING_30_650)
                }
            }
            return@on true
        }

        on(Scenery.STAIRS_25432, IntType.SCENERY, "climb-down") { player, _ ->
            if (getQuestStage(player, Quests.OBSERVATORY_QUEST) >= 8) {
                if (!WarningManager.check(Components.CWS_WARNING_9_560)) {
                    openInterface(player, Components.CWS_WARNING_9_560)
                } else {
                    runTask(player, 2) {
                        teleport(player, Location(2355, 9394, 0))
                    }
                }
            }
            return@on true
        }

        on(
            intArrayOf(Scenery.CLIMBING_ROPE_5946, Scenery.DARK_HOLE_5947),
            IntType.SCENERY,
            "climb-down",
            "climb"
        ) { player, _ ->
            val option = getUsedOption(player)
            when (option) {
                "climb" -> teleport(player, Location.create(3169, 3173, 0))
                "climb-down" -> {
                    if (!player.getSavedData().globalData.hasTiedLumbridgeRope()) {
                        sendDialogue(player, "There is a sheer drop below the hole. You will need a rope.")
                        return@on true
                    }
                    if (!WarningManager.check(Components.CWS_WARNING_17_570)) {
                        openInterface(player, Components.CWS_WARNING_17_570)
                        return@on true
                    }
                    climb(
                        player,
                        Animation(Animations.MULTI_BEND_OVER_827),
                        Location.create(3168, 9572, 0)
                    )
                }
            }
            return@on true
        }

        on(intArrayOf(Scenery.GATE_3506, Scenery.GATE_3507), IntType.SCENERY, "open") { player, node ->
            if (player.location.y == 3457) {
                DoorActionHandler.handleAutowalkDoor(player, node.asScenery())
                sendMessage(player, "You skip gladly out of murky Mort Myre.")
                GlobalScope.launch {
                    findLocalNPC(player, NPCs.ULIZIUS_1054)?.sendChat("Oh my! You're still alive!", 2)
                }
            } else {
                if (player.questRepository.hasStarted(Quests.NATURE_SPIRIT)) {
                    if (!WarningManager.check(Components.CWS_WARNING_20_580)) {
                        openInterface(player, Components.CWS_WARNING_20_580)
                    } else {
                        val targetScenery = if (player.location.x > 3443) {
                            getScenery(3444, 3458, 0)!!
                        } else {
                            getScenery(3443, 3458, 0)!!
                        }
                        DoorActionHandler.handleAutowalkDoor(player, targetScenery)
                        sendMessageWithDelay(player, "You walk into the gloomy atmosphere of Mort Myre.", 3)
                    }
                } else {
                    sendNPCDialogue(
                        player,
                        NPCs.ULIZIUS_1054,
                        "I'm sorry, but I'm afraid it's too dangerous to let you through this gate right now."
                    )
                }
            }
            return@on true
        }

        on(
            intArrayOf(Scenery.TOWER_LADDER_2511, Scenery.TOWER_LADDER_2512),
            IntType.SCENERY,
            "climb-up",
            "climb-down"
        ) { player, _ ->
            val option = getUsedOption(player)
            when (option) {
                "climb-up" -> {
                    if (!WarningManager.check(Components.CWS_WARNING_23_564)) {
                        openInterface(player, Components.CWS_WARNING_23_564)
                    } else {
                        climb(player, Animation(Animations.USE_LADDER_828), Location(2668, 3427, 2))
                        val npc = getLocalNpcs(Location.create(2668, 3427, 2))
                        var dir = ""
                        for (n in npc) if (n.id >= NPCs.TOWER_ADVISOR_684 && n.id <= NPCs.TOWER_ADVISOR_687) {
                            when (n.id) {
                                NPCs.TOWER_ADVISOR_684 -> dir = "north"
                                NPCs.TOWER_ADVISOR_685 -> dir = "east"
                                NPCs.TOWER_ADVISOR_686 -> dir = "south"
                                NPCs.TOWER_ADVISOR_687 -> dir = "west"
                            }
                            sendChat(n, "The $dir tower is occupied, get them!")
                        }
                    }
                }

                "climb-down" -> climb(player, null, Location.create(2668, 3427, 0))
            }
            return@on true
        }
    }
}