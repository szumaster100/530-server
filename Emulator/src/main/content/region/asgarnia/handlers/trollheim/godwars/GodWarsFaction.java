package content.region.asgarnia.handlers.trollheim.godwars;

import content.data.God;
import core.game.node.entity.player.Player;

import static core.api.ContentAPIKt.hasGodItem;

public enum GodWarsFaction {

    ARMADYL(6222, 6246, God.ARMADYL),

    BANDOS(6260, 6283, God.BANDOS),

    SARADOMIN(6247, 6259, God.SARADOMIN),

    ZAMORAK(6203, 6221, God.ZAMORAK);

    private final int startId;

    private final int endId;

    private final God god;

    GodWarsFaction(int startId, int endId, God god) {
        this.startId = startId;
        this.endId = endId;
        this.god = god;
    }

    public static GodWarsFaction forId(int npcId) {
        for (GodWarsFaction faction : values()) {
            if (npcId >= faction.getStartId() && npcId <= faction.getEndId()) {
                return faction;
            }
        }
        return null;
    }

    public boolean isProtected(Player player) {
        return hasGodItem(player, god);
    }

    public int getStartId() {
        return startId;
    }

    public int getEndId() {
        return endId;
    }
}