package content.region.kandarin.quest.arena.handlers

import content.region.kandarin.quest.arena.cutscene.JeremyRescueCutscene
import content.region.kandarin.quest.arena.cutscene.PrisonCutscene
import content.region.kandarin.quest.arena.dialogue.*
import content.region.kandarin.quest.arena.handlers.npc.GeneralNPC
import core.api.*
import core.api.quest.getQuestStage
import core.api.quest.setQuestStage
import core.game.global.action.DoorActionHandler
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.npc.NPC
import core.game.world.map.Location
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests
import org.rs.consts.Scenery

class FightArenaListener : InteractionListener {
    init {
        Jeremy.init()
        Jeremy.isWalks = true

        General.init()
        General.isWalks = true
    }

    override fun defineListeners() {
        on(NPCs.JEREMY_SERVIL_266, IntType.NPC, "talk-to") { player, npc ->
            openDialogue(player, JeremyServilDialogueFile(), npc)
            return@on true
        }

        on(GENERAL, IntType.NPC, "talk-to") { player, npc ->
            openDialogue(player, GeneralDialogue(), npc)
            return@on true
        }

        on(HENGARD, IntType.NPC, "talk-to") { player, npc ->
            openDialogue(player, HengradDialogue(), npc)
            return@on true
        }

        on(A_LAZY_GUARD_1, SCENERY, "talk-to") { player, node ->
            openDialogue(player, ALazyGuardDialogue(), node)
            return@on true
        }
        on(A_LAZY_GUARD_3, SCENERY, "talk-to") { player, node ->
            openDialogue(player, ALazyGuardDialogue(), node)
            return@on true
        }

        on(FULL_ARMOR_STAND, IntType.SCENERY, "borrow") { player, _ ->
            if (getQuestStage(player, Quests.FIGHT_ARENA) >= 10 &&
                !allInEquipment(player, HELMET, ARMOR) &&
                freeSlots(player) >= 2
            ) {
                replaceScenery(FULL_ARMOR_STAND_1!!.asScenery(), EMPTY_STAND, 10, location(2619, 3196, 0))
                sendMessage(player, "You borrow the suit of armour. It looks like it's just your size.")
                addItem(player, ARMOR, 1)
                addItem(player, HELMET, 1)
            } else if (!inEquipmentOrInventory(player, ARMOR) && freeSlots(player) >= 1) {
                replaceScenery(FULL_ARMOR_STAND_1!!.asScenery(), ONLY_HELM_STAND, 10, location(2619, 3196, 0))
                addItem(player, ARMOR, 1)
                sendMessage(player, "You borrow the suit of helmet. It looks like it's just your size.")
            } else if (!inEquipmentOrInventory(player, HELMET) && freeSlots(player) >= 1) {
                replaceScenery(FULL_ARMOR_STAND_1!!.asScenery(), ONLY_ARMOR_STAND, 10, location(2619, 3196, 0))
                addItem(player, HELMET, 1)
                sendMessage(player, "You could borrow this suit of armour if you had space in your inventory.")
            } else if (freeSlots(player) == 0) {
                sendMessage(player, "You could borrow this suit of armour if you had space in your inventory.")
            } else {
                sendMessage(player, "Nothing interesting happens.")
            }
            return@on true
        }

        on(A_LAZY_GUARD_2, IntType.SCENERY, "steal-keys") { player, _ ->
            face(player, location(2618, 3144, 0))
            setVarbit(player, 5627, 3)
            animate(player, 832)
            addItemOrDrop(player, CELL_KEY)
            setQuestStage(player, Quests.FIGHT_ARENA, 68)
            sendMessage(player, "You pick up the keys from the table.")
            return@on true
        }

        on(MAIN_DOOR, IntType.SCENERY, "open") { player, maingate ->
            when (player.location.y) {
                3171 -> DoorActionHandler.handleAutowalkDoor(player, maingate.asScenery())
                3172 -> {
                    if (allInEquipment(player, HELMET, ARMOR)) {
                        openDialogue(player, EastDoorSupportDialogue())
                    } else {
                        sendPlayerDialogue(player, "This door appears to be locked.")
                    }
                    return@on true
                }
            }
            when (player.location.x) {
                2585 -> DoorActionHandler.handleAutowalkDoor(player, maingate.asScenery())
                2584 -> {
                    if (allInEquipment(player, HELMET, ARMOR)) {
                        openDialogue(player, WestDoorSupportDialogue())
                    } else {
                        sendPlayerDialogue(player, "This door appears to be locked.")
                    }
                    return@on true
                }
            }
            return@on true
        }

        onUseWith(
            IntType.SCENERY,
            CELL_KEY,
            CELL_DOOR_2,
        ) { player, _, _ ->
            if (getQuestStage(player, Quests.FIGHT_ARENA) == 88) {
                PrisonCutscene(player).start()
            } else if (getQuestStage(player, Quests.FIGHT_ARENA) >= 68) {
                setAttribute(player, "spawn-ogre", true)
                JeremyRescueCutscene(player).start()
            }
            return@onUseWith true
        }

        onUseWith(
            IntType.SCENERY,
            CELL_KEY,
            CELL_DOOR_1,
        ) { player, _, _ ->
            if (getQuestStage(player, Quests.FIGHT_ARENA) >= 68) {
                sendDialogue(
                    player,
                    "I don't want to attract too much attention by freeing all the prisoners. I need to find Jeremy and he's not in this cell.",
                )
            } else {
                sendMessage(player, "The cell gate is securely locked.")
            }
            return@onUseWith false
        }

        on(CELL_DOOR_1, IntType.SCENERY, "open") { player, _ ->
            sendMessage(player, "the cell gate is securely locked.")
            return@on false
        }

        on(CELL_DOOR_2, IntType.SCENERY, "open") { player, _ ->
            sendMessage(player, "the cell gate is securely locked.")
            return@on false
        }
    }

