package content.region.kandarin.dialogue.seers

import content.region.kandarin.quest.grail.dialogue.SirLancelotHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirLancelotDialogueFile
import core.api.openDialogue
import core.api.quest.isQuestComplete
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class SirLancelotDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirLancelotDialogueFile(), NPCs.SIR_LANCELOT_239)
        } else {
            openDialogue(player, SirLancelotHGDialogue(), NPCs.SIR_LANCELOT_239)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirLancelotDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_LANCELOT_239)
    }
}
