package content.region.wilderness.handlers.castle

import core.api.*
import core.api.utils.WeightBasedTable
import core.api.utils.WeightedItem
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.system.task.Pulse
import core.tools.RandomFunction
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.Sounds

class RoguesCastleListener : InteractionListener {
    override fun defineListeners() {
        on(JAIL_DOORS, IntType.SCENERY, "open") { player, _ ->
            sendMessage(player, "The door is locked.")
            return@on true
        }

        on(FLOOR_1_CHESTS, IntType.SCENERY, "open") { player, node ->
            val scenery = node.asScenery()
            openChest(player, scenery)
            return@on true
        }

        on(FLOOR_1_CHESTS, IntType.SCENERY, "search") { player, node ->
            val scenery = node.asScenery()
            if (getCharge(scenery) == 0) {
                sendMessage(player, "This chest has already been looted.")
                return@on true
            }

            if (freeSlots(player) == 0) {
                sendMessage(player, "You don't have enough space to do that.")
                return@on true
            }

            val item = firstFloorLoot.roll()[0]
            addLoot(player, item)
            setCharge(scenery, 0)
            return@on true
        }

        on(FLOOR_2_CHESTS, IntType.SCENERY, "open") { player, _ ->
            sendMessage(player, "This chest appears to be locked.")
            return@on true
        }

        on(FLOOR_2_CHESTS, IntType.SCENERY, "pick-lock") { player, node ->
            val scenery = node.asScenery()
            if (!inInventory(player, Items.LOCKPICK_1523)) {
                sendMessage(player, "You need a lockpick in order to attempt this.")
                return@on true
            }

            if (!hasLevelDyn(player, Skills.THIEVING, 13)) {
                sendMessage(player, "You need a Thieving level of 13 to attempt this.")
                return@on true
            }
            playAudio(player, Sounds.PICK_LOCK_2407, 2)
            sendMessage(player, "You attempt to pick the lock on the chest...")
            submitIndividualPulse(
                player,
                object : Pulse(2) {
                    override fun pulse(): Boolean {
                        val success = RandomFunction.roll(10)
                        if (success) {
                            replaceScenery(scenery, scenery.id + 1, 20)
                            rewardXP(player, Skills.THIEVING, 300.0)
                        } else {
                            val dealsDamage = RandomFunction.roll(10)
                            if (dealsDamage) {
                                impact(player, RandomFunction.random(1, 3), ImpactHandler.HitsplatType.NORMAL)
                                sendMessage(player, "You activated a trap on the chest!")
                            }
                        }

                        sendMessage(player, "You ${if (success) "manage" else "fail"} to pick the lock on the chest.")
                        return true
                    }
                },
            )

            return@on true
        }

        on(FLOOR_2_CHESTS, IntType.SCENERY, "search") { player, node ->
            val scenery = node.asScenery()

            if (getCharge(scenery) == 0) {
                sendMessage(player, "This chest has already been looted.")
                return@on true
            }

            val loot = secondFloorLoot.roll()[0]
            if (freeSlots(player) > 0) {
                addItemOrDrop(player, loot.id, loot.amount)
                sendMessage(
                    player,
                    "In the chest you find some ${loot.name.lowercase() + if (!loot.name.endsWith("s")) "s" else ""}!",
                )
                setCharge(scenery, 0)
                rewardXP(player, Skills.THIEVING, 60.0)
            }

            return@on true
        }
    }

    companion object {
        private val CHEST_ANIM = getAnimation(Animations.HUMAN_OPEN_CHEST_536)
        private val FLOOR_1_CHESTS = intArrayOf(14773, 14774)
        private val FLOOR_2_CHESTS = intArrayOf(38834, 38835)
        private const val JAIL_DOORS = 38837
    }

    fun openChest(
        player: Player,
        scenery: Scenery,
    ) {
        animate(player, CHEST_ANIM)
        submitIndividualPulse(
            player,
            object : Pulse(animationDuration(CHEST_ANIM)) {
                override fun pulse(): Boolean {
                    return true.also { replaceScenery(scenery, scenery.id + 1, 20) }
                }
            },
        )
    }

    fun addLoot(
        player: Player,
        item: Item,
    ) {
        sendMessage(player, "You search the chest...")
        submitIndividualPulse(
            player,
            object : Pulse() {
                override fun pulse(): Boolean {
                    sendMessage(
                        player,
                        "... and find some ${item.name.lowercase() + if (!item.name.endsWith("s")) "s" else ""}!",
                    )
                    addItemOrDrop(player, item.id, item.amount)
                    return true
                }
            },
        )
    }

    private val firstFloorLoot =
        WeightBasedTable.create(
            WeightedItem(Items.COINS_995, 8, 25, 70.0),
            WeightedItem(Items.NATURE_RUNE_561, 2, 3, 10.0),
            WeightedItem(Items.BLOOD_RUNE_565, 2, 3, 10.0),
            WeightedItem(Items.DEATH_RUNE_560, 3, 5, 10.0),
        )

    private val secondFloorLoot =
        WeightBasedTable.create(
            WeightedItem(Items.COINS_995, 4, 57, 75.0),
            WeightedItem(Items.COINS_995, 107, 243, 5.0),
            WeightedItem(Items.BLOOD_RUNE_565, 2, 5, 5.0),
            WeightedItem(Items.GOLD_ORE_445, 1, 1, 5.0),
            WeightedItem(Items.STEEL_MED_HELM_1141, 1, 1, 5.0),
            WeightedItem(Items.STEEL_PLATELEGS_1069, 1, 1, 5.0),
        )
}
