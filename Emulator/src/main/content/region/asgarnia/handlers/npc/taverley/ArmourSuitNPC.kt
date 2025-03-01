package content.region.asgarnia.handlers.npc.taverley

import core.api.addScenery
import core.game.node.entity.npc.AbstractNPC
import core.game.node.scenery.Scenery
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.map.Location
import core.plugin.Initializable
import org.rs.consts.NPCs

@Initializable
class ArmourSuitNPC(
    id: Int = 0,
    location: Location? = null,
) : AbstractNPC(id, location) {
    override fun init() {
        super.init()
        super.setRespawn(false)
        Pulser.submit(
            object : Pulse(100, this) {
                override fun pulse(): Boolean {
                    if (!properties.combatPulse.isAttacking && !inCombat()) {
                        clear()
                        return true
                    }
                    return false
                }
            },
        )
    }

    override fun clear() {
        super.clear()
        addScenery(Scenery(818, properties.spawnLocation, 1))
    }

    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any,
    ): AbstractNPC {
        return ArmourSuitNPC(id, location)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.SUIT_OF_ARMOUR_453)
    }
}
