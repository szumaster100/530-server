package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

@Initializable
public class PrayingMantisNPC extends content.global.skill.summoning.familiar.Familiar {

    public PrayingMantisNPC() {
        this(null, 6798);
    }

    public PrayingMantisNPC(Player owner, int id) {
        super(owner, id, 6900, 12011, 6, WeaponInterface.STYLE_ACCURATE);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new PrayingMantisNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        return false;
    }

    @Override
    public int[] getIds() {
        return new int[]{6798, 6799};
    }

}
