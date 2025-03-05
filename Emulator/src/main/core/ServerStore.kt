package core

import core.api.PersistWorld
import core.api.getItemName
import core.api.log
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.tools.Log
import core.tools.SystemLogger.logShutdown
import core.tools.SystemLogger.logStartup
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import javax.script.ScriptEngineManager

class ServerStore : PersistWorld {
    override fun parse() {
        logStartup("Parsing server store...")
        val dir = File(ServerConstants.STORE_PATH!!)
        if (!dir.exists()) {
            dir.mkdirs()
            return
        }

        var parser: JSONParser
        var reader: FileReader

        dir.listFiles()?.forEach { storeFile ->
            val key = storeFile.nameWithoutExtension

            reader = FileReader(storeFile)
            parser = JSONParser()

            try {
                val data = parser.parse(reader) as JSONObject
                fileMap[key] = data
                counter++
            } catch (e: Exception) {
                log(this::class.java, Log.ERR, "Failed parsing ${storeFile.name} - stack trace below.")
                e.printStackTrace()
                return@forEach
            }
        }

        logStartup("Initialized $counter store files.")
    }

    override fun save() {
        logShutdown("Saving server store...")
        val dir = File(ServerConstants.DATA_PATH + File.separator + "serverstore")
        if (!dir.exists()) {
            dir.mkdirs()
            return
        }

        val manager = ScriptEngineManager()
        val scriptEngine = manager.getEngineByName("JavaScript")

        fileMap.forEach { (name, data) ->
            val path = dir.absolutePath + File.separator + name + ".json"

            scriptEngine.put("jsonString", data.toJSONString())
            scriptEngine.eval("result = JSON.stringify(JSON.parse(jsonString), null, 2)")
            val prettyPrintedJson = scriptEngine["result"] as String

            FileWriter(path).use {
                it.write(prettyPrintedJson)
                it.flush()
                it.close()
            }
        }
    }

    companion object {
        val fileMap = Object2ObjectOpenHashMap<String, JSONObject>()
        var counter = 0

        @JvmStatic
        fun getArchive(name: String): JSONObject {
            if (fileMap[name] == null) {
                fileMap[name] = JSONObject()
            }

            return fileMap[name]!!
        }

        fun setArchive(
            name: String,
            data: JSONObject,
        ) {
            fileMap[name] = data
        }

        fun clearDailyEntries() {
            fileMap.keys.toTypedArray().forEach {
                if (it.lowercase().contains("daily")) fileMap[it]?.clear()
            }
        }

        fun clearWeeklyEntries() {
            fileMap.keys.toTypedArray().forEach {
                if (it.lowercase().contains("weekly")) fileMap[it]?.clear()
            }
        }

        @JvmStatic
        fun JSONObject.getInt(
            key: String,
            default: Int = 0,
        ): Int {
            return when (val value = this[key]) {
                is Long -> value.toInt()
                is Double -> value.toInt()
                is Float -> value.toInt()
                is Int -> value
                is Nothing -> default
                else -> default
            }
        }

        @JvmStatic
        fun JSONObject.getString(
            key: String,
            default: String = "nothing",
        ): String {
            return this[key] as? String ?: default
        }

        @JvmStatic
        fun JSONObject.getLong(
            key: String,
            default: Long = 0L,
        ): Long {
            return this[key] as? Long ?: default
        }

        @JvmStatic
        fun JSONObject.getBoolean(
            key: String,
            default: Boolean = false,
        ): Boolean {
            return this[key] as? Boolean ?: default
        }

        fun List<Int>.toJSONArray(): JSONArray {
            val jArray = JSONArray()
            for (i in this) {
                jArray.add(i)
            }
            return jArray
        }

        inline fun <reified T> JSONObject.getList(key: String): List<T> {
            val array = this[key] as? JSONArray ?: JSONArray()
            val list = ObjectArrayList<T>()
            for (element in array) list.add(element as T)
            return list
        }

        fun JSONObject.addToList(
            key: String,
            value: Any,
        ) {
            val array = this.getOrPut(key) { JSONArray() } as JSONArray
            array.add(value)
        }

        fun NPCItemFilename(
            npc: Int,
            item: Int,
            period: String = "daily",
        ): String {
            val itemName = getItemName(item).lowercase().replace(" ", "-")
            val npcName = NPC(npc).name.lowercase()
            return "$period-$npcName-$itemName"
        }

        fun NPCItemMemory(
            npc: Int,
            item: Int,
            period: String = "daily",
        ): JSONObject {
            return getArchive(NPCItemFilename(npc, item, period))
        }

        fun getNPCItemStock(
            npc: Int,
            item: Int,
            limit: Int,
            player: Player,
            period: String = "daily",
        ): Int {
            val itemMemory = NPCItemMemory(npc, item)
            val key = player.name
            var stock = limit - itemMemory.getInt(key)
            stock = maxOf(stock, 0)
            return stock
        }

        fun getNPCItemAmount(
            npc: Int,
            item: Int,
            limit: Int,
            player: Player,
            amount: Int,
            period: String = "daily",
        ): Int {
            val stock = getNPCItemStock(npc, item, limit, player, period)
            var realamount = minOf(amount, stock)
            realamount = maxOf(realamount, 0)
            return realamount
        }

        fun addNPCItemAmount(
            npc: Int,
            item: Int,
            limit: Int,
            player: Player,
            amount: Int,
            period: String = "daily",
        ) {
            val itemMemory = NPCItemMemory(npc, item, period)
            val key = player.name
            var realamount = itemMemory.getInt(key) + amount
            realamount = minOf(realamount, limit)
            realamount = maxOf(realamount, 0)
            itemMemory[key] = realamount
        }
    }
}
