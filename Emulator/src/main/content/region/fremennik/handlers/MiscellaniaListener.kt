package content.region.fremennik.handlers

import org.rs.consts.NPCs
import content.region.fremennik.dialogue.miscellania.FarmerFromundDialogue
import content.region.fremennik.dialogue.miscellania.FishermanFrodiDialogue
import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener

class MiscellaniaListener : InteractionListener {

    override fun defineListeners() {

        on(NPCs.FISHERMAN_FRODI_1397, IntType.NPC, "talk-to") { player, _ ->
            openDialogue(player, FishermanFrodiDialogue())
            return@on true
        }

        on(NPCs.FARMER_FROMUND_3917, IntType.NPC, "talk-to") { player, _ ->
            openDialogue(player, FarmerFromundDialogue())
            return@on true
        }

    }

}