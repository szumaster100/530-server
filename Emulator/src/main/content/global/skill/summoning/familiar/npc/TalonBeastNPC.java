package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;
import org.rs.consts.Items;
import org.rs.consts.NPCs;

@Initializable
public class TalonBeastNPC extends content.global.skill.summoning.familiar.Familiar {

    public TalonBeastNPC() {
        this(null, NPCs.TALON_BEAST_7347);
    }

    public TalonBeastNPC(Player owner, int id) {
        super(owner, id, 4900, Items.TALON_BEAST_POUCH_12794, 6, WeaponInterface.STYLE_AGGRESSIVE);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new TalonBeastNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        return false;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.TALON_BEAST_7347, NPCs.TALON_BEAST_7348};
    }

}
