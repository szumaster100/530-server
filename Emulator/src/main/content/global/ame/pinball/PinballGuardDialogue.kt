package content.global.ame.pinball

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.tools.BLUE

class PinballGuardDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        when (stage) {
            0 ->
                if (getVarbit(player!!, 2121) >= 10) {
                    playerl(FaceAnim.HALF_GUILTY, "So... I'm free to go now right?").also { stage++ }
                } else {
                    lock(player!!, 1000)
                    sendNPCDialogue(
                        player!!,
                        3912,
                        "You poke 10 flashing pillars, right? You NOT poke other pillars, right? Okay, you go play now.",
                        FaceAnim.OLD_NORMAL,
                    )
                    stage += 3
                }

            1 ->
                sendNPCDialogue(
                    player!!,
                    3912,
                    "Yer, get going. We get break now.",
                    FaceAnim.OLD_NORMAL,
                ).also { stage++ }
            2 ->
                end().also {
                    sendUnclosablePlainDialogue(player!!, true, "", "Congratulations - you can now leave the arena.")
                }
            3 -> {
                end()
                unlock(player!!)
                sendUnclosablePlainDialogue(player!!, true, "", "Tag the post with the " + BLUE + "flashing rings.")
            }
        }
    }
}
