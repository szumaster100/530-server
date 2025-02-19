package content.region.kandarin.quest.murder

import core.api.addItem
import core.api.removeAttributes
import core.api.rewardXP
import core.api.sendItemZoomOnInterface
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.quest.Quest
import core.game.node.entity.skill.Skills
import core.plugin.Initializable
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.Quests
import org.rs.consts.Vars

@Initializable
class MurderMystery : Quest(Quests.MURDER_MYSTERY, 93, 92, 3, Vars.VARP_QUEST_MURDER_MYSTERY_PROGRESS_192, 0, 1, 2) {
    override fun drawJournal(
        player: Player,
        stage: Int,
    ) {
        super.drawJournal(player, stage)
        var line = 11
        if (stage >= 0) {
            line(player, "I can start this quest by speaking to one of the !!Guards?? at", line++)
            line(player, "the !!Sinclar Mansion??, North of the !!Seer's Village.??", line++)
            line++
        }
        if (stage >= 1) {
            line(player, "One of the guards asked me for my help in solving the murder.", line++, stage >= 2)
            line++
        }
        if (stage >= 2) {
            line(
                player,
                "After careful examination of the crime scene and interrogating all suspects,",
                line++,
                stage >= 3,
            )
            line(player, "I worked out who was guilty.", line++, stage >= 3)
            line++
        }
        if (stage >= 3) {
            line(player, "I took the evidence I had collected to the Guards", line++, stage >= 4)
            line(player, "and explained how it could identify the killer.", line++, stage >= 4)
            line++
        }
        if (stage >= 5) {
            line(player, "Impressed with my deductions, the killer was arrested", line++, true)
            line(player, "and I was given a fair reward for my help in solving the crime.", line++, true)
            line++
        }
        if (stage == 100) {
            line(player, "<col=FF0000>QUEST COMPLETE!", line, false)
        }
    }

    override fun finish(player: Player) {
        super.finish(player)
        var ln = 10
        sendItemZoomOnInterface(player, Components.QUEST_COMPLETE_SCROLL_277, 5, Items.COINS_6964, 230)
        drawReward(player, "3 Quest Points", ln++)
        drawReward(player, "2000 Coins", ln)
        drawReward(player, "1406 Crafting XP", ln)
        rewardXP(player, Skills.CRAFTING, 1406.0)
        addItem(player, Items.COINS_995, 2000)
        removeAttributes(
            player,
            MurderMysteryUtils.ATTRIBUTE_ANNA,
            MurderMysteryUtils.ATTRIBUTE_DAVID,
            MurderMysteryUtils.ATTRIBUTE_ELIZABETH,
        )
    }

    override fun newInstance(`object`: Any?): Quest {
        return this
    }
}
