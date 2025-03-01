package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import content.global.skill.summoning.familiar.Forager;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.ClassScanner;
import core.plugin.Initializable;
import core.plugin.Plugin;
import org.rs.consts.Items;

@Initializable
public final class CockatriceFamiliarNPC implements Plugin<Object> {

    private static final Item COCKATRICE_EGG = new Item(Items.COCKATRICE_EGG_12109);

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ClassScanner.definePlugin(new SpiritCockatrice());
        ClassScanner.definePlugin(new SpiritGuthatrice());
        ClassScanner.definePlugin(new SpiritZamatrice());
        ClassScanner.definePlugin(new SpiritPengatrice());
        ClassScanner.definePlugin(new SpiritCoraxatrice());
        ClassScanner.definePlugin(new SpiritVulatrice());
        ClassScanner.definePlugin(new SpiritSaratrice());
        return this;
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    public boolean petrifyingGaze(final Familiar familiar, final FamiliarSpecial special, final int skill) {
        final Entity target = special.getTarget();
        if (!familiar.canCombatSpecial(target)) {
            return false;
        }
        familiar.faceTemporary(target, 2);
        familiar.visualize(Animation.create(7762), Graphics.create(1467));
        GameWorld.getPulser().submit(new Pulse(1, familiar.getOwner(), familiar, target) {
            @Override
            public boolean pulse() {
                if(skill == 5) {
                    target.skills.decrementPrayerPoints(3);
                }else {
                    target.getSkills().updateLevel(skill, -3, 0);
                }
                Projectile.magic(familiar, target, 1468, 40, 36, 71, 10).send();
                familiar.sendFamiliarHit(target, 10, Graphics.create(1469));
                return true;
            }
        });
        return true;
    }

    public final class SpiritCockatrice extends Forager {

        public SpiritCockatrice() {
            this(null, 6875);
        }

        public SpiritCockatrice(Player owner, int id) {
            super(owner, id, 3600, 12095, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritCockatrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.DEFENCE);
        }

        @Override
        public int[] getIds() {
            return new int[]{6875, 6876};
        }

    }

    public class SpiritGuthatrice extends Forager {

        public SpiritGuthatrice() {
            this(null, 6877);
        }

        public SpiritGuthatrice(Player owner, int id) {
            super(owner, id, 3600, 12097, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritGuthatrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.ATTACK);
        }

        @Override
        public int[] getIds() {
            return new int[]{6877, 6878};
        }
    }

    public class SpiritZamatrice extends Forager {

        public SpiritZamatrice() {
            this(null, 6881);
        }

        public SpiritZamatrice(Player owner, int id) {
            super(owner, id, 3600, 12101, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritZamatrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.STRENGTH);
        }

        @Override
        public int[] getIds() {
            return new int[]{6881, 6882};
        }
    }

    public class SpiritPengatrice extends Forager {

        public SpiritPengatrice() {
            this(null, 6883);
        }

        public SpiritPengatrice(Player owner, int id) {
            super(owner, id, 3600, 12103, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritPengatrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.MAGIC);
        }

        @Override
        public int[] getIds() {
            return new int[]{6883, 6884};
        }
    }

    public class SpiritCoraxatrice extends Forager {

        public SpiritCoraxatrice() {
            this(null, 6885);
        }

        public SpiritCoraxatrice(Player owner, int id) {
            super(owner, id, 3600, 12105, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritCoraxatrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.SUMMONING);
        }

        @Override
        public int[] getIds() {
            return new int[]{6885, 6886};
        }

    }

    public class SpiritVulatrice extends Forager {

        public SpiritVulatrice() {
            this(null, 6887);
        }

        public SpiritVulatrice(Player owner, int id) {
            super(owner, id, 3600, 12107, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritVulatrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.RANGE);
        }

        @Override
        public int[] getIds() {
            return new int[]{6887, 6888};
        }

    }

    public class SpiritSaratrice extends Forager {

        public SpiritSaratrice() {
            this(null, 6879);
        }

        public SpiritSaratrice(Player owner, int id) {
            super(owner, id, 3600, 12099, 3, WeaponInterface.STYLE_CAST, COCKATRICE_EGG);
        }

        @Override
        public Familiar construct(Player owner, int id) {
            return new SpiritSaratrice(owner, id);
        }

        @Override
        protected boolean specialMove(FamiliarSpecial special) {
            return petrifyingGaze(this, special, Skills.PRAYER);
        }

        @Override
        public int[] getIds() {
            return new int[] { 6879, 6880 };
        }

    }

}
