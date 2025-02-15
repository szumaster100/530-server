package content.global.activity.creation

import content.data.consumables.effects.NettleTeaEffect
import core.api.*
import core.api.quest.hasRequirement
import core.game.dialogue.DialogueFile
import core.game.dialogue.FaceAnim
import core.game.global.action.DropListener
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.Node
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.music.MusicEntry
import core.game.node.item.Item
import core.game.world.map.Location
import core.tools.END_DIALOGUE
import org.rs.consts.*

class CreatureCreation : InteractionListener {
    enum class CreatureCreation(
        val material: String,
        val npcId: Int,
        val location: Location,
        val firstMaterial: Int,
        val secondMaterial: Int,
    ) {
        NORTH_EAST(
            "Feather of chicken and eye of newt",
            NPCs.NEWTROOST_5597,
            Location(3058, 4410, 0),
            Items.FEATHER_314,
            Items.EYE_OF_NEWT_221,
        ),
        NORTH_WEST(
            "Horn of unicorn and hide of cow",
            NPCs.UNICOW_5603,
            Location(3018, 4410, 0),
            Items.COWHIDE_1739,
            Items.UNICORN_HORN_237,
        ),
        SOUTH_EAST(
            "Red spiders' eggs and a sardine raw",
            NPCs.SPIDINE_5594,
            Location(3043, 4361, 0),
            Items.RED_SPIDERS_EGGS_223,
            Items.RAW_SARDINE_327,
        ),
        SOUTH_WEST(
            "Swordfish raw and chicken uncooked",
            NPCs.SWORDCHICK_5595,
            Location(3034, 4361, 0),
            Items.RAW_SWORDFISH_371,
            Items.RAW_CHICKEN_2138,
        ),
        EAST(
            "Raw meat of jubbly bird and a lobster raw",
            NPCs.JUBSTER_5596,
            Location(3066, 4380, 0),
            Items.RAW_JUBBLY_7566,
            Items.RAW_LOBSTER_377,
        ),
        WEST(
            "Legs of giant frog and a cave eel uncooked",
            NPCs.FROGEEL_5593,
            Location(3012, 4380, 0),
            Items.GIANT_FROG_LEGS_4517,
            Items.RAW_CAVE_EEL_5001,
        ),
        ;

        val materials: List<Int> = listOf(firstMaterial, secondMaterial)

        companion object {
            fun fromLocation(location: Location): CreatureCreation? {
                return values().find { it.location == location }
            }
        }
    }

    companion object {
        const val BASE_CHARGE_AMOUNT = 1000
        val CUP_OF_TEA = intArrayOf(Items.CUP_OF_TEA_712, Items.CUP_OF_TEA_1978)
        val SATCHEL_RESOURCES = intArrayOf(Items.CAKE_1891, Items.BANANA_1963, Items.TRIANGLE_SANDWICH_6962)

        val SATCHEL_IDS =
            intArrayOf(
                Items.PLAIN_SATCHEL_10877,
                Items.GREEN_SATCHEL_10878,
                Items.RED_SATCHEL_10879,
                Items.BLACK_SATCHEL_10880,
                Items.GOLD_SATCHEL_10881,
                Items.RUNE_SATCHEL_10882,
            )

        private val chargeItemMapping =
            mapOf(
                11816 to listOf(Items.CAKE_1891, Items.BANANA_1963, Items.TRIANGLE_SANDWICH_6962),
                9925 to listOf(Items.BANANA_1963, Items.TRIANGLE_SANDWICH_6962),
                9853 to listOf(Items.CAKE_1891, Items.TRIANGLE_SANDWICH_6962),
                7962 to listOf(Items.TRIANGLE_SANDWICH_6962),
                4854 to listOf(Items.CAKE_1891, Items.BANANA_1963),
                2963 to listOf(Items.BANANA_1963),
                2891 to listOf(Items.CAKE_1891),
            )
    }

