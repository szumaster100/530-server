package core.game.system.command.sets

import content.global.skill.crafting.spinning.Spinning
import content.global.skill.smithing.smelting.Bar
import content.global.skill.summoning.SummoningPouch
import core.api.addItem
import core.api.quest.finishQuest
import core.api.sendMessage
import core.api.teleport
import core.game.node.item.Item
import core.game.system.command.Privilege
import core.game.world.map.Location
import core.plugin.Initializable
import org.rs.consts.Items
import org.rs.consts.Quests

@Initializable
class ItemKitsCommandSet : CommandSet(Privilege.ADMIN) {
    private val farmKitItems =
        arrayListOf(
            Items.RAKE_5341,
            Items.SPADE_952,
            Items.SEED_DIBBER_5343,
            Items.WATERING_CAN8_5340,
            Items.SECATEURS_5329,
            Items.GARDENING_TROWEL_5325,
        )
    private val runeKitItems =
        arrayListOf(
            Items.AIR_RUNE_556,
            Items.EARTH_RUNE_557,
            Items.FIRE_RUNE_554,
            Items.WATER_RUNE_555,
            Items.MIND_RUNE_558,
            Items.BODY_RUNE_559,
            Items.DEATH_RUNE_560,
            Items.NATURE_RUNE_561,
            Items.CHAOS_RUNE_562,
            Items.LAW_RUNE_563,
            Items.COSMIC_RUNE_564,
            Items.BLOOD_RUNE_565,
            Items.SOUL_RUNE_566,
            Items.ASTRAL_RUNE_9075,
        )
    private val talismanKitItems =
        arrayListOf(
            Items.AIR_TALISMAN_1438,
            Items.EARTH_TALISMAN_1440,
            Items.FIRE_TALISMAN_1442,
            Items.WATER_TALISMAN_1444,
            Items.MIND_TALISMAN_1448,
            Items.BODY_TALISMAN_1446,
            Items.DEATH_TALISMAN_1456,
            Items.NATURE_TALISMAN_1462,
            Items.CHAOS_TALISMAN_1452,
            Items.LAW_TALISMAN_1458,
            Items.COSMIC_TALISMAN_1454,
            Items.BLOOD_TALISMAN_1450,
            Items.SOUL_TALISMAN_1460,
        )
    private val dyeKitItems =
        arrayListOf(
            Items.RED_DYE_1763,
            Items.YELLOW_DYE_1765,
            Items.BLUE_DYE_1767,
            Items.ORANGE_DYE_1769,
            Items.GREEN_DYE_1771,
            Items.PURPLE_DYE_1773,
            Items.BLACK_MUSHROOM_INK_4622,
            Items.PINK_DYE_6955,
        )

    override fun defineCommands() {
        define(
            name = "dyeskit",
            privilege = Privilege.ADMIN,
            usage = "::dyeskit",
            description = "Provides a set of dyes.",
        ) { player, _ ->
            for (item in dyeKitItems) {
                player.inventory.add(Item(item))
            }
            addItem(player, Items.RED_CAPE_1007, 8)
            return@define
        }

        define(
            name = "talismankit",
            privilege = Privilege.ADMIN,
            usage = "::talismankit",
            description = "Provides a set of talisman items.",
        ) { player, _ ->
            for (item in talismanKitItems) {
                player.inventory.add(Item(item))
            }
            return@define
        }

        define(
            name = "farmkit",
            privilege = Privilege.ADMIN,
            usage = "::farmkit",
            description = "Provides a kit of various farming equipment.",
        ) { player, _ ->
            for (item in farmKitItems) {
                player.inventory.add(Item(item))
            }
            return@define
        }

        define(
            name = "runekit",
            privilege = Privilege.ADMIN,
            usage = "::runekit",
            description = "Gives 1k of each Rune type.",
        ) { player, _ ->
            for (item in runeKitItems) {
                addItem(player, item, 1000)
            }
            return@define
        }

        define(
            name = "spinningkit",
            privilege = Privilege.ADMIN,
            usage = "::spinningkit",
            description = "Spinning items.",
        ) { player, _ ->
            val spinningWheelLocation = Location.create(3209, 3213, 1)
            for (spinning in Spinning.values()) {
                val needMessage = "Item need: ${spinning.need} -> Reward: ${spinning.product}"
                sendMessage(player, needMessage)
            }
            player.inventory.addList(Spinning.getAllNeed())
            player.inventory.addList(Spinning.getAllProduct())
            teleport(player, spinningWheelLocation)
            return@define
        }

        define(name = "barkit", privilege = Privilege.ADMIN, usage = "::barkit", description = "Bars.") { player, _ ->
            val anvilLocation = Location.create(2919, 3573, 0)
            player.inventory.addList(Bar.getAllBars())
            addItem(player, Items.HAMMER_2347)
            teleport(player, anvilLocation)
            return@define
        }

        define(name = "orekit", privilege = Privilege.ADMIN, usage = "::orekit", description = "Ores.") { player, _ ->
            player.inventory.addList(Bar.getAllOres())
            return@define
        }

        define(
            name = "summoningkit",
            privilege = Privilege.ADMIN,
            usage = "::orekit",
            description = "Ores.",
        ) { player, _ ->
            val obeliskLocation = Location.create(2208, 5344, 0)
            finishQuest(player, Quests.WOLF_WHISTLE)
            player.bank.addList(SummoningPouch.getAllPouchItems())
            teleport(player, obeliskLocation)
            return@define
        }
    }
}
