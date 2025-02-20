package content.global.skill.farming

import core.api.sendMessage
import core.cache.def.impl.SceneryDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.plugin.Plugin

@Initializable
class CompostBinOptionHandler : OptionHandler() {
    override fun newInstance(arg: Any?): Plugin<Any> {
        for (i in 7836..7839) {
            SceneryDefinition.forId(i).configObjectIds!!.forEach {
                val def = SceneryDefinition.forId(it)
                def.handlers["option:open"] = this
                def.handlers["option:close"] = this
                def.handlers["option:take-tomato"] = this
            }
        }
        return this
    }

    override fun handle(
        player: Player?,
        node: Node?,
        option: String?,
    ): Boolean {
        player ?: return false
        node ?: return false
        val cBin = CompostBins.forObject(node.asScenery()) ?: return false
        val bin = cBin.getBinForPlayer(player)

        when (option) {
            "close" ->
                if (!bin.isFull()) {
                    sendMessage(
                        player,
                        "This shouldn't be happening. Report this.",
                    )
                } else {
                    bin.close()
                }

            "open" ->
                if (!bin.isFinished) {
                    sendMessage(
                        player,
                        "I should probably wait until it is done to open it.",
                    )
                } else {
                    bin.open()
                }

            "take-tomato" -> {
                if (!bin.isTomatoes || !bin.isFinished) {
                    sendMessage(player, "This shouldn't be happening. Report this.")
                } else {
                    if (player.inventory.isFull) {
                        sendMessage(player, "You don't have enough inventory space to do this.")
                    } else {
                        val reward = bin.takeItem()
                        if (reward != null) {
                            player.inventory.add(reward)
                        }
                    }
                }
            }
        }
        return true
    }
}
