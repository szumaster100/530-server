package content.global.skill.hunter;

import core.cache.def.impl.ItemDefinition;
import core.cache.def.impl.SceneryDefinition;
import core.game.node.Node;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.GroundItem;
import core.game.node.item.GroundItemManager;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Animation;
import core.tools.RandomFunction;
import org.rs.consts.Sounds;

import static core.api.ContentAPIKt.playAudio;

public class TrapSetting {
    private final int[] nodeIds;
    private final Item[] items;
    private final String option;
    private final int level;
    private final Animation setupAnimation;
    private final Animation dismantleAnimation;
    private final int failId;
    private final int[] objectIds;
    private final int[] baitIds;
    private final boolean objectTrap;

    public TrapSetting(int[] nodeIds, Item[] items, int[] objectIds, final int[] baitIds, final String option, int level, final int failId, final Animation setupAnimation, final Animation dismantleAnimation, boolean objectTrap) {
        this.nodeIds = nodeIds;
        this.items = items;
        this.objectIds = objectIds;
        this.baitIds = baitIds;
        this.level = level;
        this.option = option;
        this.objectTrap = objectTrap;
        this.failId = failId;
        this.setupAnimation = setupAnimation;
        this.dismantleAnimation = dismantleAnimation;
    }

    public TrapSetting(int nodeId, Item[] items, int[] objectIds, final int[] baitIds, final String option, int level, final int failId, final Animation setupAnimation, final Animation dismantleAnimation, boolean objectTrap) {
        this(new int[]{nodeId}, items, objectIds, baitIds, option, level, failId, setupAnimation, dismantleAnimation, objectTrap);
    }

    public TrapSetting(int nodeId, int[] objectIds, final int[] baitIds, String option, final int failId, final Animation setupAnimation, final Animation dismantleAnimation, int level) {
        this(new int[]{nodeId}, objectIds, baitIds, option, level, failId, setupAnimation, dismantleAnimation, false);
    }

    public TrapSetting(int[] nodeIds, int[] objectIds, final int[] baitIds, String option, int level, final int failId, final Animation setupAnimation, final Animation dismantleAnimation, boolean objectTrap) {
        this(nodeIds, new Item[]{new Item(nodeIds[0])}, objectIds, baitIds, option, level, failId, setupAnimation, dismantleAnimation, objectTrap);
    }

    public boolean clear(TrapWrapper wrapper, int type) {
        Scenery scenery = wrapper.getObject();
        returnItems(scenery, wrapper, type);
        wrapper.getType().getHooks().remove(wrapper.getHook());
        removeObject(wrapper);
        return true;
    }

    public void returnItems(Scenery scenery, TrapWrapper wrapper, int type) {
        boolean ground = type == 0;
        Player player = wrapper.getPlayer();
        if (!isObjectTrap()) {
            if (ground) {
                createGroundItem(items[0], scenery.getLocation(), wrapper.getPlayer());
                return;
            }
            addTool(player, wrapper, type);
            player.getInventory().add(wrapper.getItems().toArray(new Item[]{}));
        } else {
            if (isObjectTrap() && !ground) {
                player.getInventory().add(wrapper.getItems().toArray(new Item[]{}));
            }
        }
    }

    public void addTool(Player player, TrapWrapper wrapper, int type) {
        player.getInventory().add(items[0]);
    }

    public boolean hasItems(Player player) {
        for (Item item : items) {
            if (!player.getInventory().containsItem(item)) {
                return false;
            }
        }
        return true;
    }

    public void createGroundItem(Item item, Location location, Player player) {
        GroundItemManager.create(new GroundItem(item, location, player));
    }

    public boolean removeObject(TrapWrapper wrapper) {
        return removeObject(wrapper.getObject());
    }

    public boolean removeObject(Scenery scenery) {
        return SceneryBuilder.remove(scenery);
    }

    public Scenery buildObject(Player player, Node node) {
        return new Scenery(objectIds[0], player.getLocation());
    }

    public void investigate(Player player, Scenery scenery) {
        HunterManager instance = HunterManager.getInstance(player);
        if (!instance.isOwner(scenery)) {
            player.sendMessage("This isn't your trap.");
            return;
        }
        TrapWrapper wrapper = instance.getWrapper(scenery);
        player.sendMessage("This trap " + (wrapper.isSmoked() ? "has" : "hasn't") + " been smoked.");
    }

    public void catchNpc(final TrapWrapper wrapper, final TrapNode node, final NPC npc) {
        final boolean success = isSuccess(wrapper.getPlayer(), node);
        int ticks = success ? 3 : 2;
        npc.lock(ticks);
        wrapper.setBusyTicks(ticks);
        npc.getWalkingQueue().reset();
        npc.getPulseManager().clear();
        wrapper.setTicks(wrapper.getTicks() + 4);
        GameWorld.getPulser().submit(getCatchPulse(wrapper, node, npc, success));
    }

    public void handleCatch(int counter, TrapWrapper wrapper, TrapNode node, NPC npc, boolean success) {
    }

