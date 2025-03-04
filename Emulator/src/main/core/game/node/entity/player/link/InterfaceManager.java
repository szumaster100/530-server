package core.game.node.entity.player.link;

import content.data.GameAttributes;
import content.region.misc.handlers.tutorial.TutorialStage;
import core.game.component.Component;
import core.game.component.InterfaceType;
import core.game.event.InterfaceCloseEvent;
import core.game.event.InterfaceOpenEvent;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.game.world.GameWorld;
import core.net.packet.PacketRepository;
import core.net.packet.context.InterfaceContext;
import core.net.packet.context.WindowsPaneContext;
import core.net.packet.out.CloseInterface;
import core.net.packet.out.Interface;
import core.net.packet.out.WindowsPane;
import core.tools.Log;
import org.rs.consts.Components;
import org.rs.consts.Quests;

import static core.api.ContentAPIKt.log;
import static core.api.ContentAPIKt.setVarp;
import static core.api.quest.QuestAPIKt.isQuestComplete;

public final class InterfaceManager {

    public static final int WINDOWS_PANE = Components.TOPLEVEL_548;

    public static final int DEFAULT_CHATBOX = Components.CHATDEFAULT_137;

    public static final int[] DEFAULT_TABS = {Components.WEAPON_FISTS_SEL_92, Components.STATS_320, Components.QUESTJOURNAL_V2_274,
        Components.INVENTORY_149, Components.WORNITEMS_387, Components.PRAYER_271, Components.MAGIC_192, Components.LORE_STATS_SIDE_662,
        Components.FRIENDS2_550, Components.IGNORE2_551, Components.CLANJOIN_589, Components.OPTIONS_261, Components.EMOTES_464,
        Components.MUSIC_V3_187, Components.LOGOUT_182};

    private final Player player;

    private int packetCount;

    private Component windowsPane;

    private Component opened;

    private Component[] tabs = new Component[15];

    private Component chatbox;

    private Component singleTab;

    private Component overlay;

    private Component wildyOverlay;

    private int currentTabIndex = 3;

    public InterfaceManager(Player player) {
        this.player = player;
    }

    public Component openWindowsPane(Component windowsPane) {
        return openWindowsPane(windowsPane, false);
    }

    public Component openWindowsPane(Component windowsPane, boolean overlap) {
        this.windowsPane = windowsPane;
        if (windowsPane.getDefinition().getType() != InterfaceType.WINDOW_PANE) {
            log(this.getClass(), Log.WARN, "Set interface type to WINDOW_PANE for component " + windowsPane.getId() + ", definition requires updating!");
            windowsPane.getDefinition().setType(InterfaceType.WINDOW_PANE);
        }

        PacketRepository.send(WindowsPane.class, new WindowsPaneContext(player, windowsPane.getId(), overlap ? 1 : 0));
        windowsPane.open(player);

        if (opened != null)
            player.dispatch(new InterfaceOpenEvent(opened));

        return windowsPane;
    }

    public void openWindowsPane(Component windowsPane, int type) {
        this.windowsPane = windowsPane;
        if (windowsPane.getDefinition().getType() != InterfaceType.WINDOW_PANE) {
            log(this.getClass(), Log.WARN, "Set interface type to WINDOW_PANE for component " + windowsPane.getId() + ", definition requires updating!");
            windowsPane.getDefinition().setType(InterfaceType.SINGLE_TAB);
        }

        PacketRepository.send(WindowsPane.class, new WindowsPaneContext(player, windowsPane.getId(), type));
        windowsPane.open(player);

        if (opened != null)
            player.dispatch(new InterfaceOpenEvent(opened));
    }

    public Component openComponent(int componentId) {
        return open(new Component(componentId));
    }

    public Component open(Component component) {
        if (!close()) {
            return null;
        }
        component.open(player);

        opened = component;
        player.dispatch(new InterfaceOpenEvent(opened));

        return opened;
    }

    public boolean isOpened() {
        return opened != null;
    }

    public boolean hasChatbox() {
        return chatbox != null && chatbox.getId() != DEFAULT_CHATBOX;
    }

    public boolean close() {
        if (player.getAttribute("runscript", null) != null) {
            player.removeAttribute("runscript");
            player.getPacketDispatch().sendRunScript(101, "");
        }
        // Component 333 is an immediate(no-fading) full-screen HD-mode black screen which auto-clears when interrupted.
        if (overlay != null && overlay.getId() == 333) {
            closeOverlay();
        }
        if (opened != null && opened.close(player)) {
            if (opened != null && (!opened.getDefinition().isWalkable() || opened.getId() == 14)) {
                PacketRepository.send(CloseInterface.class, new InterfaceContext(player, opened.getDefinition().getWindowPaneId(isResizable()), opened.getDefinition().getChildId(isResizable()), opened.getId(), opened.getDefinition().isWalkable()));
                player.dispatch(new InterfaceCloseEvent(opened));
            }
            opened = null;
        }
        return opened == null;
    }