    override fun defineDestinationOverrides() {
        setDest(IntType.NPC, intArrayOf(JEREMY_A), "talk-to") { _, _ ->
            return@setDest Location.create(2617, 3167, 0)
        }

        setDest(IntType.NPC, intArrayOf(FIGHTSLAVE), "talk-to") { player, _ ->
            if (inBorders(player, 2585, 3139, 2605, 3148)) {
                return@setDest Location.create(2595, 3141, 0)
            } else if (inBorders(player, 2616, 3162, 2617, 3165)) {
                return@setDest Location.create(2617, 3163, 0)
            } else {
                return@setDest Location.create(2617, 3159, 0)
            }
        }

        setDest(IntType.NPC, intArrayOf(KELVIN), "talk-to") { _, _ ->
            return@setDest Location.create(2589, 3141, 0)
        }

        setDest(IntType.NPC, intArrayOf(JOE), "talk-to") { _, _ ->
            return@setDest Location.create(2589, 3141, 0)
        }

        setDest(IntType.SCENERY, intArrayOf(A_LAZY_GUARD_2), "steal-keys") { _, _ ->
            return@setDest Location.create(2619, 3143, 0)
        }
    }

    companion object {
        const val GENERAL = NPCs.GENERAL_KHAZARD_258
        const val KELVIN = NPCs.KELVIN_260
        const val JOE = NPCs.JOE_261
        const val FIGHTSLAVE = NPCs.FIGHTSLAVE_262
        const val HENGARD = NPCs.HENGRAD_263
        const val JEREMY_A = NPCs.JEREMY_SERVIL_265

        const val CELL_DOOR_1 = Scenery.PRISON_DOOR_79
        const val CELL_DOOR_2 = Scenery.PRISON_DOOR_80
        const val MAIN_DOOR = Scenery.DOOR_81

        const val A_LAZY_GUARD_1 = Scenery.A_LAZY_KHAZARD_GUARD_41494
        const val A_LAZY_GUARD_2 = Scenery.A_LAZY_KHAZARD_GUARD_41496
        const val A_LAZY_GUARD_3 = Scenery.A_LAZY_KHAZARD_GUARD_41497

        const val FULL_ARMOR_STAND = Scenery.A_SUIT_OF_ARMOUR_41490
        val FULL_ARMOR_STAND_1 = getScenery(2619, 3196, 0)
        const val ONLY_ARMOR_STAND = Scenery.A_SUIT_OF_ARMOUR_41506
        const val ONLY_HELM_STAND = Scenery.A_SUIT_OF_ARMOUR_41507
        const val EMPTY_STAND = Scenery.A_SUIT_OF_ARMOUR_41508

        private const val HELMET = Items.KHAZARD_HELMET_74
        private const val ARMOR = Items.KHAZARD_ARMOUR_75
        private const val CELL_KEY = Items.KHAZARD_CELL_KEYS_76

        val Jeremy = NPC(NPCs.JEREMY_SERVIL_265, Location.create(2616, 3167, 0))
        val General = GeneralNPC(NPCs.GENERAL_KHAZARD_258, Location.create(2605, 3156, 0))
    }
}
