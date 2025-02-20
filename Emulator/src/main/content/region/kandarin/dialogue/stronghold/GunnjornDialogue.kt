package content.region.kandarin.dialogue.stronghold

import core.api.inInventory
import core.api.openDialogue
import core.api.quest.getQuestStage
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class GunnjornDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun open(vararg args: Any): Boolean {
        npc = args[0] as NPC
        if (getQuestStage(player, Quests.HORROR_FROM_THE_DEEP) > 0 && !inInventory(player, Items.LIGHTHOUSE_KEY_3848)) {
            options(
                "Talk about Horror from the Deep.",
                "Talk about the Agility course.",
                "Talk about the wall after the log balance.",
                "Nothing.",
            ).also {
                stage =
                    1
            }
        } else {
            options(
                "Talk about the Agility course.",
                "Talk about the wall after the log balance.",
                "Can I talk about rewards?",
                "Nothing.",
            ).also {
                stage =
                    2
            }
        }
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            1 ->
                when (buttonId) {
                    1 -> {
                        end()
                        openDialogue(
                            player,
                            content.region.fremennik.quest.horror.dialogue
                                .GunnjornDialogueFile(),
                            npc,
                        ).also {
                            stage =
                                END_DIALOGUE
                        }
                    }

                    2 -> playerl(FaceAnim.FRIENDLY, "Hey there. What is this place?").also { stage = 3 }
                    3 ->
                        playerl(FaceAnim.FRIENDLY, "What's wrong with the wall after the log balance?").also {
                            stage =
                                6
                        }
                    4 -> playerl(FaceAnim.FRIENDLY, "Nothing.").also { stage = 15 }
                }
            2 ->
                when (buttonId) {
                    1 -> playerl(FaceAnim.FRIENDLY, "Hey there. What is this place?").also { stage = 3 }
                    2 ->
                        playerl(FaceAnim.FRIENDLY, "What's wrong with the wall after the log balance?").also {
                            stage =
                                6
                        }
                    3 ->
                        playerl(FaceAnim.FRIENDLY, "Can I talk about rewards?").also {
                            stage = 16
                        }
                    4 -> playerl(FaceAnim.FRIENDLY, "Nothing.").also { stage = 15 }
                }
            3 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Haha welcome to my obstacle course. Have fun, but",
                    "remember this isn't a child's playground. People have",
                    "died here.",
                ).also {
                    stage++
                }
            4 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "This course starts at the ropeswings to the east. When you've done the swing, head across the slippery log to the building. When you've traversed the obstacles inside, you'll come out on the other side.",
                ).also {
                    stage++
                }
            5 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "From there, head across the low walls to finish. If you've done all the obstacles as I've described, and in order, you'll get a lap bonus.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
            6 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "The wall after the log balance? Nothing, really. I just put some tough material on it, giving some people something to grip hold of.",
                ).also {
                    stage++
                }
            7 -> playerl(FaceAnim.FRIENDLY, "Why would you do that?").also { stage++ }
            8 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "So people like you can have a tougher route round this course. Me and a mate get together and set up a new challenge that only the truly agile will conquer.",
                ).also {
                    stage++
                }
            9 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "The extra stuff starts at that wall; so, if you think you're up to it, I suggest you scramble up there after the log balance.",
                ).also {
                    stage++
                }
            10 -> playerl(FaceAnim.FRIENDLY, "Sounds interesting. Anything else I should know?").also { stage++ }
            11 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Nothing, really. Just make sure you complete the other obstacles before ya do. If you finish a full lap, you'll get an increased bonus for doing the tougher route.",
                ).also {
                    stage++
                }
            12 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "If you manage to do 250 laps of this advanced route without a single mistake, I'll let you have a special item.",
                ).also {
                    stage++
                }
            13 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "I'll keep track of your lap tallies, so you can check how you're getting on with me at any time.",
                ).also {
                    stage++
                }
            14 -> playerl(FaceAnim.FRIENDLY, "That's all I need for now. Bye.").also { stage++ }
            15 -> npcl(FaceAnim.FRIENDLY, "Bye for now. Come back if you need any help.").also { stage = END_DIALOGUE }
            16 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "There's no reward for you just yet. Your lap count is only 0. It's 250 successful laps or no reward.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return GunnjornDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.GUNNJORN_607)
    }
}
