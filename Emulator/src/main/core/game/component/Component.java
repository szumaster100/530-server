package core.game.component;

import core.api.Container;
import core.game.container.Slot;
import core.game.interaction.InterfaceListeners;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.InterfaceManager;
import core.net.packet.PacketRepository;
import core.net.packet.context.InterfaceContext;
import core.net.packet.out.Interface;

import java.util.ArrayList;
import java.util.List;

public class Component {

    protected int id;

    protected int childId = -1;

    private final List<Slot> slots = new ArrayList<>();

    protected final ComponentDefinition definition;

    protected CloseEvent closeEvent;

    protected ComponentPlugin plugin;

    private boolean hidden;

    public Component(int id) {
        this.id = id;
        this.definition = ComponentDefinition.forId(id);
        this.plugin = definition != null ? definition.getPlugin() : null;
    }

    public Component(int id, int childId) {
        this.id = id;
        this.childId = childId;
        this.definition = ComponentDefinition.forId(id);
        this.plugin = definition != null ? definition.getPlugin() : null;
    }

    public void open(Player player) {
        InterfaceManager manager = player.getInterfaceManager();
        InterfaceListeners.runOpen(player, this);

        if (definition == null) {
            PacketRepository.send(Interface.class, new InterfaceContext(player, manager.getWindowPaneId(), manager.getDefaultChildId(), getId(), false));
            if (plugin != null) {
                plugin.open(player, this);
            }
            return;
        }

        if (definition.getType() == InterfaceType.WINDOW_PANE) {
            return;
        }

        if (definition.getType() == InterfaceType.TAB) {
            PacketRepository.send(Interface.class, new InterfaceContext(player, definition.getWindowPaneId(manager.isResizable()), definition.getChildId(manager.isResizable()) + definition.getTabIndex(), getId(), definition.isWalkable()));
            if (plugin != null) {
                plugin.open(player, this);
            }
            return;
        }

        PacketRepository.send(Interface.class, new InterfaceContext(player, definition.getWindowPaneId(manager.isResizable()), definition.getChildId(manager.isResizable()), getId(), definition.isWalkable()));
        if (plugin != null) {
            plugin.open(player, this);
        }
    }

    public boolean close(Player player) {
        return (closeEvent == null || closeEvent.close(player, this)) && InterfaceListeners.runClose(player, this);
    }

    public boolean handleSlotSwitch(Player player, int sourceSlot, int destSlot) {
        return InterfaceListeners.runSlotSwitch(player, this, sourceSlot, destSlot);
    }

    public void addSlot(int slotId, Container container) {
        slots.add(new Slot(slotId, container));
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public int getId() {
        return id;
    }

    public int getChildId() {
        return childId;
    }

    public ComponentDefinition getDefinition() {
        return definition;
    }

    public CloseEvent getCloseEvent() {
        return closeEvent;
    }

    public Component setCloseEvent(CloseEvent closeEvent) {
        this.closeEvent = closeEvent;
        return this;
    }

    public static void setUnclosable(Player player, Component component) {
        player.setAttribute("close_c_", true);
        component.setCloseEvent(new CloseEvent() {
            @Override
            public boolean close(Player player, Component component) {
                return !player.getAttribute("close_c_", false);
            }
        });
    }

    public void setPlugin(ComponentPlugin plugin) {
        this.plugin = plugin;
    }

    public ComponentPlugin getPlugin() {
        if (plugin == null) {
            ComponentPlugin p = ComponentDefinition.forId(getId()).getPlugin();
            if ((plugin = p) != null) {
                return p;
            }
        }
        return plugin;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }
}
