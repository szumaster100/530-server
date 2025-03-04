package content.region.fremennik.quest.viking.handlers

import core.game.node.entity.Entity
import core.game.world.map.zone.MapZone
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneBuilder
import core.plugin.Initializable
import core.plugin.Plugin

@Initializable
class Longhall :
    MapZone("LonghallStageZone", true),
    Plugin<Any?> {
    override fun newInstance(arg: Any?): Longhall {
        ZoneBuilder.configure(this)
        return this
    }

    override fun configure() {
        super.register(ZoneBorders(2655, 3682, 2662, 3685))
    }

    override fun enter(e: Entity?): Boolean {
        if (e != null && e.isPlayer) {
            e.setAttribute("onStage", true)
        }
        return super.enter(e)
    }

    override fun leave(
        e: Entity?,
        logout: Boolean,
    ): Boolean {
        if (e != null && e.isPlayer) {
            e.removeAttribute("onStage")
        }
        return super.leave(e, logout)
    }

    override fun fireEvent(
        identifier: String?,
        vararg args: Any?,
    ): Any {
        return Unit
    }
}
