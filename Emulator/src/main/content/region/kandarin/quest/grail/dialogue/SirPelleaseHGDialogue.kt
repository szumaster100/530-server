package content.region.kandarin.quest.grail.dialogue

import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirPelleaseHGDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_PELLEAS_244)

        when (stage) {
            0 ->
                npcl(FaceAnim.NEUTRAL, "Greetings to the court of King Arthur!").also {
                    if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 ||
                        isQuestComplete(player!!, Quests.HOLY_GRAIL)
                    ) {
                        stage = 1
                    } else if (getQuestStage(player!!, Quests.HOLY_GRAIL) >= 10) {
                        stage = 10
                    }
                }
            1 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "You are a very talented Knight indeed to have freed Merlin so quickly. You have all of our gratitude.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
            10 -> playerl(FaceAnim.NEUTRAL, "Any suggestions on finding the Grail?").also { stage++ }
            11 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "My best guess would be some sort of spell. Merlin is our magic expert. Ask him?",
                ).also {
                    stage++
                }
            12 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "Although having said that, I believe Galahad found its location once...",
                ).also { stage++ }
            13 -> playerl(FaceAnim.NEUTRAL, "Really? Know where I can find him?").also { stage++ }
            14 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "I'm afraid not. He left here many moons ago and I know not where he went.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
        }
    }
}
