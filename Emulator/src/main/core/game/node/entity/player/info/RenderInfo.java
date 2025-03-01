package core.game.node.entity.player.info;

import core.ServerConfig;
import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.world.map.Location;

import java.util.LinkedList;
import java.util.List;

public final class RenderInfo {

    private final Player player;

    private List<Player> localPlayers = new LinkedList<Player>();

    private List<NPC> localNpcs = new LinkedList<NPC>();

    private final long[] appearanceStamps = new long[ServerConfig.MAX_PLAYERS];

    private Entity[] maskUpdates = new Entity[256];

    private int maskUpdateCount;

    private Location lastLocation;

    private boolean onFirstCycle = true;

    private boolean preparedAppearance;

    public RenderInfo(Player player) {
        this.player = player;
    }

    public void updateInformation() {
        onFirstCycle = false;
        lastLocation = player.getLocation();
        preparedAppearance = false;
    }

    public void registerMaskUpdate(Entity entity) {
        maskUpdates[maskUpdateCount++] = entity;
    }

    public List<NPC> getLocalNpcs() {
        return localNpcs;
    }

    public void setLocalNpcs(List<NPC> localNpcs) {
        this.localNpcs = localNpcs;
    }

    public boolean isOnFirstCycle() {
        return onFirstCycle;
    }

    public void setOnFirstCycle(boolean onFirstCycle) {
        this.onFirstCycle = onFirstCycle;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public List<Player> getLocalPlayers() {
        return localPlayers;
    }

    public void setLocalPlayers(List<Player> localPlayers) {
        this.localPlayers = localPlayers;
    }

    public long[] getAppearanceStamps() {
        return appearanceStamps;
    }

    public int getMaskUpdateCount() {
        return maskUpdateCount;
    }

    public void setMaskUpdateCount(int maskUpdateCount) {
        this.maskUpdateCount = maskUpdateCount;
    }

    public Entity[] getMaskUpdates() {
        return maskUpdates;
    }

    public void setMaskUpdates(Entity[] maskUpdates) {
        this.maskUpdates = maskUpdates;
    }

    public void setPreparedAppearance(boolean prepared) {
        this.preparedAppearance = prepared;
    }

    public boolean preparedAppearance() {
        return preparedAppearance;
    }
}
