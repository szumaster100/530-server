package content.region.morytania.dialogue.phasmatys

import content.region.morytania.quest.ahoy.dialogue.GravingasDialogue
import core.api.inEquipment
import core.api.openDialogue
import core.api.quest.getQuestStage
import core.api.sendDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class GravingasDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun open(vararg args: Any): Boolean {
        when {
            inEquipment(player, Items.BEDSHEET_4285) &&
                getQuestStage(
                    player,
                    Quests.GHOSTS_AHOY,
                ) >= 1 -> end().also { openDialogue(player, GravingasDialogue()) }

            !inEquipment(player, Items.GHOSTSPEAK_AMULET_552) -> npc("Woooo wooo wooooo woooo")
            else ->
                npc(
                    "Will you join with me and protect against the evil ban",
                    "of Nercrovarus and his disciples?",
                ).also { stage = 1 }
        }
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 -> sendDialogue(player!!, "You cannot understand the ghost.").also { stage = END_DIALOGUE }
            1 -> player("I'm sorry, I don't really think I should get involved.").also { stage++ }
            2 -> npc("Ah, the youth of today - so apathetic to politics.").also { stage = END_DIALOGUE }
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.GRAVINGAS_1685)
    }
}
