package content.global.handlers.scenery

import core.api.*
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import org.rs.consts.Sounds

class MillingListener : InteractionListener {
    companion object {
        private const val GRAIN = Items.GRAIN_1947
        private const val SWEETCORN = Items.SWEETCORN_5986
        private const val EMPTY_POT = Items.EMPTY_POT_1931
        private const val POT_OF_FLOUR = Items.POT_OF_FLOUR_1933
        private const val POT_OF_CORNFLOUR = Items.POT_OF_CORNFLOUR_7468

        private val HOPPERS =
            intArrayOf(
                Scenery.HOPPER_36881,
                Scenery.HOPPER_2717,
                Scenery.HOPPER_24071,
                Scenery.HOPPER_2716,
                Scenery.HOPPER_22422,
            )
        private val HOPPER_CONTROLS =
            intArrayOf(
                Scenery.HOPPER_CONTROLS_2718,
                Scenery.HOPPER_CONTROLS_2721,
                Scenery.HOPPER_CONTROLS_24072,
                Scenery.HOPPER_CONTROLS_2720,
                Scenery.LEVER_22424,
            )
        private val FLOUR_BINS =
            intArrayOf(
                Scenery.FLOUR_BIN_1782,
                Scenery.FLOUR_BIN_5792,
                Scenery.FLOUR_BIN_22420,
                Scenery.FLOUR_BIN_22421,
                Scenery.FLOUR_BIN_24070,
                Scenery.FLOUR_BIN_36878,
            )

        private const val VARP = 695
    }

    override fun defineListeners() {
        on(HOPPER_CONTROLS, IntType.SCENERY, "operate", "pull") { player, _ ->
            useHopperControl(player)
            return@on true
        }

        on(FLOUR_BINS, IntType.SCENERY, "empty") { player, _ ->
            fillPot(player)
            return@on true
        }

        onUseWith(SCENERY, intArrayOf(GRAIN, SWEETCORN), *HOPPERS) { player, used, _ ->
            fillHopper(player, used.asItem())
            return@onUseWith true
        }

        onUseWith(SCENERY, EMPTY_POT, *FLOUR_BINS) { player, _, _ ->
            fillPot(player)
            return@onUseWith true
        }
    }

    private fun useHopperControl(player: Player) {
        val hopperContents = getAttribute(player, "milling:hopper", 0)
        if (hopperContents == 0) {
            sendMessage(player, "You operate the empty hopper. Nothing interesting happens.")
            return
        }
        setAttribute(player, "/save:milling:hopper", 0)

        when (hopperContents) {
            GRAIN -> {
                animate(player, Animations.HUMAN_OPERATE_CONTROLS_3571)
                setAttribute(
                    player,
                    "/save:milling:grain",
                    (getAttribute(player, "milling:grain", 0) + 1).coerceAtMost(
                        30 -
                            getAttribute(
                                player,
                                "milling:sweetcorn",
                                0,
                            ),
                    ),
                )
                sendMessage(player, "You operate the hopper. The grain slides down the chute.")
            }

            SWEETCORN -> {
                animate(player, Animations.HUMAN_OPERATE_CONTROLS_3571)
                setAttribute(
                    player,
                    "/save:milling:sweetcorn",
                    (getAttribute(player, "milling:sweetcorn", 0) + 1).coerceAtMost(
                        30 -
                            getAttribute(
                                player,
                                "milling:grain",
                                0,
                            ),
                    ),
                )
                sendMessage(player, "You operate the hopper. The sweetcorn slides down the chute.")
            }
        }

        playAudio(player, Sounds.HOPPERLEVER_2575, 1)
        playAudio(player, Sounds.GIANT_ROC_APPROACHES_3189)
        setVarp(player, VARP, 1, true)
    }

    private fun fillHopper(
        player: Player,
        used: Item,
    ) {
        playAudio(player, Sounds.FILL_GRINDER_1133)
        animate(player, Animations.PUT_OBJECT_ON_TABLE_537)
        if (getAttribute(player, "milling:hopper", 0) == 0 && removeItem(player, used)) {
            setAttribute(player, "/save:milling:hopper", used.id)
            sendMessage(player, "You put the " + used.name.lowercase() + " in the hopper.")
            return
        }
        sendMessage(
            player,
            "There is already " +
                getItemName(
                    getAttribute(player, "milling:hopper", 0),
                ).lowercase() + " in the hopper.",
        )
    }

    private fun fillPot(player: Player) {
        if (!inInventory(player, EMPTY_POT)) {
            sendMessage(player, "I need an empty pot to hold the flour in.")
            return
        }
        if (removeItem(player, EMPTY_POT)) {
            if (getAttribute(player, "milling:sweetcorn", 0) > 0) {
                setAttribute(player, "/save:milling:sweetcorn", (getAttribute(player, "milling:sweetcorn", 0) - 1))
                addItem(player, POT_OF_CORNFLOUR)
                sendMessage(
                    player,
                    if (player.getAttribute(
                            "milling:sweetcorn",
                            0,
                        ) > 0
                    ) {
                        "You fill a pot with cornflour from the bin."
                    } else {
                        "You fill a pot with the last of the cornflour in the bin."
                    },
                )
            } else if (getAttribute(player, "milling:grain", 0) > 0) {
                setAttribute(player, "/save:milling:grain", (getAttribute(player, "milling:grain", 0) - 1))
                addItem(player, POT_OF_FLOUR)
                sendMessage(
                    player,
                    if (player.getAttribute(
                            "milling:grain",
                            0,
                        ) > 0
                    ) {
                        "You fill a pot with flour from the bin."
                    } else {
                        "You fill a pot with the last of the flour in the bin."
                    },
                )
            }

            if (getAttribute(player, "milling:sweetcorn", 0) + getAttribute(player, "milling:grain", 0) <= 0) {
                setVarp(player, VARP, 0, true)
            }
        }
    }
}
