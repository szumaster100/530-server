package core.game.container;

import com.google.errorprone.annotations.CheckReturnValue;
import core.api.ContainerListener;
import core.cache.def.impl.ItemDefinition;
import core.game.node.entity.player.Player;
import core.game.node.item.GroundItemManager;
import core.game.node.item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rs.consts.Items;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Container {

    private Item[] items;

    private final int capacity;

    private SortType sortType;

    private final ContainerType type;

    private ContainerEvent event;

    private final List<ContainerListener> listeners = new ArrayList<>(20);

    public Container(int capacity) {
        this(capacity, ContainerType.DEFAULT);
    }

    public Container(int capacity, Item... items) {
        this(capacity);
        add(items);
    }

    public Container(int capacity, ContainerType type) {
        this(capacity, type, SortType.ID);
    }

    public Container(int capacity, ContainerType type, SortType sortType) {
        this.capacity = capacity;
        this.type = type;
        this.items = new Item[capacity];
        this.sortType = sortType;
        this.event = new ContainerEvent(capacity);
    }

    public Container register(ContainerListener listener) {
        listeners.add(listener);
        return this;
    }

    public boolean add(Item... items) {
        boolean addedAll = true;
        for (Item item : items) {
            if (item == null) {
                continue;
            }
            if (!add(item, false)) {
                addedAll = false;
                break;
            }
        }
        update();
        return addedAll;
    }

    public void addList(List<Item> items) {
        items.stream().filter(Objects::nonNull).forEach(this::add);
        update();
    }

    public void insert(int fromSlot, int toSlot) {
        insert(fromSlot, toSlot, true);
    }

    public void insert(int fromSlot, int toSlot, boolean update) {
        Item temp = items[fromSlot];
        if (toSlot > fromSlot) {
            for (int i = fromSlot; i < toSlot; i++) {
                replace(get(i + 1), i, false);
            }
        } else if (fromSlot > toSlot) {
            for (int i = fromSlot; i > toSlot; i--) {
                replace(get(i - 1), i, false);
            }
        }
        replace(temp, toSlot, update);
    }

    public boolean add(final Item item, final Player player) {
        if (!add(item, true, -1)) {
            GroundItemManager.create(item, player);
            return false;
        }
        return true;
    }

    public boolean addIfDoesntHave(final Item item) {
        if (containsItem(item)) {
            return false;
        } else {
            return add(item);

        }
    }

    public boolean add(Item item) {
        return add(item, true, -1);
    }

    public boolean add(Item item, boolean fireListener) {
        return add(item, fireListener, -1);
    }

    public boolean add(Item item, boolean fireListener, int preferredSlot) {
        item = item.copy();
        int maximum = getMaximumAdd(item);
        if (maximum == 0) {
            return false;
        }
        if (preferredSlot > -1 && items[preferredSlot] != null) {
            preferredSlot = -1;
        }
        if (item.getAmount() > maximum) {
            item.setAmount(maximum);
        }
        if (type != ContainerType.NEVER_STACK && (item.getDefinition().isStackable() || type == ContainerType.ALWAYS_STACK || type == ContainerType.SHOP)) {
            boolean hashBased = sortType == SortType.HASH;
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    if ((hashBased && items[i].getIdHash() == item.getIdHash()) || (!hashBased && items[i].getId() == item.getId())) {
                        int totalCount = item.getAmount() + items[i].getAmount();
                        items[i] = new Item(items[i].getId(), totalCount, item.getCharge());
                        items[i].setIndex(i);
                        event.flag(i, items[i]);
                        if (fireListener) {
                            update();
                        }
                        return true;
                    }
                }
            }
            int slot = preferredSlot > -1 ? preferredSlot : freeSlot();
            if (slot == -1) {
                return false;
            }
            items[slot] = item;
            item.setIndex(slot);
            event.flag(slot, item);
            if (fireListener) {
                update();
            }
            return true;
        }
        int slots = freeSlots();
        if (slots >= item.getAmount()) {
            for (int i = 0; i < item.getAmount(); i++) {
                int slot = i == 0 && preferredSlot > -1 ? preferredSlot : freeSlot();
                items[slot] = new Item(item.getId(), 1, item.getCharge());
                items[slot].setIndex(slot);
                event.flag(slot, items[slot]);
            }
            if (fireListener) {
                update();
            }
            return true;
        }
        return false;
    }

    @CheckReturnValue
    public boolean remove(Item... items) {
        boolean removedAll = true;
        for (Item item : items) {
            if (!remove(item, false)) {
                removedAll = false;
            }
        }
        update();
        return removedAll;
    }

    @CheckReturnValue
    public boolean remove(Item item) {
        return remove(item, true);
    }

    @CheckReturnValue
    public boolean remove(Item item, boolean fireListener) {
        int slot = getSlot(item);
        if (slot != -1) {
            return remove(item, slot, fireListener);
        }
        return false;
    }

    @CheckReturnValue
    public boolean remove(Item item, int slot, boolean fireListener) {
        if (!contains(item.getId(), item.getAmount()))
            return false;
        Item oldItem = items[slot];
        if (oldItem == null || oldItem.getId() != item.getId()) {
            return false;
        }
        if (item.getAmount() < 1) {
            return true;
        }
        if (oldItem.getDefinition().isStackable() || type.equals(ContainerType.ALWAYS_STACK) || type == ContainerType.SHOP) {
            if (item.getAmount() >= oldItem.getAmount()) {
                items[slot] = null;
                event.flagNull(slot);
                if (fireListener) {
                    update();
                }
                return true;
            }
            items[slot] = new Item(item.getId(), oldItem.getAmount() - item.getAmount(), item.getCharge());
            items[slot].setIndex(slot);
            event.flag(slot, items[slot]);
            if (fireListener) {
                update();
            }
            return true;
        }
        items[slot] = null;
        event.flagNull(slot);
        int removed = 1;
        for (int i = removed; i < item.getAmount(); i++) {
            slot = getSlot(item);
            if (slot != -1) {
                items[slot] = null;
                event.flagNull(slot);
            } else {
                break;
            }
        }
        if (fireListener) {
            update();
        }
        return true;
    }

    public boolean removeAll(int[] ids) {
        boolean removedAll = true;
        for (int id : ids) {
            if (!removeAll(id)) {
                removedAll = false;
            }
        }
        update();
        return removedAll;
    }

    public boolean removeAll(int id) {
        ArrayList<Item> matchingIdItems = new ArrayList<>();
        for (Item item : this.items) {
            if (item != null && item.getId() == id) {
                matchingIdItems.add(item);
            }
        }
        boolean res = true;
        for (Item item : matchingIdItems) {
            if (!remove(item, false)) {
                res = false;
            }
        }
        return res;
    }

    public Item replace(Item item, int slot) {
        return replace(item, slot, true);
    }

    public Item replace(Item item, int slot, boolean fireListener) {
        if (item != null) {
            if (item.getAmount() < 1 && type != ContainerType.SHOP) {
                item = null;
            } else {
                item = item.copy();
            }
        }
        Item oldItem = items[slot];
        items[slot] = item;
        if (item == null) {
            event.flagNull(slot);
        } else {
            item.setIndex(slot);
            event.flag(slot, item);
        }
        if (fireListener) {
            update();
        }
        return oldItem;
    }

    public void update() {
        if (event.getChangeCount() < 1 && !event.isClear()) {
            return;
        }
        for (ContainerListener listener : listeners) {
            listener.update(this, event);
        }
        event.setClear(false);
        event = new ContainerEvent(capacity);
    }

    public void update(boolean force) {
        if (event.getChangeCount() < 1 && !force) {
            return;
        }
        for (ContainerListener listener : listeners) {
            listener.update(this, event);
        }
        event = new ContainerEvent(capacity);
    }

    public void refresh() {
        for (ContainerListener listener : listeners) {
            listener.refresh(this);
        }
        event = new ContainerEvent(capacity);
    }

    public void refresh(ContainerListener listener) {
        listener.refresh(this);
        event = new ContainerEvent(capacity);
    }

    public int getAsId(int slot) {
        if (slot < 0 || slot >= items.length || items[slot] == null) {
            return 0;
        }
        return items[slot].getId();
    }

    public Item get(int slot) {
        if (slot < 0 || slot >= items.length) {
            return null;
        }
        return items[slot];
    }

    public Item getNew(int slot) {
        Item item = items[slot];
        if (item != null) {
            return item;
        }
        return new Item(0);
    }

    public int getId(int slot) {
        if (slot >= items.length) {
            return -1;
        }
        Item item = items[slot];
        if (item != null) {
            return item.getId();
        }
        return -1;
    }

    public int parse(ByteBuffer buffer) {
        int slot;
        int total = 0;
        while ((slot = buffer.getShort()) != -1) {
            int id = buffer.getShort() & 0xFFFF;
            int amount = buffer.getInt();
            int charge = buffer.getInt();
            if (id >= ItemDefinition.getDefinitions().size() || slot >= items.length || slot < 0) {
                continue;
            }
            Item item = items[slot] = new Item(id, amount, charge);
            item.setIndex(slot);
            total += item.getValue();
        }
        return total;
    }

    public void parse(JSONArray itemArray) {
        AtomicInteger total = new AtomicInteger(0);
        itemArray.forEach(item -> {
            JSONObject i = (JSONObject) item;
            int slot = Integer.parseInt(i.get("slot").toString());
            int id = Integer.parseInt(i.get("id").toString());
            int amount = Integer.parseInt(i.get("amount").toString());
            int charge = Integer.parseInt(i.get("charge").toString());
            if (id >= ItemDefinition.getDefinitions().size() || id < 0 || slot >= items.length || id == Items.MAGIC_CARPET_5614) {
            } else {
                Item it = items[slot] = new Item(id, amount, charge);
                it.setIndex(slot);
                total.set(total.get() + (int) it.getValue());
            }
        });
    }

    public long save(ByteBuffer buffer) {
        long totalValue = 0;
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            if (item == null) {
                continue;
            }
            buffer.putShort((short) i);
            buffer.putShort((short) item.getId());
            buffer.putInt(item.getAmount());
            buffer.putInt(item.getCharge());
            totalValue += item.getValue();
        }
        buffer.putShort((short) -1);
        return totalValue;
    }

    public void copy(Container c) {
        items = new Item[c.items.length];
        for (int i = 0; i < items.length; i++) {
            Item it = c.items[i];
            if (it == null) {
                continue;
            }
            items[i] = new Item(it.getId(), it.getAmount(), it.getCharge());
            items[i].setIndex(i);
        }
    }

    public String format() {
        String log = "";
        Map<Integer, Integer> map = new HashMap<>();
        Integer old = null;
        for (Item item : items) {
            if (item != null) {
                old = map.get(item.getId());
                map.put(item.getId(), old == null ? item.getAmount() : old + item.getAmount());

            }
        }
        for (int i : map.keySet()) {
            log += i + "," + map.get(i) + "|";
        }
        if (log.length() > 0 && log.charAt(log.length() - 1) == '|') {
            log = log.substring(0, log.length() - 1);
        }
        return log;
    }

    public boolean containsItem(Item item) {
        return contains(item.getId(), item.getAmount());
    }

    public boolean containsItems(Item... items) {
        for (Item i : items) {
            if (!containsItem(i)) {
                return false;
            }
        }
        return true;
    }

    public boolean contains(int itemId, int amount) {
        int count = 0;
        for (Item item : items) {
            if (item != null && item.getId() == itemId) {
                if ((count += item.getAmount()) >= amount) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsAtLeastOneItem(int itemId) {
        for (Item item : items) {
            if (item != null && item.getId() == itemId && item.getAmount() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAtLeastOneItem(int[] itemIds) {
        for (int id : itemIds) {
            if (getAmount(id) > 0)
                return true;
        }
        return false;
    }

    public boolean containsAtLeastOneItem(Item... items) {
        for (Item item : items) {
            if (containsItem(item)) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(int... itemIds) {
        for (int i : itemIds) {
            if (!containsAtLeastOneItem(i)) {
                return false;
            }
        }
        return true;
    }

    public void addAll(Container container) {
        add(container.items);
    }

    public int getMaximumAdd(Item item) {
        if (type != ContainerType.NEVER_STACK) {
            if (item.getDefinition().isStackable() || type == ContainerType.ALWAYS_STACK || type == ContainerType.SHOP) {
                if (contains(item.getId(), 1)) {
                    return Integer.MAX_VALUE - getAmount(item);
                }
                return freeSlots() > 0 ? Integer.MAX_VALUE : 0;
            }
        }
        return freeSlots();
    }

    public boolean hasSpaceFor(Item item) {
        return item.getAmount() <= getMaximumAdd(item);
    }

    public boolean hasSpaceFor(Item... items) {
        Container c = new Container(28, ContainerType.DEFAULT);
        c.add(items);
        return this.hasSpaceFor(c);
    }

    public boolean hasSpaceFor(Container c) {
        if (c == null) {
            return false;
        }
        Container check = new Container(capacity, type);
        check.addAll(this);
        for (Item item : c.items) {
            if (item != null) {
                if (!check.add(item, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getSlot(Item item) {
        if (item == null) {
            return -1;
        }
        int id = item.getId();
        for (int i = 0; i < items.length; i++) {
            Item it = items[i];
            if (it != null && it.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public int getSlotHash(Item item) {
        if (item == null) {
            return -1;
        }
        int idHash = item.getIdHash();
        for (int i = 0; i < items.length; i++) {
            Item it = items[i];
            if (it != null && it.getIdHash() == idHash) {
                return i;
            }
        }
        return -1;
    }

    public Item getItem(Item item) {
        return get(getSlot(item));
    }

    public Item get(Item item) {
        for (Item i : items) {
            if (i == null) continue;
            if (item.getId() == i.getId()) return i;
        }
        return null;
    }

    public ArrayList<Item> getAll(Item item) {
        ArrayList<Item> ret = new ArrayList<Item>();
        for (Item i : items) {
            if (i == null) continue;
            if (item.getId() == i.getId()) ret.add(i);
        }
        return ret;
    }

    public int freeSlot() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int getAddSlot(Item item) {
        if (type != ContainerType.NEVER_STACK && (item.getDefinition().isStackable() || type.equals(ContainerType.ALWAYS_STACK) || type == ContainerType.SHOP)) {
            boolean hashBased = sortType == SortType.HASH;
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null) {
                    if ((hashBased && items[i].getIdHash() == item.getIdHash()) || (!hashBased && items[i].getId() == item.getId())) {
                        return i;
                    }
                }
            }
        }
        return freeSlot();
    }

    public int freeSlots() {
        return capacity - itemCount();
    }

    public int itemCount() {
        int size = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                size++;
            }
        }
        return size;
    }

    public boolean containItems(int... itemIds) {
        for (int i = 0; i < itemIds.length; i++) {
            if (!contains(itemIds[i], 1)) {
                return false;
            }
        }
        return true;
    }

    public int getAmount(Item item) {
        if (item == null) {
            return 0;
        }
        int count = 0;
        for (Item i : items) {
            if (i != null && i.getId() == item.getId()) {
                count += i.getAmount();
            }
        }
        return count;
    }

    public int getAmount(int id) {
        return getAmount(new Item(id));
    }

    public void shift() {
        final Item[] itemss = items;
        clear(false);
        for (Item item : itemss) {
            if (item == null) {
                continue;
            }
            add(item, false);
        }
        refresh();
    }

    public boolean isEmpty() {
        for (Item item : items) {
            if (item != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFull() {
        return freeSlots() < 1;
    }

    public void clear() {
        clear(true);
    }

    public void clear(boolean update) {
        items = new Item[capacity];
        event.flagEmpty();
        if (update) {
            refresh();
        }
    }

    public int getWealth() {
        int wealth = 0;
        for (Item i : items) {
            if (i == null) {
                continue;
            }
            wealth += i.getDefinition().getValue() * i.getAmount();
        }
        return wealth;
    }

    public Item[] toArray() {
        return items;
    }

    public List<ContainerListener> getListeners() {
        return listeners;
    }

    public int capacity() {
        return capacity;
    }

    public ContainerEvent getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "Container{" +
            "items=" + Arrays.toString(items) +
            ", capacity=" + capacity +
            ", sortType=" + sortType +
            ", type=" + type +
            ", event=" + event +
            ", listeners=" + listeners +
            '}';
    }
}