    public Pulse getCatchPulse(final TrapWrapper wrapper, final TrapNode node, final NPC npc, final boolean success) {
        final Player player = wrapper.getPlayer();
        wrapper.setFailed(!success);
        return new Pulse(1) {
            int counter;

            @Override
            public boolean pulse() {
                switch (++counter) {
                    case 2:
                        handleCatch(counter, wrapper, node, npc, success);
                        if (success) {
                            int transformId = getTransformId(wrapper, node);
                            npc.setAttribute("hunter", GameWorld.getTicks() + 6);
                            npc.finalizeDeath(player);
                            if (transformId != -1) {
                                wrapper.setObject(getTransformId(wrapper, node));
                            }
                            npc.getProperties().setTeleportLocation(npc.getProperties().getSpawnLocation());
                            break;
                        }
                        npc.moveStep();
                        wrapper.setObject(getFailId(wrapper, node));
                        break;
                    case 3:
                        handleCatch(counter, wrapper, node, npc, success);
                        if (success) {
                            wrapper.setTicks(GameWorld.getTicks() + 100);
                            wrapper.setReward(node);
                            wrapper.setObject(getFinalId(wrapper, node));
                            switch (wrapper.getType()) {
                                case BIRD_SNARE:
                                    playAudio(player, Sounds.HUNTING_NOOSE_2637, 0, 1, wrapper.getObject().getLocation(), 10);
                                    playAudio(player, Sounds.HUNTING_BIRDCAUGHT_2625, 20, 1, wrapper.getObject().getLocation(), 10);
                                    break;
                                case BOX_TRAP:
                                    playAudio(player, Sounds.HUNTING_BOXTRAP_2627, 0, 1, wrapper.getObject().getLocation(), 10);
                                    break;
                                case NET_TRAP:
                                    playAudio(player, Sounds.HUNTING_TWITCHNET_2652, 0, 1, wrapper.getObject().getLocation(), 10);
                                    playAudio(player, Sounds.SALAMANDER_HIT_739, 20, 1, wrapper.getObject().getLocation(), 10);
                                    break;
                                case DEAD_FALL:
                                    playAudio(player, Sounds.HUNTING_DEADFALL_2631, 0, 1, wrapper.getObject().getLocation(), 10);
                                    break;
                            }
                            return true;
                        }
                        npc.moveStep();
                        return true;
                }
                return false;
            }

        };
    }

    public boolean isSuccess(Player player, final TrapNode node) {
        double level = player.skills.getStaticLevel(Skills.HUNTER);
        double req = node.level;
        double successChance = Math.ceil((level * 50 - req * 17) / req / 3 * 4);
        int roll = RandomFunction.random(99);
        return successChance >= roll;
    }

    public TrapHook createHook(TrapWrapper wrapper) {
        return new TrapHook(wrapper, new Location[]{wrapper.getObject().getLocation()});
    }

    public void reward(Player player, Node node, TrapWrapper wrapper) {

    }

    public boolean canCatch(TrapWrapper wrapper, NPC npc) {
        return true;
    }

    public boolean hasBait(Item bait) {
        for (int id : getBaitIds()) {
            if (id == bait.getId()) {
                return true;
            }
        }
        return false;
    }

    public int getNodeForObjectId(int objectId) {
        return getNodeForObject(getObjectIndex(objectId));
    }

    public int getNodeForObject(int index) {
        return nodeIds[index];
    }

    public int getObjectForNode(Node node) {
        return getObjectForNode(getNodeIndex(node));
    }

    public int getObjectForNode(int index) {
        return objectIds[index];
    }

    public int getNodeIndex(Node node) {
        for (int i = 0; i < nodeIds.length; i++) {
            if (node.getId() == nodeIds[i]) {
                return i;
            }
        }
        return -1;
    }

    public int getObjectIndex(int objectId) {
        for (int i = 0; i < objectIds.length; i++) {
            if (objectId == objectIds[i]) {
                return i;
            }
        }
        return -1;
    }

    public int getTransformId(TrapWrapper wrapper, TrapNode node) {
        return node.getTransformId();
    }

    public int getFinalId(TrapWrapper wrapper, TrapNode node) {
        return node.getFinalId();
    }

    public int getFailId(TrapWrapper wrapper, TrapNode node) {
        return failId;
    }

    public String getLimitMessage(Player player) {
        HunterManager instance = HunterManager.getInstance(player);
        return "You don't have a high enough Hunter level to set up more than " + instance.getMaximumTraps() + " trap" + (instance.getMaximumTraps() == 1 ? "." : "s.");
    }

    public String getName() {
        if (isObjectTrap()) {
            return SceneryDefinition.forId(nodeIds[0]).getName().toLowerCase();
        }
        return ItemDefinition.forId(nodeIds[0]).getName().toLowerCase();
    }

    public String getTimeUpMessage() {
        return "The " + getName() + " " + (isObjectTrap() ? "trap that you constructed has collapsed." : "that you laid has fallen over.");
    }

    public boolean exceedsLimit(Player player) {
        return false;
    }

    public boolean isObjectTrap() {
        return objectTrap;
    }

    public int[] getNodeIds() {
        return nodeIds;
    }

    public Item[] getItems() {
        return items;
    }

    public String getOption() {
        return option;
    }

    public int getLevel() {
        return level;
    }

    public Animation getSetupAnimation() {
        return setupAnimation;
    }

    public int[] getObjectIds() {
        return objectIds;
    }

    public Animation getDismantleAnimation() {
        return dismantleAnimation;
    }

    public int getFailId() {
        return failId;
    }

    public int[] getBaitIds() {
        return baitIds;
    }

}
