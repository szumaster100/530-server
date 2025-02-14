package core.game.container.impl;

import core.ServerConfig;
import core.api.ContainerListener;
import core.game.component.Component;
import core.game.container.*;
import core.game.container.access.InterfaceContainer;
import core.api.IfaceSettingsBuilder;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.IronmanMode;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
import core.game.world.GameWorld;
import core.net.packet.PacketRepository;
import core.net.packet.context.ContainerContext;
import core.net.packet.out.ContainerPacket;
import kotlin.ranges.IntRange;
import org.rs.consts.Vars;

import java.nio.ByteBuffer;

import static core.api.ContentAPIKt.*;

public final class BankContainer extends Container {

    public static final int SIZE = ServerConfig.BANK_SIZE;

    public static final int TAB_SIZE = 11;

    private Player player;

    private final BankListener listener;

    private boolean open;

    private int lastAmountX = 50;

    private int tabIndex = 10;

    private final int[] tabStartSlot = new int[TAB_SIZE];

    public BankContainer(Player player) {
        super(SIZE, ContainerType.ALWAYS_STACK, SortType.HASH);
        super.register(listener = new BankListener(player));
        this.player = player;
    }

    public void openDepositBox() {
        player.getInterfaceManager().open(new Component(11)).setCloseEvent((player, c) -> {
            player.getInterfaceManager().openDefaultTabs();
            return true;
        });
        player.getInterfaceManager().removeTabs(0, 1, 2, 3, 4, 5, 6);
        refreshDepositBoxInterface();
    }

    public void refreshDepositBoxInterface() {
        InterfaceContainer.generateItems(
            player,
            player.getInventory().toArray(),
            new String[]{
                "Examine",
                "Deposit-X",
                "Deposit-All",
                "Deposit-10",
                "Deposit-5",
                "Deposit-1"
            }, 11, 15, 5, 7
        );
    }

    public void open() {
        if (open) {
            return;
        }
        if (player.getIronmanManager().checkRestriction(IronmanMode.ULTIMATE)) {
            return;
        }
        if (!player.getBankPinManager().isUnlocked() && !GameWorld.getSettings().isDevMode()) {
            player.getBankPinManager().openType(1);
            return;
        }
        player.getInterfaceManager().openComponent(762).setCloseEvent((player, c) -> {
            BankContainer.this.close();
            return true;
        });
        player.getInterfaceManager().openSingleTab(new Component(763));
        super.refresh();
        player.getInventory().getListeners().add(listener);
        player.getInventory().refresh();
        setVarp(player, 1249, lastAmountX);
        int settings = new IfaceSettingsBuilder().enableOptions(new IntRange(0, 5)).enableExamine().enableSlotSwitch().build();
        player.getPacketDispatch().sendIfaceSettings(settings, 0, 763, 0, 27);
        open = true;
    }

    public void open(Player player) {
        if (open) {
            return;
        }
        if (player.getIronmanManager().checkRestriction(IronmanMode.ULTIMATE)) {
            return;
        }
        if (!player.getBankPinManager().isUnlocked() && !GameWorld.getSettings().isDevMode()) {
            player.getBankPinManager().openType(1);
            return;
        }
        player.getInterfaceManager().openComponent(762).setCloseEvent((player1, c) -> {
            BankContainer.this.close();
            return true;
        });
        refresh(listener);
        player.getInterfaceManager().openSingleTab(new Component(763));
        player.getInventory().getListeners().add(player.getBank().listener);
        player.getInventory().refresh();
        setVarp(player, 1249, lastAmountX);
        player.getPacketDispatch().sendIfaceSettings(1278, 73, 762, 0, SIZE);
        int settings = new IfaceSettingsBuilder().enableOptions(new IntRange(0, 5)).enableExamine().enableSlotSwitch().build();
        player.getPacketDispatch().sendIfaceSettings(settings, 0, 763, 0, 27);
        player.getPacketDispatch().sendRunScript(1451, "");
        open = true;

    }

