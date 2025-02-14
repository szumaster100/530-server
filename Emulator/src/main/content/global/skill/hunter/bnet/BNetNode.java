package content.global.skill.hunter.bnet;

import core.cache.def.impl.NPCDefinition;
import core.game.container.impl.EquipmentContainer;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.world.update.flag.context.Graphics;

public class BNetNode {

    private static final Item BUTTERFLY_JAR = new Item(10012);

    protected static final Item IMPLING_JAR = new Item(11260);

    private final int[] npcs;

    private final int[] levels;

    private final double[] experience;

    private final Graphics[] graphics;

    private final Item reward;

    public BNetNode(int[] npcs, int[] levels, double[] experience, Graphics[] graphics, Item reward) {
        this.npcs = npcs;
        this.levels = levels;
        this.experience = experience;
        this.graphics = graphics;
        this.reward = reward;
    }

    public void reward(Player player, NPC npc) {
        if (!isBareHand(player)) {
            if (player.getInventory().remove(getJar())) {
                final Item item = getReward();
                player.getInventory().add(item);
                player.getSkills().addExperience(Skills.HUNTER, getExperience(player), true);
            }
        } else {
            player.graphics(graphics[0]);
            player.getSkills().addExperience(Skills.HUNTER, getExperiences()[1], true);
            player.getSkills().addExperience(Skills.AGILITY, getExperiences()[2], true);
        }
    }

    public void message(Player player, int type, boolean success) {
        if (!success) {
            return;
        }
        switch (type) {
            case 1:
                player.getPacketDispatch().sendMessage("You manage to catch the butterfly.");
                if (isBareHand(player)) {
                    player.getPacketDispatch().sendMessage("You release the " + NPCDefinition.forId(npcs[0]).getName().toLowerCase() + " butterfly.");
                }
                break;
        }
    }

    public boolean hasJar(Player player) {
        return player.getInventory().containsItem(getJar());
    }

    public boolean hasWeapon(Player player) {
        Item item = player.getEquipment().get(EquipmentContainer.SLOT_WEAPON);
        return item != null && (item.getId() != 10010 && item.getId() != 11259);
    }

    public boolean hasNet(Player player) {
        return player.getEquipment().contains(10010, 1) || player.getEquipment().contains(11259, 1);
    }

    public boolean isBareHand(Player player) {
        return !hasNet(player) && player.getSkills().getLevel(Skills.HUNTER) >= getBareHandLevel() && player.getSkills().getLevel(Skills.AGILITY) >= getAgilityLevel();
    }

    public double getExperience(Player player) {
        return experience[0];
    }

    public int getLevel() {
        return levels[0];
    }

    public int getAgilityLevel() {
        return levels[2];
    }

    public int getBareHandLevel() {
        return levels[1];
    }

    public int[] getNpcs() {
        return npcs;
    }

    public int[] getLevels() {
        return levels;
    }

    public double[] getExperiences() {
        return experience;
    }

    public Graphics[] getGraphics() {
        return graphics;
    }

    public Item getReward() {
        return reward;
    }

    public Item getJar() {
        return BUTTERFLY_JAR;
    }

}
