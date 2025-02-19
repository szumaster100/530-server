package content.region.fremennik.dialogue.jatizso

import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs

@Initializable
class MinerDialogue(
    player: Player? = null,
) : Dialogue(player) {
    val stages = intArrayOf(0, 100, 200, 300, 400, 500)

    override fun open(vararg args: Any): Boolean {
        stage = stages.random()
        handle(0, 0)
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 ->
                npcl(
                    FaceAnim.SAD,
                    "I've been here for two days straight. When I close my eyes, I see rocks.",
                ).also { stage++ }
            1 -> playerl(FaceAnim.HALF_ASKING, "Why would anyone stay here for so long?").also { stage++ }
            2 -> npcl(FaceAnim.NEUTRAL, "I fear the outside. I fear the big light.").also { stage++ }
            3 ->
                playerl(FaceAnim.HALF_GUILTY, "Oh dear. Being underground for so long may have driven you mad.").also {
                    stage =
                        END_DIALOGUE
                }
            100 -> npcl(FaceAnim.NEUTRAL, "My back is killing me!").also { stage++ }
            101 -> playerl(FaceAnim.HALF_GUILTY, "Could be worse, it could be the trolls killing you.").also { stage++ }
            102 -> npcl(FaceAnim.NEUTRAL, "How very droll.").also { stage++ }
            103 -> playerl(FaceAnim.NEUTRAL, "No, troll.").also { stage++ }
            104 -> npcl(FaceAnim.SAD, "Bah! I resist your attempts at jollity.").also { stage = END_DIALOGUE }
            200 -> npcl(FaceAnim.SCARED, "Gah! Trolls everywhere. There's no escape!").also { stage++ }
            201 -> playerl(FaceAnim.HALF_THINKING, "You could just leave the mine.").also { stage++ }
            202 -> npcl(FaceAnim.NEUTRAL, "Oh, I'd never thought of that.").also { stage = END_DIALOGUE }
            300 -> playerl(FaceAnim.HALF_ASKING, "How's your prospecting going?").also { stage++ }
            301 -> npcl(FaceAnim.LAUGH, "Mine's been going pretty well.").also { stage++ }
            302 -> playerl(FaceAnim.HALF_THINKING, "...").also { stage++ }
            303 -> npcl(FaceAnim.LAUGH, "Mine? Mine...you get it?").also { stage++ }
            304 -> playerl(FaceAnim.HALF_THINKING, "Oh, I got it. That doesn't make it funny though.").also { stage++ }
            305 -> npcl(FaceAnim.NEUTRAL, "Suit yourself.").also { stage = END_DIALOGUE }
            400 -> npcl(FaceAnim.NEUTRAL, "High hoe, High hoe!").also { stage++ }
            401 ->
                playerl(
                    FaceAnim.ASKING,
                    "Why are you singing about farming implements at altitude?",
                ).also { stage++ }
            402 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "I don't know, I've never thought about it. Ask my Dad, he taught me the song.",
                ).also {
                    stage =
                        END_DIALOGUE
                }
            500 -> npcl(FaceAnim.NEUTRAL, "This place rocks!").also { stage++ }
            501 -> playerl(FaceAnim.HALF_THINKING, "No it doesn't, it stays perfectly still.").also { stage++ }
            502 -> npcl(FaceAnim.HALF_THINKING, "Bah! Be quiet, pedant.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return MinerDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.MINER_5497, NPCs.MINER_5498)
    }
}
