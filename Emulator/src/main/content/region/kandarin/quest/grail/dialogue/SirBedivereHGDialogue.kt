package content.region.kandarin.quest.grail.dialogue

import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirBedivereHGDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_BEDIVERE_242)

        when (stage) {
            0 ->
                npcl(FaceAnim.FRIENDLY, "May I help you?").also {
                    if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 ||
                        isQuestComplete(player!!, Quests.HOLY_GRAIL)
                    ) {
                        stage = 2
                    } else if (getQuestStage(player!!, Quests.HOLY_GRAIL) >= 10) {
                        stage = 10
                    }
                }
            2 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "All Knights of the Round thank you for your assistance in this trying time for us.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
            10 -> npcl(FaceAnim.FRIENDLY, "You are looking for the Grail now adventurer?").also { stage++ }
            11 -> playerl(FaceAnim.FRIENDLY, "Absolutely.").also { stage++ }
            12 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "The best of luck to you! Make the name of Camelot proud, and bring it back to us.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
        }
    }
}
