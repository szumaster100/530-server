package content.region.karamja.quest.totem

import content.global.travel.EssenceTeleport
import core.api.playAudio
import core.api.quest.isQuestComplete
import core.api.quest.isQuestInProgress
import core.api.quest.setQuestStage
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.impl.Projectile
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.plugin.Initializable
import org.rs.consts.Animations
import org.rs.consts.Quests
import org.rs.consts.Sounds

@Initializable
class CrompertyDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        npcl(
            FaceAnim.HAPPY,
            "Hello ${player.name}, I'm Cromperty. Sedridor has told me about you. As a wizard and an inventor he has aided me in my great invention!",
        )
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 ->
                options(
                    "Two jobs? that's got to be tough",
                    "So what have you invented?",
                    "Can you teleport me to the Rune Essence?",
                ).also {
                    stage++
                }

            1 ->
                when (buttonId) {
                    1 -> playerl(FaceAnim.HAPPY, "Two jobs? That's got to be tough.").also { stage = 5 }
                    2 -> playerl(FaceAnim.ASKING, "So, what have you invented?").also { stage = 10 }
                    3 ->
                        playerl(FaceAnim.HAPPY, "Can you teleport me to the Rune Essence?").also {
                            if (isQuestComplete(player, Quests.RUNE_MYSTERIES)) {
                                EssenceTeleport.teleport(npc!!, player)
                            } else {
                                stage = 2
                            }
                        }
                }

            2 -> npcl(FaceAnim.THINKING, "I have no idea what you're talking about.").also { stage = 1000 }
            5 -> npcl(FaceAnim.HAPPY, "Not when you combine them it isn't! Invent MAGIC things!").also { stage++ }
            6 -> options("So what have you invented?", "Well I shall leave you to your inventing").also { stage++ }
            7 ->
                when (buttonId) {
                    1 -> playerl(FaceAnim.ASKING, "So, what have you invented?").also { stage = 10 }
                    2 -> playerl(FaceAnim.HAPPY, "Well I shall leave you to your inventing").also { stage++ }
                }

            8 -> npcl(FaceAnim.HAPPY, "Thanks for dropping by! Stop again anytime!").also { stage = 1000 }
            10 ->
                npcl(
                    FaceAnim.HAPPY,
                    "Ah! My latest invention is my patent pending teleportation block! It emits a low level magical signal,",
                ).also {
                    stage++
                }

            11 ->
                npcl(
                    FaceAnim.HAPPY,
                    "that will allow me to locate it anywhere in the world, and teleport anything directly to it! I hope to revolutionize the entire teleportation system!",
                ).also {
                    stage++
                }

            12 -> npcl(FaceAnim.HAPPY, "Don't you think I'm great? Uh, I mean it's great?").also { stage++ }
            13 -> options("So where is the other block?", "Can I be teleported please?").also { stage++ }
            14 ->
                when (buttonId) {
                    1 -> playerl(FaceAnim.ASKING, "So where is the other block?").also { stage = 15 }
                    2 -> playerl(FaceAnim.ASKING, "Can I be teleported please?").also { stage = 25 }
                }

            15 ->
                npcl(
                    FaceAnim.THINKING,
                    "Well...Hmm. I would guess somewhere between here and the Wizards' Tower in Misthalin.",
                ).also {
                    stage++
                }

            16 ->
                npcl(
                    FaceAnim.HAPPY,
                    "All I know is that it hasn't got there yet as the wizards there would have contacted me.",
                ).also {
                    stage++
                }

            17 ->
                npcl(
                    FaceAnim.THINKING,
                    "I'm using the GPDT for delivery. They assured me it would be delivered promptly.",
                ).also {
                    stage++
                }

            18 -> playerl(FaceAnim.ASKING, "Who are the GPDT?").also { stage++ }
            19 ->
                npcl(
                    FaceAnim.HAPPY,
                    "The Gielinor Parcel Delivery Team. They come very highly recommended.",
                ).also { stage++ }

            20 ->
                npcl(
                    FaceAnim.HAPPY,
                    "Their motto is: \"We aim to deliver your stuff at some point after you have paid us!\"",
                ).also {
                    stage =
                        1000
                }

            25 ->
                npcl(
                    FaceAnim.HAPPY,
                    "By all means! I'm afraid I can't give you any specifics as to where you will come out however. Presumably wherever the other block is located.",
                ).also {
                    stage++
                }

            26 ->
                options(
                    "Yes, that sounds good. Teleport me!",
                    "That sounds dangerous. Leave me here.",
                ).also { stage++ }

            27 ->
                when (buttonId) {
                    1 -> playerl(FaceAnim.HAPPY, "Yes, that sounds good. Teleport me!").also { stage = 30 }
                    2 -> playerl(FaceAnim.THINKING, "That sounds dangerous. Leave me here.").also { stage++ }
                }

            28 -> npcl(FaceAnim.HAPPY, "As you wish.").also { stage = 1000 }
            30 -> npcl(FaceAnim.HAPPY, "Okey dokey! Ready?").also { stage++ }
            31 -> {
                if (isQuestInProgress(player, Quests.TRIBAL_TOTEM, 1, 49)) {
                    npcl(FaceAnim.HAPPY, "Okay, I got a signal. Get ready!").also { stage = 36 }
                } else {
                    npcl(FaceAnim.THINKING, "Hmmm... that's odd... I can't seem to get a signal...").also { stage++ }
                }
            }

            32 -> playerl(FaceAnim.SAD, "Oh well, never mind.").also { stage = 1000 }

            36 -> {
                TribalTotemTeleport(player, npc)
                end()
            }

            1000 -> end()
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return CrompertyDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(844)
    }

    fun TribalTotemTeleport(
        player: Player,
        npc: NPC,
    ) {
        val LOCATIONS = arrayOf(Location.create(2649, 3272, 0), Location.create(2642, 3321, 0))
        npc.animate(Animation(Animations.ATTACK_437))
        npc.faceTemporary(player, 1)
        npc.graphics(Graphics(org.rs.consts.Graphics.CURSE_CAST_108))
        player.lock()
        playAudio(player, Sounds.CURSE_ALL_125, 0, 1)
        Projectile.create(npc, player, org.rs.consts.Graphics.CURSE_PROJECTILE_109).send()
        npc.sendChat("Dipsolum sententa sententi!")
        GameWorld.Pulser.submit(
            object : Pulse(1) {
                var counter = 0
                var delivered = player.questRepository.getStage(Quests.TRIBAL_TOTEM) >= 25

                override fun pulse(): Boolean {
                    when (counter++) {
                        2 -> {
                            if (delivered) {
                                setQuestStage(player, Quests.TRIBAL_TOTEM, 30)
                                player.properties.teleportLocation = LOCATIONS[1]
                            } else {
                                player.properties.teleportLocation = LOCATIONS[0]
                            }
                        }

                        3 -> {
                            player.graphics(Graphics(org.rs.consts.Graphics.CURSE_IMPACT_110))
                            player.unlock()
                            return true
                        }
                    }
                    return false
                }
            },
        )
    }
}
