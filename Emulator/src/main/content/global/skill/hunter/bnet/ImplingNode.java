package content.global.skill.hunter.bnet;

import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.ChanceItem;
import core.game.node.item.Item;
import core.tools.RandomFunction;

import java.util.Random;

public final class ImplingNode extends BNetNode {

    private final ChanceItem[] loot;

    private final int respawnTime;

    public ImplingNode(int[] npcs, int level, double exp, double puroExp, Item reward, final int respawnTime, final ChanceItem... loot) {
        super(npcs, new int[]{level}, new double[]{exp, puroExp}, null, reward);
        this.loot = loot;
        this.respawnTime = respawnTime;
    }

    public ImplingNode(int[] npcs, int level, double exp, double puroExp, Item reward, final ChanceItem... loot) {
        this(npcs, level, exp, puroExp, reward, 16, loot);
    }

    public void loot(final Player player, final Item item) {
        player.lock(1);
        if (player.getInventory().freeSlots() < 1) {
            player.getPacketDispatch().sendMessage("You don't have enough inventory space.");
            return;
        }
        final Item reward = RandomFunction.getChanceItem(getLoot()).getRandomItem();
        if (player.getInventory().remove(item)) {
            if (isBroken(player)) {
                player.sendMessage("You break the jar as you try and open it. You throw the shattered remains away.");
            } else {
                player.getInventory().add(IMPLING_JAR);
            }
            player.getInventory().add(reward, player);
        }
    }

    private boolean isBroken(Player player) {
        int strengthLevel = player.getSkills().getLevel(Skills.STRENGTH);
        strengthLevel /= 0.5;
        int level = getLevel();
        int currentLevel = RandomFunction.random(strengthLevel) + 1;
        double ratio = (double) currentLevel / (new Random().nextInt(level + 5) + 1);
        return Math.round(ratio * strengthLevel) < level;
    }

    @Override
    public void message(Player player, int type, boolean success) {
        if (!success) {
            return;
        }
        if (type == 1) {
            player.sendMessage("You manage to catch the impling and squeeze it into a jar.");
        }
    }

    @Override
    public double getExperience(Player player) {
        return player.getZoneMonitor().isInZone("puro puro") ? getExperiences()[1] : super.getExperience(player);
    }

    @Override
    public boolean isBareHand(Player player) {
        return false;
    }

    @Override
    public Item getJar() {
        return IMPLING_JAR;
    }

    public ChanceItem[] getLoot() {
        return loot;
    }

    public int getRespawnTime() {
        return respawnTime;
    }
}
