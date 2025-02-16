package content.global.handlers.item.equipment.gloves;

import core.game.node.entity.skill.Skills;
import org.rs.consts.Items;

import java.util.HashMap;
import java.util.Map;

public enum BrawlingGloves {

    MELEE(Items.BRAWLING_GLOVES_MELEE_13845,        500, 1, Skills.ATTACK),
    RANGED(Items.BRAWLING_GLOVES_RANGED_13846,      500, 2, Skills.RANGE),
    MAGIC(Items.BRAWLING_GLOVES_MAGIC_13847,        500, 3, Skills.MAGIC),
    PRAYER(Items.BRAWLING_GLOVES_PRAYER_13848,      450, 4, Skills.PRAYER),
    AGILITY(Items.BRAWLING_GLOVES_AGILITY_13849,    450, 5, Skills.AGILITY),
    WOODCUTTING(Items.BRAWLING_GLOVES_WC_13850,     450, 6, Skills.WOODCUTTING),
    FIREMAKING(Items.BRAWLING_GLOVES_FM_13851,      450, 7, Skills.FIREMAKING),
    MINING(Items.BRAWLING_GLOVES_MINING_13852,      450, 8, Skills.MINING),
    HUNTER(Items.BRAWLING_GLOVES_HUNTER_13853,      450, 9, Skills.HUNTER),
    THIEVING(Items.BRAWLING_GLOVES_THIEVING_13854,  450, 10, Skills.THIEVING),
    SMITHING(Items.BRAWLING_GLOVES_SMITHING_13855,  450, 11, Skills.SMITHING),
    FISHING(Items.BRAWLING_GLOVES_FISHING_13856,    450, 12, Skills.FISHING),
    COOKING(Items.BRAWLING_GLOVES_COOKING_13857,    450, 13, Skills.COOKING);

    private final int id;
    private final int charges;
    private final int indicator;
    private final int skillSlot;

    private static final Map<Integer, BrawlingGloves> glovesMap = new HashMap<>();
    private static final Map<Integer, BrawlingGloves> skillMap = new HashMap<>();
    private static final Map<Integer, BrawlingGloves> indicatorMap = new HashMap<>();

    static {
        for (BrawlingGloves glove : values()) {
            glovesMap.put(glove.id, glove);
            skillMap.put(glove.skillSlot, glove);
            indicatorMap.put(glove.indicator, glove);
        }
    }

    BrawlingGloves(int id, int charges, int indicator, int skillSlot) {
        this.id = id;
        this.charges = charges;
        this.indicator = indicator;
        this.skillSlot = skillSlot;
    }

    public static BrawlingGloves forId(int id) {
        return glovesMap.get(id);
    }

    public static BrawlingGloves forIndicator(int indicator) {
        return indicatorMap.get(indicator);
    }

    public static BrawlingGloves forSkill(int skillSlot) {
        return skillMap.get(skillSlot);
    }

    public int getId() {
        return id;
    }

    public int getCharges() {
        return charges;
    }

    public int getIndicator() {
        return indicator;
    }

    public int getSkillSlot() {
        return skillSlot;
    }
}
