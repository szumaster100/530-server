package core.game.node.entity.lock;

import core.game.world.GameWorld;

public class Lock {

    private int expiration;

    private LockElapse elapse;

    private String message;

    public Lock() {
        this(null);
    }

    public Lock(String message) {
        this.message = message;
    }

    public void lock() {
        lock(Integer.MAX_VALUE - GameWorld.getTicks());
    }

    public void lock(int ticks) {
        if (ticks > expiration - GameWorld.getTicks()) {
            this.expiration = GameWorld.getTicks() + ticks;
        }
    }

    public void unlock() {
        this.expiration = 0;
    }

    public boolean isLocked() {
        return expiration > GameWorld.getTicks();
    }

    public Lock setElapse(LockElapse elapse) {
        this.elapse = elapse;
        return this;
    }

    public LockElapse getElapseEvent() {
        return elapse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}