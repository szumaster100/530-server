package content.global.skill.gathering.woodcutting;

import core.ServerConstants;
import core.game.world.repository.Repository;

import java.util.HashMap;

public enum WoodcuttingNode {
    STANDARD_TREE_1( 1276, 1342,  (byte) 1),
    STANDARD_TREE_2( 1277, 1343,  (byte) 1),
    STANDARD_TREE_3( 1278, 1342,  (byte) 1),
    STANDARD_TREE_4( 1279, 1345,  (byte) 1),
    STANDARD_TREE_5( 1280, 1343,  (byte) 1),
    STANDARD_TREE_6( 1330, 1341,  (byte) 1),
    STANDARD_TREE_7( 1331, 1341,  (byte) 1),
    STANDARD_TREE_8( 1332, 1341,  (byte) 1),
    STANDARD_TREE_9( 2409, 1342,  (byte) 1),
    STANDARD_TREE_10(3033, 1345,  (byte) 1),
    STANDARD_TREE_11(3034, 1345,  (byte) 1),
    STANDARD_TREE_12(3035, 1347,  (byte) 1),
    STANDARD_TREE_13(3036, 1351,  (byte) 1),
    STANDARD_TREE_14(3879, 3880,  (byte) 1),
    STANDARD_TREE_15(3881, 3880,  (byte) 1),
    STANDARD_TREE_16(3882, 3880,  (byte) 1),
    STANDARD_TREE_17(3883, 3884,  (byte) 1),
    STANDARD_TREE_18(10041,1342,  (byte) 1),
    STANDARD_TREE_19(14308,1342,  (byte) 1),
    STANDARD_TREE_20(14309,1342,  (byte) 1),
    STANDARD_TREE_21(16264,1342,  (byte) 1),
    STANDARD_TREE_22(16265,1342,  (byte) 1),
    STANDARD_TREE_23(30132,1342,  (byte) 1),
    STANDARD_TREE_24(30133,1342,  (byte) 1),
    STANDARD_TREE_25(37477,1342,  (byte) 1),
    STANDARD_TREE_26(37478,37653, (byte) 1),
    STANDARD_TREE_27(37652,37653, (byte) 1),

    DEAD_TREE_1( 1282, 1347,  (byte) 2),
    DEAD_TREE_2( 1283, 1347,  (byte) 2),
    DEAD_TREE_3( 1284, 1348,  (byte) 2),
    DEAD_TREE_4( 1285, 1349,  (byte) 2),
    DEAD_TREE_5( 1286, 1351,  (byte) 2),
    DEAD_TREE_6( 1289, 1353,  (byte) 2),
    DEAD_TREE_7( 1290, 1354,  (byte) 2),
    DEAD_TREE_8( 1291, 23054, (byte) 2),
    DEAD_TREE_9( 1365, 1352,  (byte) 2),
    DEAD_TREE_10(1383, 1358,  (byte) 2),
    DEAD_TREE_11(1384, 1359,  (byte) 2),
    DEAD_TREE_12(5902, 1347,  (byte) 2),
    DEAD_TREE_13(5903, 1353,  (byte) 2),
    DEAD_TREE_14(5904, 1353,  (byte) 2),
    DEAD_TREE_15(32294,1353,  (byte) 2),
    DEAD_TREE_16(37481,1347,  (byte) 2),
    DEAD_TREE_17(37482,1351,  (byte) 2),
    DEAD_TREE_18(37483,1358,  (byte) 2),
    DEAD_TREE_19(24168,24169, (byte) 2),

    EVERGREEN_1(1315,1342,(byte) 3),
    EVERGREEN_2(1316,1355,(byte) 3),
    EVERGREEN_3(1318,1355,(byte) 3),
    EVERGREEN_4(1319,1355,(byte) 3),

    JUNGLE_TREE_1(2887,0, (byte) 4),
    JUNGLE_TREE_2(2889,0, (byte) 4),
    JUNGLE_TREE_3(2890,0, (byte) 4),
    JUNGLE_TREE_4(4818,0, (byte) 4),
    JUNGLE_TREE_5(4820,0, (byte) 4),

