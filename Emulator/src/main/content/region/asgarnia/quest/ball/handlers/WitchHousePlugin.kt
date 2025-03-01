package content.region.asgarnia.quest.ball.handlers

import content.region.asgarnia.quest.ball.handlers.npc.MouseNPC
import core.api.*
import core.cache.def.impl.ItemDefinition
import core.cache.def.impl.SceneryDefinition
import core.game.global.action.DoorActionHandler.handleAutowalkDoor
import core.game.interaction.NodeUsageEvent
import core.game.interaction.OptionHandler
import core.game.interaction.UseWithHandler
import core.game.node.Node
import core.game.node.entity.combat.ImpactHandler
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.TeleportManager
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.world.map.Location
import core.plugin.ClassScanner.definePlugin
import core.plugin.Initializable
import core.plugin.Plugin
import core.tools.RandomFunction
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests

@Initializable
class WitchHousePlugin : OptionHandler() {
    override fun handle(
        player: Player,
        node: Node,
        option: String,
    ): Boolean {
        val quest = player.getQuestRepository().getQuest(Quests.WITCHS_HOUSE)
        val id =
            if (node is Item) {
                node.getId()
            } else if (node is Scenery) {
                node.id
            } else if (node is NPC) {
                node.id
            } else {
                node.id
            }
        val readBook = getAttribute(player, "readWitchsBook", false)
        val magnetAttached = getAttribute(player, "attached_magnet", false)
        when (id) {
            897, 898, 899, 900 -> {
                if (option == "attack") {
                    player.properties.combatPulse.attack(node)
                }
            }

            24692 -> {
                val items = intArrayOf(1733, 1059, 1061, 1965, 1734)
                for (item in items) {
                    if (!inInventory(player, item)) {
                        when (item) {
                            1733 -> {
                                addItem(player, item)
                                sendMessage(player, "You find a sewing needle in the bottom of one of the boxes!")
                                return true
                            }

                            1734 -> {
                                addItem(player, item)
                                sendMessage(player, "You find some sewing thread in the bottom of one of the boxes!")
                                return true
                            }

                            1059 -> {
                                addItem(player, item)
                                sendMessage(
                                    player,
                                    "You find a pair of leather gloves in the bottom of one of the boxes!",
                                )
                                return true
                            }

                            1061 -> {
                                addItem(player, item)
                                sendMessage(
                                    player,
                                    "You find a pair of leather boots in the bottom of one of the boxes!",
                                )
                                return true
                            }

                            1965 -> {
                                addItem(player, item)
                                sendMessage(player, "You find an old cabbage in the bottom of one of the boxes!")
                                return true
                            }
                        }
                    }
                }
                sendMessage(player, "You find nothing interesting in the boxes.")
            }

            2869 ->
                if (player.inventory.addIfDoesntHave(MAGNET)) {
                    sendDialogueLines(player, "You find a magnet in the cupboard.")
                } else {
                    sendMessage(player, "You search the cupboard but find nothing interesting.")
                }

            2867 ->
                if (player.inventory.addIfDoesntHave(DOOR_KEY)) {
                    sendDialogueLines(player, "You find a key hidden under the flower pot.")
                } else {
                    sendMessage(player, "You search under the flower pot and find nothing.")
                }

            2861 -> {
                if (quest!!.isCompleted(player)) {
                    sendMessage(player, "The lock has seemed to changed since the last time you visited.")
                }
                if (player.inventory.containsItem(DOOR_KEY) || player.location.x >= 2901) {
                    handleAutowalkDoor(player, (node as Scenery))
                } else {
                    sendMessage(player, "The door is locked.")
                }
            }

            2862 ->
                if (magnetAttached || player.location.y < 3466) {
                    handleAutowalkDoor(player, (node as Scenery))
                    removeAttribute(player, "attached_magnet")
                } else {
                    sendDialogueLines(
                        player,
                        "Strange... I can't see any kind of lock or handle to",
                        "open this door...",
                    )
                }

            2865, 2866 ->
                if (player.equipment.containsItem(LEATHER_GLOVES)) {
                    handleAutowalkDoor(player, (node as Scenery))
                } else {
                    impact(player, RandomFunction.random(2, 3), ImpactHandler.HitsplatType.NORMAL)
                    sendDialogue(player, "As your bare hands touch the gate you feel a shock.")
                }

            24721 -> sendMessage(player, "You decide to not attract the attention of the witch by playing the piano.")
            2863 -> {
                if (player.location.x >= 2934) {
                    handleAutowalkDoor(player, (node as Scenery))
                    return true
                }
                if (player.inventory.containsItem(KEY)) {
                    handleAutowalkDoor(player, (node as Scenery))
                } else {
                    sendMessage(player, "The door is locked.")
                }
            }

            2864 -> {
                if (readBook && player.inventory.addIfDoesntHave(KEY)) {
                    sendDialogueLines(
                        player,
                        "You search for the secret compartment mentioned in the diary.",
                        "Inside it you find a small key. You take the key.",
                    )
                } else {
                    sendMessage(player, "You search the fountain but find nothing.")
                }
            }

            24724 -> sendMessage(player, "The gramophone doesn't have a record on it.")
            24672 -> teleport(player, Location(2906, 3472, 1), TeleportManager.TeleportType.INSTANT)
            24673 -> teleport(player, Location(2906, 3468, 0), TeleportManager.TeleportType.INSTANT)
        }
        return true
    }

