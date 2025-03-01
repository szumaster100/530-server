package core.game.node.item;

import core.game.node.entity.player.Player;
import core.game.world.GameWorld;
import core.game.world.map.Location;

public class GroundItem extends Item {

    private Player dropper;

    private int dropperUid;

    private int ticks;

    private int decayTime;

    private boolean remainPrivate;

    private boolean removed;

    public boolean forceVisible;

    public GroundItem(Item item) {
        this(item, null, null);
    }

    public GroundItem(Item item, Location location) {
        this(item, location, 200, null);
    }

    public GroundItem(Item item, Location location, Player player) {
        this(item, location, 200, player);
    }

    public GroundItem(Item item, Location location, int playerUid, int ticks) {
        this(item, location);
        this.dropperUid = playerUid;
        this.decayTime = ticks;
    }

    public GroundItem(Item item, Location location, int decay, Player player) {
        super(item.getId(), item.getAmount(), item.getCharge());
        super.location = location;
        super.index = -1;
        super.interactPlugin.setDefault();
        this.dropper = player;
        this.dropperUid = player != null ? player.getDetails().getUid() : -1;
        this.ticks = GameWorld.getTicks();
        this.decayTime = ticks + decay;
    }

    public void respawn() {
        // Placeholder for respawn logic
    }

    public boolean isPrivate() {
        return !forceVisible && (remainPrivate || (decayTime - GameWorld.getTicks() > 100));
    }

    @Override
    public boolean isActive() {
        // Returns {@code true} if the item is active, i.e., it hasn't decayed or been removed.
        return !removed && GameWorld.getTicks() < decayTime;
    }

    public boolean droppedBy(Player p) {
        if (p.getDetails().getUid() == dropperUid) {
            dropper = p;
            return true;
        }
        return false;
    }

    public Player getDropper() {
        return dropper;
    }

    public void setDropper(Player player) {
        this.dropper = player;
    }

    public int getTicks() {
        return ticks;
    }

    public int getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(int decayTime) {
        this.decayTime = GameWorld.getTicks() + decayTime;
    }

    public boolean isAutoSpawn() {
        return false;
    }

    public boolean isRemainPrivate() {
        return remainPrivate;
    }

    public void setRemainPrivate(boolean remainPrivate) {
        this.remainPrivate = remainPrivate;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    public int getDropperUid() {
        return dropperUid;
    }

    @Override
    public String toString() {
        return "GroundItem [dropper=" + (dropper != null ? dropper.getUsername() : dropper) +
            ", ticks=" + ticks +
            ", decayTime=" + decayTime +
            ", remainPrivate=" + remainPrivate +
            ", removed=" + removed + "]";
    }
}
