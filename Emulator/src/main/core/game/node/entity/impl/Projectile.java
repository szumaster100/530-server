package core.game.node.entity.impl;

import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.chunk.ProjectileUpdateFlag;

/**
 * Represents a projectile in the game.
 * A projectile is an object that is launched from a source entity towards a victim or a location.
 */
public class Projectile {

    private Entity source;
    private Location sourceLocation;
    private Entity victim;
    private int projectileId;
    private int startHeight;
    private int endHeight;
    private int startDelay;
    private int speed;
    private int angle;
    private int distance;
    private Location endLocation;

    /**
     * Creates a new projectile with the given parameters.
     *
     * @param source       The entity launching the projectile.
     * @param victim       The entity receiving the projectile.
     * @param projectileId The ID of the projectile.
     * @return A new {@code Projectile} object.
     */
    public static Projectile create(Entity source, Entity victim, int projectileId) {
        int speed = (int) (46 + (getLocation(source).getDistance(victim.getLocation()) * 5));
        return new Projectile(source, victim, projectileId, 40, 36, 41, speed, 5, source == null ? 11 : source.size() << 5);
    }

    public static Projectile create(Entity source, Entity victim, int projectileId, int startHeight, int endHeight) {
        int speed = (int) (46 + (getLocation(source).getDistance(victim.getLocation()) * 5));
        return new Projectile(source, victim, projectileId, startHeight, endHeight, 41, speed, 5, source == null ? 11 : source.size() << 5);
    }

