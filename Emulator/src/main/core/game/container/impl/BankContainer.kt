package core.game.container.impl

import core.ServerConfig
import core.api.*
import core.game.component.Component
import core.game.container.Container
import core.game.container.ContainerEvent
import core.game.container.ContainerType
import core.game.container.SortType
import core.game.container.access.InterfaceContainer
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.IronmanMode
import core.game.node.item.Item
import core.game.system.config.ItemConfigParser
import core.game.world.GameWorld.settings
import core.net.packet.PacketRepository
import core.net.packet.context.ContainerContext
import core.net.packet.out.ContainerPacket
import org.rs.consts.Vars
import java.nio.ByteBuffer

/**
 * Represents the bank container.
 *
 * @author Emperor
 */
class BankContainer(player: Player) : Container(SIZE, ContainerType.ALWAYS_STACK, SortType.HASH) {
    /**
     * The player reference.
     */
    private val player: Player

    /**
     * The bank listener.
     */
    private var listener: BankListener? = null

    /**
     * Checks if the bank is opened.
     *
     * @return `True` if so.
     */
    /**
     * If the bank is open.
     */
    var isOpen: Boolean = false
        private set

    /**
     * Gets the last x-amount.
     *
     * @return The last x-amount.
     */
    /**
     * The last x-amount entered.
     */
    var lastAmountX: Int = 50
        private set

    /**
     * The current tab index.
     */
    private var tabIndex = 10

    /**
     * Gets the tabStartSlot value.
     *
     * @return The tabStartSlot.
     */
    /**
     * The tab start indexes.
     */
    val tabStartSlot: IntArray = IntArray(TAB_SIZE)

    /**
     * Construct a new `BankContainer` `Object`.
     *
     * @param player The player reference.
     */
    init {
        super.register((BankListener(player).also { listener = it }) as ContainerListener)
        this.player = player
    }

    /**
     * Method used to open the deposit box.
     */
    fun openDepositBox() {
        player.interfaceManager.open(Component(11)).setCloseEvent { player: Player, c: Component? ->
            player.interfaceManager.openDefaultTabs()
            true
        }
        player.interfaceManager.removeTabs(0, 1, 2, 3, 4, 5, 6)
        refreshDepositBoxInterface()
    }

    /**
     * Invalidates the visual state of deposit box interface
     * forcing the client to re-draw the items
     */
    fun refreshDepositBoxInterface() {
        InterfaceContainer.generateItems(
            player,
            player.inventory.toArray(),
            arrayOf(
                "Examine",
                "Deposit-X",
                "Deposit-All",
                "Deposit-10",
                "Deposit-5",
                "Deposit-1"
            ), 11, 15, 5, 7
        )
    }

    /**
     * Open the bank.
     */
    fun open() {
        if (isOpen) {
            return
        }
        if (player.ironmanManager.checkRestriction(IronmanMode.ULTIMATE)) {
            return
        }
        if (!player.bankPinManager.isUnlocked && !settings!!.isDevMode) {
            player.bankPinManager.openType(1)
            return
        }
        player.interfaceManager.openComponent(762).setCloseEvent { player: Player?, c: Component? ->
            this@BankContainer.close()
            true
        }
        player.interfaceManager.openSingleTab(Component(763))
        super.refresh()
        player.inventory.listeners.add(listener)
        player.inventory.refresh()
        setVarp(player, 1249, lastAmountX)
        val settings = IfaceSettingsBuilder().enableOptions(IntRange(0, 5)).enableExamine().enableSlotSwitch().build()
        player.packetDispatch.sendIfaceSettings(settings, 0, 763, 0, 27)
        player.packetDispatch.sendRunScript(1451, "")
        isOpen = true
        setTabConfigurations()
        sendBankSpace()
    }

