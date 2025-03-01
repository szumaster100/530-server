package core.game.container;

import core.game.node.item.Item;

public final class ContainerEvent {

    public static final Item NULL_ITEM = new Item(0, 0);

    private final Item[] items;

    private boolean clear;

    public ContainerEvent(int size) {
        this.items = new Item[size];
    }

    public void flagNull(int slot) {
        items[slot] = NULL_ITEM;
    }

    public void flag(int slot, Item item) {
        items[slot] = item;
    }

    public int getChangeCount() {
        int count = 0;
        for (Item item : items) {
            if (item != null) {
                count++;
            }
        }
        return count;
    }

    public int[] getSlots() {
        int size = 0;
        int[] slots = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                slots[size++] = i;
            }
        }
        int[] slot = new int[size];
        for (int i = 0; i < size; i++) {
            slot[i] = slots[i];
        }
        return slot;
    }

    public Item[] getItems() {
        return items;
    }

    public void flagEmpty() {
        this.clear = true;
        for (int i = 0; i < items.length; i++) {
            items[i] = null;
        }
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }
}