package content.global.skill.construction.decoration.workshop

import content.data.items.BrokenItem
import content.data.items.BrokenItem.getRepair
import content.data.items.RepairItem
import content.region.misthalin.dialogue.lumbridge.BobDialogue
import core.api.*
import core.game.dialogue.DialogueFile
import core.game.interaction.NodeUsageEvent
import core.game.interaction.UseWithHandler
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Scenery
import kotlin.math.ceil

/**
 * Handles interactions with armor repair stands.
 * Support repairing broken items, and barrows equipment.
 */
@Initializable
class RepairSpace :
    UseWithHandler(
    /*
     * itemIDs that can be used with the repair stand.
     */
        494,
        468,
        474,
        476,
        478,
        470,
        472,
        496,
        498,
        500,
        502,
        504,
        506,
        686,
        687,
        697,
        698,
        689,
        4856,
        4857,
        4858,
        4859,
        4860,
        4862,
        4863,
        4864,
        4865,
        4866,
        4868,
        4869,
        4870,
        4871,
        4872,
        4874,
        4875,
        4876,
        4877,
        4878,
        4880,
        4881,
        4882,
        4883,
        4884,
        4886,
        4887,
        4888,
        4889,
        4890,
        4892,
        4893,
        4894,
        4895,
        4896,
        4898,
        4899,
        4900,
        4901,
        4902,
        4904,
        4905,
        4906,
        4907,
        4908,
        4910,
        4911,
        4912,
        4913,
        4914,
        4916,
        4917,
        4918,
        4919,
        4920,
        4922,
        4923,
        4924,
        4925,
        4926,
        4928,
        4929,
        4930,
        4931,
        4932,
        4934,
        4935,
        4936,
        4937,
        4938,
        4940,
        4941,
        4942,
        4943,
        4944,
        4946,
        4947,
        4948,
        4949,
        4950,
        4952,
        4953,
        4954,
        4955,
        4956,
        4958,
        4959,
        4960,
        4961,
        4962,
        4964,
        4965,
        4966,
        4967,
        4968,
        4970,
        4971,
        4972,
        4973,
        4974,
        4976,
        4977,
        4978,
        4979,
        4980,
        4982,
        4983,
        4984,
        4985,
        4986,
        4988,
        4989,
        4990,
        4991,
        4992,
        4994,
        4995,
        4996,
        4997,
        4998,
        6741,
    ) {
    override fun newInstance(arg: Any?): Plugin<Any> {
        addHandler(Scenery.REPAIR_BENCH_13713, OBJECT_TYPE, this)
        addHandler(Scenery.WHETSTONE_13714, OBJECT_TYPE, this)
        addHandler(Scenery.ARMOUR_REPAIR_STAND_13715, OBJECT_TYPE, this)
        return this
    }

    /**
     * Handles item repair interactions at a repair station.
     */
    override fun handle(event: NodeUsageEvent?): Boolean {
        event ?: return false
        val player = event.player
        val item = event.used.asItem()
        val repairItem = RepairItem.forId(item.id)

        val brokenItem =
            intArrayOf(
                Items.BROKEN_ARROW_687,
                Items.BROKEN_STAFF_689,
                Items.RUSTY_SWORD_686,
                Items.DAMAGED_ARMOUR_697,
                Items.BROKEN_ARMOUR_698,
            )

        when (event.usedWith.id) {
            Scenery.REPAIR_BENCH_13713 ->
                if (item.id == Items.BROKEN_ARROW_687 || item.id == Items.BROKEN_STAFF_689) {
                    repair(player, item)
                    return true
                }

            Scenery.WHETSTONE_13714 ->
                if (item.id == Items.BROKEN_ARROW_687 ||
                    item.id == Items.BROKEN_STAFF_689 ||
                    item.id == Items.RUSTY_SWORD_686
                ) {
                    repair(player, item)
                    return true
                }

            Scenery.ARMOUR_REPAIR_STAND_13715 ->
                if (item.id in brokenItem) {
                    repair(player, item)
                } else {
                    var baseCost = 0.0
                    var product: Item? = null

                    if (repairItem != null) {
                        baseCost = repairItem.cost.toDouble()
                        product = repairItem.product
                    } else if (BobDialogue.BarrowsEquipment.isBarrowsItem(item.id)) {
                        val type = BobDialogue.BarrowsEquipment.formattedName(item.id)
                        val single = BobDialogue.BarrowsEquipment.getSingleName(type)
                        val equipment = BobDialogue.BarrowsEquipment.getEquipmentType(type)
                        val formattedName =
                            type
                                .lowercase()
                                .replace(single, "")
                                .trim()
                                .replace("'s", "")
                        val fullEquip =
                            BobDialogue.BarrowsEquipment.BarrowsFullEquipment.forName("$formattedName $equipment")
                        baseCost = BobDialogue.BarrowsEquipment.getFormattedCost(equipment, item).toDouble()
                        product = fullEquip.full
                    }

                    if (product == null || baseCost == 0.0) {
                        player.sendMessage("That item can't be repaired.")
                        return true
                    }

                    val cost =
                        ceil(
                            ((100.0 - (player.skills.getLevel(Skills.SMITHING) / 2.0)) / 100.0) * baseCost,
                        ).toInt()
                    openDialogue(player, RepairDialogue(item, cost, product))
                }
        }
        return true
    }

    /**
     * Repairs a damaged item.
     *
     * @param player The player repairing the item.
     * @param item   The damaged item.
     */
    private fun repair(
        player: Player,
        item: Item,
    ): Boolean {
        animate(player, Animations.CHURN_PLUMING_STAND_3654)
        getRepairType(player, item)
        return true
    }

    private fun getRepairType(
        player: Player,
        item: Item,
    ) {
        val repairType =
            when (item.id) {
                Items.BROKEN_ARROW_687 -> BrokenItem.EquipmentType.ARROWS
                Items.BROKEN_STAFF_689 -> BrokenItem.EquipmentType.STAVES
                Items.RUSTY_SWORD_686 -> BrokenItem.EquipmentType.SWORDS
                Items.DAMAGED_ARMOUR_697 -> BrokenItem.EquipmentType.ARMOUR
                Items.BROKEN_ARMOUR_698 -> BrokenItem.EquipmentType.LEGS
                else -> return
            }

        getRepair(repairType)?.let { repairedItem ->
            player.inventory.remove(item)
            player.inventory.add(repairedItem)
            sendItemDialogue(
                player,
                repairedItem.id,
                "You repair the ${getItemName(item.id)} and find it is a ${repairedItem.name.lowercase()}.",
            )
        }
    }

    inner class RepairDialogue(
        val item: Item,
        var cost: Int,
        var product: Item,
    ) : DialogueFile() {
        override fun handle(
            componentID: Int,
            buttonID: Int,
        ) {
            when (stage) {
                0 ->
                    sendDialogue(
                        player!!,
                        "Would you like to repair your ${getItemName(item.id)} for $cost gp?",
                    ).also { stage++ }
                1 -> options("Yes, please", "No, thanks.").also { stage++ }
                2 ->
                    when (buttonID) {
                        1 -> {
                            end()
                            val coins = Item(Items.COINS_995, cost)
                            if (player!!.inventory.containsItem(coins) && player!!.inventory.containsItem(item)) {
                                player!!.inventory.remove(item, coins)
                                player!!.inventory.add(product)
                                sendDialogue(player!!, "You repair your ${getItemName(product.id)} for $cost coins.")
                            } else {
                                sendDialogue(player!!, "You can't afford that.")
                            }
                        }

                        2 -> end()
                    }
            }
        }
    }
}
