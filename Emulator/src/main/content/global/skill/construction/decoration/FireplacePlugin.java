package content.global.skill.construction.decoration;

import core.cache.def.impl.SceneryDefinition;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.update.flag.context.Animation;
import core.plugin.Initializable;
import core.plugin.Plugin;
import org.rs.consts.Items;

@Initializable
public final class FireplacePlugin extends OptionHandler {

    private static final Animation ANIMATION = Animation.create(3658);

    @Override
    public Plugin<java.lang.Object> newInstance(java.lang.Object arg) throws Throwable {
        SceneryDefinition.forId(13609).getHandlers().put("option:light", this);
        SceneryDefinition.forId(13611).getHandlers().put("option:light", this);
        SceneryDefinition.forId(13613).getHandlers().put("option:light", this);
        return this;
    }

    @Override
    public boolean handle(final Player player, Node node, String option) {
        if (!player.getInventory().contains(1511, 1) || !player.getInventory().contains(590, 1)) {
            player.sendMessage("You need some logs and a tinderbox in order to light the fireplace.");
            return true;
        }
        final Scenery obj = node.asScenery();
        player.lock(2);
        player.animate(ANIMATION);
        GameWorld.getPulser().submit(new Pulse(2, player) {
            @Override
            public boolean pulse() {
                if (!obj.isActive()) {
                    return true;
                }
                player.getInventory().remove(new Item(Items.LOGS_1511));
                player.getSkills().addExperience(Skills.FIREMAKING, 80);
                SceneryBuilder.replace(new Scenery(obj.getId(), obj.getLocation()), new Scenery(obj.getId() + 1, obj.getLocation(), obj.getRotation()), 1000);
                player.sendMessage("You light the fireplace.");
                return true;
            }
        });
        return true;
    }
}