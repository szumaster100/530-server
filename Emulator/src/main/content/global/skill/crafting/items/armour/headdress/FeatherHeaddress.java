package content.global.skill.crafting.items.armour.headdress;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.rs.consts.Items;

public enum FeatherHeaddress {

    FEATHER_HEADDRESS_BLUE(Items.BLUE_FEATHER_10089, Items.FEATHER_HEADDRESS_12210),

    FEATHER_HEADDRESS_ORANGE(Items.ORANGE_FEATHER_10091, Items.FEATHER_HEADDRESS_12222),

    FEATHER_HEADDRESS_RED(Items.RED_FEATHER_10088, Items.FEATHER_HEADDRESS_12216),

    FEATHER_HEADDRESS_STRIPY(Items.STRIPY_FEATHER_10087, Items.FEATHER_HEADDRESS_12219),

    FEATHER_HEADDRESS_YELLOW(Items.YELLOW_FEATHER_10090, Items.FEATHER_HEADDRESS_12213);

    private final int base;
    private final int product;

    private static final Int2ObjectOpenHashMap<FeatherHeaddress> baseToHeaddressMap = new Int2ObjectOpenHashMap<>();

    static {
        for (FeatherHeaddress headdress : values()) {
            baseToHeaddressMap.put(headdress.getBase(), headdress);
        }
    }

    FeatherHeaddress(int base, int product) {
        this.base = base;
        this.product = product;
    }

    public int getBase() {
        return base;
    }

    public int getProduct() {
        return product;
    }

    public static FeatherHeaddress forBase(int baseId) {
        return baseToHeaddressMap.get(baseId);
    }
}