    JUNGLE_BUSH_1(2892,2894, (byte) 5),
    JUNGLE_BUSH_2(2893,2895, (byte) 5),

    ACHEY_TREE(2023,3371, (byte) 6),

    OAK_TREE_1(1281, 1356, (byte) 7),
    OAK_TREE_2(3037, 1357, (byte) 7),
    OAK_TREE_3(37479,1356, (byte) 7),
    OAK_TREE_4(8467, 1356, (byte) 19, true),

    WILLOW_TREE_1(1308, 7399, (byte) 8),
    WILLOW_TREE_2(5551, 5554, (byte) 8),
    WILLOW_TREE_3(5552, 5554, (byte) 8),
    WILLOW_TREE_4(5553, 5554, (byte) 8),
    WILLOW_TREE_5(37480,7399, (byte) 8),
    WILLOW_TREE_6(8488,7399, (byte) 20, true),

    TEAK_1(9036, 9037, (byte) 9),
    TEAK_2(15062,9037, (byte) 9),

    MAPLE_TREE_1(1307,7400, (byte) 10),
    MAPLE_TREE_2(4674,7400, (byte) 10),
    MAPLE_TREE_3(8444,7400, (byte) 21, true),

    HOLLOW_TREE_1(2289,2310, (byte) 11),
    HOLLOW_TREE_2(4060,4061, (byte) 11),

    MAHOGANY(9034,9035, (byte) 12),

    SWAYING_TREE(4142,-1, (byte) 30),

    ARCTIC_PINE(21273,21274, (byte) 13),

    EUCALYPTUS_1(28951,28954, (byte) 14),
    EUCALYPTUS_2(28952,28955, (byte) 14),
    EUCALYPTUS_3(28953,28956, (byte) 14),

    YEW(1309,7402, (byte) 15),
    YEW_1(8513,7402,(byte) 22, true),

    MAGIC_TREE_1(1306,  7401, (byte) 16),
    MAGIC_TREE_2(37823,37824, (byte) 16),
    MAGIC_TREE_3(8409, 37824, (byte) 23, true),

    CURSED_MAGIC_TREE(37821,37822, (byte) 17),

    DRAMEN_TREE(1292, -1, (byte) 18),

    WINDSWEPT_TREE(18137, 1353, (byte) 19),
    LIGHT_JUNGLE_1(9010, 9010, (byte) 31),
    LIGHT_JUNGLE_2(9011, 9010, (byte) 31),
    LIGHT_JUNGLE_3(9012, 9010, (byte) 31),
    LIGHT_JUNGLE_4(9013, 9010, (byte) 31),
    MEDIUM_JUNGLE_1(9015, 9015, (byte) 32),
    MEDIUM_JUNGLE_2(9016, 9015, (byte) 32),
    MEDIUM_JUNGLE_3(9017, 9015, (byte) 32),
    MEDIUM_JUNGLE_4(9018, 9015, (byte) 32),
    DENSE_JUNGLE_1(9020, 9020, (byte) 33),
    DENSE_JUNGLE_2(9021, 9020, (byte) 33),
    DENSE_JUNGLE_3(9022, 9020, (byte) 33),
    DENSE_JUNGLE_4(9023, 9020, (byte) 33);

