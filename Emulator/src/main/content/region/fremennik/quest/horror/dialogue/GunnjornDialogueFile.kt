package content.region.fremennik.quest.horror.dialogue

import core.api.addItem
import core.api.freeSlots
import core.api.quest.setQuestStage
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.Quests

class GunnjornDialogueFile : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        when (stage) {
            0 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Haha welcome to my obstacle course. Have fun, but",
                    "remember this isn't a child's playground. People have",
                    "died here.",
                ).also { stage++ }

            1 -> playerl(FaceAnim.HALF_ASKING, "Hi, are you called Gunnjorn?").also { stage++ }
            2 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Why, indeed I am. I own this agility course, it can be",
                    "very dangerous!",
                ).also { stage++ }

            3 ->
                player(
                    FaceAnim.FRIENDLY,
                    "Yeah, that's great. Anyway, I understand you have a",
                    "cousin named Larrissa who gave you a key...?",
                ).also { stage++ }

            4 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Yes, she did! How did you know of this? She said she",
                    "probably wouldn't need it, but gave it to me for safe",
                    "keeping just in case.",
                ).also { stage++ }

            5 ->
                player(
                    FaceAnim.FRIENDLY,
                    "Well, something has happened at the lighthouse, and she",
                    "has been locked out. I need you to give me her key.",
                ).also { stage++ }

            6 -> {
                if (freeSlots(player!!) == 0) {
                    npc(
                        FaceAnim.HALF_GUILTY,
                        "Well, I would give you the key, but apparently you",
                        "don't have any room.",
                    ).also { stage = END_DIALOGUE }
                } else {
                    end()
                    npc("Sure. Here you go.")
                    addItem(player!!, Items.LIGHTHOUSE_KEY_3848)
                    setQuestStage(player!!, Quests.HORROR_FROM_THE_DEEP, 5)
                }
            }
        }
    }
}
