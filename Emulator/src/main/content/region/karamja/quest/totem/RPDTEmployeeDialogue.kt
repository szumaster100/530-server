package content.region.karamja.quest.totem

import core.api.quest.getQuestStage
import core.api.quest.setQuestStage
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.plugin.Initializable
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class RPDTEmployeeDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun open(vararg args: Any?): Boolean {
        npcl(FaceAnim.HAPPY, "Welcome to R.P.D.T.!")
        stage =
            if (getQuestStage(player, Quests.TRIBAL_TOTEM) == 20) {
                5
            } else {
                0
            }
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 -> playerl(FaceAnim.HAPPY, "Thank you very much.").also { stage = 1000 }

            5 -> playerl(FaceAnim.ASKING, "So, when are you going to deliver this crate?").also { stage++ }
            6 ->
                npcl(FaceAnim.THINKING, "Well... I guess we could do it now...").also {
                    setQuestStage(player, Quests.TRIBAL_TOTEM, 25)
                    stage = 1000
                }

            1000 -> end()
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.RPDT_EMPLOYEE_843)
    }
}