    override fun defineListeners() {
        on(Scenery.TRAPDOOR_21921, IntType.SCENERY, "open") { player, _ ->
            if (hasRequirement(player, Quests.TOWER_OF_LIFE)) {
                setVarbit(player, 3372, 1)
            }
            return@on true
        }

        on(Scenery.TRAPDOOR_21922, IntType.SCENERY, "close") { player, _ ->
            setVarbit(player, 3372, 0)
            return@on true
        }

        on(NPCs.HOMUNCULUS_5581, IntType.NPC, "talk-to") { player, _ ->
            openDialogue(player, HomunculusDialogue())
            return@on true
        }

        on(Scenery.STAIRS_21871, IntType.SCENERY, "climb-up") { player, _ ->
            player.musicPlayer.play(MusicEntry.forId(Music.WORK_WORK_WORK_237))
            if (!player.musicPlayer.hasUnlocked(Music.WORK_WORK_WORK_237)) {
                player.musicPlayer.unlock(Music.WORK_WORK_WORK_237)
            }
            return@on true
        }

        onUseWith(IntType.ITEM, SATCHEL_RESOURCES, *SATCHEL_IDS) { player, used, with ->
            handleResourceAddition(player, used.asItem(), with.asItem())
            return@onUseWith true
        }

        on(SATCHEL_IDS, IntType.ITEM, "inspect", "empty", "drop") { player, node ->
            handleOptionInteraction(player, node.asItem())
            return@on true
        }

        on(Items.TEA_FLASK_10859, IntType.ITEM, "drink", "look-in") { player, node ->
            val item = node as Item
            val charges = getCharge(item)

            when (getUsedOption(player)) {
                "drink" -> {
                    if (charges <= 1000) {
                        sendMessage(player, "The tea flask is empty.")
                    } else {
                        lock(player, 1)
                        animate(player, Animations.USE_TEA_FLASK_5827)
                        adjustCharge(item, -1000)
                        NettleTeaEffect().activate(player)
                        sendMessage(player, "You take a drink from the flask...")
                        sendChat(player, "Ahh, tea is so refreshing.", 1)
                    }
                }

                "look-in" -> {
                    val chargeAmount =
                        when (item.charge) {
                            6000 -> 5
                            5000 -> 4
                            4000 -> 3
                            3000 -> 2
                            2000 -> 1
                            1000 -> 0
                            else -> 0
                        }
                    sendItemDialogue(
                        player,
                        item.id,
                        "The tea flask holds: $chargeAmount ${if (chargeAmount > 1 || chargeAmount == 0) "cups" else "cup"} of tea.",
                    )
                }
            }
            return@on true
        }

        onUseWith(IntType.ITEM, Items.TEA_FLASK_10859, Items.EMPTY_CUP_1980) { player, used, with ->
            val item = used as Item
            val charges = getCharge(item)

            if (charges == 1000) {
                sendMessage(player, "The tea flask is empty.")
            } else {
                setCharge(item, charges - 1000)
                replaceSlot(player, with.asItem().slot, Item(Items.CUP_OF_TEA_712))
                sendMessage(player, "You fill the cup with tea.")
            }
            return@onUseWith true
        }

        onUseWith(IntType.ITEM, CUP_OF_TEA, Items.TEA_FLASK_10859) { player, used, with ->
            val item = with as Item
            val charges = getCharge(item)

            if (charges >= 6000) {
                sendMessage(player, "The tea flask is already full.")
            } else {
                replaceSlot(player, used.asItem().slot, Item(Items.EMPTY_CUP_1980))
                adjustCharge(item, 1000)
                sendMessage(player, "You add the tea to the flask.")
            }
            return@onUseWith true
        }

        on(Scenery.SYMBOL_OF_LIFE_21893, IntType.SCENERY, "inspect") { player, node ->
            CreatureCreation.fromLocation(node.location)?.let {
                sendDialogue(player, "You see some text scrolled above the altar on a symbol...")
                sendRequiredItemsDialogue(player, it)
            }
            return@on true
        }

        CreatureCreation.values().forEach { symbol ->
            onUseWith(
                IntType.SCENERY,
                symbol.materials.toIntArray(),
                Scenery.SYMBOL_OF_LIFE_21893,
            ) { player, used, with ->
                if (with.location == symbol.location) {
                    placeItemOnAltar(player, used.asItem(), symbol)
                }
                return@onUseWith true
            }
        }

        on(Scenery.SYMBOL_OF_LIFE_21893, IntType.SCENERY, "activate") { player, node ->
            val symbol = CreatureCreation.fromLocation(node.location)
            if (symbol != null && areAllMaterialsPlaced(player, symbol)) {
                activateAltar(player, symbol, node)
            } else {
                sendNPCDialogue(player, NPCs.HOMUNCULUS_5581, "You no haveee the two materials need.")
            }
            return@on true
        }
    }

