package content.region.misthalin.quest.rag.handlers.npc

import core.api.quest.isQuestInProgress
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.npc.NPCBehavior
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.tools.RandomFunction
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests

class MonkeyNPC : NPCBehavior(*monkeyIds) {
    companion object {
        private val monkeyIds =
            intArrayOf(
                NPCs.MONKEY_132,
                NPCs.MONKEY_1463,
                NPCs.MONKEY_1464,
                NPCs.MONKEY_1487,
                NPCs.MONKEY_2301,
                NPCs.MONKEY_2302,
                NPCs.MONKEY_2303,
                NPCs.MONKEY_4344,
                NPCs.MONKEY_4363,
                NPCs.MONKEY_5852,
                NPCs.MONKEY_5853,
                NPCs.MONKEY_6943,
                NPCs.MONKEY_7211,
                NPCs.MONKEY_7213,
                NPCs.MONKEY_7215,
                NPCs.MONKEY_7217,
                NPCs.MONKEY_7219,
                NPCs.MONKEY_7221,
                NPCs.MONKEY_7223,
                NPCs.MONKEY_7225,
                NPCs.MONKEY_7227,
                NPCs.MONKEY_ZOMBIE_1465,
                NPCs.MONKEY_ZOMBIE_1466,
                NPCs.MONKEY_ZOMBIE_1467,
            )
    }

    override fun onDropTableRolled(
        self: NPC,
        killer: Entity,
        drops: ArrayList<Item>,
    ) {
        super.onDropTableRolled(self, killer, drops)

        if (killer is Player && isQuestInProgress(killer, Quests.RAG_AND_BONE_MAN, 1, 99)) {
            if (RandomFunction.roll(4)) {
                drops.add(Item(Items.MONKEY_PAW_7854))
            }
        }
    }
}
