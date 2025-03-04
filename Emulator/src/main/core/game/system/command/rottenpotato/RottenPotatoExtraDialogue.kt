package core.game.system.command.rottenpotato

import core.api.sendInputDialogue
import core.game.dialogue.Dialogue
import core.game.dialogue.InputType
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.world.map.RegionManager
import core.game.world.repository.Repository
import core.plugin.Initializable
import core.tools.colorize

@Initializable
class RottenPotatoExtraDialogue(
    player: Player? = null,
) : Dialogue(player) {
    val ID = 38575794
    val AMEs = arrayOf("chicken", "Sandwich Lady", "tree spirit", "rick turpentine", "Genie")
    val BossIDs = arrayOf(50, 8350, 8133, 2745)
    val BossNames = arrayOf("KBD", "Tormented Demon", "Corporeal Beast", "Jad")

    override fun newInstance(player: Player?): Dialogue {
        return RottenPotatoExtraDialogue(player)
    }

    override fun open(vararg args: Any?): Boolean {
        options("Send Player Notification", "Targeted AME", "Spawn Boss", "Force Area NPC Chat", "Kill All Nearby NPCs")
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 ->
                when (buttonId) {
                    // Send Player Notification
                    1 -> {
                        end()
                        sendInputDialogue(player, InputType.STRING_LONG, "Enter the notification message:") { value ->
                            val message = value as String
                            for (p in Repository.players) {
                                p ?: continue
                                p.packetDispatch.sendString(
                                    colorize("%Y${message.replace("_", " ").capitalize()}"),
                                    754,
                                    5,
                                )
                            }
                        }
                    }
                    // Targeted AME
                    2 -> {
                        options(*AMEs)
                        stage = 100
                    }

                    // Boss Spawn menu
                    3 -> {
                        options(*BossNames)
                        stage = 200
                    }

                    // Force Area NPC Chat
                    4 -> {
                        end()
                        sendInputDialogue(player, InputType.STRING_LONG, "Enter the chat message:") { value ->
                            val msg = value as String
                            RegionManager.getLocalNpcs(player).forEach {
                                it.sendChat(msg)
                            }
                        }
                    }

                    // Kill all nearby NPCs
                    5 -> {
                        end()
                        RegionManager.getLocalNpcs(player).forEach {
                            it.finalizeDeath(player)
                        }
                    }
                }
            // AME Spawning
            100 -> {
                end()
                sendInputDialogue(player, InputType.STRING_SHORT, "Enter player name:") { value ->
                    val other = Repository.getPlayerByName(value.toString().lowercase().replace(" ", "_"))
                    if (other == null) {
                        player.sendMessage(colorize("%RInvalid player name."))
                    }
                }
            }

            // Spawn boss
            200 -> {
                end()
                val id = BossIDs[buttonId - 1]
                val boss = NPC(id, player.location)
                boss.isRespawn = false
                boss.isAggressive = true
                boss.init()
            }
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(ID)
    }
}
