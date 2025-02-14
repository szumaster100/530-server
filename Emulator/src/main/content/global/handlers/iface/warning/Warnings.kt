package content.global.handlers.iface.warning

import org.rs.consts.Components

enum class Warnings(val varbit: Int, val component: Int, val buttonId: Int) {
    DAGANNOTH_KINGS_LADDER(varbit = 3851, component = Components.CWS_WARNING_4_579, buttonId = 50),
    LUMBRIDGE_SWAMP_CAVE_ROPE(varbit = 3863, component = Components.CWS_WARNING_17_570, buttonId = 51), // Correct
    STRONGHOLD_OF_SECURITY_LADDERS(varbit = 3854, component = -1, buttonId = 52),
    FALADOR_MOLE_LAIR(varbit = 3853, component = Components.CWS_WARNING_3_568, buttonId = 53),
    DROPPED_ITEMS_IN_RANDOM_EVENTS(varbit = 3856, component = Components.CWS_WARNING_6_566, buttonId = 54),
    PLAYER_OWNED_HOUSES(varbit = 3855, component = Components.CWS_WARNING_5_563, buttonId = 55),
    CONTACT_DUNGEON_LADDER(varbit = 3852, component = Components.CWS_WARNING_2_562, buttonId = 56),
    ICY_PATH_AREA(varbit = 3861, component = Components.CWS_WARNING_1_574, buttonId = 57),
    HAM_TUNNEL_FROM_MILL(varbit = 3864, component = 571, buttonId = 58),
    FAIRY_RING_TO_DORGESH_KAAN(varbit = 3865, component = Components.CWS_WARNING_16_569, buttonId = 59),
    LUMBRIDGE_CELLAR(varbit = 3866, component = Components.CWS_WARNING_14_567, buttonId = 60),
    MORT_MYRE(varbit = 3870, component = Components.CWS_WARNING_20_580, buttonId = 61),
    OBSERVATORY_STAIRS(varbit = 3859, component = Components.CWS_WARNING_9_560, buttonId = 62),
    SHANTAY_PASS(varbit = 3860, component = Components.CWS_WARNING_10_565, buttonId = 63),
    ELID_GENIE_CAVE(varbit = 3867, component = Components.CWS_WARNING_18_577, buttonId = 64),
    WATCHTOWER_SHAMAN_CAVE(varbit = 3862, component = Components.CWS_WARNING_12_573, buttonId = 65),
    TROLLHEIM_WILDERNESS_ENTRANCE(varbit = 3858, component = Components.CWS_WARNING_13_572, buttonId = 66),
    WILDERNESS_DITCH(varbit = 3857, component = Components.WILDERNESS_WARNING_382, buttonId = 67), // Correct
    DORGESH_KAAN_CITY_EXIT(varbit = 3869, component = Components.CWS_WARNING_15_578, buttonId = 68),
    DORGESH_KAAN_TUNNEL_TO_KALPHITES(varbit = 3868, component = Components.CWS_WARNING_21_561, buttonId = 69),
    RANGING_GUILD(varbit = 3871, component = Components.CWS_WARNING_23_564, buttonId = 70), // Correct
    DEATH_PLATEAU(varbit = 3872, component = Components.CWS_WARNING_24_581, buttonId = 71), // Correct
    GOD_WARS(varbit = 3946, component = Components.CWS_WARNING_25_600, buttonId = 72),
    DUEL_ARENA(varbit = 4132, component = Components.CWS_WARNING_26_627, buttonId = 73),
    BOUNTY_AREA(varbit = 4199, component = Components.BOUNTY_WARNING_657, buttonId = 74),
    CHAOS_TUNNELS_EAST(varbit = 4307, component = Components.CWS_WARNING_27_676, buttonId = 75),
    CHAOS_TUNNELS_CENTRAL(varbit = 4308, component = Components.CWS_WARNING_28_677, buttonId = 76),
    CHAOS_TUNNELS_WEST(varbit = 4309, component = Components.CWS_WARNING_29_678, buttonId = 77),
    CORPOREAL_BEAST_DANGEROUS(varbit = 5366, component = Components.CWS_WARNING_30_650, buttonId = 78),
    CLAN_WARS_FFA_SAFETY(varbit = 5294, component = -1, buttonId = 79),
    CLAN_WARS_FFA_DANGEROUS(varbit = 5295, component = Components.CWS_WARNING_8_576, buttonId = 80),
    PVP_WORLDS(varbit = 5296, component = -1, buttonId = 81);

    companion object {
        @JvmStatic
        val values = enumValues<Warnings>()
        @JvmStatic
        val button = values.associateBy { it.varbit }
    }
}