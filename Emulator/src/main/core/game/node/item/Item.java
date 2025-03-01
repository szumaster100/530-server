package core.game.node.item;

import core.cache.def.impl.ItemDefinition;
import core.game.interaction.DestinationFlag;
import core.game.interaction.InteractPlugin;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.combat.equipment.DegradableEquipment;

public class Item extends Node {

    private int idHash;

    private int amount;

    private ItemDefinition definition;

    public Item() {
        super("null", null);
        super.interactPlugin = new InteractPlugin(this);
        this.idHash = -1 << 16 | 1000;
    }

    public Item(int id) {
        this(id, 1, 1000);
    }

    public Item(int id, int amount) {
        this(id, amount, 1000);
    }

    public Item(int id, int amount, int charge) {
        super(ItemDefinition.forId(id).getName(), null);
        super.destinationFlag = DestinationFlag.ITEM;
        super.index = -1; // Item slot.
        super.interactPlugin = new InteractPlugin(this);
        this.idHash = id << 16 | charge;
        this.amount = amount;
        this.definition = ItemDefinition.forId(id);
    }

    public Item getDropItem() {
        int itemId = DegradableEquipment.getDropReplacement(getId());
        if (itemId != getId()) {
            return new Item(itemId, getAmount());
        }
        return this;
    }

    public OptionHandler getOperateHandler() {
        return ItemDefinition.getOptionHandler(getId(), "operate");
    }

    public long getValue() {
        long value = 1;
        if (definition.getValue() > value) {
            value = definition.getValue();
        }
        if (definition.getAlchemyValue(true) > value) {
            value = definition.getAlchemyValue(true);
        }
        return value * getAmount();
    }

    public long getAlchemyValue() {
        long value = 1;
        if (definition.getAlchemyValue(true) > value) {
            value = definition.getAlchemyValue(true);
        }
        return value * getAmount();
    }

    public Item copy() {
        return new Item(getId(), getAmount(), getCharge());
    }

    public int getNoteChange() {
        int noteId = definition.getNoteId();
        if (noteId > -1) {
            return noteId;
        }
        return getId();
    }

    public int getId() {
        return idHash >> 16 & 0xFFFF;
    }

    public void setId(int id) {
        this.idHash = id << 16 | (idHash & 0xFFFF);
        this.definition = ItemDefinition.forId(id);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount < 0) {
            amount = 0;
        }
        this.amount = amount;
    }

    public boolean isCharged() {
        return getCharge() > 0;
    }

    public int getCharge() {
        return idHash & 0xFFFF;
    }

    public void setCharge(int charge) {
        this.idHash = (idHash >> 16 & 0xFFFF) << 16 | charge;
    }

    public int getIdHash() {
        return idHash;
    }

    public void setIdHash(int hash) {
        this.idHash = hash;
    }

    public boolean hasItemPlugin() {
        return getPlugin() != null;
    }

    public ItemPlugin getPlugin() {
        if (definition == null) {
            return null;
        }
        return definition.getItemPlugin();
    }

    public ItemDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(ItemDefinition definition) {
        this.definition = definition;
    }

    public int getSlot() {
        return index;
    }

    @Override
    public String toString() {
        return "Item id=" + getId() + ", name=" + getName() + ", amount=" + amount;
    }
}
