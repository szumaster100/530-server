package content.global.handlers.iface.warning

import core.api.*
import core.game.component.Component
import core.game.node.entity.player.Player

/**
 * Manages the Doomsayer warning system.
 * @author Matrix developers.
 */
object DoomsayerManager {

    private const val TOGGLE_WARNING_VARBIT = 1133

    /**
     * Warning type constants used in the `openWarning` method.
     */
    const val NORMAL_WARNING: Int = 0

    /**
     * Warning type constants used in the `openWarning` method.
     */
    const val WILDERNESS_DITCH_WARNING: Int = 1

    /**
     * Warning type constants used in the `openWarning` method.
     */
    const val CLAN_WARS_WARNING_MESSAGE: Int = 3

    /**
     * Array of varbits for different warnings displayed by Doomsayer.
     */
    private val DOOMSAYER_WARNINGS_VARBITS = intArrayOf(-1, 1134, 1135, 1136, 1137, 1138, 1139, 1140, 1141, 1142, 1143, 1146, 1147, 1148, 1149, 1150, 1151, 1152, 1153, 1154, 1155, -1, 1144, 1159, 1160, 1161, 1162, 1163, 1164, 1165, 1166, 1156, -1, 1167, 1168, 1169, 1170)

    /**
     * Array of varbits for different messages displayed by Doomsayer.
     */
    private val DOOMSAYER_MESSAGES_VARBITS = intArrayOf(9784, 16442, 17864, 18271, 21225)

    /**
     * Constant representing the safe warning in Clan Wars.
     */
    const val CLAN_WARS_SAFE_WARNING = 22

    /**
     * Constant representing the dangerous warning in Clan Wars.
     */
    const val CLAN_WARS_DANGEROUS_WARNING = 31

    /**
     * Array holding the warning status for the different Doomsayer warnings.
     */
    private val warnings = IntArray(DOOMSAYER_WARNINGS_VARBITS.size) { 0 }

    /**
     * Array holding the status of the different Doomsayer messages.
     */
    private val messages = BooleanArray(DOOMSAYER_MESSAGES_VARBITS.size)

    /**
     * Opens the Doomsayer interface for the specified player.
     *
     * @param player The player.
     */
    @JvmStatic
    fun openDoomsayer(player: Player) {
        openInterface(player, 583)
    }

    /**
     * Refreshes all warnings for the player by updating the varbits.
     *
     * @param player The player.
     */
    private fun refreshWarnings(player: Player) {
        for (i in warnings.indices) {
            if (DOOMSAYER_WARNINGS_VARBITS[i] != -1) {
                refreshWarning(player, i)
            }
        }
    }

    /**
     * Refreshes all messages for the player by updating the varbits.
     *
     * @param player The player.
     */
    private fun refreshMessages(player: Player) {
        for (i in messages.indices) {
            if (DOOMSAYER_MESSAGES_VARBITS[i] != -1) {
                refreshMessage(player, i)
            }
        }
    }

    /**
     * Refreshes the warning for an id by updating the varbit for the player.
     *
     * @param player The player.
     * @param id The id of the warning to refresh.
     */
    private fun refreshWarning(player: Player, id: Int) {
        val value = if (id == CLAN_WARS_SAFE_WARNING || id == CLAN_WARS_DANGEROUS_WARNING) {
            if (warnings[id] == 7) 1 else 0
        } else {
            warnings[id]
        }
        setVarbit(player, DOOMSAYER_WARNINGS_VARBITS[id], value, true)
    }

    /**
     * Refreshes the message for an id by updating the varbit for the player.
     *
     * @param player The player.
     * @param id The id of the message to refresh.
     */
    private fun refreshMessage(player: Player, id: Int) {
        setVarbit(player, DOOMSAYER_MESSAGES_VARBITS[id], if (messages[id]) 1 else 0, true)
    }

