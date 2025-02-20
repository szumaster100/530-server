package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.player.Player;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.tools.RandomFunction;

@Initializable
public class GeyserTitanNPC extends content.global.skill.summoning.familiar.Familiar {

    public GeyserTitanNPC() {
        this(null, 7339);
    }

    public GeyserTitanNPC(Player owner, int id) {
        super(owner, id, 6900, 12786, 6, WeaponInterface.STYLE_RANGE_ACCURATE);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new GeyserTitanNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        if (!canCombatSpecial(special.getTarget())) {
            return false;
        }
        Entity target = special.getTarget();
        visualize(new Animation(7883), new Graphics(1375, 315));
        int maxHit = 30;
        int defBonus = 0;
        for (int i = 5; i < 11; i++) {
            defBonus += target.getProperties().getBonuses()[i];
        }
        maxHit = defBonus / 40;
        if (maxHit <= 1) {
            maxHit = RandomFunction.random(0, 3);
        }
        if (maxHit > 30) {
            maxHit = RandomFunction.random(20, 30);
        }
        Projectile.ranged(this, special.getTarget(), 1376, 300, 30, 0, 45).send();
        super.sendFamiliarHit(special.getTarget(), maxHit, Graphics.create(1377));
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{7339, 7340};
    }

}
