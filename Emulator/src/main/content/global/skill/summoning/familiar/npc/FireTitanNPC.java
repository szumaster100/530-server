package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

@Initializable
public class FireTitanNPC extends ElementalTitanNPC {

    public FireTitanNPC() {
        this(null, 7355);
    }

    public FireTitanNPC(Player owner, int id) {
        super(owner, id, 6200, 12802, 20, WeaponInterface.STYLE_CAST);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new FireTitanNPC(owner, id);
    }

    @Override
    public int[] getIds() {
        return new int[]{7355, 7356};
    }

}
