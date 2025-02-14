package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.player.Player;
import org.rs.consts.Items;
import org.rs.consts.NPCs;

public class SpiritPengatriceNPC extends content.global.skill.summoning.familiar.Familiar {

    public SpiritPengatriceNPC() {
        this(null, NPCs.SPIRIT_PENGATRICE_6883);
    }

    public SpiritPengatriceNPC(Player owner, int id) {
        super(owner, id, 3600, Items.SP_PENGATRICE_POUCH_12103, 3);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new SpiritPengatriceNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        return false;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.SPIRIT_PENGATRICE_6883, NPCs.SPIRIT_PENGATRICE_6884};
    }

}