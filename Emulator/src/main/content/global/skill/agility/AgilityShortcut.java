package content.global.skill.agility;

import core.cache.def.impl.SceneryDefinition;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.scenery.Scenery;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.plugin.Plugin;
import org.rs.consts.Items;

import static core.api.ContentAPIKt.inEquipment;
import static core.api.ContentAPIKt.sendMessage;

public abstract class AgilityShortcut extends OptionHandler {
    private final int[] ids;
    private final int level;
    private final double experience;
    private final boolean canFail;
    private double failChance;
    private final String[] options;

    public AgilityShortcut(int[] ids, int level, double experience, boolean canFail, double failChance, String... options) {
        this.ids = ids;
        this.level = level;
        this.experience = experience;
        this.canFail = canFail;
        this.failChance = failChance;
        this.options = options;
    }

    public AgilityShortcut(int[] ids, int level, double experience, String... options) {
        this(ids, level, experience, false, 0.0, options);
    }

    @Override
    public Plugin<java.lang.Object> newInstance(java.lang.Object arg) throws Throwable {
        configure(this);
        return this;
    }

    @Override
    public boolean handle(Player player, Node node, String option) {
        if (!checkRequirements(player)) {
            return true;
        }
        run(player, node.asScenery(), option, checkFail(player, node.asScenery(), option));
        return true;
    }

    public abstract void run(Player player, Scenery scenery, String option, boolean failed);

    public boolean checkRequirements(Player player) {
        if (player.getSkills().getLevel(Skills.AGILITY) < level) {
            sendMessage(player, "You need an agility level of at least " + level + " to negotiate this obstacle.");
            return false;
        }
        if (inEquipment(player, Items.SLED_4084, 1)) {
            sendMessage(player, "You can't do that on a sled.");
            return false;
        }
        return true;
    }

    private boolean checkFail(Player player, Scenery scenery, String option2) {
        if (!canFail) {
            return false;
        }
        return AgilityHandler.hasFailed(player, level, failChance);
    }

    public void configure(AgilityShortcut shortcut) {
        for (int objectId : shortcut.ids) {
            SceneryDefinition def = SceneryDefinition.forId(objectId);
            for (String option : shortcut.options) {
                def.getHandlers().put("option:" + option, shortcut);
            }
        }
    }

    protected Direction getObjectDirection(Direction direction) {
        return direction == Direction.NORTH ? Direction.EAST : direction == Direction.SOUTH ? Direction.WEST : direction == Direction.EAST ? Direction.NORTH : Direction.SOUTH;
    }

    public Location pipeDestination(Player player, Scenery scenery, int steps) {
        player.faceLocation(scenery.getLocation());
        int diffX = scenery.getLocation().getX() - player.getLocation().getX();
        if (diffX < -1) {
            diffX = -1;
        }
        if (diffX > 1) {
            diffX = 1;
        }
        int diffY = scenery.getLocation().getY() - player.getLocation().getY();
        if (diffY < -1) {
            diffY = -1;
        }
        if (diffY > 1) {
            diffX = 1;
        }
        Location dest = player.getLocation().transform((diffX) * steps, (diffY) * steps, 0);
        return dest;
    }

    public Location agilityDestination(Player player, Scenery scenery, int steps) {
        player.faceLocation(scenery.getLocation());
        int diffX = scenery.getLocation().getX() - player.getLocation().getX();
        int diffY = scenery.getLocation().getY() - player.getLocation().getY();
        Location dest = player.getLocation().transform((diffX) * steps, (diffY) * steps, 0);
        return dest;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public boolean isCanFail() {
        return canFail;
    }

    public double getFailChance() {
        return failChance;
    }

    public void setFailChance(double failChance) {
        this.failChance = failChance;
    }

    public int[] getIds() {
        return ids;
    }

    public String[] getOption() {
        return options;
    }

}
