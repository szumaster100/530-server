package core.game.world.map.zone;

import core.game.interaction.Option;
import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.music.MusicZone;
import core.game.node.entity.player.link.request.RequestType;
import core.game.node.item.Item;
import core.game.world.map.Location;
import org.rs.consts.Items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class ZoneMonitor {

    static final Set<Integer> DRAGONSTONE_TELEPORT_JEWELLERY = Set.of(
        Items.AMULET_OF_GLORY_1704,
        Items.AMULET_OF_GLORY1_1706,
        Items.AMULET_OF_GLORY2_1708,
        Items.AMULET_OF_GLORY3_1710,
        Items.AMULET_OF_GLORYT_10362,
        Items.AMULET_OF_GLORYT1_10360,
        Items.AMULET_OF_GLORYT2_10358,
        Items.AMULET_OF_GLORYT3_10356,
        Items.SKILLS_NECKLACE_11113,
        Items.SKILLS_NECKLACE1_11111,
        Items.SKILLS_NECKLACE2_11109,
        Items.SKILLS_NECKLACE3_11107,
        Items.COMBAT_BRACELET_11126,
        Items.COMBAT_BRACELET1_11124,
        Items.COMBAT_BRACELET2_11122,
        Items.COMBAT_BRACELET3_11120
    );

    private final Entity entity;

    private final List<RegionZone> zones = new ArrayList<>(20);

    private final List<MusicZone> musicZones = new ArrayList<>(20);

    public ZoneMonitor(Entity entity) {
        this.entity = entity;
    }

    public int getType() {
        for (RegionZone zone : zones) {
            if (zone.getZone().getZoneType() != 0) {
                return zone.getZone().getZoneType();
            }
        }
        return 0;
    }

    public boolean canLogout() {
        for (RegionZone z : zones) {
            if (!z.getZone().canLogout((Player) entity)) {
                return false;
            }
        }
        return true;
    }

    public boolean isRestricted(ZoneRestriction restriction) {
        return isRestricted(restriction.getFlag());
    }

    public boolean isRestricted(int flag) {
        for (RegionZone z : zones) {
            if (z.getZone().isRestricted(flag)) {
                return true;
            }
        }
        return false;
    }

    public boolean handleDeath(Entity killer) {
        for (RegionZone z : zones) {
            if (z.getZone().death(entity, killer)) {
                return true;
            }
        }
        return false;
    }

    public boolean continueAttack(Node target, CombatStyle style, boolean message) {
        if (target instanceof Entity) {
            if (!entity.continueAttack((Entity) target, style, message)) {
                return false;
            }
        }
        for (RegionZone z : zones) {
            if (!z.getZone().continueAttack(entity, target, style, message)) {
                return false;
            }
        }
        if (entity instanceof Player && target instanceof Player) {
            if (!((Player) entity).getSkullManager().isWilderness() || !((Player) target).getSkullManager().isWilderness()) {
                if (message) {
                    ((Player) entity).getPacketDispatch().sendMessage("You can only attack other players in the wilderness.");
                }
                return false;
            }
        }
        if (target instanceof Entity && !MapZone.checkMulti(entity, (Entity) target, message)) {
            return false;
        }
        return true;
    }

    public boolean interact(Node target, Option option) {
        for (RegionZone z : zones) {
            if (z.getZone().interact(entity, target, option)) {
                return true;
            }
        }
        return false;
    }

    public boolean useWith(Item used, Node with) {
        for (RegionZone z : zones) {
            if (z.getZone().handleUseWith(entity.asPlayer(), used, with)) {
                return true;
            }
        }
        return false;
    }

    public boolean clickButton(int interfaceId, int buttonId, int slot, int itemId, int opcode) {
        for (RegionZone z : zones) {
            if (z.getZone().actionButton((Player) entity, interfaceId, buttonId, slot, itemId, opcode)) {
                return true;
            }
        }
        return false;
    }

    public boolean isIgnoreMultiBoundaries(Entity victim) {
        for (RegionZone z : zones) {
            if (z.getZone().ignoreMultiBoundaries(entity, victim)) {
                return true;
            }
        }
        return false;
    }

    public boolean teleport(int type, Node node) {
        if (type != -1 && entity.isTeleBlocked() && !canTeleportByJewellery(type, node)) {
            if (entity.isPlayer()) {
                entity.asPlayer().sendMessage("A magical force has stopped you from teleporting.");
            }
            return false;
        }
        for (RegionZone z : zones) {
            if (!z.getZone().teleport(entity, type, node)) {
                return false;
            }
        }
        return true;
    }

    private boolean canTeleportByJewellery(int type, Node node) {
        if (type != 1 || !DRAGONSTONE_TELEPORT_JEWELLERY.contains(node.asItem().getId())) {
            return false;
        }
        if (entity.timers.getTimer("teleblock") != null)
            return false;

        if (entity.getZoneMonitor().isRestricted(ZoneRestriction.TELEPORT)) {
            return false;
        }

        if (entity.getLocks().isTeleportLocked()) {
            if (entity.isPlayer()) {
                Player p = entity.asPlayer();
                if (p.getSkullManager().getLevel() >= 1 && p.getSkullManager().getLevel() <= 30) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean startDeath(Entity entity, Entity killer) {
        for (RegionZone z : zones) {
            if (!z.getZone().startDeath(entity, killer)) {
                return false;
            }
        }
        return true;
    }

    public boolean canFireRandomEvent() {
        for (RegionZone z : zones) {
            if (!z.getZone().isFireRandoms()) {
                return false;
            }
        }
        return true;
    }

    public boolean clear() {
        for (RegionZone z : zones) {
            if (!z.getZone().leave(entity, true)) {
                return false;
            }
        }
        for (MusicZone z : musicZones) {
            z.leave(entity, true);
        }
        zones.clear();
        musicZones.clear();
        return true;
    }

    public boolean move(Location location, Location destination) {
        for (RegionZone z : zones) {
            if (!z.getZone().move(entity, location, destination)) {
                return false;
            }
        }
        return true;
    }

    public boolean updateLocation(Location last) {
        if (entity instanceof Player && !entity.asPlayer().isArtificial()) {
            checkMusicZones();
        }
        entity.updateLocation(last);
        for (Iterator<RegionZone> it = zones.iterator(); it.hasNext(); ) {
            RegionZone zone = it.next();
            if (!zone.getBorders().insideBorder(entity)) {
                if (zone.getZone().isDynamicZone()) {
                    continue;
                }
                if (!zone.getZone().leave(entity, false)) {
                    return false;
                }
                it.remove();
            }
        }
        for (RegionZone zone : entity.getViewport().getRegion().getRegionZones()) {
            if (!zone.getBorders().insideBorder(entity)) {
                continue;
            }
            boolean alreadyEntered = false;
            for (RegionZone z : zones) {
                if (z.getZone() == zone.getZone()) {
                    alreadyEntered = true;
                    break;
                }
            }
            if (alreadyEntered) {
                zone.getZone().locationUpdate(entity, last);
                continue;
            }
            if (!zone.getZone().enter(entity)) {
                return false;
            }
            zones.add(zone);
            zone.getZone().locationUpdate(entity, last);
        }
        return true;
    }

    public void checkMusicZones() {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        Location l = player.getLocation();
        for (Iterator<MusicZone> it = musicZones.iterator(); it.hasNext(); ) {
            MusicZone zone = it.next();
            if (!zone.getBorders().insideBorder(l.getX(), l.getY())) {
                zone.leave(player, false);
                it.remove();
            }
        }
        for (MusicZone zone : player.getViewport().getRegion().getMusicZones()) {
            if (!zone.getBorders().insideBorder(l.getX(), l.getY())) {
                continue;
            }
            if (!musicZones.contains(zone)) {
                zone.enter(player);
                musicZones.add(zone);
            }
        }
        if (musicZones.isEmpty() && !player.getMusicPlayer().isPlaying()) {
            player.getMusicPlayer().playDefault();
        }
    }

    public boolean parseCommand(Player player, String name, String[] arguments) {
        for (RegionZone zone : zones) {
            if (zone.getZone().parseCommand(player, name, arguments)) {
                return true;
            }
        }
        return false;
    }

    public boolean canRequest(RequestType type, Player target) {
        for (RegionZone zone : zones) {
            if (!zone.getZone().canRequest(type, entity.asPlayer(), target)) {
                return false;
            }
        }
        return true;
    }

    public boolean isInZone(String name) {
        int uid = name.hashCode();
        for (RegionZone zone : zones) {
            if (zone.getZone().getUid() == uid) {
                return true;
            }
        }
        return false;
    }

    public void remove(MapZone zone) {
        for (Iterator<RegionZone> it = zones.iterator(); it.hasNext(); ) {
            if (it.next().getZone() == zone) {
                it.remove();
                break;
            }
        }
    }

    public List<RegionZone> getZones() {
        return zones;
    }

    public List<MusicZone> getMusicZones() {
        return musicZones;
    }

}
