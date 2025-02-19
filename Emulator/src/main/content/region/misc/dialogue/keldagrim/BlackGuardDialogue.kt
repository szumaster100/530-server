package content.region.misc.dialogue.keldagrim

import core.api.sendMessage
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.START_DIALOGUE
import org.rs.consts.NPCs

@Initializable
class BlackGuardDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            START_DIALOGUE -> {
                var random = arrayOf(1, 2, 3, 4, 5).random()
                when (random) {
                    1 -> npcl(FaceAnim.OLD_ANGRY3, "Black Guard: Obey the law!").also { stage = END_DIALOGUE }
                    2 -> npcl(FaceAnim.OLD_ANGRY3, "Stay out of trouble!").also { stage = END_DIALOGUE }
                    3 -> npcl(FaceAnim.OLD_ANGRY3, "Out of the way, human!").also { stage = END_DIALOGUE }
                    4 -> npcl(FaceAnim.OLD_ANGRY3, "I'm keeping an eye on you!").also { stage = END_DIALOGUE }
                    5 -> {
                        end()
                        sendMessage(player, "The guard ignores you.")
                    }
                }
            }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return BlackGuardDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(
            NPCs.BLACK_GUARD_2130,
            NPCs.BLACK_GUARD_2131,
            NPCs.BLACK_GUARD_2132,
            NPCs.BLACK_GUARD_2133,
            NPCs.BLACK_GUARD_BERSERKER_2134,
            NPCs.BLACK_GUARD_BERSERKER_2135,
            NPCs.BLACK_GUARD_BERSERKER_2136,
        )
    }
}
