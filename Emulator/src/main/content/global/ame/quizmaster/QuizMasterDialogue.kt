package content.global.ame.quizmaster

import content.data.GameAttributes
import core.api.*
import core.cache.def.impl.ItemDefinition
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.net.packet.PacketRepository
import core.net.packet.context.DisplayModelContext
import core.net.packet.context.DisplayModelContext.ModelType
import core.net.packet.out.DisplayModel
import core.plugin.Initializable
import core.tools.Log
import core.tools.RandomFunction
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.NPCs
import java.util.*

/**
 * Handles the quiz master dialogue.
 * @author Vexia
 */
@Initializable
class QuizMasterDialogue : Dialogue {
    /**
     * The wrong button id.
     */
    private var wrongId = 0

    constructor()

    constructor(player: Player?) : super(player)

    override fun newInstance(player: Player?): Dialogue {
        return QuizMasterDialogue(player)
    }

    override fun open(vararg args: Any?): Boolean {
        npc(
            "WELCOME to the GREATEST QUIZ SHOW in the",
            "whole of " + GameWorld.settings?.name + ".",
            "<col=7f0000>O <col=6f000f>D <col=5f001f>D <col=4f002f>O <col=3f003f>N <col=2f004f>E <col=1f005f>O <col=0f006f>U <col=00007f>T",
        )
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        var buttonId = buttonId
        when (stage) {
            0 -> player(FaceAnim.THINKING, "I'm sure I didn't ask to take part in a quiz show...").also { stage++ }
            1 -> {
                npc(
                    "Please welcome our newest contestant:",
                    RED + player.username,
                    "Just pick the ODD ONE OUT",
                    "Four questions right, and then you win!",
                )
                setAttribute(player, GameAttributes.RE_QUIZ_SCORE, 0)
                stage++
            }

            2 -> {
                display(QuizSet.quizSet)
                player.interfaceManager.openChatbox(Components.MACRO_QUIZSHOW_191)
                stage++
            }

            3 -> {
                /*
                 * TODO: I will fix it as soon as I can.
                 */
                buttonId -= 1
                val wrong = wrongId != buttonId
                if (wrong) {
                    setAttribute(player, GameAttributes.RE_QUIZ_SCORE, 0)
                } else {
                    player.incrementAttribute(GameAttributes.RE_QUIZ_SCORE)
                }
                if (player.getAttribute(GameAttributes.RE_QUIZ_SCORE, -1) == 4) {
                    npc(
                        BLUE + "CONGRATULATIONS!",
                        "You are a " + RED + "WINNER</col>!",
                        "Please choose your " + BLUE + "PRIZE</col>!",
                    )
                    stage = 4
                } else {
                    if (wrong) {
                        npc(
                            FaceAnim.NEUTRAL,
                            QuizMaster.WRONG.random(),
                            "You're supposed to pick the ODD ONE OUT.",
                            "Now, let's start again..."
                        )
                    } else {
                        npc(
                            FaceAnim.HAPPY,
                            QuizMaster.CORRECT.random(),
                            "Okay, next question!"
                        )
                    }
                    stage = 2
                }
            }

            4 -> options("1000 Coins", "Mystery Box").also { stage++ }
            5 -> {
                if (buttonId == 1) {
                    addItemOrBank(player, QuizMaster.COINS, 1000)
                } else {
                    addItemOrBank(player, QuizMaster.MYSTERY_BOX, 1)
                    setAttribute(player, GameAttributes.RE_QUIZ_REWARD, true)
                }
                end()
                QuizMaster.cleanup(player)
                submitIndividualPulse(
                    player,
                    object : Pulse(1) {
                        override fun pulse(): Boolean {
                            QuizMaster.cleanup(player)
                            return true
                        }
                    },
                )
                return false
            }

            else -> log(this::class.java, Log.WARN, RED + "UNHANDLED QUIZ STAGE $stage, $buttonId, $interfaceId")
        }
        return true
    }

    /**
     * Displays a quiz.
     * @param quiz the quiz.
     */
    private fun display(quiz: Array<QuizSet?>) {
        val correct = quiz[0]
        val wrong = quiz[1]
        val childs: MutableList<Int> = ArrayList()
        childs.add(1)
        childs.add(2)
        childs.add(3)
        childs.shuffle()
        for (i in 0..1) {
            sendItem(correct!!.getModel(i), childs[i])
        }
        wrongId = childs[2]
        sendItem(wrong!!.getModel(RandomFunction.random(wrong.ids.size)), wrongId)
    }

    /**
     * Sends an item.
     * @param model the model.
     * @param index the index.
     */
    private fun sendItem(
        model: Int,
        index: Int,
    ) {
        PacketRepository.send(
            DisplayModel::class.java,
            DisplayModelContext(player, ModelType.MODEL, model, 1, Components.MACRO_QUIZSHOW_191, 6 + index),
        )
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.QUIZ_MASTER_2477)
    }
}

/**
 * Represents a quiz set.
 * @author Vexia
 */
enum class QuizSet(
    internal vararg val ids: Int,
) {
    FISH(Items.NULL_6189, Items.NULL_6190),
    WEAPONRY(Items.NULL_6191, Items.NULL_6192),
    ARMOUR(Items.NULL_6193, Items.NULL_6194),
    TOOLS(Items.NULL_6195, Items.NULL_6196),
    JEWELLERY(Items.NULL_6197, Items.NULL_6198),
    ;

    /**
     * Gets the model id.
     * @param index the index.
     * @return the model.
     */
    fun getModel(index: Int): Int {
        return ItemDefinition.forId(ids[index]).interfaceModelId
    }

    companion object {
        val quizSet: Array<QuizSet?>
            /**
             * Gets the quiz set.
             * @return the set.
             */
            get() {
                val sets: MutableList<QuizSet?> = ArrayList()
                for (s in QuizSet.values()) {
                    sets.add(s)
                }
                sets.shuffle()
                val set = arrayOfNulls<QuizSet>(2)
                set[0] = sets[0]
                sets.remove(set[0])
                set[1] = sets[RandomFunction.random(sets.size)]
                return set
            }
    }
}
