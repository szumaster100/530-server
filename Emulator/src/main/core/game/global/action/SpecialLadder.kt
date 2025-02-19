package core.game.global.action

import core.api.finishDiaryTask
import core.game.node.entity.player.Player
import core.game.node.entity.player.link.diary.DiaryType
import core.game.world.map.Location
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

enum class SpecialLadder(
    private val ladderLoc: Location,
    private val destLoc: Location,
) : LadderAchievementCheck {
    ALKHARID_CRAFTING_DOWN(
        ladderLoc = Location.create(3314, 3187, 1),
        destLoc = Location.create(3310, 3187, 0),
    ),
    ALKHARID_CRAFTING_UP(
        ladderLoc = Location.create(3311, 3187, 0),
        destLoc = Location.create(3314, 3187, 1),
    ),
    ALKHARID_SOCRCERESS_DOWN(
        ladderLoc = Location.create(3325, 3139, 1),
        destLoc = Location.create(3325, 3143, 0),
    ),
    ALKHARID_SOCRCERESS_UP(
        ladderLoc = Location.create(3325, 3142, 0),
        destLoc = Location.create(3325, 3139, 1),
    ),
    ALKHARID_ZEKE_DOWN(
        ladderLoc = Location.create(3284, 3190, 1),
        destLoc = Location.create(3284, 3186, 0),
    ),
    ALKHARID_ZEKE_UP(
        ladderLoc = Location.create(3284, 3186, 0),
        destLoc = Location.create(3284, 3190, 1),
    ),
    BEAR_CAGE_DOWN(
        ladderLoc = Location.create(3230, 3508, 0),
        destLoc = Location.create(3229, 9904, 0),
    ),
    BEAR_CAGE_UP(
        ladderLoc = Location.create(3230, 9904, 0),
        destLoc = Location.create(3231, 3504, 0),
    ),
    BETTY_BASEMENT_CLIMB_UP(
        ladderLoc = Location.create(3220, 4522, 0),
        destLoc = Location.create(3015, 3260, 0),
    ),
    BETTY_BASEMENT_CLIMB_DOWN(
        ladderLoc = Location.create(3015, 3257, 0),
        destLoc = Location.create(3219, 4522, 0),
    ),
    CATHERBY_BLACK_DRAGONS_LADDER_UP(
        ladderLoc = Location.create(2841, 9824, 0),
        destLoc = Location.create(2842, 3423, 0),
    ),
    TRAVERLEY_DUNGEON_LADDER_UP(
        ladderLoc = Location.create(2884, 9797, 0),
        destLoc = Location.create(2884, 3398, 0),
    ),
    CHAMPION_CHALLENGE_LADDER_UP(
        ladderLoc = Location.create(3190, 9758, 0),
        destLoc = Location.create(3191, 3355, 0),
    ),
    CHAMPION_CHALLENGE_LADDER_DOWN(
        ladderLoc = Location.create(3190, 3355, 0),
        destLoc = Location.create(3189, 9758, 0),
    ),
    CASTLEWARS_SARADOMIN_MAIN_FLOOR_STAIRS_DOWN(
        ladderLoc = Location.create(2419, 3080, 1),
        destLoc = Location.create(2419, 3077, 0),
    ),
    CASTLEWARS_SARADOMIN_MAIN_FLOOR_STAIRS_UP(
        ladderLoc = Location.create(2428, 3081, 1),
        destLoc = Location.create(2430, 3080, 2),
    ),
    CASTLEWARS_SARADOMIN_OUTER_WALL_STAIRS_DOWN(
        ladderLoc = Location.create(2417, 3075, 0),
        destLoc = Location.create(2417, 3078, 0),
    ),
    CASTLEWARS_SARADOMIN_OUTER_WALL_STAIRS_UP(
        ladderLoc = Location.create(2417, 3077, 0),
        destLoc = Location.create(2416, 3075, 0),
    ),
    CASTLEWARS_ZAMORAK_MAIN_FLOOR_STAIRS_UP(
        ladderLoc = Location.create(2380, 3129, 0),
        destLoc = Location.create(2379, 3127, 1),
    ),
    CASTLEWARS_ZAMORAK_OUTER_WALL_STAIRS_UP(
        ladderLoc = Location.create(2382, 3130, 0),
        destLoc = Location.create(2383, 3132, 0),
    ),
    CASTLEWARS_ZAMORAK_TOP_FLOOR_DOWN(
        ladderLoc = Location.create(2374, 3133, 3),
        destLoc = Location.create(2374, 3130, 2),
    ),
    CASTLEWARS_ZAMORAK_OUTER_WALL_STAIRS_DOWN(
        ladderLoc = Location.create(2382, 3132, 0),
        destLoc = Location.create(2382, 3129, 0),
    ),
    CLOCKTOWER_HIDDEN_LADDER(
        ladderLoc = Location.create(2572, 9631, 0),
        destLoc = Location.create(2572, 3230, 0),
    ),
    CREATURE_CREATION_EXIT(
        ladderLoc = Location.create(3038, 4375, 0),
        destLoc = Location.create(2647, 3214, 0),
    ),
    CREATURE_CREATION_ENTER(
        ladderLoc = Location.create(2647, 3213, 0),
        Location.create(3038, 4376, 0),
    ),
    DRAYNOR_SEWER_NORTHWEST_DOWN(
        ladderLoc = Location.create(3084, 3272, 0),
        destLoc = Location.create(3085, 9672, 0),
    ),
    DRAYNOR_SEWER_NORTHWEST_UP(
        ladderLoc = Location.create(3084, 9672, 0),
        destLoc = Location.create(3084, 3271, 0),
    ),
    DRAYNOR_SEWER_SOUTHEAST_DOWN(
        ladderLoc = Location.create(3118, 3244, 0),
        destLoc = Location.create(3118, 9643, 0),
    ),
    DRAYNOR_SEWER_SOUTHEAST_UP(
        ladderLoc = Location.create(3118, 9643, 0),
        destLoc = Location.create(3118, 3243, 0),
    ),
    ENTRANA_GLASSBLOWING_PIPE_HOUSE_DOWN(
        ladderLoc = Location.create(2816, 3352, 1),
        destLoc = Location.create(2817, 3352, 0),
    ),
    EDGEVILLE_CLIMB_UP(
        ladderLoc = Location.create(3097, 9867, 0),
        destLoc = Location.create(3096, 3468, 0),
    ),
    FISHING_GUILD_HOUSE_UP(
        ladderLoc = Location.create(2615, 3394, 0),
        destLoc = Location.create(2615, 3395, 1),
    ),
    FISHING_GUILD_HOUSE_DOWN(
        ladderLoc = Location.create(2615, 3394, 1),
        destLoc = Location.create(2615, 3395, 0),
    ),
    FOG_ENTER(
        ladderLoc = Location.create(3240, 3575, 0),
        destLoc = Location.create(1675, 5599, 0),
    ),
    FOG_LEAVE(
        ladderLoc = Location.create(1673, 5598, 0),
        destLoc = Location.create(3242, 3574, 0),
    ),
    GEM_MINE(
        ladderLoc = Location.create(2821, 2996, 0),
        destLoc = Location.create(2838, 9387, 0),
    ),
    GEM_MINE_UP(
        ladderLoc = Location.create(2838, 9388, 0),
        destLoc = Location.create(2820, 2996, 0),
    ),
    GLARIAL_EXIT(
        ladderLoc = Location.create(2556, 9844, 0),
        destLoc = Location.create(2557, 3444, 0),
    ),
    HADLEY_HOUSE_STAIRS_CLIMB_UP(
        ladderLoc = Location.create(2517, 3429, 0),
        destLoc = Location.create(2518, 3431, 1),
    ),
    HAM_STORAGE_UP(
        ladderLoc = Location.create(2567, 5185, 0),
        destLoc = Location.create(3166, 9623, 0),
    ),
    HAUNTED_MINE_LADDER_F0_DOWN(
        ladderLoc = Location.create(3422, 9625, 0),
        destLoc = Location.create(2783, 4569, 0),
    ),
    HAUNTED_MINE_LADDER_F0_UP(
        ladderLoc = Location.create(2782, 4569, 1),
        destLoc = Location.create(3423, 9625, 0),
    ),
    HAUNTED_MINE_LADDER_F1_DOWN(
        ladderLoc = Location.create(2798, 4567, 0),
        destLoc = Location.create(2733, 4503, 0),
    ),
    HAUNTED_MINE_LADDER_F1_UP(
        ladderLoc = Location.create(2734, 4503, 0),
        destLoc = Location.create(2797, 4567, 0),
    ),
    HAUNTED_MINE_LADDER_F2_DOWN(
        ladderLoc = Location.create(2725, 4486, 0),
        destLoc = Location.create(2789, 4487, 0),
    ),
    HAUNTED_MINE_LADDER_F2_UP(
        ladderLoc = Location.create(2789, 4486, 0),
        destLoc = Location.create(2797, 4567, 0),
    ),
    HEROES_GUILD_LADDER_UP(
        ladderLoc = Location.create(2890, 3508, 1),
        destLoc = Location.create(2890, 3507, 2),
    ),
    HEROES_GUILD_STAIRCASE_UP(
        ladderLoc = Location.create(2895, 3513, 0),
        destLoc = Location.create(2897, 3513, 1),
    ),
    HORROR_FROM_THE_DEEP_BASEMENT_UP(
        ladderLoc = Location.create(2519, 4618, 1),
        destLoc = Location.create(2510, 3644, 0),
    ),
    HORROR_FROM_THE_DEEP_BASEMENT_AFTER_QUEST_UP(
        ladderLoc = Location.create(2519, 9994, 1),
        destLoc = Location.create(2510, 3644, 0),
    ),
    HETTY_BASEMENT_CLIMB_UP(
        ladderLoc = Location.create(3165, 4514, 0),
        destLoc = Location.create(2967, 3201, 0),
    ),
    HETTY_BASEMENT_CLIMB_DOWN(
        ladderLoc = Location.create(2968, 3201, 0),
        destLoc = Location.create(3165, 4515, 0),
    ),
    JATIZSO_STAIRS_1_DOWN(
        ladderLoc = Location.create(2388, 3804, 2),
        destLoc = Location.create(2387, 3804, 0),
    ),
    JATIZSO_STAIRS_1_UP(
        ladderLoc = Location.create(2388, 3804, 0),
        destLoc = Location.create(2387, 3804, 2),
    ),
    JATIZSO_STAIRS_2_DOWN(
        ladderLoc = Location.create(2388, 3793, 2),
        destLoc = Location.create(2387, 3793, 0),
    ),
    JATIZSO_STAIRS_2_UP(
        ladderLoc = Location.create(2388, 3793, 0),
        destLoc = Location.create(2387, 3793, 2),
    ),
    JATIZSO_STAIRS_3_DOWN(
        ladderLoc = Location.create(2410, 3823, 2),
        destLoc = Location.create(2410, 3824, 0),
    ),
    JATIZSO_STAIRS_3_UP(
        ladderLoc = Location.create(2410, 3823, 0),
        destLoc = Location.create(2410, 3824, 2),
    ),
    JATIZSO_STAIRS_4_DOWN(
        ladderLoc = Location.create(2421, 3823, 2),
        destLoc = Location.create(2421, 3824, 0),
    ),
    JATIZSO_STAIRS_4_UP(
        ladderLoc = Location.create(2421, 3823, 0),
        destLoc = Location.create(2421, 3824, 2),
    ),
    JATIZSO_MINE_DOWN(
        ladderLoc = Location.create(2397, 3812, 0),
        destLoc = Location.create(2405, 10188, 0),
    ),
    JATIZSO_MINE_UP(
        ladderLoc = Location.create(2406, 10188, 0),
        destLoc = Location.create(2397, 3811, 0),
    ),
    JATIZSO_SHOUT_TOWER_DOWN(
        ladderLoc = Location.create(2373, 3800, 2),
        destLoc = Location.create(2374, 3800, 0),
    ),
    JATIZSO_SHOUT_TOWER_UP(
        ladderLoc = Location.create(2373, 3800, 0),
        destLoc = Location.create(2374, 3800, 2),
    ),
    KHARIDIAN_DESERT_SUMMONING_UP(
        ladderLoc = Location.create(3299, 9318, 0),
        destLoc = Location.create(3303, 2897, 0),
    ),
    KHARIDIAN_DESERT_SUMMONING_DOWN(
        ladderLoc = Location.create(3304, 2897, 0),
        destLoc = Location.create(3299, 9317, 0),
    ),
    KELDAGRIM_LIBRARY_UP(
        ladderLoc = Location.create(2865, 10222, 0),
        destLoc = Location.create(2865, 10224, 1),
    ),
    LAVA_MAZE_EAST_UP(
        ladderLoc = Location.create(3017, 10249, 0),
        destLoc = Location.create(3069, 3855, 0),
    ),
    LAVA_MAZE_KBD_UP(
        ladderLoc = Location.create(3069, 10256, 0),
        destLoc = Location.create(3017, 3848, 0),
    ),
    LEGENDS_GUILD_STAIRCASE_UP(
        ladderLoc = Location.create(2732, 3377, 0),
        destLoc = Location.create(2732, 3380, 1),
    ),
    LEGENDS_GUILD_STAIRCASE_DOWN(
        ladderLoc = Location.create(2724, 3374, 0),
        destLoc = Location.create(2725, 9775, 0),
    ),
    LUMBRIDGE_LEARNING_THE_ROPES_DOWN(
        ladderLoc = Location.create(3230, 3241, 0),
        destLoc = Location.create(3290, 4936, 0),
    ),
    LUMBRIDGE_LEARNING_THE_ROPES_UP(
        ladderLoc = Location.create(3290, 4935, 0),
        destLoc = Location.create(3230, 3240, 0),
    ),
    MOVARIO_LADDER_UP(
        ladderLoc = Location.create(2036, 4379, 0),
        destLoc = Location.create(2502, 3255, 0),
    ),
    NEITIZNOT_STAIRS_UP(
        ladderLoc = Location.create(2363, 3799, 0),
        destLoc = Location.create(2364, 3799, 2),
    ),
    NEITIZNOT_STAIRS_DOWN(
        ladderLoc = Location.create(2363, 3799, 2),
        destLoc = Location.create(2362, 3799, 0),
    ),
    OBSERVATORY_STAIRS_CLIMB_DOWN(
        ladderLoc = Location.create(2443, 3159, 1),
        destLoc = Location.create(2444, 3162, 0),
    ),
    OBSERVATORY_STAIRS_CLIMB_UP(
        ladderLoc = Location.create(2444, 3159, 0),
        destLoc = Location.create(2442, 3159, 1),
    ),
    OURANIA_LADDER_CLIMB_UP(
        ladderLoc = Location.create(3271, 4862, 0),
        destLoc = Location.create(2452, 3230, 0),
    ),
    OURANIA_LADDER_CLIMB_DOWN(
        ladderLoc = Location.create(2452, 3231, 0),
        destLoc = Location.create(3271, 4862, 0),
    ),
    PATERDOMUS_TEMPLE_STAIRCASE_NORTH_UP(
        ladderLoc = Location.create(3415, 3490, 0),
        destLoc = Location.create(3415, 3491, 1),
    ),
    PATERDOMUS_TEMPLE_STAIRCASE_NORTH_DOWN(
        ladderLoc = Location.create(3415, 3491, 1),
        destLoc = Location.create(3414, 3491, 0),
    ),
    PATERDOMUS_TEMPLE_STAIRCASE_SOUTH_DOWN(
        ladderLoc = Location.create(3415, 3486, 1),
        destLoc = Location.create(3414, 3486, 0),
    ),
    PORT_PHASMATYS_BAR_DOWN(
        ladderLoc = Location.create(3681, 3498, 0),
        destLoc = Location.create(3682, 9961, 0),
    ),
    PORT_PHASMATYS_BAR_UP(
        ladderLoc = Location.create(3682, 9962, 0),
        destLoc = Location.create(3681, 3497, 0),
    ),
    PIRATES_COVE_SHIP_CENTER_STAIRS_DOWN(
        ladderLoc = Location.create(2225, 3807, 1),
        destLoc = Location.create(2225, 3809, 0),
    ),
    PIRATES_COVE_SHIP_CENTER_STAIRS_UP(
        ladderLoc = Location.create(2225, 3808, 0),
        destLoc = Location.create(2225, 3806, 1),
    ),
    PIRATES_COVE_SHIP_NORTH_EAST_STAIRS_DOWN(
        ladderLoc = Location.create(2227, 3806, 3),
        destLoc = Location.create(2227, 3805, 2),
    ),
    PIRATES_COVE_SHIP_NORTH_EAST_STAIRS_UP(
        ladderLoc = Location.create(2227, 3806, 2),
        destLoc = Location.create(2227, 3808, 3),
    ),
    PIRATES_COVE_SHIP_NORTH_WEST_STAIRS_DOWN(
        ladderLoc = Location.create(2221, 3806, 3),
        destLoc = Location.create(2221, 3805, 2),
    ),
    PIRATES_COVE_SHIP_NORTH_WEST_STAIRS_UP(
        ladderLoc = Location.create(2221, 3806, 2),
        destLoc = Location.create(2221, 3808, 3),
    ),
    PIRATES_COVE_SHIP_SOUTH_EAST_STAIRS_DOWN(
        ladderLoc = Location.create(2227, 3793, 3),
        destLoc = Location.create(2227, 3795, 2),
    ),
    PIRATES_COVE_SHIP_SOUTH_EAST_STAIRS_UP(
        ladderLoc = Location.create(2227, 3794, 2),
        destLoc = Location.create(2227, 3792, 3),
    ),
    PIRATES_COVE_SHIP_SOUTH_LEFT_STAIRS_DOWN(
        ladderLoc = Location.create(2221, 3792, 2),
        destLoc = Location.create(2223, 3792, 1),
    ),
    PIRATES_COVE_SHIP_SOUTH_LEFT_STAIRS_UP(
        ladderLoc = Location.create(2222, 3792, 1),
        destLoc = Location.create(2220, 3792, 2),
    ),
    PIRATES_COVE_SHIP_SOUTH_RIGHT_STAIRS_DOWN(
        ladderLoc = Location.create(2226, 3792, 2),
        destLoc = Location.create(2225, 3792, 1),
    ),
    PIRATES_COVE_SHIP_SOUTH_RIGHT_STAIRS_UP(
        ladderLoc = Location.create(2226, 3792, 1),
        destLoc = Location.create(2228, 3792, 2),
    ),
    PIRATES_COVE_SHIP_SOUTH_WEST_STAIRS_DOWN(
        ladderLoc = Location.create(2221, 3793, 3),
        destLoc = Location.create(2221, 3795, 2),
    ),
    PIRATES_COVE_SHIP_SOUTH_WEST_STAIRS_UP(
        ladderLoc = Location.create(2221, 3794, 2),
        destLoc = Location.create(2221, 3792, 3),
    ),
    RAT_PITS_PORT_SARIM_LADDER_UP(
        ladderLoc = Location.create(2962, 9651, 0),
        destLoc = Location.create(3018, 3231, 0),
    ),
    RAT_PITS_PORT_SARIM_LADDER_DOWN(
        ladderLoc = Location.create(3018, 3232, 0),
        destLoc = Location.create(2962, 9650, 0),
    ) {
        override fun checkAchievement(player: Player) {
            finishDiaryTask(player, DiaryType.FALADOR, 1, 11)
        }
    },
    RAT_PITS_ARDOUGNE_LADDER_UP(
        ladderLoc = Location.create(2661, 9641, 0),
        destLoc = Location.create(2562, 3320, 0),
    ),
    RAT_PITS_ARDOUGNE_LADDER_DOWN(
        ladderLoc = Location.create(2561, 3320, 0),
        destLoc = Location.create(2662, 9641, 0),
    ),
    RAT_PITS_VARROCK_LADDER_UP(
        ladderLoc = Location.create(2895, 5097, 0),
        destLoc = Location.create(3268, 3400, 0),
    ),
    RAT_PITS_VARROCK_LADDER_DOWN(
        ladderLoc = Location.create(3267, 3400, 0),
        destLoc = Location.create(2894, 5097, 0),
    ),
    RAT_PITS_KELDAGRIM_LADDER_UP(
        ladderLoc = Location.create(1943, 4703, 1),
        destLoc = Location.create(2914, 10187, 0),
    ),
    RAT_PITS_KELDAGRIM_LADDER_DOWN(
        ladderLoc = Location.create(2914, 10188, 0),
        destLoc = Location.create(1943, 4705, 1),
    ),
    SEERS_VILLAGE_SPINNING_HOUSE_ROOFTOP_DOWN(
        ladderLoc = Location.create(2715, 3472, 3),
        destLoc = Location.create(2714, 3472, 1),
    ),
    SEERS_VILLAGE_SPINNING_HOUSE_ROOFTOP_UP(
        ladderLoc = Location.create(2715, 3472, 1),
        destLoc = Location.create(2714, 3472, 3),
    ) {
        override fun checkAchievement(player: Player) {
            finishDiaryTask(player, DiaryType.SEERS_VILLAGE, 1, 3)
        }
    },
    SHADOW_DUNGEON_UP(
        ladderLoc = Location.create(2629, 5072, 0),
        destLoc = Location.create(2548, 3421, 0),
    ),
    SOPHANEM_ALTAR_LADDER_UP(
        ladderLoc = Location.create(3279, 2771, 1),
        destLoc = Location.create(3279, 2770, 3),
    ),
    SOPHANEM_ALTAR_LADDER_DOWN(
        ladderLoc = Location.create(3279, 2771, 3),
        destLoc = Location.create(3279, 2772, 1),
    ),
    SWENSEN_DOWN(
        ladderLoc = Location.create(2644, 3657, 0),
        destLoc = Location.create(2631, 10006, 0),
    ),
    SWENSEN_UP(
        ladderLoc = Location.create(2665, 10037, 0),
        destLoc = Location.create(2649, 3661, 0),
    ),
    TREE_GNOME_STRONGHOLD_WEST_BAR_STAIRS_DOWN(
        ladderLoc = Location.create(2418, 3491, 1),
        destLoc = Location.create(2419, 3491, 0),
    ),
    TREE_GNOME_STRONGHOLD_WEST_BAR_STAIRS_UP(
        ladderLoc = Location.create(2417, 3490, 0),
        destLoc = Location.create(2418, 3492, 1),
    ),
    TREE_GNOME_STRONGHOLD_SPINNING_WHEEL_STAIRS_UP(
        ladderLoc = Location.create(2488, 3407, 0),
        destLoc = Location.create(2489, 3409, 1),
    ),
    TREE_GNOME_STRONGHOLD_GATE_STAIRS_UP(
        ladderLoc = Location.create(2461, 3416, 0),
        destLoc = Location.create(2460, 3417, 1),
    ),
    TAVERLEY_DUNGEON_DOWN(
        ladderLoc = Location.create(2884, 3397, 0),
        destLoc = Location.create(2884, 9796, 0),
    ),
    WATERBIRTH_ISLAND_DUNGEON_SUBLEVEL_2_WALLASALKI_3_LADDER_DOWN(
        ladderLoc = Location.create(1799, 4387, 2),
        destLoc = Location.create(1799, 4388, 1),
    ),
    WATERBIRTH_LADDER_WAY_TO_LIGHTHOUSE_1_UP(
        ladderLoc = Location.create(1932, 4378, 0),
        destLoc = Location.create(1932, 4380, 2),
    ),
    WATERBIRTH_LADDER_WAY_TO_LIGHTHOUSE_2_UP(
        ladderLoc = Location.create(1961, 4391, 2),
        destLoc = Location.create(1961, 4393, 3),
    ),
    WATERBIRTH_LADDER_WAY_TO_LIGHTHOUSE_3_UP(
        ladderLoc = Location.create(1975, 4408, 3),
        destLoc = Location.create(2510, 3644, 0),
    ),
    WEREWOLF_AGILITY_COURSE_LADDER_UP(
        ladderLoc = Location.create(3549, 9864, 0),
        destLoc = Location.create(3543, 3463, 0),
    ),
    WITCH_HOUSE_TO_BASEMENT_DOWN(
        ladderLoc = Location.create(2907, 3476, 0),
        destLoc = Location.create(2906, 9876, 0),
    ),
    WITCH_HOUSE_TO_BASEMENT_UP(
        ladderLoc = Location.create(2907, 9876, 0),
        destLoc = Location.create(2906, 3476, 0),
    ),
    WIZARD_TOWER_LADDER_UP(
        ladderLoc = Location.create(3103, 9576, 0),
        destLoc = Location.create(3105, 3162, 0),
    ),
    WHITE_WOLF_MOUNTAIN_FAKE_LADDER_1_UP(
        ladderLoc = Location.create(2837, 9927, 0),
        destLoc = Location.create(2837, 3527, 0),
    ),
    WHITE_WOLF_MOUNTAIN_FAKE_LADDER_2_UP(
        ladderLoc = Location.create(2823, 9930, 0),
        destLoc = Location.create(2823, 3529, 0),
    ),
    DRAYNOR_MANOR_LADDER_DOWN(
        ladderLoc = Location.create(3092, 3362, 0),
        destLoc = Location.create(3117, 9753, 0),
    ),
    DRAYNOR_MANOR_LADDER_UP(
        ladderLoc = Location.create(3117, 9754, 0),
        destLoc = Location.create(3092, 3361, 0),
    ),
    VARROCK_HOUSE_UP(
        ladderLoc = Location(3230, 3383, 0),
        destLoc = Location(3230, 3382, 1),
    ),
    VARROCK_HOUSE_DOWN(
        ladderLoc = Location(3230, 3383, 1),
        destLoc = Location(3230, 3386, 0),
    ),
    VARROCK_KING_RAT_LADDER_UP(
        ladderLoc = Location(3268, 3379, 0),
        destLoc = Location.create(3269, 3379, 1),
    ),
    VARROCK_KING_RAT_LADDER_DOWN(
        ladderLoc = Location(3268, 3379, 1),
        destLoc = Location.create(3267, 3379, 0),
    ),
    VARROCK_SEWERS_DOWN(
        ladderLoc = Location(3237, 3458, 0),
        destLoc = Location(3237, 9858, 0),
    ),
    VARROCK_SEWERS_UP(
        ladderLoc = Location(3237, 9858, 0),
        destLoc = Location.create(3238, 3458, 0),
    ),
    VARROCK_FUR_SHOP_DOWN(
        ladderLoc = Location.create(3283, 3398, 1),
        destLoc = Location.create(3283, 3397, 0),
    ),
    VARROCK_FUR_SHOP_UP(
        ladderLoc = Location.create(3283, 3398, 0),
        destLoc = Location.create(3283, 3399, 1),
    ),
    VARROCK_LOWE_STAIRS_UP(
        ladderLoc = Location.create(3233, 3424, 0),
        destLoc = Location.create(3232, 3424, 1),
    ),
    ;

    companion object {
        @JvmStatic private val destinationMap = Object2ObjectOpenHashMap<Location, Location>()

        @JvmStatic private val ladderMap = Object2ObjectOpenHashMap<Location, SpecialLadder>()

        init {
            for (entry in values()) {
                destinationMap.putIfAbsent(entry.ladderLoc, entry.destLoc)
                ladderMap.putIfAbsent(entry.ladderLoc, entry)
            }
        }

        @JvmStatic
        fun add(
            from: Location,
            to: Location,
        ) {
            destinationMap[from] = to
        }

        @JvmStatic
        fun getDestination(loc: Location): Location? {
            return destinationMap[loc]
        }

        @JvmStatic
        fun getSpecialLadder(loc: Location): SpecialLadder? {
            return ladderMap[loc]
        }
    }
}
