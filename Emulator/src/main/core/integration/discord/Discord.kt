package core.integration.discord

import core.ServerConstants
import core.api.TickListener
import core.api.getItemName
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class Discord : TickListener {
    override fun tick() {
        if (removeList.isNotEmpty()) {
            pendingMessages.removeAll(removeList.toSet())
            removeList.clear()
        }

        if (!pendingMessages.isEmpty() && !flushingPends) {
            flushingPends = true
            GlobalScope.launch {
                try {
                    val count = pendingMessages.size
                    for (i in 0 until count) {
                        val (url, content) = pendingMessages[i]
                        sendJsonPost(url, content)
                        removeList.add(pendingMessages[i])
                    }
                } finally {
                    flushingPends = false
                }
            }
        }
    }

    companion object {
        private const val COLOR_NEW_BUY_OFFER = 47789
        private const val COLOR_NEW_SALE_OFFER = 5752709
        private const val COLOR_OFFER_UPDATE = 15588691

        private val pendingMessages = ArrayList<Pair<String, String>>()
        private var flushingPends = false
        private val removeList = ArrayList<Pair<String, String>>()

        fun postNewOffer(
            isSale: Boolean,
            itemId: Int,
            value: Int,
            qty: Int,
            user: String,
        ) {
            if (ServerConstants.DISCORD_GE_WEBHOOK.isEmpty()) return
            GlobalScope.launch {
                val offer = encodeOfferJson(isSale, itemId, value, qty, user)
                try {
                    sendJsonPost(ServerConstants.DISCORD_GE_WEBHOOK, offer)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun postOfferUpdate(
            isSale: Boolean,
            itemId: Int,
            value: Int,
            amtLeft: Int,
        ) {
            if (ServerConstants.DISCORD_GE_WEBHOOK.isEmpty()) return
            GlobalScope.launch {
                val offer = encodeUpdateJson(isSale, itemId, value, amtLeft)
                try {
                    sendJsonPost(ServerConstants.DISCORD_GE_WEBHOOK, offer)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @JvmStatic
        fun postPlayerAlert(
            player: String,
            type: String,
        ) {
            if (ServerConstants.DISCORD_MOD_WEBHOOK.isEmpty()) return
            GlobalScope.launch {
                val alert = encodeUserAlert(type, player)
                try {
                    sendJsonPost(ServerConstants.DISCORD_MOD_WEBHOOK, alert)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        @JvmStatic fun sendToOpenRSC(
            player: String,
            type: String,
        ) {
            if (ServerConstants.DISCORD_OPENRSC_HOOK.isEmpty()) return
            GlobalScope.launch {
                val alert = encodeUserAlert(type, player)
                try {
                    sendJsonPost(ServerConstants.DISCORD_OPENRSC_HOOK, alert)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun encodeUpdateJson(
            sale: Boolean,
            itemId: Int,
            value: Int,
            amtLeft: Int,
        ): String {
            val obj = JSONObject()
            val embeds = JSONArray()
            val embed = JSONObject()

            val fields =
                arrayOf(
                    EmbedField("Item", getItemName(itemId), false),
                    EmbedField("Amount Remaining", "%,d".format(amtLeft), true),
                    EmbedField("Price", "%,d".format(value) + "gp", true),
                )

            embed["title"] = if (sale) "Sell Offer Updated" else "Buy Offer Updated"
            embed["color"] = COLOR_OFFER_UPDATE
            embed["thumbnail"] = getItemImage(itemId)
            embed["fields"] = getFields(fields)

            embeds.add(embed)
            obj["embeds"] = embeds

            return obj.toJSONString()
        }

        private fun encodeOfferJson(
            isSale: Boolean,
            itemId: Int,
            value: Int,
            qty: Int,
            user: String,
        ): String {
            val obj = JSONObject()
            val embeds = JSONArray()
            val embed = JSONObject()

            val fields =
                arrayOf(
                    EmbedField("Player", user, false),
                    EmbedField("Item", getItemName(itemId), false),
                    EmbedField("Amount", "%,d".format(qty), true),
                    EmbedField("Price", "%,d".format(value) + "gp", true),
                )

            embed["title"] = if (isSale) "New Sell Offer" else "New Buy Offer"
            embed["color"] = if (isSale) COLOR_NEW_SALE_OFFER else COLOR_NEW_BUY_OFFER
            embed["thumbnail"] = getItemImage(itemId)
            embed["fields"] = getFields(fields)

            embeds.add(embed)
            obj["embeds"] = embeds

            return obj.toJSONString()
        }

        private fun encodeUserAlert(
            type: String,
            player: String,
        ): String {
            val obj = JSONObject()
            val embeds = JSONArray()
            val embed = JSONObject()

            val fields =
                arrayOf(
                    EmbedField("Player", player, false),
                    EmbedField("Type", type, false),
                )

            embed["title"] = "Player Alert"
            embed["fields"] = getFields(fields)
            embeds.add(embed)
            obj["embeds"] = embeds
            return obj.toJSONString()
        }

        private fun getFields(fields: Array<EmbedField>): JSONArray {
            val arr = JSONArray()

            for (field in fields) {
                val o = JSONObject()
                o["name"] = field.name
                o["value"] = field.value
                if (field.inline) o["inline"] = true
                arr.add(o)
            }

            return arr
        }

        data class EmbedField(
            val name: String,
            val value: String,
            val inline: Boolean,
        )

        fun getItemImage(id: Int): JSONObject {
            val obj = JSONObject()
            obj["url"] = "https://github.com/szumaster3/web/raw/master/services/m%3Ddata/img/items/$id.png"
            return obj
        }

        private fun sendJsonPost(
            url: String = ServerConstants.DISCORD_GE_WEBHOOK,
            data: String,
        ) {
            try {
                val conn = URL(url).openConnection() as HttpURLConnection
                conn.doOutput = true
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.useCaches = false

                DataOutputStream(conn.outputStream).use { it.writeBytes(data) }
                BufferedReader(InputStreamReader(conn.inputStream)).use { br ->
                    var line: String?
                    while (br.readLine().also { line = it } != null) {
                        println(line)
                    }
                }
            } catch (e: Exception) {
                pendingMessages.add(Pair(url, data))
            }
        }
    }
}
