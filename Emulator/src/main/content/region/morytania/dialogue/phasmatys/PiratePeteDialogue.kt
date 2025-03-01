package content.region.morytania.dialogue.phasmatys

import content.region.morytania.quest.deal.dialogue.PiratePeteDialogueFile
import core.api.openDialogue
import core.api.quest.getQuest
import core.api.sendDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.quest.QuestRequirements
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs

@Initializable
class PiratePeteDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        val quest = getQuest(player, QuestRequirements.RUM_DEAL.questName)
        if (quest.hasRequirements(player)) {
            openDialogue(player, PiratePeteDialogueFile())
        } else {
            sendDialogue(player, "The Pirate Pete is too busy to talk.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.PIRATE_PETE_2825)
    }
}
