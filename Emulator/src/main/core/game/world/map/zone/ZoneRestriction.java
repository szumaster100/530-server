package core.game.world.map.zone;

public enum ZoneRestriction {

    FOLLOWERS,

    RANDOM_EVENTS,

    FIRES,

    MEMBERS,

    CANNON,

    GRAVES,

    TELEPORT;

    public int getFlag() {
        return 1 << ordinal();
    }
}