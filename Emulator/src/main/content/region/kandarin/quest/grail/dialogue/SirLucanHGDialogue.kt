package content.region.kandarin.quest.grail.dialogue

import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirLucanHGDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_LUCAN_245)

        when (stage) {
            0 ->
                npcl(FaceAnim.FRIENDLY, "Hello there adventurer.").also {
                    if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 ||
                        isQuestComplete(player!!, Quests.HOLY_GRAIL)
                    ) {
                        stage = 1
                    } else if (getQuestStage(player!!, Quests.HOLY_GRAIL) >= 10) {
                        stage = 10
                    }
                }
            1 -> npcl(FaceAnim.FRIENDLY, "Congratulations on freeing Merlin!").also { stage = END_DIALOGUE }
            10 -> playerl(FaceAnim.FRIENDLY, "I seek the Grail of legend!").also { stage++ }
            11 -> npcl(FaceAnim.FRIENDLY, "I'm afraid I don't have any suggestions...").also { stage++ }
            12 ->
                playerl(
                    FaceAnim.FRIENDLY,
                    "Thanks. I'll try and find someone who does.",
                ).also { stage = END_DIALOGUE }
        }
    }
}
