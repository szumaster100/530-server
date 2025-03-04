package core.game.worldevents.events.christmas.randoms

import core.api.addItemOrDrop
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.worldevents.events.HolidayRandoms
import org.rs.consts.Items

class CookHolidayRandomDialogue : DialogueFile() {
    private val cakes = listOf(Items.CAKE_1891, Items.CHOCOLATE_CAKE_1897, Items.MINT_CAKE_9475)

    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        if (HolidayRandoms.getEventNpc(player!!) == null) {
            player!!.dialogueInterpreter.close()
        }

        when (stage) {
            0 ->
                npcl(
                    FaceAnim.HAPPY,
                    "Greetings, ${player!!.username}. Every year around christmas I give away cake to the community. Would you care for a fresh baked cake?",
                ).also {
                    stage++
                }
            1 -> options("Sure, I will take a cake.", "Why are you giving cakes away?", "No, thanks.").also { stage++ }
            2 ->
                when (buttonID) {
                    1 -> playerl(FaceAnim.HAPPY, "Sure, I will take a cake.").also { stage = 40 }
                    2 -> playerl(FaceAnim.HALF_ASKING, "Why are you giving away cakes?").also { stage = 20 }
                    3 -> playerl(FaceAnim.NEUTRAL, "No, thanks.").also { stage = 10 }
                }
            10 ->
                npcl(
                    FaceAnim.HAPPY,
                    "Okay, I will find someone else who wants it. Merry Christmas, ${player!!.username}.",
                ).also {
                    stage++
                }
            11 -> {
                HolidayRandoms.terminateEventNpc(player!!)
                end()
            }

            20 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "It's just something I do this time of year... Errr... well...",
                ).also { stage++ }
            21 -> playerl(FaceAnim.HALF_ASKING, "Is there something else?").also { stage++ }
            22 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "I used to be the cook for the old Duke of Lumbridge. Visiting dignitaries praised me for my fine banquets!",
                ).also {
                    stage++
                }
            23 ->
                npcl(
                    FaceAnim.ANNOYED,
                    "But then someone found a rule that said that only one family could hold that post.",
                ).also {
                    stage++
                }
            24 ->
                npcl(
                    FaceAnim.ANGRY,
                    "Overnight I was fired and replaced by some fool who can't even bake a cake without help!",
                ).also {
                    stage++
                }
            25 ->
                playerl(
                    FaceAnim.NEUTRAL,
                    "I'm sorry to hear that. It sounds as if you are a great cook!",
                ).also { stage++ }
            26 -> npcl(FaceAnim.HAPPY, "Thanks, I am!").also { stage++ }
            27 ->
                npcl(
                    FaceAnim.LAUGH,
                    "I also do this each year to show everyone how incapable that sorry excuse of a cook they replaced me with is.",
                ).also {
                    stage++
                }
            28 -> npcl(FaceAnim.HAPPY, "So, did you want a cake?").also { stage++ }
            29 -> options("Sure, I will take a cake.", "No, thanks.").also { stage++ }
            30 ->
                when (buttonID) {
                    1 -> playerl(FaceAnim.HAPPY, "Sure, I will take a cake.").also { stage = 40 }
                    2 -> playerl(FaceAnim.NEUTRAL, "No, thanks.").also { stage = 10 }
                }
            40 ->
                npcl(
                    FaceAnim.HAPPY,
                    "Here is a the finest cake you will ever have! Merry Christmas, ${player!!.username}.",
                ).also {
                    stage++
                }
            41 -> {
                HolidayRandoms.terminateEventNpc(player!!)
                addItemOrDrop(player!!, cakes.random())
                end()
            }
        }
    }
}