    private fun placeItemOnAltar(
        player: Player,
        item: Item,
        symbol: CreatureCreation,
    ) {
        if (symbol.materials.contains(item.id)) {
            val attributeKey = "${symbol.name}:${item.id}"
            if (!getAttribute(player, attributeKey, false)) {
                player.lock(1)
                removeItem(player, item.id)
                animate(player, Animations.HUMAN_BURYING_BONES_827)
                sendDialogueLines(player, "You place the ${getItemName(item.id).lowercase()} on the altar.")
                setAttribute(player, attributeKey, true)
            } else {
                sendMessage(player, "You already placed the ${getItemName(item.id).lowercase()} on the altar!")
            }
        }
    }

    private fun areAllMaterialsPlaced(
        player: Player,
        symbol: CreatureCreation,
    ): Boolean {
        return symbol.materials.all {
            player.getAttribute("${symbol.name}:$it", false)
        }
    }

    private fun activateAltar(
        player: Player,
        symbol: CreatureCreation,
        node: Node,
    ) {
        sendNPCDialogue(player, NPCs.HOMUNCULUS_5581, "You have the materials needed. Here goes!", FaceAnim.OLD_NORMAL)
        addDialogueAction(player) { _, button ->
            if (button >= 5) {
                removePlacedMaterials(player, symbol)
                replaceScenery(node.asScenery(), node.id + 1, 3)
                spawnCreature(player, symbol)
            } else {
                player.sendMessage("Nothing interesting happens.")
            }
        }
    }

    private fun spawnCreature(
        player: Player,
        symbol: CreatureCreation,
    ) {
        val spawnLocation = symbol.location.adjustedForCreature()
        val creature =
            core.game.node.entity.npc.NPC
                .create(symbol.npcId, spawnLocation)
        creature.init()
        creature.attack(player)
        creature.isRespawn = false
    }

    private fun removePlacedMaterials(
        player: Player,
        symbol: CreatureCreation,
    ) {
        symbol.materials.forEach {
            removeAttribute(player, "${symbol.name}:$it")
        }
    }

    private fun Location.adjustedForCreature(): Location {
        return if (this == Location(3018, 4410, 0)) {
            Location.getRandomLocation(Location(3022, 4403, 0), 2, true)
        } else {
            Location.create(this.x - 1, this.y - 3, 0)
        }
    }

    private fun sendRequiredItemsDialogue(
        player: Player,
        symbol: CreatureCreation,
    ) {
        sendDoubleItemDialogue(player, symbol.firstMaterial, symbol.secondMaterial, "${symbol.material}...")
    }

    private fun handleResourceAddition(
        player: Player,
        used: Item,
        satchelItem: Item,
    ) {
        val chargesAmount = getCharge(satchelItem)
        val baseChargeAmount = BASE_CHARGE_AMOUNT

        if (isSatchelFull(chargesAmount, baseChargeAmount)) {
            sendMessage(player, "Your satchel is already full.")
            return
        }

        val itemId = used.id
        val itemName = getItemName(itemId).lowercase()
        val targetCharges = itemId + baseChargeAmount

        if (isItemAlreadyInSatchel(satchelItem, chargesAmount, targetCharges, itemId)) {
            sendMessage(player, "You already have a $itemName in there.")
            return
        }

        replaceSlot(player, used.slot, Item())
        adjustCharge(satchelItem, itemId)
        sendMessage(player, "You add a $itemName to the satchel.")
    }

