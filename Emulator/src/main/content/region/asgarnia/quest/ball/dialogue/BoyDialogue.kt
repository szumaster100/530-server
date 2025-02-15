package content.region.asgarnia.quest.ball.dialogue

import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.plugin.Initializable
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class BoyDialogue(
    player: Player? = null,
) : Dialogue(player) {
    override fun open(vararg args: Any): Boolean {
        val quest = player.getQuestRepository().getQuest(Quests.WITCHS_HOUSE)
        player.debug(quest.isStarted(player).toString() + " " + quest.getStage(player))
        if (!quest.isStarted(player) && quest.getStage(player) < 10) {
            player("Hello young man.")
            setStage(1)
            return true
        }
        if (quest.isCompleted(player) || quest.getStage(player) == 100) {
            sendDialogue("The boy is too busy playing with his ball to talk.")
            finish()
            return true
        }
        if (!player.inventory.containsItem(BALL)) {
            npc(FaceAnim.CHILD_GUILTY, "Have you gotten my ball back yet?")
        } else {
            player("Hi, I have got your ball back. It was MUCH harder", "than I thought it would be.")
        }
        setStage(11)
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        val quest = player.getQuestRepository().getQuest(Quests.WITCHS_HOUSE)
        when (stage) {
            -1 -> end()
            1 -> {
                sendDialogue("The boy sobs.")
                next()
            }

            2 -> {
                options("What's the matter?", "Well if you're not going to answer then I'll go.")
                next()
            }

            3 ->
                when (buttonId) {
                    1 -> {
                        player("What's the matter?")
                        setStage(5)
                    }

                    2 -> {
                        player("Well if you're not going to answer then I'll go.")
                        next()
                    }
                }

            4 -> {
                sendDialogue("The boy sniffs slightly.")
                finish()
            }

            5 -> {
                npc(
                    FaceAnim.CHILD_SAD,
                    "I've kicked my ball over that hedge, into that garden!",
                    "The old lady who lives there is scary... She's locked the",
                    "ball in her wooden shed! Can you get my ball back for",
                    "me please?",
                )
                next()
            }

            6 -> {
                options("Ok, I'll see what I can do.", "Get it back yourself.")
                next()
            }

            7 ->
                when (buttonId) {
                    1 -> {
                        player("Ok, I'll see what I can do.")
                        setStage(10)
                    }

                    2 -> {
                        player("Get it back yourself.")
                        next()
                    }
                }

            8 -> {
                npc(FaceAnim.CHILD_SAD, "You're a meany.")
                next()
            }

            9 -> {
                sendDialogue("The boy starts crying again.")
                finish()
            }

            10 -> {
                npc(FaceAnim.CHILD_FRIENDLY, "Thanks mister!")
                finish()
                quest.start(player)
            }

            11 ->
                if (!player.inventory.containsItem(BALL)) {
                    player("Not yet.")
                    next()
                } else {
                    if (player.inventory.remove(BALL)) interpreter.sendItemMessage(BALL, "You give the ball back.")
                    setStage(13)
                }

            12 -> {
                npc(FaceAnim.CHILD_GUILTY, "Well it's in the shed in that garden.")
                finish()
            }

            13 -> {
                npc(FaceAnim.CHILD_FRIENDLY, "Thank you so much!")
                next()
            }

            14 -> {
                quest.finish(player)
                finish()
            }
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(NPCs.BOY_895)
    }

    companion object {
        private val BALL = Item(Items.BALL_2407)
    }
}