    public boolean isWalkable() {
        if (opened != null) {
            if (opened.getId() == Components.OBJDIALOG_389) {
                return false;
            }
            if (opened.getDefinition().isWalkable()) {
                return true;
            }
        }
        return true;
    }

    public boolean close(Component component) {
        if (component.close(player)) {
            if (component.getId() == DEFAULT_CHATBOX) {
                return true;
            }
            if (component.getDefinition().getType() == InterfaceType.TAB) {
                PacketRepository.send(CloseInterface.class, new InterfaceContext(player, component.getDefinition().getWindowPaneId(isResizable()), component.getDefinition().getChildId(isResizable()) + component.getDefinition().getTabIndex(), component.getId(), component.getDefinition().isWalkable()));
                return true;
            }
            PacketRepository.send(CloseInterface.class, new InterfaceContext(player, component.getDefinition().getWindowPaneId(isResizable()), component.getDefinition().getChildId(isResizable()), component.getId(), component.getDefinition().isWalkable()));
            return true;
        }
        return false;
    }

    public void closeChatbox() {
        if (chatbox != null && chatbox.getId() != DEFAULT_CHATBOX) {
            if (close(chatbox)) {
                openChatbox(DEFAULT_CHATBOX);
                player.getPacketDispatch().sendRunScript(101, "");
            }
        }
    }

    public Component openSingleTab(Component component) {
        if (component.getDefinition().getType() != InterfaceType.SINGLE_TAB) {
            log(this.getClass(), Log.WARN, "Set interface type to SINGLE_TAB for component " + component.getId() + ", definition requires updating!");
            component.getDefinition().setType(InterfaceType.SINGLE_TAB);
        }
        component.open(player);
        if (component.getCloseEvent() == null) {
            component.setCloseEvent((player, c) -> {
//				openDefaultTabs();
                return true;
            });
        }
        return singleTab = component;
    }

    public boolean closeSingleTab() {
        if (singleTab != null && close(singleTab)) {
            singleTab = null;
        }
        return true;
    }

    public Component getSingleTab() {
        return singleTab;
    }

    public void removeTabs(int... tabs) {
        boolean changeViewedTab = false;
        for (int slot : tabs) {
            if (slot == currentTabIndex) {
                changeViewedTab = true;
            }
            Component tab = this.tabs[slot];
            if (tab != null) {
                close(tab);
                this.tabs[slot] = null;
            }
        }
        if (changeViewedTab) {
            int currentIndex = -1;
            if (this.tabs[3] == null) {
                for (int i = 0; i < this.tabs.length; i++) {
                    if (this.tabs[i] != null) {
                        currentIndex = i;
                        break;
                    }
                }
            } else {
                currentIndex = 3;
            }
            if (currentIndex > -1) {
                setViewedTab(currentIndex);
            }
        }
    }

    public void restoreTabs() {
        for (int i = 0; i < tabs.length; i++) {
            Component tab = tabs[i];
            if (tab == null) {
                switch (i) {
                    case 0:
                        WeaponInterface inter = player.getExtension(WeaponInterface.class);
                        if (inter == null) {
                            player.addExtension(WeaponInterface.class, inter = new WeaponInterface(player));
                        }
                        openTab(0, inter);
                        break;
                    case 6:
                        openTab(6, new Component(player.getSpellBookManager().getSpellBook())); // Magic
                        break;
                    case 7:
                        if (player.getFamiliarManager().hasFamiliar()) {
                            openTab(7, new Component(662));
                        }
                        break;
                    default:
                        openTab(i, new Component(DEFAULT_TABS[i]));
                }
            } else if (tab.isHidden()) {
                int child = (i < 7 ? 38 : 13) + i;
//				boolean resize = isResizable(); //TODO:
                player.getPacketDispatch().sendInterfaceConfig(getWindowPaneId(), child, false);
                player.getPacketDispatch().sendInterfaceConfig(getWindowPaneId(), child + 7, false);
                tabs[i].setHidden(false);
            }
        }
    }

