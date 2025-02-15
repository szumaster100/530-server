package content.global.handlers.scenery

import core.api.sendMessage
import core.cache.def.impl.SceneryDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.plugin.Plugin

@Initializable
class LookAtOptionHandler : OptionHandler() {
    override fun newInstance(arg: Any?): Plugin<Any> {
        for (i in 18877..18900) {
            SceneryDefinition.forId(i).handlers["option:look at"] = this
        }
        return this
    }

    override fun handle(
        player: Player,
        node: Node,
        option: String,
    ): Boolean {
        sendMessage(player, "The " + node.name.lowercase() + " seem to be going south-west.")
        return true
    }
}