    fun open(player: Player) {
        if (isOpen) {
            return
        }
        if (player.ironmanManager.checkRestriction(IronmanMode.ULTIMATE)) {
            return
        }
        if (!player.bankPinManager.isUnlocked && !settings!!.isDevMode) {
            player.bankPinManager.openType(1)
            return
        }
        player.interfaceManager.openComponent(762).setCloseEvent { player1: Player?, c: Component? ->
            this@BankContainer.close()
            true
        }
        refresh(listener)
        player.interfaceManager.openSingleTab(Component(763))
        player.inventory.listeners.add(player.bank.listener)
        player.inventory.refresh()
        setVarp(player, 1249, lastAmountX)
        player.packetDispatch.sendIfaceSettings(1278, 73, 762, 0, SIZE)
        val settings = IfaceSettingsBuilder().enableOptions(IntRange(0, 5)).enableExamine().enableSlotSwitch().build()
        player.packetDispatch.sendIfaceSettings(settings, 0, 763, 0, 27)
        player.packetDispatch.sendRunScript(1451, "")
        isOpen = true
        this.player.bank.setTabConfigurations(player)
    }

    override fun save(buffer: ByteBuffer): Long {
        buffer.putInt(lastAmountX)
        buffer.put(tabStartSlot.size.toByte())
        for (j in tabStartSlot) {
            buffer.putShort(j.toShort())
        }
        return super.save(buffer)
    }

    override fun parse(buffer: ByteBuffer): Int {
        lastAmountX = buffer.getInt()
        val length = buffer.get().toInt() and 0xFF
        for (i in 0 until length) {
            tabStartSlot[i] = buffer.getShort().toInt()
        }
        return super.parse(buffer)
    }

    /**
     * Closes the bank.
     */
    fun close() {
        isOpen = false
        player.inventory.listeners.remove(listener)
        player.interfaceManager.closeSingleTab()
        player.removeAttribute("search")
        player.packetDispatch.sendRunScript(571, "")
    }

    /**
     * Adds an item to the bank container.
     *
     * @param slot   The item slot.
     * @param amount The amount.
     */
    fun addItem(slot: Int, amount: Int) {
        var amount = amount
        if (slot < 0 || slot > player.inventory.capacity() || amount < 1) {
            return
        }
        var item = player.inventory[slot] ?: return

        if (!item.definition.getConfiguration(ItemConfigParser.BANKABLE, true)) {
            player.sendMessage("A magical force prevents you from banking this item")
            return
        }

        val maximum = player.inventory.getAmount(item)
        if (amount > maximum) {
            amount = maximum
        }

        item = Item(item.id, amount, item.charge)
        val unnote = !item.definition.isUnnoted

        var add = if (unnote) Item(item.definition.noteId, amount, item.charge) else item
        if (unnote && !add.definition.isUnnoted) {
            add = item
        }

        val maxCount = super.getMaximumAdd(add)
        if (amount > maxCount) {
            add.amount = maxCount
            item.amount = maxCount
            if (maxCount < 1) {
                player.packetDispatch.sendMessage("There is not enough space left in your bank.")
                return
            }
        }

        if (player.inventory.remove(item, slot, true)) {
            var preferredSlot = -1
            if (tabIndex != 0 && tabIndex != 10 && !super.contains(add.id, 1)) {
                preferredSlot = tabStartSlot[tabIndex] + getItemsInTab(tabIndex)
                insert(freeSlot(), preferredSlot, false)
                increaseTabStartSlots(tabIndex)
            }
            super.add(add, true, preferredSlot)
            setTabConfigurations()
        }
    }

    /**
     * Re-opens the bank interface.
     */
    fun reopen() {
        if (!isOpen) {
            return
        }
        player.interfaceManager.close()
        open()
        refresh()
    }

    /**
     * Takes an item from the bank container and adds one to the inventory
     * container.
     *
     * @param slot   The slot.
     * @param amount The amount.
     */
    fun takeItem(slot: Int, amount: Int) {
        var amount = amount
        if (slot < 0 || slot > super.capacity() || amount <= 0) {
            return
        }
        var item = get(slot) ?: return
        if (amount > item.amount) {
            amount = item.amount // It always stacks in the bank.
        }
        item = Item(item.id, amount, item.charge)
        val noteId = item.definition.noteId
        var add = if (isNoteItems && noteId > 0) Item(noteId, amount, item.charge) else item
        val maxCount = player.inventory.getMaximumAdd(add)
        if (amount > maxCount) {
            item.amount = maxCount
            add.amount = maxCount
            if (maxCount < 1) {
                player.packetDispatch.sendMessage("Not enough space in your inventory.")
                return
            }
        }
        if (isNoteItems && noteId < 0) {
            player.packetDispatch.sendMessage("This item can't be withdrawn as a note.")
            add = item
        }
        if (super.remove(item, slot, false)) {
            player.inventory.add(add)
        }
        val tabId = getTabByItemSlot(slot)
        if (get(slot) == null) {
            decreaseTabStartSlots(tabId)
        }
        setTabConfigurations()
        shift()
        if (player.getAttribute("search", false)) {
            reopen()
        }
    }

