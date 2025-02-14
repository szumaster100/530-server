package content.region.kandarin.quest.grail.dialogue

import org.rs.consts.NPCs
import org.rs.consts.Quests
import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE

class SirLancelotHGDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.SIR_LANCELOT_239)

        when (stage) {
            0 -> npcl(FaceAnim.FRIENDLY, "Greetings! I am Sir Lancelot, the greatest Knight in the land! What do you want?").also {
                if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 || isQuestComplete(player!!, Quests.HOLY_GRAIL)) {
                    stage = 1
                } else {
                    stage = 10
                }
            }

            1 -> npcl(FaceAnim.NEUTRAL, "Hmmm. I heard you freed Merlin. Either you're better than you look or you got lucky. I think the latter.").also { stage = END_DIALOGUE }
            10 -> playerl(FaceAnim.FRIENDLY, "I am questing for the Holy Grail.").also { stage++ }
            11 -> npcl(FaceAnim.FRIENDLY, "The Grail? Ha! Frankly, little man, you're not in that league.").also { stage++ }
            12 -> playerl(FaceAnim.FRIENDLY, "Why do you say that?").also { stage++ }
            13 -> npcl(FaceAnim.FRIENDLY, "You got lucky with freeing Merlin but there's no way a puny wannabe like you is going to find the Holy Grail where so many others have failed.").also { stage++ }
            14 -> playerl(FaceAnim.ANGRY, "We'll see about that.").also { stage = END_DIALOGUE }
        }
    }
}