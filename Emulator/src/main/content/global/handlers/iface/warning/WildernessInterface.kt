package content.global.handlers.iface.warning

import org.rs.consts.Animations
import org.rs.consts.Components
import org.rs.consts.Sounds
import core.api.forceMove
import core.api.playAudio
import core.cache.def.impl.SceneryDefinition
import core.game.component.Component
import core.game.global.action.DoorActionHandler
import core.game.interaction.InterfaceListener
import core.game.interaction.MovementPulse
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.impl.PulseType
import core.game.node.entity.player.Player
import core.game.node.scenery.Scenery
import core.game.world.map.Location
import core.plugin.Initializable
import core.plugin.Plugin

class WildernessInterface : InterfaceListener {

    override fun defineInterfaceListeners() {

        on(Components.WILDERNESS_WARNING_382) { player, component, _, buttonID, _, _ ->
            when {
                buttonID == 18 && player.getAttribute<Scenery>("wildy_ditch") != null -> {
                    player.interfaceManager.close()
                    handleDitch(player)
                }

                buttonID == 18 && player.getAttribute<Scenery>("wildy_gate") != null -> {
                    player.interfaceManager.close()
                    handleGate(player)
                }

                buttonID == 28 -> WarningManager.toggle(player, component.id)
            }

            WarningManager.increment(player, component.id)
            return@on true
        }
    }

    internal fun handleDitch(player: Player) {
        val ditch = player.getAttribute<Scenery>("wildy_ditch") ?: return
        player.removeAttribute("wildy_ditch")

        val l = ditch.location
        val x = player.location.x
        val y = player.location.y

        val (start, end) = if (ditch.rotation % 2 == 0) {
            if (y <= l.y) Location.create(x, l.y - 1, 0) to Location.create(x, l.y + 2, 0)
            else Location.create(x, l.y + 2, 0) to Location.create(x, l.y - 1, 0)
        } else {
            if (x > l.x) Location.create(l.x + 2, y, 0) to Location.create(l.x - 1, y, 0)
            else Location.create(l.x - 1, y, 0) to Location.create(l.x + 2, y, 0)
        }

        forceMove(player, start, end, 0, 60, null, Animations.JUMP_OVER_OBSTACLE_6132)
        playAudio(player, Sounds.JUMP2_2462, 30)
    }

    private fun handleGate(player: Player) {
        val gate = player.getAttribute<Scenery>("wildy_gate") ?: return
        player.removeAttribute("wildy_gate")
        DoorActionHandler.handleAutowalkDoor(player, gate)
    }
}

@Initializable
class WildernessDitchPlugin : OptionHandler() {

    override fun newInstance(arg: Any?): Plugin<Any> {
        SceneryDefinition.forId(23271).handlers["option:cross"] = this
        return this
    }

    override fun handle(player: Player, node: Node, option: String): Boolean {
        if (player.location.getDistance(node.location) < 3) {
            handleDitch(player, node)
        } else {
            player.pulseManager.run(
                object : MovementPulse(player, node) {
                    override fun pulse(): Boolean {
                        handleDitch(player, node)
                        return true
                    }
                },
                PulseType.STANDARD
            )
        }
        return true
    }

    private fun handleDitch(player: Player, node: Node) {
        player.faceLocation(node.location)
        val ditch = node as? Scenery ?: return
        player.setAttribute("wildy_ditch", ditch)

        if (!player.isArtificial) {
            val shouldWarn = (ditch.rotation % 2 == 0 && player.location.y <= node.location.y) ||
                    (ditch.rotation % 2 != 0 && player.location.x > node.location.x)

            if (shouldWarn) {
                if (WarningManager.check(Components.WILDERNESS_WARNING_382)) {
                    WildernessInterface().handleDitch(player)
                } else {
                    player.interfaceManager.open(Component(Components.WILDERNESS_WARNING_382))
                }
                return
            }
        }

        WildernessInterface().handleDitch(player)
    }

    override fun isWalk(): Boolean = true
}