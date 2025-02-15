package content.minigame.duelarena.dialogue

import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.NPCs

@Initializable
class AfrahDialogue(
    player: Player? = null,
) : Dialogue(player) {
    private val conversations = arrayOf(0, 4, 10, 11, 15, 17, 20, 22, 23, 24, 29, 32)

    override fun open(vararg args: Any): Boolean {
        player(FaceAnim.ASKING, "Hi!")
        stage = conversations.random()
        npc = args[0] as NPC
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 -> npc(FaceAnim.ASKING, "Ooh. This is exciting!").also { stage++ }
            1 -> player(FaceAnim.ASKING, "Yup!").also { stage = END_DIALOGUE }
            2 ->
                npc(
                    FaceAnim.ASKING,
                    "I wouldn't want to be the poor guy that has to",
                    "clean up after the duels.",
                ).also { stage++ }

            3 -> player(FaceAnim.ASKING, "Me neither.").also { stage = END_DIALOGUE }
            4 -> npc(FaceAnim.ASKING, "My son just won his first duel!").also { stage++ }
            5 -> player(FaceAnim.ASKING, "Congratulations!").also { stage++ }
            6 -> npc(FaceAnim.ASKING, "He ripped his opponent in half!").also { stage++ }
            7 -> player(FaceAnim.ASKING, "That's gotta hurt!").also { stage++ }
            8 -> npc(FaceAnim.ASKING, "He's only 10 as well!").also { stage++ }
            9 -> player(FaceAnim.ASKING, "You gotta start 'em young!").also { stage = END_DIALOGUE }
            10 -> npc(FaceAnim.ASKING, "Hmph.").also { stage = END_DIALOGUE }
            11 -> npc(FaceAnim.ASKING, "My favourite fighter is Mubariz!").also { stage++ }
            12 -> player(FaceAnim.ASKING, "The guy at the information kiosk?").also { stage++ }
            13 -> npc(FaceAnim.ASKING, "Yeah! He rocks!").also { stage++ }
            14 -> player(FaceAnim.ASKING, "Takes all sorts, I guess.").also { stage = END_DIALOGUE }
            15 -> npc(FaceAnim.ASKING, "Hi! I'm here to watch the duels!").also { stage++ }
            16 -> player(FaceAnim.ASKING, "Me too!").also { stage = END_DIALOGUE }
            17 -> npc(FaceAnim.ASKING, "Did you know they think this place dates", "back to the second age?!")
            18 -> player(FaceAnim.ASKING, "Really?").also { stage++ }
            19 ->
                npc(FaceAnim.ASKING, "Yeah. The guy at the information kiosk was telling me.").also {
                    stage = END_DIALOGUE
                }

            20 -> npc(FaceAnim.ANGRY, "Can't you see I'm watching the duels?").also { stage++ }
            21 -> player(FaceAnim.SAD, "I'm sorry!").also { stage = END_DIALOGUE }
            22 -> npc(FaceAnim.ASKING, "Well. This beats doing the shopping!").also { stage = END_DIALOGUE }
            23 -> npc(FaceAnim.ASKING, "Hi!").also { stage = END_DIALOGUE }
            24 -> npc(FaceAnim.ASKING, "Knock knock!").also { stage++ }
            25 -> player(FaceAnim.ASKING, "Who's there?").also { stage++ }
            26 -> npc(FaceAnim.ASKING, "Boo!").also { stage++ }
            27 -> player(FaceAnim.ASKING, "Boo who?").also { stage++ }
            28 -> npc(FaceAnim.LAUGH, "Don't cry, it's just me!").also { stage = END_DIALOGUE }
            29 -> npc(FaceAnim.ASKING, "Why did the skeleton burp?").also { stage++ }
            30 -> player(FaceAnim.ASKING, "I don't know?").also { stage++ }
            31 -> npc(FaceAnim.ASKING, "'Cause it didn't have the guts to fart!").also { stage = END_DIALOGUE }
            32 -> npc(FaceAnim.ASKING, "Waaaaassssssuuuuupp?!.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.AFRAH_968)
    }
}
