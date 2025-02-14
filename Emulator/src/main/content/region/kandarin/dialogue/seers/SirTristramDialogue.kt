package content.region.kandarin.dialogue.seers

import org.rs.consts.NPCs
import org.rs.consts.Quests
import content.region.kandarin.quest.grail.dialogue.SirTristramHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirTristramDialogueFile
import core.api.quest.isQuestComplete
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable

@Initializable
class SirTristramDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirTristramDialogueFile(), NPCs.SIR_TRISTRAM_243)
        } else {
            openDialogue(player, SirTristramHGDialogue(), NPCs.SIR_TRISTRAM_243)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirTristramDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_TRISTRAM_243)
    }
}