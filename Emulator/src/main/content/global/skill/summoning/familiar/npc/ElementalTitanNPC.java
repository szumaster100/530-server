package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;

public abstract class ElementalTitanNPC extends Familiar {

    private static final int scrollHealAmount = 8;
    private static final double scrollDefenceBoostPercent = 0.125;

    public ElementalTitanNPC(Player owner, int id, int ticks, int pouchId, int specialCost, int attackStyle) {
        super(owner, id, ticks, pouchId, specialCost, attackStyle);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        int currentDefenceLevel = owner.getSkills().getLevel(Skills.DEFENCE);
        int maximumDefenceLevel = owner.getSkills().getStaticLevel(Skills.DEFENCE);
        owner.getSkills().updateLevel(Skills.DEFENCE,
                (int) ((1.0 + scrollDefenceBoostPercent) * currentDefenceLevel),
                (int) ((1.0 + scrollDefenceBoostPercent) * maximumDefenceLevel));
        int currentHp = owner.getSkills().getLifepoints();
        int maxHp = owner.getSkills().getMaximumLifepoints() + scrollHealAmount;
        int healAmount = Math.min(maxHp - currentHp, scrollHealAmount);
        if (healAmount > 0) {
            owner.getSkills().healNoRestrictions(healAmount);
            return true;
        } else {
            owner.sendMessage("You are already at maximum hitpoints!");
            return false;
        }
    }
}
