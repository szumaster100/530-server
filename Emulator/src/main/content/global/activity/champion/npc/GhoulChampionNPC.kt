package content.global.activity.champion.npc

import content.global.activity.champion.ChallengeListener
import core.api.*
import core.game.container.impl.EquipmentContainer
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.system.config.ItemConfigParser
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.map.Location
import core.plugin.Initializable
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Vars

@Initializable
class GhoulChampionNPC(
    id: Int = 0,
    location: Location? = null,
) : AbstractNPC(id, location) {
    var clearTime = 0

    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any,
    ): AbstractNPC {
        return GhoulChampionNPC(id, location)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.GHOUL_CHAMPION_3059)
    }

    override fun handleTickActions() {
        super.handleTickActions()
        if (clearTime++ > 288) poofClear(this)
    }

    companion object {
        @JvmStatic
        fun spawnGhoulChampion(player: Player) {
            val champion = GhoulChampionNPC(NPCs.GHOUL_CHAMPION_3059)
            champion.location = location(3170, 9758, 0)
            champion.isWalks = true
            champion.isAggressive = true
            champion.isActive = false

            if (champion.asNpc() != null && champion.isActive) {
                champion.properties.teleportLocation = champion.properties.spawnLocation
            }
            champion.isActive = true
            GameWorld.Pulser.submit(
                object : Pulse(0, champion) {
                    override fun pulse(): Boolean {
                        champion.init()
                        registerHintIcon(player, champion)
                        champion.attack(player)
                        return true
                    }
                },
            )
        }
    }

    override fun checkImpact(state: BattleState) {
        super.checkImpact(state)
        val player = state.attacker
        if (player is Player) {
            val i: Item = player.equipment[EquipmentContainer.SLOT_WEAPON].id.asItem()
            if (i.definition.getConfiguration(
                    ItemConfigParser.TWO_HANDED,
                    false,
                ) == true &&
                state.style == CombatStyle.MELEE
            ) {
                state.neutralizeHits()
                state.estimatedHit = state.maximumHit
            }
            if (state.style == CombatStyle.MAGIC) {
                sendMessage(player, "You can use only weapons in this challenge.")
                if (state.estimatedHit > -1) {
                    state.estimatedHit = 0
                    return
                }
                if (state.secondaryHit > -1) {
                    state.secondaryHit = 0
                    return
                }
            }
        }
    }

    override fun finalizeDeath(killer: Entity?) {
        if (killer is Player) {
            lock(killer, 2)
            runTask(killer, 1) {
                openInterface(killer, Components.CHAMPIONS_SCROLL_63)
                sendString(killer, "Well done, you defeated the Ghoul Champion!", Components.CHAMPIONS_SCROLL_63, 2)
                sendItemZoomOnInterface(killer, Components.CHAMPIONS_SCROLL_63, 3, Items.CHAMPION_SCROLL_6799, 260)
                sendString(killer, "400 Slayer Xp", Components.CHAMPIONS_SCROLL_63, 6)
                sendString(killer, "400 Hitpoint Xp", Components.CHAMPIONS_SCROLL_63, 7)
            }
            setVarbit(killer, Vars.VARBIT_SCENERY_CHAMPIONS_CHALLENGE_GHOUL_BANNER_1453, 1, true)
            rewardXP(killer, Skills.HITPOINTS, 400.0)
            rewardXP(killer, Skills.SLAYER, 400.0)
            removeAttribute("championsarena:start")
            clearHintIcon(killer)
            ChallengeListener.isFinalBattle(killer)
        }
        clear()
        super.finalizeDeath(killer)
    }
}
