package content.region.desert.quest.deserttreasure.diamonds

import content.region.desert.quest.deserttreasure.DTUtils
import content.region.desert.quest.deserttreasure.DesertTreasure
import core.api.*
import core.api.event.applyPoison
import core.api.quest.getQuestStage
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.dialogue.Topic
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.GroundItemManager
import core.game.node.item.Item
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.tools.END_DIALOGUE
import core.tools.RandomFunction
import core.tools.START_DIALOGUE
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests
import org.rs.consts.Scenery

class ShadowDiamond : InteractionListener {
    companion object {
        fun roll(player: Player): Boolean {
            val chance = RandomFunction.randomDouble(1.0, 100.0)
            val successChance = RandomFunction.getSkillSuccessChance(52.0, 128.0, getDynLevel(player, Skills.THIEVING))

            return chance < successChance
        }

        fun pickAttempt(
            player: Player,
            picklockItem: Item?,
        ) {
            queueScript(player, 0, QueueStrength.SOFT) { stage: Int ->
                when (stage) {
                    0 -> {
                        sendMessage(player, "You attempt to pick the first lock...")
                        return@queueScript delayScript(player, 1)
                    }

                    1 -> {
                        if (roll(player)) {
                            sendMessage(player, "...You successfully picked the first lock!")
                            return@queueScript delayScript(player, 1)
                        } else {
                            if (picklockItem != null) {
                                removeItem(player, picklockItem)
                            } else {
                                removeItem(player, Items.LOCKPICK_1523)
                            }
                            sendMessage(player, "...and fail. The locking mechanism has reset itself.")
                            sendMessage(player, "Your lock pick snapped while attempting to pick the lock.")
                            impact(player, 3)
                            applyPoison(player, player, 6)
                            return@queueScript stopExecuting(player)
                        }
                    }

                    2 -> {
                        sendMessage(player, "You attempt to pick the second lock...")
                        return@queueScript delayScript(player, 1)
                    }

                    3 -> {
                        if (roll(player)) {
                            sendMessage(player, "...You successfully picked the second lock!")
                            return@queueScript delayScript(player, 1)
                        } else {
                            if (picklockItem != null) {
                                removeItem(player, picklockItem)
                            } else {
                                removeItem(player, Items.LOCKPICK_1523)
                            }
                            sendMessage(player, "...and fail. The locking mechanism has reset itself.")
                            sendMessage(player, "Your lock pick snapped while attempting to pick the lock.")
                            impact(player, 3)
                            applyPoison(player, player, 6)
                            return@queueScript stopExecuting(player)
                        }
                    }

                    4 -> {
                        sendMessage(player, "You attempt to pick the final lock...")
                        return@queueScript delayScript(player, 1)
                    }

                    5 -> {
                        if (roll(player)) {
                            sendMessage(player, "You managed to pick the final lock.")
                            return@queueScript delayScript(player, 1)
                        } else {
                            if (picklockItem != null) {
                                removeItem(player, picklockItem)
                            } else {
                                removeItem(player, Items.LOCKPICK_1523)
                            }
                            sendMessage(player, "...and fail. The locking mechanism has reset itself.")
                            sendMessage(player, "Your lock pick snapped while attempting to pick the lock.")
                            impact(player, 3)
                            applyPoison(player, player, 6)
                            return@queueScript stopExecuting(player)
                        }
                    }

                    6 -> {
                        if (DTUtils.getSubStage(player, DesertTreasure.shadowStage) == 1) {
                            DTUtils.setSubStage(player, DesertTreasure.shadowStage, 2)
                        }
                        return@queueScript stopExecuting(player)
                    }

                    else -> return@queueScript stopExecuting(player)
                }
            }
        }
    }

