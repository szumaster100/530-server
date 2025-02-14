package core.game.activity;

import core.ServerConfig;
import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.game.world.map.Region;
import core.game.world.map.build.DynamicRegion;
import core.game.world.map.zone.*;
import core.game.world.map.zone.impl.MultiwayCombatZone;
import core.plugin.Plugin;
import core.plugin.PluginManifest;
import core.plugin.PluginType;

@PluginManifest(type = PluginType.ACTIVITY)
public abstract class ActivityPlugin extends MapZone implements Plugin<Player> {

    private boolean instanced;

    private boolean multicombat;

    private boolean safe;

    protected DynamicRegion region;

    protected Location base;

    protected Location safeRespawn = ServerConfig.HOME_LOCATION;

    protected Player player;

    public ActivityPlugin(String name, boolean instanced, boolean multicombat, boolean safe, ZoneRestriction... restrictions) {
        super(name, true, ZoneRestriction.RANDOM_EVENTS);
        for (ZoneRestriction restriction : restrictions) {
            addRestriction(restriction.getFlag());
        }
        this.instanced = instanced;
        this.multicombat = multicombat;
        this.safe = safe;
        if (safe) {
            setZoneType(ZoneType.SAFE.getId());
        }
    }

    @Override
    public void register(ZoneBorders borders) {
        if (multicombat) {
            MultiwayCombatZone.Companion.getInstance().register(borders);
        }
        super.register(borders);
    }

    protected void setRegionBase() {
        if (region != null) {
            if (multicombat) {
                region.toggleMulticombat();
            }
            setBase(Location.create(region.getBorders().getSouthWestX(), region.getBorders().getSouthWestY(), 0));
        }
    }

    protected void setRegionBase(DynamicRegion[] regions) {
        region = regions[0];
        Location l = region.getBaseLocation();
        for (DynamicRegion r : regions) {
            if (r.getX() > l.getX() || r.getY() > l.getY()) {
                l = r.getBaseLocation();
            }
        }
        ZoneBorders borders = new ZoneBorders(region.getX() << 6, region.getY() << 6, l.getX() + Region.SIZE, l.getY() + Region.SIZE);
        RegionZone multiZone = multicombat ? new RegionZone(MultiwayCombatZone.Companion.getInstance(), borders) : null;
        RegionZone zone = new RegionZone(this, borders);
        for (DynamicRegion r : regions) {
            if (multicombat) {
                r.setMulticombat(true);
                r.getRegionZones().add(multiZone);
            }
            r.getRegionZones().add(zone);
        }
        setBase(Location.create(borders.getSouthWestX(), borders.getSouthWestY(), 0));
    }

    public boolean start(Player player, boolean login, Object... args) {
        this.player = player;
        return true;
    }

    @Override
    public boolean enter(Entity e) {
        Location l;
        if (e instanceof Player && (l = getSpawnLocation()) != null) {
            e.getProperties().setSpawnLocation(l);
        }
        e.getProperties().setSafeZone(safe);
        e.getProperties().safeRespawn = this.safeRespawn;
        e.setAttribute("activity", this);
        return super.enter(e);
    }

    @Override
    public boolean leave(Entity e, boolean logout) {
        if (e instanceof Player) {
            e.getProperties().setSpawnLocation(ServerConfig.HOME_LOCATION);
        }
        Location l;
        if (instanced && logout && (l = getSpawnLocation()) != null) {
            e.setLocation(l);
        }
        e.getProperties().setSafeZone(false);
        e.getProperties().safeRespawn = ServerConfig.HOME_LOCATION;
        e.removeAttribute("activity");
        return super.leave(e, logout);
    }

    public void register() {
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public abstract ActivityPlugin newInstance(Player p) throws Throwable;

    public abstract Location getSpawnLocation();

    public boolean isInstanced() {
        return instanced;
    }

    public void setInstanced(boolean instanced) {
        this.instanced = instanced;
    }

    public boolean isMulticombat() {
        return multicombat;
    }

    public void setMulticombat(boolean multicombat) {
        this.multicombat = multicombat;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getBase() {
        return base;
    }

    public void setBase(Location base) {
        this.base = base;
    }

}