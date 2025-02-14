package content.global.handlers.npc

import org.rs.consts.NPCs
import core.api.sendChat
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction

class DuckNPC : NPCBehavior(NPCs.DUCK_46, NPCs.DUCK_2693, NPCs.DUCK_6113) {

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.random(100) < 10) {
            sendChat(
                self, when (self.id) {
                    46 -> "Eep!"
                    else -> "Quack!"
                }
            )
        }
        return true
    }
}