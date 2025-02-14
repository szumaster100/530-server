package content.global.skill.construction;

import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.world.map.Location;
import core.tools.StringUtils;

@SuppressWarnings("all")
public enum HouseLocation {

    NOWHERE(-1, null, 0, 0),

    RIMMINGTON(15478, Location.create(2953, 3224, 0), 5000, 1),

    TAVERLY(15477, Location.create(2893, 3465, 0), 5000, 10),

    POLLNIVNEACH(15479, Location.create(3340, 3003, 0), 7500, 20),

    RELLEKKA(15480, Location.create(2670, 3631, 0), 10000, 30),

    BRIMHAVEN(15481, Location.create(2757, 3178, 0), 15000, 40),

    YANILLE(15482, Location.create(2544, 3096, 0), 25000, 50);

    private final int portalId;

    private final Location exitLocation;

    private final int cost;

    private final int levelRequirement;

    public boolean hasLevel(Player player) {
        return player.getSkills().getStaticLevel(Skills.CONSTRUCTION) >= levelRequirement;
    }

    private HouseLocation(int portalId, Location exitLocation, int cost, int levelRequirement) {
        this.portalId = portalId;
        this.exitLocation = exitLocation;
        this.cost = cost;
        this.levelRequirement = levelRequirement;
    }

    public String getName() {
        return StringUtils.formatDisplayName(name().toLowerCase());
    }

    public int getPortalId() {
        return portalId;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public int getCost() {
        return cost;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

}
