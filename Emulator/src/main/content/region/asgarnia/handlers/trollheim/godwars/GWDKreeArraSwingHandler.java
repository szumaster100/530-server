package content.region.asgarnia.handlers.trollheim.godwars;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.CombatSwingHandler;
import core.game.node.entity.combat.InteractionType;
import core.game.node.entity.combat.equipment.ArmourSet;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.npc.NPC;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.map.zone.ZoneBorders;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.tools.RandomFunction;

import java.util.ArrayList;
import java.util.List;

public final class GWDKreeArraSwingHandler extends CombatSwingHandler {

    private static final ZoneBorders CHAMBER = new ZoneBorders(2824, 5296, 2842, 5308);
    private static final Animation MELEE_ATTACK = new Animation(6977, Priority.HIGH);
    private static final Animation RANGE_ATTACK = new Animation(6976, Priority.HIGH);
    private static final Graphics END_GRAPHICS = new Graphics(80, 96);

    public GWDKreeArraSwingHandler() {
        super(CombatStyle.RANGE);
    }

    @Override
    public InteractionType canSwing(Entity entity, Entity victim) {
        return CombatStyle.RANGE.getSwingHandler().canSwing(entity, victim);
    }

    @Override
    public int swing(Entity entity, Entity victim, BattleState state) {
        int ticks = 1;
        int distance = entity != null ? entity.size() >> 1 : 0;
        if (entity != null && !entity.inCombat()) {
            distance++;
        }
        if (entity != null && victim.getLocation().withinDistance(entity.getCenterLocation(), distance) && RandomFunction.RANDOM.nextBoolean()) {
            int hit = 0;
            int max = CombatStyle.MELEE.getSwingHandler().calculateHit(entity, victim, 1.0);
            if (CombatStyle.MELEE.getSwingHandler().isAccurateImpact(entity, victim, CombatStyle.MELEE)) {
                hit = RandomFunction.random(max);
            }
            state.setEstimatedHit(hit);
            hit = 0;
            if (CombatStyle.MELEE.getSwingHandler().isAccurateImpact(entity, victim, CombatStyle.MELEE)) {
                hit = RandomFunction.random(max);
            }
            state.setSecondaryHit(hit);
            state.setMaximumHit(max);
            state.setStyle(CombatStyle.MELEE);
        } else {
            ticks += (int) Math.ceil(entity != null ? entity.getLocation().getDistance(victim.getLocation()) : 0.0 * 0.3);
            NPC npc = (NPC) entity;
            List<BattleState> list = new ArrayList<>(20);
            for (Entity t : RegionManager.getLocalPlayers(npc, 28)) {
                if (!CHAMBER.insideBorder(t.getLocation())) {
                    continue;
                }
                if (t.isAttackable(npc, CombatStyle.RANGE, false)) {
                    list.add(new BattleState(entity, t));
                }
            }
            BattleState[] targets;
            state.setStyle(CombatStyle.RANGE);
            state.setTargets(targets = list.toArray(new BattleState[0]));
            for (BattleState s : targets) {
                CombatStyle style = RandomFunction.randomize(10) < 3 ? CombatStyle.MAGIC : CombatStyle.RANGE;
                s.setStyle(style);
                int hit = 0;
                if (style.getSwingHandler().isAccurateImpact(entity, s.getVictim(), style)) {
                    int max = style.getSwingHandler().calculateHit(entity, s.getVictim(), 1.0);
                    s.setMaximumHit(max);
                    if (style.equals(CombatStyle.RANGE)) {
                        max = 71;
                    }
                    hit = RandomFunction.random(max);
                }
                s.setEstimatedHit(hit);
            }
        }
        return ticks;
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        if (state != null) {
            if (state.getStyle() == CombatStyle.MELEE) {
                entity.animate(MELEE_ATTACK);
            } else {
                entity.animate(RANGE_ATTACK);
                for (BattleState s : state.getTargets()) {
                    int gfxId = 1197;
                    if (s.getStyle() == CombatStyle.MAGIC) {
                        gfxId = 1198;
                    }
                    Projectile.ranged(entity, s.getVictim(), gfxId, 92, 36, 46, 5).send();
                }
            }
        }
    }

    @Override
    public ArmourSet getArmourSet(Entity e) {
        if (getType() != null)
            return getType().getSwingHandler().getArmourSet(e);
        else return ArmourSet.AHRIM;
    }

    @Override
    public double getSetMultiplier(Entity e, int skillId) {
        if (getType() != null)
            return getType().getSwingHandler().getSetMultiplier(e, skillId);
        else return 0.0;
    }

    @Override
    public void impact(Entity entity, Entity victim, BattleState state) {
        if (state != null && state.getStyle() == CombatStyle.MELEE) {
            state.getStyle().getSwingHandler().impact(entity, victim, state);
            return;
        }
        if (state != null) {
            for (BattleState s : state.getTargets()) {
                if (s == null || s.getEstimatedHit() < 0) {
                    continue;
                }
                int hit = s.getEstimatedHit();
                s.getVictim().getImpactHandler().handleImpact(entity, hit, s.getStyle(), s);
            }
        }
    }

    @Override
    public void visualizeImpact(Entity entity, Entity victim, BattleState state) {
        if (state != null && state.getStyle() == CombatStyle.MELEE) {
            victim.animate(victim.getProperties().getDefenceAnimation());
            return;
        }
        if (state != null) {
            for (BattleState s : state.getTargets()) {
                s.getVictim().animate(s.getVictim().getProperties().getDefenceAnimation());
                if (RandomFunction.randomize(10) < 8) {
                    Direction dir = Direction.getLogicalDirection(entity.getLocation(), s.getVictim().getLocation());
                    Location destination = s.getVictim().getLocation().transform(dir.getStepX(), dir.getStepY(), 0);
                    if (CHAMBER.insideBorder(destination)) {
                        s.getVictim().getProperties().setTeleportLocation(destination);
                        s.getVictim().graphics(END_GRAPHICS);
                    }
                }
            }
        }
    }

    @Override
    public void adjustBattleState(Entity entity, Entity victim, BattleState state) {
        if (state.getStyle() == CombatStyle.MELEE) {
            state.getStyle().getSwingHandler().adjustBattleState(entity, victim, state);
            return;
        }
        for (BattleState s : state.getTargets()) {
            s.getStyle().getSwingHandler().adjustBattleState(entity, s.getVictim(), s);
        }
    }

    @Override
    public int calculateAccuracy(Entity entity) {
        if (getType() != null)
            return getType().getSwingHandler().calculateAccuracy(entity);
        else return -1;
    }

    @Override
    public int calculateDefence(Entity victim, Entity attacker) {
        if (getType() != null)
            return getType().getSwingHandler().calculateDefence(victim, attacker);
        else return -1;
    }

    @Override
    public int calculateHit(Entity entity, Entity victim, double modifier) {
        if (getType() != null)
            return getType().getSwingHandler().calculateHit(entity, victim, modifier);
        else return -1;
    }

}
