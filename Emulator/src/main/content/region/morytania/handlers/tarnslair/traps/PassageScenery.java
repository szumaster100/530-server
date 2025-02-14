package content.region.morytania.handlers.tarnslair.traps;

import core.game.world.map.Location;

public class PassageScenery {

    private final int objectId;
    private final Location location;

    public PassageScenery(int objectId, Location location) {
        this.objectId = objectId;
        this.location = location;
    }

    public int getObjectId() {
        return objectId;
    }

    public Location getLocation() {
        return location;
    }
}