package core.game.node.entity.impl;

import core.game.container.Container;
import core.game.container.impl.EquipmentContainer;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.CombatPulse;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.equipment.ArmourSet;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.combat.spell.CombatSpell;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
import core.game.system.config.NPCConfigParser;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;

public final class Properties {

    private final Entity entity;

    private CombatPulse combatPulse;

    private boolean retaliating = true;

    private boolean teleporting;

    private int combatLevel;

    private WeaponInterface.AttackStyle attackStyle;

    private Location teleportLocation;

    private Location spawnLocation;

    private int[] bonuses = new int[15];

    private int attackSpeed = 4;

    private long lastAnimationEnd;

    private Animation attackAnimation = new Animation(422, Animator.Priority.HIGH);

    private Animation defenceAnimation = new Animation(404);

    private Animation deathAnimation = new Animation(9055, Animator.Priority.HIGH);

    public Graphics deathGfx = new Graphics(-1);

    private Animation rangeAnimation;

    private Animation magicAnimation;

    private CombatSpell spell;

    private CombatSpell autocastSpell;

    private ArmourSet armourSet;

    private boolean multiZone;

    private boolean safeZone;

    public Location safeRespawn;

    private int combatTimeOut = 10;

    private boolean npcWalkable;

    private CombatStyle protectStyle;

    public Properties(Entity entity) {
        this.entity = entity;
        this.combatPulse = new CombatPulse(entity);
    }

    public void updateDefenceAnimation() {
        if (entity instanceof NPC) {
            Animation animation = ((NPC) entity).getDefinition().getConfiguration(NPCConfigParser.DEFENCE_ANIMATION);
            if (animation != null) {
                defenceAnimation = animation;
            }
            return;
        }
        Container c = ((Player) entity).getEquipment();
        Item item = c.get(EquipmentContainer.SLOT_SHIELD);
        if (item != null) {
            defenceAnimation = item.getDefinition().getConfiguration(ItemConfigParser.DEFENCE_ANIMATION, Animation.create(1156));
        } else if ((item = c.get(EquipmentContainer.SLOT_WEAPON)) != null) {
            defenceAnimation = item.getDefinition().getConfiguration(ItemConfigParser.DEFENCE_ANIMATION, Animation.create(424));
        } else {
            defenceAnimation = Animation.create(397);
        }
    }

    public Animation getCombatAnimation(int index) {
        return index == 0 ? attackAnimation : index == 1 ? magicAnimation : index == 2 ? rangeAnimation : index == 3 ? defenceAnimation : deathAnimation;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isTeleporting() {
        return teleporting;
    }

    public void setTeleporting(boolean teleporting) {
        this.teleporting = teleporting;
    }

    @Deprecated
    public int getCombatLevel() {
        return combatLevel;
    }

    public int getCurrentCombatLevel() {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if ((GameWorld.getSettings().isPvp() || (GameWorld.getSettings().getWild_pvp_enabled() && player.getSkullManager().isWilderness()))
                && !player.getFamiliarManager().isUsingSummoning()) {

                return combatLevel;
            } else {
                return combatLevel + player.getFamiliarManager().getSummoningCombatLevel();
            }
        }
        return combatLevel;
    }

    public void setCombatLevel(int combatLevel) {
        this.combatLevel = combatLevel;
    }

    public WeaponInterface.AttackStyle getAttackStyle() {
        return attackStyle;
    }

    public void setAttackStyle(WeaponInterface.AttackStyle attackStyle) {
        this.attackStyle = attackStyle;
    }

    public Location getTeleportLocation() {
        return teleportLocation;
    }

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public int[] getBonuses() {
        return bonuses;
    }

    public void setBonuses(int[] bonuses) {
        this.bonuses = bonuses;
    }

    public long getLastAnimationEnd() {
        return lastAnimationEnd;
    }

    public CombatPulse getCombatPulse() {
        return combatPulse;
    }

    public void setCombatPulse(CombatPulse combatPulse) {
        this.combatPulse = (CombatPulse) combatPulse;
    }

    public boolean isRetaliating() {
        return retaliating;
    }

    public void setRetaliating(boolean retaliating) {
        this.retaliating = retaliating;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public Animation getAttackAnimation() {
        return attackAnimation;
    }

    public void setAttackAnimation(Animation attackAnimation) {
        this.attackAnimation = attackAnimation;
    }

    public Animation getDefenceAnimation() {
        return defenceAnimation;
    }

    public void setDefenceAnimation(Animation defenceAnimation) {
        this.defenceAnimation = defenceAnimation;
    }

    public void setSpell(CombatSpell spell) {
        this.spell = spell;
        if (spell != null) {
            combatPulse.updateStyle();
        }
    }

    public CombatSpell getSpell() {
        return spell;
    }

    public CombatSpell getAutocastSpell() {
        return autocastSpell;
    }

    public void setAutocastSpell(CombatSpell autocastSpell) {
        this.autocastSpell = autocastSpell;
    }

    public ArmourSet getArmourSet() {
        return armourSet;
    }

    public void setArmourSet(ArmourSet armourSet) {
        this.armourSet = armourSet;
    }

    public Animation getDeathAnimation() {
        return deathAnimation;
    }

    public void setDeathAnimation(Animation deathAnimation) {
        this.deathAnimation = deathAnimation;
    }

    public boolean isMultiZone() {
        return multiZone;
    }

    public void setMultiZone(boolean multiZone) {
        this.multiZone = multiZone;
    }

    public boolean isSafeZone() {
        return safeZone;
    }

    public void setSafeZone(boolean safeZone) {
        this.safeZone = safeZone;
    }

    public int getCombatTimeOut() {
        return combatTimeOut;
    }

    public void setCombatTimeOut(int combatTimeOut) {
        this.combatTimeOut = combatTimeOut;
    }

    public boolean isNPCWalkable() {
        return npcWalkable;
    }

    public void setNPCWalkable(boolean npcWalkable) {
        this.npcWalkable = npcWalkable;
    }

    public Animation getRangeAnimation() {
        return rangeAnimation;
    }

    public void setRangeAnimation(Animation rangeAnimation) {
        this.rangeAnimation = rangeAnimation;
    }

    public Animation getMagicAnimation() {
        return magicAnimation;
    }

    public void setMagicAnimation(Animation magicAnimation) {
        this.magicAnimation = magicAnimation;
    }

    public CombatStyle getProtectStyle() {
        return protectStyle;
    }

    public void setProtectStyle(CombatStyle protectStyle) {
        this.protectStyle = protectStyle;
    }

}
