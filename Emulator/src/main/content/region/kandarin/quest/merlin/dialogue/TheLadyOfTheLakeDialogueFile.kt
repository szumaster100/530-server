package content.region.kandarin.quest.merlin.dialogue

import core.api.addItemOrDrop
import core.api.removeItem
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.IfTopic
import core.game.dialogue.Topic
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.link.diary.DiaryType
import core.game.node.item.Item
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs

class TheLadyOfTheLakeDialogueFile : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.THE_LADY_OF_THE_LAKE_250)
        var missingSword = !player!!.hasItem(Item(Items.EXCALIBUR_35, 1))

        when (stage) {
            0 -> {
                if (player!!.achievementDiaryManager.getDiary(DiaryType.SEERS_VILLAGE)!!.isComplete(2) &&
                    player!!.equipment.contains(14631, 1) &&
                    player!!.equipment.contains(35, 1)
                ) {
                    npcl(FaceAnim.HAPPY, "I am the Lady of the Lake.")
                    stage = 110
                } else {
                    npc("Good day to you, " + (if (player!!.isMale) "sir" else "madam") + ".")
                    stage = 1
                }
            }

            110 -> player("And I'm-").also { stage++ }
            111 ->
                npc(
                    "You're " + player!!.username + ". And I see from the sign you",
                    "wear that you have earned the trust of Sir Kay.",
                ).also {
                    stage++
                }
            112 -> player("It was nothing.. really...").also { stage++ }
            113 -> npc("You shall be rewarded handsomely!").also { stage++ }
            114 -> {
                interpreter!!
                    .sendItemMessage(
                        Items.EXCALIBUR_35,
                        "The Lady of the Lake reaches out and touches the",
                        "blade Excalibur which seems to vibrate with new power.",
                    ).also {
                        if (removeItem(player!!, Items.EXCALIBUR_35)) {
                            addItemOrDrop(player!!, Items.ENHANCED_EXCALIBUR_14632)
                        } else if (player!!.equipment.containsAtLeastOneItem(Items.EXCALIBUR_35)) {
                            player!!.equipment.remove(Item(Items.EXCALIBUR_35))
                            player!!.equipment.add(Item(Items.ENHANCED_EXCALIBUR_14632), true, false)
                        }
                        stage++
                    }
            }

            115 -> player("What does this do then?").also { stage++ }
            116 ->
                npc(
                    "I made the blade more powerful, and also gave it a",
                    "rather healthy effect when you use the special.",
                ).also {
                    stage++
                }

            117 -> player("Thanks!").also { stage = END_DIALOGUE }

            1 ->
                showTopics(
                    Topic(FaceAnim.NEUTRAL, "Who are you?", 2),
                    Topic(FaceAnim.NEUTRAL, "Good day.", 5),
                    IfTopic(FaceAnim.NEUTRAL, "I seek the sword Excalibur.", 50, missingSword),
                )

            2 -> npcl(FaceAnim.NEUTRAL, "I am the Lady of the Lake.").also { stage = END_DIALOGUE }
            5 ->
                npcl(FaceAnim.NEUTRAL, "Good day to you ${if (player!!.isMale) "sir" else "madam"}.").also {
                    stage = END_DIALOGUE
                }

            50 ->
                npc(
                    "... But you have already proved thyself to be worthy",
                    "of wielding it once already. I shall return it to you",
                    "if you can prove yourself to still be worthy.",
                ).also {
                    stage++
                }

            51 -> player("... And how can I do that?").also { stage++ }
            52 -> npc("Why, by proving yourself to be above material goods.").also { stage++ }
            53 -> npc("500 coins ought to do it.").also { stage++ }
            54 ->
                if (player!!.inventory.contains(Items.COINS_995, 500)) {
                    player("Ok, here you go.").also { stage = 56 }
                } else {
                    player("I don't have that kind of money...").also { stage = 55 }
                }

            55 -> npc("Well, come back when you do.").also { stage = END_DIALOGUE }
            56 ->
                if (player!!.inventory.freeSlots() == 0) {
                    player("Sorry, I don't seem to have enough inventory space.").also { stage = END_DIALOGUE }
                } else {
                    player!!.inventory.remove(Item(Items.COINS_995, 500))
                    player!!.inventory.add(Item(Items.EXCALIBUR_35, 1))
                    npc(
                        "You are still worthy to wield Excalibur! And thanks",
                        "for the cash! I felt like getting a new haircut!",
                    )
                    stage++
                }

            57 -> interpreter!!.sendDialogue("The lady of the Lake hands you Excalibur.").also { stage = END_DIALOGUE }
        }
    }
}
