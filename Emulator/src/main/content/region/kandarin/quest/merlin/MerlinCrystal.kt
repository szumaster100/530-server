package content.region.kandarin.quest.merlin

import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.Quests
import org.rs.consts.Vars
import content.region.kandarin.quest.merlin.handlers.MerlinUtils
import core.api.*
import core.api.quest.updateQuestTab
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.quest.Quest
import core.plugin.Initializable

@Initializable
class MerlinCrystal : Quest(Quests.MERLINS_CRYSTAL, 87, 86, 6, Vars.VARP_QUEST_MERLIN_CRYSTAL_PROGRESS_14, 0, 1, 7) {

    override fun drawJournal(player: Player, stage: Int) {
        super.drawJournal(player, stage)
        player ?: return
        var ln = 11

        when (stage) {
            0 -> {
                line(
                    player,
                    "<blue>I can start this quest by speaking to <red>King Arthur<blue> at<n> <red>Camelot Castle<blue>, just <red>North West of Catherby<n><blue>I must be able to defeat a <red>level 37 enemy",
                    11
                )
            }

            10 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "I should ask the !!other Knights?? if they have any !!advice?? for", ln++, false)
                line(player, "me on how I should go about doing this.", ln++, false)
            }

            20 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "!!Gawain?? thinks this was the work of !!Morgan Le Faye?? who", ln++, false)
                line(player, "lives in an !!impenetrable fortress?? south of here full of", ln++, false)
                line(player, "!!renegade knights?? led by the evil !!Sir Mordred??", ln++, false)
            }

            30 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "Gawain told me it was the work of Morgan Le Faye.", ln++, true)
                line(player, "I told Lancelot of Gawain's suspicions, and he told me that", ln++, true)
                line(player, "Mordred's Fortress is not completely impenetratable.", ln++, true)
                line(player, "There might be a way to enter with a !!delivery by sea??...", ln++, false)
            }

            40 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "Gawain told me it was the work of Morgan Le Faye.", ln++, true)
                line(player, "I told Lancelot of Gawain's suspicions, and he told me that", ln++, true)
                line(player, "Mordred's Fortress is not completely impenetratable.", ln++, true)
                line(player, "I stowed away in a shipment of candles and reached the", ln++, true)
                line(player, "Stronghold of Sir Mordred, and bested him in combat.", ln++, true)
                line(player, "In return for sparing her son, Morgan Le Faye told me how I", ln++, true)
                line(player, "could break the spell that had imprisoned Merlin.", ln++, true)
                line(player, "I need to summon the spirit !!Thrantax?? at a !!magic symbol?? as", ln++, false)
                line(player, "close to the Crystal as I can possibly find.", ln++, false)
                line(player, "To bind !!Thrantax?? to my will I will need the following:", ln++, false)

                if (player.getAttribute(MerlinUtils.ATTR_STATE_ALTAR_FINISH, false) == true) {
                    line(player, "I have the magic words from the Chaos Altar memorised", ln++, true)
                } else {
                    line(player, "Some !!magic words?? from a !!Chaos Altar??", ln++, false)
                }

                if (inInventory(player, Items.LIT_BLACK_CANDLE_32, 1)) {
                    line(player, "I have a lit black candle with me", ln++, true)
                } else {
                    line(player, "A !!lit Black Candle??", ln++, false)
                }

                if (inInventory(player, Items.EXCALIBUR_35, 1)) {
                    line(player, "I have the Holy Sword Excalibur with me", ln++, true)
                } else {
                    line(player, "The !!Holy Sword Excalibur?? from the !!Lady of the Lake??", ln++, false)
                }

                if (inInventory(player, Items.BAT_BONES_530, 1)) {
                    line(player, "I have some bat bones with me", ln++, true)
                } else {
                    line(player, "Some !!bat bones??", ln++, false)
                }
            }

            50 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "Gawain told me it was the work of Morgan Le Faye.", ln++, true)
                line(player, "I told Lancelot of Gawain's suspicions, and he told me that", ln++, true)
                line(player, "Mordred's Fortress is not completely impenetratable.", ln++, true)
                line(player, "I stowed away in a shipment of candles and reached the", ln++, true)
                line(player, "Stronghold of Sir Mordred, and bested him in combat.", ln++, true)
                line(player, "In return for sparing her son, Morgan Le Faye told me how I", ln++, true)
                line(player, "could break the spell that had imprisoned Merlin.", ln++, true)
                line(player, "I need to summon the spirit Thrantax at a magic symbol as", ln++, true)
                line(player, "close to the Crystal as I can possibly find.", ln++, true)
                line(player, "I summoned the Spirit Thrantax and forced him to aid me in", ln++, true)
                line(player, "breaking Morgan Le Fayes curse upon Merlin. He used his", ln++, true)
                line(player, "magic to weaken the crystal so that a blow from Excalibur", ln++, true)
                line(player, "would be able to shatter it for good.", ln++, true)
                line(player, "I should free !!Merlin?? by using !!Excalibur?? on the !!crystal??", ln++, false)
            }

            60 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "Gawain told me it was the work of Morgan Le Faye.", ln++, true)
                line(player, "I told Lancelot of Gawain's suspicions, and he told me that", ln++, true)
                line(player, "Mordred's Fortress is not completely impenetratable.", ln++, true)
                line(player, "I stowed away in a shipment of candles and reached the", ln++, true)
                line(player, "Stronghold of Sir Mordred, and bested him in combat.", ln++, true)
                line(player, "In return for sparing her son, Morgan Le Faye told me how I", ln++, true)
                line(player, "could break the spell that had imprisoned Merlin.", ln++, true)
                line(player, "I need to summon the spirit Thrantax<blue> at a magic symbol as", ln++, true)
                line(player, "<blue>close to the Crystal as I can possibly find.", ln++, true)
                line(player, "I summoned the Spirit Thrantax and forced him to aid me in", ln++, true)
                line(player, "breaking Morgan Le Fayes curse upon Merlin. He used his", ln++, true)
                line(player, "magic to weaken the crystal so that a blow from Excalibur", ln++, true)
                line(player, "would be able to shatter it for good.", ln++, true)
                line(player, "I freed Merlin by shattering the Crystal that trapped him", ln++, true)
                line(player, "Now that !!Merlin?? is freed I should speak to !!King Arthur?? to", ln++, false)
                line(player, "claim my !!reward?? and become a !!Knight of the Round Table??", ln++, false)
            }

            100 -> {
                line(player, "I spoke to King Arthur and he said I would be worthy of", ln++, true)
                line(player, "becoming a Knight of the Round Table if I could free Merlin", ln++, true)
                line(player, "from a giant crystal that he has been trapped in.", ln++, true)
                line(player, "Gawain told me it was the work of Morgan Le Faye.", ln++, true)
                line(player, "I told Lancelot of Gawain's suspicions, and he told me that", ln++, true)
                line(player, "Mordred's Fortress is not completely impenetratable.", ln++, true)
                line(player, "I stowed away in a shipment of candles and reached the", ln++, true)
                line(player, "Stronghold of Sir Mordred, and bested him in combat.", ln++, true)
                line(player, "In return for sparing her son, Morgan Le Faye told me how I", ln++, true)
                line(player, "could break the spell that had imprisoned Merlin.", ln++, true)
                line(player, "I need to summon the spirit Thrantax at a magic symbol as", ln++, true)
                line(player, "close to the Crystal as I can possibly find.", ln++, true)
                line(player, "I summoned the Spirit Thrantax and forced him to aid me in", ln++, true)
                line(player, "breaking Morgan Le Fayes curse upon Merlin. He used his", ln++, true)
                line(player, "magic to weaken the crystal so that a blow from Excalibur", ln++, true)
                line(player, "would be able to shatter it for good.", ln++, true)
                line(player, "I freed Merlin by shattering the Crystal that trapped him", ln++, true)
                line(player, "and when I told King Arthur how I had singlehandedly freed", ln++, true)
                line(player, "Merlin from his prison when his other Knights had failed he", ln++, true)
                line(player, "immediately made me a Knight of the Round Table", ln++, true)
                ln++
                line(player, "<col=FF0000>QUEST COMPLETE!", ln, false)
            }
        }
    }

    override fun newInstance(`object`: Any?): Quest {
        return this
    }

    override fun finish(player: Player) {
        super.finish(player)
        sendString(player, "6 Quest Points", 277, 8 + 2)
        sendString(player, "Excalibur", 277, 9 + 2)
        sendItemZoomOnInterface(player, Components.QUEST_COMPLETE_SCROLL_277, 5, Items.EXCALIBUR_35, 235)
        removeAttributes(
            player,
            MerlinUtils.ATTR_STATE_ALTAR_FINISH,
            MerlinUtils.ATTR_STATE_CLAIM_EXCALIBUR,
            MerlinUtils.ATTR_STATE_TALK_LADY,
            MerlinUtils.ATTR_STATE_TALK_BEGGAR,
            MerlinUtils.ATTR_STATE_TALK_CANDLE
        )
        updateQuestTab(player)
        setVarbit(player, Vars.VARBIT_SCENERY_MUSEUM_DISPLAY_9_3655, 1, true)
    }
}