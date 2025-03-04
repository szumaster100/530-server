package core.game.system.command

import core.ServerConstants
import core.game.node.entity.player.Player
import core.game.world.GameWorld

/*
 * TODO
 *  [ ] - Replace to book interface.
 */
class Command(
    val name: String,
    val privilege: Privilege,
    val usage: String = "UNDOCUMENTED",
    val description: String = "UNDOCUMENTED",
    val handle: (Player, Array<String>) -> Unit,
) {
    fun attemptHandling(
        player: Player,
        args: Array<String>?,
    ) {
        args ?: return
        if (player.rights.ordinal >= privilege.ordinal ||
            GameWorld.settings?.isDevMode == true ||
            ServerConstants.I_AM_A_CHEATER
        ) {
            handle(player, args)
        }
    }
}

object CommandMapping {
    private val mapping = hashMapOf<String, Command>()

    fun get(name: String): Command? {
        return mapping[name]
    }

    fun register(command: Command) {
        mapping[command.name] = command
    }

    fun getCommands(): Array<Command> {
        return mapping.values.toTypedArray()
    }

    fun getNames(): Array<String> {
        return mapping.keys.toTypedArray()
    }

    fun getPageIndices(rights: Int): IntArray {
        val list = ArrayList<Int>()
        list.add(0)

        var lineCounter = 0
        for ((index, command) in getCommands().filter { it.privilege.ordinal <= rights }.withIndex()) {
            lineCounter += 2
            if (command.usage.isNotEmpty()) lineCounter++
            if (command.description.isNotEmpty()) lineCounter++

            if (lineCounter > 306) {
                list.add(index)
                lineCounter = 0
            }
        }

        return list.toIntArray()
    }
}
