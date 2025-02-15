package content.global.handlers.iface.warning

import core.api.getVarbit
import core.api.sendMessage
import core.api.setVarbit
import core.api.ui.sendInterfaceConfig
import core.game.node.entity.player.Player
import org.rs.consts.Components

object WarningManager {
    private val warningStates = mutableMapOf<Warnings, Boolean>()

    init {
        Warnings.values().forEach { warning ->
            warningStates[warning] = false
        }
    }

    @JvmStatic
    fun turn(
        player: Player,
        component: Int,
    ): Boolean {
        val warning = Warnings.values.find { it.component == component } ?: return false
        val currentVarbit = getVarbit(player, warning.varbit)
        return currentVarbit == 7
    }

    @JvmStatic
    fun toggle(
        player: Player,
        component: Int,
    ) {
        val warning = Warnings.values.find { it.component == component } ?: return
        val currentState = warningStates[warning] ?: false
        val newState = !currentState
        warningStates[warning] = newState
        val varbitId = if (newState) 7 else 6
        setVarbit(player, warning.varbit, varbitId, true)
        sendMessage(
            player,
            "You have toggled this warning screen " + (if (newState) "off" else "on") + ". You will " +
                (if (!newState) "see this interface again." else "no longer see this warning screen."),
        )
    }

    @JvmStatic
    fun check(component: Int): Boolean {
        val warning = Warnings.values.find { it.component == component } ?: return false
        return warningStates[warning] ?: false
    }

    @JvmStatic
    fun increment(
        player: Player,
        warningId: Int,
    ) {
        val warning = Warnings.values.find { it.component == warningId } ?: return
        val tries = getVarbit(player, warning.varbit)
        if (tries >= 6) {
            when (warning.component) {
                Components.WILDERNESS_WARNING_382 -> sendInterfaceConfig(player, warning.component, 26, false)
                else -> sendInterfaceConfig(player, warning.component, 21, false)
            }
        }

        if (tries < 6) {
            val newVarbitValue = tries + 1
            setVarbit(player, warning.varbit, newVarbitValue, true)
        }
    }
}
