package core.game.system.task;

import core.game.node.entity.Entity;
import core.game.world.map.Location;

public interface MovementHook {

    boolean handle(Entity e, Location l);

}