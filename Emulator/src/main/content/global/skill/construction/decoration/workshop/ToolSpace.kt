package content.global.skill.construction.decoration.workshop

import core.api.*
import core.cache.def.impl.ItemDefinition
import core.cache.def.impl.SceneryDefinition
import core.game.dialogue.Dialogue
import core.game.dialogue.DialogueInterpreter
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import org.rs.consts.Items
import org.rs.consts.Scenery

@Initializable
class ToolSpace : OptionHandler() {
    private enum class ToolStore(
        val objectId: Int,
        vararg val tools: Int,
    ) {
        TOOLSTORE_1(Scenery.TOOLS_13699, Items.SAW_8794, Items.CHISEL_1755, Items.HAMMER_2347, Items.SHEARS_1735),
        TOOLSTORE_2(Scenery.TOOLS_13700, Items.BUCKET_1925, Items.SPADE_952, Items.TINDERBOX_590),
        TOOLSTORE_3(Scenery.TOOLS_13701, Items.BROWN_APRON_1757, Items.GLASSBLOWING_PIPE_1785, Items.NEEDLE_1733),
        TOOLSTORE_4(
            Scenery.TOOLS_13702,
            Items.AMULET_MOULD_1595,
            Items.NECKLACE_MOULD_1597,
            Items.RING_MOULD_1592,
            Items.HOLY_MOULD_1599,
            Items.TIARA_MOULD_5523,
        ),
        TOOLSTORE_5(
            Scenery.TOOLS_13703,
            Items.RAKE_5341,
            Items.SPADE_952,
            Items.TROWEL_676,
            Items.SEED_DIBBER_5343,
            Items.WATERING_CAN_5331,
        ),
        ;

        companion object {
            @JvmStatic
            fun forId(objectId: Int): ToolStore? {
                for (t in values()) {
                    if (t.objectId == objectId) {
                        return t
                    }
                }
                return null
            }
        }
    }

    @Throws(Throwable::class)
    override fun newInstance(arg: Any?): Plugin<Any> {
        for (t in ToolStore.values()) {
            SceneryDefinition.forId(t.objectId).handlers["option:search"] = this
        }
        definePlugin(ToolDialogue())
        return this
    }

    override fun handle(
        player: Player,
        node: Node,
        option: String,
    ): Boolean {
        val `object` = node.asScenery()
        val n = ToolStore.forId(`object`.id)

        if (player.houseManager.isBuildingMode && !player.isAdmin) {
            player.sendMessage("You can't do this in build mode.")
            return true
        }

        if (n != null) {
            player.dialogueInterpreter.open(DialogueInterpreter.getDialogueKey("con:tools"), n)
        }

        return true
    }

    private inner class ToolDialogue : Dialogue {
        private var toolStore: ToolStore? = null

        internal constructor()

        internal constructor(player: Player?) : super(player)

        override fun newInstance(player: Player?): Dialogue {
            return ToolDialogue(player)
        }

        override fun open(vararg args: Any?): Boolean {
            toolStore = args[0] as ToolStore?
            val itemNames: MutableList<String> = ArrayList()
            for (itemId in toolStore!!.tools) {
                val n: ItemDefinition = ItemDefinition.forId(itemId)
                itemNames.add(n.name)
            }

            sendDialogueOptions(player, "Select a Tool", *itemNames.toTypedArray())
            stage = 1
            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            when (stage) {
                1 -> {
                    val item = Item(toolStore!!.tools[buttonId - 1], 1)
                    if (freeSlots(player) <= 0) {
                        sendDialogue(player, "You have no space in your inventory.")
                        stage = 2
                        return true
                    }
                    addItem(player, item.id)
                    end()
                    return true
                }

                2 -> {
                    end()
                    return true
                }
            }
            return false
        }

        override fun getIds(): IntArray {
            return intArrayOf(DialogueInterpreter.getDialogueKey("con:tools"))
        }
    }
}
