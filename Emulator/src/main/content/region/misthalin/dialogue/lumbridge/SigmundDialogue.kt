package content.region.misthalin.dialogue.lumbridge

import core.api.quest.getQuestStage
import core.api.setAttribute
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import org.rs.consts.Quests

@Initializable
class SigmundDialogue(
    player: Player? = null,
) : Dialogue(player) {
    var TLTNPCS = intArrayOf(278, 0, 519, 2244, 3777)

    override fun open(vararg args: Any): Boolean {
        npc = args[0] as NPC
        npc(FaceAnim.HALF_GUILTY, "Can I help you?")
        if (getQuestStage(player, Quests.THE_LOST_TRIBE) in 1..99) {
            npc("Have you found out what it was?")
            stage = 34
            return true
        }
        stage = 0
        return true
    }

    override fun handle(
        interfaceId: Int,
        buttonId: Int,
    ): Boolean {
        when (stage) {
            0 -> options("Do you have any quests for me?", "Who are you?").also { stage++ }
            1 ->
                when (buttonId) {
                    1 -> {
                        player(FaceAnim.HALF_GUILTY, "Do you have any quests for me?")
                        stage = 10
                    }

                    2 -> {
                        player(FaceAnim.HALF_GUILTY, "Who are you?")
                        stage = 20
                    }
                }

            20 -> {
                npc(FaceAnim.HALF_GUILTY, "I'm the Duke's advisor.")
                stage = 21
            }

            21 -> {
                player(FaceAnim.HALF_GUILTY, "Can you give me any advice then?")
                stage = 22
            }

            22 -> {
                npc(
                    FaceAnim.HALF_GUILTY,
                    "I only advise the Duke. But if you want to make",
                    "yourself useful, there are evil goblins to slay on the",
                    "other side of the river.",
                )
                stage = 23
            }

            23 -> end()
            10 -> {
                if (player.getQuestRepository().hasStarted(Quests.THE_LOST_TRIBE) &&
                    !player
                        .getQuestRepository()
                        .isComplete(Quests.THE_LOST_TRIBE)
                ) {
                    npc("No, not right now.")
                    stage = 12
                }
                if (player.getQuestRepository().isComplete(Quests.GOBLIN_DIPLOMACY) &&
                    player
                        .getQuestRepository()
                        .isComplete(Quests.RUNE_MYSTERIES) &&
                    !player
                        .getQuestRepository()
                        .hasStarted(Quests.THE_LOST_TRIBE)
                ) {
                    npc("There was recently some damage to the castle cellar.", "Part of the wall has collapsed.")
                    stage = 30
                }
                npc(
                    FaceAnim.HALF_GUILTY,
                    "I hear the Duke has a task for an adventurer.",
                    "Otherwise, if you want to make yourself useful, there",
                    "are always evil monsters to slay.",
                )
                stage = 11
            }

            11 -> {
                player(FaceAnim.HALF_GUILTY, "Okay, I might just do that.")
                stage = 12
            }

            12 -> end()
            30 -> {
                npc("The Duke insists that it was an earthquake, but I think", "some kind of monsters are to blame.")
                stage++
            }

            31 -> {
                npc("You should ask other people around the town if they", "saw anything.")
                stage = END_DIALOGUE
                player.getQuestRepository().getQuest(Quests.THE_LOST_TRIBE).start(player)
                setAttribute(player, "/save:tlt-witness", TLTNPCS[0])
            }

            34 -> {
                player("No...")
                stage = END_DIALOGUE
            }
        }
        return true
    }

    override fun getIds(): IntArray {
        return intArrayOf(2082, 2083, 2090, 3713, 3716, 3717, 3718, 3719, 3720, 4328, 4331, 4332, 4333, 4334, 4335)
    }
}