    /**
     * Updates the last x-amount entered.
     *
     * @param amount The amount to set.
     */
    fun updateLastAmountX(amount: Int) {
        this.lastAmountX = amount
        setVarp(player, 1249, amount)
    }

    /**
     * Gets the tab the item slot is in.
     *
     * @param itemSlot The item slot.
     * @return The tab index.
     */
    fun getTabByItemSlot(itemSlot: Int): Int {
        var tabId = 0
        for (i in tabStartSlot.indices) {
            if (itemSlot >= tabStartSlot[i]) {
                tabId = i
            }
        }
        return tabId
    }

    /**
     * Increases a tab's start slot.
     *
     * @param startId The start id.
     */
    fun increaseTabStartSlots(startId: Int) {
        for (i in startId + 1 until tabStartSlot.size) {
            tabStartSlot[i]++
        }
    }

    /**
     * Decreases a tab's start slot.
     *
     * @param startId The start id.
     */
    fun decreaseTabStartSlots(startId: Int) {
        if (startId == 10) {
            return
        }
        for (i in startId + 1 until tabStartSlot.size) {
            tabStartSlot[i]--
        }
        if (getItemsInTab(startId) == 0) {
            collapseTab(startId)
        }
    }

    /**
     * Sends the bank space values on the interface.
     */
    fun sendBankSpace() {
        player.packetDispatch.sendString((capacity() - freeSlots()).toString(), 762, 97)
        player.packetDispatch.sendString(capacity().toString(), 762, 98)
    }

    /**
     * Collapses a tab.
     *
     * @param tabId The tab index.
     */
    fun collapseTab(tabId: Int) {
        val size = getItemsInTab(tabId)
        val tempTabItems = arrayOfNulls<Item>(size)
        for (i in 0 until size) {
            tempTabItems[i] = get(tabStartSlot[tabId] + i)
            replace(null, tabStartSlot[tabId] + i, false)
        }
        shift()
        for (i in tabId until tabStartSlot.size - 1) {
            tabStartSlot[i] = tabStartSlot[i + 1] - size
        }
        tabStartSlot[10] = tabStartSlot[10] - size
        for (i in 0 until size) {
            val slot = freeSlot()
            replace(tempTabItems[i], slot, false)
        }
        refresh() //We only refresh once.
        setTabConfigurations()
    }

    /**
     * Sets the tab configs.
     */
    fun setTabConfigurations() {
        var value = getItemsInTab(1)
        value += getItemsInTab(2) shl 10
        value += getItemsInTab(3) shl 20
        setVarp(player, 1246, value)
        value = getItemsInTab(4)
        value += getItemsInTab(5) shl 10
        value += getItemsInTab(6) shl 20
        setVarp(player, 1247, value)
        value = -2013265920
        value += (134217728 * (if (tabIndex == 10) 0 else tabIndex))
        value += getItemsInTab(7)
        value += getItemsInTab(8) shl 10
        setVarp(player, 1248, value)
    }

    /**
     * Sets the tab configs.
     */
    fun setTabConfigurations(player: Player) {
        var value = getItemsInTab(1)
        value += getItemsInTab(2) shl 10
        value += getItemsInTab(3) shl 20
        setVarp(player, 1246, value)
        value = getItemsInTab(4)
        value += getItemsInTab(5) shl 10
        value += getItemsInTab(6) shl 20
        setVarp(player, 1247, value)
        value = -2013265920
        value += (134217728 * (if (tabIndex == 10) 0 else tabIndex))
        value += getItemsInTab(7)
        value += getItemsInTab(8) shl 10
        setVarp(player, 1248, value)
    }

