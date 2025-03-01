package content.region.kandarin.quest.grail

import core.game.node.entity.Entity
import core.game.node.entity.npc.AbstractNPC
import core.game.world.map.Location
import core.plugin.Initializable
import org.rs.consts.NPCs

@Initializable
class BlackKnightTitanNPC(
    id: Int = 0,
    location: Location? = null,
) : AbstractNPC(id, location) {
    override fun construct(
        id: Int,
        location: Location?,
        vararg objects: Any?,
    ): AbstractNPC {
        val obj = BlackKnightTitanNPC(id, location)
        obj.isNeverWalks = true
        obj.isWalks = false
        return obj
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.BLACK_KNIGHT_TITAN_221)
    }

    override fun tick() {
        if (this.skills.lifepoints == 0) {
            clearAttributes()
            this.face(null)
            skills.restore()
            this.properties.combatPulse.stop()
        }

        this.locks.lockMovement(10)

        super.tick()
    }

    override fun finalizeDeath(killer: Entity?) {
        if (killer != null && killer.isPlayer) {
            killer.locks.lockMovement(3)
            killer.walkingQueue.reset()
            killer.walkingQueue.addPath(2790, 4722)
        }

        super.finalizeDeath(killer)
    }
}
