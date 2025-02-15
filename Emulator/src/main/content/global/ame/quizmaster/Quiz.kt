package content.global.ame.quizmaster

import core.api.MapArea
import core.game.node.entity.Entity
import core.game.node.entity.player.Player
import core.game.world.map.zone.ZoneBorders
import org.rs.consts.NPCs

class Quiz : MapArea {
    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(ZoneBorders(1954, 4763, 1950, 4770))
    }

    override fun areaLeave(
        entity: Entity,
        logout: Boolean,
    ) {
        if (entity is Player) {
            val player = entity.asPlayer()
            QuizMaster.cleanup(player)
        }
    }

    override fun areaEnter(entity: Entity) {
        super.areaEnter(entity)
        if (entity is Player) {
            val player = entity.asPlayer()
            player.dialogueInterpreter.open(NPCs.QUIZ_MASTER_2477)
            player.lock()
        }
    }
}
