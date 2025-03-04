package content.global.skill.gathering;

import core.ServerConstants;
import core.game.node.entity.skill.Skills;
import core.game.world.repository.Repository;
import core.game.world.update.flag.context.Animation;
import org.rs.consts.Scenery;

import java.util.HashMap;
import java.util.Map;

public enum SkillingResource {
	STANDARD_TREE_1( Scenery.TREE_1276, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_2( Scenery.TREE_1277, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1343, Skills.WOODCUTTING),
	STANDARD_TREE_3( Scenery.TREE_1278, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_4( Scenery.TREE_1279, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1345, Skills.WOODCUTTING),
	STANDARD_TREE_5( Scenery.TREE_1280, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1343, Skills.WOODCUTTING),
	STANDARD_TREE_6( Scenery.TREE_1330, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1341, Skills.WOODCUTTING),
	STANDARD_TREE_7( Scenery.TREE_1331, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1341, Skills.WOODCUTTING),
	STANDARD_TREE_8( Scenery.TREE_1332, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1341, Skills.WOODCUTTING),
	STANDARD_TREE_9( Scenery.TREE_2409, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_10(Scenery.TUTORIAL_TREE_3033, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1345, Skills.WOODCUTTING),
	STANDARD_TREE_11(Scenery.TREE_3034, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1345, Skills.WOODCUTTING),
	STANDARD_TREE_12(Scenery.TREE_3035, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1347, Skills.WOODCUTTING),
	STANDARD_TREE_13(Scenery.TREE_3036, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1351, Skills.WOODCUTTING),
	STANDARD_TREE_14(Scenery.TREE_3879, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 3880, Skills.WOODCUTTING),
	STANDARD_TREE_15(Scenery.TREE_3881, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 3880, Skills.WOODCUTTING),
	STANDARD_TREE_16(Scenery.TREE_3882, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 3880, Skills.WOODCUTTING),
	STANDARD_TREE_17(Scenery.TREE_3883, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 3884, Skills.WOODCUTTING),
	STANDARD_TREE_18(Scenery.TREE_10041, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_19(Scenery.TREE_14308, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_20(Scenery.TREE_14309, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_21(Scenery.TREE_16264, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_22(Scenery.TREE_16265, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_23(Scenery.TREE_30132, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_24(Scenery.TREE_30133, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_25(Scenery.TREE_37477, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 1342, Skills.WOODCUTTING),
	STANDARD_TREE_26(Scenery.TREE_37478, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 37653, Skills.WOODCUTTING),
	STANDARD_TREE_27(Scenery.TREE_37652, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "tree", null, 37653, Skills.WOODCUTTING),

	APPLE_TREE(Scenery.APPLE_TREE_7941, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),
	BANANA_TREE(Scenery.BANANA_TREE_8000, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),
	ORANGE_TREE(Scenery.ORANGE_TREE_8057, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),
	CURRY_TREE(Scenery.CURRY_TREE_8026, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),
	PINEAPPLE_TREE(Scenery.PINEAPPLE_PLANT_7972, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),
	PAPAYA_TREE(Scenery.PAPAYA_TREE_8111, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),
	PALM_TREE(Scenery.PALM_TREE_8084, 1, 0.05, 50 | 100 << 16, 25.0, -1, 1, "tree", null, 37653, Skills.WOODCUTTING, true),

	DEAD_TREE_1(Scenery.DEAD_TREE_1282, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1347, Skills.WOODCUTTING),
	DEAD_TREE_2(Scenery.DEAD_TREE_1283, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1347, Skills.WOODCUTTING),
	DEAD_TREE_3(Scenery.DEAD_TREE_1284, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1348, Skills.WOODCUTTING),
	DEAD_TREE_4(Scenery.DEAD_TREE_1285, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1349, Skills.WOODCUTTING),
	DEAD_TREE_5(Scenery.DEAD_TREE_1286, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1351, Skills.WOODCUTTING),
	DEAD_TREE_6(Scenery.DEAD_TREE_1289, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1353, Skills.WOODCUTTING),
	DEAD_TREE_7(Scenery.DEAD_TREE_1290, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1354, Skills.WOODCUTTING),
	DEAD_TREE_8(Scenery.DEAD_TREE_1291, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 23054, Skills.WOODCUTTING),
	DEAD_TREE_9(Scenery.DEAD_TREE_1365, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1352, Skills.WOODCUTTING),
	DEAD_TREE_10(Scenery.DEAD_TREE_1383,  1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1358, Skills.WOODCUTTING),
	DEAD_TREE_11(Scenery.DEAD_TREE_1384,  1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1359, Skills.WOODCUTTING),
	DEAD_TREE_12(Scenery.DEAD_TREE_5902,  1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1347, Skills.WOODCUTTING),
	DEAD_TREE_13(Scenery.DEAD_TREE_5903,  1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1353, Skills.WOODCUTTING),
	DEAD_TREE_14(Scenery.DEAD_TREE_5904,  1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1353, Skills.WOODCUTTING),
	DEAD_TREE_15(Scenery.DEAD_TREE_32294, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1353, Skills.WOODCUTTING),
	DEAD_TREE_16(Scenery.DEAD_TREE_37481, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1347, Skills.WOODCUTTING),
	DEAD_TREE_17(Scenery.DEAD_TREE_37482, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1351, Skills.WOODCUTTING),
	DEAD_TREE_18(Scenery.DEAD_TREE_37483, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dead tree", null, 1358, Skills.WOODCUTTING),
	DEAD_TREE_19(Scenery.DYING_TREE_24168, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "dying tree", null, 24169, Skills.WOODCUTTING),

	DRAMEN_TREE(Scenery.DRAMEN_TREE_1292, 36, 0.05, -1, 25.0, 771, Integer.MAX_VALUE, "dramen tree", null, -1, Skills.WOODCUTTING),

	EVERGREEN_1(Scenery.EVERGREEN_1315, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "evergreen", null, 1342, Skills.WOODCUTTING),
	EVERGREEN_2(Scenery.EVERGREEN_1316, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "evergreen", null, 1355, Skills.WOODCUTTING),
	EVERGREEN_3(Scenery.EVERGREEN_1318, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "evergreen", null, 1355, Skills.WOODCUTTING),
	EVERGREEN_4(Scenery.EVERGREEN_1319, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 1, "evergreen", null, 1355, Skills.WOODCUTTING),

	JUNGLE_TREE_1(Scenery.JUNGLE_TREE_2887, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 2, "jungle tree", null, 0, Skills.WOODCUTTING),
	JUNGLE_TREE_2(Scenery.JUNGLE_TREE_2889, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 2, "jungle tree", null, 0, Skills.WOODCUTTING),
	JUNGLE_TREE_3(Scenery.JUNGLE_TREE_2890, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 2, "jungle tree", null, 0, Skills.WOODCUTTING),
	JUNGLE_TREE_4(Scenery.JUNGLE_TREE_4818, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 2, "jungle tree", null, 0, Skills.WOODCUTTING),
	JUNGLE_TREE_5(Scenery.JUNGLE_TREE_4820, 1, 0.05, 50 | 100 << 16, 25.0, 1511, 2, "jungle tree", null, 0, Skills.WOODCUTTING),

	JUNGLE_BUSH_1(Scenery.JUNGLE_BUSH_2892, 1, 0.15, 50 | 100 << 16, 100.0, 1511, 1, "jungle bush", null, 2894, Skills.WOODCUTTING),
	JUNGLE_BUSH_2(Scenery.JUNGLE_BUSH_2893, 1, 0.15, 50 | 100 << 16, 100.0, 1511, 1, "jungle bush", null, 2895, Skills.WOODCUTTING),

	ACHEY_TREE(Scenery.ACHEY_TREE_2023, 1, 0.05, 50 | 100 << 16, 25.0, 2862, 1, "achey tree", null, 3371, Skills.WOODCUTTING),

	OAK_TREE_1(Scenery.OAK_1281, 15, 0.15, 14 | 22 << 16, 37.5, 1521, 10, "oak tree", null, 1356, Skills.WOODCUTTING),
	OAK_TREE_2(Scenery.OAK_3037, 15, 0.15, 14 | 22 << 16, 37.5, 1521, 10, "oak tree", null, 1357, Skills.WOODCUTTING),
	OAK_TREE_3(Scenery.OAK_37479, 15, 0.15, 14 | 22 << 16, 37.5, 1521, 10, "oak tree", null, 1356, Skills.WOODCUTTING),
	OAK_TREE_4(Scenery.OAK_8467, 15, 0.15, 14 | 22 << 16, 37.5, 1521, 10, "oak tree", null, 1356, Skills.WOODCUTTING, true),

	WILLOW_TREE_1(Scenery.WILLOW_1308, 30, 0.3, 14 | 22 << 16, 67.8, 1519, 20, "willow tree", null, 7399, Skills.WOODCUTTING),
	WILLOW_TREE_2(Scenery.WILLOW_5551, 30, 0.3, 14 | 22 << 16, 67.8, 1519, 20, "willow tree", null, 5554, Skills.WOODCUTTING),
	WILLOW_TREE_3(Scenery.WILLOW_5552, 30, 0.3, 14 | 22 << 16, 67.8, 1519, 20, "willow tree", null, 5554, Skills.WOODCUTTING),
	WILLOW_TREE_4(Scenery.WILLOW_5553, 30, 0.3, 14 | 22 << 16, 67.8, 1519, 20, "willow tree", null, 5554, Skills.WOODCUTTING),
	WILLOW_TREE_5(Scenery.WILLOW_37480, 30, 0.3, 14 | 22 << 16, 67.8, 1519, 20, "willow tree", null, 7399, Skills.WOODCUTTING),
	WILLOW_TREE_6(Scenery.WILLOW_TREE_8488, 30, 0.3, 14 | 22 << 16, 67.8, 1519, 20, "willow tree", null, 7399, Skills.WOODCUTTING, true),

	TEAK_1(Scenery.TEAK_9036, 35, 0.7, 35 | 60 << 16, 85.0, 6333, 25, "teak", null, 9037, Skills.WOODCUTTING),
	TEAK_2(Scenery.TEAK_15062, 35, 0.7, 35 | 60 << 16, 85.0, 6333, 25, "teak", null, 9037, Skills.WOODCUTTING),

	MAPLE_TREE_1(Scenery.MAPLE_TREE_1307, 45, 0.65, 58 | 100 << 16, 100.0, 1517, 30, "maple tree", null, 7400, Skills.WOODCUTTING),
	MAPLE_TREE_2(Scenery.MAPLE_TREE_4674, 45, 0.65, 58 | 100 << 16, 100.0, 1517, 30, "maple tree", null, 7400, Skills.WOODCUTTING),
	MAPLE_TREE_3(Scenery.MAPLE_TREE_8444, 45, 0.65, 58 | 100 << 16, 100.0, 1517, 30, "maple tree", null, 7400, Skills.WOODCUTTING, true),

	HOLLOW_TREE_1(Scenery.HOLLOW_TREE_2289, 45, 0.6, 58 | 100 << 16, 82.5, 3239, 30, "hollow tree", null, 2310, Skills.WOODCUTTING),
	HOLLOW_TREE_2(Scenery.HOLLOW_TREE_4060, 45, 0.6, 58 | 100 << 16, 82.5, 3239, 30, "hollow tree", null, 4061, Skills.WOODCUTTING),

	MAHOGANY(Scenery.MAHOGANY_9034, 50, 0.7, 62 | 115 << 16, 125.0, 6332, 35, "mahogany", null, 9035, Skills.WOODCUTTING),

	ARCTIC_PINE(Scenery.ARCTIC_PINE_21273, 54, 0.73, 75 | 130 << 16, 140.2, 10810, 35, "arctic pine", null, 21274, Skills.WOODCUTTING),

	EUCALYPTUS_1(Scenery.EUCALYPTUS_TREE_28951, 58, 0.77, 80 | 140 << 16, 165.0, 12581, 35, "eucalyptus tree", null, 28954, Skills.WOODCUTTING),
	EUCALYPTUS_2(Scenery.EUCALYPTUS_TREE_28952, 58, 0.77, 80 | 140 << 16, 165.0, 12581, 35, "eucalyptus tree", null, 28955, Skills.WOODCUTTING),
	EUCALYPTUS_3(Scenery.EUCALYPTUS_TREE_28953, 58, 0.77, 80 | 140 << 16, 165.0, 12581, 35, "eucalyptus tree", null, 28956, Skills.WOODCUTTING),

	YEW_0(Scenery.YEW_1309, 60, 0.8, 100 | 162 << 16, 175.0, 1515, 40, "yew", null, 7402, Skills.WOODCUTTING),
	YEW_1(Scenery.YEW_TREE_8513, 60, 0.8, 100 | 162 << 16, 175.0, 1515, 40, "yew", null, 7402, Skills.WOODCUTTING, true),

	MAGIC_TREE_1(Scenery.MAGIC_TREE_1306, 75, 0.9, 200 | 317 << 16, 250.0, 1513, 50, "magic tree", null, 7401, Skills.WOODCUTTING),
	MAGIC_TREE_2(Scenery.MAGIC_TREE_37823, 75, 0.9, 200 | 317 << 16, 250.0, 1513, 50, "magic tree", null, 37824, Skills.WOODCUTTING),
	MAGIC_TREE_3(Scenery.MAGIC_TREE_8409, 75, 0.9, 200 | 317 << 16, 250.0, 1513, 50, "magic tree", null, 37824, Skills.WOODCUTTING, true),

	CURSED_MAGIC_TREE(Scenery.CURSED_MAGIC_TREE_37821, 82, 0.95, 200 | 317 << 16, 275.0, 1513, 50, "magic tree", null, 37822, Skills.WOODCUTTING),

	COPPER_ORE_0(Scenery.ROCKS_2090, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 450, Skills.MINING),
	COPPER_ORE_1(Scenery.ROCKS_2091, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 452, Skills.MINING),
	COPPER_ORE_2(Scenery.MINERAL_VEIN_4976, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 4994, Skills.MINING),
	COPPER_ORE_3(Scenery.MINERAL_VEIN_4977, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 4995, Skills.MINING),
	COPPER_ORE_4(Scenery.MINERAL_VEIN_4978, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 4996, Skills.MINING),
	COPPER_ORE_5(Scenery.ROCKS_9710, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 18954, Skills.MINING),
	COPPER_ORE_6(Scenery.ROCKS_9709, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 32448, Skills.MINING),
	COPPER_ORE_7(Scenery.ROCKS_9708, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 32447, Skills.MINING),
	COPPER_ORE_8(Scenery.ROCKS_11960, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11555, Skills.MINING),
	COPPER_ORE_9(Scenery.ROCKS_11961, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11556, Skills.MINING),
	COPPER_ORE_10(Scenery.ROCKS_11962,  1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11557, Skills.MINING),
	COPPER_ORE_11(Scenery.ROCKS_11937,  1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11553, Skills.MINING),
	COPPER_ORE_12(Scenery.ROCKS_11936,  1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11552, Skills.MINING),
	COPPER_ORE_13(Scenery.ROCKS_11938,  1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11554, Skills.MINING),
	COPPER_ORE_14(Scenery.RUBBLE_12746, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 450, Skills.MINING),
	COPPER_ORE_15(Scenery.ROCKS_14906,  1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 14894, Skills.MINING),
	COPPER_ORE_16(Scenery.ROCKS_14907,  1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 14895, Skills.MINING),
	COPPER_ORE_17(Scenery.ORE_VEIN_20448, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 20445, Skills.MINING),
	COPPER_ORE_18(Scenery.ORE_VEIN_20451, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 20445, Skills.MINING),
	COPPER_ORE_19(Scenery.ORE_VEIN_20446, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 20443, Skills.MINING),
	COPPER_ORE_20(Scenery.ORE_VEIN_20447, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 20444, Skills.MINING),
	COPPER_ORE_21(Scenery.ORE_VEIN_20408, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 20407, Skills.MINING),
	COPPER_ORE_22(Scenery.ROCKS_18993, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 19005, Skills.MINING),
	COPPER_ORE_23(Scenery.ROCKS_18992, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 19004, Skills.MINING),
	COPPER_ORE_24(Scenery.ROCKS_19007, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 19016, Skills.MINING),
	COPPER_ORE_25(Scenery.ROCKS_19006, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 19021, Skills.MINING),
	COPPER_ORE_26(Scenery.ROCKS_18991, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 19003, Skills.MINING),
	COPPER_ORE_27(Scenery.ROCKS_19008, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 19017, Skills.MINING),
	COPPER_ORE_28(Scenery.ROCKS_21285, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 21297, Skills.MINING),
	COPPER_ORE_29(Scenery.ROCKS_21284, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 21296, Skills.MINING),
	COPPER_ORE_30(Scenery.ROCKS_21286, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 21298, Skills.MINING),
	COPPER_ORE_31(Scenery.ROCKS_29231, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 29219, Skills.MINING),
	COPPER_ORE_32(Scenery.ROCKS_29230, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 29218, Skills.MINING),
	COPPER_ORE_33(Scenery.ROCKS_29232, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 29220, Skills.MINING),
	COPPER_ORE_34(Scenery.ROCKS_31082, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37650, Skills.MINING),
	COPPER_ORE_35(Scenery.ROCKS_31081, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37649, Skills.MINING),
	COPPER_ORE_36(Scenery.ROCKS_31080, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37639, Skills.MINING),
	COPPER_ORE_37(Scenery.ROCKS_37647, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37650, Skills.MINING),
	COPPER_ORE_38(Scenery.ROCKS_37646, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37649, Skills.MINING),
	COPPER_ORE_39(Scenery.ROCKS_37645, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37639, Skills.MINING),
	COPPER_ORE_40(Scenery.ROCKS_37637, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 37639, Skills.MINING),
	COPPER_ORE_41(Scenery.ROCKS_37688, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 21298, Skills.MINING),
	COPPER_ORE_42(Scenery.ROCKS_37686, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 21296, Skills.MINING),
	COPPER_ORE_43(Scenery.ROCKS_37687, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 21297, Skills.MINING),
	COPPER_ORE_44(Scenery.ROCKS_3042, 1, 0.05, 4 | 8 << 16, 17.5, 436, 1, "copper rocks", null, 11552, Skills.MINING),

	TIN_ORE_0(Scenery.ROCKS_2094, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 450, Skills.MINING),
	TIN_ORE_1(Scenery.ROCKS_2095, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 452, Skills.MINING),
	TIN_ORE_2(Scenery.ROCKS_3043, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11552, Skills.MINING),
	TIN_ORE_3(Scenery.MINERAL_VEIN_4979, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 4994, Skills.MINING),
	TIN_ORE_4(Scenery.MINERAL_VEIN_4980, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 4995, Skills.MINING),
	TIN_ORE_5(Scenery.MINERAL_VEIN_4981, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 4996, Skills.MINING),
	TIN_ORE_6(Scenery.ROCKS_11957, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11555, Skills.MINING),
	TIN_ORE_7(Scenery.ROCKS_11958, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11556, Skills.MINING),
	TIN_ORE_8(Scenery.ROCKS_11959, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11557, Skills.MINING),
	TIN_ORE_9(Scenery.ROCKS_11934, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11553, Skills.MINING),
	TIN_ORE_10(Scenery.ROCKS_11935, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11554, Skills.MINING),
	TIN_ORE_11(Scenery.ROCKS_11933, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 11552, Skills.MINING),
	TIN_ORE_12(Scenery.ROCKS_14902, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 14894, Skills.MINING),
	TIN_ORE_13(Scenery.ROCKS_14903, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 14895, Skills.MINING),
	TIN_ORE_14(Scenery.ROCKS_18995, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 19004, Skills.MINING),
	TIN_ORE_15(Scenery.ROCKS_18994, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 19003, Skills.MINING),
	TIN_ORE_16(Scenery.ROCKS_18996, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 19005, Skills.MINING),
	TIN_ORE_17(Scenery.ROCKS_19025, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 19016, Skills.MINING),
	TIN_ORE_18(Scenery.ROCKS_19024, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 19021, Skills.MINING),
	TIN_ORE_19(Scenery.ROCKS_19026, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 19017, Skills.MINING),
	TIN_ORE_20(Scenery.ROCKS_21293, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 21296, Skills.MINING),
	TIN_ORE_21(Scenery.ROCKS_21295, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 21298, Skills.MINING),
	TIN_ORE_22(Scenery.ROCKS_21294, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 21297, Skills.MINING),
	TIN_ORE_23(Scenery.ROCKS_29227, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 29218, Skills.MINING),
	TIN_ORE_24(Scenery.ROCKS_29229, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 29220, Skills.MINING),
	TIN_ORE_25(Scenery.ROCKS_29228, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 29219, Skills.MINING),
	TIN_ORE_26(Scenery.ROCKS_31079, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37650, Skills.MINING),
	TIN_ORE_27(Scenery.ROCKS_31078, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37649, Skills.MINING),
	TIN_ORE_28(Scenery.ROCKS_31077, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37639, Skills.MINING),
	TIN_ORE_29(Scenery.ROCKS_37644, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37650, Skills.MINING),
	TIN_ORE_30(Scenery.ROCKS_37643, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37649, Skills.MINING),
	TIN_ORE_31(Scenery.ROCKS_37642, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37639, Skills.MINING),
	TIN_ORE_32(Scenery.ROCKS_37638, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 37639, Skills.MINING),
	TIN_ORE_33(Scenery.ROCKS_37685, 1, 0.05, 4 | 8 << 16, 17.5, 438, 1, "tin rocks", null, 21298, Skills.MINING),

	RUNE_ESSENCE(Scenery.RUNE_ESSENCE_2491, 1, 0.1, 1 | 1 << 16, 5.0, 1436, Integer.MAX_VALUE, "rune essence", null, -1, Skills.MINING),
	RUNE_ESSENCE_1(Scenery.ROCK_16684, 1, 0.1, 1 | 1 << 16, 5.0, 1436, Integer.MAX_VALUE, "rune essence", null, -1, Skills.MINING),
// Scenery.RUNE_ESSENCE_16687
	CLAY_0(Scenery.ROCKS_2109, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 452, Skills.MINING),
	CLAY_1(Scenery.ROCKS_2108, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 450, Skills.MINING),
	CLAY_2(Scenery.ROCKS_9712, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 32448, Skills.MINING),
	CLAY_3(Scenery.ROCKS_9713, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 18954, Skills.MINING),
	CLAY_4(Scenery.ROCKS_9711, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 32447, Skills.MINING),
	CLAY_5(Scenery.ROCKS_10949, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 10945, Skills.MINING),
	CLAY_6(Scenery.ROCKS_11190, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 21297, Skills.MINING),
	CLAY_7(Scenery.ROCKS_11191, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 21298, Skills.MINING),
	CLAY_8(Scenery.ROCKS_11189, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 21296, Skills.MINING),
	CLAY_9(Scenery.MINERAL_VEIN_12942, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 4995, Skills.MINING),
	CLAY_10(Scenery.MINERAL_VEIN_12943, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 4996, Skills.MINING),
	CLAY_11(Scenery.MINERAL_VEIN_12941, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 4994, Skills.MINING),
	CLAY_12(Scenery.ROCKS_14904, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 14894, Skills.MINING),
	CLAY_13(Scenery.ROCKS_14905, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 14895, Skills.MINING),
	CLAY_14(Scenery.ROCKS_15505, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 11557, Skills.MINING),
	CLAY_15(Scenery.ROCKS_15504, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 11556, Skills.MINING),
	CLAY_16(Scenery.ROCKS_15503, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 11555, Skills.MINING),
	CLAY_17(Scenery.ORE_VEIN_20449, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 20443, Skills.MINING),
	CLAY_18(Scenery.ORE_VEIN_20450, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 20444, Skills.MINING),
	CLAY_19(Scenery.ORE_VEIN_20409, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 20407, Skills.MINING),
	CLAY_20(Scenery.ROCKS_32429, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 33400, Skills.MINING),
	CLAY_21(Scenery.ROCKS_32430, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 33401, Skills.MINING),
	CLAY_22(Scenery.ROCKS_32431, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 33402, Skills.MINING),
	CLAY_23(Scenery.ROCKS_31062, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 37639, Skills.MINING),
	CLAY_24(Scenery.ROCKS_31063, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 37649, Skills.MINING),
	CLAY_25(Scenery.ROCKS_31064, 1, 0.1, 1 | 1 << 16, 5.0, 434, 1, "clay", null, 37650, Skills.MINING),

	LIMESTONE_0(Scenery.PILE_OF_ROCK_4027, 10, 0.2, 10 | 20 << 16, 26.5, 3211, 1, "limestone", null, 4028, Skills.MINING),
	LIMESTONE_1(Scenery.PILE_OF_ROCK_4028, 10, 0.2, 10 | 20 << 16, 26.5, 3211, 1, "limestone", null, 4029, Skills.MINING),
	LIMESTONE_2(Scenery.PILE_OF_ROCK_4029, 10, 0.2, 10 | 20 << 16, 26.5, 3211, 1, "limestone", null, 4030, Skills.MINING),
	LIMESTONE_3(Scenery.PILE_OF_ROCK_4030, 10, 0.2, 10 | 20 << 16, 26.5, 3211, 1, "limestone", null, 4027, Skills.MINING),

	BLURITE_ORE_0(Scenery.ROCKS_33220, 10, 0.2, 10 | 20 << 16, 17.5, 668, 1, "blurite rocks", null, 33222, Skills.MINING),
	BLURITE_ORE_1(Scenery.ROCKS_33221, 10, 0.2, 10 | 20 << 16, 17.5, 668, 1, "blurite rocks", null, 33223, Skills.MINING),

	IRON_ORE_0(Scenery.ROCKS_2092, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 450, Skills.MINING),
	IRON_ORE_1(Scenery.ROCKS_2093, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 452, Skills.MINING),
	IRON_ORE_2(Scenery.MINERAL_VEIN_4982, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 4994, Skills.MINING),
	IRON_ORE_3(Scenery.MINERAL_VEIN_4983, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 4995, Skills.MINING),
	IRON_ORE_4(Scenery.MINERAL_VEIN_4984, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 4996, Skills.MINING),
	IRON_ORE_5(Scenery.ROCKS_6943, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 21296, Skills.MINING),
	IRON_ORE_6(Scenery.ROCKS_6944, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 21297, Skills.MINING),
	IRON_ORE_7(Scenery.ROCKS_9718, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 32448, Skills.MINING),
	IRON_ORE_8(Scenery.ROCKS_9719, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 18954, Skills.MINING),
	IRON_ORE_9(Scenery.ROCKS_9717, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 32447, Skills.MINING),
	IRON_ORE_10(Scenery.ROCKS_11956, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 11557, Skills.MINING),
	IRON_ORE_11(Scenery.ROCKS_11954, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 11555, Skills.MINING),
	IRON_ORE_12(Scenery.ROCKS_11955, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 11556, Skills.MINING),
	IRON_ORE_13(Scenery.ROCKS_14914, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 14895, Skills.MINING),
	IRON_ORE_14(Scenery.ROCKS_14913, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 14894, Skills.MINING),
	IRON_ORE_15(Scenery.ROCKS_14858, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 25373, Skills.MINING),
	IRON_ORE_16(Scenery.ROCKS_14857, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 25372, Skills.MINING),
	IRON_ORE_17(Scenery.ROCKS_14856, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 25371, Skills.MINING),
	IRON_ORE_18(Scenery.ROCKS_14900, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 14894, Skills.MINING),
	IRON_ORE_19(Scenery.ROCKS_14901, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 14895, Skills.MINING),
	IRON_ORE_20(Scenery.ORE_VEIN_20423, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 20444, Skills.MINING),
	IRON_ORE_21(Scenery.ORE_VEIN_20422, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 20443, Skills.MINING),
	IRON_ORE_22(Scenery.ORE_VEIN_20425, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 20407, Skills.MINING),
	IRON_ORE_23(Scenery.ORE_VEIN_20424, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 20445, Skills.MINING),
	IRON_ORE_24(Scenery.ROCKS_19002, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 19005, Skills.MINING),
	IRON_ORE_25(Scenery.ROCKS_19001, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 19004, Skills.MINING),
	IRON_ORE_26(Scenery.ROCKS_19000, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 19003, Skills.MINING),
	IRON_ORE_27(Scenery.ROCKS_21281, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 21296, Skills.MINING),
	IRON_ORE_28(Scenery.ROCKS_21283, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 21298, Skills.MINING),
	IRON_ORE_29(Scenery.ROCKS_21282, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 21297, Skills.MINING),
	IRON_ORE_30(Scenery.ROCKS_29221, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 29218, Skills.MINING),
	IRON_ORE_31(Scenery.ROCKS_29223, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 29220, Skills.MINING),
	IRON_ORE_32(Scenery.ROCKS_29222, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 29219, Skills.MINING),
	IRON_ORE_33(Scenery.ROCKS_32441, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 33400, Skills.MINING),
	IRON_ORE_34(Scenery.ROCKS_32443, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 33402, Skills.MINING),
	IRON_ORE_35(Scenery.ROCKS_32442, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 33401, Skills.MINING),
	IRON_ORE_36(Scenery.ROCKS_32452, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 32448, Skills.MINING),
	IRON_ORE_37(Scenery.ROCKS_32451, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 32447, Skills.MINING),
	IRON_ORE_38(Scenery.ROCKS_31073, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 37650, Skills.MINING),
	IRON_ORE_39(Scenery.ROCKS_31072, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 37649, Skills.MINING),
	IRON_ORE_40(Scenery.ROCKS_31071, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 37639, Skills.MINING),
	IRON_ORE_41(Scenery.ROCKS_37307, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 11552, Skills.MINING),
	IRON_ORE_42(Scenery.ROCKS_37309, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 11554, Skills.MINING),
	IRON_ORE_43(Scenery.ROCKS_37308, 15, 0.2, 15 | 25 << 16, 35.0, 440, 1, "iron rocks", null, 11553, Skills.MINING),

	SILVER_ORE_0(Scenery.ROCKS_2101, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 452, Skills.MINING),
	SILVER_ORE_1(Scenery.ROCKS_2100, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 450, Skills.MINING),
	SILVER_ORE_2(Scenery.ROCKS_6945, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 21296, Skills.MINING),
	SILVER_ORE_3(Scenery.ROCKS_6946, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 21297, Skills.MINING),
	SILVER_ORE_4(Scenery.ROCKS_9716, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 18954, Skills.MINING),
	SILVER_ORE_5(Scenery.ROCKS_9714, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 32447, Skills.MINING),
	SILVER_ORE_6(Scenery.ROCKS_9715, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 32448, Skills.MINING),
	SILVER_ORE_7(Scenery.ROCKS_11188, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 21298, Skills.MINING),
	SILVER_ORE_8(Scenery.ROCKS_11186, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 21296, Skills.MINING),
	SILVER_ORE_9(Scenery.ROCKS_11187, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 21297, Skills.MINING),
	SILVER_ORE_10(Scenery.ROCKS_15581, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14834, Skills.MINING),
	SILVER_ORE_11(Scenery.ROCKS_15580, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14833, Skills.MINING),
	SILVER_ORE_12(Scenery.ROCKS_15579, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14832, Skills.MINING),
	SILVER_ORE_13(Scenery.ROCKS_16998, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14915, Skills.MINING),
	SILVER_ORE_14(Scenery.ROCKS_16999, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14916, Skills.MINING),
	SILVER_ORE_15(Scenery.ROCKS_17007, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14915, Skills.MINING),
	SILVER_ORE_16(Scenery.ROCKS_17000, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 31061, Skills.MINING),
	SILVER_ORE_17(Scenery.ROCKS_17009, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 31061, Skills.MINING),
	SILVER_ORE_18(Scenery.ROCKS_17008, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 14916, Skills.MINING),
	SILVER_ORE_19(Scenery.ROCKS_17385, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 32447, Skills.MINING),
	SILVER_ORE_20(Scenery.ROCKS_17387, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 18954, Skills.MINING),
	SILVER_ORE_21(Scenery.ROCKS_17386, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 32448, Skills.MINING),
	SILVER_ORE_22(Scenery.ROCKS_29225, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 29219, Skills.MINING),
	SILVER_ORE_23(Scenery.ROCKS_29224, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 29218, Skills.MINING),
	SILVER_ORE_24(Scenery.ROCKS_29226, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 29220, Skills.MINING),
	SILVER_ORE_25(Scenery.ROCKS_32445, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 33401, Skills.MINING),
	SILVER_ORE_26(Scenery.ROCKS_32444, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 33400, Skills.MINING),
	SILVER_ORE_27(Scenery.ROCKS_32446, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 33402, Skills.MINING),
	SILVER_ORE_28(Scenery.ROCKS_31075, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 37649, Skills.MINING),
	SILVER_ORE_29(Scenery.ROCKS_31074, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 37639, Skills.MINING),
	SILVER_ORE_30(Scenery.ROCKS_31076, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 37650, Skills.MINING),
	SILVER_ORE_31(Scenery.ROCKS_37305, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11553, Skills.MINING),
	SILVER_ORE_32(Scenery.ROCKS_37304, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11552, Skills.MINING),
	SILVER_ORE_33(Scenery.ROCKS_37306, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11554, Skills.MINING),
	SILVER_ORE_34(Scenery.ROCKS_37670, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11552, Skills.MINING),
	SILVER_ORE_35(Scenery.ROCKS_11948, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11555, Skills.MINING),
	SILVER_ORE_36(Scenery.ROCKS_11949, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11556, Skills.MINING),
	SILVER_ORE_37(Scenery.ROCKS_11950, 20, 0.3, 100 | 200 << 16, 40.0, 442, 1, "silver rocks", null, 11557, Skills.MINING),

	COAL_0(Scenery.ROCKS_2097, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 452, Skills.MINING),
	COAL_1(Scenery.ROCKS_2096, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 450, Skills.MINING),
	COAL_2(Scenery.MINERAL_VEIN_4985, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 4994, Skills.MINING),
	COAL_3(Scenery.MINERAL_VEIN_4986, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 4995, Skills.MINING),
	COAL_4(Scenery.MINERAL_VEIN_4987, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 4996, Skills.MINING),
	COAL_5(Scenery.ROCKS_4676, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 450, Skills.MINING),
	COAL_6(Scenery.ROCKS_10948, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 10944, Skills.MINING),
	COAL_7(Scenery.ROCKS_11964, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 11556, Skills.MINING),
	COAL_8(Scenery.ROCKS_11965, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 11557, Skills.MINING),
	COAL_9(Scenery.ROCKS_11963, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 11555, Skills.MINING),
	COAL_10(Scenery.ROCKS_11932, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 11554, Skills.MINING),
	COAL_11(Scenery.ROCKS_11930, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 11552, Skills.MINING),
	COAL_12(Scenery.ROCKS_11931, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 11553, Skills.MINING),
	COAL_13(Scenery.ROCKS_15246, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 15249, Skills.MINING),
	COAL_14(Scenery.ROCKS_15247, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 15250, Skills.MINING),
	COAL_15(Scenery.ROCKS_15248, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 15251, Skills.MINING),
	COAL_16(Scenery.ROCKS_14852, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 25373, Skills.MINING),
	COAL_17(Scenery.ROCKS_14851, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 25372, Skills.MINING),
	COAL_18(Scenery.ROCKS_14850, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 25371, Skills.MINING),
	COAL_19(Scenery.ORE_VEIN_20410, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 20443, Skills.MINING),
	COAL_20(Scenery.ORE_VEIN_20411, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 20444, Skills.MINING),
	COAL_21(Scenery.ORE_VEIN_20412, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 20445, Skills.MINING),
	COAL_22(Scenery.ORE_VEIN_20413, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 20407, Skills.MINING),
	COAL_23(Scenery.ROCKS_18999, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 19005, Skills.MINING),
	COAL_24(Scenery.ROCKS_18998, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 19004, Skills.MINING),
	COAL_25(Scenery.ROCKS_18997, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 19003, Skills.MINING),
	COAL_26(Scenery.ROCKS_21287, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21296, Skills.MINING),
	COAL_27(Scenery.ROCKS_21289, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21298, Skills.MINING),
	COAL_28(Scenery.ROCKS_21288, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21297, Skills.MINING),
	COAL_29(Scenery.ROCKS_23565, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21298, Skills.MINING),
	COAL_30(Scenery.ROCKS_23564, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21297, Skills.MINING),
	COAL_31(Scenery.ROCKS_23563, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21296, Skills.MINING),
	COAL_32(Scenery.ROCKS_29215, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 29218, Skills.MINING),
	COAL_33(Scenery.ROCKS_29217, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 29220, Skills.MINING),
	COAL_34(Scenery.ROCKS_29216, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 29219, Skills.MINING),
	COAL_35(Scenery.ROCKS_32426, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 33400, Skills.MINING),
	COAL_36(Scenery.ROCKS_32427, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 33401, Skills.MINING),
	COAL_37(Scenery.ROCKS_32428, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 33402, Skills.MINING),
	COAL_38(Scenery.ROCKS_32450, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 32448, Skills.MINING),
	COAL_39(Scenery.ROCKS_32449, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 32447, Skills.MINING),
	COAL_40(Scenery.ROCKS_31068, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 37639, Skills.MINING),
	COAL_41(Scenery.ROCKS_31069, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 37649, Skills.MINING),
	COAL_42(Scenery.ROCKS_31070, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 37650, Skills.MINING),
	COAL_43(Scenery.ROCKS_31168, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 14833, Skills.MINING),
	COAL_44(Scenery.ROCKS_31169, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 14834, Skills.MINING),
	COAL_45(Scenery.ROCKS_31167, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 14832, Skills.MINING),
	COAL_46(Scenery.ROCKS_37699, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21298, Skills.MINING),
	COAL_47(Scenery.ROCKS_37698, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21297, Skills.MINING),
	COAL_48(Scenery.ROCKS_37697, 30, 0.4, 50 | 100 << 16, 50.0, 453, 1, "coal", null, 21296, Skills.MINING),

	SANDSTONE(Scenery.ROCKS_10946, 35, 0.2, 30 | 60 << 16, 30.0, 6971, 1, "sandstone", null, 10944, Skills.MINING),

	GOLD_ORE_0(Scenery.ROCKS_2099, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 452, Skills.MINING),
	GOLD_ORE_1(Scenery.ROCKS_2098, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 450, Skills.MINING),
	GOLD_ORE_2(Scenery.ROCKS_2611, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21298, Skills.MINING),
	GOLD_ORE_3(Scenery.ROCKS_2610, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21297, Skills.MINING),
	GOLD_ORE_4(Scenery.ROCKS_2609, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21296, Skills.MINING),
	GOLD_ORE_5(Scenery.ROCKS_9722, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 18954, Skills.MINING),
	GOLD_ORE_6(Scenery.ROCKS_9720, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 32447, Skills.MINING),
	GOLD_ORE_7(Scenery.ROCKS_9721, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 32448, Skills.MINING),
	GOLD_ORE_8(Scenery.ROCKS_11183, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21296, Skills.MINING),
	GOLD_ORE_9(Scenery.ROCKS_11184, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21297, Skills.MINING),
	GOLD_ORE_10(Scenery.ROCKS_11185, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21298, Skills.MINING),
	GOLD_ORE_11(Scenery.ROCKS_11952, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 11556, Skills.MINING),
	GOLD_ORE_12(Scenery.ROCKS_11953, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 11557, Skills.MINING),
	GOLD_ORE_13(Scenery.ROCKS_11951, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 11555, Skills.MINING),
	GOLD_ORE_14(Scenery.ROCKS_15578, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 14834, Skills.MINING),
	GOLD_ORE_15(Scenery.ROCKS_15577, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 14833, Skills.MINING),
	GOLD_ORE_16(Scenery.ROCKS_15576, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 14832, Skills.MINING),
	GOLD_ORE_17(Scenery.ROCKS_17002, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 14916, Skills.MINING),
	GOLD_ORE_18(Scenery.ROCKS_17003, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 31061, Skills.MINING),
	GOLD_ORE_19(Scenery.ROCKS_17001, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 14915, Skills.MINING),
	GOLD_ORE_20(Scenery.ROCKS_21291, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21297, Skills.MINING),
	GOLD_ORE_21(Scenery.ROCKS_21290, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21296, Skills.MINING),
	GOLD_ORE_22(Scenery.ROCKS_21292, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 21298, Skills.MINING),
	GOLD_ORE_23(Scenery.ROCKS_32433, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 33401, Skills.MINING),
	GOLD_ORE_24(Scenery.ROCKS_32432, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 33400, Skills.MINING),
	GOLD_ORE_25(Scenery.ROCKS_32434, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 33402, Skills.MINING),
	GOLD_ORE_26(Scenery.ROCKS_31065, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 37639, Skills.MINING),
	GOLD_ORE_27(Scenery.ROCKS_31066, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 37649, Skills.MINING),
	GOLD_ORE_28(Scenery.ROCKS_31067, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 37650, Skills.MINING),
	GOLD_ORE_29(Scenery.ROCKS_37311, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 11553, Skills.MINING),
	GOLD_ORE_30(Scenery.ROCKS_37310, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 11552, Skills.MINING),
	GOLD_ORE_31(Scenery.ROCKS_37312, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 11554, Skills.MINING),
	GOLD_ORE_32(Scenery.ROCKS_37471, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 15249, Skills.MINING),
	GOLD_ORE_33(Scenery.ROCKS_37473, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 15251, Skills.MINING),
	GOLD_ORE_34(Scenery.ROCKS_37472, 40, 0.6, 100 | 200 << 16, 65.0, 444, 1, "gold rocks", null, 15250, Skills.MINING),

	GRANITE(Scenery.ROCKS_10947, 45, 0.2, 10 | 20 << 16, 50.0, 6979, 1, "granite", null, 10945, Skills.MINING),

	RUBIUM(Scenery.RUBIUM_29746, 46, 0.6, 50 | 100 << 16, 17.5, 12630, 1, "rubium", null, 29747, Skills.MINING),

	MITHRIL_ORE_0(Scenery.ROCKS_2102, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 450, Skills.MINING),
	MITHRIL_ORE_1(Scenery.ROCKS_2103, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 452, Skills.MINING),
	MITHRIL_ORE_2(Scenery.MINERAL_VEIN_4988, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 4994, Skills.MINING),
	MITHRIL_ORE_3(Scenery.MINERAL_VEIN_4989, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 4995, Skills.MINING),
	MITHRIL_ORE_4(Scenery.MINERAL_VEIN_4990, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 4996, Skills.MINING),
	MITHRIL_ORE_5(Scenery.ROCKS_11943, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 11553, Skills.MINING),
	MITHRIL_ORE_6(Scenery.ROCKS_11942, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 11552, Skills.MINING),
	MITHRIL_ORE_7(Scenery.ROCKS_11945, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 11555, Skills.MINING),
	MITHRIL_ORE_8(Scenery.ROCKS_11944, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 11554, Skills.MINING),
	MITHRIL_ORE_9(Scenery.ROCKS_11947, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 11557, Skills.MINING),
	MITHRIL_ORE_10(Scenery.ROCKS_11946, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 11556, Skills.MINING),
	MITHRIL_ORE_11(Scenery.ROCKS_14855, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 25373, Skills.MINING),
	MITHRIL_ORE_12(Scenery.ROCKS_14854, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 25372, Skills.MINING),
	MITHRIL_ORE_13(Scenery.ROCKS_14853, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 25371, Skills.MINING),
	MITHRIL_ORE_14(Scenery.ROCK_16684, 50, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 450, Skills.MINING),
	MITHRIL_ORE_15(Scenery.ORE_VEIN_20421, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 20407, Skills.MINING),
	MITHRIL_ORE_16(Scenery.ORE_VEIN_20420, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 20445, Skills.MINING),
	MITHRIL_ORE_17(Scenery.ORE_VEIN_20419, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 20444, Skills.MINING),
	MITHRIL_ORE_18(Scenery.ORE_VEIN_20418, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 20443, Skills.MINING),
	MITHRIL_ORE_19(Scenery.ROCKS_19012, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 19021, Skills.MINING),
	MITHRIL_ORE_20(Scenery.ROCKS_19013, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 19016, Skills.MINING),
	MITHRIL_ORE_21(Scenery.ROCKS_19014, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 19017, Skills.MINING),
	MITHRIL_ORE_22(Scenery.ROCKS_21278, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 21296, Skills.MINING),
	MITHRIL_ORE_23(Scenery.ROCKS_21279, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 21297, Skills.MINING),
	MITHRIL_ORE_24(Scenery.ROCKS_21280, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 21298, Skills.MINING),
	MITHRIL_ORE_25(Scenery.ROCKS_25369, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 10586, Skills.MINING),
	MITHRIL_ORE_26(Scenery.ROCKS_25368, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 10585, Skills.MINING),
	MITHRIL_ORE_27(Scenery.ROCKS_25370, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 10587, Skills.MINING),
	MITHRIL_ORE_28(Scenery.ROCKS_29236, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 29218, Skills.MINING),
	MITHRIL_ORE_29(Scenery.ROCKS_29237, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 29219, Skills.MINING),
	MITHRIL_ORE_30(Scenery.ROCKS_29238, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 29220, Skills.MINING),
	MITHRIL_ORE_31(Scenery.ROCKS_32439, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 33401, Skills.MINING),
	MITHRIL_ORE_32(Scenery.ROCKS_32438, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 33400, Skills.MINING),
	MITHRIL_ORE_33(Scenery.ROCKS_32440, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 33402, Skills.MINING),
	MITHRIL_ORE_34(Scenery.ROCKS_31087, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 37649, Skills.MINING),
	MITHRIL_ORE_35(Scenery.ROCKS_31086, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 37639, Skills.MINING),
	MITHRIL_ORE_36(Scenery.ROCKS_31088, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 37650, Skills.MINING),
	MITHRIL_ORE_37(Scenery.ROCKS_31170, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 14832, Skills.MINING),
	MITHRIL_ORE_38(Scenery.ROCKS_31171, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 14833, Skills.MINING),
	MITHRIL_ORE_39(Scenery.ROCKS_31172, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 14834, Skills.MINING),
	MITHRIL_ORE_40(Scenery.ROCKS_37692, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 21296, Skills.MINING),
	MITHRIL_ORE_41(Scenery.ROCKS_37693, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 21297, Skills.MINING),
	MITHRIL_ORE_42(Scenery.ROCKS_37694, 55, 0.70, 200 | 400 << 16, 80.0, 447, 1, "mithril rocks", null, 21298, Skills.MINING),

	ADAMANTITE_ORE_0(Scenery.ROCKS_2104, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 450, Skills.MINING),
	ADAMANTITE_ORE_1(Scenery.ROCKS_2105, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 452, Skills.MINING),
	ADAMANTITE_ORE_2(Scenery.MINERAL_VEIN_4991, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 4994, Skills.MINING),
	ADAMANTITE_ORE_3(Scenery.MINERAL_VEIN_4992, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 4995, Skills.MINING),
	ADAMANTITE_ORE_4(Scenery.MINERAL_VEIN_4993, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 4996, Skills.MINING),
	ADAMANTITE_ORE_5(Scenery.ROCKS_11941, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 11554, Skills.MINING),
	ADAMANTITE_ORE_6(Scenery.ROCKS_11940, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 11553, Skills.MINING),
	ADAMANTITE_ORE_7(Scenery.ROCKS_11939, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 11552, Skills.MINING),
	ADAMANTITE_ORE_8(Scenery.ROCKS_14864, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 25373, Skills.MINING),
	ADAMANTITE_ORE_9(Scenery.ROCKS_14863, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 25372, Skills.MINING),
	ADAMANTITE_ORE_10(Scenery.ROCKS_14862, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 25371, Skills.MINING),
	ADAMANTITE_ORE_11(Scenery.ORE_VEIN_20417, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 20407, Skills.MINING),
	ADAMANTITE_ORE_12(Scenery.ORE_VEIN_20416, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 20445, Skills.MINING),
	ADAMANTITE_ORE_13(Scenery.ORE_VEIN_20414, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 20443, Skills.MINING),
	ADAMANTITE_ORE_14(Scenery.ORE_VEIN_20415, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 20444, Skills.MINING),
	ADAMANTITE_ORE_15(Scenery.ROCKS_19020, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 19017, Skills.MINING),
	ADAMANTITE_ORE_16(Scenery.ROCKS_19018, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 19021, Skills.MINING),
	ADAMANTITE_ORE_17(Scenery.ROCKS_19019, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 19016, Skills.MINING),
	ADAMANTITE_ORE_18(Scenery.ROCKS_21275, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 21296, Skills.MINING),
	ADAMANTITE_ORE_19(Scenery.ROCKS_21276, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 21297, Skills.MINING),
	ADAMANTITE_ORE_20(Scenery.ROCKS_21277, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 21298, Skills.MINING),
	ADAMANTITE_ORE_21(Scenery.ROCKS_29233, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 29218, Skills.MINING),
	ADAMANTITE_ORE_22(Scenery.ROCKS_29234, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 29219, Skills.MINING),
	ADAMANTITE_ORE_23(Scenery.ROCKS_29235, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 29220, Skills.MINING),
	ADAMANTITE_ORE_24(Scenery.ROCKS_32435, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 33400, Skills.MINING),
	ADAMANTITE_ORE_25(Scenery.ROCKS_32437, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 33402, Skills.MINING),
	ADAMANTITE_ORE_26(Scenery.ROCKS_32436, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 33401, Skills.MINING),
	ADAMANTITE_ORE_27(Scenery.ROCKS_31083, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 37639, Skills.MINING),
	ADAMANTITE_ORE_28(Scenery.ROCKS_31085, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 37650, Skills.MINING),
	ADAMANTITE_ORE_29(Scenery.ROCKS_31084, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 37649, Skills.MINING),
	ADAMANTITE_ORE_30(Scenery.ROCKS_31173, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 14832, Skills.MINING),
	ADAMANTITE_ORE_31(Scenery.ROCKS_31174, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 14833, Skills.MINING),
	ADAMANTITE_ORE_32(Scenery.ROCKS_31175, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 14834, Skills.MINING),
	ADAMANTITE_ORE_33(Scenery.ROCKS_37468, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 15249, Skills.MINING),
	ADAMANTITE_ORE_34(Scenery.ROCKS_37469, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 15250, Skills.MINING),
	ADAMANTITE_ORE_35(Scenery.ROCKS_37470, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 15251, Skills.MINING),
	ADAMANTITE_ORE_36(Scenery.ROCKS_37689, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 21296, Skills.MINING),
	ADAMANTITE_ORE_37(Scenery.ROCKS_37690, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 21297, Skills.MINING),
	ADAMANTITE_ORE_38(Scenery.ROCKS_37691, 70, 0.85, 400 | 800 << 16, 95.0, 449, 1, "adamant rocks", null, 21298, Skills.MINING),

	RUNITE_ORE_0(Scenery.ROCKS_2107, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 452, Skills.MINING),
	RUNITE_ORE_1(Scenery.ROCKS_2106, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 450, Skills.MINING),
	RUNITE_ORE_2(Scenery.ROCKS_6669, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 25373, Skills.MINING),
	RUNITE_ORE_3(Scenery.ROCKS_6671, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 25372, Skills.MINING),
	RUNITE_ORE_4(Scenery.ROCKS_6670, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 25371, Skills.MINING),
	RUNITE_ORE_5(Scenery.ROCKS_14861, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 33401, Skills.MINING),
	RUNITE_ORE_6(Scenery.ROCKS_14860, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 33400, Skills.MINING),
	RUNITE_ORE_7(Scenery.ROCKS_37208, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 21296, Skills.MINING),
	RUNITE_ORE_8(Scenery.ROCKS_37465, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 15249, Skills.MINING),
	RUNITE_ORE_9(Scenery.ROCKS_37466, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 15250, Skills.MINING),
	RUNITE_ORE_10(Scenery.ROCKS_37467, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 15251, Skills.MINING),
	RUNITE_ORE_11(Scenery.ROCKS_37695, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 21297, Skills.MINING),
	RUNITE_ORE_12(Scenery.ROCKS_37696, 85, 0.95, 1250 | 2500 << 16, 125.0, 451, 1, "runite rocks", null, 21298, Skills.MINING),

	MAGIC_STONE_0(Scenery.ROCKS_6669, 20, 0.3, 100 | 200 << 16, 0.0, 4703, 1, "magic stone", null, 21296, Skills.MINING),
	MAGIC_STONE_1(Scenery.ROCKS_6671, 20, 0.3, 100 | 200 << 16, 0.0, 4703, 1, "magic stone", null, 21298, Skills.MINING),
	MAGIC_STONE_2(Scenery.ROCKS_6670, 20, 0.3, 100 | 200 << 16, 0.0, 4703, 1, "magic stone", null, 21297, Skills.MINING),

	GEM_ROCK_0(Scenery.ROCKS_23567, 40, 0.95, 166 | 175 << 16, 65, 1625, 1, "gem rocks", null, 21297, Skills.MINING),
	GEM_ROCK_1(Scenery.ROCKS_23566, 40, 0.95, 166 | 175 << 16, 65, 1625, 1, "gem rocks", null, 21296, Skills.MINING),
	GEM_ROCK_2(Scenery.ROCKS_23568, 40, 0.95, 166 | 175 << 16, 65, 1625, 1, "gem rocks", null, 21298, Skills.MINING);

	private static final Map<Integer, SkillingResource> RESOURCES = new HashMap<>();

	static {
		for (SkillingResource resource : SkillingResource.values()) {
			if (RESOURCES.containsKey(resource.id)) {
			}
			RESOURCES.put(resource.id, resource);
		}
	}

	private final int id;
	private final int level;
	private final double rate;
	private final int respawnRate;
	private final double experience;
	private final int reward;
	private final int rewardAmount;
	private final String name;
	private final Animation animation;
	private final int emptyId;
	private final int skillId;
	private final boolean farming;

	private SkillingResource(int id, int level, double rate, int respawnRate, double experience, int reward, int rewardAmount, String name, Animation animation, int emptyId, int skillId) {
		this.id = id;
		this.level = level;
		this.rate = rate;
		this.respawnRate = respawnRate;
		this.experience = experience;
		this.reward = reward;
		this.rewardAmount = rewardAmount;
		this.name = name;
		this.animation = animation;
		this.emptyId = emptyId;
		this.skillId = skillId;
		this.farming = false;
	}

	private SkillingResource(int id, int level, double rate, int respawnRate, double experience, int reward, int rewardAmount, String name, Animation animation, int emptyId, int skillId, boolean farming) {
		this.id = id;
		this.level = level;
		this.rate = rate;
		this.respawnRate = respawnRate;
		this.experience = experience;
		this.reward = reward;
		this.rewardAmount = rewardAmount;
		this.name = name;
		this.animation = animation;
		this.emptyId = emptyId;
		this.skillId = skillId;
		this.farming = farming;
	}

	public static SkillingResource forId(int id) {
		return RESOURCES.get(id);
	}

	public static boolean isEmpty(int id) {
		for (SkillingResource r : SkillingResource.values()) {
			if (r.getEmptyId() == id) {
				return true;
			}
		}
		return false;
	}

	public int getRespawnDuration() {
		int minimum = respawnRate & 0xFFFF;
		int maximum = (respawnRate >> 16) & 0xFFFF;
		double playerRatio = (double) ServerConstants.MAX_PLAYERS / Repository.getPlayers().size();
		return (int) (minimum + ((maximum - minimum) / playerRatio));
	}

	public int getMaximumRespawn() {
		return (respawnRate >> 16) & 0xFFFF;
	}

	public int getMinimumRespawn() {
		return respawnRate & 0xFFFF;
	}

	public int getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public double getRate() {
		return rate;
	}

	public int getRespawnRate() {
		return respawnRate;
	}

	public double getExperience() {
		return experience;
	}

	public int getReward() {
		return reward;
	}

	public String getName() {
		return name;
	}

	public Animation getAnimation() {
		return animation;
	}

	public int getEmptyId() {
		return emptyId;
	}

	public int getSkillId() {
		return skillId;
	}

	public int getRewardAmount() {
		return rewardAmount;
	}

	public boolean isFarming() {
		return farming;
	}
}