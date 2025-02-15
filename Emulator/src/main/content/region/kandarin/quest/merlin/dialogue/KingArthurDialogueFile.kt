package content.region.kandarin.quest.merlin.dialogue

import core.api.quest.finishQuest
import core.api.quest.getQuestStage
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class KingArthurDialogueFile : DialogueFile() {
    val STAGE_MERLIN_FINISH = 20
    val STAGE_STARTED_QUEST = 30

    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        val quest = player!!.getQuestRepository().getQuest(Quests.MERLINS_CRYSTAL)
        npc = NPC(NPCs.KING_ARTHUR_251)

        when (stage) {
            0 -> {
                if (quest.getStage(player) == 60) {
                    playerl(FaceAnim.NEUTRAL, "I have freed Merlin from his crystal!").also { stage++ }
                    stage = STAGE_MERLIN_FINISH
                } else {
                    npcl(FaceAnim.NEUTRAL, "Welcome to my court. I am King Arthur.")
                    stage++
                }
            }
            1 ->
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "I want to become a Knight of the Round Table!", 2),
                    Topic(FaceAnim.NEUTRAL, "So what are you doing in Gielinor?", 10),
                    Topic(FaceAnim.NEUTRAL, "Thank you very much.", END_DIALOGUE),
                )
            2 -> {
                if (getQuestStage(player!!, Quests.MERLINS_CRYSTAL) > 0) {
                    npcl(
                        FaceAnim.NEUTRAL,
                        "Well then you must complete your quest to rescue Merlin. Talk to my knights if you need any help.",
                    ).also { stage = END_DIALOGUE }
                } else {
                    npcl(
                        FaceAnim.NEUTRAL,
                        "Well, in that case I think you need to go on a quest to prove yourself worthy.",
                    ).also {
                        stage++
                    }
                }
            }
            3 -> npcl(FaceAnim.NEUTRAL, "My knights all appreciate a good quest.").also { stage++ }
            4 -> npcl(FaceAnim.DISGUSTED, "Unfortunately, our current quest is to rescue Merlin.").also { stage++ }
            5 ->
                npcl(
                    FaceAnim.THINKING,
                    "Back in England, he got himself trapped in some sort of magical Crystal. We've moved him from the cave we found him in and now he's upstairs in his tower.",
                ).also {
                    stage++
                }
            6 -> {
                playerl(FaceAnim.NEUTRAL, "I will see what I can do then.")
                quest.start(player)
                stage++
            }
            7 -> npcl(FaceAnim.NEUTRAL, "Talk to my knights if you need any help.").also { stage = END_DIALOGUE }
            10 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "Well legend says we will return to Britain in its time of greatest need. But that's not for quite a while yet.",
                ).also {
                    stage++
                }
            11 -> npcl(FaceAnim.NEUTRAL, "So we've moved the whole outfit here for now.").also { stage++ }
            12 -> npcl(FaceAnim.NEUTRAL, "We're passing the time in Gielinor!").also { stage = END_DIALOGUE }
            20 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "Ah, A good job, well done. I dub thee a Knight Of The Round Table. You are now an honorary knight.",
                ).also {
                    stage++
                }
            21 -> {
                end()
                finishQuest(player!!, Quests.MERLINS_CRYSTAL)
            }
        }
    }
}
