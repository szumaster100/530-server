package content.region.karamja.quest.mm.dialogue

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.player.link.TeleportManager
import core.game.system.task.Pulse
import core.game.world.map.Location
import core.tools.RandomFunction
import org.rs.consts.Components
import org.rs.consts.Items

class CrateMMDialogue(
    val it: Int,
) : DialogueFile() {
    private val monkeymadnessDungeon = Location.create(2804, 9168, 0)

    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        when (it) {
            0 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.MAMULET_MOULD_4020,
                            "The crate is full of ... monkey amulet moulds!",
                        ).also { stage++ }

                    1 ->
                        player!!
                            .dialogueInterpreter
                            .sendOptions(
                                "Do you wish to take one?",
                                "Yes",
                                "No",
                            ).also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.MAMULET_MOULD_4020)
                                end()
                            }

                            2 -> end()
                        }
                }

            1 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.THREAD_1734,
                            "The crate is full of ... crafting thread.",
                        ).also { stage++ }

                    1 -> sendDialogueOptions(player!!, "Do you wish to take one?", "Yes", "No").also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.THREAD_1734)
                                end()
                            }

                            2 -> end()
                        }
                }

            2 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.MONKEY_DENTURES_4006,
                            "The crate is full of ... magical monkey talking dentures!",
                        ).also { stage++ }

                    1 -> sendDialogueOptions(player!!, "Do you wish to take one?", "Yes", "No").also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.MONKEY_DENTURES_4006)
                                end()
                            }

                            2 -> end()
                        }
                }

            3 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.BANANA_1963,
                            "The crate is full of ... bananas.",
                        ).also { stage++ }
                    1 ->
                        player!!
                            .dialogueInterpreter
                            .sendOptions(
                                "Do you wish to take one?",
                                "Yes",
                                "No",
                            ).also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.BANANA_1963)
                                end()
                            }

                            2 -> end()
                        }
                }

            4 ->
                when (stage) {
                    0 ->
                        sendDialogue(
                            player!!,
                            "You find a hole in the floor under the crate! All you can see is the faint glimmer of light from extremely far below.",
                        ).also { stage++ }

                    1 ->
                        sendDialogueOptions(
                            player!!,
                            "Would you like to go down?",
                            "Yes, I'm sure.",
                            "No, not yet.",
                        ).also { stage++ }

                    2 ->
                        when (buttonID) {
                            1 ->
                                sendDialogue(player!!, "You begin to lower yourself into the hole...").also {
                                    stage =
                                        10
                                }
                            2 -> end()
                        }

                    10 -> {
                        end()
                        openInterface(player!!, 120)
                        player!!.pulseManager.run(
                            object : Pulse() {
                                var counter = 0

                                override fun pulse(): Boolean {
                                    when (counter++) {
                                        3 ->
                                            teleport(
                                                player!!,
                                                monkeymadnessDungeon,
                                                TeleportManager.TeleportType.INSTANT,
                                            )
                                    }
                                    return false
                                }
                            },
                        )
                        monkeyMadnessGoIntoBasementDamage()
                    }
                }

            5 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.TINDERBOX_590,
                            "The crate is full of ... tinderboxes.",
                        ).also { stage++ }

                    1 -> sendDialogueOptions(player!!, "Do you wish to take one?", "Yes", "No").also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.TINDERBOX_590)
                                end()
                            }

                            2 -> end()
                        }
                }

            6 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.EYE_OF_GNOME_4008,
                            "The crate is full of ... slimy gnome eyes!",
                        ).also { stage++ }

                    1 -> sendDialogueOptions(player!!, "Do you wish to take one?", "Yes", "No").also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.EYE_OF_GNOME_4008)
                                end()
                            }

                            2 -> end()
                        }
                }

            7 ->
                when (stage) {
                    0 ->
                        sendItemDialogue(
                            player!!,
                            Items.HAMMER_2347,
                            "The crate is full of ... hammers.",
                        ).also { stage++ }
                    1 -> sendDialogueOptions(player!!, "Do you wish to take one?", "Yes", "No").also { stage++ }
                    2 ->
                        when (buttonID) {
                            1 -> {
                                addItemOrDrop(player!!, Items.HAMMER_2347)
                                end()
                            }

                            2 -> end()
                        }
                }
        }
    }

    private fun monkeyMadnessGoIntoBasementDamage() {
        if (player!!.location.isInRegion(monkeymadnessDungeon.regionId)) {
            player!!.interfaceManager.close()
            openInterface(player!!, Components.FADE_FROM_BLACK_170)
            player!!.pulseManager.run(
                object : Pulse() {
                    var counter = 0

                    override fun pulse(): Boolean {
                        when (counter++) {
                            3 -> {
                                player!!.impactHandler.manualHit(
                                    player,
                                    RandomFunction.random(0, player!!.skills.lifepoints / 2),
                                    ImpactHandler.HitsplatType.NORMAL,
                                )
                            }
                        }
                        return false
                    }
                },
            )
        } else {
            monkeyMadnessGoIntoBasementDamage()
        }
    }
}
