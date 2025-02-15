package content.region.kandarin.quest.grail.dialogue

import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirPalomedesHGDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_PALOMEDES_3787)

        when (stage) {
            0 ->
                npcl(FaceAnim.HALF_GUILTY, "Hello there adventurer, what do you want of me?").also {
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
                    FaceAnim.HALF_GUILTY,
                    "After your help freeing Merlin, my help is the least I can offer as a man of honour.",
                ).also {
                    stage++
                }
            2 ->
                playerl(FaceAnim.HALF_GUILTY, "Nothing right now, but I'll bear it in mind. Thanks.").also {
                    stage =
                        END_DIALOGUE
                }
            10 -> playerl(FaceAnim.FRIENDLY, "I'd like some advice on finding the Grail.").also { stage++ }
            11 -> npcl(FaceAnim.FRIENDLY, "Sorry, I cannot help you with that.").also { stage = END_DIALOGUE }
        }
    }
}