    int full,empty,reward,respawnRate,level, rewardAmount;
    double experience,rate;
    public byte identifier;
    boolean farming;
    public double baseLow = 2;
    public double baseHigh = 6;
    public double tierModLow = 1;
    public double tierModHigh = 3;
    WoodcuttingNode(int full, int empty,byte identifier){
        this.full = full;
        this.empty = empty;
        this.identifier = identifier;
        this.farming = false;
        this.rewardAmount = 1;
        switch(identifier & 0xFF){
            case 1:
            case 2:
            case 3:
            case 4:
                reward = 1511;
                respawnRate = 50 | 100 << 16;
                rate = 0.05;
                experience = 25.0;
                level = 1;
                baseLow = 64;
                baseHigh = 200;
                tierModLow = 32;
                tierModHigh = 100;
                break;
            case 5:
                reward = 1511;
                respawnRate = 50 | 100 << 16;
                rate = 0.15;
                experience = 100;
                level = 1;
                this.rewardAmount = 2;
                baseLow = 64;
                baseHigh = 200;
                tierModLow = 32;
                tierModHigh = 100;
                break;
            case 6:
                reward = 2862;
                respawnRate = 50 | 100 << 16;
                rate = 0.05;
                experience = 25.0;
                level = 1;
                baseLow = 64;
                baseHigh = 200;
                tierModLow = 32;
                tierModHigh = 100;
                break;
            case 7:
                reward = 1521;
                respawnRate = 14 | 22 << 16;
                rate = 0.15;
                experience = 37.5;
                level = 15;
                rewardAmount = 10;
                baseLow = 32;
                baseHigh = 100;
                tierModLow = 16;
                tierModHigh = 50;
                break;
            case 8:
                reward = 1519;
                respawnRate = 14 | 22 << 16;
                rate = 0.3;
                experience = 67.8;
                level = 30;
                rewardAmount = 20;
                baseLow = 16;
                baseHigh = 50;
                tierModLow = 8;
                tierModHigh = 25;
                break;
            case 9:
                reward = 6333;
                respawnRate = 35 | 60 << 16;
                rate = 0.7;
                experience = 85.0;
                level = 35;
                rewardAmount = 25;
                baseLow = 15;
                baseHigh = 46;
                tierModLow = 8;
                tierModHigh = 23.5;
                break;
            case 10:
                reward = 1517;
                respawnRate = 58 | 100 << 16;
                rate = 0.65;
                experience = 100.0;
                level = 45;
                rewardAmount = 30;
                baseLow = 8;
                baseHigh = 25;
                tierModLow = 4;
                tierModHigh = 12.5;
                break;
            case 11:
                reward = 3239;
                respawnRate = 58 | 100 << 16;
                rate = 0.6;
                experience = 82.5;
                level = 45;
                rewardAmount = 30;
                baseLow = 18;
                baseHigh = 26;
                tierModLow = 10;
                tierModHigh = 14;
                break;
            case 12:
                reward = 6332;
                respawnRate = 62 | 115 << 16;
                rate = 0.7;
                experience = 125.0;
                level = 50;
                rewardAmount = 35;
                baseLow = 8;
                baseHigh = 25;
                tierModLow = 4;
                tierModHigh = 12.5;
                break;
            case 13:
                reward = 10810;
                respawnRate = 75 | 130 << 16;
                rate = 0.73;
                experience = 40.0;
                level = 54;
                rewardAmount = 35;
                baseLow = 6;
                baseHigh = 30;
                tierModLow = 3;
                tierModHigh = 13.5;
                break;
            case 14:
                reward = 12581;
                respawnRate = 80 | 140 << 16;
                rate = 0.77;
                experience = 165.0;
                level = 58;
                rewardAmount = 35;
                break;
            case 15:
                reward = 1515;
                respawnRate = 100 | 162 << 16;
                rate = 0.8;
                experience = 175.0;
                level = 60;
                rewardAmount = 40;
                baseLow = 4;
                baseHigh = 12.5;
                tierModLow = 2;
                tierModHigh = 6.25;
                break;
            case 16:
                reward = 1513;
                respawnRate = 200 | 317 << 16;
                rate = 0.9;
                experience = 250.0;
                level = 75;
                rewardAmount = 50;
                baseLow = 2;
                baseHigh = 6;
                tierModLow = 1;
                tierModHigh = 3;
                break;
            case 17:
                reward = 1513;
                respawnRate = 200 | 317 << 16;
                rate = 0.95;
                experience = 275.0;
                level = 82;
                rewardAmount = 50;
                break;
            case 18:
                reward = 771;
                respawnRate = -1;
                rate = 0.05;
                experience = 25.0;
                level = 36;
                rewardAmount = Integer.MAX_VALUE;
                baseLow = 255;
                baseHigh = 255;
                tierModLow = 0;
                tierModHigh = 0;
                break;
            case 30:
                reward = 3692;
                respawnRate = -1;
                rate = 0.05;
                experience = 1;
                level = 40;
                rewardAmount = Integer.MAX_VALUE;
                break;
        }
    }
    WoodcuttingNode(int full, int empty, byte identifier, boolean farming){
        this.full = full;
        this.empty = empty;
        this.identifier = identifier;
        this.farming = farming;
        switch(identifier & 0xFF){
            case 19:
                reward = 1521;
                respawnRate = 14 | 22 << 16;
                rate = 0.15;
                experience = 37.5;
                level = 15;
                rewardAmount = 10;
                baseLow = 32;
                baseHigh = 100;
                tierModLow = 16;
                tierModHigh = 50;
                break;
            case 20:
                reward = 1519;
                respawnRate = 14 | 22 << 16;
                rate = 0.3;
                experience = 67.8;
                level = 30;
                rewardAmount = 20;
                baseLow = 16;
                baseHigh = 50;
                tierModLow = 8;
                tierModHigh = 25;
                break;
            case 21:
                reward = 1517;
                respawnRate = 58 | 100 << 16;
                rate = 0.65;
                experience = 100.0;
                level = 45;
                rewardAmount = 30;
                baseLow = 8;
                baseHigh = 25;
                tierModLow = 4;
                tierModHigh = 12.5;
                break;
            case 22:
                reward = 1515;
                respawnRate = 100 | 162 << 16;
                rate = 0.8;
                experience = 175.0;
                level = 60;
                rewardAmount = 40;
                baseLow = 4;
                baseHigh = 12.5;
                tierModLow = 2;
                tierModHigh = 6.25;
                break;
            case 23:
                reward = 1513;
                respawnRate = 200 | 317 << 16;
                rate = 0.9;
                experience = 250.0;
                level = 75;
                rewardAmount = 50;
                baseLow = 2;
                baseHigh = 6;
                tierModLow = 1;
                tierModHigh = 3;
                break;
            case 31:
                reward = 6281;
                respawnRate = 200 | (317 << 16);
                rate = 0.2;
                experience = 32.0;
                level = 10;
                rewardAmount = 50;
                baseLow = 0.0;
                baseHigh = 9.5;
                tierModLow = 0.065;
                tierModHigh = 0.25;
                break;
            case 32:
                reward = 6283;
                respawnRate = 200 | (317 << 16);
                rate = 0.2;
                experience = 55.0;
                level = 20;
                rewardAmount = 50;
                baseLow = 0.0;
                baseHigh = 8.0;
                tierModLow = 0.065;
                tierModHigh = 0.25;
                break;
            case 33:
                reward = 6285;
                respawnRate = 200 | (317 << 16);
                rate = 0.2;
                experience = 80.0;
                level = 35;
                rewardAmount = 50;
                baseLow = 0.0;
                baseHigh = 6.0;
                tierModLow = 0.06;
                tierModHigh = 0.25;
                break;
        }
    }