    override fun defineListeners() {
        on(Scenery.LADDER_6561, SCENERY, "climb-down") { player, node ->
            teleport(player, Location(2630, 5072))
            return@on true
        }

        onEquip(Items.RING_OF_VISIBILITY_4657) { player, _ ->
            if ((
                    DTUtils.getSubStage(player, DesertTreasure.shadowStage) >= 3 &&
                        getQuestStage(player, Quests.DESERT_TREASURE) >= 9
                ) ||
                getQuestStage(player, Quests.DESERT_TREASURE) >= 10
            ) {
                setVarbit(player, DesertTreasure.varbitSmokeDungeonLadder, 1)
                return@onEquip true
            }
            sendMessage(player, "You need to complete part of Desert Treasure to equip this.")
            return@onEquip false
        }

        onUnequip(Items.RING_OF_VISIBILITY_4657) { player, _ ->
            setVarbit(player, DesertTreasure.varbitSmokeDungeonLadder, 0)
            return@onUnequip true
        }

        on(Scenery.SECURE_CHEST_6448, SCENERY, "open") { player, node ->
            if (DTUtils.getSubStage(player, DesertTreasure.shadowStage) == 1) {
                if (inInventory(player, Items.LOCKPICK_1523)) {
                    openDialogue(
                        player,
                        object : DialogueFile() {
                            override fun handle(
                                componentID: Int,
                                buttonID: Int,
                            ) {
                                when (stage) {
                                    START_DIALOGUE ->
                                        dialogue(
                                            "Your skill as a thief allows you to see some kind of elaborate booby",
                                            "trapped locking mechanism on this chest.",
                                        ).also { stage++ }

                                    1 ->
                                        showTopics(
                                            Topic(FaceAnim.NEUTRAL, "Yes", 2, true),
                                            Topic(FaceAnim.NEUTRAL, "No", END_DIALOGUE, true),
                                            title = "Try to open the chest?",
                                        )

                                    2 ->
                                        end().also {
                                            if (inInventory(player, Items.LOCKPICK_1523)) {
                                                pickAttempt(player, null)
                                            } else {
                                                sendMessage(player, "You need a lockpick in order to attempt this.")
                                            }
                                        }
                                }
                            }
                        },
                    )
                } else {
                    sendMessage(player, "You need a lockpick in order to attempt this.")
                }
            } else if ((
                    DTUtils.getSubStage(player, DesertTreasure.shadowStage) >= 2 &&
                        getQuestStage(player, Quests.DESERT_TREASURE) >= 9
                ) ||
                getQuestStage(player, Quests.DESERT_TREASURE) >= 10
            ) {
                if (inInventory(player, Items.GILDED_CROSS_4674)) {
                    sendMessage(player, "The chest is empty.")
                } else {
                    replaceScenery(node as core.game.node.scenery.Scenery, 6449, 2)
                    sendMessage(player, "Inside the chest, hidden under some rags, you find a Gilded Cross.")
                    addItemOrDrop(player, Items.GILDED_CROSS_4674)
                }
            } else {
                sendPlayerDialogue(player, "These bandits are hostile enough without me trying to rob them!")
            }
            return@on true
        }

        onUseWith(SCENERY, Items.LOCKPICK_1523, Scenery.SECURE_CHEST_6448) { player, used, _ ->
            if (DTUtils.getSubStage(player, DesertTreasure.shadowStage) == 1) {
                pickAttempt(player, used as Item)
            } else if ((
                    DTUtils.getSubStage(player, DesertTreasure.shadowStage) >= 2 &&
                        getQuestStage(player, Quests.DESERT_TREASURE) >= 9
                ) ||
                getQuestStage(player, Quests.DESERT_TREASURE) >= 10
            ) {
                sendMessage(player, "The chest is unlocked.")
            } else {
                sendPlayerDialogue(player, "These bandits are hostile enough without me trying to rob them!")
            }
            return@onUseWith true
        }
    }
}

class ShadowDungeonWarning : MapArea {
    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(ZoneBorders(2726, 5072, 2728, 5072))
    }

    override fun areaEnter(entity: Entity) {
        if (entity is Player &&
            getQuestStage(entity, Quests.DESERT_TREASURE) == 9 &&
            DTUtils.getSubStage(entity, DesertTreasure.shadowStage) == 3 &&
            getAttribute(entity, DesertTreasure.attributeDamisWarning, false)
        ) {
            sendMessage(entity, "A voice seems to come from the walls around you;")
            sendMessage(entity, "'You... do not be... long in this... place")
            sendMessage(entity, "Turn... back now, or... prepare... to meet your... doom'")
            getAttribute(entity, DesertTreasure.attributeDamisWarning, true)
        }
    }
}

class ShadowDungeonAttack : MapArea {
    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(ZoneBorders(2731, 5085, 2748, 5097))
    }

    override fun areaEnter(entity: Entity) {
        if (entity is Player) {
            if (getQuestStage(entity, Quests.DESERT_TREASURE) == 9) {
                if (DTUtils.getSubStage(entity, DesertTreasure.shadowStage) == 3 &&
                    getAttribute<NPC?>(entity, DesertTreasure.attributeDamisInstance, null) == null
                ) {
                    val npc = NPC.create(NPCs.DAMIS_1974, Location(2739, 5088, 0))
                    setAttribute(entity, DesertTreasure.attributeDamisInstance, npc)
                    setAttribute(npc, "target", entity)
                    npc.isRespawn = false
                    npc.walkRadius = 30
                    npc.init()
                    npc.attack(entity)
                    sendChat(npc, "You should have listened to me!")
                }
            } else if (DTUtils.getSubStage(entity, DesertTreasure.shadowStage) >= 100) {
                if (!inInventory(entity, Items.SHADOW_DIAMOND_4673) && !inBank(entity, Items.SHADOW_DIAMOND_4673)) {
                    sendMessage(entity, "The Diamond of Shadow seems to have mystically found its way back here...")
                    GroundItemManager.create(Item(Items.SHADOW_DIAMOND_4673), Location(2739, 5088, 0), entity)
                }
            }
        }
    }
}
