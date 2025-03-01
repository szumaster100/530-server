package content.global.skill.slayer.npc;

import content.global.skill.slayer.items.MirrorShieldHandler;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatSwingHandler;
import core.game.node.entity.npc.AbstractNPC;
import core.game.world.map.Location;
import core.plugin.Initializable;
import org.rs.consts.NPCs;

@Initializable
public class CockatriceNPC extends AbstractNPC {

    public CockatriceNPC() {
        super(0, null);
    }

    public CockatriceNPC(int id, Location location) {
        super(id, location);
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        return new CockatriceNPC(id, location);
    }

    @Override
    public CombatSwingHandler getSwingHandler(boolean swing) {
        return MirrorShieldHandler.SINGLETON;
    }

    @Override
    public void checkImpact(BattleState state) {
        super.checkImpact(state);
        MirrorShieldHandler.SINGLETON.checkImpact(state);
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.COCKATRICE_1620, NPCs.COCKATRICE_1621};
    }
}
