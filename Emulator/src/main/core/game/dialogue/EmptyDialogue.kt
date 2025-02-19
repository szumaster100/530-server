package core.game.dialogue

import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.tools.START_DIALOGUE

class EmptyDialogue(
    player: Player? = null,
    val file: DialogueFile?,
) : Dialogue(player) {
    override fun newInstance(player: Player?): Dialogue {
        return EmptyDialogue(player, null)
    }

    override fun open(vararg args: Any?): Boolean {
        if (args.isNotEmpty() && args[0] is NPC) {
            npc = args[0] as NPC
        }
        stage = START_DIALOGUE
        loadFile(file)
        interpreter.handle(0, 0)
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(Integer.MAX_VALUE)
    }
}
