package content.minigame.pestcontrol;

import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.world.map.zone.MapZone;
import core.game.world.map.zone.ZoneRestriction;

public final class PCIslandZone extends MapZone {

    public PCIslandZone() {
        super("pest control island", true, ZoneRestriction.CANNON, ZoneRestriction.FIRES, ZoneRestriction.RANDOM_EVENTS);
    }

    @Override
    public boolean death(Entity e, Entity killer) {
        if (e instanceof Player) {
            e.getProperties().setTeleportLocation(e.getLocation());
            return true;
        }
        return false;
    }

    @Override
    public void configure() {
        registerRegion(10537);
    }

}