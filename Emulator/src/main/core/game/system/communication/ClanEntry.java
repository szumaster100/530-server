package core.game.system.communication;

import core.game.node.entity.player.Player;
import core.game.world.GameWorld;

public class ClanEntry {

    private final String name;

    private Player player;

    private int worldId;

    public ClanEntry(Player player) {
        this.player = player;
        this.name = player.getName();
        this.worldId = GameWorld.getSettings().getWorldId();
    }

    public ClanEntry(String name, int worldId) {
        this.name = name;
        this.worldId = worldId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        ClanEntry e = (ClanEntry) o;
        if (name != null && !name.equals(e.name)) {
            return false;
        }
        return e.player == player;
    }

    public String getName() {
        return name;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }
}