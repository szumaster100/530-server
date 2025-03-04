package content.region.morytania.quest.deal

import core.api.registerTimer
import core.api.spawnTimer
import core.game.node.entity.Entity
import core.game.node.entity.combat.BattleState
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.tools.RandomFunction
import core.tools.minutesToTicks
import org.rs.consts.NPCs

class FeverSpider : NPCBehavior(NPCs.FEVER_SPIDER_2850) {
    override fun beforeAttackFinalized(
        self: NPC,
        victim: Entity,
        state: BattleState,
    ) {
        super.beforeAttackFinalized(self, victim, state)
        if (RandomFunction.roll(10)) {
            registerTimer(victim, spawnTimer("disease", minutesToTicks(12)))
        }
    }
}
