package content.global.skill.herblore

import core.Util.findMatching
import core.api.quest.hasRequirement
import core.api.replaceSlot
import core.api.rewardXP
import core.api.sendMessage
import core.game.interaction.NodeUsageEvent
import core.game.interaction.UseWithHandler
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.plugin.Initializable
import core.plugin.Plugin
import it.unimi.dsi.fastutil.ints.IntArrayList
import org.rs.consts.Items
import org.rs.consts.Quests

@Initializable
class GuthixRestPlugin :
    UseWithHandler(Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251, Items.CLEAN_HARRALANDER_255) {
    private val herbIds = setOf(Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251, Items.CLEAN_HARRALANDER_255)

    enum class PartialTea(
        val ingredients: IntArrayList,
        val teaId: Int,
    ) {
        HERB_TEA_MIX_1(IntArrayList(intArrayOf(Items.CLEAN_HARRALANDER_255)), Items.HERB_TEA_MIX_4464),
        HERB_TEA_MIX_2(IntArrayList(intArrayOf(Items.CLEAN_GUAM_249)), Items.HERB_TEA_MIX_4466),
        HERB_TEA_MIX_3(IntArrayList(intArrayOf(Items.CLEAN_MARRENTILL_251)), Items.HERB_TEA_MIX_4468),
        HERB_TEA_MIX_4(
            IntArrayList(intArrayOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_MARRENTILL_251)),
            Items.HERB_TEA_MIX_4470,
        ),
        HERB_TEA_MIX_5(
            IntArrayList(intArrayOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_GUAM_249)),
            Items.HERB_TEA_MIX_4472,
        ),
        HERB_TEA_MIX_6(IntArrayList(intArrayOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249)), Items.HERB_TEA_MIX_4474),
        HERB_TEA_MIX_7(
            IntArrayList(intArrayOf(Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251)),
            Items.HERB_TEA_MIX_4476,
        ),
        HERB_TEA_MIX_8(
            IntArrayList(intArrayOf(Items.CLEAN_HARRALANDER_255, Items.CLEAN_MARRENTILL_251, Items.CLEAN_GUAM_249)),
            Items.HERB_TEA_MIX_4478,
        ),
        HERB_TEA_MIX_9(
            IntArrayList(intArrayOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_MARRENTILL_251)),
            Items.HERB_TEA_MIX_4480,
        ),
        HERB_TEA_MIX_10(
            IntArrayList(intArrayOf(Items.CLEAN_GUAM_249, Items.CLEAN_GUAM_249, Items.CLEAN_HARRALANDER_255)),
            Items.HERB_TEA_MIX_4482,
        ),
        COMPLETE_MIX(
            IntArrayList(
                intArrayOf(
                    Items.CLEAN_GUAM_249,
                    Items.CLEAN_GUAM_249,
                    Items.CLEAN_MARRENTILL_251,
                    Items.CLEAN_HARRALANDER_255,
                ),
            ),
            Items.GUTHIX_REST3_4419,
        ),
    }

    override fun handle(event: NodeUsageEvent?): Boolean {
        event ?: return false
        val player = event.player
        val fromItem = event.usedItem
        val toItem = event.baseItem

        return handleItemOnItemAction(player, fromItem, toItem, event.usedItem.slot, event.baseItem.slot)
    }

    private fun handleItemOnItemAction(
        player: Player,
        from: Item,
        to: Item,
        fromSlot: Int,
        toSlot: Int,
    ): Boolean {
        if (!hasRequirement(player, Quests.DRUIDIC_RITUAL) || !hasRequirement(player, Quests.ONE_SMALL_FAVOUR)) {
            return false
        }

        if (player.skills.getLevel(Skills.HERBLORE) < 18) {
            sendMessage(player, "You need a Herblore level of at least 18 to mix a Guthix Rest Tea.")
            return false
        }

        val (herb, mix) = if (isHerb(from)) from to to else to to from
        val existingIngredients =
            findMatching(PartialTea.values()) { it.teaId == mix.id }?.ingredients ?: IntArrayList()
        existingIngredients.add(herb.id)

        val upgradedTea = findMatching(PartialTea.values()) { it.ingredients.equals(existingIngredients) }
        if (upgradedTea == null) {
            sendMessage(player, "Nothing interesting happens.")
            return false
        }

        val inventory = player.inventory
        replaceSlot(player, if (mix == from) fromSlot else toSlot, Item(upgradedTea.teaId))
        inventory.remove(herb, if (herb == from) fromSlot else toSlot, true)

        val completionMessage =
            if (upgradedTea == PartialTea.COMPLETE_MIX) {
                " and make Guthix Rest Tea."
            } else {
                "."
            }
        sendMessage(
            player,
            "You place the ${herb.name.lowercase().replace(" leaf", "")} into the steamy mixture$completionMessage",
        )
        rewardXP(player, Skills.HERBLORE, 13.5 + existingIngredients.size * 0.5)

        return true
    }

    private fun isHerb(item: Item): Boolean {
        return item.id in herbIds
    }

    override fun newInstance(arg: Any?): Plugin<Any> {
        val herbTea =
            arrayOf(
                Items.HERB_TEA_MIX_4464,
                Items.HERB_TEA_MIX_4466,
                Items.HERB_TEA_MIX_4468,
                Items.HERB_TEA_MIX_4470,
                Items.HERB_TEA_MIX_4472,
                Items.HERB_TEA_MIX_4474,
                Items.HERB_TEA_MIX_4476,
                Items.HERB_TEA_MIX_4478,
                Items.HERB_TEA_MIX_4480,
                Items.HERB_TEA_MIX_4482,
            )
        herbTea.forEach { addHandler(it, ITEM_TYPE, this) }
        addHandler(Items.CUP_OF_HOT_WATER_4460, ITEM_TYPE, this)
        return this
    }
}
