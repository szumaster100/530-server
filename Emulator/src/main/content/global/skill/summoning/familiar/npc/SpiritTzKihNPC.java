package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import org.rs.consts.Items;
import org.rs.consts.NPCs;

import java.util.List;

@Initializable
public class SpiritTzKihNPC extends content.global.skill.summoning.familiar.Familiar {

    public SpiritTzKihNPC() {
        this(null, NPCs.SPIRIT_TZ_KIH_7361);
    }

    public SpiritTzKihNPC(Player owner, int id) {
        super(owner, id, 1800, Items.SPIRIT_TZ_KIH_POUCH_12808, 6, WeaponInterface.STYLE_CAST);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new SpiritTzKihNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        final List<Entity> entitys = RegionManager.getLocalEntitys(owner, 8);
        if (entitys.size() == 0) {
            return false;
        }
        boolean success = false;
        Entity target = null;
        for (int i = 0; i < 2; i++) {
            if (entitys.size() >= i) {
                target = entitys.get(i);
                if (target == null || target == this || target == owner) {
                    continue;
                }
                if (!canCombatSpecial(target)) {
                    continue;
                }
                success = true;
                sendFamiliarHit(target, 7, Graphics.create(1329));
            }
        }
        if (success) {
            animate(Animation.create(8257));
            return true;
        }
        return false;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.SPIRIT_TZ_KIH_7361, NPCs.SPIRIT_TZ_KIH_7362};
    }

}
