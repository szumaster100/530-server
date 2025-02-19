package content.global.ame.sandwichlady

import content.data.GameAttributes
import content.global.ame.RandomEventNPC
import core.api.utils.WeightBasedTable
import core.game.node.entity.npc.NPC
import core.tools.RandomFunction
import org.rs.consts.Items
import org.rs.consts.NPCs

class SandwichLadyNPC(
    override var loot: WeightBasedTable? = null,
) : RandomEventNPC(NPCs.SANDWICH_LADY_3117) {
    val phrases =
        arrayOf(
            "Hello, @name, can you hear me?",
            "Sandwiches, @name!",
            "Are you ignoring me @name??",
            "Yoohoo! Sandwiches, @name!",
            "Hello, @name?",
            "Come get your sandwiches @name!",
            "How could you ignore me like this @name?!",
            "Do you even want your sandwiches, @name?",
        )
    var assigned_item = 0
    val items =
        arrayOf(
            Items.BAGUETTE_6961,
            Items.TRIANGLE_SANDWICH_6962,
            Items.SQUARE_SANDWICH_6965,
            Items.ROLL_6963,
            Items.MEAT_PIE_2327,
            Items.KEBAB_1971,
            Items.CHOCOLATE_BAR_1973,
        )

    override fun tick() {
        if (RandomFunction.random(1, 15) == 5) {
            sendChat(
                phrases.random().replace(
                    "@name",
                    player.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                ),
            )
        }
        super.tick()
    }

    override fun init() {
        super.init()
        assignItem()
        sendChat(
            phrases.random().replace(
                "@name",
                player.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
            ),
        )
    }

    fun assignItem() {
        assigned_item = items.random()
        player.setAttribute(GameAttributes.S_LADY_ITEM, assigned_item)
    }

    override fun talkTo(npc: NPC) {
        player.dialogueInterpreter.open(SandwichLadyDialogue(false), npc)
    }
}
