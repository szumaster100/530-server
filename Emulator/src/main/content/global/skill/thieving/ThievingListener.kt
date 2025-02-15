package content.global.skill.thieving

import core.api.lockInteractions
import core.api.submitIndividualPulse
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.scenery.Scenery

class ThievingListener : InteractionListener {
    override fun defineListeners() {
        on(IntType.SCENERY, "steal-from", "steal from", "steal") { player, node ->
            submitIndividualPulse(player, StallThiefPulse(player, node as Scenery, Stall.forScenery(node)))
            lockInteractions(player, 6)
            return@on true
        }
    }
}
