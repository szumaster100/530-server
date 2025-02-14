package core.game.node.entity.combat;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.equipment.Ammunition;
import core.game.node.entity.combat.equipment.ArmourSet;
import core.game.node.entity.combat.equipment.RangeWeapon;
import core.game.node.entity.combat.equipment.Weapon;
import core.game.node.entity.combat.spell.CombatSpell;

public final class BattleState {

    private Entity entity;

    private Entity victim;

    private int estimatedHit;

    private int maximumHit;

    private int secondaryHit = -1;

    private int recoilDamage = -1;

    private int poisonDamage = -1;

    private boolean frozen;

    private BattleState[] targets;

    private Weapon weapon;

    private RangeWeapon rangeWeapon;

    private Ammunition ammunition;

    private CombatSpell spell;

    private CombatStyle style;

    private ArmourSet armourEffect;

    public BattleState() {

    }

    public BattleState(Entity entity, Entity victim) {
        this.victim = victim;
        this.entity = entity;
    }

    public void neutralizeHits() {
        if (getEstimatedHit() > 0) {
            setEstimatedHit(0);
        }
        if (getSecondaryHit() > 0) {
            setSecondaryHit(0);
        }
    }

    public Entity getVictim() {
        return victim;
    }

    public void setVictim(Entity victim) {
        this.victim = victim;
    }

    public int getEstimatedHit() {
        return estimatedHit;
    }

    public void setEstimatedHit(int estimatedHit) {
        this.estimatedHit = estimatedHit;
    }

    public int getSecondaryHit() {
        return secondaryHit;
    }

    public void setSecondaryHit(int secondaryHit) {
        this.secondaryHit = secondaryHit;
    }

    public int getRecoilDamage() {
        return recoilDamage;
    }

    public void setRecoilDamage(int recoilDamage) {
        this.recoilDamage = recoilDamage;
    }

    public BattleState[] getTargets() {
        return targets;
    }

    public void setTargets(BattleState[] targets) {
        this.targets = targets;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public void setPoisonDamage(int poisonDamage) {
        this.poisonDamage = poisonDamage;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public CombatSpell getSpell() {
        return spell;
    }

    public void setSpell(CombatSpell spell) {
        this.spell = spell;
    }

    public int getMaximumHit() {
        return maximumHit;
    }

    public void setMaximumHit(int maximumHit) {
        this.maximumHit = maximumHit;
    }

    public RangeWeapon getRangeWeapon() {
        return rangeWeapon;
    }

    public void setRangeWeapon(RangeWeapon rangeWeapon) {
        this.rangeWeapon = rangeWeapon;
    }

    public Ammunition getAmmunition() {
        return ammunition;
    }

    public void setAmmunition(Ammunition ammunition) {
        this.ammunition = ammunition;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    public CombatStyle getStyle() {
        return style;
    }

    public void setStyle(CombatStyle style) {
        this.style = style;
    }

    public ArmourSet getArmourEffect() {
        return armourEffect;
    }

    public void setArmourEffect(ArmourSet armourEffect) {
        this.armourEffect = armourEffect;
    }

    public Entity getAttacker() {
        return entity;
    }

    public int getTotalDamage() {
        int hit = Math.max(estimatedHit, 0) + Math.max(secondaryHit, 0);

        if (targets != null) {
            for (BattleState s : targets) {
                if (s != null) {
                    hit += Math.max(s.getEstimatedHit(), 0) + Math.max(s.getSecondaryHit(), 0);
                }
            }
        }
        return hit;
    }

}