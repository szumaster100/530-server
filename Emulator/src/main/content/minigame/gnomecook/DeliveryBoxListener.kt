package content.minigame.gnomecook

import core.api.getAttribute
import core.api.interaction.getNPCName
import core.api.sendDialogueLines
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.item.Item
import org.rs.consts.Items

class DeliveryBoxListener : InteractionListener {

    override fun defineListeners() {
        on(Items.ALUFT_ALOFT_BOX_9477, IntType.ITEM, "check") { player, _ ->
            val jobId = getAttribute(player, "$GC_BASE_ATTRIBUTE:$GC_JOB_ORDINAL", -1)

            if (jobId == -1) {
                sendDialogueLines(player, "You do not currently have a job.")
            } else {
                val job = GnomeCookingJob.values()[jobId]
                val item = getAttribute(player, "$GC_BASE_ATTRIBUTE:$GC_NEEDED_ITEM", Item(0))
                val npcName = getNPCName(job.npc_id).lowercase()
                sendDialogueLines(
                    player, "I need to deliver a ${item.name.lowercase()} to $npcName,", "who is ${job.tip}"
                )
            }
            return@on true
        }
    }
}
