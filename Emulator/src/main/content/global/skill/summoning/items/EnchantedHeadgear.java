package content.global.skill.summoning.items;

import core.game.node.item.Item;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.rs.consts.Items;

public enum EnchantedHeadgear {
    ANTLERS(new Item(Items.ANTLERS_12204), new Item(Items.ANTLERS_12204), new Item(Items.ANTLERS_CHARGED_12206), 40, 10),
    ADAMANT_FULL_HELM(new Item(Items.ADAMANT_FULL_HELM_1161), new Item(Items.ADAMANT_FULL_HELM_E_12658), new Item(Items.ADAMANT_FULL_HELM_CHARGED_12659), 50, 20),
    SLAYER_HELMET(new Item(Items.SLAYER_HELMET_13263), new Item(Items.SLAYER_HELMET_E_14636), new Item(Items.SLAYER_HELMET_CHARGED_14637), 50, 20),
    SNAKESKIN_BANDANA(new Item(Items.SNAKESKIN_BANDANA_6326), new Item(Items.SNAKESKIN_BANDANA_E_12660), new Item(Items.SNAKESKIN_BANDANA_CHARGED_12661), 50, 20),
    LIZARD_SKULL(new Item(Items.LIZARD_SKULL_12207), new Item(Items.LIZARD_SKULL_12207), new Item(Items.LIZARD_SKULL_CHARGED_12209), 65, 30),
    SPLITBARK_HELM(new Item(Items.SPLITBARK_HELM_3385), new Item(Items.SPLITBARK_HELM_E_12662), new Item(Items.SPLITBARK_HELM_CHARGED_12663), 50, 30),
    RUNE_FULL_HELM(new Item(Items.RUNE_FULL_HELM_1163), new Item(Items.RUNE_FULL_HELM_E_12664), new Item(Items.RUNE_FULL_HELM_CHARGED_12665), 60, 30),
    WARRIOR_HELM(new Item(Items.WARRIOR_HELM_3753), new Item(Items.WARRIOR_HELM_E_12676), new Item(Items.WARRIOR_HELM_CHARGED_12677), 70, 35),
    BERSERKER_HELM(new Item(Items.BERSERKER_HELM_3751), new Item(Items.BERSERKER_HELM_E_12674), new Item(Items.BERSERKER_HELM_CHARGED_12675), 70, 35),
    ARCHER_HELM(new Item(Items.ARCHER_HELM_3749), new Item(Items.ARCHER_HELM_E_12672), new Item(Items.ARCHER_HELM_CHARGED_12673), 70, 35),
    FARSEER_HELM(new Item(Items.FARSEER_HELM_3755), new Item(Items.FARSEER_HELM_E_12678), new Item(Items.FARSEER_HELM_CHARGED_12679), 70, 35),
    HELM_OF_NEITIZNOT(new Item(Items.HELM_OF_NEITIZNOT_10828), new Item(Items.HELM_OF_NEITIZNOT_E_12680), new Item(Items.HELM_OF_NEITIZNOT_CHARGED_12681), 90, 45),
    FEATHER_HEADDRESS_0(new Item(Items.FEATHER_HEADDRESS_12210), new Item(Items.FEATHER_HEADDRESS_12210), new Item(Items.FEATHER_HEADDRESS_CHARGED_12212), 150, 50),
    FEATHER_HEADDRESS_1(new Item(Items.FEATHER_HEADDRESS_12222), new Item(Items.FEATHER_HEADDRESS_12222), new Item(Items.FEATHER_HEADDRESS_CHARGED_12224), 150, 50),
    FEATHER_HEADDRESS_2(new Item(Items.FEATHER_HEADDRESS_12216), new Item(Items.FEATHER_HEADDRESS_12216), new Item(Items.FEATHER_HEADDRESS_CHARGED_12218), 150, 50),
    FEATHER_HEADDRESS_3(new Item(Items.FEATHER_HEADDRESS_12219), new Item(Items.FEATHER_HEADDRESS_12219), new Item(Items.FEATHER_HEADDRESS_CHARGED_12221), 150, 50),
    FEATHER_HEADDRESS_4(new Item(Items.FEATHER_HEADDRESS_12213), new Item(Items.FEATHER_HEADDRESS_12213), new Item(Items.FEATHER_HEADDRESS_CHARGED_12215), 150, 50),
    DRAGON_HELM(new Item(Items.DRAGON_MED_HELM_1149), new Item(Items.DRAGON_MED_HELM_E_12666), new Item(Items.DRAGON_MED_HELM_CHARGED_12667), 110, 50),
    LUNAR_HELM(new Item(Items.LUNAR_HELM_9096), new Item(Items.LUNAR_HELM_E_12668), new Item(Items.LUNAR_HELM_CHARGED_12669), 110, 55),
    ARMADYL_HELM(new Item(Items.ARMADYL_HELMET_11718), new Item(Items.ARMADYL_HELMET_E_12670), new Item(Items.ARMADYL_HELMET_CHARGED_12671), 120, 60);

    private final Item defaultItem;
    private final Item enchantedItem;
    private final Item chargedItem;
    private final int scrollCapacity;
    private final int requiredLevel;

    private static final Int2ObjectOpenHashMap<EnchantedHeadgear> product = new Int2ObjectOpenHashMap<>();

    static {
        for (EnchantedHeadgear headgear : values()) {
            product.put(headgear.getDefaultItem().getId(), headgear);
        }
    }

    EnchantedHeadgear(Item defaultItem, Item enchantedItem, Item chargedItem, int scrollCapacity, int requiredLevel) {
        this.defaultItem = defaultItem;
        this.enchantedItem = enchantedItem;
        this.chargedItem = chargedItem;
        this.scrollCapacity = scrollCapacity;
        this.requiredLevel = requiredLevel;
    }

    public Item getDefaultItem() {
        return defaultItem;
    }

    public Item getEnchantedItem() {
        return enchantedItem;
    }

    public Item getChargedItem() {
        return chargedItem;
    }

    public int getScrollCapacity() {
        return scrollCapacity;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public static EnchantedHeadgear forItem(Item item) {
        return product.get(item.getId());
    }
}
