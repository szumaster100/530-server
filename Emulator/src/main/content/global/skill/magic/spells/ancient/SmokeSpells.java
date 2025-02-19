package content.global.skill.magic.spells.ancient;

import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.spell.CombatSpell;
import core.game.node.entity.combat.spell.Runes;
import core.game.node.entity.combat.spell.SpellType;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.player.link.SpellBookManager.SpellBook;
import core.game.node.item.Item;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.plugin.Plugin;
import org.rs.consts.Sounds;

import java.util.List;

import static core.api.ContentAPIKt.playGlobalAudio;
import static core.api.event.EventAPIKt.applyPoison;

@Initializable
public final class SmokeSpells extends CombatSpell {

    private static final Projectile RUSH_PROJECTILE = Projectile.create((Entity) null, null, 384, 40, 36, 52, 75, 15, 11);
    private static final Graphics RUSH_END = new Graphics(385, 96);
    private static final Projectile BURST_PROJECTILE = Projectile.create((Entity) null, null, 386, 40, 36, 52, 75, 15, 11);
    private static final Graphics BURST_END = new Graphics(387, 0);
    private static final Projectile BLITZ_PROJECTILE = Projectile.create((Entity) null, null, 389, 40, 36, 52, 75, 15, 11);
    private static final Graphics BLITZ_END = new Graphics(388, 96);
    private static final Projectile BARRAGE_PROJECTILE = Projectile.create((Entity) null, null, 391, 40, 36, 52, 75, 15, 11);
    private static final Graphics BARRAGE_END = new Graphics(390, 0);

    public SmokeSpells() {

    }

    private SmokeSpells(SpellType type, int level, double baseExperience, int sound, int impactSound, Animation anim, Graphics start, Projectile projectile, Graphics end, Item... runes) {
        super(type, SpellBook.ANCIENT, level, baseExperience, sound, impactSound, anim, start, projectile, end, runes);
    }

    @Override
    public Plugin<SpellType> newInstance(SpellType arg) throws Throwable {
        SpellBook.ANCIENT.register(8, new SmokeSpells(SpellType.RUSH, 50, 30.0, Sounds.SMOKE_CAST_183, Sounds.SMOKE_RUSH_IMPACT_185, new Animation(1978, Priority.HIGH), null, RUSH_PROJECTILE, RUSH_END, Runes.DEATH_RUNE.getItem(2), Runes.CHAOS_RUNE.getItem(2), Runes.FIRE_RUNE.getItem(1), Runes.AIR_RUNE.getItem(1)));
        SpellBook.ANCIENT.register(10, new SmokeSpells(SpellType.BURST, 62, 36.0, Sounds.SMOKE_CAST_183, Sounds.SMOKE_BURST_IMPACT_182, new Animation(1979, Priority.HIGH), null, BURST_PROJECTILE, BURST_END, Runes.DEATH_RUNE.getItem(2), Runes.CHAOS_RUNE.getItem(4), Runes.FIRE_RUNE.getItem(2), Runes.AIR_RUNE.getItem(2)));
        SpellBook.ANCIENT.register(9, new SmokeSpells(SpellType.BLITZ, 74, 42.0, Sounds.SMOKE_CAST_183, Sounds.SMOKE_BLITZ_IMPACT_181, new Animation(1978, Priority.HIGH), null, BLITZ_PROJECTILE, BLITZ_END, Runes.BLOOD_RUNE.getItem(2), Runes.DEATH_RUNE.getItem(2), Runes.FIRE_RUNE.getItem(2), Runes.AIR_RUNE.getItem(2)));
        SpellBook.ANCIENT.register(11, new SmokeSpells(SpellType.BARRAGE, 86, 48.0, Sounds.SMOKE_CAST_183, Sounds.SMOKE_BARRAGE_IMPACT_180, new Animation(1979, Priority.HIGH), null, BARRAGE_PROJECTILE, BARRAGE_END, Runes.BLOOD_RUNE.getItem(2), Runes.DEATH_RUNE.getItem(4), Runes.FIRE_RUNE.getItem(4), Runes.AIR_RUNE.getItem(4)));
        return this;
    }

    @Override
    public void visualize(Entity entity, Node target) {
        entity.graphics(graphics);
        if (projectile != null) {
            projectile.transform(entity, (Entity) target, false, 58, 10).send();
        }
        entity.animate(animation);
        playGlobalAudio(entity.getLocation(), audio.id, 20);
    }

    @Override
    public void fireEffect(Entity entity, Entity victim, BattleState state) {
        if (state.getEstimatedHit() > -1) {
            applyPoison(victim, entity, type.ordinal() >= SpellType.BLITZ.ordinal() ? 4 : 2);
        }
    }

    @Override
    public BattleState[] getTargets(Entity entity, Entity target) {
        if (animation.getId() == 1978 || !entity.getProperties().isMultiZone() || !target.getProperties().isMultiZone()) {
            return super.getTargets(entity, target);
        }
        List<Entity> list = getMultihitTargets(entity, target, 9);
        BattleState[] targets = new BattleState[list.size()];
        int index = 0;
        for (Entity e : list) {
            targets[index++] = new BattleState(entity, e);
        }
        return targets;
    }

    @Override
    public int getMaximumImpact(Entity entity, Entity victim, BattleState state) {
        return getType().getImpactAmount(entity, victim, 1);
    }

}
