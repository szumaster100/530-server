package content.global.skill.construction.decoration.workshop

import content.data.GameAttributes
import content.global.skill.construction.BuildingUtils
import content.global.skill.construction.Decoration
import core.api.*
import core.cache.def.impl.SceneryDefinition
import core.game.dialogue.Dialogue
import core.game.dialogue.DialogueInterpreter
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items

@Initializable
class ClockmakingSpace : OptionHandler() {
    internal enum class Craftable(
        val itemId: Int,
        val craftingLevel: Int,
        vararg materials: Item,
    ) {
        TOY_HORSEY(itemId = Items.TOY_HORSEY_2520, craftingLevel = 10, BuildingUtils.PLANK),
        CLOCKWORK(itemId = Items.CLOCKWORK_8792, craftingLevel = 8, Item(Items.STEEL_BAR_2353)),
        TOY_SOLDIER(
            itemId = Items.TOY_SOLDIER_7759,
            craftingLevel = 13,
            BuildingUtils.PLANK,
            Item(Items.CLOCKWORK_8792),
        ),
        TOY_DOLL(itemId = Items.TOY_DOLL_7763, craftingLevel = 18, BuildingUtils.PLANK, Item(Items.CLOCKWORK_8792)),
        TOY_MOUSE(itemId = Items.TOY_MOUSE_7767, craftingLevel = 33, BuildingUtils.PLANK, Item(Items.CLOCKWORK_8792)),
        TOY_CAT(itemId = Items.CLOCKWORK_CAT_7771, craftingLevel = 85, BuildingUtils.PLANK, Item(Items.CLOCKWORK_8792)),
        WATCH(itemId = Items.WATCH_2575, craftingLevel = 28, Item(Items.CLOCKWORK_8792), Item(Items.STEEL_BAR_2353)),
        SEXTANT(itemId = Items.SEXTANT_2574, craftingLevel = 23, Item(Items.STEEL_BAR_2353)),
        ;

        val materials: Array<Item> = materials as Array<Item>
    }

    @Throws(Throwable::class)
    override fun newInstance(arg: Any?): Plugin<Any> {
        SceneryDefinition.forId(13709).handlers["option:craft"] = this
        SceneryDefinition.forId(13710).handlers["option:craft"] = this
        SceneryDefinition.forId(13711).handlers["option:craft"] = this
        SceneryDefinition.forId(13712).handlers["option:craft"] = this
        definePlugin(ClockmakerBenchDialogue())
        return this
    }

    override fun handle(
        player: Player,
        node: Node,
        option: String,
    ): Boolean {
        player.dialogueInterpreter.open(
            DialogueInterpreter.getDialogueKey(GameAttributes.CON_CLOCKMAKER_DIAL),
            node.asScenery(),
        )
        return true
    }

    private inner class ClockmakerBenchDialogue : Dialogue {
        var decoration: Decoration? = null

        internal constructor()

        internal constructor(player: Player?) : super(player)

        override fun newInstance(player: Player?): Dialogue {
            return ClockmakerBenchDialogue(player)
        }

        override fun open(vararg args: Any): Boolean {
            val scenery = args[0] as Scenery
            decoration = Decoration.forObjectId(scenery.id)
            if (decoration != null) {
                when (decoration) {
                    Decoration.CRAFTING_TABLE_1 ->
                        sendDialogueOptions(
                            player,
                            "Select an Option",
                            "Toy Horsey",
                            "Nevermind",
                        )

                    Decoration.CRAFTING_TABLE_2 ->
                        sendDialogueOptions(
                            player,
                            "Select an Option",
                            "Toy Horsey",
                            "Clockwork Mechanism",
                        )

                    Decoration.CRAFTING_TABLE_3 ->
                        sendDialogueOptions(
                            player,
                            "Select an Option",
                            "Toy Horsey",
                            "Clockwork Mechanism",
                            "Clockwork Devices",
                        )

                    Decoration.CRAFTING_TABLE_4 ->
                        sendDialogueOptions(
                            player,
                            "Select an Option",
                            "Toy Horsey",
                            "Clockwork Mechanism",
                            "Clockwork Devices",
                            "Watch",
                            "Sextant",
                        )

                    else -> {}
                }
            }
            stage = 1
            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            when (stage) {
                1 ->
                    when (buttonId) {
                        1, 2 -> {
                            if (decoration == Decoration.CRAFTING_TABLE_1 && buttonId == 2) {
                                end()
                                return true
                            }
                            craftItem(if (buttonId == 1) Craftable.TOY_HORSEY else Craftable.CLOCKWORK)
                            stage = 3
                        }

                        3 -> {
                            if (decoration == Decoration.CRAFTING_TABLE_3) {
                                sendDialogueOptions(player, "Select an Option", "Clockwork Soldier", "Clockwork Doll")
                            } else if (decoration == Decoration.CRAFTING_TABLE_4) {
                                sendDialogueOptions(
                                    player,
                                    "Select an Option",
                                    "Clockwork Soldier",
                                    "Clockwork Doll",
                                    "Clockwork Mouse",
                                    "Clockwork Cat",
                                )
                            }
                            stage = 2
                        }

                        4, 5 -> {
                            craftItem(if (buttonId == 4) Craftable.WATCH else Craftable.SEXTANT)
                            stage = 3
                        }
                    }

                2 -> {
                    when (buttonId) {
                        1 -> craftItem(Craftable.TOY_SOLDIER)
                        2 -> craftItem(Craftable.TOY_DOLL)
                        3 -> craftItem(Craftable.TOY_MOUSE)
                        4 -> craftItem(Craftable.TOY_CAT)
                    }
                    stage = 3
                }

                3 -> end()
            }
            return true
        }

        override fun getIds(): IntArray {
            return intArrayOf(DialogueInterpreter.getDialogueKey(GameAttributes.CON_CLOCKMAKER_DIAL))
        }

        fun craftItem(c: Craftable?) {
            if (c != null) {
                if (getStatLevel(player, Skills.CRAFTING) < c.craftingLevel) {
                    sendDialogue(player!!, "You need level " + c.craftingLevel + " crafting to make that.")
                    return
                }
                for (n in c.materials) {
                    if (!player.inventory.containsItem(n)) {
                        sendDialogue(player!!, "You need a " + getItemName(n.id) + " to make that.")
                        return
                    }
                }
                for (n in c.materials) {
                    n.amount = 1
                    player.inventory.remove(n)
                }
                rewardXP(player, Skills.CRAFTING, 15.0)
                addItem(player, c.itemId, 1)
                animate(player, BuildingUtils.BUILD_MID_ANIM)
                sendDialogue(player, "You made a " + getItemName(c.itemId) + ".")
            }
        }
    }
}
