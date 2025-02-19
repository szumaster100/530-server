package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.*;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.SkillBonus;
import core.game.node.entity.skill.Skills;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.tools.RandomFunction;

@Initializable
public final class DreadfowlNPC extends content.global.skill.summoning.familiar.Familiar {

    private static final CombatSwingHandler COMBAT_HANDLER = new MeleeSwingHandler() {

        @Override
        public InteractionType canSwing(Entity entity, Entity victim) {
            if (((DreadfowlNPC) entity).specialMove) {
                return CombatStyle.MAGIC.getSwingHandler().canSwing(entity, victim);
            }
            return super.canSwing(entity, victim);
        }

        @Override
        public int swing(Entity entity, Entity victim, BattleState state) {
            DreadfowlNPC npc = (DreadfowlNPC) entity;
            if (npc.specialMove) {
                npc.specialMove(new content.global.skill.summoning.familiar.FamiliarSpecial(victim));
                npc.specialMove = false;
                return -1;
            }
            npc.specialMove = RandomFunction.randomize(10) == 0;
            return super.swing(entity, victim, state);
        }

    };
    private boolean specialMove;

    public DreadfowlNPC() {
        this(null, 6825);
    }

    public DreadfowlNPC(Player owner, int id) {
        super(owner, id, 400, 12043, 3, WeaponInterface.STYLE_CAST);
        super.setCombatHandler(COMBAT_HANDLER);
        boosts.add(new SkillBonus(Skills.FARMING, 1));
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new DreadfowlNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        final Entity target = (Entity) special.getNode();
        if (!canAttack(target)) {
            return false;
        }
        if (!owner.getProperties().getCombatPulse().isAttacking() && !owner.inCombat()) {
            owner.getPacketDispatch().sendMessage("Your familiar can only attack when you're in combat.");
            return false;
        }
        if (getProperties().getCombatPulse().getNextAttack() > GameWorld.getTicks() || CombatStyle.MAGIC.getSwingHandler().canSwing(this, target) == InteractionType.NO_INTERACT) {
            specialMove = true;
            getProperties().getCombatPulse().attack(target);
            return true;
        }
        visualize(new Animation(5387, Priority.HIGH), Graphics.create(1523));
        Projectile.magic(this, target, 1318, 40, 36, 51, 10).send();
        int ticks = 2 + (int) Math.floor(getLocation().getDistance(target.getLocation()) * 0.5);
        getProperties().getCombatPulse().setNextAttack(4);
        GameWorld.getPulser().submit(new Pulse(ticks, this, target) {
            @Override
            public boolean pulse() {
                BattleState state = new BattleState(DreadfowlNPC.this, target);
                int hit = 0;
                if (CombatStyle.MAGIC.getSwingHandler().isAccurateImpact(DreadfowlNPC.this, target)) {
                    hit = RandomFunction.randomize(3);
                }
                state.setEstimatedHit(hit);
                target.getImpactHandler().handleImpact(owner, hit, CombatStyle.MAGIC, state);
                return true;
            }
        });
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{6825, 6826};
    }

}
