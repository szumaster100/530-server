package core.game.node.entity.combat.equipment.special;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.InteractionType;
import core.game.node.entity.combat.MeleeSwingHandler;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.tools.RandomFunction;
import org.rs.consts.Items;
import org.rs.consts.Sounds;

import java.util.List;

import static core.api.ContentAPIKt.playGlobalAudio;

@Initializable
public final class PowerStabSpecialHandler extends MeleeSwingHandler implements Plugin<Object> {

    private static final int SPECIAL_ENERGY = 60;

    private static final Animation ANIMATION = new Animation(3157, Priority.HIGH);

    private static final Graphics GRAPHICS = new Graphics(org.rs.consts.Graphics.DRAGON_2H_SPECIAL_1225);

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public void impact(Entity entity, Entity victim, BattleState state) {
        if (state.getTargets() != null) {
            for (BattleState s : state.getTargets()) {
                if (s != null) {
                    s.getVictim().getImpactHandler().handleImpact(entity, s.getEstimatedHit(), CombatStyle.MELEE, s);
                }
            }
            return;
        }
        victim.getImpactHandler().handleImpact(entity, state.getEstimatedHit(), CombatStyle.MELEE, state);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        CombatStyle.MELEE.getSwingHandler().register(Items.DRAGON_2H_SWORD_7158, this);
        return this;
    }

    @Override
    public int swing(Entity entity, Entity victim, BattleState state) {
        if (!((Player) entity).getSettings().drainSpecial(SPECIAL_ENERGY)) {
            return -1;
        }
        boolean multi = entity.getProperties().isMultiZone();
        if (!multi) {
            return super.swing(entity, victim, state);
        }
        @SuppressWarnings("rawtypes")
        List list = victim instanceof NPC ? RegionManager.getSurroundingNPCs(entity, 9, entity) : RegionManager.getSurroundingPlayers(entity, 9, entity);
        BattleState[] targets = new BattleState[list.size()];
        int count = 0;
        for (Object o : list) {
            Entity e = (Entity) o;
            if (CombatStyle.RANGE.getSwingHandler().canSwing(entity, e) != InteractionType.NO_INTERACT) {
                BattleState s = targets[count++] = new BattleState(entity, e);
                int hit = 0;
                if (isAccurateImpact(entity, e)) {
                    hit = RandomFunction.random(calculateHit(entity, e, 1.0) + 1);
                }
                s.setStyle(CombatStyle.MELEE);
                s.setEstimatedHit(hit);
            }
        }
        state.setTargets(targets);
        return 1;
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        playGlobalAudio(entity.getLocation(), Sounds.DRAGON_AXE_THUNDER_2530);
        entity.visualize(ANIMATION, GRAPHICS);
    }

    @Override
    public void visualizeImpact(Entity entity, Entity victim, BattleState state) {
        if (state.getTargets() != null) {
            for (BattleState s : state.getTargets()) {
                if (s != null) {
                    s.getVictim().animate(victim.getProperties().getDefenceAnimation());
                }
            }
            return;
        }
        victim.animate(victim.getProperties().getDefenceAnimation());
    }
}
