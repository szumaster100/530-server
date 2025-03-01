package content.region.fremennik.quest.horror.handlers

import core.api.*
import core.api.quest.isQuestComplete
import core.game.dialogue.DialogueFile
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.Items
import org.rs.consts.Quests
import org.rs.consts.Scenery

class LighthouseBookcaseListener :
    DialogueFile(),
    InteractionListener {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        when (stage) {
            0 -> handleInitialStage()
            1 -> showOptions()
            2 -> handleItemSelection(buttonID)
        }
    }

    private fun handleInitialStage() {
        if (isQuestComplete(player!!, Quests.HORROR_FROM_THE_DEEP)) {
            end()
            sendDialogue(
                player!!,
                "You have completed the Horror from the Deep quest. You probably don't need this book.",
            )
        } else {
            sendDialogue(
                player!!,
                "There are three books here that look important... What would you like to do?",
            ).also { stage++ }
        }
    }

    private fun showOptions() {
        options(
            "Take the Lighthouse Manual",
            "Take the ancient Diary",
            "Take Jossik's Journal",
            "Take all three books",
        ).also {
            stage++
        }
    }

    private fun handleItemSelection(buttonID: Int) {
        when (buttonID) {
            1 -> takeItem(Items.MANUAL_3847)
            2 -> takeItem(Items.DIARY_3846)
            3 -> takeItem(Items.JOURNAL_3845)
            4 -> takeAllItems()
        }
    }

    private fun takeItem(item: Int) {
        end()
        if (freeSlots(player!!) < 1) {
            sendDialogue(player!!, "You do not have enough room to take that.")
        } else {
            addItemOrDrop(player!!, item)
        }
    }

    private fun takeAllItems() {
        end()
        if (freeSlots(player!!) < 3) {
            sendDialogue(player!!, "You do not have enough room to take all three.")
        } else {
            addItemOrDrop(player!!, Items.MANUAL_3847)
            addItemOrDrop(player!!, Items.DIARY_3846)
            addItemOrDrop(player!!, Items.JOURNAL_3845)
        }
    }

    override fun defineListeners() {
        on(Scenery.BOOKCASE_4617, IntType.SCENERY, "search") { player, node ->
            openDialogue(player, LighthouseBookcaseListener(), node.asScenery())
            return@on true
        }
    }
}
