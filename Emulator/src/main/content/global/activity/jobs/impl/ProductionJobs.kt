package content.global.activity.jobs.impl

import content.global.activity.jobs.Job
import content.global.activity.jobs.JobType
import core.game.node.entity.skill.Skills
import org.rs.consts.Items

enum class ProductionJobs(
    override val lower: Int,
    override val upper: Int,
    val lvlReq: Int,
    val itemId: Int,
    val skill: Int,
    override val employer: Employers,
) : Job {
    BRONZE_DAGGER(24, 24, 1, Items.BRONZE_DAGGER_1205, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_HELMS(22, 22, 1, Items.BRONZE_MED_HELM_1139, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_FULL_HELM(10, 10, 7, Items.BRONZE_FULL_HELM_1155, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_CHAINBODY(10, 10, 11, Items.BRONZE_CHAINBODY_1103, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_2H(10, 10, 14, Items.BRONZE_2H_SWORD_1307, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_SCIM(10, 10, 5, Items.BRONZE_SCIMITAR_1321, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_PLATEBODY(9, 10, 18, Items.BRONZE_PLATEBODY_1117, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_PLATELEGS(9, 10, 16, Items.BRONZE_PLATELEGS_1075, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_PLATESKIRTS(9, 10, 16, Items.BRONZE_PLATESKIRT_1087, Skills.SMITHING, Employers.SMELTING_TUTOR),
    BRONZE_WARHAMMER(9, 9, 9, Items.BRONZE_WARHAMMER_1337, Skills.SMITHING, Employers.SMELTING_TUTOR),
    IRON_DAGGER(13, 13, 15, Items.IRON_DAGGER_1203, Skills.SMITHING, Employers.SMELTING_TUTOR),
    IRON_CHAINBODY(10, 10, 26, Items.IRON_CHAINBODY_1101, Skills.SMITHING, Employers.SMELTING_TUTOR),
    IRON_2H(9, 9, 29, Items.IRON_2H_SWORD_1309, Skills.SMITHING, Employers.SMELTING_TUTOR),
    STEEL_BATTLEAXE(9, 9, 40, Items.STEEL_BATTLEAXE_1365, Skills.SMITHING, Employers.SMELTING_TUTOR),
    STEEL_SCIMITAR(9, 9, 35, Items.STEEL_SCIMITAR_1325, Skills.SMITHING, Employers.SMELTING_TUTOR),
    STEEL_PLATEBODY(9, 10, 48, Items.STEEL_PLATEBODY_1119, Skills.SMITHING, Employers.SMELTING_TUTOR),
    STEEL_WARHAMMER(9, 10, 39, Items.STEEL_WARHAMMER_1339, Skills.SMITHING, Employers.SMELTING_TUTOR),
    STEEL_SQUARE_SHIELD(9, 9, 30, Items.STEEL_SQ_SHIELD_1177, Skills.SMITHING, Employers.SMELTING_TUTOR),

    FIRE_RUNE(22, 26, 14, Items.FIRE_RUNE_554, Skills.RUNECRAFTING, Employers.AGGIE),
    RUNE_ESS(20, 28, 1, Items.RUNE_ESSENCE_1436, Skills.MINING, Employers.MINING_TUTOR),
    EARTH_RUNE(20, 28, 9, Items.EARTH_RUNE_557, Skills.RUNECRAFTING, Employers.AGGIE),
    WATER_RUNE(22, 22, 5, Items.WATER_RUNE_555, Skills.RUNECRAFTING, Employers.HANS),
    AIR_RUNE(20, 28, 1, Items.AIR_RUNE_556, Skills.RUNECRAFTING, Employers.HANS),

    BALL_OF_WOOL(20, 28, 1, Items.BALL_OF_WOOL_1759, Skills.CRAFTING, Employers.GILLIE_GROATS),
    LEATHER_BOOT(24, 24, 1, Items.LEATHER_BOOTS_1061, Skills.CRAFTING, Employers.HANS),
    LEATHER_GLOVE(22, 25, 1, Items.LEATHER_GLOVES_1059, Skills.CRAFTING, Employers.GILLIE_GROATS),
    LEATHER_CHAPS(22, 28, 18, Items.LEATHER_CHAPS_1095, Skills.CRAFTING, Employers.CRAFTING_TUTOR),
    LEATHER_VAMPS(22, 28, 11, Items.LEATHER_VAMBRACES_1063, Skills.CRAFTING, Employers.IAIN),
    LEATHER_BODY(22, 28, 14, Items.LEATHER_BODY_1129, Skills.CRAFTING, Employers.CRAFTING_TUTOR),
    LEATHER_COWL(22, 28, 9, Items.LEATHER_COWL_1167, Skills.CRAFTING, Employers.CRAFTING_TUTOR),
    COIF(22, 28, 38, Items.COIF_1169, Skills.CRAFTING, Employers.IAIN),
    HARD_LEATHER_BODY(22, 28, 28, Items.HARDLEATHER_BODY_1131, Skills.CRAFTING, Employers.IAIN),
    COWHIDES(20, 25, 1, Items.COWHIDE_1739, 0, Employers.HANS),

    RAW_SHRIMP(22, 28, 1, Items.RAW_SHRIMPS_317, Skills.FISHING, Employers.FISHING_TUTOR),
    COOKED_SHRIMP(22, 28, 1, Items.SHRIMPS_315, Skills.COOKING, Employers.COOKING_TUTOR),
    RAW_CRAYFISH(22, 28, 1, Items.RAW_CRAYFISH_13435, Skills.FISHING, Employers.FISHING_TUTOR),
    COOKED_CRAYFISH(22, 28, 1, Items.CRAYFISH_13433, Skills.COOKING, Employers.COOKING_TUTOR),
    RAW_TROUT(22, 28, 20, Items.RAW_TROUT_335, Skills.FISHING, Employers.FISHING_TUTOR),
    COOKED_TROUT(22, 28, 15, Items.TROUT_333, Skills.COOKING, Employers.COOKING_TUTOR),
    RAW_SALMON(22, 28, 30, Items.RAW_SALMON_331, Skills.FISHING, Employers.FISHING_TUTOR),
    COOKED_SALMON(22, 28, 25, Items.SALMON_329, Skills.COOKING, Employers.COOKING_TUTOR),
    ANCHOVY_PIZZA(12, 16, 55, Items.ANCHOVY_PIZZA_2297, Skills.COOKING, Employers.COOKING_TUTOR),

    BREAD(12, 26, 1, Items.BREAD_2309, Skills.COOKING, Employers.GILLIE_GROATS),
    CAKE(12, 16, 40, Items.CAKE_1891, Skills.COOKING, Employers.GILLIE_GROATS),
    MEAT_PIE(12, 16, 20, Items.MEAT_PIE_2327, Skills.COOKING, Employers.COOKING_TUTOR),
    PLAIN_PIZZA(12, 16, 35, Items.PLAIN_PIZZA_2289, Skills.COOKING, Employers.COOKING_TUTOR),
    REDBERRY_PIE(12, 16, 10, Items.REDBERRY_PIE_2325, Skills.COOKING, Employers.GILLIE_GROATS),

    LOG(22, 28, 1, Items.LOGS_1511, Skills.WOODCUTTING, Employers.WOODCUTTING_TUTOR),
    OAK(22, 28, 15, Items.OAK_LOGS_1521, Skills.WOODCUTTING, Employers.WOODCUTTING_TUTOR),
    WILLOW(22, 28, 30, Items.WILLOW_LOGS_1519, Skills.WOODCUTTING, Employers.WOODCUTTING_TUTOR),

    COPPER_ORE(22, 28, 1, Items.COPPER_ORE_436, Skills.MINING, Employers.MINING_TUTOR),
    TIN_ORE(23, 26, 1, Items.TIN_ORE_438, Skills.MINING, Employers.MINING_TUTOR),
    IRON_ORE(24, 24, 15, Items.IRON_ORE_440, Skills.MINING, Employers.MINING_TUTOR),
    SILVER_ORE(22, 28, 20, Items.SILVER_ORE_442, Skills.MINING, Employers.PRAYER_TUTOR),
    COAL(22, 26, 30, Items.COAL_453, Skills.MINING, Employers.MINING_TUTOR),
    GOLD_ORE(22, 26, 40, Items.GOLD_ORE_444, Skills.MINING, Employers.MINING_TUTOR),

    BRONZE_BAR(10, 13, 1, Items.BRONZE_BAR_2349, Skills.SMITHING, Employers.SMELTING_TUTOR),
    IRON_BAR(22, 28, 15, Items.IRON_BAR_2351, Skills.SMITHING, Employers.SMELTING_TUTOR),
    STEEL_BAR(12, 17, 30, Items.STEEL_BAR_2353, Skills.SMITHING, Employers.SMELTING_TUTOR),
    GOLD_BAR(9, 16, 40, Items.GOLD_BAR_2357, Skills.SMITHING, Employers.SMELTING_TUTOR),

    BONES(22, 28, 1, Items.BONES_526, 0, Employers.HANS),
    ASHES(24, 26, 1, Items.ASHES_592, 0, Employers.AGGIE),
    ;

    override val type = JobType.PRODUCTION
}
