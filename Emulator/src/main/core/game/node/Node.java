package core.game.node;

import core.api.utils.Vector;
import core.game.interaction.DestinationFlag;
import core.game.interaction.InteractPlugin;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.tools.StringUtils;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public abstract class Node {

    private static final Int2ObjectMap<Node> nodeMap = new Int2ObjectOpenHashMap<>();

    public static void registerNode(Node node) {
        nodeMap.put(node.getIndex(), node);
    }

    protected String name;

    protected Location location;

    protected int index;

    protected Direction direction;

    protected int size = 1;

    protected boolean active = true;

    protected InteractPlugin interactPlugin;

    protected DestinationFlag destinationFlag;

    protected boolean renderable = true;

    public Node(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public NPC asNpc() {
        return (NPC) this;
    }

    public Player asPlayer() {
        return (Player) this;
    }

    public Scenery asScenery() {
        return (Scenery) this;
    }

    public Item asItem() {
        return (Item) this;
    }

    public int getId() {
        if (this instanceof NPC) {
            return ((NPC) this).getId();
        } else if (this instanceof Scenery) {
            return ((Scenery) this).getId();
        } else if (this instanceof Item) {
            return ((Item) this).getId();
        }
        return -1;
    }

    public int getIdHash() {
        return this instanceof Item ? ((Item) this).getIdHash() : -1;
    }

    public Location getCenterLocation() {
        int offset = size >> 1; // Half the size
        return location.transform(offset, offset, 0);
    }

    public Vector getMathematicalCenter() {
        Location topRight = location.transform(size - 1, size - 1, 0);
        double x = ((double) location.getX() + (double) topRight.getX()) / 2.0;
        double y = ((double) location.getY() + (double) topRight.getY()) / 2.0;
        return new Vector(x, y);
    }

    public Location getFaceLocation(Location fromLoc) {
        Vector center = getMathematicalCenter();
        Vector fromVec = new Vector((double) fromLoc.getX(), (double) fromLoc.getY());
        Vector difference = fromVec.minus(center);
        Vector end = center.plus(difference.invert());
        return Location.create((int) end.getX(), (int) end.getY(), fromLoc.getZ());
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return StringUtils.formatDisplayName(name);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (direction != null) {
            this.direction = direction;
        }
    }

    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public InteractPlugin getInteraction() {
        if (interactPlugin != null && !interactPlugin.isInitialized()) {
            interactPlugin.setDefault();
        }
        return interactPlugin;
    }

    public void setInteraction(InteractPlugin interactPlugin) {
        this.interactPlugin = interactPlugin;
    }

    public DestinationFlag getDestinationFlag() {
        return destinationFlag;
    }

    public void setDestinationFlag(DestinationFlag destinationFlag) {
        this.destinationFlag = destinationFlag;
    }

    public boolean isRenderable() {
        return renderable;
    }

    public void setRenderable(boolean renderable) {
        this.renderable = renderable;
    }

    public static class NodeRepository {

        private static final Int2ObjectMap<Node> nodeMap = new Int2ObjectOpenHashMap<>();

        public static void registerNode(Node node) {
            nodeMap.put(node.getIndex(), node);
        }

        public static Node forId(int id) {
            return nodeMap.get(id);
        }

        public static boolean isEmpty(int id) {
            return !nodeMap.containsKey(id);
        }
    }
}
