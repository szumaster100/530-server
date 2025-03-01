package core.game.node.entity.impl;

import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.chunk.ProjectileUpdateFlag;

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

    public static int getSpeed(Entity source, Location targetLoc) {
        return (int) (46 + (getLocation(source).getDistance(targetLoc) * 5));
    }

    public static Location getLocation(Entity n) {
        if (n == null) {
            return null;
        }
        return n.getCenterLocation();
    }

    public Projectile transform(Entity source, Entity victim) {
        return transform(source, victim, source instanceof NPC, 46, 5);
    }

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

    public Projectile copy(Entity source, Entity victim, double speedMultiplier) {
        // int distance = source instanceof NPC ? source.size() << 6 : 11;
        int speed = (int) (this.speed + (source.getLocation().getDistance(victim.getLocation()) * speedMultiplier));
        return new Projectile(source, victim, projectileId, startHeight, endHeight, startDelay, speed, angle, distance);
    }

    public Projectile transform(Entity source, Location l) {
        return transform(source, l, source instanceof NPC, 46, 5);
    }

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

    public void send() {
        send(this);
    }

    public static void send(Projectile p) {
        RegionManager.getRegionChunk(p.getSourceLocation()).flag(new ProjectileUpdateFlag(p));
    }

    public static void send(Entity source, Entity victim, Projectile p) {
        send(p.transform(source, victim));
    }

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