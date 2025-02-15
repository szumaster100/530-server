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

/**
 * Represents a node in the game world.
 */
public abstract class Node {
    /**
     * A map storing all registered nodes by their index.
     */
    private static final Int2ObjectMap<Node> nodeMap = new Int2ObjectOpenHashMap<>();

    /**
     * Registers a node into the global node repository.
     *
     * @param node The node to register.
     */
    public static void registerNode(Node node) {
        nodeMap.put(node.getIndex(), node);
    }

    // The name of the node
    protected String name;
    // The location of the node in the game world
    protected Location location;
    // The index identifier for the node
    protected int index;
    // The direction the node is facing
    protected Direction direction;
    // The size of the node (default is 1)
    protected int size = 1;
    // Whether the node is active
    protected boolean active = true;
    // Interaction behavior of the node
    protected InteractPlugin interactPlugin;
    // The movement destination flag
    protected DestinationFlag destinationFlag;
    // Whether the node is renderable
    protected boolean renderable = true;

    /**
     * Constructs a new Node.
     *
     * @param name     The name of the node.
     * @param location The location of the node.
     */
    public Node(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Casts the node to an NPC.
     *
     * @return The NPC instance.
     */
    public NPC asNpc() {
        return (NPC) this;
    }

    /**
     * Casts the node to a Player.
     *
     * @return The Player instance.
     */
    public Player asPlayer() {
        return (Player) this;
    }

    /**
     * Casts the node to a Scenery object.
     *
     * @return The Scenery instance.
     */
    public Scenery asScenery() {
        return (Scenery) this;
    }

    /**
     * Casts the node to an Item.
     *
     * @return The Item instance.
     */
    public Item asItem() {
        return (Item) this;
    }

    /**
     * Retrieves the ID of the node.
     *
     * @return The ID of the node, or -1 if it does not have one.
     */
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

    /**
     * Retrieves the hash ID of an item node.
     *
     * @return The item ID hash, or -1 if not applicable.
     */
    public int getIdHash() {
        return this instanceof Item ? ((Item) this).getIdHash() : -1;
    }

    /**
     * Retrieves the center location of the node.
     *
     * @return The center location of the node.
     */
    public Location getCenterLocation() {
        int offset = size >> 1; // Half the size
        return location.transform(offset, offset, 0);
    }

    /**
     * Retrieves the mathematical center of the node.
     *
     * @return A Vector representing the node's center.
     */
    public Vector getMathematicalCenter() {
        Location topRight = location.transform(size - 1, size - 1, 0);
        double x = ((double) location.getX() + (double) topRight.getX()) / 2.0;
        double y = ((double) location.getY() + (double) topRight.getY()) / 2.0;
        return new Vector(x, y);
    }

    /**
     * Determines the location to face based on another location.
     *
     * @param fromLoc The location from which to determine facing.
     * @return The calculated face location.
     */
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

    /**
     * A repository for managing Node instances.
     */
    public static class NodeRepository {
        /**
         * A map storing nodes by their index.
         */
        private static final Int2ObjectMap<Node> nodeMap = new Int2ObjectOpenHashMap<>();

        /**
         * Registers a node into the repository.
         *
         * @param node The node to register.
         */
        public static void registerNode(Node node) {
            nodeMap.put(node.getIndex(), node);
        }

        /**
         * Retrieves a node by its ID.
         *
         * @param id The ID of the node.
         * @return The corresponding Node, or null if not found.
         */
        public static Node forId(int id) {
            return nodeMap.get(id);
        }

        /**
         * Checks if a node with the given ID exists.
         *
         * @param id The ID to check.
         * @return True if no node exists for the given ID, otherwise false.
         */
        public static boolean isEmpty(int id) {
            return !nodeMap.containsKey(id);
        }
    }
}
