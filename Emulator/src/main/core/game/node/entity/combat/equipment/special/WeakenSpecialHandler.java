package core.game.node.entity.combat.equipment.special;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.MeleeSwingHandler;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.tools.RandomFunction;
import org.rs.consts.Sounds;

import static core.api.ContentAPIKt.playGlobalAudio;

@Initializable
public final class WeakenSpecialHandler extends MeleeSwingHandler implements Plugin<Object> {

    private static final int SPECIAL_ENERGY = 50;

    private static final Animation ANIMATION = new Animation(2890, Priority.HIGH);

    private static final Graphics GRAPHICS = new Graphics(483);

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        CombatStyle.MELEE.getSwingHandler().register(6746, this);
        return this;
    }

    @Override
    public int swing(Entity entity, Entity victim, BattleState state) {
        if (!((Player) entity).getSettings().drainSpecial(SPECIAL_ENERGY)) {
            return -1;
        }
        state.setStyle(CombatStyle.MELEE);
        int hit = 0;
        if (isAccurateImpact(entity, victim, CombatStyle.MELEE, 1.0, 1.0)) {
            hit = RandomFunction.random(calculateHit(entity, victim, 1.0) + 1);
            if (victim instanceof Player) {
                ((Player) victim).getPacketDispatch().sendMessage("You have been drained.");
            }
            // TODO 10% drain to demons
            int lower = (int) (victim.getSkills().getStaticLevel(Skills.DEFENCE) * 0.05) + 1;
            victim.getSkills().updateLevel(Skills.DEFENCE, -lower, 0);
            lower = (int) (victim.getSkills().getStaticLevel(Skills.ATTACK) * 0.05) + 1;
            victim.getSkills().updateLevel(Skills.ATTACK, -lower, 0);
            lower = (int) (victim.getSkills().getStaticLevel(Skills.STRENGTH) * 0.05) + 1;
            victim.getSkills().updateLevel(Skills.STRENGTH, -lower, 0);
        }
        state.setEstimatedHit(hit);
        return hit;
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        entity.visualize(ANIMATION, GRAPHICS);
        playGlobalAudio(entity.getLocation(), Sounds.DARKLIGHT_WEAKEN_225);
    }
}
