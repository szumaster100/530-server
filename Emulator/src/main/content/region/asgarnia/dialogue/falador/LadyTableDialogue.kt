package content.region.asgarnia.dialogue.falador

import org.rs.consts.NPCs
import content.region.asgarnia.quest.rd.handlers.ObservationTest
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable

@Initializable
class LadyTableDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        openDialogue(player, ObservationTest(), npc)
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.LADY_TABLE_2283)
    }
}