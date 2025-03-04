package core.game.shops

import content.global.skill.crafting.Tanning
import core.ServerConstants
import core.api.*
import core.api.item.itemDefinition
import core.api.quest.getQuestPoints
import core.api.quest.getQuestStage
import core.api.quest.isQuestComplete
import core.game.dialogue.FaceAnim
import core.game.dialogue.InputType
import core.game.ge.GrandExchange
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.system.command.Privilege
import core.game.world.GameWorld
import core.tools.Log
import core.tools.secondsToTicks
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.rs.consts.Components
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Quests
import java.io.FileReader

class Shops :
    StartupListener,
    TickListener,
    InteractionListener,
    InterfaceListener,
    Commands {
    companion object {
        @JvmStatic
        val personalizedShops = "world.personalized_shops"

        @JvmStatic
        val shopsById = Int2ObjectOpenHashMap<Shop>()

        @JvmStatic
        val shopsByNpc = Int2ObjectOpenHashMap<Shop>()

        private var lastPlayerStockClear = 0

        @JvmStatic
        fun openId(
            player: Player,
            id: Int,
        ) {
            shopsById[id]?.openFor(player)
        }

        fun logShop(msg: String) {
            log(this::class.java, Log.FINE, "[SHOPS] $msg")
        }

        fun parseStock(
            stock: String,
            id: Int,
        ): List<ShopItem> {
            if (stock.isEmpty()) return emptyList()

            val items = mutableListOf<ShopItem>()
            val idsInStock = IntOpenHashSet()

            stock.split('-').forEach {
                try {
                    val tokens =
                        it
                            .replace("{", "")
                            .replace("}", "")
                            .split(",")
                            .toTypedArray()
                    val itemId = tokens[0].toInt()
                    val amount = tokens[1].trim().toIntOrNull() ?: -1
                    val restockRate = tokens.getOrNull(2)?.toIntOrNull() ?: 100

                    if (idsInStock.add(itemId)) {
                        items.add(ShopItem(itemId, amount, restockRate))
                    } else {
                        val existingItem = items.find { it.itemId == itemId }
                        existingItem?.amount = (existingItem?.amount ?: 0) + amount
                    }
                } catch (e: Exception) {
                    log(this::class.java, Log.WARN, "[SHOPS] MALFORMED STOCK IN SHOP ID $id FOR ITEM $it")
                }
            }
            return items
        }
    }

    override fun startup() {
        val path = ServerConstants.CONFIG_PATH + "shops.json"
        logShop("Using JSON path: $path")

        val reader = FileReader(path)
        val data = JSONParser().parse(reader) as JSONArray

        data.forEach { rawShop ->
            val shopData = rawShop as JSONObject
            val id = shopData["id"].toString().toInt()
            val title = shopData["title"].toString()
            val general = shopData["general_store"].toString().toBoolean()
            val stock = parseStock(shopData["stock"].toString(), id)
            val npcs = shopData["npcs"]?.toString()?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
            val currency = shopData["currency"].toString().toInt()
            val highAlch = shopData["high_alch"].toString() == "1"
            val forceShared = shopData.getOrDefault("force_shared", "false").toString().toBoolean()

            val shop = Shop(title, stock.toTypedArray(), general, currency, highAlch, forceShared)
            npcs.forEach { shopsByNpc[it] = shop }
            shopsById[id] = shop
        }

        logShop("Parsed ${shopsById.size} shops.")
    }

    override fun tick() {
        shopsById.values.forEach(Shop::restock)

        val playerStockClearInterval = secondsToTicks(ServerConstants.PLAYER_STOCK_CLEAR_INTERVAL * 60)
        if (getWorldTicks() % playerStockClearInterval == 0) {
            val clearToGe = ServerConstants.PLAYER_STOCK_RECIRCULATE
            if (clearToGe) {
                Shop.generalPlayerStock.toArray().filterNotNull().forEach {
                    GrandExchange.addBotOffer(it.id, it.amount)
                }
            }
            Shop.generalPlayerStock.clear()
        }
    }

    override fun defineListeners() {
        on(IntType.NPC, "trade", "shop") { player, node ->
            val npc = node as NPC
            if (npc.id == 2824 || npc.id == 1041 || npc.id == 804) {
                Tanning.open(player, npc.id)
                return@on true
            }
            if (npc.id == 7601) {
                openInterface(player, 732)
                return@on true
            }
            shopsByNpc[npc.id]?.openFor(player) ?: return@on false
            return@on true
        }

        on(NPCs.SIEGFRIED_ERKLE_933, IntType.NPC, "trade") { player, node ->
            val points = getQuestPoints(player)
            if (!GameWorld.settings!!.isMembers) {
                sendNPCDialogueLines(
                    player,
                    NPCs.SIEGFRIED_ERKLE_933,
                    FaceAnim.HALF_GUILTY,
                    false,
                    "I'm sorry but serves of this shop are only for the",
                    "pleasure of those who are rightful members of the",
                    "Legends Guild. I would get into serious trouble if I sold",
                    "a non-member an item from this store.",
                )
            } else if (points < 40) {
                sendNPCDialogueLines(
                    player,
                    NPCs.SIEGFRIED_ERKLE_933,
                    FaceAnim.HALF_GUILTY,
                    false,
                    "I'm sorry but serves of this shop are only for the",
                    "pleasure of those who are rightful members of the",
                    "Legends Guild.",
                )
            } else {
                shopsByNpc[node.id]?.openFor(player)
            }
            return@on true
        }

        on(NPCs.FUR_TRADER_1316, IntType.NPC, "trade") { player, node ->
            if (!isQuestComplete(player, Quests.THE_FREMENNIK_TRIALS)) {
                sendNPCDialogue(player, NPCs.FUR_TRADER_1316, "I don't sell to outerlanders.", FaceAnim.ANNOYED)
                return@on true
            }
            shopsByNpc[node.id]?.openFor(player)
            return@on true
        }

        on(NPCs.CANDLE_MAKER_562, IntType.NPC, "trade") { player, node ->
            if (getQuestStage(player, Quests.MERLINS_CRYSTAL) > 60) {
                openId(player, 56)
            } else {
                shopsByNpc[node.id]?.openFor(player)
            }
            return@on true
        }
    }

    override fun defineInterfaceListeners() {
        on(Components.SHOP_TEMPLATE_620) { player, _, opcode, buttonID, slot, _ ->
            val opValue = 155
            val opExamine = 9
            val buyOne = 196
            val buyFive = 124
            val buyTen = 199
            val buyX = 234

            val shop = getAttribute<Shop?>(player, "shop", null) ?: return@on false
            val isMainStock = getAttribute(player, "shop-main", true)

            when (buttonID) {
                26 -> shop.showTab(player, false).also { return@on true }
                25 -> shop.showTab(player, true).also { return@on true }
                27, 29 -> return@on true
            }

            val price = shop.getBuyPrice(player, slot)

            when (opcode) {
                opValue ->
                    sendMessage(
                        player,
                        "${getItemName(
                            if (isMainStock) shop.stock[slot].itemId else shop.playerStock[slot].id,
                        )}: This item currently costs ${price.amount} ${price.name.lowercase()}.",
                    )

                buyOne -> shop.buy(player, slot, 1)
                buyFive -> shop.buy(player, slot, 5)
                buyTen -> shop.buy(player, slot, 10)
                buyX ->
                    sendInputDialogue(player, InputType.AMOUNT, "Enter the amount to buy:") { value ->
                        val amt = value as Int
                        shop.buy(player, slot, amt)
                    }

                opExamine ->
                    sendMessage(
                        player,
                        itemDefinition(if (isMainStock) shop.stock[slot].itemId else shop.playerStock[slot].id).examine,
                    )
            }

            return@on true
        }

        onOpen(Components.SHOP_TEMPLATE_SIDE_621) { player, _ ->
            val settings = IfaceSettingsBuilder().enableOptions(0 until 9).build()
            player.packetDispatch.sendIfaceSettings(settings, 0, 621, 0, 28)
            player.packetDispatch.sendRunScript(
                150,
                "IviiiIsssssssss",
                "",
                "",
                "",
                "",
                "Sell X",
                "Sell 10",
                "Sell 5",
                "Sell 1",
                "Value",
                -1,
                0,
                7,
                4,
                93,
                621 shl 16,
            )
            return@onOpen true
        }

        onClose(Components.SHOP_TEMPLATE_620) { player, _ ->
            val shop = getAttribute<Shop?>(player, "shop", null) ?: return@onClose true
            val listener = Shop.listenerInstances[player.details.uid] ?: return@onClose true

            if (getServerConfig().getBoolean(personalizedShops, false)
            ) {
                shop.stockInstances[player.details.uid]?.listeners?.remove(listener)
            } else {
                shop.stockInstances[ServerConstants.SERVER_NAME.hashCode()]!!.listeners.remove(listener)
            }

            shop.playerStock.listeners.remove(listener)
            player.interfaceManager.closeSingleTab()
            return@onClose true
        }

        on(Components.SHOP_TEMPLATE_SIDE_621) { player, _, opcode, _, slot, _ ->
            val opValue = 155
            val sellOne = 196
            val sellFive = 124
            val sellTen = 199
            val sellX = 234

            val shop = getAttribute<Shop?>(player, "shop", null) ?: return@on false

            val itemInSlot = player.inventory[slot]
            if (itemInSlot == null) {
                player.sendMessage("That item doesn't appear to be there anymore. Please try again.")
                return@on true
            }

            val (_, price) = shop.getSellPrice(player, slot)
            val def = itemDefinition(player.inventory[slot].id)

            val valueMsg =
                when {
                    (price.amount == -1) ||
                        !def.hasShopCurrencyValue(price.id) ||
                        def.id in
                        intArrayOf(
                            Items.COINS_995,
                            Items.TOKKUL_6529,
                            Items.ARCHERY_TICKET_1464,
                            Items.CASTLE_WARS_TICKET_4067,
                        ) -> "This shop will not buy that item."
                    else -> "${player.inventory[slot].name}: This shop will buy this item for ${price.amount} ${price.name.lowercase()}."
                }

            when (opcode) {
                opValue -> sendMessage(player, valueMsg)
                sellOne -> shop.sell(player, slot, 1)
                sellFive -> shop.sell(player, slot, 5)
                sellTen -> shop.sell(player, slot, 10)
                sellX ->
                    sendInputDialogue(player, InputType.AMOUNT, "Enter the amount to sell:") { value ->
                        val amt = value as Int
                        shop.sell(player, slot, amt)
                    }
            }

            return@on true
        }
    }

    override fun defineDestinationOverrides() {
        setDest(IntType.NPC, "trade", "shop") { _, node ->
            val npc = node as NPC
            if (npc.getAttribute("facing_booth", false)) {
                val offsetX = npc.direction.stepX shl 1
                val offsetY = npc.direction.stepY shl 1
                return@setDest npc.location.transform(offsetX, offsetY, 0)
            }
            return@setDest node.location
        }
    }

    override fun defineCommands() {
        define("openshop", Privilege.ADMIN) { player, args ->
            if (args.size < 2) reject(player, "Usage: ::openshop shopId")
            val shopId = args[1].toInt()
            shopsById[shopId]?.openFor(player)
        }

        define("shopscript", Privilege.ADMIN) { player, args ->
            val arg1 = args[1].toInt()

            player.packetDispatch.sendRunScript(25, "vg", arg1, 92)
        }
    }
}
