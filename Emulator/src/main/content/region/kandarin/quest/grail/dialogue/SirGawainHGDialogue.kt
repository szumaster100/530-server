package content.region.kandarin.quest.grail.dialogue

import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirGawainHGDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_GAWAIN_240)

        when (stage) {
            0 ->
                npcl(FaceAnim.NEUTRAL, "Good day to you sir!").also {
                    if (getQuestStage(player!!, Quests.HOLY_GRAIL) == 0 ||
                        isQuestComplete(player!!, Quests.HOLY_GRAIL)
                    ) {
                        stage = 1
                    } else if (getQuestStage(player!!, Quests.HOLY_GRAIL) >= 10) {
                        stage = 10
                    }
                }
            1 ->
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Good day.", END_DIALOGUE),
                    Topic(FaceAnim.NEUTRAL, "Know you of any quests sir knight?", 5),
                )
            5 ->
                npcl(FaceAnim.NEUTRAL, "I think you've done the main quest we were on right now...").also {
                    stage =
                        END_DIALOGUE
                }
            10 -> playerl(FaceAnim.NEUTRAL, "I seek the Grail in the name of Camelot!").also { stage++ }
            11 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "The Grail? That is truly a noble quest indeed. None but Galahad have come close.",
                ).also {
                    stage++
                }
            12 -> playerl(FaceAnim.NEUTRAL, "Galahad? Who is he?").also { stage++ }
            13 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "He used to be one of the Knights of the Round Table, but he mysteriously disappeared many years ago.",
                ).also {
                    stage++
                }
            14 -> playerl(FaceAnim.NEUTRAL, "Why would he quit being a Knight?").also { stage++ }
            15 -> npcl(FaceAnim.NEUTRAL, "That is a good question.").also { stage++ }
            16 -> npcl(FaceAnim.NEUTRAL, "I'm afraid I don't have the answer.").also { stage = END_DIALOGUE }
        }
    }
}
