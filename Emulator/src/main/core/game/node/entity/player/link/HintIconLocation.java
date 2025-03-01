package core.game.node.entity.player.link;

public enum HintIconLocation {
    ENTITY(1),
    CENTER(2),
    WEST(3),
    EAST(4),
    SOUTH(5),
    NORTH(6);

    private final int location;

    HintIconLocation(int location) {
        this.location = location;
    }

    public int getLocation() {
        return location;
    }
}