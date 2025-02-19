package content.global.skill.slayer.npc;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.CombatSwingHandler;
import core.game.node.entity.combat.MultiSwingHandler;
import core.game.node.entity.combat.equipment.SwitchAttack;
import core.game.node.entity.combat.equipment.special.DragonfireSwingHandler;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.npc.AbstractNPC;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import org.rs.consts.NPCs;

@Initializable
public class BrutalDragonNPC extends AbstractNPC {

    public BrutalDragonNPC() {
        super(0, null);
    }

    private static final SwitchAttack DRAGONFIRE = DragonfireSwingHandler.get(
            false, 52, new Animation(81, Priority.HIGH), Graphics.create(1), null, null
    );

    private final CombatSwingHandler combatAction = new MultiSwingHandler(
            true,
            new SwitchAttack(CombatStyle.MELEE.getSwingHandler(), new Animation(80, Priority.HIGH)),
            new SwitchAttack(CombatStyle.MELEE.getSwingHandler(), new Animation(80, Priority.HIGH)),
            new SwitchAttack(CombatStyle.MAGIC.getSwingHandler(), new Animation(81, Priority.HIGH), null, null, Projectile.create((Entity) null, null, 500, 20, 20, 41, 40, 18, 255)),
            DRAGONFIRE
    );

    public BrutalDragonNPC(int id, Location location) {
        super(id, location);
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        return new BrutalDragonNPC(id, location);
    }

    @Override
    public CombatSwingHandler getSwingHandler(boolean swing) {
        return combatAction;
    }

    @Override
    public int getDragonfireProtection(boolean fire) {
        return 0x2 | 0x4 | 0x8;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.BRUTAL_GREEN_DRAGON_5362};
    }
}
