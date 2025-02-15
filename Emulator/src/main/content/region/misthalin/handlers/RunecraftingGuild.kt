package content.region.misthalin.handlers

import content.global.skill.runecrafting.items.Talisman
import content.global.skill.runecrafting.pouch.PouchManager.Pouches
import core.api.*
import core.api.MapArea
import core.api.quest.isQuestComplete
import core.game.dialogue.Dialogue
import core.game.dialogue.FaceAnim
import core.game.dialogue.InputType
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.interaction.QueueStrength
import core.game.node.entity.combat.CombatStyle
import core.game.node.entity.combat.spell.CombatSpell
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.SpellBookManager.SpellBook
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneRestriction
import core.plugin.Initializable
import core.tools.END_DIALOGUE
import core.tools.Log
import org.rs.consts.*

class RunecraftingGuild :
    MapArea,
    InteractionListener,
    InterfaceListener {
    companion object {
        private const val RC_PORTAL = 38279
        private const val MAP_TABLE = Scenery.MAP_TABLE_38315
        private val BOOKCASES = intArrayOf(38322, 38323, 38324)
        private val RC_HAT = intArrayOf(13626, 13625, 13621, 13620, 13616, 13615)

        private const val OMNI_TIARA = Items.OMNI_TIARA_13655
        private const val OMNI_TALISMAN = Items.OMNI_TALISMAN_13649
        private const val STUDY_INTERFACE = Components.RCGUILD_MAP_780
        private val ALTAR_MAP_MODELS = intArrayOf(35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 47, 48)

        val talismanIDs = Talisman.values().map { it.item.id }.toIntArray()
    }

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(ZoneBorders.forRegion(6741))
    }

    override fun getRestrictions(): Array<ZoneRestriction> {
        return arrayOf(
            ZoneRestriction.CANNON,
            ZoneRestriction.RANDOM_EVENTS,
            ZoneRestriction.GRAVES,
            ZoneRestriction.FIRES,
        )
    }

    override fun defineListeners() {
        onUseWith(IntType.SCENERY, talismanIDs, MAP_TABLE) { player, used, _ ->
            if (anyInInventory(player, *talismanIDs)) {
                openInterface(player, STUDY_INTERFACE)
                setComponentVisibility(player, STUDY_INTERFACE, swapIds(used.id), false)
            }
            return@onUseWith true
        }

        onUseWith(IntType.SCENERY, OMNI_TALISMAN, MAP_TABLE) { player, _, _ ->
            if (!inEquipment(player, OMNI_TALISMAN) || !inEquipment(player, OMNI_TIARA)) {
                openInterface(player, STUDY_INTERFACE)
                for (rune in ALTAR_MAP_MODELS) {
                    setComponentVisibility(player, STUDY_INTERFACE, rune, false).also {
                        sendString(player, "All the altars of " + GameWorld.settings!!.name + ".", STUDY_INTERFACE, 33)
                    }
                }
            }
            return@onUseWith true
        }

        on(BOOKCASES, IntType.SCENERY, "search") { player, _ ->
            sendMessage(player, "You search the books...")
            sendMessageWithDelay(player, "None of them look very interesting.", 1)
            return@on true
        }

        on(RC_HAT, IntType.ITEM, "Goggles") { player, node ->
            val newHatId =
                when (node.id) {
                    13626 -> 13625
                    13625 -> 13626
                    13621 -> 13620
                    13620 -> 13621
                    13616 -> 13615
                    13615 -> 13616
                    else -> return@on false
                }
            replaceSlot(player, node.asItem().slot, Item(newHatId))
            return@on true
        }

        on(NPCs.WIZARD_ELRISS_8032, IntType.NPC, "Exchange") { player, _ ->
            openInterface(player, Components.RCGUILD_REWARDS_779)
            return@on true
        }

        on(MAP_TABLE, IntType.SCENERY, "Study") { player, _ ->
            openInterface(player, Components.RCGUILD_MAP_780)
            return@on true
        }

        on(RC_PORTAL, IntType.SCENERY, "Enter") { player, _ ->
            if (getStatLevel(player, Skills.RUNECRAFTING) < 50) {
                sendMessage(player, "You require 50 Runecrafting to enter the Runecrafters' Guild.")
                return@on true
            }
            if (!isQuestComplete(player, Quests.RUNE_MYSTERIES)) {
                sendMessage(player, "You need to complete Rune Mysteries to enter the Runecrafting guild.")
                return@on true
            }

            val destination =
                if (player.viewport.region.regionId == 12337) {
                    Location.create(1696, 5461, 2)
                } else {
                    Location.create(3106, 3160, 1)
                }
            visualize(player, Animations.RC_TP_A_10180, Graphics.RC_GUILD_TP)
            queueScript(player, 3, QueueStrength.SOFT) {
                teleport(player, destination)
                visualize(player, Animations.RC_TP_B_10182, Graphics.RC_GUILD_TP)
                face(player, destination)
                return@queueScript stopExecuting(player)
            }
            return@on true
        }
    }

    override fun defineDestinationOverrides() {
        setDest(IntType.SCENERY, intArrayOf(RC_PORTAL), "enter") { player, node ->
            if (player.viewport.region.regionId == 12337) {
                return@setDest node.asScenery().location
            } else {
                return@setDest Location.create(1696, 5461, 2)
            }
        }
    }

    class ShopItem(
        val id: Int,
        val price: Int,
        val amount: Int,
    )

    override fun defineInterfaceListeners() {
        onOpen(STUDY_INTERFACE) { player, _ ->
            if (inEquipment(player, Items.OMNI_TALISMAN_STAFF_13642) || inEquipment(player, Items.OMNI_TIARA_13655)) {
                for (rune in ALTAR_MAP_MODELS) {
                    setComponentVisibility(player, STUDY_INTERFACE, rune, false).also {
                        sendString(player, "All the altars of " + GameWorld.settings!!.name + ".", STUDY_INTERFACE, 33)
                    }
                }
            }
            return@onOpen true
        }

        onOpen(Components.RCGUILD_REWARDS_779) { player, _ ->
            sendTokens(player)
            return@onOpen true
        }

        on(Components.RCGUILD_REWARDS_779) { player, _, opcode, button, _, _ ->
            var choice: ShopItem
            when (button) {
                6 -> choice = airTalisman
                13 -> choice = mindTalisman
                15 -> choice = waterTalisman
                10 -> choice = earthTalisman
                11 -> choice = fireTalisman
                7 -> choice = bodyTalisman
                9 -> choice = cosmicTalisman
                8 -> choice = chaosTalisman
                14 -> choice = natureTalisman
                12 -> choice = lawTalisman
                36 -> choice = blueRCHat
                37 -> choice = yellowRCHat
                38 -> choice = greenRCHat
                39 -> choice = blueRCRobe
                40 -> choice = yellowRCRobe
                41 -> choice = greenRCRobe
                42 -> choice = blueRCBottom
                43 -> choice = yellowRCBottom
                44 -> choice = greenRCBottom
                45 -> choice = blueRCGloves
                46 -> choice = yellowRCGloves
                47 -> choice = greenRCGloves
                72 -> choice = airTablet
                80 -> choice = mindTablet
                83 -> choice = waterTablet
                77 -> choice = earthTablet
                78 -> choice = fireTablet
                73 -> choice = bodyTablet
                75 -> choice = cosmicTablet
                74 -> choice = chaosTablet
                81 -> choice = astralTablet
                82 -> choice = natureTablet
                79 -> choice = lawTablet
                76 -> choice = deathTablet
                84 -> choice = bloodTablet
                85 -> choice = guildTablet
                114 -> choice = rcStaff
                115 -> choice = pureEssence
                else ->
                    log(
                        this::class.java,
                        Log.WARN,
                        "Unhandled button ID for RC shop interface: $button",
                    ).also { return@on true }
            }

            handleOpcode(choice, opcode, player)
            if (opcode == 155) {
                when (button) {
                    163 -> sendMessage(player, "You must select something to buy before you can confirm your purchase")
                }
            }
            sendItem(choice, choice.amount, player)
            return@on true
        }
    }

    private fun handleBuyOption(
        item: ShopItem,
        amount: Int,
        player: Player,
    ) {
        val neededTokens = Item(Items.RUNECRAFTING_GUILD_TOKEN_13650, item.price * amount)
        if (!player.inventory.containsItem(neededTokens)) {
            sendMessage(player, "You don't have enough tokens to purchase that.")
            return
        }
        if (freeSlots(player) == 0) {
            sendMessage(player, "You don't have enough space in your inventory.")
            return
        }

        sendMessage(player, "Your purchase has been added to your inventory.")
        player.inventory.remove(neededTokens)
        player.inventory.add(Item(item.id, amount))
        sendString(player, " ", Components.RCGUILD_REWARDS_779, 136)
        sendTokens(player)
    }

    private fun handleOpcode(
        item: ShopItem,
        opcode: Int,
        player: Player,
    ) {
        when (opcode) {
            155 -> handleBuyOption(item, 1, player)
            196 -> {
                sendInputDialogue(player, InputType.AMOUNT, "Enter the amount to buy:") { value ->
                    val amt = value.toString().toIntOrNull()
                    if (amt == null || amt <= 0) {
                        sendDialogue(player, "Please enter a valid amount greater than zero.")
                        return@sendInputDialogue
                    } else {
                        handleBuyOption(item, amt, player)
                    }
                }
            }
        }
    }

    private fun sendTokens(player: Player) {
        sendString(player, "Tokens: ${amountInInventory(player, 13650)}", Components.RCGUILD_REWARDS_779, 135)
    }

    private fun sendItem(
        item: ShopItem,
        amount: Int,
        player: Player,
    ) {
        sendString(player, "${getItemName(item.id)}($amount)", Components.RCGUILD_REWARDS_779, 136)
    }

    private val airTalisman = ShopItem(1438, 50, 1)
    private val mindTalisman = ShopItem(1448, 50, 1)
    private val waterTalisman = ShopItem(1444, 50, 1)
    private val earthTalisman = ShopItem(1440, 50, 1)
    private val fireTalisman = ShopItem(1442, 50, 1)
    private val bodyTalisman = ShopItem(1446, 50, 1)
    private val cosmicTalisman = ShopItem(1454, 125, 1)
    private val chaosTalisman = ShopItem(1452, 125, 1)
    private val natureTalisman = ShopItem(1462, 125, 1)
    private val lawTalisman = ShopItem(1458, 125, 1)
    private val blueRCHat = ShopItem(13626, 1000, 1)
    private val yellowRCHat = ShopItem(13616, 1000, 1)
    private val greenRCHat = ShopItem(13621, 1000, 1)
    private val blueRCRobe = ShopItem(13624, 1000, 1)
    private val yellowRCRobe = ShopItem(13614, 1000, 1)
    private val greenRCRobe = ShopItem(13619, 1000, 1)
    private val blueRCBottom = ShopItem(13627, 1000, 1)
    private val yellowRCBottom = ShopItem(13617, 1000, 1)
    private val greenRCBottom = ShopItem(13622, 1000, 1)
    private val blueRCGloves = ShopItem(13628, 1000, 1)
    private val yellowRCGloves = ShopItem(13618, 1000, 1)
    private val greenRCGloves = ShopItem(13623, 1000, 1)
    private val rcStaff = ShopItem(13629, 10000, 1)
    private val pureEssence = ShopItem(7937, 100, 1)
    private val airTablet = ShopItem(13599, 30, 1)
    private val mindTablet = ShopItem(13600, 32, 1)
    private val waterTablet = ShopItem(13601, 34, 1)
    private val earthTablet = ShopItem(13602, 36, 1)
    private val fireTablet = ShopItem(13603, 37, 1)
    private val bodyTablet = ShopItem(13604, 38, 1)
    private val cosmicTablet = ShopItem(13605, 39, 1)
    private val chaosTablet = ShopItem(13606, 40, 1)
    private val astralTablet = ShopItem(13611, 41, 1)
    private val natureTablet = ShopItem(13607, 42, 1)
    private val lawTablet = ShopItem(13608, 43, 1)
    private val deathTablet = ShopItem(13609, 44, 1)
    private val bloodTablet = ShopItem(13610, 45, 1)
    private val guildTablet = ShopItem(13598, 15, 1)

    private fun swapIds(id: Int): Int {
        return when (id) {
            1438 -> 35
            1440 -> 38
            1442 -> 40
            1444 -> 48
            1446 -> 36
            1448 -> 37
            1450 -> 43
            1452 -> 41
            1454 -> 39
            1456 -> 47
            1458 -> 42
            1462 -> 44
            else -> 0
        }
    }

    @Initializable
    class WizardNPC(
        id: Int = 0,
        location: Location? = null,
    ) : AbstractNPC(id, location) {
        override fun construct(
            id: Int,
            location: Location,
            vararg objects: Any,
        ): AbstractNPC {
            return WizardNPC(id, location)
        }

        override fun init() {
            super.init()
            properties.combatPulse.style = CombatStyle.MAGIC
            properties.autocastSpell = SpellBook.MODERN.getSpell(8) as CombatSpell
        }

        override fun getIds(): IntArray {
            return intArrayOf(NPCs.WIZARD_13)
        }
    }

    @Initializable
    class WizardElrissDialogue(
        player: Player? = null,
    ) : Dialogue(player) {
        override fun open(vararg args: Any?): Boolean {
            npc = args[0] as NPC
            if (anyInInventory(player!!, *requiredTalismanItems.map { it.first }.toIntArray()) &&
                !getAttribute(player, ACCESS_TO_OMNI_ITEMS, false)
            ) {
                npcl(FaceAnim.HAPPY, "Welcome to the Runecrafting Guild.")
                stage = 1
            } else {
                npcl(FaceAnim.HAPPY, "Welcome to the Runecrafting Guild.")
                stage = 0
            }
            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            var buyAmount = 0
            when (stage) {
                1 -> {
                    if (allItemsInInventory()) {
                        options(
                            "I've lost my omni-talisman.",
                            "What can I do here?",
                            "Can I buy some tokens?",
                            "Never mind.",
                        )
                        stage++
                    } else {
                        options("What is this place?", "What can I do here?", "Can I buy some tokens?", "Never mind.")
                        stage++
                    }
                }
                2 -> {
                    when (buttonId) {
                        1 -> {
                            if (allItemsInInventory()) {
                                player("I've lost my omni-talisman.")
                                stage = 4
                            } else {
                                playerl(FaceAnim.HALF_ASKING, "What is this place?")
                                stage = 5
                            }
                        }
                        2 -> playerl(FaceAnim.HALF_ASKING, "What can I do here?").also { stage = 19 }
                        3 -> playerl(FaceAnim.HALF_ASKING, "Can I buy some tokens?").also { stage = 200 }
                        4 -> playerl(FaceAnim.HALF_ASKING, "Never mind.").also { stage = END_DIALOGUE }
                    }
                }
                3 -> {
                    end()
                    openInterface(player, Components.RCGUILD_REWARDS_779)
                }
                4 -> {
                    val hasAllItems = allItemsInInventory()
                    if (hasAllItems) {
                        npc("Thank you! You've shown me all the talismans.")
                        rewardXP(player!!, Skills.RUNECRAFTING, 1000.0) // Example XP reward
                        sendItemDialogue(player!!, Items.OMNI_TALISMAN_13649, "You receive the omni-talisman.")
                        addItemOrDrop(player!!, Items.OMNI_TALISMAN_13649)
                        setAttribute(player!!, ACCESS_TO_OMNI_ITEMS, true)
                    } else {
                        player("I need to show you all the required talismans first.")
                        stage = 1
                    }
                    // end()
                    // npc("Oh, well. Here's another one. Do try to be careful with", "it this time.")
                    // addItemOrDrop(player, Items.OMNI_TALISMAN_13649)
                }
                5 ->
                    npc(
                        FaceAnim.FRIENDLY,
                        "This is the Runecrafting Guild, as I said. After the",
                        "secret of Runecrafting was re-discovered, I set up the",
                        "guild as a place for the most advanced runecrafters to",
                        "work together.",
                    ).also {
                        stage++
                    }
                6 ->
                    options(
                        "Work together towards what?",
                        "Where are we exactly?",
                        "What can I do here?",
                        "Can I buy some tokens?",
                        "Never mind.",
                    ).also {
                        stage++
                    }
                7 ->
                    when (buttonId) {
                        1 -> playerl(FaceAnim.HALF_ASKING, "Work together towards what?").also { stage++ }
                        2 -> playerl(FaceAnim.HALF_ASKING, "Where are we exactly?").also { stage = 38 }
                        3 -> playerl(FaceAnim.HALF_ASKING, "What can I do here?").also { stage = 19 }
                        4 -> playerl(FaceAnim.HALF_ASKING, "Can I buy some tokens?").also { stage = 200 }
                        5 -> player(FaceAnim.NEUTRAL, "Never mind.").also { stage = END_DIALOGUE }
                    }
                8 ->
                    npc(
                        FaceAnim.FRIENDLY,
                        "Towards a greater understanding of Runecrafting, of",
                        "course. The basics of Runecrafting may have been re-",
                        "discovered, but many of the secrets of the first",
                        "Wizards' Tower remain unknown.",
                    ).also {
                        stage++
                    }
                9 ->
                    options(
                        "What secrets?",
                        "Where are we exactly?",
                        "What can I do here?",
                        "Never mind.",
                    ).also { stage++ }
                10 ->
                    when (buttonId) {
                        1 -> playerl(FaceAnim.HALF_ASKING, "What secrets?").also { stage++ }
                        2 ->
                            playerl(
                                FaceAnim.HALF_ASKING,
                                "Acantha and Vief are hardly working together!",
                            ).also { stage++ }
                        3 -> playerl(FaceAnim.HALF_ASKING, "Where are we exactly?").also { stage = 38 }
                        4 -> playerl(FaceAnim.HALF_ASKING, "What can I do here?").also { stage = 19 }
                        5 -> player(FaceAnim.NEUTRAL, "Never mind.").also { stage = END_DIALOGUE }
                    }
                11 ->
                    npc(
                        FaceAnim.FRIENDLY,
                        "Oh, nothing to interest an adventurer such as yourself",
                        "I'm sure.",
                    ).also { stage++ }
                12 -> options("I am interested.", "Yeah, I'm not really interested.").also { stage++ }
                13 ->
                    when (buttonId) {
                        1 -> playerl(FaceAnim.HALF_ASKING, "I am interested.").also { stage++ }
                        2 ->
                            playerl(FaceAnim.HALF_ASKING, "Yeah, I'm not really interested.").also {
                                stage =
                                    END_DIALOGUE
                            }
                    }
                14 ->
                    npc(
                        FaceAnim.FRIENDLY,
                        "We all have our projects, adventurer. Acantha and Vief",
                        "are happy to involve junior runecrafters in their feud,",
                        "but others prefer to keep their research private until it",
                        "is revealed.",
                    ).also {
                        stage++
                    }
                15 ->
                    npc(
                        FaceAnim.SAD,
                        "An idea may be subject to cruel ridicule if it is aired",
                        "prematurely, as I have learned to my cost. You must",
                        "forgive me if I am not so forthcoming again.",
                    ).also {
                        stage++
                    }
                16 -> options("Never mind, then.", "Go on, tell me.").also { stage++ }
                17 ->
                    when (buttonId) {
                        1 -> playerl(FaceAnim.HALF_ASKING, "Never mind, then.").also { stage = END_DIALOGUE }
                        2 -> playerl(FaceAnim.HALF_ASKING, "Go on, tell me.").also { stage++ }
                    }
                18 -> npc(FaceAnim.FRIENDLY, "Leave me be!").also { stage = END_DIALOGUE }
                19 ->
                    npc(
                        "Wizard Acantha and Wizard Vief are running The",
                        "Great Orb Project. It requires large numbers of",
                        "runecrafters, so you should speak with them if you",
                        "want something to do.",
                    ).also {
                        stage++
                    }
                20 ->
                    options(
                        "Tell me about Acantha and Vief's project.",
                        "Tell me about the omni-talisman.",
                        "Tell me about Wizard Korvak's pouch repairs.",
                        "Never mind.",
                    ).also {
                        stage++
                    }
                21 ->
                    when (buttonId) {
                        1 -> playerl(FaceAnim.HALF_ASKING, "Tell me about Acantha and Vief's project.").also { stage++ }
                        2 -> playerl(FaceAnim.HALF_ASKING, "Tell me about the omni-talisman.").also { stage = 54 }
                        3 ->
                            playerl(
                                FaceAnim.HALF_ASKING,
                                "Tell me about Wizard Korvak's pouch repairs.",
                            ).also { stage = 58 }
                        4 -> playerl(FaceAnim.HALF_ASKING, "Never mind.").also { stage = END_DIALOGUE }
                    }
                22 ->
                    npc(
                        "The Orb Proj...I beg your pardon, The Great Orb",
                        "Project? It's truly fascinating. Wizards Acantha and",
                        "Vief have found that energy leaks out of some of the",
                        "Runecrafting altars. They are recruiting teams of",
                    ).also {
                        stage++
                    }
                23 -> npc("experienced runecrafters such as yourself, to force the", "energy back in.").also { stage++ }
                24 ->
                    npc(
                        "Join one of the teams by speaking to Wizard Acantha",
                        "or Wizard Vief. When the wizards have enough helpers,",
                        "I will open a portal to the Air Altar.",
                    ).also {
                        stage++
                    }
                25 ->
                    npc(
                        "The energy appears in the form of floating orbs. These",
                        "can be moved by means of wands that attract or repel",
                        "them. Acantha or Vief will give you one of each wand.",
                    ).also {
                        stage++
                    }
                26 ->
                    npc(
                        "Your goal is to move the correct colour orb to the altar",
                        "stone, while keeping the other orbs away. Wizard",
                        "Acantha favours green orbs, while Wizard Vief",
                        "favours yellow ones.",
                    ).also {
                        stage++
                    }
                27 ->
                    npc(
                        "You will also have a third magic wand, which allows you to",
                        "create magical barriers to block the opposing team's",
                        "orbs.",
                    ).also {
                        stage++
                    }
                28 ->
                    npc(
                        "After two minutes the team that absorbed the most orbs",
                        "wins that altar. I then open the portal to the next altar",
                        "in the sequence. After you have visited all eight altars,",
                        "you will be returned here.",
                    ).also {
                        stage++
                    }
                29 ->
                    options(
                        "What's in it for me?",
                        "Could you go over the instruction again?",
                        "Which colour orb is best?",
                        "Thanks.",
                    ).also {
                        stage++
                    }
                30 ->
                    when (buttonId) {
                        1 -> player("What's in it for me?").also { stage = 46 }
                        2 -> player("Could you go over the instruction again?").also { stage = 24 }
                        3 -> player("Which colour orb is best?").also { stage++ }
                        4 -> player("Thanks.").also { stage = END_DIALOGUE }
                    }
                31 ->
                    npc(
                        "Wizard Acantha believes that the green orbs are best.",
                        "Wizard Vief believe that the yellow ones are. You",
                        "should help out the wizard whose team you join.",
                    ).also {
                        stage++
                    }
                32 -> player("But what do you think?").also { stage++ }
                33 -> npc("does it metter?").also { stage++ }
                34 -> options("Of course it matters!", "No, I suppose not.", "Never mind.").also { stage++ }
                35 ->
                    when (buttonId) {
                        1 -> player("Of course it matters!").also { stage = 37 }
                        2 -> player("No, I suppose not.").also { stage++ }
                        3 -> player("Never mind.").also { stage = END_DIALOGUE }
                    }
                36 ->
                    npc(
                        "No. The important thing is that the orbs get pushed",
                        "back into the altars, whatever colour they are.",
                    ).also {
                        stage =
                            1
                    }
                37 ->
                    npc(
                        "Of course it does, of course it does. Be careful",
                        "which team you join, then.",
                        "I'll accept your reward tokens, either way.",
                    ).also {
                        stage =
                            1
                    }
                38 ->
                    npc(
                        "You will notice that, whenever you use a Runecrafting",
                        "altar, you enter another plane: a self-contained island, or",
                        "cave, or some other place, which contains the true altar.",
                    ).also {
                        stage++
                    }
                39 ->
                    npc(
                        "These temples are not exactly in " + GameWorld.settings!!.name + ". They are pocket",
                        "dimensions unto themselves: areas of folded space created",
                        "by the energy of the rune altar.",
                    ).also {
                        stage++
                    }
                40 ->
                    options(
                        "So we're in something similar?",
                        "What does that have to do with the guild?",
                        "Not the Astral Altar.",
                    ).also {
                        stage++
                    }
                41 ->
                    when (buttonId) {
                        1 -> player("So we're in something similar?").also { stage = 42 }
                        2 -> player("What does that have to do with the guild?").also { stage = 45 }
                        3 -> player("Not the Astral Altar. That has no pocket dimension.").also { stage = 43 }
                    }
                42 ->
                    npc(
                        "Quite right. This is a shadow of Wizard's",
                        "Tower, What better place to study the",
                        "mysteries of Runecrafting?",
                    ).also {
                        stage =
                            40
                    }
                43 ->
                    npc(
                        "Quite right. I have heard of the Astral Altar,",
                        "although I have not been there myself. The lunar",
                        "wizards have found a way to keep the altar open.",
                    ).also {
                        stage++
                    }
                44 ->
                    npc(
                        "Their magic has flattened out the space around the",
                        "altar so the pocket dimension becomes part of normal space.",
                    ).also {
                        stage =
                            1
                    }
                45 ->
                    npc(
                        "Don't you see? The Runecrafting Guild exists in",
                        "a similar pocket dimension, created by our own magic.",
                        "What better place to study the mysteries of Runecrafting?",
                    ).also {
                        stage =
                            1
                    }
                46 ->
                    npc(
                        "A fair question. We have agreed on a token scheme",
                        "that allows you to choose from several rewards. When",
                        "you return from the last altar, your senior wizard will",
                        "give you a number of tokens.",
                    ).also {
                        stage++
                    }
                47 ->
                    npc(
                        "You will get 50 tokens per altar that your team captured,",
                        "provided that you contributed to the capture in some way.",
                        "You will get an extra 100 tokens if your team captured",
                        "more altars overall, or 50 extra if it is a draw.",
                    ).also {
                        stage++
                    }
                48 ->
                    npc(
                        "You can exchange the tokens for rewards by speaking to me.",
                        "You may also find rune essence appearing in your inventory",
                        "at the end of each round. This is a side-product of the",
                        "absorption process and you are free to use it as you wish.",
                    ).also {
                        stage =
                            1
                    }
                49 ->
                    options(
                        "What rewards are there?",
                        "Could you go over the instruction again?",
                        "Which colour orb is best?",
                        "Thanks.",
                    ).also {
                        stage++
                    }
                50 ->
                    when (buttonId) {
                        1 -> player("What rewards are there?").also { stage = 51 }
                        2 -> player("Could you go over the instruction again?").also { stage = 24 }
                        3 -> player("Which colour orb is best?").also { stage = 31 }
                        4 -> player("Thanks.").also { stage = END_DIALOGUE }
                    }
                51 ->
                    npc(
                        "The rewards include runemaster robes, designed to",
                        "protect you while Runecrafting. These robes also",
                        "let you move orbs a little further - if you wear",
                        "robes of the same colour as the orb.",
                    ).also {
                        stage++
                    }
                52 ->
                    npc(
                        "Another reward is the Runecrafting staff. This",
                        "can be combined with a talisman, in the same way",
                        "that a tiara can.",
                    ).also {
                        stage++
                    }
                53 ->
                    npc(
                        "I also offer teleport tablets to the various altars.",
                        "You may also trade your tokens in for talismans and",
                        "certificates you can exchange at a bank for",
                        "rune essence.",
                    ).also {
                        stage =
                            1
                    }
                54 ->
                    npc(
                        "Ever since the Duke of Lumbridge sent the first air",
                        "talisman to Sedridor, I have studied the talismans in",
                        "great detail.",
                    ).also {
                        stage++
                    }
                55 ->
                    npc(
                        "I believe I can create a new form of talisman that",
                        "combines the properties of all of them.",
                    ).also {
                        stage++
                    }
                56 ->
                    npc(
                        "The omni-talisman will allow you to access any of the",
                        "Runecrafting altars. It can be combined with a tiara or",
                        "a staff, just like an ordinary talisman. If you show me",
                    ).also {
                        stage++
                    }
                57 ->
                    npc(
                        "each type of known talisman, I will create an omni-talisman",
                        "for you. For each talisman you show me, I will also teach",
                        "you a bit about Runecrafting.",
                    ).also {
                        stage =
                            20
                    }
                58 ->
                    npc(
                        "Wizard Korvak is the only one of us to have",
                        "visited the Abyss. He learned about rune pouches",
                        "and how to repair them.",
                    ).also {
                        stage++
                    }
                59 ->
                    npc(
                        "None of us quite knows how he does it, and",
                        "I'm not sure he does either, but it seems to work.",
                    ).also {
                        stage =
                            20
                    }
                200 ->
                    npcl(
                        FaceAnim.FRIENDLY,
                        "Sure, there will be no problem with it, the cost of one token is 100 coins, how many will you need?",
                    ).also {
                        stage =
                            201
                    }
                201 -> {
                    setTitle(player, 4)
                    sendDialogueOptions(
                        player,
                        "How many tokens do you need?",
                        "50",
                        "250",
                        "1000",
                        "5000",
                    ).also { stage++ }
                }
                202 ->
                    when (buttonId) {
                        1 -> buyAmount = 50
                        2 -> buyAmount = 250
                        3 -> buyAmount = 1000
                        4 -> buyAmount = 5000
                        else -> buyAmount = 0
                    }.also {
                        if (buyAmount > 0) {
                            if (player?.inventory?.containsItem(Item(995, 100 * buyAmount))!!) {
                                player?.inventory?.add(Item(13650, buyAmount))
                                player?.inventory?.remove(Item(995, 100 * buyAmount))
                            } else {
                                sendMessage(player, "You don't have enough coins for that.")
                            }
                        }
                        end()
                    }
            }
            return true
        }

        private fun allItemsInInventory(): Boolean {
            val requiredItems = listOf(1438, 1440, 1442, 1444, 1446, 1448, 1452, 1454, 1458, 1462)
            return requiredItems.all { inInventory(player, it, 1) }
        }

        private val requiredTalismanItems =
            listOf(
                1438 to "air",
                1448 to "mind",
                1444 to "water",
                1440 to "earth",
                1442 to "fire",
                1446 to "body",
                1454 to "cosmic",
                1452 to "chaos",
                1462 to "nature",
                1458 to "law",
                1456 to "death",
                1450 to "blood",
            )

        private val ACCESS_TO_OMNI_ITEMS = "/save:rcguild:omni-access"

        override fun getIds(): IntArray {
            return intArrayOf(NPCs.WIZARD_ELRISS_8032)
        }
    }

    @Initializable
    class WizardAcanthaDialogue(
        player: Player? = null,
    ) : Dialogue(player) {
        override fun open(vararg args: Any?): Boolean {
            npc = args[0] as NPC
            npc("You look slightly more intelligent than the rest of", "these goons. Will you help me?")

            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            when (stage) {
                0 -> options("Yes, I'll help.", "No.", "Help with what?").also { stage++ }
                1 ->
                    when (buttonId) {
                        1 -> player("Yes, I'll help.").also { stage = 23 }
                        2 -> player("No.").also { stage++ }
                        3 -> player("Help with what?").also { stage = 3 }
                        4 -> player("How can you be sure about which orb is good?").also { stage = 19 }
                    }
                2 -> npc("Humph. The youth today have no sense of adventure.").also { stage = END_DIALOGUE }
                3 ->
                    npc(
                        "Don't you keep up with the Runecrafting Theory Journal?",
                        "We've been making major discoveries! The youth of today",
                        "are so lazy. Shall I tell you about it?",
                    ).also {
                        stage++
                    }
                4 -> player("Sure. What am I supposed to do?").also { stage++ }
                5 ->
                    npc(
                        "When we began formally studying the rune altars, we",
                        "saw a spike in the energy fields surrounding altars",
                        "when there is an increase in rune production.",
                    ).also {
                        stage++
                    }
                6 ->
                    npc(
                        "When an essence is used on the altar, energy is",
                        "transferred and bound into the essence, creating",
                        "a rune.",
                    ).also {
                        stage++
                    }
                7 ->
                    npc(
                        "As with all transfers of energy, some is lost in the",
                        "process. This lost energy, manifested as green orbs,",
                        "needs to be returned to the altar to keep them well",
                        "maintained.",
                    ).also {
                        stage++
                    }
                8 ->
                    npc(
                        "When the green energy escapes during the transfer,",
                        "yellow energy particles in the air gather around the",
                        "pure green energy. They form into opposing orbs and",
                        "must be kept away as they are impure energy forms.",
                    ).also {
                        stage++
                    }
                9 ->
                    npc(
                        "I need you to put the green orbs back in the altar. I",
                        "will give you two wands to move the orbs around and a",
                        "third wand to create barriers to prevent those pesky",
                        "yellow orbs from getting in.",
                    ).also {
                        stage++
                    }
                10 ->
                    npc(
                        "To further this cause, we've set up a project to repair",
                        "the altars. I wanted to just call it The Orb Project, but",
                        "the buffoon wanted it to sound grander, so now it's",
                        "The Great Orb Project. As if it needed an adjective.",
                    ).also {
                        stage++
                    }
                11 ->
                    options(
                        "Okay, okay. Green orbs into the altar, yellow orbs away.",
                        "Right, yellow orbs into altar, green orbs away.",
                        "So, what do I get for all this hard work?",
                        "How can you be sure about which orb is good?",
                    ).also {
                        stage++
                    }
                12 ->
                    when (buttonId) {
                        1 -> player("Okay, okay. Green orbs into the altar, yellow orbs away.").also { stage = 16 }
                        2 -> player("Right, yellow orbs into altar, green orbs away.").also { stage = 17 }
                        3 -> player("So, what do I get for all this hard work?").also { stage = 13 }
                        4 -> player("How can you be sure about which orb is good?").also { stage = 19 }
                    }
                13 ->
                    npc(
                        "Aaaah, a true mercenary. Although we cannot agree",
                        "scientifically, Vief and I have decided to reward those",
                        "who help us. If you get more green orbs into the altar",
                        "than the yellow team gets yellow orbs, you win the",
                    ).also {
                        stage++
                    }
                14 ->
                    npc(
                        "altar. You will receive rune essence each round you",
                        "win. The team who winds the most altars will receive",
                        "tokens which you can give to Wizard Elriss in exchange",
                        "for a reward. I don't know the exact details, so it's best",
                    ).also {
                        stage++
                    }
                15 -> npc(FaceAnim.LAUGH, "to speak to her.").also { stage = END_DIALOGUE }
                16 ->
                    npc(
                        "Well, at least you aren't as thick as that Wizard Vief.",
                        "Make sure to stop his apprentices putting yellow orbs into altars.",
                        "Use your wands intelligently and you'll come out victorious.",
                    ).also {
                        stage =
                            END_DIALOGUE
                    }
                17 -> npc("Read my lips, whippersnapper! NO YELLOW ORBS", "IN THE ALTAR.").also { stage++ }
                18 ->
                    player("Eek! Okay, okay. Green orbs into the altar,", "yellow orbs away.").also {
                        stage =
                            END_DIALOGUE
                    }
                19 ->
                    npc(
                        "Do you think I got to where I am today on false",
                        "calculations? I researched it, that's how I know!",
                        "There was a time when my opinion was respected and",
                        "accepted without question.",
                    ).also {
                        stage++
                    }
                20 ->
                    npc(
                        "Age steals that from you. I will not explain my",
                        "findings to fools. If you want to see how I reached my",
                        "conclusions, you should keep up with the scientific",
                        "wizarding community.",
                    ).also {
                        stage++
                    }
                21 ->
                    npc(
                        "I have no time for laziness! Wizard Vief, in",
                        "an attempt to progress his career, has decided",
                        "to disagree with my  findings and cast doubt upon my",
                        "ability as a wizard.",
                    ).also {
                        stage++
                    }
                22 ->
                    npc(
                        "What better way to attract attention to one's self",
                        "than disagree with those more knowledgeable?",
                        "Pay no attention to that man, he's a buffoon.",
                    ).also {
                        stage =
                            END_DIALOGUE
                    }
                23 ->
                    npc(
                        FaceAnim.ANNOYED,
                        "You need your head right hand and two free",
                        "inventory slots, or I can't give you the necessary",
                        "equipment.",
                    ).also {
                        stage =
                            END_DIALOGUE
                    }
            }
            return true
        }

        override fun getIds(): IntArray {
            return intArrayOf(NPCs.WIZARD_ACANTHA_8031)
        }
    }

    @Initializable
    class WizardKorvakDialogue(
        player: Player? = null,
    ) : Dialogue(player) {
        override fun open(vararg args: Any?): Boolean {
            npc = args[0] as NPC
            npcl(FaceAnim.HAPPY, "AAAAAAAAAAH!")
            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            val hasMediumPouch =
                hasAnItem(player, Items.MEDIUM_POUCH_5510).container != null &&
                    hasAnItem(player, Items.MEDIUM_POUCH_5511).container != null
            when (stage) {
                0 -> npc("Don't sneak up on me like that.").also { stage++ }
                1 -> player("Uh, sorry.").also { stage++ }
                2 ->
                    if (hasMediumPouch) {
                        player("I've lost my medium-sized pouch.", "Could you replace it?").also { stage = 37 }
                    } else {
                        setTitle(player!!, 4)
                        sendDialogueOptions(
                            player!!,
                            "Choose an option:",
                            "Can you help me with my essence pouches?",
                            "I've got an omni-talisman that I would like to attach to a tiara or staff.",
                            "Why are you so jumpy?",
                            "Never mind.",
                        )
                        stage++
                    }

                3 ->
                    when (buttonId) {
                        1 -> player("Can you help me with my essence pouches?").also { stage = 18 }
                        2 -> {
                            setComponentVisibility(player!!, 228, 6, true)
                            setComponentVisibility(player!!, 228, 9, false)
                            sendDialogueOptions(player!!, "Which would you like to make?", "Omni-tiara.", "Omni-staff.")
                            stage++
                        }

                        3 -> player("Why are you so jumpy?").also { stage = 7 }
                        4 -> end()
                    }

                4 ->
                    when (buttonId) {
                        1, 2 ->
                            player(
                                "I'd like to attach my omni-talisman to a " +
                                    if (buttonId ==
                                        1
                                    ) {
                                        "tiara."
                                    } else {
                                        "staff." + " Do you"
                                    },
                                "know how to do that?",
                            ).also { stage++ }
                    }

                5 -> {
                    if (!inInventory(player, Items.OMNI_TALISMAN_13649) ||
                        !inInventory(player, Items.TIARA_5525) ||
                        !inInventory(player, Items.RUNECRAFTING_STAFF_13629)
                    ) {
                        player("Oi, I'm broke!")
                        stage = 38
                    } else if (inInventory(player, Items.OMNI_TALISMAN_13649) &&
                        inInventory(player, Items.TIARA_5525)
                    ) {
                        npc("Perhaps...transform...carry the three and divide by the", "alteration factor - chikens.")
                        stage = 100
                    } else if (inInventory(player, Items.OMNI_TALISMAN_13649) &&
                        inInventory(player, Items.RUNECRAFTING_STAFF_13629)
                    ) {
                        npc("A little twist here, a little adhesive spell here. Kapow!")
                        stage = 200
                    } else {
                        sendMessage(player, "You do not have the required items.")
                    }
                }

                100 -> {
                    if (removeItem(player, Item(Items.OMNI_TALISMAN_13649, 1)) &&
                        removeItem(player, Item(Items.TIARA_5525, 1))
                    ) {
                        end()
                        npc("There! A pretty tiara for a pretty lady.")
                        addItemOrDrop(player, Items.OMNI_TIARA_13655, 1)
                    } else {
                        player("Oi, I'm broke!")
                        stage = 38
                    }
                }

                200 -> {
                    if (removeItem(player, Item(Items.OMNI_TALISMAN_13649, 1)) &&
                        removeItem(player, Item(Items.RUNECRAFTING_STAFF_13629, 1))
                    ) {
                        end()
                        npc("A staff for all your Runecrafting needs.")
                        addItemOrDrop(player, Items.OMNI_TALISMAN_STAFF_13642, 1)
                    } else {
                        player("Oi, I'm broke!")
                        stage = 38
                    }
                }

                7 -> npc("I am not jumpy! I am insane. There is a difference.").also { stage++ }
                8 ->
                    player(
                        "If you are insane, wouldn't you think you were sane",
                        "and so be insensible to the insanity that is you?",
                    ).also {
                        stage++
                    }
                9 -> npc("Yebno.").also { stage++ }
                10 -> player("Beg your pardon?").also { stage++ }
                11 ->
                    npc(
                        "Yebno. It's a fast way of saying Yes Maybe No. Since",
                        "I didn't know the answer to your question I gave you",
                        "all of the answers.",
                    ).also {
                        stage++
                    }
                12 -> player("How did you end up in your rather peculiar mindset?").also { stage++ }
                13 ->
                    npc(
                        FaceAnim.SAD,
                        "They sent me to the place. They knew the dark",
                        "wizards were there and someone had betrayed them.",
                        "Us. So they send me to spy. To the place.",
                        "They sent me and would not let me come back.",
                    ).also {
                        stage++
                    }
                14 -> player("who sent you? What betrayal?").also { stage++ }
                15 ->
                    npc(
                        "He sits there with the spinning light, always thinking.",
                        "One of us led them to the place where the pickaxe",
                        "hammers and so the betrayal happened.",
                    ).also {
                        stage++
                    }
                16 -> npc(FaceAnim.SAD, "Please don't make me talk about it.").also { stage++ }
                17 -> player("Okay, we don't have to talk about it.").also { stage = END_DIALOGUE }
                18 ->
                    if (!anyInInventory(player, 5509, 5510, 5511, 5512, 5513, 5514, 5515)) {
                        npc(
                            "You don't have any pouches that need repair...I",
                            "could sell you a pouch, but only if you don't tell!",
                        ).also {
                            stage =
                                29
                        }
                    } else {
                        npc(
                            "I can restore them for a price - for a",
                            "price, indeed. Muahahahahaahaha!",
                        ).also { stage++ }
                    }

                19 -> player("Uh, what kind of a price?").also { stage++ }
                20 ->
                    npc(
                        "Whatever the voices tell me to ask for.",
                        "Currently, they require 9.000 gp for a large pouch",
                        "and 12,000 gp for a giant pouch.",
                    ).also {
                        stage++
                    }

                21 ->
                    npc(
                        "Shhhh, don't tell any one else: I have a connection",
                        "on the inside and I can sell you pouches too.",
                        "For a mere 25,000 gp, you can have a large pouch.",
                    ).also {
                        stage++
                    }

                22 -> npc("A reasonable 50,000 gp will get you a giant pouch.").also { stage++ }
                23 -> options("I'd like to have a pouch repaired.", "I'd like to buy a pouch.").also { stage++ }
                24 ->
                    when (buttonId) {
                        1 -> player("I'd like to have a pouch repaired.").also { stage++ }
                        2 -> player("I'd like to buy a pouch.").also { stage = 32 }
                    }

                25 -> npc("Very well. Let's have a look at it.").also { stage++ }
                26 -> {
                    setComponentVisibility(player!!, 230, 7, true)
                    setComponentVisibility(player!!, 230, 10, false)
                    sendDialogueOptions(
                        player,
                        "Which pouch would you like to repair?",
                        "Repair large pouch for 9,000 gp.",
                        "Repair giant pouch for 12,000 gp.",
                        "Never mind.",
                    ).also {
                        stage++
                    }
                }

                27 ->
                    when (buttonId) {
                        1 -> player("Repair large pouch for 9,000 gp.").also { stage++ }
                        2 -> player("Repair giant pouch for 12,000 gp.").also { stage = 29 }
                        3 -> player("Never mind.").also { stage = END_DIALOGUE }
                    }

                28 -> {
                    if (!removeItem(player, Item(Items.COINS_995, 9000)) &&
                        inInventory(player, Items.LARGE_POUCH_5513)
                    ) {
                        player("Oi, I'm broke!")
                        stage = 38
                    } else if (!inInventory(player, Items.LARGE_POUCH_5513)) {
                        end()
                        npc("The voices are angry at you! You have nothing to", "repair. Leave us be.")
                    } else {
                        end()
                        repair()
                        removeItem(player, Item(Items.COINS_995, 9000))
                        npc(
                            "Magic makes me happy, magic makes me glad, magic",
                            "makes the voices quiet, and nothing rhymes with",
                            "purple.",
                        )
                    }
                }

                29 -> {
                    if (!removeItem(player, Item(Items.COINS_995, 12000)) &&
                        inInventory(player, Items.GIANT_POUCH_5515)
                    ) {
                        player("Oi, I'm broke!")
                        stage = 38
                    } else if (!inInventory(player, Items.GIANT_POUCH_5515)) {
                        end()
                        npc("The voices are angry at you! You have nothing to", "repair. Leave us be.")
                    } else {
                        end()
                        repair()
                        removeItem(player, Item(Items.COINS_995, 12000))
                        npc(
                            "Ahhh, the simple act of a transformation spell.",
                            "So, soothing. It makes the voices quiet.",
                            "Your pouch is repaired.",
                        )
                    }
                }

                30 -> options("I'd like to buy a pouch.", "Never mind.").also { stage++ }
                31 ->
                    when (buttonId) {
                        1 -> player("I'd like to buy a pouch.").also { stage++ }
                        2 -> player("Never mind.").also { stage = END_DIALOGUE }
                    }

                32 -> npc("Ah, coins to fund my rock collection.").also { stage++ }
                33 -> {
                    setTitle(player, 3)
                    sendDialogueOptions(
                        player,
                        "Which pouch would you like to buy?",
                        "Buy a large pouch for 25,000 gp.",
                        "Buy a giant pouch for 50,000 gp.",
                        "Never mind.",
                    ).also {
                        stage++
                    }
                }

                34 ->
                    when (buttonId) {
                        1 -> player("Buy a large pouch for 25,000 gp.").also { stage++ }
                        2 -> player("Buy a giant pouch for 50,000 gp.").also { stage = 36 }
                        3 -> player("Never mind.").also { stage = END_DIALOGUE }
                    }

                35 -> {
                    if (removeItem(player, Item(Items.COINS_995, 25000))) {
                        end()
                        addItemOrDrop(player, Items.LARGE_POUCH_5512)
                    } else {
                        player("Oi, I'm broke!")
                        stage = 38
                    }
                }

                36 -> {
                    if (removeItem(player, Item(Items.COINS_995, 50000))) {
                        end()
                        addItemOrDrop(player, Items.GIANT_POUCH_5514)
                    } else {
                        player("Oi, I'm broke!")
                        stage = 38
                    }
                }

                37 -> {
                    end()
                    npc(
                        "Lost it, did you? Or did they take it back?",
                        "CLAIM IT BACK! Quick, better have another before",
                        "the spoons come.",
                    )
                    addItemOrDrop(player, Items.MEDIUM_POUCH_5511, 1)
                }

                38 -> {
                    end()
                    npc("So?")
                }
            }
            return true
        }

        override fun getIds(): IntArray {
            return intArrayOf(NPCs.WIZARD_KORVAK_8029)
        }

        fun repair(): Boolean {
            player.pouchManager.pouches.forEach { (id: Int, pouch: Pouches) ->
                pouch.currentCap = pouch.capacity
                val newCharges: Int
                newCharges =
                    when (id) {
                        Items.MEDIUM_POUCH_5510 -> 264
                        Items.LARGE_POUCH_5512 -> 186
                        Items.GIANT_POUCH_5514 -> 140
                        else -> 3
                    }
                pouch.charges = newCharges
                var essence: Item? = null
                if (!pouch.container.isEmpty) {
                    val ess = pouch.container[0].id
                    val amount = pouch.container.getAmount(ess)
                    essence = Item(ess, amount)
                }
                pouch.remakeContainer()
                if (essence != null) {
                    pouch.container.add(essence)
                }
                if (id != 5509) {
                    if (player.inventory.contains(id + 1, 1)) {
                        player.inventory.remove(Item(id + 1, 1))
                        player.inventory.add(Item(id, 1))
                    }
                    if (player.bank.contains(id + 1, 1)) {
                        player.bank.remove(Item(id + 1, 1))
                        player.bank.add(Item(id, 1))
                    }
                }
            }
            return true
        }
    }

    @Initializable
    class WizardsDialogue(
        player: Player? = null,
    ) : Dialogue(player) {
        override fun open(vararg args: Any?): Boolean {
            npc = args[0] as NPC
            npcl(FaceAnim.HAPPY, "Hello!").also { stage = 0 }
            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            when (stage) {
                0 -> options("I want to join the orb project!", "Never mind.").also { stage++ }
                1 ->
                    when (buttonId) {
                        1 -> player("I want to join the orb project!").also { stage = END_DIALOGUE }
                        2 -> player("Never mind.").also { stage = END_DIALOGUE }
                    }
            }
            return true
        }

        override fun getIds(): IntArray {
            return intArrayOf(
                NPCs.WIZARD_8033,
                NPCs.WIZARD_8034,
                NPCs.WIZARD_8035,
                NPCs.WIZARD_8036,
                NPCs.WIZARD_8037,
                NPCs.WIZARD_8038,
                NPCs.WIZARD_8039,
                NPCs.WIZARD_8040,
            )
        }
    }

    @Initializable
    class WizardViefDialogue(
        player: Player? = null,
    ) : Dialogue(player) {
        override fun open(vararg args: Any?): Boolean {
            npc = args[0] as NPC
            npcl(FaceAnim.HAPPY, "Ah! You'll help me, won't you?").also { stage = END_DIALOGUE }
            return true
        }

        override fun handle(
            interfaceId: Int,
            buttonId: Int,
        ): Boolean {
            return true
        }

        override fun getIds(): IntArray {
            return intArrayOf(NPCs.WIZARD_VIEF_8030)
        }
    }
}
