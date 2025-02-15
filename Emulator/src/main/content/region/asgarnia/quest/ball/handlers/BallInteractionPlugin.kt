package content.region.asgarnia.quest.ball.handlers

import content.minigame.mta.TelekineticGrabSpell
import content.region.asgarnia.quest.ball.handlers.npc.WitchExperimentNPC
import core.api.getAttribute
import core.api.sendMessage
import core.api.setAttribute
import core.game.interaction.MovementPulse
import core.game.interaction.Option
import core.game.interaction.PluginInteraction
import core.game.interaction.PluginInteractionManager
import core.game.node.entity.combat.spell.SpellBlocks.register
import core.game.node.entity.impl.PulseType
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.map.Location
import core.plugin.Initializable
import core.plugin.Plugin
import core.tools.RED
import org.rs.consts.Items

@Initializable
class BallInteractionPlugin : PluginInteraction() {
    @Throws(Throwable::class)
    override fun newInstance(arg: Any?): Plugin<Any> {
        register(TelekineticGrabSpell.SPELL_ID, Item(Items.BALL_2407))
        setIds(intArrayOf(Items.BALL_2407))
        PluginInteractionManager.register(this, PluginInteractionManager.InteractionType.ITEM)
        return this
    }

    override fun handle(
        player: Player,
        item: Item,
        option: Option,
    ): Boolean {
        if (option.name.equals("take", ignoreCase = true)) {
            player.pulseManager.run(
                object : MovementPulse(player, item.location.transform(0, -1, 0)) {
                    override fun pulse(): Boolean {
                        return true
                    }
                },
                PulseType.STANDARD,
            )
            handleBall(player)
        }
        return true
    }

    fun handleBall(player: Player) {
        if (player.getAttribute("witchs_house:experiment_killed", false)) {
            if (player.inventory.containsItem(Item(2407))) {
                player.sendMessage("You already have the ball.")
            } else {
                if (getAttribute(player, "witchs-experiment:npc_spawned", false)) {
                    player.sendMessage("Finish fighting the experiment first!")
                    return
                }
                val skillsToDecrease =
                    intArrayOf(Skills.DEFENCE, Skills.ATTACK, Skills.STRENGTH, Skills.RANGE, Skills.MAGIC)
                for (i in skillsToDecrease.indices) {
                    player
                        .getSkills()
                        .setLevel(i, if (player.getSkills().getLevel(i) > 5) player.getSkills().getLevel(i) - 5 else 1)
                }
                sendMessage(player, RED + "The experiment glares at you, and you feel yourself weaken.</col>")
                WitchExperimentNPC(
                    player.getAttribute("witchs_house:experiment_id", 897),
                    Location.create(2936, 3463, 0),
                    player,
                ).init()
                setAttribute(player, "witchs-experiment:npc_spawned", true)
            }
        }
    }

    override fun fireEvent(
        identifier: String,
        vararg args: Any,
    ): Any? {
        return null
    }
}
