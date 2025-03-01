package content.region.fremennik.quest.horror.dialogue

import core.api.addItemOrDrop
import core.api.removeItem
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import org.rs.consts.Items
import org.rs.consts.NPCs

class JossikRewardDialogue : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = NPC(NPCs.JOSSIK_1334)
        when (stage) {
            0 -> playerl(FaceAnim.FRIENDLY, "I see you managed to escape from those monsters intact!").also { stage++ }
            1 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "It seems I was not as injured as I thought I was after all! I must thank you for all of your help!",
                ).also {
                    stage++
                }
            2 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Now, about that casket you found on that monster's corpse...",
                ).also { stage++ }
            3 ->
                playerl(
                    FaceAnim.FRIENDLY,
                    "I have it here. You said you might be able to tell me something about it...?",
                ).also {
                    stage++
                }
            4 -> npcl(FaceAnim.FRIENDLY, "I can indeed! Here, let me have a closer look...").also { stage++ }
            5 -> npcl(FaceAnim.FRIENDLY, "Yes! There is something written on it!").also { stage++ }
            6 -> npcl(FaceAnim.FRIENDLY, "It is very faint however... Can you read it?").also { stage++ }
            7 -> options("Saradomin", "Zamorak", "Guthix").also { stage++ }
            8 ->
                when (buttonID) {
                    1 -> playerl(FaceAnim.FRIENDLY, "I think it says... Saradomin...").also { stage = 10 }
                    2 -> playerl(FaceAnim.FRIENDLY, "I think it says... Zamorak...").also { stage = 17 }
                    3 -> playerl(FaceAnim.FRIENDLY, "I think it says... Guthix...").also { stage = 24 }
                }
            10 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Are you sure? I mean, are you REALLY sure?",
                    "Maybe you'd better look again...",
                ).also {
                    stage++
                }
            11 -> options("Saradomin", "Zamorak", "Guthix").also { stage++ }
            12 ->
                when (buttonID) {
                    1 -> playerl(FaceAnim.FRIENDLY, "Nope, it definitely says Saradomin.").also { stage = 14 }
                    2 -> playerl(FaceAnim.FRIENDLY, "I think it says... Zamorak...").also { stage = 17 }
                    3 -> playerl(FaceAnim.FRIENDLY, "I think it says... Guthix...").also { stage = 24 }
                }
            14 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "I think you're right! Hand it over, and let's see what's inside!",
                ).also { stage++ }
            15 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Wow! It's an Holy Book of Saradomin! I thought these things had all vanished! Well, it's all yours, I hope you appreciate it.",
                ).also {
                    stage =
                        32
                }
            32 -> {
                end()
                if (removeItem(player!!, Items.RUSTY_CASKET_3849)) {
                    addItemOrDrop(player!!, Items.DAMAGED_BOOK_3839)
                }
            }
            17 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Are you sure? I mean, are you REALLY sure?",
                    "Maybe you'd better look again...",
                ).also {
                    stage++
                }
            18 -> options("Saradomin", "Zamorak", "Guthix").also { stage++ }
            19 ->
                when (buttonID) {
                    1 -> playerl(FaceAnim.FRIENDLY, "I think it says... Saradomin...").also { stage = 10 }
                    2 -> playerl(FaceAnim.FRIENDLY, "Nope, it definitely says Zamorak.").also { stage = 21 }
                    3 -> playerl(FaceAnim.FRIENDLY, "I think it says... Guthix...").also { stage = 24 }
                }
            21 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "I think you're right! Hand it over, and let's see what's inside!",
                ).also { stage++ }
            22 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Wow! It's an Unholy Book of Zamorak! I thought these things had all vanished! Well, it's all yours, I hope you appreciate it.",
                ).also {
                    stage =
                        30
                }
            30 -> {
                end()
                if (removeItem(player!!, Items.RUSTY_CASKET_3849)) {
                    addItemOrDrop(player!!, Items.DAMAGED_BOOK_3841)
                }
            }
            24 ->
                npc(
                    FaceAnim.FRIENDLY,
                    "Are you sure? I mean, are you REALLY sure?",
                    "Maybe you'd better look again...",
                ).also {
                    stage++
                }
            25 -> options("Saradomin", "Zamorak", "Guthix").also { stage++ }
            26 ->
                when (buttonID) {
                    1 -> playerl(FaceAnim.FRIENDLY, "I think it says... Saradomin...").also { stage = 10 }
                    2 -> playerl(FaceAnim.FRIENDLY, "I think it says... Zamorak...").also { stage = 17 }
                    3 -> playerl(FaceAnim.FRIENDLY, "Nope, it definitely says Guthix.").also { stage = 28 }
                }
            28 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "I think you're right! Hand it over, and let's see what's inside!",
                ).also { stage++ }
            29 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Wow! It's an Balance Book of Guthix! I thought these things had all vanished! Well, it's all yours, I hope you appreciate it.",
                ).also {
                    stage =
                        31
                }
            31 -> {
                end()
                if (removeItem(player!!, Items.RUSTY_CASKET_3849)) {
                    addItemOrDrop(player!!, Items.DAMAGED_BOOK_3843)
                }
            }
        }
    }
}
