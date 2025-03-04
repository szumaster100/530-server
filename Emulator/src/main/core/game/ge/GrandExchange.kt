package core.game.ge

import core.ServerConstants
import core.api.*
import core.api.item.itemDefinition
import core.cache.def.impl.ItemDefinition
import core.game.node.entity.player.Player
import core.game.node.entity.player.info.PlayerDetails
import core.game.system.command.Privilege
import core.game.system.config.ItemConfigParser
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.repository.Repository
import core.tools.Log
import core.tools.SystemLogger
import core.tools.colorize
import org.rs.consts.Sounds
import java.lang.Integer.max
import java.util.concurrent.LinkedBlockingDeque

class GrandExchange :
    StartupListener,
    Commands {
    var isRunning = false

    fun boot() {
        if (isRunning) return

        SystemLogger.logGE("Initializing GE Update Worker")

        Thread {
            Thread.currentThread().name = "GE Update Worker"
            while (true) {
                var offer = pendingOffers.takeFirst()
                offer.writeNew()
                offer = getOfferByUid(offer.uid) ?: continue
                selectPotentialMatches(offer)
                    .asSequence()
                    .sortedBy { if (offer.sell) -it.offeredValue else it.offeredValue }
                    .filter {
                        if (offer.sell) {
                            it.offeredValue >= offer.offeredValue
                        } else {
                            it.offeredValue <=
                                offer.offeredValue
                        }
                    }.forEach { match -> exchange(offer, match) }
                if (!offer.isBot && offer.amountLeft > 0 && !offer.sell) {
                    tryExchangeWithBots(offer)
                }
            }
        }.start()

        isRunning = true
    }

    private fun tryExchangeWithBots(offer: GrandExchangeOffer) {
        GEDatabase.run { conn ->
            val query = conn.prepareStatement(GET_MATCH_FROM_BOT_OFFERS)
            query.setInt(1, offer.itemID)
            val res = query.executeQuery()

            if (res.next()) {
                exchange(offer, GrandExchangeOffer.fromBotQuery(res).also { it.timeStamp = offer.timeStamp - 1L })
            }
        }
    }

    private fun selectPotentialMatches(offer: GrandExchangeOffer): List<GrandExchangeOffer> {
        val matches = ArrayList<GrandExchangeOffer>()
        GEDatabase.run { conn ->
            val query = conn.prepareStatement(GET_MATCHES_FROM_PLAYER_OFFERS)
            query.setInt(1, offer.itemID)
            query.setBoolean(2, !offer.sell)
            val res = query.executeQuery()
            while (res.next()) {
                matches.add(GrandExchangeOffer.fromQuery(res))
            }
        }
        return matches
    }

    override fun defineCommands() {
        define("addbotoffer", Privilege.ADMIN) { player, strings ->
            val id = strings[1].toInt()
            val amount = strings[2].toInt()
            addBotOffer(id, amount)
            notify(player, "Added ${amount}x ${getItemName(id)} to the bot offers.")
        }

        define("bange", Privilege.ADMIN) { player, strings ->
            val id = strings[1].toInt()
            PriceIndex.banItem(id)
            notify(player, "Banned ${getItemName(id)} from GE trade.")
        }

        define("allowge", Privilege.ADMIN) { player, strings ->
            val id = strings[1].toInt()
            PriceIndex.allowItem(id)
            notify(player, "Allowed ${getItemName(id)} for GE trade.")
        }

        define("geprivacy", Privilege.ADMIN) { player, _ ->
            val current = getAttribute(player, "ge-exclude", false)
            val new = !current
            notify(player, "Your name is now ${if (new) colorize("%RHIDDEN") else colorize("%RSHOWN")}.")
            setAttribute(player, "/save:ge-exclude", new)
        }
    }

    companion object {
        val pendingOffers = LinkedBlockingDeque<GrandExchangeOffer>()
        private val GET_SPECIFIC_OFFER_BY_UID = "SELECT * FROM player_offers WHERE uid = ?;"
        private val GET_MATCHES_FROM_PLAYER_OFFERS = "SELECT * FROM player_offers WHERE item_id = ? AND is_sale = ? AND offer_state < 4 AND NOT offer_state = 2;"
        private val GET_MATCH_FROM_BOT_OFFERS = "SELECT * FROM bot_offers WHERE item_id = ?;"

        private fun getOfferByUid(uid: Long): GrandExchangeOffer? {
            var offer: GrandExchangeOffer? = null
            GEDatabase.run { conn ->
                val query = conn.prepareStatement(GET_SPECIFIC_OFFER_BY_UID)
                query.setLong(1, uid)
                val res = query.executeQuery()

                if (res.next()) {
                    offer = GrandExchangeOffer.fromQuery(res)
                }
            }
            return offer
        }

        @JvmStatic
        fun getRecommendedPrice(
            itemID: Int,
            fromBot: Boolean = false,
        ): Int {
            var base = max(PriceIndex.getValue(itemID), getItemDefPrice(itemID))
            if (fromBot) base = (max(BotPrices.getPrice(itemID), base) * 1.10).toInt()
            return base
        }

        private fun getItemDefPrice(itemID: Int): Int {
            return max(
                itemDefinition(itemID).getConfiguration(ItemConfigParser.GE_PRICE) ?: 0,
                itemDefinition(itemID).value,
            )
        }

        @JvmStatic
        fun getOfferStats(
            itemID: Int,
            sale: Boolean,
        ): String {
            val sb = StringBuilder()

            GEDatabase.run { conn ->
                var foundOffers = 0
                var totalAmount = 0
                var bestPrice = 0
                var stmt = conn.createStatement()

                if (!sale) {
                    var botAmt = 0
                    var botPrice = 0
                    val player_offers =
                        stmt.executeQuery(
                            "SELECT * from player_offers where item_id = $itemID AND is_sale = 1 AND offer_state < 4 AND NOT offer_state = 2",
                        )

                    while (player_offers.next()) {
                        val o = GrandExchangeOffer.fromQuery(player_offers)
                        ++foundOffers
                        totalAmount += o.amountLeft
                        if (o.offeredValue < bestPrice || bestPrice == 0) {
                            bestPrice = o.offeredValue
                        }
                    }

                    stmt.close()
                    stmt = conn.createStatement()
                    val bot_offers = stmt.executeQuery("SELECT * from bot_offers where item_id = $itemID")
                    if (bot_offers.next()) {
                        val o = GrandExchangeOffer.fromBotQuery(bot_offers)
                        botAmt = o.amount
                        botPrice = getRecommendedPrice(itemID, true)
                    }

                    sb.append("Player Stock: <col=FFFFFF>$totalAmount  ")
                    sb.append("</col>  Lowest Price: <col=FFFFFF>$bestPrice<br>")
                    sb.append("-".repeat(50))
                    sb.append("</col><br>Bot Stock: <col=FFFFFF>$botAmt  ")
                    sb.append("</col>  Bot Price: <col=FFFFFF>$botPrice")
                } else {
                    val buy_offers =
                        stmt.executeQuery(
                            "SELECT * from player_offers where item_id = $itemID AND is_sale = 0 AND offer_state < 4 AND NOT offer_state = 2",
                        )

                    while (buy_offers.next()) {
                        val o = GrandExchangeOffer.fromQuery(buy_offers)
                        ++foundOffers
                        totalAmount += o.amountLeft
                        if (o.offeredValue > bestPrice) {
                            bestPrice = o.offeredValue
                        }
                    }

                    sb.append("Buy Offers: <col=FFFFFF>$totalAmount    ")
                    sb.append("</col>Highest Offer: <col=FFFFFF>$bestPrice</col>")
                }

                stmt.close()
            }

            return sb.toString()
        }

        fun addBotOffer(
            itemID: Int,
            amount: Int,
        ): Boolean {
            if (!PriceIndex.canTrade(itemID)) {
                return false
            }

            val itemDef = ItemDefinition.forId(itemID)
            val offer = GrandExchangeOffer.createBotOffer(if (itemDef.isUnnoted) itemID else itemDef.noteId, amount)
            pendingOffers.addLast(offer)

            return true
        }

        fun dispatch(
            player: Player,
            offer: GrandExchangeOffer,
        ): Boolean {
            if (offer.amount < 1) {
                sendMessage(player, "You must choose the quantity you wish to buy!").also { return false }
            }

            if (offer.offeredValue < 1) {
                sendMessage(player, "You must choose the price you wish to buy for!").also { return false }
            }

            if (offer.offerState != OfferState.PENDING || offer.uid != 0L) {
                log(this::class.java, Log.WARN, "[GE] DISPATCH FAILURE: ${offer.offerState.name}, UID: ${offer.uid}")
                return false
            }

            if (player.isArtificial) {
                offer.playerUID = PlayerDetails.getDetails(GameWorld.settings!!.name).uid.also { offer.isBot = true }
            } else {
                offer.playerUID = player.details.uid
            }

            offer.offerState = OfferState.REGISTERED
            // GERecords.getInstance(player).update(offer)

            if (offer.sell && !player.isArtificial) {
                val username = if (getAttribute(player, "ge-exclude", false)) "?????" else player.username
                Repository.sendNews(
                    username + " just offered " + offer.amount + " " + getItemName(offer.itemID) + " on the GE.",
                )
            }

            if (ServerConstants.I_AM_A_CHEATER) {
                val otherO = GrandExchangeOffer()
                otherO.itemID = offer.itemID
                otherO.amount = offer.amount
                otherO.sell = !offer.sell
                otherO.offeredValue = offer.offeredValue
                offer.writeNew()
                GameWorld.Pulser.submit(
                    object : Pulse(5) {
                        override fun pulse(): Boolean {
                            val offer2 = getOfferByUid(offer.uid) ?: return false
                            exchange(offer2, otherO)
                            return true
                        }
                    },
                )
                return true
            }

            pendingOffers.add(offer)
            return true
        }

        fun exchange(
            offer: GrandExchangeOffer,
            other: GrandExchangeOffer,
        ) {
            if (offer.sell == other.sell) return
            val amount = Integer.min(offer.amount - offer.completedAmount, other.amount - other.completedAmount)

            if (amount == 0) return

            val seller = if (offer.sell) offer else other
            val buyer = if (offer == seller) other else offer

            val sellerBias = seller.timeStamp > buyer.timeStamp

            if (seller.offeredValue > buyer.offeredValue) return

            seller.completedAmount += amount
            buyer.completedAmount += amount

            if (seller.amountLeft < 1 && seller.player != null) {
                playAudio(seller.player!!, Sounds.GE_COLLECT_COINS_4042)
            }

            seller.addWithdrawItem(995, amount * if (sellerBias) buyer.offeredValue else seller.offeredValue)
            buyer.addWithdrawItem(seller.itemID, amount)

            if (!sellerBias) {
                buyer.addWithdrawItem(995, amount * (buyer.offeredValue - seller.offeredValue))
            }

            if (seller.amountLeft < 1) {
                seller.offerState = OfferState.COMPLETED
            }
            if (buyer.amountLeft < 1) {
                buyer.offerState = OfferState.COMPLETED
            }

            val totalCoinXC = (if (sellerBias) buyer.offeredValue else seller.offeredValue) * amount

            seller.totalCoinExchange += totalCoinXC
            buyer.totalCoinExchange += totalCoinXC

            if (canUpdatePriceIndex(seller, buyer)) {
                PriceIndex.addTrade(offer.itemID, amount, (totalCoinXC / amount))
            }

            seller.update()
            val sellerPlayer = Repository.uid_map[seller.playerUID]
            sellerPlayer?.let { ExchangeHistory.getInstance(sellerPlayer).visualizeRecords() }
            buyer.update()
            val buyerPlayer = Repository.uid_map[buyer.playerUID]
            buyerPlayer?.let { ExchangeHistory.getInstance(buyerPlayer).visualizeRecords() }
        }

        private fun canUpdatePriceIndex(
            seller: GrandExchangeOffer,
            buyer: GrandExchangeOffer,
        ): Boolean {
            if (seller.playerUID == buyer.playerUID) return false
            if (!ServerConstants.BOTS_INFLUENCE_PRICE_INDEX && (seller.isBot || buyer.isBot)) return false
            return true
        }

        fun getValidOffers(): List<GrandExchangeOffer> {
            val offers = ArrayList<GrandExchangeOffer>()

            GEDatabase.run { conn ->
                val stmt = conn.createStatement()

                val results =
                    stmt.executeQuery("SELECT * FROM player_offers WHERE offer_state < 4 AND NOT offer_state = 2")
                while (results.next()) {
                    val o = GrandExchangeOffer.fromQuery(results)
                    offers.add(o)
                }
                stmt.close()
            }
            return offers
        }

        fun getBotOffers(): List<GrandExchangeOffer> {
            val offers = ArrayList<GrandExchangeOffer>()

            GEDatabase.run { conn ->
                val stmt = conn.createStatement()
                val results = stmt.executeQuery("SELECT item_id,amount FROM bot_offers WHERE amount > 0")
                while (results.next()) {
                    val o = GrandExchangeOffer.fromBotQuery(results)
                    offers.add(o)
                }
                stmt.close()
            }
            return offers
        }

        fun getBotstockForId(itemId: Int): Int {
            var total = 0
            GEDatabase.run { conn ->
                val stmt = conn.prepareStatement("SELECT sum(amount) FROM bot_offers WHERE amount > 0 AND item_id = ?")
                stmt.setInt(1, itemId)

                val results = stmt.executeQuery()
                while (results.next()) {
                    total += results.getInt(1)
                }
            }
            return total
        }
    }

    override fun startup() {
        GEDatabase.init()
        boot()
    }
}
