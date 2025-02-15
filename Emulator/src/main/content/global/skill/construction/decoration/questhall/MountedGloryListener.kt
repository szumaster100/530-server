package content.global.skill.construction.decoration.questhall

import core.api.*
import core.api.ui.closeDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.link.TeleportManager
import core.game.world.map.Location
import org.rs.consts.Scenery

class MountedGloryListener : InteractionListener {
    override fun defineListeners() {
        on(Scenery.AMULET_OF_GLORY_13523, IntType.SCENERY, "rub", "remove") { player, node ->
            val option = getUsedOption(player)
            when (option) {
                "rub" -> {
                    setTitle(player, 5)
                    sendDialogueOptions(
                        player,
                        "Where would you like to teleport to?",
                        "Edgeville",
                        "Karamja",
                        "Draynor Village",
                        "Al Kharid",
                        "Nowhere",
                    )
                    addDialogueAction(player) { player, button ->
                        when (button) {
                            2 ->
                                teleport(
                                    player,
                                    Location.create(3087, 3495, 0),
                                    TeleportManager.TeleportType.RANDOM_EVENT_OLD,
                                )

                            3 ->
                                teleport(
                                    player,
                                    Location.create(2919, 3175, 0),
                                    TeleportManager.TeleportType.RANDOM_EVENT_OLD,
                                )

                            4 ->
                                teleport(
                                    player,
                                    Location.create(3081, 3250, 0),
                                    TeleportManager.TeleportType.RANDOM_EVENT_OLD,
                                )

                            5 ->
                                teleport(
                                    player,
                                    Location.create(3304, 3124, 0),
                                    TeleportManager.TeleportType.RANDOM_EVENT_OLD,
                                )

                            6 -> closeDialogue(player)
                        }
                    }
                }

                else ->
                    if (!player.houseManager.isBuildingMode) {
                        sendMessage(player, "You have to be in building mode to do this.")
                    } else {
                        openDialogue(player, "con:removedec", node.asScenery())
                    }
            }
            return@on true
        }
    }
}
