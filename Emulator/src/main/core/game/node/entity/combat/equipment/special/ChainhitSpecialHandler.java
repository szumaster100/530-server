package core.game.node.entity.combat.equipment.special;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.*;
import core.game.node.entity.combat.ImpactHandler.HitsplatType;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.repository.Repository;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.tools.RandomFunction;
import org.rs.consts.Sounds;

import java.util.Iterator;
import java.util.List;

import static core.api.ContentAPIKt.playGlobalAudio;

@Initializable
public final class ChainhitSpecialHandler extends RangeSwingHandler implements Plugin<Object> {

    public ChainhitSpecialHandler() {
        super(SwingHandlerFlag.IGNORE_PRAYER_BOOSTS_DAMAGE);
    }

    private static final int SPECIAL_ENERGY = 10;

    private static final Animation ANIMATION = new Animation(1068, Priority.HIGH);

    private static final Graphics GRAPHICS = new Graphics(257, 96);

    private static final Location[] DOOR_SUPPORTS = new Location[]{Location.create(2545, 10145, 0), Location.create(2545, 10141, 0), Location.create(2543, 10143, 0)};

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        CombatStyle.RANGE.getSwingHandler().register(805, this);
        return this;
    }

    @Override
    public int swing(Entity entity, final Entity victim, BattleState state) {
        if (entity.getAttribute("chain-hit_v") != null) {
            ((Player) entity).getPacketDispatch().sendMessage("You're already using the chain-hit special attack.");
            ((Player) entity).getSettings().toggleSpecialBar();
            return -1;
        }
        if (!((Player) entity).getSettings().drainSpecial(SPECIAL_ENERGY)) {
            return -1;
        }
        state.setStyle(CombatStyle.RANGE);
        if (victim instanceof NPC) {
            NPC npc = victim.asNpc();
            if (npc.getId() == 2440) {
                for (Location l : DOOR_SUPPORTS) {
                    final NPC n = Repository.findNPC(l);
                    if (n == null) {
                        continue;
                    }
                    if (DeathTask.isDead(n) || n.getId() != n.getOriginalId()) {
                        continue;
                    }
                    int speed = (int) (32 + (victim.getLocation().getDistance(n.getLocation()) * 5));
                    Projectile.create(victim, n, 258, 40, 36, 32, speed, 5, 11).send();
                    n.getSkills().heal(100);
                    GameWorld.getPulser().submit(new Pulse(3) {

                        @Override
                        public boolean pulse() {
                            n.getImpactHandler().manualHit(victim, 1, HitsplatType.NORMAL);
                            return true;
                        }

                    });
                }
            }
        }
        return super.swing(entity, victim, state);
    }

    @Override
    public void visualize(Entity entity, Entity victim, BattleState state) {
        playGlobalAudio(entity.getLocation(), Sounds.CHAINSHOT_2528);
        entity.visualize(ANIMATION, GRAPHICS);
        int speed = (int) (32 + (entity.getLocation().getDistance(victim.getLocation()) * 5));
        Projectile.create(entity, victim, 258, 40, 36, 32, speed, 5, 11).send();
    }

    @Override
    public void visualizeImpact(Entity entity, Entity victim, BattleState state) {
        handleHit(entity, victim, (Player) entity, state);
        super.visualizeImpact(entity, victim, state);
    }

    @Override
    public void impact(Entity entity, Entity victim, BattleState state) {
        // Empty.
    }

    public void handleHit(final Entity entity, final Entity victim, final Player player, final BattleState state) {
        GameWorld.getPulser().submit(new Pulse(1, player, victim) {
            @Override
            public boolean pulse() {
                ChainhitSpecialHandler.super.onImpact(player, victim, state);
                ChainhitSpecialHandler.super.impact(entity, victim, state);
                return true;
            }
        });
        if (victim.getId() == 2440 || victim.getId() == 2446 || victim.getId() == 2443) {
            return;
        }
        if (victim.getProperties().isMultiZone() && player.getSettings().getSpecialEnergy() >= SPECIAL_ENERGY) {
            List<? extends Entity> list = getVictimsList(player, victim);
            for (Iterator<? extends Entity> it = list.iterator(); it.hasNext(); ) {
                final Entity e = it.next();
                it.remove();
                if (!e.isAttackable(player, CombatStyle.RANGE, false) || !e.getProperties().isMultiZone()) {
                    continue;
                }
                double distance = victim.getLocation().getDistance(e.getLocation());
                int speed = (int) (32 + (distance * 5));
                Projectile.create(victim, e, 258, 40, 36, 32, speed, 5, 11).send();
                GameWorld.getPulser().submit(new Pulse((int) (distance / 3), entity, victim, e) {
                    @Override
                    public boolean pulse() {
                        BattleState bs = new BattleState(entity, e);
                        bs.setMaximumHit(calculateHit(player, e, 1.0));
                        bs.setEstimatedHit(RandomFunction.random(bs.getMaximumHit() + 1));
                        handleHit(victim, e, player, bs);
                        ChainhitSpecialHandler.super.visualizeImpact(player, e, bs);
                        return true;
                    }
                });
                player.getSettings().setSpecialEnergy(player.getSettings().getSpecialEnergy() - SPECIAL_ENERGY);
                return;
            }
        }
        player.removeAttribute("chain-hit_v");
    }

    private List<? extends Entity> getVictimsList(Entity e, Entity victim) {
        List<? extends Entity> list = e.getAttribute("chain-hit_v");
        if (list == null) {
            int distance = 5;
            if (victim instanceof NPC) {
                e.setAttribute("chain-hit_v", list = RegionManager.getLocalNpcs(e, distance));
            } else {
                e.setAttribute("chain-hit_v", list = RegionManager.getLocalPlayers(e, distance));
            }
            list.remove(e);
            list.remove(victim);
        }
        return list;
    }
}
