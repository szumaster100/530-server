package core.game.node.entity;

import core.game.event.*;
import core.game.interaction.DestinationFlag;
import core.game.interaction.MovementPulse;
import core.game.interaction.ScriptProcessor;
import core.game.node.Node;
import core.game.node.entity.combat.*;
import core.game.node.entity.combat.equipment.ArmourSet;
import core.game.node.entity.impl.Properties;
import core.game.node.entity.impl.*;
import core.game.node.entity.lock.ActionLocks;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.TeleportManager;
import core.game.node.entity.skill.Skills;
import core.game.system.task.Pulse;
import core.game.system.timer.TimerManager;
import core.game.system.timer.TimerRegistry;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.map.Viewport;
import core.game.world.map.path.Path;
import core.game.world.map.path.Pathfinder;
import core.game.world.map.zone.ZoneMonitor;
import core.game.world.map.zone.ZoneRestriction;
import core.game.world.update.UpdateMasks;
import core.game.world.update.flag.EFlagType;
import core.game.world.update.flag.EntityFlag;
import core.game.world.update.flag.EntityFlags;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Entity extends Node {

    private final Properties properties = new Properties(this);

    private final UpdateMasks updateMasks = new UpdateMasks(this);

    private final WalkingQueue walkingQueue = new WalkingQueue(this);

    public Skills skills = new Skills(this);

    private final Map<Class<?>, Object> extensions = new HashMap<Class<?>, Object>();

    private final GameAttributes attributes = new GameAttributes();

    private final Viewport viewport = new Viewport();

    private final PulseManager pulseManager = new PulseManager();

    private final ImpactHandler impactHandler = new ImpactHandler(this);

    private final Animator animator = new Animator(this);

    private final TeleportManager teleporter = new TeleportManager(this);

    private final ZoneMonitor zoneMonitor = new ZoneMonitor(this);

    private final ActionLocks locks = new ActionLocks();

    public ScriptProcessor scripts = new ScriptProcessor(this);

    public final int[] clocks = new int[10];

    public MovementPulse currentMovement;

    private HashMap<Class<?>, ArrayList<EventHook>> hooks = new HashMap<>();

    public TimerManager timers = new TimerManager(this);

    private boolean invisible;

    public Entity(String name, Location location) {
        super(name, location);
        super.destinationFlag = DestinationFlag.ENTITY;
    }

    public void moveStep() {
        if (locks.isMovementLocked()) {
            return;
        }
        Path path;
        if (!(path = Pathfinder.find(this, getLocation().transform(-1, 0, 0), false, Pathfinder.DUMB)).isSuccessful()) {
            if (!(path = Pathfinder.find(this, getLocation().transform(0, -1, 0), false, Pathfinder.DUMB)).isSuccessful()) {
                if (!(path = Pathfinder.find(this, getLocation().transform(1, 0, 0), false, Pathfinder.DUMB)).isSuccessful()) {
                    if (!(path = Pathfinder.find(this, getLocation().transform(0, 1, 0), false, Pathfinder.DUMB)).isSuccessful()) {
                        path = null;
                    }
                }
            }
        }
        if (path != null) {
            path.walk(this);
        }
    }

    public void dispatch(Event event) {
        if (this.hooks.containsKey(event.getClass())) {
            ArrayList<EventHook> processList = new ArrayList(this.hooks.get(event.getClass()));
            processList.forEach((hook) -> {
                hook.process(this, event);
            });
        }
    }

    public void unhook(EventHook hook) {
        for (ArrayList<EventHook> s : hooks.values()) s.remove(hook);
    }

    public void hook(Event event, EventHook hook) {
        hook(event.getClass(), hook);
    }

    public void hook(Class<?> event, EventHook hook) {
        ArrayList<EventHook> hookList;
        if (hooks.get(event) != null) {
            hookList = hooks.get(event);
        } else {
            hookList = new ArrayList<EventHook>();
        }
        if (!hookList.contains(hook))
            hookList.add(hook);
        hooks.put(event, hookList);
    }

    public void init() {
        active = true;
        TimerRegistry.addAutoTimers(this);
    }

    public void tick() {
        scripts.preMovement();
        dispatch(new TickEvent(GameWorld.getTicks()));
        skills.pulse();
        Location old = location != null ? location.transform(0, 0, 0) : Location.create(0, 0, 0);
        walkingQueue.update();
        scripts.postMovement(!Objects.equals(location, old));
        timers.processTimers();
        updateMasks.prepare(this);
    }

    public void update() {
    }

    public void reset() {
        updateMasks.reset();
        properties.setTeleporting(false);
    }

    public void clear() {
        active = false;
        viewport.remove(this);
        pulseManager.clear();
    }

    public boolean inCombat() {
        return getAttribute("combat-time", 0L) > System.currentTimeMillis();
    }

    public void fullRestore() {
        skills.restore();
        timers.removeTimer("poison");
        timers.removeTimer("poison:immunity");
        timers.removeTimer("disease");
    }

    public void commenceDeath(Entity killer) {
    }

    public void finalizeDeath(Entity killer) {
        skills.restore();
        skills.rechargePrayerPoints();
        impactHandler.getImpactQueue().clear();
        impactHandler.setDisabledTicks(10);
        timers.onEntityDeath();
        removeAttribute("combat-time");
        face(null);
        //Check if it's a Loar shade and transform back into the shadow version.
        if (this.getId() == 1240 || this.getId() == 1241) {
            this.asNpc().transform(1240);
        }
    }

    public void updateLocation(Location last) {

    }

    public boolean isIgnoreMultiBoundaries(Entity victim) {
        if (this instanceof NPC) {
            return ((NPC) this).behavior.shouldIgnoreMultiRestrictions((NPC) this, victim);
        }
        return false;
    }

    public boolean shouldPreventStacking(Entity mover) {
        return false;
    }

    public void checkImpact(BattleState state) {
        getProperties().getCombatPulse().setLastReceivedAttack(GameWorld.getTicks());
        int ticks = GameWorld.getTicks() - getProperties().getCombatPulse().getLastSentAttack();
        if (ticks > 10 && this instanceof NPC && ((NPC) this).getDefinition().getConfiguration("safespot", false)) {
            Pathfinder.find(state.getAttacker(), getLocation()).walk(state.getAttacker());
            Pathfinder.find(state.getVictim(), state.getAttacker().getLocation()).walk(state.getVictim());
            if (ticks > 40) {
                state.getAttacker().moveStep();
                state.getAttacker().getProperties().getCombatPulse().stop();
                state.getVictim().moveStep();
                state.getVictim().getProperties().getCombatPulse().stop();
            }
        }
    }

    public void onImpact(final Entity entity, BattleState state) {
        if (DeathTask.isDead(this))
            state.neutralizeHits();
        if (this instanceof NPC) {
            ((NPC) this).behavior.afterDamageReceived((NPC) this, entity, state);
        }
        if (properties.isRetaliating() && !properties.getCombatPulse().isAttacking() && !getLocks().isInteractionLocked() && properties.getCombatPulse().getNextAttack() < GameWorld.getTicks()) {
            if (!getWalkingQueue().hasPath() && !getPulseManager().isMovingPulse() || (this instanceof NPC)) {
                properties.getCombatPulse().attack(entity);
            }
        }
    }

    public void onAttack(final Entity target) {

    }

    public void attack(final Node node) {
        getProperties().getCombatPulse().attack(node);
    }

    public boolean canMove(Location destination) {
        return true;
    }

    public void teleport(Location location) {
        getProperties().setTeleportLocation(location);
    }

    public void teleport(final Location location, int ticks) {
        GameWorld.getPulser().submit(new Pulse(ticks, this) {
            @Override
            public boolean pulse() {
                teleport(location);
                return true;
            }
        });
    }

    public void lock() {
        locks.lock();
    }

    public void lock(int time) {
        locks.lock(time);
    }

    public void unlock() {
        locks.unlock();
    }

    public abstract boolean hasProtectionPrayer(CombatStyle style);

    public abstract int getDragonfireProtection(boolean fire);

    public boolean isAttackable(Entity entity, CombatStyle style, boolean message) {
        if (DeathTask.isDead(this)) {
            return false;
        }
        if (!entity.getZoneMonitor().continueAttack(this, style, message)) {
            return false;
        }
        return true;
    }

    public boolean graphics(Graphics graphics) {
        return animator.graphics(graphics);
    }

    public boolean animate(Animation animation) {
        return animator.animate(animation);
    }

    public boolean graphics(final Graphics graphics, int delay) {
        GameWorld.getPulser().submit(new Pulse(delay, this) {
            @Override
            public boolean pulse() {
                graphics(graphics);
                return true;
            }
        });
        return true;
    }

    public boolean continueAttack(Entity target, CombatStyle style, boolean message) {
        return true;
    }

    public boolean animate(final Animation animation, int delay) {
        GameWorld.getPulser().submit(new Pulse(delay, this) {
            @Override
            public boolean pulse() {
                animate(animation);
                return true;
            }
        });
        return true;
    }

    public void sendImpact(BattleState state) {
        getProperties().getCombatPulse().setLastSentAttack(GameWorld.getTicks());
    }

    public boolean canSelectTarget(Entity target) {
        return true;
    }

    public boolean visualize(Animation animation, Graphics graphics) {
        return animator.animate(animation, graphics);
    }

    public boolean faceTemporary(Entity entity, int ticks) {
        return faceTemporary(entity, null, ticks);
    }

    public boolean faceTemporary(Entity entity, final Entity reset, int ticks) {
        if (face(entity)) {
            GameWorld.getPulser().submit(new Pulse(ticks + 1, this) {
                @Override
                public boolean pulse() {
                    face(reset);
                    return true;
                }
            });
            return true;
        }
        return false;
    }

    public double getFormattedHit(BattleState state, int hit) {
        if (state.getAttacker() == null || state.getVictim() == null || state.getStyle() == null) {
            return hit;
        }
        Entity entity = state.getAttacker();
        Entity victim = state.getVictim();
        CombatStyle type = state.getStyle();
        if (state.getArmourEffect() != ArmourSet.VERAC && !entity.isIgnoreProtection(type) && victim.hasProtectionPrayer(type)) {
            return hit *= (entity instanceof Player && victim instanceof Player) ? 0.6 : 0;
        }
        return hit;
    }

    public boolean isIgnoreProtection(CombatStyle style) {
        return false;
    }

    public void startDeath(Entity killer) {
        if (zoneMonitor.startDeath(this, killer)) {
            DeathTask.startDeath(this, killer);
        }
    }

    public Player asPlayer() {
        return (Player) this;
    }

    public boolean isPlayer() {
        return this instanceof Player;
    }

    public boolean face(Entity entity) {
        if (entity == null) {
            int ordinal = EntityFlags.getOrdinal(EFlagType.of(this), EntityFlag.FaceEntity);
            if (getUpdateMasks().unregisterSynced(ordinal)) {
                return getUpdateMasks().register(EntityFlag.FaceEntity, null);
            }
            return true;
        }
        return getUpdateMasks().register(EntityFlag.FaceEntity, entity, true);
    }

    public boolean faceLocation(Location location) {
        if (location == null) {
            int ordinal = EntityFlags.getOrdinal(EFlagType.of(this), EntityFlag.FaceLocation);
            getUpdateMasks().unregisterSynced(ordinal);
            return true;
        }
        return getUpdateMasks().register(EntityFlag.FaceLocation, location, true);
    }

    public boolean sendChat(String string) {
        return getUpdateMasks().register(EntityFlag.ForceChat, string);
    }

    public abstract CombatSwingHandler getSwingHandler(boolean swing);

    public abstract boolean isPoisonImmune();

    public void sendChat(final String string, int ticks) {
        GameWorld.getPulser().submit(new Pulse(ticks, this) {
            @Override
            public boolean pulse() {
                sendChat(string);
                return true;
            }
        });
    }

    public double getLevelMod(Entity entity, Entity victim) {
        return 0;
    }

    public ActionLocks getLocks() {
        return locks;
    }

    public int getClientIndex() {
        return index;
    }

    public Properties getProperties() {
        return properties;
    }

    public UpdateMasks getUpdateMasks() {
        return updateMasks;
    }

    public void addExtension(Class<?> clazz, Object object) {
        extensions.put(clazz, object);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<?> clazz) {
        return (T) extensions.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<?> clazz, T fail) {
        T extension = (T) extensions.get(clazz);
        if (extension == null) {
            return fail;
        }
        return extension;
    }

    public void removeExtension(Class<?> clazz) {
        extensions.remove(clazz);
    }

    public Map<String, Object> getAttributes() {
        return attributes.getAttributes();
    }

    public void clearAttributes() {
        this.attributes.getAttributes().clear();
        this.attributes.getSavedAttributes().clear();
    }

    public void setAttribute(String key, Object value) {
        attributes.setAttribute(key, value);
        dispatch(new AttributeSetEvent(this, key, value));
    }

    public <T> T getAttribute(String key) {
        return attributes.getAttribute(key);
    }

    public void incrementAttribute(String key) {
        incrementAttribute(key, 1);
    }

    public void incrementAttribute(String key, int amount) {
        attributes.setAttribute(key, attributes.getAttribute(key, 0) + amount);
    }

    public <T> T getAttribute(String string, T fail) {
        return attributes.getAttribute(string, fail);
    }

    public void removeAttribute(String string) {
        attributes.removeAttribute(string);
        dispatch(new AttributeRemoveEvent(this, string));
    }

    public GameAttributes getGameAttributes() {
        return attributes;
    }

    public WalkingQueue getWalkingQueue() {
        return walkingQueue;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Skills getSkills() {
        return skills;
    }

    public PulseManager getPulseManager() {
        return pulseManager;
    }

    public ImpactHandler getImpactHandler() {
        return impactHandler;
    }

    public Animator getAnimator() {
        return animator;
    }

    public TeleportManager getTeleporter() {
        return teleporter;
    }

    public ZoneMonitor getZoneMonitor() {
        return zoneMonitor;
    }

    public boolean hasFireResistance() {
        return getAttribute("fire:immune", 0) >= GameWorld.getTicks();
    }

    public boolean isTeleBlocked() {
        return timers.getTimer("teleblock") != null || getLocks().isTeleportLocked() || getZoneMonitor().isRestricted(ZoneRestriction.TELEPORT);
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public Location getClosestOccupiedTile(@NotNull Location other) {
        List<Location> occupied = getOccupiedTiles();

        Location closest = location;
        if (occupied.size() > 1) {
            double lowest = 9999;
            for (Location tile : occupied) {
                double dist = tile.getDistance(other);
                if (dist < lowest) {
                    lowest = dist;
                    closest = tile;
                }
            }
        }

        return closest;
    }

    public List<Location> getOccupiedTiles() {
        ArrayList<Location> occupied = new ArrayList<>();

        Location northEast = location.transform(size, size, 0);

        for (int x = location.getX(); x < northEast.getX(); x++) {
            for (int y = location.getY(); y < northEast.getY(); y++) {
                occupied.add(Location.create(x, y, location.getZ()));
            }
        }
        return occupied;
    }

    public boolean delayed() {
        return scripts.getDelay() > GameWorld.getTicks();
    }

    public boolean isTeleporting() {
        return getAttribute("tele-pulse", null) != null || properties.getTeleportLocation() != null;
    }
}
