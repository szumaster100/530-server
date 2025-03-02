package content.region.fremennik.quest.horror.handlers.bookcase

import content.data.QuestItem
import core.api.addItem
import core.api.freeSlots
import core.api.openDialogue
import core.api.quest.isQuestComplete
import core.api.sendDialogue
import core.game.dialogue.DialogueFile
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.Quests

class LighthouseBookcase :
    DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        when (stage) {
            0 -> {
                if (isQuestComplete(player!!, Quests.HORROR_FROM_THE_DEEP)) {
                    openDialogue(player!!, QuestItem(Items.MANUAL_3847))
                } else {
                    sendDialogue(
                        player!!,
                        "There are three books here that look important... What would you like to do?"
                    ).also { stage++ }
                }
            }

            1 -> options(
                "Take the Lighthouse Manual",
                "Take the ancient Diary",
                "Take Jossik's Journal",
                "Take all three books",
            ).also {
                stage++
            }

            2 -> {
                if (freeSlots(player!!) < 1) {
                    sendDialogue(player!!, "You do not have enough room to take that.")
                    return
                }
                when (buttonID) {
                    1 -> addItem(player!!, Items.MANUAL_3847, 1).also { stage = 3 }
                    2 -> addItem(player!!, Items.DIARY_3846, 1).also { stage = 3 }
                    3 -> addItem(player!!, Items.JOURNAL_3845, 1).also { stage = 3 }
                    4 -> {
                        end()
                        if (freeSlots(player!!) < 3) {
                            sendDialogue(player!!, "You do not have enough room to take all three.")
                            stage = 3
                            return
                        }
                        addItem(player!!, Items.MANUAL_3847, 1)
                        addItem(player!!, Items.DIARY_3846, 1)
                        addItem(player!!, Items.JOURNAL_3845, 1)
                    }
                }
            }
            3 -> end()
        }
    }
}
