package content.region.kandarin.quest.merlin.dialogue

import org.rs.consts.NPCs
import org.rs.consts.Quests
import content.region.kandarin.quest.merlin.handlers.MerlinUtils
import core.api.quest.getQuestStage
import core.api.setAttribute
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.IfTopic
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE

class LadyOfTheLakeDialogueFile : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.THE_LADY_OF_THE_LAKE_250)
        var canSeekSword = getQuestStage(player!!, Quests.MERLINS_CRYSTAL) >= 40
        when (stage) {
            0 -> npcl(FaceAnim.NEUTRAL, "Good day to you sir.").also { stage++ }
            1 -> showTopics(
                Topic(FaceAnim.NEUTRAL, "Who are you?", 2),
                Topic(FaceAnim.NEUTRAL, "Good day.", 5),
                IfTopic(FaceAnim.NEUTRAL, "I seek the sword Excalibur.", 10, canSeekSword)
            )
            2 -> npcl(FaceAnim.NEUTRAL, "I am the Lady of the Lake.").also { stage = END_DIALOGUE }
            5 -> npcl(FaceAnim.NEUTRAL, "Good day to you ${if (player!!.isMale) "sir" else "madam"}.").also { stage = END_DIALOGUE }
            10 -> npcl(FaceAnim.NEUTRAL, "Aye, I have that artefact in my possession.").also { stage++ }
            11 -> npcl(FaceAnim.NEUTRAL, "'Tis very valuable, and not an artefact to be given away lightly.").also { stage++ }
            12 -> npcl(FaceAnim.NEUTRAL, "I would want to give it away only to one who is worthy and good.").also { stage++ }
            13 -> playerl(FaceAnim.NEUTRAL, "And how am I meant to prove that?").also { stage++ }
            14 -> npcl(FaceAnim.NEUTRAL, "I shall set a test for you.").also { stage++ }
            15 -> npcl(FaceAnim.NEUTRAL, "First I need you to travel to Port Sarim. Then go to the upstairs room of the jeweller's shop there.").also { stage++ }
            16 -> playerl(FaceAnim.NEUTRAL, "Ok. That seems easy enough.").also {
                setAttribute(player!!, MerlinUtils.ATTR_STATE_TALK_LADY, true)
                setAttribute(player!!, MerlinUtils.ATTR_STATE_TALK_BEGGAR, false)
                stage = END_DIALOGUE
            }
        }
    }
}