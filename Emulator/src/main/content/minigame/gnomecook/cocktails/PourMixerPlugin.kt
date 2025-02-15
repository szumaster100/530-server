package content.minigame.gnomecook.cocktails

import core.api.inInventory
import core.cache.def.impl.ItemDefinition
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items

private const val WIZ_BLIZ_MIX = 9566
private const val SGG_MIX = 9567
private const val F_BLAST_MIX = 9568
private const val P_PUNCH_MIX = 9569
private const val BLU_SPEC_MIX = 9570
private const val CHOC_SAT_MIX = 9571
private const val DRUNK_DRAG_MIX = 9574
private val mixers =
    arrayOf(WIZ_BLIZ_MIX, SGG_MIX, F_BLAST_MIX, P_PUNCH_MIX, BLU_SPEC_MIX, CHOC_SAT_MIX, DRUNK_DRAG_MIX)

@Initializable
class PourMixerPlugin : OptionHandler() {
    override fun newInstance(arg: Any?): Plugin<Any> {
        for (mixer in mixers) {
            ItemDefinition.forId(mixer).handlers["option:pour"] = this
        }
        return this
    }

    override fun handle(
        player: Player?,
        node: Node?,
        option: String?,
    ): Boolean {
        player ?: return false
        node ?: return false
        when (node.id) {
            WIZ_BLIZ_MIX -> attemptMake(PouredDrink.WIZ_BLIZZ, player, node)
            SGG_MIX -> attemptMake(PouredDrink.SHORT_G_G, player, node)
            F_BLAST_MIX -> attemptMake(PouredDrink.FRUIT_BLAST, player, node)
            P_PUNCH_MIX -> attemptMake(PouredDrink.PINE_PUNCH, player, node)
            BLU_SPEC_MIX -> attemptMake(PouredDrink.BLUR_SPEC, player, node)
            CHOC_SAT_MIX -> attemptMake(PouredDrink.CHOC_SAT, player, node)
            DRUNK_DRAG_MIX -> attemptMake(PouredDrink.DRUNK_DRAG, player, node)
        }
        return true
    }

    private fun attemptMake(
        drink: PouredDrink,
        player: Player,
        node: Node,
    ) {
        if (!inInventory(player, Items.COCKTAIL_GLASS_2026)) {
            player.dialogueInterpreter.sendDialogue("You need a glass to pour this into.")
            return
        }

        var hasAll = true
        for (ingredient in drink.requiredItems) {
            if (!player.inventory.containsItem(ingredient)) {
                hasAll = false
            }
        }

        if (!hasAll) {
            player.dialogueInterpreter.sendDialogue("You don't have the garnishes for this.")
            return
        }

        player.inventory.remove(*drink.requiredItems)
        player.inventory.remove(node.asItem())
        player.inventory.remove(Item(Items.COCKTAIL_GLASS_2026))
        player.inventory.add(Item(drink.product))
        player.inventory.add(Item(Items.COCKTAIL_SHAKER_2025))
        player.skills.addExperience(Skills.COOKING, 50.0)
    }

    internal enum class PouredDrink(
        val product: Int,
        val requiredItems: Array<Item>,
    ) {
        FRUIT_BLAST(
            product = 9514,
            requiredItems = arrayOf(Item(Items.LEMON_SLICES_2106)),
        ),
        PINE_PUNCH(
            product = 9512,
            requiredItems =
                arrayOf(
                    Item(Items.LIME_CHUNKS_2122),
                    Item(Items.PINEAPPLE_CHUNKS_2116),
                    Item(Items.ORANGE_SLICES_2112),
                ),
        ),
        WIZ_BLIZZ(
            product = 9508,
            requiredItems = arrayOf(Item(Items.PINEAPPLE_CHUNKS_2116), Item(Items.LIME_SLICES_2124)),
        ),
        SHORT_G_G(
            product = 9510,
            requiredItems = arrayOf(Item(Items.LIME_SLICES_2124), Item(Items.EQUA_LEAVES_2128)),
        ),
        DRUNK_DRAG(
            product = 9575,
            requiredItems =
                arrayOf(
                    Item(Items.GIN_2019),
                    Item(Items.VODKA_2015),
                    Item(Items.DWELLBERRIES_2126),
                    Item(Items.PINEAPPLE_CHUNKS_2116),
                    Item(Items.POT_OF_CREAM_2130),
                ),
        ),

        CHOC_SAT(
            product = 9572,
            requiredItems =
                arrayOf(
                    Item(Items.WHISKY_2017),
                    Item(Items.EQUA_LEAVES_2128),
                    Item(Items.BUCKET_OF_MILK_1927),
                    Item(Items.CHOCOLATE_DUST_1975),
                    Item(Items.POT_OF_CREAM_2130),
                    Item(Items.CHOCOLATE_BAR_1973),
                ),
        ),
        BLUR_SPEC(
            product = 9520,
            requiredItems =
                arrayOf(
                    Item(Items.LEMON_CHUNKS_2104),
                    Item(Items.ORANGE_CHUNKS_2110),
                    Item(Items.EQUA_LEAVES_2128),
                    Item(Items.LIME_SLICES_2124),
                ),
        ),
    }
}
