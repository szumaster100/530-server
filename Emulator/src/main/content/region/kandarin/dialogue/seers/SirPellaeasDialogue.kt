package content.region.kandarin.dialogue.seers

import org.rs.consts.NPCs
import org.rs.consts.Quests
import content.region.kandarin.quest.grail.dialogue.SirPelleaseHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirPelleasDialogueFile
import core.api.quest.isQuestComplete
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable

@Initializable
class SirPellaeasDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirPelleasDialogueFile(), NPCs.SIR_PELLEAS_244)
        } else {
            openDialogue(player, SirPelleaseHGDialogue(), NPCs.SIR_PELLEAS_244)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirPellaeasDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_PELLEAS_244)
    }
}