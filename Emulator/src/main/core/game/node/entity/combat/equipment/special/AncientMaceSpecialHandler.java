package core.game.node.entity.combat.equipment.special;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.MeleeSwingHandler;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.player.Player;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.tools.RandomFunction;
import org.rs.consts.Sounds;

import static core.api.ContentAPIKt.playGlobalAudio;

@Initializable
public final class AncientMaceSpecialHandler extends MeleeSwingHandler implements Plugin<Object> {

    private static final int SPECIAL_ENERGY = 100;

    private static final Animation ANIMATION = new Animation(6147, Priority.HIGH);

    private static final Graphics GRAPHICS = new Graphics(1052);

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        CombatStyle.MELEE.getSwingHandler().register(11061, this);
        return this;
    }

    @Override
    public int swing(Entity entity, Entity victim, BattleState state) {
        if (!((Player) entity).getSettings().drainSpecial(SPECIAL_ENERGY)) {
            return -1;
        }
        state.setStyle(CombatStyle.MELEE);
        int hit = 0;
        if (isAccurateImpact(entity, victim, CombatStyle.MELEE, 1.1, 0.98)) {
            hit = RandomFunction.random(calculateHit(entity, victim, 1) + 1);
            if (entity.getSkills().getPrayerPoints() < entity.getSkills().getStaticLevel(5)) {
                entity.getSkills().setPrayerPoints(entity.getSkills().getPrayerPoints() + hit);
            }
            victim.getSkills().decrementPrayerPoints(hit);
        }
        state.setEstimatedHit(hit);
        return 1;
    }

    @Override
    protected int getFormattedHit(Entity entity, Entity victim, BattleState state, int hit) {
        // Disables protection prayers.
        return formatHit(victim, hit);
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        playGlobalAudio(entity.getLocation(), Sounds.GOBLIN_MACE_3592);
        entity.visualize(ANIMATION, GRAPHICS);
    }
}
