package content.global.ame.drunkdwarf

import org.rs.consts.NPCs
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction

class DrunkenDwarfBehavior : NPCBehavior(NPCs.DRUNKEN_DWARF_956) {

    override fun beforeAttackFinalized(self: NPC, victim: Entity, state: BattleState) {
        state.estimatedHit = RandomFunction.getRandom(3)
    }
}