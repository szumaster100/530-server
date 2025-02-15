package content.region.kandarin.dialogue.seers

import content.region.kandarin.quest.grail.dialogue.SirLucanHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirLucanDialogueFile
import core.api.openDialogue
import core.api.quest.isQuestComplete
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class SirLucanDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirLucanDialogueFile(), NPCs.SIR_LUCAN_245)
        } else {
            openDialogue(player, SirLucanHGDialogue(), NPCs.SIR_LUCAN_245)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirLucanDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_LUCAN_245)
    }
}