    public static Projectile create(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay) {
        int speed = (int) (46 + (getLocation(source).getDistance(victim.getLocation()) * 5));
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, 5, source == null ? 11 : source.size() << 5);
    }

    public static Projectile create(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay, int speed) {
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, 5, source == null ? 11 : source.size() << 5);
    }

    public static Projectile create(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay, int speed, int angle) {
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, angle, source.size() << 5);
    }

    public static Projectile create(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay, int speed, int angle, int distance) {
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, angle, distance);
    }

    public static Projectile create(Location start, Location destination, int projectileId, int startHeight, int endHeight, int startDelay, int speed, int angle, int distance) {
        return new Projectile(start, destination, projectileId, startHeight, endHeight, startDelay, speed, angle, distance);
    }

    public static Projectile magic(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay, int angle) {
        int speed = (int) (46 + (getLocation(source).getDistance(victim.getLocation()) * 10));
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, angle, source.size() << 5);
    }

    public static Projectile ranged(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay, int angle) {
        int speed = (int) (46 + (getLocation(source).getDistance(victim.getLocation()) * 5));
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, angle, source.size() << 5);
    }

    public Projectile() {

    }

    private Projectile(Entity source, Entity victim, int projectileId, int startHeight, int endHeight, int startDelay, int speed, int angle, int distance) {
        this.source = source;
        this.sourceLocation = getLocation(source);
        this.victim = victim;
        this.projectileId = projectileId;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startDelay = startDelay;
        this.speed = speed;
        this.angle = angle;
        this.distance = distance;
    }

    private Projectile(Location start, Location l, int projectileId, int startHeight, int endHeight, int startDelay, int speed, int angle, int distance) {
        this.sourceLocation = start;
        this.endLocation = l;
        this.projectileId = projectileId;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startDelay = startDelay;
        this.speed = speed;
        this.angle = angle;
        this.distance = distance;
    }

    /**
     * Returns the speed of the projectile based on the distance between the source and the target location.
     *
     * @param source    The entity launching the projectile.
     * @param targetLoc The location of the target.
     * @return The speed of the projectile.
     */
    public static int getSpeed(Entity source, Location targetLoc) {
        return (int) (46 + (getLocation(source).getDistance(targetLoc) * 5));
    }

    /**
     * Returns the location of the entity, or {@code null} if the entity is {@code null}.
     *
     * @param n The entity whose location is to be retrieved.
     * @return The location of the entity.
     */
    public static Location getLocation(Entity n) {
        if (n == null) {
            return null;
        }
        return n.getCenterLocation();
    }

    /**
     * Transforms the projectile's source and victim entities with new parameters.
     *
     * @param source The new source entity.
     * @param victim The new victim entity.
     * @return The updated {@code Projectile}.
     */
    public Projectile transform(Entity source, Entity victim) {
        return transform(source, victim, source instanceof NPC, 46, 5);
    }

    /**
     * Transforms the projectile's source, victim, and other properties.
     *
     * @param source        The new source entity.
     * @param victim        The new victim entity.
     * @param npc           A flag indicating if the source is an NPC.
     * @param baseSpeed     The base speed of the projectile.
     * @param modifiedSpeed The modified speed of the projectile.
     * @return The updated {@code Projectile}.
     */
    public Projectile transform(Entity source, Entity victim, boolean npc, int baseSpeed, int modifiedSpeed) {
        this.source = source;
        this.sourceLocation = getLocation(source);
        this.victim = victim;
        this.speed = (int) (baseSpeed + sourceLocation.getDistance(victim.getLocation()) * modifiedSpeed);
        if (npc) {
            this.distance = source.size() << 5;
        }
        return this;
    }

    /**
     * Creates a copy of the projectile with a modified speed.
     *
     * @param source          The new source entity.
     * @param victim          The new victim entity.
     * @param speedMultiplier The multiplier to adjust the speed.
     * @return A new {@code Projectile} that is a copy of this one, with a modified speed.
     */
    public Projectile copy(Entity source, Entity victim, double speedMultiplier) {
        // int distance = source instanceof NPC ? source.size() << 6 : 11;
        int speed = (int) (this.speed + (source.getLocation().getDistance(victim.getLocation()) * speedMultiplier));
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, angle, distance);
    }

    /**
     * Transforms the projectile's source and a new location.
     *
     * @param source The new source entity.
     * @param l      The new destination location.
     * @return The updated {@code Projectile}.
     */
    public Projectile transform(Entity source, Location l) {
        return transform(source, l, source instanceof NPC, 46, 5);
    }

    /**
     * Transforms the projectile's source, location, and other properties.
     *
     * @param source        The new source entity.
     * @param l             The new destination location.
     * @param npc           A flag indicating if the source is an NPC.
     * @param baseSpeed     The base speed of the projectile.
     * @param modifiedSpeed The modified speed of the projectile.
     * @return The updated {@code Projectile}.
     */
    public Projectile transform(Entity source, Location l, boolean npc, int baseSpeed, int modifiedSpeed) {
        this.source = source;
        this.sourceLocation = getLocation(source);
        this.endLocation = l;
        this.speed = (int) (baseSpeed + sourceLocation.getDistance(l) * modifiedSpeed);
        if (npc) {
            this.distance = source.size() << 5;
        }
        return this;
    }

    /**
     * Sends the projectile update to the game region.
     */
    public void send() {
        send(this);
    }

    /**
     * Sends the projectile update to the specified projectile.
     *
     * @param p The projectile to send.
     */
    public static void send(Projectile p) {
        RegionManager.getRegionChunk(p.getSourceLocation()).flag(new ProjectileUpdateFlag(p));
    }

    /**
     * Sends the projectile update with the source and victim entities.
     *
     * @param source The source entity.
     * @param victim The victim entity.
     * @param p      The projectile to send.
     */
    public static void send(Entity source, Entity victim, Projectile p) {
        send(p.transform(source, victim));
    }

    /**
     * Sends the projectile update with the source entity and a location.
     *
     * @param source The source entity.
     * @param l      The destination location.
     * @param p      The projectile to send.
     */
    public static void send(Entity source, Location l, Projectile p) {
        send(p.transform(source, l));
    }

    public void setSource(Entity source) {
        this.source = source;
    }

    public Entity getSource() {
        return source;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setVictim(Entity victim) {
        this.victim = victim;
    }

    public Entity getVictim() {
        return victim;
    }

    public void setProjectileId(int projectileId) {
        this.projectileId = projectileId;
    }

    public int getProjectileId() {
        return projectileId;
    }

    public void setStartHeight(int startHeight) {
        this.startHeight = startHeight;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public void setEndHeight(int endHeight) {
        this.endHeight = endHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public void setStartDelay(int delay) {
        this.startDelay = delay;
    }

    public int getStartDelay() {
        return startDelay;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public int getAngle() {
        return angle;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isLocationBased() {
        return endLocation != null;
    }

    public Location getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(Location endLocation) {
        this.endLocation = endLocation;
    }

}