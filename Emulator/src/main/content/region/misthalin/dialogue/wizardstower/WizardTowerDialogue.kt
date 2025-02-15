package content.region.misthalin.dialogue.wizardstower

import core.game.dialogue.Dialogue
import core.game.dialogue.DialogueInterpreter
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.RandomFunction

@Initializable
class WizardTowerDialogue(
    player: Player? = null,
) : Dialogue(player) {
    private var bookName = ""
    private val books =
        arrayOf(
            "Living with a Wizard Husband - a Housewife's Story",
            "Wind Strike for Beginners",
            "So you think you're a Mage? Volume 28",
            "101 Ways to Impress your Mates with Magic",
            "The Life & Times of a Thingummywut by Traiborn the Wizard",
            "How to become the Ultimate Wizard of the Universe",
            "The Dark Arts of Magical Wands",
        )

    override fun open(vararg args: Any): Boolean {
        sendDialogue(
            "There's a large selection of books, the majority of which look fairly",
            "old. Some very strange names... You pick one at random:",
        )
        bookName = books[RandomFunction.random(books.size)]
        stage = 0
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 -> sendDialogue("'$bookName'").also { stage++ }
            1 -> sendDialogue("Interesting...").also { stage = END_DIALOGUE }
            2 -> end()
        }
        return true
    }

    override fun newInstance(player: Player): Dialogue {
        return WizardTowerDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(DialogueInterpreter.getDialogueKey("wizard-tower-dialogue"))
    }
}
