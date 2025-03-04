package core.game.system.command.sets

import com.google.gson.GsonBuilder
import core.cache.Cache
import core.cache.def.impl.*
import core.game.component.ComponentDefinition
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
    override fun defineCommands() {/*
         * Dumps for educational purposes the interface id data.
         */

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
                    IfaceDefinition.forId(interfaceId)
                } catch (e: Exception) {
                    println("Error loading interface ID $interfaceId: ${e.message}")
                    null
                } ?: continue

                try {
                    val ifaceMap = ifaceDef::class.memberProperties.filter { prop ->
                            prop.returnType.classifier !in listOf(
                                IfaceDefinition::class, List::class, Map::class
                            )
                        }.associate { prop ->
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

        /*
         * Dumps for educational purposes identity kit configurations to a .csv file.
         */

        define(
            name = "dumpidk",
            privilege = Privilege.ADMIN,
            usage = "::dumpidk",
            description = "Dumps identity kits data to a .csv file.",
        ) { p, _ ->
            val index = Cache.getIndexes()[2]
            val length = index.getFilesSize(3)

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

                writer.appendLine("$i,${def.bodyPartId},$bodyModelIdsString,${def.isNotSelectable},$headModelIdsString")
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
                val itemMap = itemDef::class.memberProperties.filter { prop ->
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

                        val values = allProperties.map { propName ->
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

                val index = Cache.getIndexes()[2]
                val containers = index.information.containers[26].filesIndexes

                for (fID in containers) {
                    val file = index.getFileData(26, fID)
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
            description = "Dumps data maps configurations to a JSON file.",
        ) { player, _ ->
            try {
                val dump = File("datamaps.json")

                val gson = GsonBuilder().setPrettyPrinting().create()

                val dataMapsList = mutableListOf<Map<String, Any>>()

                val index = Cache.getIndexes()[17]
                val containers = index.information.containersIndexes

                containers.forEach { cID ->
                    val fileIndexes = index.information.containers[cID].filesIndexes
                    fileIndexes.forEach { fID ->
                        val file = index.getFileData(cID, fID)
                        file?.let {
                            val def = DataMap.parse((cID shl 8) or fID, it)

                            val dataMap = mapOf(
                                "id" to def.id,
                                "keyType" to def.keyType,
                                "valueType" to when (def.valueType) {
                                    'K' -> "Normal"
                                    'J' -> "Struct Pointer"
                                    else -> "Unknown"
                                },
                                "defaultString" to (def.defaultString ?: "N/A"),
                                "defaultInt" to def.defaultInt,
                                "dataStore" to def.dataStore,
                            )

                            dataMapsList.add(dataMap)
                        }
                    }
                }

                dump.writeText(gson.toJson(dataMapsList))
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
                GsonBuilder().setPrettyPrinting().disableHtmlEscaping().excludeFieldsWithModifiers(Modifier.TRANSIENT)
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

                        npcMap[prop.name] = when (value) {
                            is String -> {
                                if (prop.name == "combat_audio") {
                                    value
                                } else {
                                    value.contains("Animation [priority=").toString()
                                        .replace("Animation [priority=", "priority=").replace("]", " ")
                                }
                            }

                            is Number, is Boolean -> value
                            is List<*> -> value.map { it.toString() }
                            is Map<*, *> -> value.mapValues { it.value.toString() }
                            is ShortArray -> value.toList().map { it.toInt().toString() }
                            is ByteArray -> value.toList().map { it.toString() }
                            is IntArray -> value.toList().map { it.toInt().toString() }
                            is Array<*> -> when (value) {
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

        /*
         * Dumps the component definitions into a .json file.
         */

        define(
            name = "dumpcomponents",
            privilege = Privilege.ADMIN,
            usage = "::dumpcomponents",
            description = "Dumps component definitions data to a .json file.",
        ) { p, _ ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            val dump = File("component_definitions.json")
            val components = mutableListOf<Map<String, Any?>>()

            for ((id, componentDef) in ComponentDefinition.getDefinitions()) {
                val componentMap = mapOf(
                    "id" to id,
                    "type" to componentDef.type.name,
                    "walkable" to componentDef.isWalkable,
                    "tabIndex" to componentDef.tabIndex,
                    "plugin" to componentDef.plugin?.javaClass?.simpleName,
                )
                components.add(componentMap)
            }

            dump.writeText(gson.toJson(components))
            p.debug("Component data successfully dumped to $dump.")
        }

        /*
         * Dumps the animation definitions into a .json file.
         */

        define(
            name = "dumpanimations",
            privilege = Privilege.ADMIN,
            usage = "::dumpanimations",
            description = "Dumps animation definitions data to a .json file.",
        ) { p, _ ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            val dump = File("animation_definitions.json")
            val animations = mutableListOf<Map<String, Any?>>()

            for ((id, animDef) in AnimationDefinition.getDefinition()) {
                val animationMap = mapOf(
                    "id" to id,
                    "duration" to animDef.getDuration(),
                    "cycles" to animDef.getCycles(),
                    "durationTicks" to animDef.getDurationTicks(),
                    "emoteItem" to animDef.emoteItem,
                    "hasSoundEffect" to animDef.hasSoundEffect,
                    "effect2Sound" to animDef.effect2Sound,
                )
                animations.add(animationMap)
            }

            dump.writeText(gson.toJson(animations))
            p.debug("Animation data has been successfully dumped to $dump.")
        }

        /*
         * Dumps the varbit definitions into a .json file.
         */

        define(
            name = "dumpvarbits",
            privilege = Privilege.ADMIN,
            usage = "::dumpvarbits",
            description = "Dumps varbit definitions data to a .json file.",
        ) { p, _ ->
            val gson = GsonBuilder().setPrettyPrinting().create()
            val dump = File("varbit_definitions.json")
            val varbits = mutableListOf<Map<String, Any?>>()

            for ((id, varbitDef) in VarbitDefinition.getMapping()) {
                val varbitMap = mapOf(
                    "id" to id,
                    "varpId" to varbitDef.varpId,
                    "startBit" to varbitDef.startBit,
                    "endBit" to varbitDef.endBit,
                    "mask" to varbitDef.getMask()
                )
                varbits.add(varbitMap)
            }

            dump.writeText(gson.toJson(varbits))
            p.debug("Varbit data has been successfully dumped to $dump.")
        }

        /*
         * Dumps the CS2Mapping definitions into a .txt file.
         */

        define(
            name = "dumpcs2txt",
            privilege = Privilege.ADMIN,
            usage = "::dumpcs2txt",
            description = "Dumps CS2Mapping data to a .txt file."
        ) { p, _ ->
            val dump = File("cs2_mappings.txt")
            val sb = StringBuilder()

            for (scriptId in 0 until 10000) {
                val mapping = CS2Mapping.forId(scriptId) ?: continue
                val map = mapping.getMap() ?: continue

                sb.append("Script ID: ${mapping.getScriptId()}\n")
                sb.append("Unknown: ${mapping.getUnknown()}\n")
                sb.append("Unknown1: ${mapping.getUnknown1()}\n")
                sb.append("Default String: ${mapping.getDefaultString()}\n")
                sb.append("Default Int: ${mapping.getDefaultInt()}\n")
                sb.append("Map:\n")
                for ((key, value) in map) {
                    sb.append("  Key: $key, Value: $value\n")
                }
                mapping.getArray()?.let { array ->
                    sb.append("Array: ${array.joinToString(", ")}\n")
                }
                sb.append("\n")
            }

            dump.writeText(sb.toString())
            p.debug("CS2Mapping data has been successfully dumped to $dump.")
        }

    }
}
