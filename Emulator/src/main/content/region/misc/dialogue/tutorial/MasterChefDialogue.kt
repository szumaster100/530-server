package content.region.misc.dialogue.tutorial

import content.region.misc.handlers.tutorial.TutorialStage
import core.api.*
import core.game.component.Component
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs

@Initializable
class MasterChefDialogue(
    player: Player? = null,
) : Dialogue(player) {
    val bucket = Item(Items.BUCKET_1925, 1)
    val pot = Item(Items.EMPTY_POT_1931, 1)

    override fun open(vararg args: Any?): Boolean {
        npc = args[0] as NPC
        when (getAttribute(player, TutorialStage.TUTORIAL_STAGE, 0)) {
            18 ->
                Component.setUnclosable(
                    player,
                    interpreter.sendDialogues(
                        npc,
                        FaceAnim.FRIENDLY,
                        "Ah! Welcome, newcomer. I am the Master Chef, Lev. It",
                        "is here I will teach you how to cook food truly fit for a",
                        "king.",
                    ),
                )

            19, 20 -> sendNPCDialogue(player, npc.id, "Hello again.", FaceAnim.FRIENDLY)
            in 21..25 -> {
                setTitle(player, 3)
                sendDialogueOptions(
                    player,
                    title = "What would you like to hear more about?",
                    "Making dough.",
                    "Range cooking.",
                    "Nothing, thanks.",
                )
                stage++
            }
        }
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (getAttribute(player, TutorialStage.TUTORIAL_STAGE, 0)) {
            18 ->
                when (stage) {
                    0 ->
                        sendTutorialNPCDialogue(
                            player,
                            npc.id,
                            "I already know how to cook. Brynna taught me just",
                            "now.",
                        ).also {
                            stage++
                        }
                    1 ->
                        sendTutorialNPCDialogue(
                            player,
                            npc.id,
                            "Hahahahahaha! You call THAT cooking? Some shrimp",
                            "on an open log fire? Oh, no, no no. I am going to",
                            "teach you the fine art of cooking bread.",
                        ).also {
                            stage++
                        }
                    2 ->
                        sendTutorialNPCDialogue(
                            player,
                            npc.id,
                            "And no fine meal is complete without good music, so",
                            "we'll cover that while you're here too.",
                        ).also {
                            stage++
                        }
                    3 -> {
                        Component.setUnclosable(
                            player,
                            interpreter.sendDoubleItemMessage(
                                Items.BUCKET_OF_WATER_1929,
                                Items.POT_OF_FLOUR_1933,
                                "The Cooking Guide gives you a <col=08088A>bucket of water<col> and a <col=08088A>pot of flour</col>.",
                            ),
                        )
                        addItem(player, Items.BUCKET_OF_WATER_1929)
                        addItem(player, Items.POT_OF_FLOUR_1933)
                        stage++
                    }

                    4 -> {
                        end()
                        setAttribute(player, TutorialStage.TUTORIAL_STAGE, 19)
                        TutorialStage.load(player, 19)
                    }
                }

            19, 20 ->
                when (stage) {
                    0 -> {
                        if (!player.inventory.containsItems(
                                Item(Items.BUCKET_OF_WATER_1929, 1),
                                Item(Items.POT_OF_FLOUR_1933, 1),
                            ) ||
                            player.inventory.containsItems(Item(Items.BUCKET_1925, 1), Item(Items.EMPTY_POT_1931, 1))
                        ) {
                            removeItem(player, Item(Items.EMPTY_POT_1931, 1), Container.INVENTORY)
                            removeItem(player, Item(Items.BUCKET_1925, 1), Container.INVENTORY)
                            sendDoubleItemDialogue(
                                player,
                                Items.BUCKET_OF_WATER_1929,
                                Items.POT_OF_FLOUR_1933,
                                "The Cooking Guide gives you a <col=08088A>bucket of water<col> and a <col=08088A>pot of flour</col>.",
                            )
                            addItem(player, Items.BUCKET_OF_WATER_1929, 1, Container.INVENTORY)
                            addItem(player, Items.POT_OF_FLOUR_1933, 1, Container.INVENTORY)
                            TutorialStage.load(player, 19)
                            return true
                        }
                        if (!player.inventory.containsItems(Item(Items.BUCKET_OF_WATER_1929, 1)) &&
                            removeItem(player, Items.BUCKET_1925)
                        ) {
                            sendItemDialogue(
                                player,
                                Items.BUCKET_OF_WATER_1929,
                                "The Master Chef gives you another bucket of water.",
                            )
                            addItem(player, Items.BUCKET_OF_WATER_1929, 1)
                            TutorialStage.load(player, 19)
                            return true
                        }
                        if (!player.inventory.containsItems(Item(Items.POT_OF_FLOUR_1933, 1)) &&
                            removeItem(player, Item(Items.EMPTY_POT_1931))
                        ) {
                            Component.setUnclosable(
                                player,
                                interpreter.sendItemMessage(
                                    Items.POT_OF_FLOUR_1933,
                                    "The Master Chef gives you another pot of flour.",
                                ),
                            )
                            addItem(player, Items.POT_OF_FLOUR_1933, 1)
                            TutorialStage.load(player, 19)
                            return true
                        }
                        return false
                    }
                }

            in 21..26 ->
                when (buttonId) {
                    0 -> player("Making dough.").also { stage = 150 }
                    1 -> player("Range cooking.").also { stage = 200 }
                    2 -> TutorialStage.rollback(player)
                }
            150 ->
                sendNPCDialogue(
                    player,
                    npc.id,
                    "This is the base for many of the meals. To make dough we must mix flour and water. First, right click the bucket of water and select use, then left click on the pot of flour.",
                ).also {
                    stage =
                        1000
                }
            200 ->
                sendNPCDialogue(player, npc.id, "To cook the dough, use it with the range shown by the arrow.").also {
                    stage =
                        1000
                }
            in 27..100 ->
                sendNPCDialogue(player, npc.id, "Follow the path to the home of the quest guide.").also {
                    stage =
                        1000
                }
            1000 -> {
                stage = END_DIALOGUE
                TutorialStage.rollback(player)
            }
        }
        return true
    }

    override fun newInstance(player: Player?): Dialogue {
        return MasterChefDialogue(player)
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.MASTER_CHEF_942)
    }
}