    public void openDefaultTabs() {
        WeaponInterface inter = player.getExtension(WeaponInterface.class);
        if (inter == null) {
            player.addExtension(WeaponInterface.class, inter = new WeaponInterface(player));
        }
        openTab(0, inter);
        openTab(1, new Component(Components.STATS_320));
        openTab(2, new Component(Components.QUESTJOURNAL_V2_274));
        openTab(3, new Component(Components.INVENTORY_149));
        openTab(4, new Component(Components.WORNITEMS_387));
        openTab(5, new Component(Components.PRAYER_271));
        openTab(6, new Component(player.getSpellBookManager().getSpellBook()));
        if (player.getFamiliarManager().hasFamiliar()) {
            openTab(7, new Component(Components.LORE_STATS_SIDE_662));
        }
        openTab(8, new Component(Components.FRIENDS2_550));
        openTab(9, new Component(Components.IGNORE2_551));
        openTab(10, new Component(Components.CLANJOIN_589));
        openTab(11, new Component(Components.OPTIONS_261));
        openTab(12, new Component(Components.EMOTES_464));
        openTab(13, new Component(Components.MUSIC_V3_187));
        openTab(14, new Component(Components.LOGOUT_182));
        if (player.getProperties().getAutocastSpell() != null) {
            inter.selectAutoSpell(inter.getAutospellId(player.getProperties().getAutocastSpell().spellId), true);
        }
    }