    /**
     * Toggles the warning for an id for the player.
     *
     * @param player The player.
     * @param id The id of the warning to toggle.
     */
    @JvmStatic
    fun toggleWarning(player: Player, id: Int) {
        if ((id == CLAN_WARS_SAFE_WARNING || id == CLAN_WARS_DANGEROUS_WARNING) && warnings[id] != 7) {
            sendMessage(
                player,
                "You may turn this warning off when you enter the ${if (id == 22) "safe" else "dangerous"} free-for-all arena."
            )
            return
        }
        if (warnings[id] < 6) {
            sendMessage(
                player,
                "You cannot toggle this warning screen on or off. You need to go to the area it is linked to enough times to have the option to do so."
            )
            return
        }
        warnings[id] = if (warnings[id] != 7) 7 else 6
        refreshWarning(player, id)
        sendMessage(
            player,
            "You have toggled this warning screen ${if (warnings[id] == 7) "off" else "on"}. You will ${if (warnings[id] == 7) "no longer see this warning screen." else "see this interface again."}"
        )
    }

    /**
     * Increments the warning value for an id for the player.
     *
     * @param player The player.
     * @param id The id of the warning to increment.
     */
    @JvmStatic
    fun incrementWarning(player: Player, id: Int) {
        if (warnings[id] >= 6) return
        warnings[id]++
        refreshWarning(player, id)
    }

    /**
     * Toggles a message.
     *
     * @param player The player.
     * @param id The id of the message to toggle.
     */
    @JvmStatic
    fun toggleMessage(player: Player, id: Int) {
        messages[id] = !messages[id]
        refreshMessage(player, id)
        val message = when (id) {
            0 -> if (messages[id]) "You will no longer be notified when a sinkhole is about to spawn." else "You will now be notified when a sinkhole is about to spawn."
            1 -> if (messages[id]) "You will no longer see notifications when goblins begin raiding." else "You will now be notified when goblins begin raiding."
            2 -> if (messages[id]) "You will no longer see notifications when demons begin invading." else "You will now be notified when demons begin invading."
            3 -> if (messages[id]) "You will no longer see notifications when wilderness warbands begin." else "You will now be notified when wilderness warbands begin."
            4 -> if (messages[id]) "You will no longer see notifications of news concerning a world event." else "You will now be notified of news concerning a world event."
            else -> "Unknown message."
        }
        sendMessage(player, message)
    }

    /**
     * Toggles the current warning for the player based on the current varbit value.
     */
    fun toogleCurrentWarning(player: Player) {
        toggleWarning(player, getVarbit(player, TOGGLE_WARNING_VARBIT))
    }

    /**
     * Opens a warning interface for the player.
     *
     * @param type The type of warning to display.
     * @param id The id associated with the warning type.
     * @param warningMessage The message to display in the warning interface.
     * @param enterMessage The message to display (optional).
     */
    @JvmStatic
    fun openWarning(player: Player, type: Int, id: Int, warningMessage: String?, enterMessage: String?) {
        when (type) {
            NORMAL_WARNING -> {
                player.interfaceManager.open(Component(583))
                if (warningMessage != null) sendString(player, warningMessage, 583, 15)
                if (enterMessage != null) sendString(player, enterMessage, 583, 18)
                setComponentVisibility(player, 583, 10, false)
            }

            WILDERNESS_DITCH_WARNING -> {
                player.interfaceManager.open(Component(382))
            }

            CLAN_WARS_WARNING_MESSAGE -> {
                player.interfaceManager.open(Component(793))
                setVarbit(player, 4094, if (id == CLAN_WARS_DANGEROUS_WARNING) 1 else 0)
            }
        }
        setVarbit(player, TOGGLE_WARNING_VARBIT, id)
    }

    /**
     * Checks if a warning is turned off.
     *
     * @param id The id of the warning to check.
     * @return True if the warning is turned off, otherwise false.
     */
    @JvmStatic
    fun isWarningOff(id: Int): Boolean = warnings[id] == 7

    /**
     * Checks if a message is turned off.
     *
     * @param id The id of the message to check.
     * @return True if the message is turned off, otherwise false.
     */
    @JvmStatic
    fun isMessageOff(id: Int): Boolean = messages[id]
}
