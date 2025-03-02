package content.region.fremennik.quest.horror

import content.data.GameAttributes
import core.api.*
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.quest.Quest
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.Quests
import org.rs.consts.Vars

@Initializable
class HorrorFromTheDeep :
    Quest(Quests.HORROR_FROM_THE_DEEP, 77, 76, 2, Vars.VARBIT_QUEST_HORROR_FROM_THE_DEEP_PROGRESS_34, 0, 1, 10) {
    override fun drawJournal(
        player: Player,
        stage: Int,
    ) {
        super.drawJournal(player, stage)
        // TODO: Replace all stages with varbits.
        val progress = getVarbit(player, Vars.VARBIT_QUEST_HORROR_FROM_THE_DEEP_PROGRESS_34)
        var line = 11
        if (progress == 0) {
            line(player, "I can start this quest by speaking to !!Larrissa?? at the", line++)
            line(player, "!!Lighthouse?? to the !!North?? of the !!Barbarian Outpost??.", line++)
            line(player, "To complete this quest I need:", line++)
            line(
                player,
                if (hasLevelStat(player, Skills.AGILITY, 35)) "---Level 35 agility/--" else "!!Level 35 agility??",
                line++,
            )
            line(
                player,
                if (hasLevelStat(
                        player,
                        Skills.MAGIC,
                        13,
                    )
                ) {
                    "---Level 13 or higher magic will be an advantage/--"
                } else {
                    "!!Level 13 or higher magic will be an advantage??"
                },
                line++,
            )
            line(player, "!!I must also be able to defeat strong level 100 enemies??", line++)
            limitScrolling(player, line, true)
            line++
        }
        if (progress >= 1) {
            line(
                player,
                "I travelled to an isolated !!Lighthouse?? north of the !!Barbarian outpost??, ",
                line++,
                stage >= 2,
            )
            line(player, "to find a !!Fremennik?? girl called !!Larrissa?? locked outside, ", line++, stage >= 2)
            line(player, "and worried about her boyfriend !!Jossik??.", line++, stage >= 2)
            line++
        }
        if(stage >= 2) {
            line(
                player,
                "I recovered a !!spare key?? from Larrissa's cousin !!Gunnjorn??",
                line++,
                inInventory(player, Items.LIGHTHOUSE_KEY_3848) || stage >= 5,
            )
            line(
                player,
                "and repaired the !!bridge?? to Rellekka with some planks.",
                line++,
                getAttribute(player, GameAttributes.QUEST_HFTD_UNLOCK_BRIDGE, 0) == 2,
            )
            line++
        }
        if (stage >= 20) {
            line(player, "I should talk to !!Larrissa??.", line++, stage >= 40)
            line++
        }
        if (stage >= 40 && getAttribute(player, GameAttributes.QUEST_HFTD_STRANGE_WALL_DISCOVER, false)) {
            line(
                player,
                "After I entered the !!lighthouse??, and repaired the !!lighting mechanism??, ",
                line++,
                stage >= 55,
            )
            line(
                player,
                "I discovered a !!strange wall?? that blocked the entrance to an underground cavern, ",
                line++,
                stage >= 55,
            )
            line(player, "where !!Jossik?? was.", line++, stage >= 55)
            line++
        }
        if (stage >= 60) {
            line(player, "After I killed some strange !!sea monsters??, ", line++, stage >= 90)
            line(player, "I managed to get !!Jossik?? out of the cavern", line++, stage >= 90)
            line(player, "and back to the !!lighthouse??.", line++, stage >= 90)
            line++
        }
        if (stage >= 90) {
            line(player, "I found a !!strange casket?? on the dead body of the !!sea monster??, ", line++, stage >= 100)
            line(player, "which !!Jossik?? said he could tell me about.", line++, stage >= 100)
            line++
        }
        if (stage == 100) {
            line(player, "<col=FF0000>QUEST COMPLETE!</col>", line, false)
            limitScrolling(player, line, false)
        }
    }

    override fun finish(player: Player) {
        super.finish(player)
        var ln = 10
        /*
         * Trivia: The only one interface that has different text in journal.
         */
        sendString(player, "You have survived the ${Quests.HORROR_FROM_THE_DEEP}!", Components.QUEST_COMPLETE_SCROLL_277, 4)
        sendItemZoomOnInterface(player, Components.QUEST_COMPLETE_SCROLL_277, 5, Items.RUSTY_CASKET_3849)
        drawReward(player, "2 Quest Points", ln++)
        drawReward(player, "4662 XP in each of: Ranged,", ln++)
        drawReward(player, "Magic, Strength", ln)
        setVarbit(player, Vars.VARBIT_QUEST_HORROR_FROM_THE_DEEP_PROGRESS_34, 10, true)
        rewardXP(player, Skills.RANGE, 4662.0)
        rewardXP(player, Skills.MAGIC, 4662.0)
        rewardXP(player, Skills.STRENGTH, 4662.0)
        /*
        removeAttributes(
            player,
            GameAttributes.QUEST_HFTD_LIGHTHOUSE_MECHANISM,
            GameAttributes.QUEST_HFTD_UNLOCK_BRIDGE,
            GameAttributes.QUEST_HFTD_UNLOCK_DOOR,
            GameAttributes.QUEST_HFTD_STRANGE_WALL_DISCOVER,
            GameAttributes.QUEST_HFTD_USE_AIR_RUNE,
            GameAttributes.QUEST_HFTD_USE_FIRE_RUNE,
            GameAttributes.QUEST_HFTD_USE_EARTH_RUNE,
            GameAttributes.QUEST_HFTD_USE_WATER_RUNE,
            GameAttributes.QUEST_HFTD_USE_ARROW,
            GameAttributes.QUEST_HFTD_USE_SWORD,
        )
        */
    }

    override fun newInstance(`object`: Any?): Quest {
        return this
    }
}
