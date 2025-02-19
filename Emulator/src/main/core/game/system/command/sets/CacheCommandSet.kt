package core.game.system.command.sets

import com.google.gson.GsonBuilder
import core.cache.Archives
import core.cache.Cache
import core.cache.Indices
import core.cache.def.impl.*
import core.game.system.command.Privilege
import core.plugin.Initializable
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Modifier
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

@Initializable
class CacheCommandSet : CommandSet(Privilege.ADMIN) {

    override fun defineCommands() {
        /*
         * Dumps for educational purposes the sprite to .png files.
         */

        define(
            name = "dumpsprite",
            privilege = Privilege.ADMIN,
            usage = "::dumpsprite [spriteId] [outputDir]",
            description = "Dumps sprite data to .txt and .png.",
        ) { p, args ->
            /*
             * Unused.
             */
            return@define
        }

        /*
         * Writes sprite data for educational purposes to a .png file.
         */

        define(
            name = "writesprite",
            privilege = Privilege.ADMIN,
            usage = "::writesprite [spriteId] [directory] [name]",
            description = "Writes sprite data to .png.",
        ) { p, args ->
            /*
             * Unused.
             */
            return@define
        }

        /*
         * Dumps for educational purposes the interface id data.
         */

        /*
        define(
            name = "dumpinterfaces",
            privilege = Privilege.ADMIN,
            usage = "::dumpinterfaces",
            description = "Dumps all interface definitions to a .json file."
        ) { p, _ ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            val dump = File("interface_definitions.json")
            val interfaces = mutableListOf<Map<String, Any?>>()

            for (interfaceId in 0 until Cache.getInterfaceDefinitionsSize()) {
                val ifaceDef = try {
                    IfaceDefinition.loadAndParse(interfaceId)
                } catch (e: Exception) {
                    println("Error loading interface ID $interfaceId: ${e.message}")
                    null
                } ?: continue

                try {
                    val ifaceMap = ifaceDef::class
                        .memberProperties
                        .filter { prop ->
                            prop.returnType.classifier !in listOf(
                                IfaceDefinition::class,
                                List::class,
                                Map::class
                            )
                        }
                        .associate { prop ->
                            prop.isAccessible = true
                            prop.name to (prop.getter.call(ifaceDef) ?: "null")
                        }

                    if (ifaceMap.isNotEmpty()) {
                        interfaces.add(ifaceMap)
                    }
                } catch (e: Exception) {
                    println("Error processing interface ID $interfaceId: ${e.message}")
                }
            }

            dump.writeText(gson.toJson(interfaces))
            p.debug("Interface definitions have been successfully dumped to $dump.")
        }
        */

        /*
         * Dumps for educational purposes identity kit configurations to a .csv file.
         */

        define(
            name = "dumpidk",
            privilege = Privilege.ADMIN,
            usage = "::dumpidk",
            description = "Dumps identity kits data to a .csv file.",
        ) { p, _ ->
            val index = Cache.getIndexes()[Indices.CONFIGURATION]
            val length = index.getFilesSize(Archives.IDENTITY_KIT)

            val dump = File("identity_kits.csv")
            val headers = listOf("id", "bodyPartId", "bodyModelIds", "isSelectable", "headModelIds")

            if (dump.exists()) {
                dump.delete()
            }

            val writer = dump.bufferedWriter()
            writer.appendLine(headers.joinToString(","))

            for (i in 0 until length) {
                val def = ClothDefinition.forId(i)

                val bodyModelIdsString = def.bodyModelIds?.joinToString(",") { it.toString() } ?: ""
                val headModelIdsString = def.headModelIds?.joinToString(",") { it.toString() }

                writer.appendLine("${i},${def.bodyPartId},${bodyModelIdsString},${def.isNotSelectable},${headModelIdsString}")
            }

            writer.close()
            p.debug("Identity kits data has been successfully dumped to $dump.")
        }

        /*
         * Dumps for educational purposes item definitions into a .json.
         */

        define(
            name = "dumpitems",
            privilege = Privilege.ADMIN,
            usage = "::dumpitems",
            description = "Dumps item definitions data to a .json file.",
        ) { p, _ ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            val dump = File("item_definitions.json")
            val items = mutableListOf<Map<String, Any?>>()

            for (itemId in 0 until Cache.getItemDefinitionsSize()) {
                val itemDef = ItemDefinition.forId(itemId) ?: continue
                val itemMap =
                    itemDef::class
                        .memberProperties
                        .filter { prop ->
                            prop.returnType.classifier !in listOf(ItemDefinition::class, List::class, Map::class)
                        }.associate { prop ->
                            prop.isAccessible = true
                            try {
                                prop.name to prop.getter.call(itemDef)
                            } catch (e: Exception) {
                                prop.name to "Error"
                            }
                        }

                if (itemMap.isNotEmpty()) {
                    items.add(itemMap)
                }
            }
            dump.writeText(gson.toJson(items))
            p.debug("Item data has been successfully dumped to $dump.")
        }

        /*
         * Dumps for educational purposes CS2 mapping data to a .csv file.
         */

        define(
            name = "dumpcs2",
            privilege = Privilege.ADMIN,
            usage = "::dumpcs2",
            description = "Dumps CS2 mapping data to a .csv file.",
        ) { p, _ ->
            val dump = File("clientscripts.csv")
            val gson = GsonBuilder().disableHtmlEscaping().create()

            try {
                BufferedWriter(FileWriter(dump)).use { writer ->
                    val allProperties = CS2Mapping::class.memberProperties.map { it.name }

                    writer.write(allProperties.joinToString(","))
                    writer.newLine()

                    for (itemId in 0 until 6000) {
                        val itemDef = CS2Mapping.forId(itemId) ?: continue

                        val values =
                            allProperties.map { propName ->
                                val prop = CS2Mapping::class.memberProperties.find { it.name == propName }
                                prop?.let {
                                    it.isAccessible = true
                                    try {
                                        val value = it.getter.call(itemDef)
                                        when (value) {
                                            is Array<*> -> value.joinToString(";") { it.toString() }
                                            is List<*> -> value.joinToString(";") { it.toString() }
                                            is Map<*, *> -> gson.toJson(value)
                                            null -> "null"
                                            else -> value.toString()
                                        }
                                    } catch (e: Exception) {
                                        "Error"
                                    }
                                } ?: "null"
                            }

                        writer.write(values.joinToString(", "))
                        writer.newLine()
                    }
                }

                p.debug("CS2 data has been successfully dumped to $dump.")
            } catch (e: IOException) {
                p.debug("Error writing to CSV file: ${e.message}")
                e.printStackTrace()
            }
        }

        /*
         * Dumps for educational purposes struct data to a .csv file.
         */

        define(
            name = "dumpstructs",
            privilege = Privilege.ADMIN,
            usage = "::dumpstructs",
            description = "Dumps structs data to a .csv file.",
        ) { player, _ ->
            try {
                val dump = File("structs.csv")
                val headers = listOf("id", "data")
                if (dump.exists()) {
                    dump.delete()
                }
                val writer = dump.bufferedWriter()
                writer.appendLine(headers.joinToString(", "))

                val index = Cache.getIndexes()[Indices.CONFIGURATION]
                val containers = index.information.containers[Archives.STRUCT].filesIndexes

                for (fID in containers) {
                    val file = index.getFileData(Archives.STRUCT, fID)
                    if (file != null) {
                        val def = Struct.parse(fID, file)
                        if (def.dataStore.isNotEmpty()) {
                            val structData = def.dataStore.map { it.toString() }.joinToString(",")
                            writer.appendLine("${def.id}, $structData")
                        }
                    }
                }

                writer.close()
                player.debug("Struct data has been successfully dumped to $dump.")
            } catch (e: IOException) {
                e.printStackTrace()
                reject(player, "Error writing to file: ${e.message}")
            }
        }

        /*
         * Dumps for educational purposes data map configurations to a .json file.
         */

        define(
            name = "dumpdatamaps",
            privilege = Privilege.ADMIN,
            usage = "::dumpdatamaps",
            description = "Dumps data maps configurations to a .csv file.",
        ) { player, _ ->
            try {
                val dump = File("datamaps.csv")
                val headers = listOf("id", "keyType", "valueType", "defaultString", "defaultInt", "dataStore")

                if (dump.exists()) {
                    dump.delete()
                }
                val writer = dump.bufferedWriter()
                writer.appendLine(headers.joinToString(","))

                val index = Cache.getIndexes()[Indices.CONFIGURATION_ENUMS]
                val containers = index.information.containersIndexes

                containers.forEach { cID ->
                    val fileIndexes = index.information.containers[cID].filesIndexes
                    fileIndexes.forEach { fID ->
                        val file = index.getFileData(cID, fID)
                        file?.let {
                            val def = DataMap.parse((cID shl 8) or fID, it)

                            val dataMap = mutableListOf<String>()
                            dataMap.add(def.id.toString())
                            dataMap.add(def.keyType.toString())
                            dataMap.add(
                                when (def.valueType) {
                                    'K' -> "Normal"
                                    'J' -> "Struct Pointer"
                                    else -> "Unknown"
                                }
                            )
                            dataMap.add(def.defaultString ?: "N/A")
                            dataMap.add(def.defaultInt.toString())
                            dataMap.add(def.dataStore.toString())
                            writer.appendLine(dataMap.joinToString(", "))
                        }
                    }
                }

                writer.close()
                player.debug("Data maps successfully dumped to $dump.")
            } catch (e: IOException) {
                e.printStackTrace()
                reject(player, "Error writing to file: ${e.message}")
            }
        }

        /*
         * Dumps for educational purposes the NPC definitions into a .json file.
         */

        define(
            name = "dumpnpcs",
            privilege = Privilege.ADMIN,
            usage = "::dumpnpcs",
            description = "Dumps NPC definitions data to a .json file.",
        ) { p, _ ->
            val gson =
                GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .create()

            val dump = File("npc_definitions.json")
            val items = mutableListOf<Map<String, Any?>>()

            val excludedFields = setOf("handlers")

            for (npcId in 0 until Cache.getNPCDefinitionsSize()) {
                val npcDef = NPCDefinition.forId(npcId) ?: continue
                val npcMap = mutableMapOf<String, Any?>()

                for (prop in npcDef::class.memberProperties) {
                    if (prop.name in excludedFields) {
                        continue
                    }

                    prop.isAccessible = true
                    try {
                        val value = prop.getter.call(npcDef)

                        npcMap[prop.name] =
                            when (value) {
                                is String -> {
                                    if (prop.name == "combat_audio") {
                                        value
                                    } else {
                                        value
                                            .contains("Animation [priority=")
                                            .toString()
                                            .replace("Animation [priority=", "priority=")
                                            .replace("]", " ")
                                    }
                                }

                                is Number, is Boolean -> value
                                is List<*> -> value.map { it.toString() }
                                is Map<*, *> -> value.mapValues { it.value.toString() }
                                is ShortArray -> value.toList().map { it.toInt().toString() }
                                is ByteArray -> value.toList().map { it.toString() }
                                is IntArray -> value.toList().map { it.toInt().toString() }
                                is Array<*> ->
                                    when (value) {
                                        is ShortArray -> value.toList().map { it.toInt().toString() }
                                        is ByteArray -> value.toList().map { it.toString() }
                                        is IntArray -> value.toList().map { it.toInt().toString() }
                                        else -> value.toList().joinToString(",") { it.toString() }
                                    }

                                null -> "null"
                                else -> value.toString()
                            }
                    } catch (e: Exception) {
                        p.debug("Err read prop '${prop.name}': ${e.message}")
                        npcMap[prop.name] = "Err: ${e.message}"
                    }
                }

                if (npcMap.isNotEmpty()) {
                    items.add(npcMap)
                }
            }

            try {
                dump.writeText(gson.toJson(items))
                p.debug("NPC data has been successfully dumped to $dump.")
            } catch (e: Exception) {
                p.debug("Error saving JSON file: ${e.message}")
            }
        }
    }
}
