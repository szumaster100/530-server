package content.global.handlers.iface.warning

import content.global.skill.agility.AgilityHandler
import core.api.*
import core.api.quest.hasRequirement
import core.game.dialogue.FaceAnim
import core.game.global.action.ClimbActionHandler
import core.game.global.action.ClimbActionHandler.climb
import core.game.global.action.DoorActionHandler
import core.game.interaction.InterfaceListener
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.map.RegionManager.getLocalNpcs
import core.game.world.update.flag.context.Animation
import org.rs.consts.*

class WarningInterface : InterfaceListener {
    override fun defineInterfaceListeners() {
        on(Components.CWS_DOOMSAYER_583) { player, _, _, buttonID, _, _ ->
            val warning = Warnings.values().find { it.buttonId == buttonID }
            warning?.let {
                WarningManager.toggle(player, warning.component)
            }
            return@on true
        }

        on(Components.CWS_WARNING_30_650) { player, componentID, _, buttonID, _, _ ->
            if (!hasRequirement(player, Quests.SUMMERS_END)) return@on true
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    if (getAttribute(player, "corp-beast-cave-delay", 0) <= GameWorld.ticks) {
                        teleport(player, player.location.transform(4, 0, 0))
                        setAttribute(player, "corp-beast-cave-delay", GameWorld.ticks + 5)
                    }
                    WarningManager.increment(player, componentID.id)
                }
            }
            return@on true
        }

        on(Components.CWS_WARNING_5_563) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    player.houseManager.toggleBuildingMode(player, true)
                }

                else -> closeInterface(player)
            }
            return@on true
        }

        on(Components.CWS_WARNING_9_560) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    runTask(player, 2) {
                        teleport(player, Location(2355, 9394, 0))
                        WarningManager.increment(player, componentID.id)
                    }
                }

                else -> closeInterface(player)
            }
            return@on true
        }

        on(Components.CWS_WARNING_17_570) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    if (!player.getSavedData().globalData.hasTiedLumbridgeRope()) {
                        sendDialogue(player, "There is a sheer drop below the hole. You will need a rope.")
                    } else {
                        ClimbActionHandler.climb(
                            player,
                            Animation(Animations.MULTI_BEND_OVER_827),
                            Location.create(3168, 9572, 0),
                        )
                    }
                    WarningManager.increment(player, componentID.id)
                }

                else -> closeInterface(player)
            }
            return@on true
        }

        on(Components.CWS_WARNING_3_568) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    teleport(player, Location.create(1752, 5237, 0))
                    playAudio(player, Sounds.ROOF_COLLAPSE_1384)
                    sendMessage(player, "You seem to have dropped down into a network of mole tunnels.")
                    if (!hasDiaryTaskComplete(player, DiaryType.FALADOR, 0, 5)) {
                        finishDiaryTask(player, DiaryType.FALADOR, 0, 5)
                    }
                }

                else -> closeInterface(player)
            }
            return@on true
        }

        on(Components.CWS_WARNING_20_580) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    val targetScenery =
                        if (player.location.x > 3443) {
                            getScenery(3444, 3458, 0)!!
                        } else {
                            getScenery(3443, 3458, 0)!!
                        }
                    DoorActionHandler.handleAutowalkDoor(player, targetScenery)
                    sendMessageWithDelay(player, "You walk into the gloomy atmosphere of Mort Myre.", 3)
                    WarningManager.increment(player, componentID.id)
                }

                else -> {
                    closeInterface(player)
                }
            }
            return@on true
        }

        on(Components.CWS_WARNING_23_564) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    climb(player, Animation(Animations.USE_LADDER_828), Location(2668, 3427, 2))
                    val npc = getLocalNpcs(Location.create(2668, 3427, 2))
                    var dir = ""
                    for (n in npc) {
                        if (n.id >= NPCs.TOWER_ADVISOR_684 && n.id <= NPCs.TOWER_ADVISOR_687) {
                            when (n.id) {
                                NPCs.TOWER_ADVISOR_684 -> dir = "north"
                                NPCs.TOWER_ADVISOR_685 -> dir = "east"
                                NPCs.TOWER_ADVISOR_686 -> dir = "south"
                                NPCs.TOWER_ADVISOR_687 -> dir = "west"
                            }
                            sendChat(n, "The $dir tower is occupied, get them!")
                        }
                    }
                    WarningManager.increment(player, componentID.id)
                }

                else -> closeInterface(player)
            }
            return@on true
        }

        on(Components.CWS_WARNING_10_565) { player, componentID, _, buttonID, _, _ ->
            when (buttonID) {
                20 -> WarningManager.toggle(player, componentID.id)
                17 -> {
                    closeInterface(player)
                    if (!removeItem(player, Item(Items.SHANTAY_PASS_1854, 1))) {
                        sendNPCDialogue(
                            player,
                            NPCs.SHANTAY_GUARD_838,
                            "You need a Shantay pass to get through this gate. See Shantay, he will sell you one for a very reasonable price.",
                            FaceAnim.NEUTRAL,
                        )
                    } else {
                        sendMessage(player, "You go through the gate.")
                        AgilityHandler.walk(
                            player,
                            0,
                            player.location,
                            player.location.transform(0, if (player.location.y > 3116) -2 else 2, 0),
                            null,
                            0.0,
                            null,
                        )
                    }
                }

                else -> {
                    closeInterface(player)
                    sendDialogue(
                        player,
                        "You decide that your visit to the desert can be postponed. Perhaps indefinitely.",
                    )
                }
            }
            return@on true
        }
    }
}
