package content.region.kandarin.quest.merlin.dialogue

import core.api.quest.getQuestStage
import core.api.quest.setQuestStage
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs
import org.rs.consts.Quests

class SirLancelotDialogueFile : DialogueFile() {
    val STAGE_FULL_OF_YOURSELF = 2
    val STAGE_GET_MERLIN_OUT = 11

    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.SIR_LANCELOT_239)
        val merlinsCrystalQuest = player!!.questRepository.getQuest(Quests.MERLINS_CRYSTAL)

        when (stage) {
            0 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "Greetings! I am Sir Lancelot, the greatest Knight in the land! What do you want?",
                ).also {
                    if (merlinsCrystalQuest.getStage(player) == 10 || merlinsCrystalQuest.getStage(player) >= 30) {
                        stage = 10
                    } else if (merlinsCrystalQuest.getStage(player) == 20) {
                        stage = 20
                    } else if (merlinsCrystalQuest.getStage(player) == 0) {
                        stage++
                    }
                }

            1 ->
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "You're a little full of yourself aren't you?", STAGE_FULL_OF_YOURSELF),
                    Topic(FaceAnim.NEUTRAL, "I seek a quest!", 5),
                )

            STAGE_FULL_OF_YOURSELF ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "I have every right to be proud of myself.",
                ).also { stage++ }

            3 -> npcl(FaceAnim.NEUTRAL, "My prowess in battle is world renowned!").also { stage = END_DIALOGUE }

            5 -> npcl(FaceAnim.NEUTRAL, "Leave questing to the professionals.").also { stage++ }
            6 -> npcl(FaceAnim.NEUTRAL, "Such as myself.").also { stage = END_DIALOGUE }

            10 ->
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "I want to get Merlin out of the crystal.", STAGE_GET_MERLIN_OUT),
                    Topic(FaceAnim.NEUTRAL, "You're a little full of yourself aren't you?", STAGE_FULL_OF_YOURSELF),
                )

            STAGE_GET_MERLIN_OUT ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "Well, if the Knights of the Round Table can't manage it, I can't see how a commoner like you could succeed where we have failed.",
                ).also {
                    stage =
                        END_DIALOGUE
                }

            20 ->
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "I want to get Merlin out of the crystal.", STAGE_GET_MERLIN_OUT),
                    Topic(FaceAnim.NEUTRAL, "You're a little full of yourself aren't you?", STAGE_FULL_OF_YOURSELF),
                    Topic(FaceAnim.NEUTRAL, "Any ideas on how to get into Morgan Le Faye's stronghold?", 21),
                )

            21 -> npcl(FaceAnim.NEUTRAL, "That stronghold is built in a strong defensive position.").also { stage++ }
            22 -> npcl(FaceAnim.NEUTRAL, "It's on a big rock sticking out into the sea.").also { stage++ }
            23 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "There are two ways in that I know of, the large heavy front doors, and the sea entrance, only penetrable by boat.",
                ).also {
                    stage++
                }

            24 -> {
                npcl(FaceAnim.NEUTRAL, "They take all their deliveries by boat.").also {
                    if (getQuestStage(player!!, Quests.MERLINS_CRYSTAL) == 20) {
                        setQuestStage(player!!, Quests.MERLINS_CRYSTAL, 30)
                    }
                    stage = END_DIALOGUE
                }
            }
        }
    }
}
