package content.global.skill.crafting.items.armour.capes;

import core.game.node.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.rs.consts.Items;

public enum Capes {
    BLACK(Dyes.BLACK, new Item(Items.BLACK_CAPE_1019)),
    RED(Dyes.RED, new Item(Items.RED_CAPE_1007)),
    BLUE(Dyes.BLUE, new Item(Items.BLUE_CAPE_1021)),
    YELLOW(Dyes.YELLOW, new Item(Items.YELLOW_CAPE_1023)),
    GREEN(Dyes.GREEN, new Item(Items.GREEN_CAPE_1027)),
    PURPLE(Dyes.PURPLE, new Item(Items.PURPLE_CAPE_1029)),
    ORANGE(Dyes.ORANGE, new Item(Items.ORANGE_CAPE_1031)),
    PINK(Dyes.PINK, new Item(Items.PINK_CAPE_6959));

    private final Dyes dye;
    private final Item cape;

    private static final Int2ObjectOpenHashMap<Capes> dyeToCapeMap = new Int2ObjectOpenHashMap<>();

    static {
        for (Capes cape : values()) {
            dyeToCapeMap.put(cape.dye.getItem().getId(), cape);
        }
    }

    Capes(Dyes dye, Item cape) {
        this.dye = dye;
        this.cape = cape;
    }

    public Dyes getDye() {
        return dye;
    }

    public Item getCape() {
        return cape;
    }

    public static Capes forDye(int dyeId) {
        return dyeToCapeMap.get(dyeId);
    }
}
