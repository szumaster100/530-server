package content.global.skill.agility.courses.werewolf

import org.rs.consts.NPCs
import core.api.animate
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction

class AgilityBossNPC : NPCBehavior(NPCs.AGILITY_BOSS_1661) {

    override fun tick(self: NPC): Boolean {
        if (RandomFunction.roll(25))
            animate(self, 6549)
        return true
    }
}