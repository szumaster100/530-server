package content.region.misthalin.dialogue.digsite

import content.region.misthalin.quest.itexam.ExpertListener
import core.api.addItemOrDrop
import core.api.sendDialogue
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.item.Item
import org.rs.consts.Items

class ArchaeologicalExpertDialogue(
    val it: Int,
) : DialogueFile() {
    var i = ExpertListener()
    var n = NPC(i.archy)
    var itemUsed = it

    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        npc = n
        when (stage) {
            0 ->
                when (itemUsed) {
                    i.staff -> player(FaceAnim.ASKING, "What do you think of this?").also { stage = 100 }
                    i.unidentifiedLiquid -> {
                        replaceAll(i.unidentifiedLiquid, i.nitroglycerin)
                        npcl(
                            FaceAnim.WORRIED,
                            "This is a VERY dangerous liquid called nitroglycerin. Be careful how you handle it. Don't drop it or it will explode!",
                        ).also {
                            stage =
                                99
                        }
                    }
                    i.nitroglycerin ->
                        player(FaceAnim.ASKING, "Can you tell me any more about this?").also {
                            stage =
                                110
                        }
                    i.ammoniumNitrate -> player(FaceAnim.ASKING, "Have a look at this.").also { stage = 120 }
                    i.nuggets -> player(FaceAnim.ASKING, "I have these gold nuggets...").also { stage = 130 }
                    i.needle -> player(FaceAnim.NEUTRAL, "I found a needle.").also { stage = 140 }
                    i.rottenApple -> player(FaceAnim.SAD, "I found these...").also { stage = 150 }
                    i.brokenGlass -> player(FaceAnim.NEUTRAL, "Have a look at this glass.").also { stage = 160 }
                    i.brokenArrow -> player(FaceAnim.FRIENDLY, "Have a look at this arrow.").also { stage = 170 }
                    i.panningTray -> {
                        if (player!!.inventory.contains(Items.PANNING_TRAY_677, 1)) {
                            npc(FaceAnim.NEUTRAL, "I have no need for panning trays!").also { stage = 99 }
                        }
                        if (player!!.inventory.contains(Items.PANNING_TRAY_678, 1) ||
                            player!!.inventory.contains(Items.PANNING_TRAY_679, 1)
                        ) {
                            npc(FaceAnim.NEUTRAL, "Have you searched this tray yet?").also { stage = 180 }
                        }
                    }
                    i.bones -> player(FaceAnim.FRIENDLY, "Have a look at these bones.").also { stage = 190 }
                    i.buttons -> player(FaceAnim.FRIENDLY, "I found these buttons.").also { stage = 200 }
                    i.crackedSample -> player(FaceAnim.WORRIED, "I found this rock...").also { stage = 210 }
                    i.oldTooth -> player(FaceAnim.FRIENDLY, "Hey look at this.").also { stage = 220 }
                    i.rustySword -> player(FaceAnim.FRIENDLY, "I found an old sword.").also { stage = 230 }
                    i.brokenStaff -> player(FaceAnim.ASKING, "Have a look at this staff.").also { stage = 240 }
                    i.brokenArmour -> player(FaceAnim.ASKING, "I found some armour.").also { stage = 250 }
                    i.damagedArmour -> player(FaceAnim.THINKING, "I found some old armour.").also { stage = 260 }
                    i.ceramicRemains -> player(FaceAnim.FRIENDLY, "I found some pottery pieces.").also { stage = 270 }
                    i.beltBuckle -> player(FaceAnim.ASKING, "Have a look at this unusual item...").also { stage = 280 }
                    i.animalSkull -> player(FaceAnim.FRIENDLY, "Have a look at this.").also { stage = 290 }
                    i.specialCup -> player(FaceAnim.FRIENDLY, "Have a look at this.").also { stage = 300 }
                    i.teddy -> player(FaceAnim.FRIENDLY, "Have a look at this.").also { stage = 310 }
                    i.stoneTablet -> player(FaceAnim.FRIENDLY, "Have a look at this.").also { stage = 320 }
                    else ->
                        npcl(FaceAnim.FRIENDLY, "I don't think that has any archaeological significance").also {
                            stage = 99
                        }
                }
            100 ->
                npcl(
                    FaceAnim.AMAZED,
                    "That staff is incredible! It's the same symbol that was on that talisman you found here. Does this mean you've found out more about Zaros?",
                ).also {
                    stage++
                }
            101 ->
                playerl(
                    FaceAnim.FRIENDLY,
                    "I found out that he was banished, and that the people's hero was trapped in a pyramid and...",
                ).also {
                    stage++
                }
            102 ->
                npcl(
                    FaceAnim.THINKING,
                    "So you're the one who found out about that. I've heard the story from my friends in the museum. Well done on being able to wield such an impressive symbol.",
                ).also {
                    stage++
                }
            103 -> npc(FaceAnim.FRIENDLY, "Anyway....").also { stage = 99 }
            110 ->
                npcl(
                    FaceAnim.WORRIED,
                    "Nitroglycerin! This is a dangerous substance. This is normally mixed with other chemicals to produce a potent compound.",
                ).also {
                    stage++
                }
            111 ->
                npcl(
                    FaceAnim.WORRIED,
                    "Be sure not to drop it! That stuff is highly volatile...",
                ).also { stage = 99 }
            120 ->
                npcl(
                    FaceAnim.WORRIED,
                    "Really, you do find the most unusual items. I know what this is - it's a strong chemical called ammonium nitrate. Why you want this I'll never know...",
                ).also {
                    stage =
                        99
                }
            130 -> {
                if (player!!.inventory.getAmount(i.nuggets) >= 3) {
                    player!!.inventory.remove(Item(i.nuggets, 3))
                    addItemOrDrop(player!!, Items.GOLD_ORE_444, 1)
                    npcl(
                        FaceAnim.FRIENDLY,
                        "Good - that's three; I can exchange them for normal gold now. You can get this refined and make a profit!",
                    ).also {
                        stage =
                            131
                    }
                } else if (player!!.inventory.getAmount(i.nuggets) < 3) {
                    npcl(
                        FaceAnim.FRIENDLY,
                        "I can't do much with these nuggets yet. Come back when you have 3 and I will exchange them for you.",
                    ).also {
                        stage =
                            99
                    }
                }
            }
            131 -> player(FaceAnim.FRIENDLY, "Excellent!").also { stage = 99 }
            140 ->
                npcl(
                    FaceAnim.LAUGH,
                    "Hmm, yes; I wondered why this race were so well dressed! It looks like they had mastery of needlework.",
                ).also {
                    stage =
                        99
                }
            150 -> npc(FaceAnim.DISGUSTED, "Ew! Throw them away this instant!").also { stage = 99 }
            160 ->
                npcl(
                    FaceAnim.NEUTRAL,
                    "Hey you should be careful of that. It might cut your fingers, throw it away!",
                ).also {
                    stage =
                        99
                }
            170 ->
                npcl(
                    FaceAnim.AMAZED,
                    "No doubt this arrow was shot by a strong warrior - it's split in half! It is not a valuable object though.",
                ).also {
                    stage =
                        99
                }
            180 -> player(FaceAnim.THINKING, "Not that I remember...").also { stage++ }
            181 ->
                npcl(
                    FaceAnim.DISGUSTED,
                    "It may contain something; I don't want to get my hands dirty.",
                ).also { stage++ }
            182 -> sendDialogue(player!!, "The expert hands the tray back to you.").also { stage = 99 }
            190 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Ah, yes - a fine bone example... no noticeable fractures... and in good condition. These are common cow bones, however; they have no archaeological value.",
                ).also {
                    stage =
                        99
                }
            200 ->
                npcl(
                    FaceAnim.THINKING,
                    "Let's have a look. Ah, I think these are from the nobility, perhaps a royal servant?",
                ).also {
                    stage =
                        99
                }
            210 ->
                npcl(
                    FaceAnim.THINKING,
                    "What a shame it's cracked; this looks like it would have been a good sample.",
                ).also {
                    stage =
                        99
                }
            220 ->
                npcl(
                    FaceAnim.LAUGH,
                    "Oh, an old tooth. It looks like it has come from a mighty being. Pity there are no tooth fairies around here!",
                ).also {
                    stage =
                        99
                }
            230 ->
                npcl(
                    FaceAnim.THINKING,
                    "Oh, it's very rusty isn't it? I'm not sure this sword belongs here, it looks very out of place.",
                ).also {
                    stage =
                        99
                }
            240 ->
                npcl(
                    FaceAnim.HALF_THINKING,
                    "Look at this... Interesting... This appears to belong to a cleric of some kind; certainly not a follower of Saradomin, however.",
                ).also {
                    stage++
                }
            241 ->
                npcl(
                    FaceAnim.THINKING,
                    "I wonder if there was another civilization here before the Saradominists?",
                ).also {
                    stage =
                        99
                }
            250 -> npcl(FaceAnim.AMAZED, "It looks like the wearer of this fought a mighty battle.").also { stage = 99 }
            260 ->
                npcl(
                    FaceAnim.THINKING,
                    "How unusual. This armour doesn't seem to match with the other finds. Keep looking.",
                ).also {
                    stage =
                        99
                }
            270 ->
                npcl(
                    FaceAnim.FRIENDLY,
                    "Yes, many parts are discovered. The inhabitants of these parts were great potters.",
                ).also {
                    stage++
                }
            271 -> player(FaceAnim.ASKING, "You mean they were good at using potions?").also { stage++ }
            272 -> npcl(FaceAnim.LAUGH, "No, no, silly. They were known for their skill with clay.").also { stage = 99 }
            280 ->
                npcl(
                    FaceAnim.THINKING,
                    "Let me see. This is a belt buckle. Not so unusual - I should imagine it came from a guard.",
                ).also {
                    stage =
                        99
                }
            290 ->
                npcl(
                    FaceAnim.THINKING,
                    "Hmm, an interesting find; an animal skull for sure. Another student found one just like this today.",
                ).also {
                    stage =
                        99
                }
            300 ->
                npcl(
                    FaceAnim.THINKING,
                    "Looks like an award cup for some small find. Perhaps it belongs to one of the students?",
                ).also {
                    stage =
                        99
                }
            310 ->
                npcl(FaceAnim.LAUGH, "Why, it looks like a teddy bear to me. Perhaps someone's lucky mascot!").also {
                    stage =
                        99
                }
            320 -> npcl(FaceAnim.NEUTRAL, "I don't need another tablet! One is enough, thank you.").also { stage = 99 }
            99 -> end()
        }
    }

    fun replaceAll(
        originalItem: Int,
        newItem: Int,
    ) {
        for (a in 0..player!!.inventory.getAmount(originalItem)) {
            if (player!!.inventory.contains(originalItem, 1)) {
                player!!.inventory.replace(Item(newItem), player!!.inventory.getSlot(Item(originalItem)))
            }
        }
    }
}
