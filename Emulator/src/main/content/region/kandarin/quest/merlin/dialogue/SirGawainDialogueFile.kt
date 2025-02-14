package content.region.kandarin.quest.merlin.dialogue

import org.rs.consts.NPCs
import org.rs.consts.Quests
import core.api.quest.getQuestStage
import core.api.quest.setQuestStage
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE

class SirGawainDialogueFile : DialogueFile() {

    val STAGE_LE_FAYE_END = 20
    val STAGE_PROGRESS = 15

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.SIR_GAWAIN_240)

        when (stage) {
            0 -> {
                npcl(FaceAnim.NEUTRAL, "Good day to you " + (if (player!!.isMale) "sir" else "madam") + "!")

                if (getQuestStage(player!!, Quests.MERLINS_CRYSTAL) == 0) {
                    stage = 1
                } else if (getQuestStage(player!!, Quests.MERLINS_CRYSTAL) == 10) {
                    stage = 10
                } else if (getQuestStage(player!!, Quests.MERLINS_CRYSTAL) in 20..30) {
                    stage = 20
                } else if (getQuestStage(player!!, Quests.MERLINS_CRYSTAL) >= 40) {
                    stage = 40
                }
            }

            1 -> {
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Good day.", END_DIALOGUE),
                    Topic(FaceAnim.NEUTRAL, "Know you of any quests sir knight?", 2)
                )
            }

            2 -> {
                npcl(FaceAnim.NEUTRAL, "The king is the man to talk to if you want a quest.")
                stage = END_DIALOGUE
            }

            10 -> {
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Good day.", END_DIALOGUE),
                    Topic(FaceAnim.NEUTRAL, "Any ideas on how to get Merlin out of that crystal?", 11),
                    Topic(FaceAnim.NEUTRAL, "Do you know how Merlin got trapped?", STAGE_PROGRESS)
                )
            }

            11 -> {
                npcl(
                    FaceAnim.NEUTRAL,
                    "I'm a little stumped myself. We've tried opening it with anything and everything!"
                )
                stage = END_DIALOGUE
            }

            15 -> {
                npcl(FaceAnim.ANGRY, "I would guess this is the work of the evil Morgan Le Faye!")
                stage++
            }

            16 -> {
                playerl(FaceAnim.NEUTRAL, "And where could I find her?")
                stage++
            }

            17 -> {
                npc(
                    "She lives in her stronghold to the south of here,",
                    "guarded by some renegade knights led by Sir Mordred."
                )
                setQuestStage(player!!, Quests.MERLINS_CRYSTAL, 20)
                player!!.getQuestRepository().syncronizeTab(player)
                stage++
            }

            18 -> {
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Any idea how to get into Moran Le Faye's stronghold?", STAGE_LE_FAYE_END),
                    Topic(FaceAnim.NEUTRAL, "Thank you for the information.", 25)
                )
            }

            STAGE_LE_FAYE_END -> {
                npcl(FaceAnim.NEUTRAL, "No, you've got me stumped there...")
                stage = END_DIALOGUE
            }

            25 -> {
                npcl(FaceAnim.NEUTRAL, "It is the least I can do.")
                stage = END_DIALOGUE
            }

            30 -> {
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Any idea how to get into Moran Le Faye's stronghold?", STAGE_LE_FAYE_END),
                    Topic(FaceAnim.NEUTRAL, "Hello again.", END_DIALOGUE)
                )
            }

            40 -> {
                playerl(FaceAnim.NEUTRAL, "Any ideas on finding Excalibur?")
                stage++
            }

            41 -> {
                npcl(FaceAnim.NEUTRAL, "Unfortunately not, adventurer.")
                stage = END_DIALOGUE
            }
        }
    }
}