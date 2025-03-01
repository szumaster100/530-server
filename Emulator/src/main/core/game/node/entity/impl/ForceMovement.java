package core.game.node.entity.impl;

import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.game.world.update.flag.EntityFlag;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.ForceMoveCtx;

public class ForceMovement extends Pulse {

    public static final int WALKING_SPEED = 10;

    public static final int RUNNING_SPEED = 20;

    public static final Animation WALK_ANIMATION = Animation.create(819);

    protected Entity entity;

    private Location start;

    private Location destination;

    private Animation startAnim;

    protected Animation animation;

    private Animation endAnimation = null;

    protected Direction direction;

    private int commenceSpeed;

    private int pathSpeed;

    private boolean unlockAfter;

    @Deprecated
    public ForceMovement(Entity e, Location start, Location destination, Animation startAnim, Animation animation, Direction direction, int commenceSpeed, int pathSpeed, boolean unlockAfter) {
        super(1, e);
        this.entity = e;
        this.start = start;
        this.destination = destination;
        this.startAnim = startAnim;
        this.animation = animation;
        this.direction = direction;
        this.commenceSpeed = commenceSpeed;
        this.pathSpeed = pathSpeed;
        this.unlockAfter = unlockAfter;
    }

    @Deprecated
    public ForceMovement(Entity e, Location start, Location end, Animation animation, int speed) {
        this(e, start, end, WALK_ANIMATION, animation, direction(start, end), WALKING_SPEED, speed, true);
    }

    @Deprecated
    public ForceMovement(Entity e, Location destination, int startSpeed, int animSpeed) {
        this(e, e.getLocation(), destination, WALK_ANIMATION, WALK_ANIMATION, direction(e.getLocation(), destination), startSpeed, animSpeed, true);
    }

    @Deprecated
    public ForceMovement(Entity e, Location start, Location destination, Animation animation) {
        this(e, start, destination, WALK_ANIMATION, animation, direction(start, destination), WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public ForceMovement(Location start, Location destination, Animation animation) {
        this(null, start, destination, WALK_ANIMATION, animation, direction(start, destination), WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location destination) {
        return run(e, e.getLocation(), destination, WALK_ANIMATION, WALK_ANIMATION, direction(e.getLocation(), destination), WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination) {
        return run(e, start, destination, WALK_ANIMATION, WALK_ANIMATION, direction(e.getLocation(), destination), WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation animation) {
        return run(e, start, destination, WALK_ANIMATION, animation, direction(start, destination), WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation animation, int speed) {
        return run(e, start, destination, WALK_ANIMATION, animation, direction(start, destination), WALKING_SPEED, speed, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation startAnim, Animation animation) {
        return run(e, start, destination, startAnim, animation, direction(start, destination), WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation startAnim, Animation animation, Direction direction) {
        return run(e, start, destination, startAnim, animation, direction, WALKING_SPEED, WALKING_SPEED, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation startAnim, Animation animation, Direction direction, int pathSpeed) {
        return run(e, start, destination, startAnim, animation, direction, WALKING_SPEED, pathSpeed, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation startAnim, Animation animation, Direction direction, int commenceSpeed, int pathSpeed) {
        return run(e, start, destination, startAnim, animation, direction, commenceSpeed, pathSpeed, true);
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location start, Location destination, Animation startAnim, Animation animation, Direction direction, int commenceSpeed, int pathSpeed, boolean unlockAfter) {
        if (startAnim != null) {
            startAnim.setPriority(Animator.Priority.VERY_HIGH);
        }
        if (animation != null) {
            animation.setPriority(Animator.Priority.VERY_HIGH);
        }
        ForceMovement fm = new ForceMovement(e, start, destination, startAnim, animation, direction, commenceSpeed, pathSpeed, unlockAfter);
        fm.start();
        e.lock();
        GameWorld.getPulser().submit(fm);
        return fm;
    }

    @Deprecated
    public static ForceMovement run(Entity e, Location destination, int commenceSpeed, int pathSpeed) {
        return run(e, e.getLocation(), destination, WALK_ANIMATION, WALK_ANIMATION, direction(e.getLocation(), destination), commenceSpeed, pathSpeed, true);
    }

    @Deprecated
    public void run(final Entity e, final int speed) {
        this.entity = e;
        int commence = (int) start.getDistance(e.getLocation());
        if (commence != 0 && commenceSpeed != 0) {
            commence = (int) (1 + (commence / (commenceSpeed * 0.1)));
        }
        int path = 1 + (int) Math.ceil(start.getDistance(destination) / (pathSpeed * 0.1));
        this.pathSpeed = pathSpeed == 0 ? path : speed;
        this.commenceSpeed = commence;
        start();
        e.lock();
        GameWorld.getPulser().submit(this);
    }

    public void run(final Entity e) {
        run(e, 0);
    }

    public void run() {
        run(entity);
    }

    public static Direction direction(Location s, Location d) {
        Location delta = Location.getDelta(s, d);
        int x = Math.abs(delta.getX());
        int y = Math.abs(delta.getY());
        if (x > y) {
            return Direction.getDirection(delta.getX(), 0);
        }
        return Direction.getDirection(0, delta.getY());
    }

    @Override
    public void start() {
        commenceSpeed = (int) Math.ceil(start.getDistance(entity.getLocation()) / (commenceSpeed * 0.1));
        pathSpeed = (int) Math.ceil(start.getDistance(destination) / (pathSpeed * 0.1));
        if (commenceSpeed != 0) {
            entity.animate(startAnim);
            super.setDelay(commenceSpeed);
        } else {
            entity.animate(animation);
            super.setDelay(pathSpeed);
        }
        int ticks = 1 + commenceSpeed + pathSpeed;
        entity.getImpactHandler().setDisabledTicks(ticks);
        entity.getUpdateMasks().register(EntityFlag.ForceMove, new ForceMoveCtx(start, destination, commenceSpeed * 30, pathSpeed * 30, direction));
        if (entity instanceof Player) {
            entity.getWalkingQueue().updateRegion(destination, false);
        }
        super.start();
    }

    @Override
    public boolean pulse() {
        if (commenceSpeed != 0) {
            entity.animate(animation);
            setDelay(pathSpeed);
            commenceSpeed = 0;
            entity.getProperties().setTeleportLocation(start);
            return false;
        }
        return true;
    }

    @Override
    public void stop() {
        super.stop();
        entity.getProperties().setTeleportLocation(destination);
        if (endAnimation != null) {
            entity.animate(endAnimation);
        }
        if (unlockAfter) entity.unlock();
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getDestination() {
        return destination;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getCommenceSpeed() {
        return commenceSpeed;
    }

    public void setCommenceSpeed(int commenceSpeed) {
        this.commenceSpeed = commenceSpeed;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public int getPathSpeed() {
        return pathSpeed;
    }

    public void setPathSpeed(int pathSpeed) {
        this.pathSpeed = pathSpeed;
    }

    public Entity getEntity() {
        return entity;
    }

    public Animation getStartAnim() {
        return startAnim;
    }

    public void setStartAnim(Animation startAnim) {
        this.startAnim = startAnim;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Animation getEndAnimation() {
        return endAnimation;
    }

    public void setEndAnimation(Animation endAnimation) {
        this.endAnimation = endAnimation;
    }
}