    private fun handleOptionInteraction(
        player: Player,
        item: Item,
    ) {
        when (getUsedOption(player)) {
            "inspect" -> inspectSatchel(player, item)
            "empty" -> emptySatchel(player, item)
            "drop" -> dropSatchel(player, item)
        }
    }

    private fun isSatchelFull(
        charges: Int,
        base: Int,
    ) = charges >= 10816 + base

    private fun getFormattedItemName(itemId: Int): String =
        getItemName(itemId).lowercase().removePrefix("triangle ").trim()

    private fun inspectSatchel(
        player: Player,
        item: Item,
    ) {
        val itemIds = getItemsInSatchel(item)
        val message =
            when (itemIds.size) {
                0 -> "Empty!"
                1 -> "one ${getFormattedItemName(itemIds[0])}"
                2 -> itemIds.joinToString(", ") { "one ${getFormattedItemName(it)}" }
                3 -> itemIds.joinToString(", ", limit = 2, truncated = "and one ${getFormattedItemName(itemIds[2])}")
                else -> throw IllegalStateException("Unexpected satchel content size.")
            }

        sendItemDialogue(player, item.id, "The ${getItemName(item.id)}!<br>(Containing: $message)")
    }

    private fun emptySatchel(
        player: Player,
        item: Item,
    ) {
        getItemsInSatchel(item).forEach { itemId ->
            addItemOrDrop(player, itemId, 1)
        }
        setCharge(item, 1000)
    }

    private fun getItemsInSatchel(item: Item): List<Int> {
        return chargeItemMapping[getCharge(item)] ?: emptyList()
    }

    private fun isItemAlreadyInSatchel(
        item: Item,
        charges: Int,
        targetCharge: Int,
        checkId: Int,
    ): Boolean {
        val potentialItems =
            listOf(
                Items.CAKE_1891 + targetCharge,
                Items.BANANA_1963 + targetCharge,
                Items.TRIANGLE_SANDWICH_6962 + targetCharge,
            )
        return (item.isCharged && charges == targetCharge) || potentialItems.any { it == charges }
    }

    private fun dropSatchel(
        player: Player,
        item: Item,
    ) {
        if (getCharge(item) != 1000) {
            setCharge(item, 1000)
            sendMessage(player, "The contents of the satchel fell out as you dropped it!")
        }
        DropListener.drop(player, item)
    }

    inner class HomunculusDialogue : DialogueFile() {
        override fun handle(
            componentID: Int,
            buttonID: Int,
        ) {
            npc = NPC(NPCs.HOMUNCULUS_5581)
            when (stage) {
                0 ->
                    playerl(
                        FaceAnim.HALF_ASKING,
                        "Hi there, you mentioned something about creating monsters...?",
                    ).also { stage++ }

                1 ->
                    npcl(
                        FaceAnim.OLD_NORMAL,
                        "Good! I gain know from alchemists and builders. Me make beings.",
                    ).also { stage++ }

                2 -> playerl(FaceAnim.THINKING, "Interesting. Tell me if I'm right.").also { stage++ }
                3 ->
                    playerl(
                        FaceAnim.THINKING,
                        "By the alchemists and builders creating you, you have inherited their combined knowledge in much the same way that a child might inherit the looks of their parents.",
                    ).also { stage++ }

                4 -> npcl(FaceAnim.OLD_NORMAL, "Yes, you right!").also { stage++ }
                5 -> playerl(FaceAnim.HALF_ASKING, "So what do you need me to do?").also { stage++ }
                6 ->
                    npcl(
                        FaceAnim.OLD_NORMAL,
                        "Inspect symbol of life altars around dungeon. You see item give. Use item on altar. Activate altar to create, you fight.",
                    ).also { stage++ }

                7 -> playerl(FaceAnim.NOD_YES, "Okay. Sounds like a challenge.").also { stage = END_DIALOGUE }
            }
        }
    }
}