    @Override
    public long save(ByteBuffer buffer) {
        buffer.putInt(lastAmountX);
        buffer.put((byte) tabStartSlot.length);
        for (int j : tabStartSlot) {
            buffer.putShort((short) j);
        }
        return super.save(buffer);
    }

    @Override
    public int parse(ByteBuffer buffer) {
        lastAmountX = buffer.getInt();
        int length = buffer.get() & 0xFF;
        for (int i = 0; i < length; i++) {
            tabStartSlot[i] = buffer.getShort();
        }
        return super.parse(buffer);
    }

    public void close() {
        open = false;
        player.getInventory().getListeners().remove(listener);
        player.getInterfaceManager().closeSingleTab();
        player.removeAttribute("search");
        player.getPacketDispatch().sendRunScript(571, "");
    }

    public void addItem(int slot, int amount) {
        if (slot < 0 || slot > player.getInventory().capacity() || amount < 1) {
            return;
        }
        Item item = player.getInventory().get(slot);
        if (item == null) {
            return;
        }

        if (!item.getDefinition().getConfiguration(ItemConfigParser.BANKABLE, true)) {
            player.sendMessage("A magical force prevents you from banking this item");
            return;
        }

        int maximum = player.getInventory().getAmount(item);
        if (amount > maximum) {
            amount = maximum;
        }

        item = new Item(item.getId(), amount, item.getCharge());
        boolean unnote = !item.getDefinition().isUnnoted();

        Item add = unnote ? new Item(item.getDefinition().getNoteId(), amount, item.getCharge()) : item;
        if (unnote && !add.getDefinition().isUnnoted()) {
            add = item;
        }

        int maxCount = super.getMaximumAdd(add);
        if (amount > maxCount) {
            add.setAmount(maxCount);
            item.setAmount(maxCount);
            if (maxCount < 1) {
                player.getPacketDispatch().sendMessage("There is not enough space left in your bank.");
                return;
            }
        }

        if (player.getInventory().remove(item, slot, true)) {
            int preferredSlot = -1;
            if (tabIndex != 0 && tabIndex != 10 && !super.contains(add.getId(), 1)) {
                preferredSlot = tabStartSlot[tabIndex] + getItemsInTab(tabIndex);
                insert(freeSlot(), preferredSlot, false);
                increaseTabStartSlots(tabIndex);
            }
            super.add(add, true, preferredSlot);
        }
    }

    public void takeItem(int slot, int amount) {
        if (slot < 0 || slot > super.capacity() || amount <= 0) {
            return;
        }
        Item item = get(slot);
        if (item == null) {
            return;
        }
        if (amount > item.getAmount()) {
            amount = item.getAmount();
        }
        item = new Item(item.getId(), amount, item.getCharge());
        int noteId = item.getDefinition().getNoteId();
        Item add = isNoteItems() && noteId > 0 ? new Item(noteId, amount, item.getCharge()) : item;
        int maxCount = player.getInventory().getMaximumAdd(add);
        if (amount > maxCount) {
            item.setAmount(maxCount);
            add.setAmount(maxCount);
            if (maxCount < 1) {
                player.getPacketDispatch().sendMessage("Not enough space in your inventory.");
                return;
            }
        }
        if (isNoteItems() && noteId < 0) {
            player.getPacketDispatch().sendMessage("This item can't be withdrawn as a note.");
            add = item;
        }
        if (super.remove(item, slot, false)) {
            player.getInventory().add(add);
        }
        if (get(slot) == null) {
            int tabId = getTabByItemSlot(slot);
            decreaseTabStartSlots(tabId);
            shift();
        } else update();
    }

    public void updateLastAmountX(int amount) {
        this.lastAmountX = amount;
        setVarp(player, 1249, amount);
    }

    public int getTabByItemSlot(int itemSlot) {
        int tabId = 0;
        for (int i = 0; i < tabStartSlot.length; i++) {
            if (itemSlot >= tabStartSlot[i]) {
                tabId = i;
            }
        }
        return tabId;
    }

