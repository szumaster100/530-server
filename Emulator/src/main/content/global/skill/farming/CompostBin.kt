package content.global.skill.farming

import core.api.*
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.tools.RandomFunction
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.rs.consts.Items
import org.rs.consts.Sounds
import java.util.concurrent.TimeUnit

class CompostBin(
    val player: Player,
    val bin: CompostBins,
) {
    private var items = ArrayList<Int>()
    var isSuperCompost = true
    var isTomatoes = true
    var isClosed = false
    var finishedTime = 0L
    var isFinished = false

    fun reset() {
        items.clear()
        isSuperCompost = true
        isTomatoes = true
        isClosed = false
        finishedTime = 0L
        isFinished = false
        updateBit()
    }

    fun isFull(): Boolean {
        return items.size == 15
    }

    fun close() {
        isClosed = true
        sendMessage(player, "You close the compost bin.")
        animate(player, 810)
        playAudio(player, Sounds.COMPOST_CLOSE_2428)
        sendMessageWithDelay(player, "The contents have begun to rot.", 1)
        finishedTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(RandomFunction.random(35, 50).toLong())
        updateBit()
    }

    fun open() {
        isClosed = false
        animate(player, 810)
        playAudio(player, Sounds.COMPOST_OPEN_2429)
        sendMessage(player, "You open the compost bin.")
        updateBit()
    }

    fun takeItem(): Item? {
        if (items.isEmpty()) return null
        val item = items[0]
        items.remove(item)
        if (items.isEmpty()) {
            isFinished = false
            finishedTime = 0L
            isTomatoes = true
            isSuperCompost = true
            isClosed = false
        }
        playAudio(player, Sounds.FARMING_SCOOP_2443)
        updateBit()
        if (isSuperCompost) {
            rewardXP(player, Skills.FARMING, 8.5)
        } else {
            rewardXP(player, Skills.FARMING, 4.5)
        }
        return Item(item)
    }

    fun isDefaultState(): Boolean {
        return (isFinished == false && finishedTime == 0L && items.size == 0)
    }

    fun isReady(): Boolean {
        return System.currentTimeMillis() > finishedTime && finishedTime != 0L
    }

    fun checkSuperCompostItem(id: Int): Boolean {
        return when (id) {
            Items.WATERMELON_5982,
            Items.PINEAPPLE_2114,
            Items.CALQUAT_FRUIT_5980,
            Items.OAK_ROOTS_6043,
            Items.WILLOW_ROOTS_6045,
            Items.MAPLE_ROOTS_6047,
            Items.YEW_ROOTS_6049,
            Items.MAGIC_ROOTS_6051,
            Items.COCONUT_5974,
            Items.COCONUT_SHELL_5978,
            Items.PAPAYA_FRUIT_5972,
            Items.JANGERBERRIES_247,
            Items.WHITE_BERRIES_239,
            Items.POISON_IVY_BERRIES_6018,
            Items.CLEAN_TOADFLAX_2998,
            Items.CLEAN_AVANTOE_261,
            Items.CLEAN_KWUARM_263,
            Items.CLEAN_CADANTINE_265,
            Items.CLEAN_DWARF_WEED_267,
            Items.CLEAN_TORSTOL_269,
            Items.CLEAN_LANTADYME_2481,
            Items.CLEAN_SNAPDRAGON_3000,
            Items.GRIMY_TOADFLAX_3049,
            Items.GRIMY_KWUARM_213,
            Items.GRIMY_AVANTOE_211,
            Items.GRIMY_TORSTOL_219,
            Items.GRIMY_DWARF_WEED_217,
            Items.GRIMY_LANTADYME_2485,
            Items.GRIMY_SNAPDRAGON_3051,
            Items.GRIMY_CADANTINE_215,
            -> true

            else -> false
        }
    }

    fun addItem(item: Int) {
        if (!isFull()) {
            items.add(item)
            if (!checkSuperCompostItem(item)) {
                isSuperCompost = false
            }
            if (item != Items.TOMATO_1982) isTomatoes = false
        }
        updateBit()
    }

    fun addItem(item: Item) {
        val remaining = 15 - items.size
        val amount =
            if (item.amount > remaining) {
                remaining
            } else {
                item.amount
            }
        for (i in 0 until amount) {
            playAudio(player, Sounds.FARMING_PUTIN_2441)
            addItem(item.id)
        }
    }

    fun updateBit() {
        if (items.isNotEmpty()) {
            if (isClosed) {
                setVarbit(player, bin.varbit, 0x40)
            } else if (isFinished) {
                var finalValue = if (items.size == 15) 15 else 14
                if (isTomatoes) {
                    finalValue += 0x80
                } else if (isSuperCompost) {
                    finalValue += 0x20
                }
                setVarbit(player, bin.varbit, finalValue)
            } else {
                var finalValue = items.size
                if (isTomatoes) finalValue += 0x80
                setVarbit(player, bin.varbit, finalValue)
            }
        } else {
            setVarbit(player, bin.varbit, 0)
        }
    }

    fun save(root: JSONObject) {
        val binObject = JSONObject()
        binObject["isSuper"] = this.isSuperCompost
        val items = JSONArray()
        for (id in this.items) {
            items.add(id)
        }
        binObject["items"] = items
        binObject["finishTime"] = finishedTime
        binObject["isTomato"] = isTomatoes
        binObject["isClosed"] = isClosed
        binObject["isFinished"] = isFinished
        root["binData"] = binObject
    }

    fun parse(_data: JSONObject) {
        val isSuper = if (_data.containsKey("isSuper")) (_data["isSuper"] as Boolean) else true
        if (_data.containsKey("items")) {
            (_data["items"] as JSONArray).forEach {
                addItem(it.toString().toInt())
            }
        }
        if (_data.containsKey("finishTime")) finishedTime = _data["finishTime"].toString().toLong()
        if (_data.containsKey("isTomato")) isTomatoes = _data["isTomato"] as Boolean
        if (_data.containsKey("isClosed")) isClosed = _data["isClosed"] as Boolean
        if (_data.containsKey("isFinished")) isFinished = _data["isFinished"] as Boolean
        updateBit()
    }

    fun finish() {
        if (isTomatoes) {
            items = items.map { Items.ROTTEN_TOMATO_2518 } as ArrayList<Int>
        } else if (isSuperCompost) {
            items = items.map { Items.SUPERCOMPOST_6034 } as ArrayList<Int>
        } else {
            items = items.map { Items.COMPOST_6032 } as ArrayList<Int>
        }
        isFinished = true
    }

    fun convert() {
        if (!isSuperCompost) {
            items = items.map { Items.SUPERCOMPOST_6034 } as ArrayList<Int>
            isSuperCompost = true
            updateBit()
        }
    }
}
