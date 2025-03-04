package content.minigame.ratpits

import core.api.openDialogue
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import org.rs.consts.NPCs

class RatpitListener : InteractionListener {
    override fun defineListeners() {
        on(NPCs.VAERINGK_2992, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, VaeringkDialogue())
            return@on true
        }

        on(NPCs.OXI_2993, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, OxiDialogue())
            return@on true
        }

        on(NPCs.FIOR_2994, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, FiorDialogue())
            return@on true
        }

        on(2995, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, SigraDialogue())
            return@on true
        }

        on(NPCs.ANLEIF_2996, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, AnleifDialogue())
            return@on true
        }
    }
}
