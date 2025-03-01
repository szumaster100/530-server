package core.game.world.map.zone;

import content.global.skill.summoning.familiar.Familiar;
import core.game.interaction.Option;
import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.request.RequestType;
import core.game.node.item.Item;
import core.game.world.map.Location;
import core.game.world.map.Region;
import core.game.world.map.RegionManager;

import java.util.Iterator;
import java.util.Objects;

public abstract class MapZone implements Zone {

    private int uid;

    private String name;

    private boolean overlappable;

    protected boolean fireRandomEvents;

    private int restriction;

    private int zoneType;

    public MapZone(String name, boolean overlappable, ZoneRestriction... restrictions) {
        this.name = name;
        this.overlappable = overlappable;
        for (ZoneRestriction restriction : restrictions) {
            addRestriction(restriction.getFlag());
        }
    }

    @Override
    public boolean enter(Entity e) {
        if (e instanceof Player) {
            Player p = (Player) e;
        } else if (e instanceof NPC) {
            NPC npc = (NPC) e;
            if (e instanceof Familiar && isRestricted(ZoneRestriction.FOLLOWERS.getFlag())) {
                npc.setInvisible(true);
            }
        }
        return true;
    }

    @Override
    public boolean leave(Entity e, boolean logout) {
        return true;
    }

    public boolean canLogout(Player p) {
        return true;
    }

    public boolean death(Entity e, Entity killer) {
        return false;
    }

    public boolean interact(Entity e, Node target, Option option) {
        return false;
    }

    public boolean handleUseWith(Player player, Item used, Node with) {
        return false;
    }

    public boolean actionButton(Player player, int interfaceId, int buttonId, int slot, int itemId, int opcode) {
        return false;
    }

    public boolean continueAttack(Entity e, Node target, CombatStyle style, boolean message) {
        return true;
    }

    public boolean ignoreMultiBoundaries(Entity attacker, Entity victim) {
        return false;
    }

    public static boolean checkMulti(Entity e, Entity t, boolean message) {
        long time = System.currentTimeMillis();
        boolean multi = t.getProperties().isMultiZone() && e.getProperties().isMultiZone();
        if (multi || e.isIgnoreMultiBoundaries(t) || e.getZoneMonitor().isIgnoreMultiBoundaries(t)) {
            return true;
        }
        Entity target = t.getAttribute("combat-attacker", e);
        if (t.getAttribute("combat-time", -1L) > time && target != e && target.isActive()) {
            if (message && e instanceof Player) {
                ((Player) e).getPacketDispatch().sendMessage("Someone else is already fighting this" + (t instanceof Player ? " player." : "."));
            }
            return false;
        }
        if (e.getAttribute("combat-time", -1L) > time && (target = e.getAttribute("combat-attacker", t)) != t && target.isActive()) {
            if (t.getId() == 1614 || t.getId() == 1613) {
                return true;
            }
            if (message && e instanceof Player) {
                ((Player) e).getPacketDispatch().sendMessage("You're already under attack!");
            }
            return false;
        }
        return true;
    }

    public boolean teleport(Entity e, int type, Node node) {
        return true;
    }

    public boolean startDeath(Entity e, Entity killer) {
        return true;
    }

    public boolean canRequest(RequestType type, Player player, Player target) {
        return true;
    }

    public boolean move(Entity e, Location from, Location to) {
        return true;
    }

    public boolean parseCommand(Player player, String name, String[] arguments) {
        return false;
    }

    public void locationUpdate(Entity e, Location last) {

    }

    public void configure() {
    }

    ;

    public void cleanItems(Player player, Item[] items) {
        if (player == null) {
            return;
        }
        for (Item item : items) {
            if (item == null) {
                continue;
            }
            if (player.getInventory().containsItem(item)) {
                player.getInventory().remove(new Item(item.getId(), player.getInventory().getAmount(item)));
            }
            if (player.getEquipment().containsItem(item)) {
                player.getEquipment().remove(new Item(item.getId(), player.getEquipment().getAmount(item)));
            }
            if (player.getBank().containsItem(item)) {
                player.getBank().remove(new Item(item.getId(), player.getBank().getAmount(item)));
            }
        }
    }

    protected static void message(Entity e, String message) {
        if (!(e instanceof Player)) {
            return;
        }
        ((Player) e).getPacketDispatch().sendMessage(message);
    }

    public void register(ZoneBorders borders) {
        for (Integer id : borders.getRegionIds()) {
            Region r = RegionManager.forId(id);
            if (r != null) {
                r.add(new RegionZone(this, borders));
            }
        }
    }

    public void unregister(ZoneBorders borders) {
        for (Integer id : borders.getRegionIds()) {
            Region r = RegionManager.forId(id);
            if (r != null) {
                r.remove(new RegionZone(this, borders));
            }
        }
    }

    public void registerRegion(int regionId) {
        register(ZoneBorders.forRegion(regionId));
    }

    public void registerRegion(int regionId, ZoneBorders borders) {
        Region r = RegionManager.forId(regionId);
        if (r != null) {
            r.add(new RegionZone(this, borders));
        }
    }

    public void unregisterRegion(int regionId) {
        Region r = RegionManager.forId(regionId);
        if (r != null) {
            for (Iterator<RegionZone> it = r.getRegionZones().iterator(); it.hasNext(); ) {
                if (it.next().getZone() == this) {
                    it.remove();
                }
            }
        }
    }

    public void disableRandomEvents() {
        this.fireRandomEvents = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOverlappable() {
        return overlappable;
    }

    public void setOverlappable(boolean overlappable) {
        this.overlappable = overlappable;
    }

    public int getUid() {
        return getName().hashCode();
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isFireRandoms() {
        return fireRandomEvents;
    }

    public boolean isDynamicZone() {
        return false;
    }

    public void addRestriction(ZoneRestriction restriction) {
        addRestriction(restriction.getFlag());
    }

    public void addRestriction(int flag) {
        restriction |= flag;
    }

    public boolean isRestricted(int flag) {
        return (restriction & flag) != 0;
    }

    public int getRestriction() {
        return restriction;
    }

    public int getZoneType() {
        return zoneType;
    }

    public void setZoneType(int type) {
        this.zoneType = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapZone mapZone = (MapZone) o;
        return getUid() == mapZone.getUid();
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, name, overlappable, fireRandomEvents, restriction, zoneType);
    }
}