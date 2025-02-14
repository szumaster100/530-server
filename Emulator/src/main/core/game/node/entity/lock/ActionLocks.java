package core.game.node.entity.lock;

import core.game.node.Node;
import core.game.world.GameWorld;

import java.util.HashMap;
import java.util.Map;

public final class ActionLocks {

    private Lock movementLock = new Lock();

    private Lock teleportLock = new Lock();

    private Lock componentLock = new Lock();

    private Lock interactionLock = new Lock();

    private Lock equipmentLock = null;

    private Map<String, Lock> customLocks;

    public ActionLocks() {
        this.customLocks = new HashMap<>();
    }

    public void lock() {
        lock(Integer.MAX_VALUE - GameWorld.getTicks());
    }

    public void lock(int ticks) {
        lockMovement(ticks);
        lockInteractions(ticks);
    }

    public void unlock() {
        unlockMovement();
        unlockInteraction();
    }

    public void lockMovement(int ticks) {
        movementLock.lock(ticks);
    }

    public void unlockMovement() {
        movementLock.unlock();
    }

    public boolean isMovementLocked() {
        return movementLock.isLocked();
    }

    public void lockTeleport(int ticks) {
        teleportLock.lock(ticks);
    }

    public void unlockTeleport() {
        teleportLock.unlock();
    }

    public boolean isTeleportLocked() {
        return teleportLock.isLocked();
    }

    public void lockComponent(int ticks) {
        componentLock.lock(ticks);
    }

    public void unlockComponent() {
        componentLock.unlock();
    }

    public boolean isComponentLocked() {
        return componentLock.isLocked();
    }

    public void lockInteractions(int ticks) {
        interactionLock.lock(ticks);
    }

    public void unlockInteraction() {
        interactionLock.unlock();
    }

    public boolean isInteractionLocked() {
        return interactionLock.isLocked();
    }

    public Lock lock(String key, int ticks) {
        return lock(key, ticks, null);
    }

    public Lock lock(String key, int ticks, LockElapse elapse) {
        Lock lock = customLocks.get(key);
        if (lock == null) {
            customLocks.put(key, lock = new Lock());
        }
        lock.setElapse(elapse).lock(ticks);
        return lock;
    }

    public void unlock(String key, Node node) {
        unlock(key, true, node);
    }

    public void unlock(String key, boolean cacheRemove, Node node) {
        Lock lock = customLocks.get(key);
        if (lock != null) {
            lock.unlock();
            if (lock.getElapseEvent() != null) {
                lock.getElapseEvent().elapse(node, lock);
            }
            if (cacheRemove) {
                customLocks.remove(key);
            }
        }
    }

    public boolean isLocked(String key) {
        Lock lock = customLocks.get(key);
        return lock != null && lock.isLocked();
    }

    public Lock getEquipmentLock() {
        return equipmentLock;
    }

    public void setEquipmentLock(Lock equipmentLock) {
        this.equipmentLock = equipmentLock;
    }

}