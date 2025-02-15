package content.region.kandarin.dialogue.seers

import content.region.kandarin.quest.grail.dialogue.SirGawainHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirGawainDialogueFile
import core.api.openDialogue
import core.api.quest.isQuestComplete
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class SirGawainDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirGawainDialogueFile(), NPCs.SIR_GAWAIN_240)
        } else {
            openDialogue(player, SirGawainHGDialogue(), NPCs.SIR_GAWAIN_240)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirGawainDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_GAWAIN_240)
    }
}
