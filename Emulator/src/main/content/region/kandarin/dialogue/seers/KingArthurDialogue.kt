package content.region.kandarin.dialogue.seers

import content.region.kandarin.quest.merlin.dialogue.KingArthurDialogueFile
import core.api.openDialogue
import core.api.quest.isQuestComplete
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class KingArthurDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        if (!isQuestComplete(player, Quests.MERLINS_CRYSTAL)) {
            openDialogue(player, KingArthurDialogueFile(), NPCs.KING_ARTHUR_251)
        } else {
            openDialogue(
                player,
                content.region.kandarin.quest.grail.dialogue
                    .KingArthurHGDialogue(),
                NPCs.KING_ARTHUR_251,
            )
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return KingArthurDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.KING_ARTHUR_251)
    }
}
