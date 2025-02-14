package content.global.handlers.player

import org.rs.consts.Items
import org.rs.consts.Music
import core.api.*
import core.game.activity.ActivityManager
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.system.SystemManager
import core.game.world.GameWorld
import core.plugin.Initializable
import core.plugin.Plugin
import core.plugin.PluginManifest
import core.plugin.PluginType
import java.util.concurrent.TimeUnit

@Initializable
@PluginManifest(type = PluginType.LOGIN)
class LoginValidation : Plugin<Player> {

    private val QUEST_ITEMS = arrayOf(Item(Items.QUEST_POINT_CAPE_9813), Item(Items.QUEST_POINT_HOOD_9814))

    override fun fireEvent(identifier: String, vararg args: Any): Any? {
        return null
    }

    override fun newInstance(player: Player): Plugin<Player> {
        player.unlock()
        if (player.isArtificial) {
            return this
        }
        if (!SystemManager.systemConfig.validLogin(player)) {
            return this
        }
        if (GameWorld.settings!!.isDevMode) {
            player.toggleDebug()
        }
        if (player.getAttribute("fc_wave", -1) > -1) {
            ActivityManager.start(player, "fight caves", true)
        }
        if (player.getAttribute("falconry", false)) {
            ActivityManager.start(player, "falconry", true)
        }
        if (player.getSavedData().questData.getDragonSlayerAttribute("repaired")) {
            setVarp(player, 177, 1967876)
        }
        if (player.getSavedData().globalData.getLootShareDelay() < System.currentTimeMillis() && player.getSavedData().globalData.getLootShareDelay() != 0L) {
            player.globalData.setLootSharePoints((player.globalData.getLootSharePoints() - player.globalData.getLootSharePoints() * 0.10).toInt())
        } else {
            player.getSavedData().globalData.setLootShareDelay(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
        }

        if (!player.musicPlayer.hasUnlocked(Music.ADVENTURE_177)) {
            player.musicPlayer.unlock(Music.ADVENTURE_177)
        }

        if (!player.musicPlayer.hasUnlocked(Music.BITTERSWEET_BUNNY_502)) {
            player.musicPlayer.unlock(Music.BITTERSWEET_BUNNY_502)
        }

        if (!player.musicPlayer.hasUnlocked(Music.THE_DANCE_OF_THE_SNOW_QUEEN_593)) {
            player.musicPlayer.unlock(Music.THE_DANCE_OF_THE_SNOW_QUEEN_593)
        }

        if (!player.musicPlayer.hasUnlocked(Music.DIANGOS_LITTLE_HELPERS_532)) {
            player.musicPlayer.unlock(Music.DIANGOS_LITTLE_HELPERS_532)
        }

        if (!player.musicPlayer.hasUnlocked(Music.LAND_OF_SNOW_189)) {
            player.musicPlayer.unlock(Music.LAND_OF_SNOW_189)
        }

        if (!player.musicPlayer.hasUnlocked(Music.FUNNY_BUNNIES_603)) {
            player.musicPlayer.unlock(Music.FUNNY_BUNNIES_603)
        }

        if (!player.musicPlayer.hasUnlocked(Music.HIGH_SPIRITS_205)) {
            player.musicPlayer.unlock(Music.HIGH_SPIRITS_205)
        }

        if (!player.musicPlayer.hasUnlocked(Music.GRIMLY_FIENDISH_432)) {
            player.musicPlayer.unlock(Music.GRIMLY_FIENDISH_432)
        }

        if (!player.musicPlayer.hasUnlocked(Music.EASTER_JIG_273)) {
            player.musicPlayer.unlock(Music.EASTER_JIG_273)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SEA_SHANTY_XMAS_210)) {
            player.musicPlayer.unlock(Music.SEA_SHANTY_XMAS_210)
        }

        if (!player.musicPlayer.hasUnlocked(Music.JUNGLE_BELLS_209)) {
            player.musicPlayer.unlock(Music.JUNGLE_BELLS_209)
        }

        if (!player.musicPlayer.hasUnlocked(Music.JUNGLE_ISLAND_XMAS_208)) {
            player.musicPlayer.unlock(Music.JUNGLE_ISLAND_XMAS_208)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SCAPE_MAIN_16)) {
            player.musicPlayer.unlock(Music.SCAPE_MAIN_16)
        }

        if (!player.musicPlayer.hasUnlocked(Music.HOMESCAPE_621)) {
            player.musicPlayer.unlock(Music.SCAPE_MAIN_16)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SCAPE_HUNTER_207)) {
            player.musicPlayer.unlock(Music.SCAPE_HUNTER_207)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SCAPE_ORIGINAL_400)) {
            player.musicPlayer.unlock(Music.SCAPE_ORIGINAL_400)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SCAPE_SUMMON_457)) {
            player.musicPlayer.unlock(Music.SCAPE_SUMMON_457)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SCAPE_SANTA_547)) {
            player.musicPlayer.unlock(Music.SCAPE_SANTA_547)
        }

        if (!player.musicPlayer.hasUnlocked(Music.SCAPE_SCARED_321)) {
            player.musicPlayer.unlock(Music.SCAPE_SCARED_321)
        }

        if (!player.musicPlayer.hasUnlocked(Music.GROUND_SCAPE_466)) {
            player.musicPlayer.unlock(Music.GROUND_SCAPE_466)
        }
        checkQuestPointsItems(player)
        return this
    }

    private fun checkQuestPointsItems(player: Player) {
        if (!player.getQuestRepository().hasCompletedAll() &&
            anyInEquipment(player, Items.QUEST_POINT_CAPE_9813, Items.QUEST_POINT_HOOD_9814)) {
            var location1: String? = null
            var location2: String? = null
            var item1 = 0
            var item2 = 0
            var amt = 0
            for (i in QUEST_ITEMS) {
                if (removeItem(player, i, Container.EQUIPMENT)) {
                    amt++
                    var location: String
                    if (addItem(player, i.id, i.amount, Container.INVENTORY)) {
                        location = "your inventory"
                    } else if (addItem(player, i.id, i.amount, Container.BANK)) {
                        location = "your bank"
                    } else {
                        location = "the Wise Old Man"
                        if (i.id == Items.QUEST_POINT_CAPE_9813) {
                            setAttribute(player, "/save:reclaim-qp-cape", true)
                        } else {
                            setAttribute(player, "/save:reclaim-qp-hood", true)
                        }
                    }
                    if (amt == 1) {
                        item1 = i.id
                        location1 = location
                    }
                    if (amt == 2) {
                        item2 = i.id
                        location2 = location
                    }
                }
            }
            if (amt == 2) {
                sendDoubleItemDialogue(player, item1, item2, "As you no longer have completed all the quests, your " + getItemName(item1) + " unequips itself to " + location1 + " and your " + getItemName(item2) + " unequips itself to " + location2 + "!")
            } else {
                sendItemDialogue(player, item1, "As you no longer have completed all the quests, your " + getItemName(item1) + " unequips itself to " + location1 + "!")
            }
        }
    }
}