package content.global.handlers.scenery;

import core.cache.def.impl.SceneryDefinition;
import core.game.global.action.DoorActionHandler;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.plugin.Initializable;
import core.plugin.Plugin;
import org.rs.consts.Animations;
import org.rs.consts.Sounds;

import static core.api.ContentAPIKt.animate;
import static core.api.ContentAPIKt.playAudio;

@Initializable
public final class ObjectManagerPlugin extends OptionHandler {

    @Override
    public Plugin<java.lang.Object> newInstance(java.lang.Object arg) throws Throwable {
        SceneryDefinition.setOptionHandler("open", this);
        SceneryDefinition.setOptionHandler("close", this);
        SceneryDefinition.setOptionHandler("shut", this);
        SceneryDefinition.setOptionHandler("go-through", this);
        return this;
    }

    @Override
    public boolean handle(Player player, Node node, String option) {
        Scenery scenery = (Scenery) node;
        if (scenery.getType() != 9 && !player.getLocation().equals(node.getLocation()) && !player.getLocation().isNextTo(scenery) && scenery.getName().contains("cupboard")) {
            return true;
        }
        String name = scenery.getName().toLowerCase();
        if (name.contains("drawers") || name.contains("wardrobe") || name.contains("cupboard")) {
            switch (option) {
                case "open":
                    if (name.contains("drawers")) {
                        animate(player, 546, false);
                        playAudio(player, Sounds.DRAWER_OPEN_64);
                    } else if (name.contains("wardrobe")) {
                        animate(player, Animations.OPEN_WARDROBE_545, false);
                        playAudio(player, Sounds.WARDROBE_OPEN_96);
                    } else if (name.contains("cupboard")) {
                        animate(player, 542, false);
                        playAudio(player, Sounds.CUPBOARD_OPEN_58);
                    }
                case "go-through":
                    if (scenery.isActive()) {
                        SceneryBuilder.replace(scenery, scenery.transform(scenery.getId() + 1), 80);
                    }
                    return true;
                case "close":
                case "shut":
                    if (name.contains("drawers")) {
                        animate(player, 547, false);
                        playAudio(player, Sounds.DRAWER_CLOSE_63);
                    } else if (name.contains("wardrobe")) {
                        animate(player, 546, false);
                        playAudio(player, Sounds.WARDROBE_CLOSE_95);
                    } else if (name.contains("cupboard")) {
                        animate(player, 541, false);
                        playAudio(player, Sounds.CUPBOARD_CLOSE_57);
                    }
                    SceneryBuilder.replace(scenery, scenery.transform(scenery.getId() - 1));
                    return true;
            }
            return true;
        }
        if (name.contains("trapdoor") || name.contains("trap door")) {
            Location destination = scenery.getLocation().transform(0, 6400, 0);
            if (!RegionManager.isTeleportPermitted(destination)) {
                player.getPacketDispatch().sendMessage("This doesn't seem to go anywhere.");
                return true;
            }
            player.getProperties().setTeleportLocation(destination);
            return true;
        }
        if (!name.contains("door") && !name.contains("gate") && !name.contains("fence") && !name.contains("wall") && !name.contains("exit") && !name.contains("entrance")) {
            return false;
        }
        DoorActionHandler.handleDoor(player, scenery);
        return true;
    }

    @Override
    public Location getDestination(Node n, Node node) {
        Scenery o = (Scenery) node;
        if (o.getType() < 4 || o.getType() == 9) {
            return DoorActionHandler.getDestination((Player) n, o);
        }
        return null;
    }

}