    override fun newInstance(arg: Any?): Plugin<Any> {
        definePlugin(WitchHouseUseWithHandler())
        definePlugin(MouseNPC())
        SceneryDefinition.forId(org.rs.consts.Scenery.POTTED_PLANT_2867).handlers["option:look-under"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.DOOR_2861).handlers["option:open"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.GATE_2865).handlers["option:open"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.GATE_2866).handlers["option:open"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.DOOR_2862).handlers["option:open"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.GRAMOPHONE_24724).handlers["option:wind-up"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.STAIRCASE_24673).handlers["option:walk-down"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.STAIRCASE_24672).handlers["option:walk-up"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.PIANO_24721).handlers["option:play"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.BOXES_24692).handlers["option:search"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.CUPBOARD_2869).handlers["option:search"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.DOOR_2863).handlers["option:open"] = this
        SceneryDefinition.forId(org.rs.consts.Scenery.FOUNTAIN_2864).handlers["option:check"] = this
        ItemDefinition.forId(org.rs.consts.Scenery.LADDER_2408).handlers["option:read"] = this

        return this
    }

    private fun startFight(player: Player) {
        setAttribute(player, "experimentAlive", true)
        player.getSavedData().questData.isWitchsExperimentKilled = false
    }

    class WitchHouseUseWithHandler : UseWithHandler(CHEESE.id) {
        override fun newInstance(arg: Any?): Plugin<Any> {
            addHandler(org.rs.consts.Scenery.MOUSE_HOLE_15518, OBJECT_TYPE, this)
            addHandler(
                NPCs.MOUSE_901,
                NPC_TYPE,
                object : UseWithHandler(2410) {
                    override fun newInstance(arg: Any?): Plugin<Any> {
                        addHandler(NPCs.MOUSE_901, NPC_TYPE, this)
                        return this
                    }

                    override fun handle(event: NodeUsageEvent): Boolean {
                        val player = event.player
                        val useditem = event.usedItem
                        val npc = event.usedWith as NPC
                        checkNotNull(useditem)
                        if (useditem.id == MAGNET.id &&
                            npc.id == 901 &&
                            player.getAttribute<Any?>("mouse_out") != null
                        ) {
                            sendDialogueLines(
                                player,
                                "You attach a magnet to the mouse's harness. The mouse finishes",
                                "the cheese and runs back into its hole. You hear some odd noises",
                                "from inside the walls. There is a strange whirring noise from above",
                                "the door frame.",
                            )
                            removeAttribute(player, "mouse_out")
                            if (removeItem(player, MAGNET)) setAttribute(player, "attached_magnet", true)
                        }
                        return true
                    }
                },
            )
            return this
        }

        override fun handle(event: NodeUsageEvent): Boolean {
            val player = event.player
            val used = event.usedItem
            val scenery = event.usedWith as Scenery
            checkNotNull(used)
            if (player.getAttribute<Any?>("mouse_out") != null && used.id == CHEESE.id && scenery.id == 15518) {
                sendDialogue(player, "You can't do this right now.")
            }
            if (used.id == CHEESE.id && scenery.id == 15518 && player.getAttribute<Any?>("mouse_out") == null) {
                if (removeItem(player, CHEESE)) sendDialogue(player, "A mouse runs out of the hole.")
                val mouse = NPC.create(NPCs.MOUSE_901, Location.create(2903, 3466, 0)) as MouseNPC
                mouse.player = player
                mouse.isRespawn = false
                mouse.isWalks = false
                mouse.init()
                mouse.faceLocation(Location(2903, 3465, 0))
                setAttribute(player, "mouse_out", true)
            }
            return true
        }
    }

    companion object {
        private val LEATHER_GLOVES = Item(Items.LEATHER_GLOVES_1059)
        private val MAGNET = Item(Items.MAGNET_2410)
        private val CHEESE = Item(Items.CHEESE_1985)
        val KEY: Item = Item(Items.KEY_2411)
        val DOOR_KEY: Item = Item(Items.DOOR_KEY_2409)
        val BALL: Item = Item(Items.BALL_2407)
    }
}
