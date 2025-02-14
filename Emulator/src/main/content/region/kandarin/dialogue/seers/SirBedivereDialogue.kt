package content.region.kandarin.dialogue.seers

import org.rs.consts.NPCs
import org.rs.consts.Quests
import content.region.kandarin.quest.grail.dialogue.SirBedivereHGDialogue
import content.region.kandarin.quest.merlin.dialogue.SirBedivereDialogueFile
import core.api.quest.isQuestComplete
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable

@Initializable
class SirBedivereDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, SirBedivereDialogueFile(), NPCs.SIR_BEDIVERE_242)
        } else {
            openDialogue(player, SirBedivereHGDialogue(), NPCs.SIR_BEDIVERE_242)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return SirBedivereDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SIR_BEDIVERE_242)
    }
}