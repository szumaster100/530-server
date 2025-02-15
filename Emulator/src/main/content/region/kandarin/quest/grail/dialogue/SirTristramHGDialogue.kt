package content.region.kandarin.quest.grail.dialogue

import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirTristramHGDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_TRISTRAM_243)

        when (stage) {
            0 -> npcl(FaceAnim.NEUTRAL, "Hail Arthur, King of the Britons!").also { stage++ }
            1 ->
                playerl(FaceAnim.NEUTRAL, "Um... Hello.").also {
                    if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 ||
                        isQuestComplete(player!!, Quests.HOLY_GRAIL)
                    ) {
                        stage = 2
                    } else if (getQuestStage(player!!, Quests.HOLY_GRAIL) >= 10) {
                        stage = 10
                    }
                }
            2 -> npcl(FaceAnim.NEUTRAL, "Thanks for freeing Merlin.").also { stage++ }
            3 -> playerl(FaceAnim.NEUTRAL, "No problem. It was easy.").also { stage = END_DIALOGUE }
            10 -> playerl(FaceAnim.NEUTRAL, "I am seeking the Grail...").also { stage++ }
            11 -> npcl(FaceAnim.NEUTRAL, "Good luck with that!").also { stage = END_DIALOGUE }
        }
    }
}
