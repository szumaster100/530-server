package core.game.world.map.zone;

import core.game.node.entity.Entity;

public interface Zone {

    boolean enter(Entity e);

    boolean leave(Entity e, boolean logout);
}
