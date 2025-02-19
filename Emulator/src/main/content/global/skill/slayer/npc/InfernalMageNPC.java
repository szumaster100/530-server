package content.global.skill.slayer.npc;

import content.global.skill.slayer.Tasks;
import core.game.node.entity.combat.spell.CombatSpell;
import core.game.node.entity.npc.AbstractNPC;
import core.game.node.entity.player.link.SpellBookManager.SpellBook;
import core.game.world.map.Location;
import core.plugin.Initializable;

@Initializable
public class InfernalMageNPC extends AbstractNPC {

    public InfernalMageNPC() {
        super(0, null);
    }

    public InfernalMageNPC(int id, Location location) {
        super(id, location);
    }

    @Override
    public void init() {
        super.init();
        getProperties().setAutocastSpell((CombatSpell) SpellBook.MODERN.getSpell(8));
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        return new InfernalMageNPC(id, location);
    }

    @Override
    public int[] getIds() {
        return Tasks.INFERNAL_MAGES.getNpcs();
    }
}
