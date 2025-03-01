package content.region.kandarin.handlers.barbtraining.smithing;

import org.rs.consts.Items;
import java.util.HashMap;
import java.util.Map;

public enum BarbarianWeapon {

    BRONZE(Items.LOGS_1511, Items.BRONZE_BAR_2349, Items.BRONZE_SPEAR_1237, Items.BRONZE_HASTA_11367, 1, 5, 25.00),
    IRON(Items.OAK_LOGS_1521, Items.IRON_BAR_2351, Items.IRON_SPEAR_1239, Items.IRON_HASTA_11369, 1, 20, 50.00),
    STEEL(Items.WILLOW_LOGS_1519, Items.STEEL_BAR_2353, Items.STEEL_SPEAR_1241, Items.STEEL_HASTA_11371, 1, 35, 75.00),
    MITHRIL(Items.MAPLE_LOGS_1517, Items.MITHRIL_BAR_2359, Items.MITHRIL_SPEAR_1243, Items.MITHRIL_HASTA_11373, 1, 55, 100.00),
    ADAMANT(Items.YEW_LOGS_1515, Items.ADAMANTITE_BAR_2361, Items.ADAMANT_SPEAR_1245, Items.ADAMANT_HASTA_11375, 1, 75, 125.00),
    RUNE(Items.MAGIC_LOGS_1513, Items.RUNITE_BAR_2363, Items.RUNE_SPEAR_1247, Items.RUNE_HASTA_11377, 1, 90, 150.00);

    private final int requiredWood;
    private final int requiredBar;
    private final int spearId;
    private final int hastaId;
    private int amount;
    private int requiredLevel;
    private final double experience;

    BarbarianWeapon(int requiredWood, int requiredBar, int spearId, int hastaId, int amount, int requiredLevel, double experience) {
        this.requiredWood = requiredWood;
        this.requiredBar = requiredBar;
        this.spearId = spearId;
        this.hastaId = hastaId;
        this.amount = amount;
        this.requiredLevel = requiredLevel;
        this.experience = experience;
    }

    public int getRequiredWood() {
        return requiredWood;
    }

    public int getRequiredBar() {
        return requiredBar;
    }

    public int getSpearId() {
        return spearId;
    }

    public int getHastaId() {
        return hastaId;
    }

    public int getAmount() {
        return amount;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public double getExperience() {
        return experience;
    }

    private static final Map<Integer, BarbarianWeapon> weaponMap = new HashMap<>();

    static {
        for (BarbarianWeapon weapon : BarbarianWeapon.values()) {
            weaponMap.put(weapon.getRequiredBar(), weapon);
        }
    }

    public static BarbarianWeapon getWeapon(int id) {
        return weaponMap.get(id);
    }

    public static final Map<Integer, BarbarianWeapon> product = new HashMap<>();

    static {
        for (BarbarianWeapon weapon : BarbarianWeapon.values()) {
            product.put(weapon.getRequiredBar(), weapon);
        }
    }
}
