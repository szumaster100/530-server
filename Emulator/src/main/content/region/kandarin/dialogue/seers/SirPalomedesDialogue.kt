package content.region.kandarin.dialogue.seers

import org.rs.consts.NPCs
import org.rs.consts.Quests
import content.region.kandarin.quest.grail.dialogue.SirPalomedesHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirPalomedesDialogueFile
import core.api.quest.isQuestComplete
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable

@Initializable
class SirPalomedesDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirPalomedesDialogueFile(), NPCs.SIR_PALOMEDES_3787)
        } else {
            openDialogue(player, SirPalomedesHGDialogue(), NPCs.SIR_PALOMEDES_3787)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirPalomedesDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_PALOMEDES_3787)
    }
}