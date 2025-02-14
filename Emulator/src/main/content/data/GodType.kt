package content.data

import org.rs.consts.*
import core.api.*
import core.game.dialogue.DialogueAction
import core.game.node.entity.player.Player
import core.game.node.item.GroundItemManager
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.update.flag.context.Graphics
import core.tools.StringUtils

enum class GodType(
    val cape: Item,
    val staff: Item,
    val statueId: Int,
    val npcId: Int,
    val dropMessage: String
) {

    SARADOMIN(
        cape = Item(Items.SARADOMIN_CAPE_2412),
        staff = Item(Items.SARADOMIN_STAFF_2415),
        statueId = org.rs.consts.Scenery.STATUE_OF_SARADOMIN_2873,
        npcId = NPCs.BATTLE_MAGE_913,
        dropMessage = "The cape disappears in a flash of light as it touches the ground."
    ),

    GUTHIX(
        cape = Item(Items.GUTHIX_CAPE_2413),
        staff = Item(Items.GUTHIX_STAFF_2416),
        statueId = org.rs.consts.Scenery.STATUE_OF_GUTHIX_2875,
        npcId = NPCs.BATTLE_MAGE_914,
        dropMessage = "The cape disintegrates as it touches the earth."
    ),

    ZAMORAK(
        cape = Item(Items.ZAMORAK_CAPE_2414),
        staff = Item(Items.ZAMORAK_STAFF_2417),
        statueId = org.rs.consts.Scenery.STATUE_OF_ZAMORAK_2874,
        npcId = NPCs.BATTLE_MAGE_912,
        dropMessage = "The cape ignites and burns up as it touches the ground."
    );

    fun pray(player: Player, statue: Scenery) {

        if (hasAny(player)) {
            lock(player, 3)
            animate(player, Animations.HUMAN_PRAY_645)
            sendMessage(player, "You kneel and begin to chant to ${getName()}...")
            sendMessage(player, "...but there is no response.")
            return
        }

        sendDialogue(player, "You kneel and begin to chant to ${getName()}...")
        addDialogueAction(player, object : DialogueAction {
            override fun handle(player: Player, buttonId: Int) {
                lock(player, 4)
                animate(player, Animations.HUMAN_PRAY_645)
                GameWorld.Pulser.submit(object : Pulse(3, player) {
                    override fun pulse(): Boolean {

                        val loc = statue.location.transform(0, -1, 0)
                        val g = GroundItemManager.get(cape.id, loc, player)
                        if (g == null) {
                            GroundItemManager.create(cape, loc, player)
                        }

                        sendGraphics(
                            Graphics(
                                org.rs.consts.Graphics.RANDOM_EVENT_PUFF_OF_SMOKE_86,
                                0,
                                0
                            ), loc)
                        return true
                    }
                })
            }
        })
    }

    companion object {

        @JvmStatic
        fun forScenery(scenery: Int): GodType? {
            return values().find { it.statueId == scenery }
        }

        @JvmStatic
        fun getCape(player: Player, invyOnly: Boolean): GodType? {
            return values().find { cape ->
                if (invyOnly) {
                    player.inventory.containsItems(cape.cape)
                } else {
                    player.equipment.containsItem(cape.cape) || player.inventory.containsItems(cape.cape)
                }
            }
        }

        fun getCape(player: Player): GodType? {
            return getCape(player, false)
        }

        fun forCape(cape: Item): GodType? {
            return values().find { it.cape.id == cape.id }
        }

        fun forId(id: Int): GodType? {
            return values().find { it.npcId == id }
        }

        fun hasAny(player: Player): Boolean {
            return values().any { player.hasItem(it.cape) }
        }
    }

    fun isFriendly(player: Player): Boolean {
        return player.equipment.containsItem(cape)
    }

    fun getName(): String {
        return StringUtils.formatDisplayName(name.lowercase())
    }
}
