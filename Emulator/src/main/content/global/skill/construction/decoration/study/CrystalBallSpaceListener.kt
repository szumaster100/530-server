package content.global.skill.construction.decoration.study

import org.rs.consts.Items
import org.rs.consts.Scenery
import core.api.sendMessage
import core.game.interaction.OptionHandler
import core.game.node.Node
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.update.flag.context.Animation
import core.plugin.Plugin

class CrystalBallSpaceListener : OptionHandler() {

    override fun handle(player: Player?, node: Node?, option: String?): Boolean {
        if (player!!.houseManager == null) return true
        if (player.houseManager!!.isBuildingMode) {
            player.sendMessage("You can't do this in build mode.")
            return true
        }
        val staff = Staff.MAP[node!!.id] ?: return true
        if (node.asScenery().id == 13659 && node.id > 1387) {
            sendMessage(player, "You can only change the element of basic staves on this crystal ball.")
        } else if (node.asScenery().id == 13660 && node.id > 1399) {
            sendMessage(
                player,
                "You can only change the element of basic staves and battlestaves on this elemental sphere."
            )
        }
        return true
    }

    fun getItems(): Array<Any> {
        return arrayOf(1381, 1383, 1385, 1387, 1393, 1395, 1397, 1399, 1401, 1403, 1405, 1407)
    }

    fun getObjects(): Array<Any> {
        return arrayOf(Scenery.CRYSTAL_BALL_13659, Scenery.ELEMENTAL_SPHERE_13660, Scenery.CRYSTAL_OF_POWER_13661)
    }

    private enum class Staff(val staffId: Int, val start: Animation, val end: Animation, val cost: Item? = null) {
        STAFF_OF_AIR(1381, Animation(4043), Animation(4044)),
        STAFF_OF_WATER(1383, Animation(4047), Animation(4048)),
        STAFF_OF_EARTH(1385, Animation(4045), Animation(4046)),
        STAFF_OF_FIRE(1387, Animation(4049), Animation(4050)),
        AIR_BATTLESTAFF(1397, Animation(4051), Animation(4052), Item(Items.AIR_RUNE_556, 100)),
        WATER_BATTLESTAFF(1395, Animation(4055), Animation(4056), Item(Items.WATER_RUNE_555, 100)),
        EARTH_BATTLESTAFF(1399, Animation(4053), Animation(4054), Item(Items.EARTH_RUNE_557, 100)),
        FIRE_BATTLESTAFF(1393, Animation(4057), Animation(4058), Item(Items.FIRE_RUNE_554, 100)),
        MYSTIC_AIR_STAFF(1405, Animation(4059), Animation(4060), Item(Items.AIR_RUNE_556, 1000)),
        MYSTIC_WATER_STAFF(1403, Animation(4063), Animation(4064), Item(Items.WATER_RUNE_555, 1000)),
        MYSTIC_EARTH_STAFF(1407, Animation(4061), Animation(4062), Item(Items.EARTH_RUNE_557, 1000)),
        MYSTIC_FIRE_STAFF(1401, Animation(4065), Animation(4066), Item(Items.FIRE_RUNE_554, 1000));

        companion object {

            val staffGroup = arrayOf(
                arrayOf(Item(1381), Item(1383), Item(1385), Item(1387)),
                arrayOf(Item(1397), Item(1395), Item(1399), Item(1393)),
                arrayOf(Item(1405), Item(1403), Item(1407), Item(1401))
            )

            private val VALUES = values()

            val MAP: MutableMap<Int, Staff> = HashMap(VALUES.size)

            init {
                for (value in VALUES) MAP[value.staffId] = value
            }
        }
    }

    override fun newInstance(arg: Any?): Plugin<Any> {
        TODO("Not yet implemented")
    }
}