package content.global.activity.ttrail;

import core.game.global.action.DigAction;
import core.game.global.action.DigSpadeHandler;
import core.game.interaction.Option;
import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.game.world.map.zone.ZoneBorders;

public abstract class MapClueScroll extends ClueScrollPlugin {

    private final Location location;

    private final int object;

    public MapClueScroll(String name, int clueId, ClueLevel level, int interfaceId, Location location, final int object, ZoneBorders... borders) {
        super(name, clueId, level, interfaceId, borders);
        this.location = location;
        this.object = object;
    }

    public MapClueScroll(String name, int clueId, ClueLevel level, int interfaceId, Location location) {
        this(name, clueId, level, interfaceId, location, 0);
    }

    @Override
    public boolean interact(Entity e, Node target, Option option) {
        if (e instanceof Player) {
            Player p = e.asPlayer();
            if (target.getId() == object && option.getName().equals("Search")) {
                if (!p.getInventory().contains(clueId, 1) || !target.getLocation().equals(location)) {
                    p.sendMessage("Nothing interesting happens.");
                    return false;
                }
                reward(p);
                return true;
            }
        }
        return super.interact(e, target, option);
    }

    @Override
    public void configure() {
        DigSpadeHandler.register(getLocation(), new MapDigAction());
        super.configure();
    }

    public void dig(Player player) {
        reward(player);
        player.getDialogueInterpreter().sendItemMessage(405, "You've found a casket!");
    }

    public final class MapDigAction implements DigAction {

        @Override
        public void run(Player player) {
            if (!hasRequiredItems(player)) {
                player.sendMessage("Nothing interesting happens.");
                return;
            }
            dig(player);
        }

    }

    public boolean hasRequiredItems(Player player) {
        return player.getInventory().contains(clueId, 1);
    }

    public Location getLocation() {
        return location;
    }

    public int getObject() {
        return object;
    }

}