    public void increaseTabStartSlots(int startId) {
        for (int i = startId + 1; i < tabStartSlot.length; i++) {
            tabStartSlot[i]++;
        }
    }

    public void decreaseTabStartSlots(int startId) {
        if (startId == 10) {
            return;
        }
        for (int i = startId + 1; i < tabStartSlot.length; i++) {
            tabStartSlot[i]--;
        }
        if (getItemsInTab(startId) == 0) {
            collapseTab(startId);
        }
    }

    public static int getArrayIndex(int tabId) {
        if (tabId == 41 || tabId == 74) {
            return 10;
        }
        int base = 39;
        for (int i = 1; i < 10; i++) {
            if (tabId == base) {
                return i;
            }
            base -= 2;
        }
        return -1;
    }

    public void sendBankSpace() {
        setVarc(player, 192, capacity() - freeSlots());
    }

    public void collapseTab(int tabId) {
        int size = getItemsInTab(tabId);
        Item[] tempTabItems = new Item[size];
        for (int i = 0; i < size; i++) {
            tempTabItems[i] = get(tabStartSlot[tabId] + i);
            replace(null, tabStartSlot[tabId] + i, false);
        }
        shift();
        for (int i = tabId; i < tabStartSlot.length - 1; i++) {
            tabStartSlot[i] = tabStartSlot[i + 1] - size;
        }
        tabStartSlot[10] = tabStartSlot[10] - size;
        for (int i = 0; i < size; i++) {
            int slot = freeSlot();
            replace(tempTabItems[i], slot, false);
        }
        refresh();
    }

    public void setTabConfigurations() {
        for (int i = 0; i < 8; i++) {
            setVarbit(player, 4885 + i, getItemsInTab(i + 1));
        }
    }

    public int getItemsInTab(int tabId) {
        return tabStartSlot[tabId + 1] - tabStartSlot[tabId];
    }

    public boolean canAdd(Item item) {
        return item.getDefinition().getConfiguration(ItemConfigParser.BANKABLE, true);
    }

    public int getLastAmountX() {
        return lastAmountX;
    }

    public boolean isNoteItems() {
        return getVarbit(player, Vars.VARBIT_IFACE_BANK_NOTE_MODE_7001) == 1;
    }

    public void setNoteItems(boolean noteItems) {
        setVarbit(player, Vars.VARBIT_IFACE_BANK_NOTE_MODE_7001, noteItems ? 1 : 0);
    }

    public int[] getTabStartSlot() {
        return tabStartSlot;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex == 0 ? 10 : tabIndex;
        setVarbit(player, 4893, tabIndex + 1);
        setAttribute(player, "bank:lasttab", tabIndex);
    }

    public void setInsertItems(boolean insertItems) {
        setVarbit(player, Vars.VARBIT_IFACE_BANK_INSERT_MODE_7000, insertItems ? 1 : 0);
    }

    public boolean isInsertItems() {
        return getVarbit(player, Vars.VARBIT_IFACE_BANK_INSERT_MODE_7000) == 1;
    }

    public boolean isOpen() {
        return open;
    }

    private static class BankListener implements ContainerListener {

        private Player player;

        public BankListener(Player player) {
            this.player = player;
        }

        @Override
        public void update(Container c, ContainerEvent event) {
            if (c instanceof BankContainer) {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, 762, 64000, 95, event.getItems(), false, event.getSlots()));
            } else {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, 763, 64000, 93, event.getItems(), false, event.getSlots()));
            }
            player.getBank().setTabConfigurations();
            player.getBank().sendBankSpace();
        }

        @Override
        public void refresh(Container c) {
            if (c instanceof BankContainer) {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, 762, 64000, 95, c.toArray(), c.capacity(), false));
            } else {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, 763, 64000, 93, c.toArray(), 28, false));
            }
            player.getBank().setTabConfigurations();
            player.getBank().sendBankSpace();
        }
    }
}
