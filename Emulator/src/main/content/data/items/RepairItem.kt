package content.data.items

import core.game.node.item.Item
import org.rs.consts.Items

/**
 * Represents a repairable item, its repaired product, and the repair cost.
 */
enum class RepairItem(
    @JvmField val item: Item,
    @JvmField val product: Item,
    @JvmField val cost: Int,
) {
    BRONZE_HATCHET(Item(Items.BROKEN_AXE_494, 1), Item(Items.BRONZE_AXE_1351, 1), 0),
    BRONZE_PICKAXE(Item(Items.BROKEN_PICKAXE_468, 1), Item(Items.BRONZE_PICKAXE_1265, 1), 0),

    IRON_HATCHET(Item(Items.BROKEN_AXE_496, 1), Item(Items.IRON_AXE_1349, 1), 0),
    IRON_PICKAXE(Item(Items.BROKEN_PICKAXE_470, 1), Item(Items.IRON_PICKAXE_1267, 1), 0),

    STEEL_HATCHET(Item(Items.BROKEN_AXE_498, 1), Item(Items.STEEL_AXE_1353, 1), 0),
    STEEL_PICKAXE(Item(Items.BROKEN_PICKAXE_472, 1), Item(Items.STEEL_PICKAXE_1269, 1), 14),

    BLACK_HATCHET(Item(Items.BROKEN_AXE_500, 1), Item(Items.BLACK_AXE_1361, 1), 10),

    MITHRIL_HATCHET(Item(Items.BROKEN_AXE_502, 1), Item(Items.MITHRIL_AXE_1355, 1), 18),
    MITHRIL_PICKAXE(Item(Items.BROKEN_PICKAXE_474, 1), Item(Items.MITHRIL_PICKAXE_1273, 1), 43),

    ADAMANT_HATCHET(Item(Items.BROKEN_AXE_504, 1), Item(Items.ADAMANT_AXE_1357, 1), 43),
    ADAMANT_PICKAXE(Item(Items.BROKEN_PICKAXE_476, 1), Item(Items.ADAMANT_PICKAXE_1271, 1), 107),

    RUNE_HATCHET(Item(Items.BROKEN_AXE_506, 1), Item(Items.RUNE_AXE_1359, 1), 427),
    RUNE_PICKAXE(Item(Items.BROKEN_PICKAXE_478, 1), Item(Items.RUNE_PICKAXE_1275, 1), 1100),

    DRAGON_HATCHET(Item(Items.BROKEN_AXE_6741, 1), Item(Items.DRAGON_AXE_6739, 1), 1800),
    ;

    companion object {
        /**
         * Finds a [RepairItem] by its item id.
         *
         * @param id The item id.
         * @return The [RepairItem], or `null` if not found.
         */
        @JvmStatic
        fun forId(id: Int): RepairItem? = values().find { it.item.id == id }
    }
}