    public void openInfoBars() {
        //Hp orb
        PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 13 : 70, Components.TOPSTAT_HITPOINTS_748, true));
        //Prayer orb
        PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 14 : 71, Components.TOPSTAT_PRAYER_749, true));
        //Energy orb
        PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 15 : 72, Components.TOPSTAT_RUN_750, true));
        //Summoning bar
        if(GameWorld.getSettings().isMembers() && isQuestComplete(player, Quests.WOLF_WHISTLE)) {
            PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 16 : 73, Components.TOPSTAT_LORE_747, true));
        }
        //Split PM
        PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 71 : 10, Components.PMCHAT_754, true));
    }

    public void closeDefaultTabs() {
        WeaponInterface inter = player.getExtension(WeaponInterface.class);
        if (inter != null) {
            close(inter); // Attack
        }
        close(new Component(Components.STATS_320)); // Skills
        close(new Component(Components.QUESTJOURNAL_V2_274)); // Quest
        close(new Component(Components.AREA_TASK_259)); // Diary
        close(new Component(Components.INVENTORY_149)); // inventory
        close(new Component(Components.WORNITEMS_387)); // Equipment
        close(new Component(Components.PRAYER_271)); // Prayer
        close(new Component(player.getSpellBookManager().getSpellBook()));
        close(new Component(Components.LORE_STATS_SIDE_662)); // summoning.
        close(new Component(Components.FRIENDS2_550)); // Friends
        close(new Component(Components.IGNORE2_551)); // Ignores
        close(new Component(Components.CLANJOIN_589)); // Clan chat
        close(new Component(Components.OPTIONS_261)); // Settings
        close(new Component(Components.EMOTES_464)); // Emotes
        close(new Component(Components.MUSIC_V3_187)); // Music
        //close(new Component(Components.LOGOUT_182)); // Logout
    }

    public void openTab(int slot, Component component) {
        if (component.getId() == Components.WEAPON_FISTS_SEL_92 && !(component instanceof WeaponInterface)) {
            throw new IllegalStateException("Attack tab can only be instanced as " + WeaponInterface.class.getCanonicalName() + "!");
        }
        if (component.getDefinition().getTabIndex() != slot) {
            log(this.getClass(), Log.WARN, "Set tab index to " + slot + " for component " + component.getId() + ", definition requires updating!");
            component.getDefinition().setTabIndex(slot);
        }
        if (component.getDefinition().getType() != InterfaceType.TAB) {
            log(this.getClass(), Log.WARN, "Set interface type to TAB for component " + component.getId() + ", definition requires updating!");
            component.getDefinition().setType(InterfaceType.TAB);
        }
        component.open(player);
        tabs[slot] = component;
    }

    public void openTab(Component component) {
        if (component.getDefinition().getTabIndex() < 0) {
            log(this.getClass(), Log.WARN, "No component definitions found for tab " + component.getId() + "!");
            return;
        }
        openTab(component.getDefinition().getTabIndex(), component);
    }

    public void openChatbox(int componentId) {
        openChatbox(new Component(componentId));
    }

    public void openChatbox(Component component) {
        if (component.getId() == DEFAULT_CHATBOX) {
            if (chatbox == null || (chatbox.getId() != DEFAULT_CHATBOX && chatbox.getDefinition().getType() == InterfaceType.CHATBOX)) {
                PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 23 : 14, Components.FILTERBUTTONS_751, true));
                PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 70 : 75, Components.CHATTOP_752, true));
                PacketRepository.send(Interface.class, new InterfaceContext(player, InterfaceType.CHATBOX.fixedPaneId, InterfaceType.CHATBOX.fixedChildId, Components.CHATDEFAULT_137, true));
            }
            chatbox = component;
            setVarp(player, 334, 1);
        } else {
            chatbox = component;
            if (chatbox.getDefinition().getType() != InterfaceType.DIALOGUE && chatbox.getDefinition().getType() != InterfaceType.CHATBOX && chatbox.getDefinition().getType() != InterfaceType.CS_CHATBOX) {
                log(this.getClass(), Log.WARN, "Set interface type to CHATBOX for component " + component.getId() + ", definition requires updating!");
                chatbox.getDefinition().setType(InterfaceType.DIALOGUE);
            }
            chatbox.open(player);
        }
    }

    public void switchWindowMode(int windowMode) {
        if (windowMode != player.getSession().getClientInfo().getWindowMode()) {
            player.getSession().getClientInfo().setWindowMode(windowMode);
            openWindowsPane(new Component(isResizable() ? Components.TOPLEVEL_FULLSCREEN_746 : Components.TOPLEVEL_548));
            if (!player.getAttribute(GameAttributes.TUTORIAL_COMPLETE, false)) {
                TutorialStage.hideTabs(player, false);
            } else {
                openDefaultTabs();
            }
            openInfoBars();
            PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 23 : 14, Components.FILTERBUTTONS_751, true));
            PacketRepository.send(Interface.class, new InterfaceContext(player, getWindowPaneId(), isResizable() ? 70 : 75, Components.CHATTOP_752, true));
        }
    }

    public Component getComponent(int componentId) {
        if (opened != null && opened.getId() == componentId) {
            return opened;
        }
        if (chatbox != null && chatbox.getId() == componentId) {
            return chatbox;
        }
        if (singleTab != null && singleTab.getId() == componentId) {
            return singleTab;
        }
        if (overlay != null && overlay.getId() == componentId) {
            return overlay;
        }
        if (windowsPane.getId() == componentId) {
            return windowsPane;
        }
        for (Component c : tabs) {
            if (c != null && c.getId() == componentId) {
                return c;
            }
        }
        if (componentId == Components.FILTERBUTTONS_751 || componentId == Components.TOPSTAT_RUN_750 || componentId == Components.TOPSTAT_LORE_747) {
            //Chatbox settings, run orb & summoning orb.
            return new Component(componentId);
        }
        return null;
    }

    public void setViewedTab(int tabIndex) {
        if (tabs[tabIndex] == null) {
            return;
        }
        currentTabIndex = tabIndex;
        switch (tabIndex) {
            case 0:
                tabIndex = 1;
                break;
            case 1:
                tabIndex = 2;
                break;
            case 2:
                tabIndex = 3;
                break;
        }
        if (tabIndex > 9) {
            tabIndex--;
        }
        player.getPacketDispatch().sendRunScript(115, "i", tabIndex);
    }

    public boolean hasMainComponent(int id) {
        return opened != null && opened.getId() == id;
    }

    public void openOverlay(Component component) {
        if (overlay != null && !overlay.close(player)) {
            return;
        }
        overlay = component;
        if (overlay.getDefinition().getType() != InterfaceType.OVERLAY && overlay.getDefinition().getType() != InterfaceType.OVERLAY_B) {
            log(this.getClass(), Log.WARN, "Set interface type to OVERLAY for component " + component.getId() + ", definition requires updating!");
            overlay.getDefinition().setType(InterfaceType.OVERLAY);
            overlay.getDefinition().setWalkable(true);
        }
        overlay.open(player);
    }

    public void closeOverlay() {
        if (overlay != null && close(overlay)) {
            overlay = null;
        }
    }

    public WeaponInterface getWeaponTab() {
        return player.getExtension(WeaponInterface.class);
    }

    public Component getOpened() {
        return opened;
    }

    public void setOpened(Component opened) {
        this.opened = opened;
    }

    public Component[] getTabs() {
        return tabs;
    }

    public void setTabs(Component[] tabs) {
        this.tabs = tabs;
    }

    public Component getChatbox() {
        return chatbox;
    }

    public void setChatbox(Component chatbox) {
        this.chatbox = chatbox;
    }

    public Component getOverlay() {
        return overlay;
    }

    public void setOverlay(Component overlay) {
        this.overlay = overlay;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int currentTabIndex) {
        this.currentTabIndex = currentTabIndex;
    }

    public Component getWindowsPane() {
        return windowsPane;
    }

    public int getWindowPaneId() {
        if (windowsPane == null) {
            return Components.TOPLEVEL_548;
        }
        return windowsPane.getId();
    }

    public int getDefaultChildId() {
        return isResizable() ? 6 : 11;
    }

    public boolean isResizable() {
        if (player.getSession().getClientInfo() == null) {
            return false;
        }
        return player.getSession().getClientInfo().isResizable();
    }

    public int getPacketCount(int increment) {
        int count = packetCount;
        packetCount += increment;
        return count;
    }
}