    private static HashMap<Integer, WoodcuttingNode> NODE_MAP = new HashMap<>();
    private static HashMap<Integer, Integer> EMPTY_MAP = new HashMap<>();
    static{
        for(WoodcuttingNode node : WoodcuttingNode.values()){
            NODE_MAP.putIfAbsent(node.full,node);
            EMPTY_MAP.putIfAbsent(node.empty,node.full);
        }
    }

    public static WoodcuttingNode forId(int id){
        return NODE_MAP.get(id);
    }

    public static boolean isEmpty(int id){
        return EMPTY_MAP.get(id) != null;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public int getEmptyId() {
        return empty;
    }

    public int getReward() {
        return reward;
    }

    public double getExperience() {
        return experience;
    }

    public int getRespawnRate() {
        return respawnRate;
    }

    public double getRate() {
        return rate;
    }

    public int getLevel() {
        return level;
    }

    public int getId() {
        return full;
    }

    public int getMinimumRespawn() {
        return respawnRate & 0xFFFF;
    }

    public int getMaximumRespawn() {
        return (respawnRate >> 16) & 0xFFFF;
    }

    public boolean isFarming(){ return farming;}

    public int getRespawnDuration() {
        int minimum = respawnRate & 0xFFFF;
        int maximum = (respawnRate >> 16) & 0xFFFF;
        double playerRatio = (double) ServerConstants.MAX_PLAYERS / Repository.getPlayers().size();
        return (int) (minimum + ((maximum - minimum) / playerRatio));
    }
}
