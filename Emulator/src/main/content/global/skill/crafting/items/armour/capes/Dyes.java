package content.global.skill.crafting.items.armour.capes;

import core.game.node.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.rs.consts.Items;

public enum Dyes {
    BLACK(new Item(Items.BLACK_MUSHROOM_INK_4622)),
    RED(new Item(Items.RED_DYE_1763)),
    YELLOW(new Item(Items.YELLOW_DYE_1765)),
    BLUE(new Item(Items.BLUE_DYE_1767)),
    ORANGE(new Item(Items.ORANGE_DYE_1769)),
    GREEN(new Item(Items.GREEN_DYE_1771)),
    PURPLE(new Item(Items.PURPLE_DYE_1773)),
    PINK(new Item(Items.PINK_DYE_6955));

    private final Item item;

    private static final Int2ObjectOpenHashMap<Dyes> itemToDyeMap = new Int2ObjectOpenHashMap<>();

    static {
        for (Dyes dye : values()) {
            itemToDyeMap.put(dye.item.getId(), dye);
        }
    }

    Dyes(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public static Dyes forItem(Item item) {
        return itemToDyeMap.get(item.getId());
    }
}
