package content.region.kandarin.quest.grail.dialogue

import org.rs.consts.NPCs
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.tools.END_DIALOGUE

class DiseasedPeasantDialogue : DialogueFile() {

    override fun handle(componentID: Int, buttonID: Int) {
        npc = NPC(NPCs.PEASANT_214)

        when (stage) {
            0 -> npcl(FaceAnim.SAD, "Woe is me! Our crops are all failing... how shall I feed myself this winter?").also { stage = END_DIALOGUE }
        }
    }
}