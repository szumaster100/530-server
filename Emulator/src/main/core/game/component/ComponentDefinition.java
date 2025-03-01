package core.game.component;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.sql.SQLException;

public final class ComponentDefinition {

    private static final Int2ObjectOpenHashMap<ComponentDefinition> DEFINITIONS = new Int2ObjectOpenHashMap<>();

    private InterfaceType type = InterfaceType.DEFAULT;

    private boolean walkable = false;

    private int tabIndex = -1;

    private ComponentPlugin plugin;

    public ComponentDefinition() {
        // Empty
    }

    public ComponentDefinition parse(String type, String walkable, String tabIndex) throws SQLException {
        try {
            this.type = InterfaceType.values()[Integer.parseInt(type)];
            this.walkable = Boolean.parseBoolean(walkable);
            this.tabIndex = Integer.parseInt(tabIndex);
        } catch (NumberFormatException e) {
            throw new SQLException("Error parsing ComponentDefinition values", e);
        }
        return this;
    }

    public static ComponentDefinition forId(int componentId) {
        return DEFINITIONS.computeIfAbsent(componentId, id -> new ComponentDefinition());
    }

    public static void put(int id, ComponentPlugin plugin) {
        ComponentDefinition.forId(id).setPlugin(plugin);
    }

    public static Int2ObjectOpenHashMap<ComponentDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public ComponentPlugin getPlugin() {
        return plugin;
    }

    public void setPlugin(ComponentPlugin plugin) {
        this.plugin = plugin;
    }

    public int getWindowPaneId(boolean resizable) {
        return resizable ? type.resizablePaneId : type.fixedPaneId;
    }

    public int getChildId(boolean resizable) {
        return resizable ? type.resizableChildId : type.fixedChildId;
    }

    public InterfaceType getType() {
        return type;
    }

    public void setType(InterfaceType type) {
        this.type = type;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    @Override
    public String toString() {
        return "ComponentDefinition [type=" + type + ", walkable=" + walkable + ", tabIndex=" + tabIndex + ", plugin=" + plugin + "]";
    }
}
