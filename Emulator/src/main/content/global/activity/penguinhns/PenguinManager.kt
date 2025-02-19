package content.global.activity.penguinhns

import core.ServerStore.Companion.toJSONArray
import core.api.log
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.world.map.Location
import core.tools.Log
import org.json.simple.JSONArray
import org.json.simple.JSONObject

class PenguinManager {
    companion object {
        var penguins: MutableList<Int> = ArrayList()
        var npcs = ArrayList<NPC>()
        val spawner = PenguinSpawner()
        var tagMapping: MutableMap<Int, JSONArray> = HashMap()

        fun registerTag(
            player: Player,
            location: Location,
        ) {
            val ordinal = Penguin.forLocation(location)?.ordinal ?: -1
            val list = tagMapping[ordinal] ?: JSONArray()

            list.add(player.username.lowercase())
            tagMapping[ordinal] = list
            updateStoreFile()
        }

        fun hasTagged(
            player: Player,
            location: Location,
        ): Boolean {
            val ordinal = Penguin.forLocation(location)?.ordinal
            return tagMapping[ordinal]?.contains(player.username.lowercase()) ?: false
        }

        private fun updateStoreFile() {
            val jsonTags = JSONArray()
            tagMapping.filter { it.value.isNotEmpty() }.forEach { (ordinal, taggers) ->
                log(this::class.java, Log.FINE, "$ordinal - ${taggers.first()}")

                val tag = JSONObject()
                tag["ordinal"] = ordinal
                tag["taggers"] = taggers
                jsonTags.add(tag)
            }

            PenguinHNSEvent.getStoreFile()["tag-mapping"] = jsonTags
        }
    }

    fun rebuildVars() {
        if (!PenguinHNSEvent.getStoreFile().containsKey("spawned-penguins")) {
            penguins = spawner.spawnPenguins(10)
            PenguinHNSEvent.getStoreFile()["spawned-penguins"] = penguins.toJSONArray()
            tagMapping.clear()

            for (p in penguins) {
                tagMapping[p] = JSONArray()
            }
            updateStoreFile()
        } else {
            val spawnedOrdinals =
                (PenguinHNSEvent.getStoreFile()["spawned-penguins"] as JSONArray).map {
                    it
                        .toString()
                        .toInt()
                }
            spawner.spawnPenguins(spawnedOrdinals)
            val storedTags =
                (PenguinHNSEvent.getStoreFile()["tag-mapping"] as? JSONArray)
                    ?.associate { jRaw ->
                        val jObj = jRaw as JSONObject
                        jObj["ordinal"].toString().toInt() to (jObj["taggers"] as JSONArray)
                    }?.toMutableMap() ?: HashMap()

            tagMapping = storedTags
            penguins = spawnedOrdinals.toMutableList()
        }
    }
}
