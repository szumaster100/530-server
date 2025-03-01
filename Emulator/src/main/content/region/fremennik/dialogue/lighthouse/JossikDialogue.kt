package content.region.fremennik.dialogue.lighthouse

import content.data.GodBook
import content.region.fremennik.quest.horror.dialogue.JossikRewardDialogue
import core.api.inInventory
import core.api.openDialogue
import core.game.dialogue.Dialogue
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import org.rs.consts.Items
import org.rs.consts.NPCs

@Initializable
class JossikDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun open(vararg args: Any): Boolean {
        npc = args[0] as NPC
        if (inInventory(player, Items.RUSTY_CASKET_3849, 1)) {
            end()
            openDialogue(player, JossikRewardDialogue(), npc)
        } else {
            npc("Hello again, adventurer.", "What brings you this way?")
        }
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        var uncompleted: MutableList<GodBook>? = null
        when (stage) {
            0 -> options("Can I see your wares?", "Have you found any prayerbooks?").also { stage++ }
            1 ->
                stage =
                    if (buttonId == 1) {
                        player("Can I see your wares?")
                        10
                    } else {
                        player("Have you found any prayerbooks?")
                        20
                    }

            20 -> {
                var missing = false
                for (book in GodBook.values()) {
                    if (player.getSavedData().globalData.hasCompletedGodBook(book) && !player.hasItem(book.book)) {
                        missing = true
                        player.inventory.add(book.book, player)
                        npc(
                            "As a matter of fact, I did! This book washed up on the",
                            "beach, and I recognised it as yours!",
                        )
                    }
                }
                val damaged = player.getSavedData().globalData.getGodBook()
                if (damaged != -1 && !player.hasItem(GodBook.values()[damaged].damagedBook)) {
                    missing = true
                    player.inventory.add(GodBook.values()[damaged].damagedBook, player)
                    npc(
                        "As a matter of fact, I did! This book washed up on the",
                        "beach, and I recognised it as yours!",
                    )
                }
                if (missing) {
                    stage = 23
                    return true
                }
                uncompleted = ArrayList(5)
                for (book in GodBook.values()) {
                    if (!player.getSavedData().globalData.hasCompletedGodBook(book!!)) {
                        uncompleted.add(book!!)
                    }
                }
                var hasUncompleted = false
                for (book in GodBook.values()) {
                    if (player.hasItem(book.damagedBook)) {
                        hasUncompleted = true
                    }
                }
                if (uncompleted.size == 0 || hasUncompleted) {
                    npc("No, sorry adventurer, I haven't.")
                    stage = 23
                    return true
                }
                npc(
                    "Funnily enough I have! I found some books in caskets",
                    "just the other day! I'll sell one to you for 5000 coins;",
                    "what do you say?",
                )
                stage++
            }

            21 -> {
                val names = arrayOfNulls<String>(uncompleted!!.size + 1)
                var i = 0
                while (i < uncompleted!!.size) {
                    names[i] = uncompleted!![i].bookName
                    i++
                }
                names[names.size - 1] = "Don't buy anything."
                options(*names)
                stage++
            }

            22 -> {
                if (buttonId - 1 > uncompleted!!.size - 1) {
                    player("Don't buy anything.")
                    stage = 23
                }
                if (!player.inventory.contains(995, 5000)) {
                    player("Sorry, I don't seem to have enough coins.")
                    stage = 23
                }
                if (player.inventory.freeSlots() == 0) {
                    player("Sorry, I don't have enough inventory space.")
                    stage = 23
                }
                val purchase = uncompleted!![buttonId - 1]
                if (purchase != null && player.inventory.remove(Item(995, 5000))) {
                    npc("Here you go!")
                    player.getSavedData().globalData.setGodBook(purchase.ordinal)
                    player.inventory.add(purchase.damagedBook, player)
                    stage = 23
                } else {
                    end()
                }
            }

            23 -> end()
            10 -> {
                npc("Sure thing!", "I think you'll agree, my prices are remarkable!")
                stage++
            }

            11 -> {
                npc.openShop(player)
                end()
            }
        }
        return true
    }

    fun hasGodBook(): Boolean {
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.JOSSIK_1334)
    }
}