    /**
     * Gets the amount of items in one tab.
     *
     * @param tabId The tab index.
     * @return The amount of items in this tab.
     */
    fun getItemsInTab(tabId: Int): Int {
        return tabStartSlot[tabId + 1] - tabStartSlot[tabId]
    }

    /**
     * Checks if the item can be added.
     *
     * @param item the item.
     * @return `True` if so.
     */
    fun canAdd(item: Item): Boolean {
        return item.definition.getConfiguration(ItemConfigParser.BANKABLE, true)
    }

    var isNoteItems: Boolean
        /**
         * If items have to be noted.
         *
         * @return If items have to be noted `true`.
         */
        get() = getVarbit(player, Vars.VARBIT_IFACE_BANK_NOTE_MODE_7001) == 1
        /**
         * Set if items have to be noted.
         *
         * @param noteItems If items have to be noted `true`.
         */
        set(noteItems) {
            setVarbit(player, Vars.VARBIT_IFACE_BANK_NOTE_MODE_7001, if (noteItems) 1 else 0, true)
        }

    /**
     * Gets the tabIndex value.
     *
     * @return The tabIndex.
     */
    fun getTabIndex(): Int {
        return tabIndex
    }

    /**
     * Sets the tabIndex value.
     *
     * @param tabIndex The tabIndex to set.
     */
    fun setTabIndex(tabIndex: Int) {
        this.tabIndex = tabIndex

        /*
         * Kludge to update the interface
         * after dumping all to prevent
         * "invisible" items in slots.
         */
        update(true)
    }

    var isInsertItems: Boolean
        /**
         * Gets the insert items value.
         *
         * @return `True` if inserting items mode is enabled.
         */
        get() = getVarbit(player, Vars.VARBIT_IFACE_BANK_INSERT_MODE_7000) == 1
        /**
         * Sets the insert items value.
         *
         * @param insertItems The insert items value.
         */
        set(insertItems) {
            setVarbit(player, Vars.VARBIT_IFACE_BANK_INSERT_MODE_7000, if (insertItems) 1 else 0, true)
        }

    /**
     * Listens to the bank container.
     *
     * @author Emperor
     */
    private class BankListener
    /**
     * Construct a new `BankListener` `Object`.
     *
     * @param player The player reference.
     */(
        /**
         * The player reference.
         */
        private val player: Player
    ) : ContainerListener {
        override fun update(container: Container?, event: ContainerEvent?) {
            if (container is BankContainer) {
                PacketRepository.send(
                    ContainerPacket::class.java,
                    ContainerContext(player, 762, 64000, 95, event!!.items, false, *event.slots)
                )
            } else {
                PacketRepository.send(
                    ContainerPacket::class.java,
                    ContainerContext(player, 763, 64000, 93, event!!.items, false, *event.slots)
                )
            }
            player.bank.setTabConfigurations()
            player.bank.sendBankSpace()
        }

        override fun refresh(c: Container?) {
            if (c is BankContainer) {
                PacketRepository.send(
                    ContainerPacket::class.java,
                    ContainerContext(player, 762, 64000, 95, c.toArray(), c.capacity(), false)
                )
            } else {
                PacketRepository.send(
                    ContainerPacket::class.java,
                    ContainerContext(player, 763, 64000, 93, c!!.toArray(), 28, false)
                )
            }
            player.bank.setTabConfigurations()
            player.bank.sendBankSpace()
        }
    }

    companion object {
        /**
         * The bank container size.
         */
        val SIZE: Int = ServerConfig.BANK_SIZE

        /**
         * The maximum amount of bank tabs
         */
        const val TAB_SIZE: Int = 11

        /**
         * Gets the array index for a tab.
         *
         * @param tabId The tab id.
         * @return The array index.
         */
        fun getArrayIndex(tabId: Int): Int {
            if (tabId == 41 || tabId == 74) {
                return 10
            }
            var base = 39
            for (i in 1..9) {
                if (tabId == base) {
                    return i
                }
                base -= 2
            }
            return -1
        }
    }
}
