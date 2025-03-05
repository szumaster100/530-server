package content.minigame.gnomecook.crunchies

import core.api.Container
import core.api.addItem
import core.api.removeItem
import core.api.rewardXP
import core.game.interaction.NodeUsageEvent
import core.game.interaction.UseWithHandler
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items

@Initializable
class GnomeCrunchyGarnisher : UseWithHandler(
    Items.UNFINISHED_CRUNCHY_9578,
    Items.UNFINISHED_CRUNCHY_9580,
    Items.UNFINISHED_CRUNCHY_9582,
    Items.UNFINISHED_CRUNCHY_9584
) {
    override fun newInstance(arg: Any?): Plugin<Any> {
        addHandler(Items.CHOCOLATE_DUST_1975, ITEM_TYPE, this)
        addHandler(Items.GNOME_SPICE_2169, ITEM_TYPE, this)
        addHandler(Items.EQUA_LEAVES_2128, ITEM_TYPE, this)
        return this
    }

    override fun handle(event: NodeUsageEvent?): Boolean {
        event ?: return false
        val player = event.player
        val used = event.used.asItem()
        val with = event.usedWith.asItem()

        val product = when (with.id) {
            Items.CHOCOLATE_DUST_1975 -> {
                when (used.id) {
                    Items.UNFINISHED_CRUNCHY_9578 -> Items.CHOCCHIP_CRUNCHIES_2209
                    else -> -1
                }
            }

            Items.GNOME_SPICE_2169 -> {
                when (used.id) {
                    Items.UNFINISHED_CRUNCHY_9584 -> Items.WORM_CRUNCHIES_2205
                    Items.UNFINISHED_CRUNCHY_9580 -> Items.SPICY_CRUNCHIES_2213
                    else -> -1
                }
            }

            Items.EQUA_LEAVES_2128 -> {
                when (used.id) {
                    Items.UNFINISHED_CRUNCHY_9582 -> Items.TOAD_CRUNCHIES_2217
                    else -> -1
                }
            }

            else -> -1
        }

        if (product == -1) return false

        player.inventory.remove(used)
        if (with.id != Items.GNOME_SPICE_2169) {
            removeItem(player, with, Container.INVENTORY)
        }
        addItem(player, product)
        rewardXP(player, Skills.COOKING, 64.0)
        return true
    }
}
