package core.game.node.entity.skill;

public final class SkillBonus {

    private final int skillId;

    private final double bonus;

    private final int baseBonus;

    public SkillBonus(int skillId, double bonus) {
        this(skillId, bonus, 0);
    }

    public SkillBonus(int skillId, double bonus, int baseBonus) {
        this.skillId = skillId;
        this.bonus = bonus;
        this.baseBonus = baseBonus;
    }

    public int getSkillId() {
        return skillId;
    }

    public double getBonus() {
        return bonus;
    }

    public int getBaseBonus() {
        return baseBonus;
    }

}
