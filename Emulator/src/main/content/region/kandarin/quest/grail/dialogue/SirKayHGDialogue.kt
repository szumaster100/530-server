package content.region.kandarin.quest.grail.dialogue

import org.rs.consts.NPCs
import org.rs.consts.Quests
import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE

class SirKayHGDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.SIR_KAY_241)

        when (stage) {
            0 -> npcl(FaceAnim.NEUTRAL, "Good morrow sirrah!").also {
                if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 || isQuestComplete(player!!, Quests.HOLY_GRAIL)) {
                    stage = 1
                } else if (getQuestStage(player!!, Quests.HOLY_GRAIL) >= 10) {
                    stage = 10
                }
            }

            1 -> npcl(FaceAnim.NEUTRAL, "Sir Knight! Many thanks for your assistance in restoring Merlin to his former freedom!").also { stage++ }
            2 -> playerl(FaceAnim.NEUTRAL, "Hey, no problem.").also { stage = END_DIALOGUE }
            10 -> npcl(FaceAnim.NEUTRAL, "I hear you are questing for the Holy Grail?!").also { stage++ }
            11 -> playerl(FaceAnim.NEUTRAL, "That's right. Any hints?").also { stage++ }
            12 -> npcl(FaceAnim.NEUTRAL, "Unfortunately not, Sirrah.").also { stage = END_DIALOGUE }
        }
    }
}