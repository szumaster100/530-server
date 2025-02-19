package content.region.wilderness.handlers.npc;

import org.rs.consts.NPCs;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.ImpactHandler.HitsplatType;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.npc.AbstractNPC;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.map.path.Path;
import core.game.world.map.path.Pathfinder;
import core.plugin.Initializable;
import core.tools.RandomFunction;

import static core.api.event.EventAPIKt.isPoisoned;
import static core.api.event.EventAPIKt.isStunned;

@Initializable
public final class DarkEnergyCoreNPC extends AbstractNPC {
    private NPC master;
    private int ticks = 0;
    private int fails = 0;
    public DarkEnergyCoreNPC() {
        this(8127, null);
    }

    public DarkEnergyCoreNPC(int id, Location location) {
        super(id, location, false);
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        DarkEnergyCoreNPC core = new DarkEnergyCoreNPC(id, location);
        if (objects.length > 0) {
            core.master = (NPC) objects[0];
        }
        core.setRespawn(false);
        return core;
    }

    @Override
    public boolean canStartCombat(Entity victim) {
        return false;
    }

    @Override
    public void handleTickActions() {
        ticks++;
        boolean poisoned = isPoisoned(this);
        if (isStunned(this) || isInvisible()) {
            return;
        }
        if (fails == 0 && poisoned && (ticks % 100) != 0) {
            return;
        }
        if (ticks % 2 == 0) {
            boolean jump = true;
            for (Player player : getViewport().getCurrentPlane().getPlayers()) {
                if (player.getLocation().withinDistance(getLocation(), 1)) {
                    jump = false;
                    int hit = 5 + RandomFunction.random(6);
                    player.getImpactHandler().manualHit(master, hit, HitsplatType.NORMAL);
                    master.getSkills().heal(hit);
                    player.getPacketDispatch().sendMessage("The dark core creature steals some life from you for its master.");
                }
            }
            if (jump) {
                Entity victim = master.getProperties().getCombatPulse().getVictim();
                if (++fails >= 3 && victim != null && victim.getViewport().getCurrentPlane() == getViewport().getCurrentPlane()) {
                    Path path = Pathfinder.find(getLocation(), victim.getLocation(), 1);
                    if (path.isSuccessful() || !path.isMoveNear()) {
                        jump(victim.getLocation());
                        fails = 0;
                    }
                }
            } else {
                fails = 0;
            }
        }
    }

    private void jump(final Location location) {
        setInvisible(true);
        Projectile.create(getLocation(), location, 1828, 0, 0, 0, 60, 20, 0).send();
        GameWorld.getPulser().submit(new Pulse(2, this) {
            @Override
            public boolean pulse() {
                getProperties().setTeleportLocation(location);
                setInvisible(false);
                return true;
            }
        });
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.DARK_ENERGY_CORE_8127};
    }
}
