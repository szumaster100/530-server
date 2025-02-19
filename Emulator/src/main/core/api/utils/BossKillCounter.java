package core.api.utils;

import core.game.node.entity.player.Player;
import core.tools.StringUtils;
import org.rs.consts.NPCs;

public enum BossKillCounter {

    KING_BLACK_DRAGON(
            new int[]{NPCs.KING_BLACK_DRAGON_50},
            "King Black Dragon"
    ),
    BORK(
            new int[]{NPCs.BORK_7133, NPCs.BORK_7134},
            "Bork"
    ),
    DAGANNOTH_SUPREME(
            new int[]{NPCs.DAGANNOTH_SUPREME_2881},
            "Dagannoth Supreme"
    ),
    DAGANNOTH_PRIME(
            new int[]{NPCs.DAGANNOTH_PRIME_2882},
            "Dagannoth Prime"
    ),
    DAGANNOTH_REX(
            new int[]{NPCs.DAGANNOTH_REX_2883},
            "Dagannoth Rex"
    ),
    CHAOS_ELEMENTAL(
            new int[]{NPCs.CHAOS_ELEMENTAL_3200},
            "Chaos Elemental"
    ),
    GIANT_MOLE(
            new int[]{NPCs.GIANT_MOLE_3340},
            "Giant Mole"
    ),
    SARADOMIN(
            new int[]{NPCs.COMMANDER_ZILYANA_6247},
            "Commander Zilyana"
    ),
    ZAMORAK(
            new int[]{NPCs.KRIL_TSUTSAROTH_6203},
            "K'ril Tsutsaroth"
    ),
    BANDOS(
            new int[]{NPCs.GENERAL_GRAARDOR_6260},
            "General Graardor"
    ),
    ARMADYL(
            new int[]{NPCs.KREEARRA_6222},
            "Kree'arra"
    ),
    JAD(
            new int[]{NPCs.TZTOK_JAD_2745},
            "Tz-Tok Jad"
    ),
    KALPHITE_QUEEN(
            new int[]{NPCs.KALPHITE_QUEEN_1160},
            "Kalphite Queen"
    ),
    CORPOREAL_BEAST(
            new int[]{NPCs.CORPOREAL_BEAST_8133},
            "Corporeal Beast"
    ),
    TORMENTED_DEMONS(
            new int[]{
                    NPCs.TORMENTED_DEMON_8349, NPCs.TORMENTED_DEMON_8350, NPCs.TORMENTED_DEMON_8351, NPCs.TORMENTED_DEMON_8352,
                    NPCs.TORMENTED_DEMON_8353, NPCs.TORMENTED_DEMON_8354, NPCs.TORMENTED_DEMON_8355, NPCs.TORMENTED_DEMON_8356,
                    NPCs.TORMENTED_DEMON_8357, NPCs.TORMENTED_DEMON_8358, NPCs.TORMENTED_DEMON_8359, NPCs.TORMENTED_DEMON_8360,
                    NPCs.TORMENTED_DEMON_8361, NPCs.TORMENTED_DEMON_8362, NPCs.TORMENTED_DEMON_8363, NPCs.TORMENTED_DEMON_8364,
                    NPCs.TORMENTED_DEMON_8365, NPCs.TORMENTED_DEMON_8366
            },
            "Tormented demon"
    );

    private final int[] npc;
    private final String bossName;

    BossKillCounter(int[] npc, String bossName) {
        this.npc = npc;
        this.bossName = bossName;
    }

    public int[] getNpc() {
        return npc;
    }

    public String getBossName() {
        return bossName;
    }

    public static BossKillCounter forNPC(int npc) {
        for (BossKillCounter kc : values()) {
            for (int i : kc.getNpc()) {
                if (npc == i) {
                    return kc;
                }
            }
        }
        return null;
    }

    public static void addToKillCount(Player killer, int npcId) {
        if (killer == null) {
            return;
        }
        BossKillCounter boss = BossKillCounter.forNPC(npcId);
        if (boss == null) {
            return;
        }

        killer.getSavedData().globalData.getBossCounters()[boss.ordinal()]++;
        String formattedName = StringUtils.formatDisplayName(boss.getBossName());
        int currentKillCount = killer.getSavedData().globalData.getBossCounters()[boss.ordinal()];
        killer.getPacketDispatch().sendMessage(String.format("Your %s killcount is now: <col=FF0000>%d</col>.", formattedName, currentKillCount));
    }

    public static void addToBarrowsCount(Player player) {
        if (player == null) {
            return;
        }
        player.getSavedData().globalData.setBarrowsLoots(player.getSavedData().globalData.getBarrowsLoots() + 1);
        int barrowsLoots = player.getSavedData().globalData.getBarrowsLoots();
        player.getPacketDispatch().sendMessage(String.format("Your Barrows chest count is: <col=ff0000>%d</col>.", barrowsLoots));
    }
}
