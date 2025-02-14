package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

@Initializable
public class MossTitanNPC extends ElementalTitanNPC {

    public MossTitanNPC() {
        this(null, 7357);
    }

    public MossTitanNPC(Player owner, int id) {
        super(owner, id, 5800, 12804, 20, WeaponInterface.STYLE_AGGRESSIVE);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new MossTitanNPC(owner, id);
    }

    @Override
    public int[] getIds() {
        return new int[]{7357, 7358};
    }

}
