package content.global.skill.crafting.items.armour.leather.dragon;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.rs.consts.Items;

public enum DragonLeather {

    GREEN_D_HIDE_VAMBS(Items.GREEN_D_LEATHER_1745, 1, Items.GREEN_DHIDE_VAMB_1065, 57, 62.0),

    GREEN_D_HIDE_CHAPS(Items.GREEN_D_LEATHER_1745, 2, Items.GREEN_DHIDE_CHAPS_1099, 60, 124.0),

    GREEN_D_HIDE_BODY(Items.GREEN_D_LEATHER_1745, 3, Items.GREEN_DHIDE_BODY_1135, 63, 186.0),

    BLUE_D_HIDE_VAMBS(Items.BLUE_D_LEATHER_2505, 1, Items.BLUE_DHIDE_VAMB_2487, 66, 70.0),

    BLUE_D_HIDE_CHAPS(Items.BLUE_D_LEATHER_2505, 2, Items.BLUE_DHIDE_CHAPS_2493, 68, 140.0),

    BLUE_D_HIDE_BODY(Items.BLUE_D_LEATHER_2505, 3, Items.BLUE_DHIDE_BODY_2499, 71, 210.0),

    RED_D_HIDE_VAMBS(Items.RED_DRAGON_LEATHER_2507, 1, Items.RED_DHIDE_VAMB_2489, 73, 78.0),

    RED_D_HIDE_CHAPS(Items.RED_DRAGON_LEATHER_2507, 2, Items.RED_DHIDE_CHAPS_2495, 75, 156.0),

    RED_D_HIDE_BODY(Items.RED_DRAGON_LEATHER_2507, 3, Items.RED_DHIDE_BODY_2501, 77, 234.0),

    BLACK_D_HIDE_VAMBS(Items.BLACK_D_LEATHER_2509, 1, Items.BLACK_DHIDE_VAMB_2491, 79, 86.0),

    BLACK_D_HIDE_CHAPS(Items.BLACK_D_LEATHER_2509, 2, Items.BLACK_DHIDE_CHAPS_2497, 82, 172.0),

    BLACK_D_HIDE_BODY(Items.BLACK_D_LEATHER_2509, 3, Items.BLACK_DHIDE_BODY_2503, 84, 258.0);

    private final int leather;
    private final int amount;
    private final int product;
    private final int level;
    private final double experience;

    private static final Int2ObjectOpenHashMap<DragonLeather> productToDragonLeatherMap = new Int2ObjectOpenHashMap<>();

    static {
        for (DragonLeather leather : values()) {
            productToDragonLeatherMap.put(leather.getProduct(), leather);
        }
    }

    DragonLeather(int leather, int amount, int product, int level, double experience) {
        this.leather = leather;
        this.amount = amount;
        this.product = product;
        this.level = level;
        this.experience = experience;
    }

    public int getLeather() {
        return leather;
    }

    public int getAmount() {
        return amount;
    }

    public int getProduct() {
        return product;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public static DragonLeather forId(int productId) {
        return productToDragonLeatherMap.get(productId);
    }
}
