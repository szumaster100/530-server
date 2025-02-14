package content.region.kandarin.dialogue.seers

import org.rs.consts.NPCs
import org.rs.consts.Quests
import content.region.kandarin.quest.merlin.dialogue.LadyOfTheLakeDialogueFile
import content.region.kandarin.quest.merlin.dialogue.TheLadyOfTheLakeDialogueFile
import core.api.quest.isQuestComplete
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable

@Initializable
class LadyOfTheLakeDialogue(player: Player? = null) : Dialogue(player) {

    override fun handle(interfaceId: Int, buttonId: Int): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, LadyOfTheLakeDialogueFile(), NPCs.THE_LADY_OF_THE_LAKE_250)
        } else {
            openDialogue(player, TheLadyOfTheLakeDialogueFile(), NPCs.THE_LADY_OF_THE_LAKE_250)
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return LadyOfTheLakeDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.THE_LADY_OF_THE_LAKE_250)
    }
}