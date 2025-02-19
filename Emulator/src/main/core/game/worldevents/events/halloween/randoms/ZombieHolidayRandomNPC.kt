package core.game.worldevents.events.halloween.randoms

import core.api.*
import core.game.interaction.QueueStrength
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.npc.NPC
import core.game.worldevents.events.HolidayRandomEventNPC
import core.game.worldevents.events.HolidayRandoms
import core.game.worldevents.events.ResetHolidayAppearance
import core.tools.RandomFunction
import core.tools.colorize
import org.rs.consts.Animations
import org.rs.consts.NPCs
import org.rs.consts.Sounds

class ZombieHolidayRandomNPC : HolidayRandomEventNPC(NPCs.ZOMBIE_2714) {
    override fun init() {
        super.init()
        this.isAggressive = false
        playGlobalAudio(this.location, Sounds.ZOMBIE_DEATH_922)
        playJingle(player, 314)
        sendMessage(player, "A zombie crawls out of the ground beneath you...")
        animate(player, Animations.HUMAN_ZOMBIE_HAND_7272)
        queueScript(this, 2, QueueStrength.SOFT) { stage: Int ->
            when (stage) {
                0 -> {
                    when (RandomFunction.getRandom(2)) {
                        0 -> sendChat(this, "Brainnnss!")
                        1 -> sendChat(this, "RArrgghhh")
                        2 -> sendChat(this, "Flesshhh")
                    }
                    return@queueScript delayScript(this, 1)
                }

                1 -> {
                    this.face(player)
                    visualize(this, Animations.ZOMBIE_GRAB_5568, -1)
                    playGlobalAudio(this.location, Sounds.ZOMBIE_ATTACK_918)
                    if (RandomFunction.roll(2)) {
                        player.timers.registerTimer(ResetHolidayAppearance())
                        sendMessage(player, colorize("%RThe zombie bites you!"))
                        val hit = if (player.skills.lifepoints < 5) 0 else 2
                        impact(player, hit, ImpactHandler.HitsplatType.NORMAL)
                        player.appearance.transformNPC(NPCs.ZOMBIE_2866)
                    } else {
                        impact(player, 0, ImpactHandler.HitsplatType.NORMAL)
                        sendMessage(player, "The zombie tries to bite you and misses!")
                    }
                    return@queueScript delayScript(this, 8)
                }

                2 -> {
                    animate(this, Animations.ZOMBIE_DEAD_5575)
                    HolidayRandoms.terminateEventNpc(player)
                    return@queueScript stopExecuting(this)
                }

                else -> return@queueScript stopExecuting(this)
            }
        }
    }

    override fun talkTo(npc: NPC) {
    }
}
