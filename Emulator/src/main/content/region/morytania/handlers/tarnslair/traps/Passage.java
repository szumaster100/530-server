package content.region.morytania.handlers.tarnslair.traps;

import org.rs.consts.Scenery;
import core.game.world.map.Location;

import java.util.HashMap;

public enum Passage {

    FLOOR_1_PASSAGE_1(new PassageScenery(Scenery.PASSAGEWAY_20575, new Location(3158, 4553, 0)), new Location(3158, 4557, 1)),
    FLOOR_1_PASSAGE_2(new PassageScenery(Scenery.PASSAGEWAY_20575, new Location(3184, 4553, 0)), new Location(3184, 4557, 1)),
    FLOOR_1_PASSAGE_3(new PassageScenery(Scenery.PASSAGEWAY_20847, new Location(3178, 4561, 0)), new Location(3174, 4561, 1)),
    FLOOR_1_PASSAGE_4(new PassageScenery(Scenery.PASSAGEWAY_20849, new Location(3195, 4571, 0)), new Location(3195, 4575, 1)),
    FLOOR_1_PASSAGE_5(new PassageScenery(Scenery.PASSAGEWAY_20575, new Location(3147, 4548, 0)), new Location(3143, 4548, 1)),
    FLOOR_1_PASSAGE_6(new PassageScenery(Scenery.PASSAGEWAY_20824, new Location(3156, 4559, 0)), new Location(3161, 4559, 0)),
    FLOOR_1_PASSAGE_7(new PassageScenery(Scenery.PASSAGEWAY_20823, new Location(3160, 4559, 0)), new Location(3155, 4559, 0)),
    FLOOR_1_PASSAGE_8(new PassageScenery(Scenery.PASSAGEWAY_20822, new Location(3163, 4561, 0)), new Location(3163, 4565, 0)),
    FLOOR_1_PASSAGE_9(new PassageScenery(Scenery.PASSAGEWAY_20821, new Location(3163, 4564, 0)), new Location(3163, 4560, 0)),

    FLOOR_1_PASSAGE_10(new PassageScenery(Scenery.PASSAGEWAY_20575, new Location(3168, 4569, 0)), new Location(3172, 4569, 1)),
    FLOOR_1_PASSAGE_11(new PassageScenery(Scenery.PASSAGEWAY_20842, new Location(3184, 4570, 0)), new Location(3184, 4566, 1)),
    FLOOR_1_PASSAGE_12(new PassageScenery(Scenery.PASSAGEWAY_20843, new Location(3184, 4580, 0)), new Location(3184, 4585, 1)),
    FLOOR_1_PASSAGE_13(new PassageScenery(Scenery.PASSAGEWAY_20846, new Location(3171, 4577, 0)), new Location(3175, 4577, 1)),
    FLOOR_1_PASSAGE_14(new PassageScenery(Scenery.PASSAGEWAY_20855, new Location(3168, 4580, 0)), new Location(3168, 4586, 0)),
    FLOOR_1_PASSAGE_15(new PassageScenery(Scenery.PASSAGEWAY_20838, new Location(3165, 4577, 0)), new Location(3161, 4577, 0)),
    FLOOR_1_PASSAGE_16(new PassageScenery(Scenery.PASSAGEWAY_20837, new Location(3162, 4577, 0)), new Location(3166, 4577, 0)),
    FLOOR_1_PASSAGE_17(new PassageScenery(Scenery.PASSAGEWAY_20836, new Location(3150, 4583, 0)), new Location(3146, 4583, 1)),
    FLOOR_1_PASSAGE_18(new PassageScenery(Scenery.PASSAGEWAY_20856, new Location(3168, 4585, 0)), new Location(3168, 4579, 0)),
    FLOOR_1_PASSAGE_19(new PassageScenery(Scenery.PASSAGEWAY_20857, new Location(3165, 4589, 0)), new Location(3161, 4589, 1)),
    FLOOR_1_PASSAGE_20(new PassageScenery(Scenery.PASSAGEWAY_20864, new Location(3168, 4593, 0)), new Location(3168, 4597, 0)),
    FLOOR_1_PASSAGE_21(new PassageScenery(Scenery.PASSAGEWAY_20863, new Location(3168, 4596, 0)), new Location(3168, 4592, 0)),
    FLOOR_1_PASSAGE_22(new PassageScenery(Scenery.PASSAGEWAY_20862, new Location(3159, 4598, 0)), new Location(3149, 4598, 0)),
    FLOOR_1_PASSAGE_23(new PassageScenery(Scenery.PASSAGEWAY_20861, new Location(3150, 4598, 0)), new Location(3160, 4598, 0)),
    FLOOR_1_PASSAGE_24(new PassageScenery(Scenery.PASSAGEWAY_20860, new Location(3145, 4593, 0)), new Location(3145, 4589, 1)),
    FLOOR_1_PASSAGE_25(new PassageScenery(Scenery.PASSAGEWAY_20575, new Location(3190, 4598, 0)), new Location(3194, 4598, 1)),
    FLOOR_1_PASSAGE_26(new PassageScenery(Scenery.PASSAGEWAY_20871, new Location(3185, 4602, 0)), new Location(3149, 4644, 0)),
    FLOOR_1_PASSAGE_27(new PassageScenery(Scenery.PASSAGEWAY_20466, new Location(3149, 4643, 0)), new Location(3185, 4601, 0)),
    FLOOR_1_PASSAGE_28(new PassageScenery(Scenery.PASSAGEWAY_20572, new Location(3149, 4659, 0)), new Location(3149, 4664, 0)),
    FLOOR_1_PASSAGE_29(new PassageScenery(Scenery.PASSAGEWAY_20573, new Location(3149, 4663, 0)), new Location(3149, 4658, 0)),
    FLOOR_1_PASSAGE_30(new PassageScenery(Scenery.PASSAGEWAY_20573, new Location(3186, 4631, 0)), new Location(3186, 4626, 0)),

    FLOOR_2_PASSAGE_1(new PassageScenery(Scenery.PASSAGEWAY_20654, new Location(3141, 4551, 1)), new Location(3141, 4555, 2)),
    FLOOR_2_PASSAGE_2(new PassageScenery(Scenery.STAIRS_20619, new Location(3144, 4548, 1)), new Location(3148, 4548, 0)),
    FLOOR_2_PASSAGE_3(new PassageScenery(Scenery.STAIRS_20619, new Location(3158, 4556, 1)), new Location(3158, 4552, 0)),
    FLOOR_2_PASSAGE_4(new PassageScenery(Scenery.PASSAGEWAY_20654, new Location(3158, 4564, 1)), new Location(3158, 4568, 2)),
    FLOOR_2_PASSAGE_5(new PassageScenery(Scenery.PASSAGEWAY_20848, new Location(3175, 4561, 1)), new Location(3179, 4561, 0)),
    FLOOR_2_PASSAGE_6(new PassageScenery(Scenery.STAIRS_20619, new Location(3171, 4569, 1)), new Location(3167, 4569, 0)),
    FLOOR_2_PASSAGE_7(new PassageScenery(Scenery.STAIRS_20619, new Location(3184, 4556, 1)), new Location(3184, 4552, 0)),
    FLOOR_2_PASSAGE_8(new PassageScenery(Scenery.PASSAGEWAY_20841, new Location(3184, 4567, 1)), new Location(3184, 4571, 0)),
    FLOOR_2_PASSAGE_9(new PassageScenery(Scenery.PASSAGEWAY_20850, new Location(3195, 4574, 1)), new Location(3195, 4570, 0)),

    FLOOR_2_PASSAGE_10(new PassageScenery(Scenery.PASSAGEWAY_20851, new Location(3193, 4577, 1)), new Location(3189, 4577, 2)),
    FLOOR_2_PASSAGE_11(new PassageScenery(Scenery.PASSAGEWAY_20854, new Location(3186, 4584, 1)), new Location(3186, 4578, 2)),
    FLOOR_2_PASSAGE_12(new PassageScenery(Scenery.PASSAGEWAY_20844, new Location(3184, 4584, 1)), new Location(3184, 4579, 0)),
    FLOOR_2_PASSAGE_13(new PassageScenery(Scenery.PASSAGEWAY_20654, new Location(3177, 4577, 1)), new Location(3181, 4577, 2)),
    FLOOR_2_PASSAGE_14(new PassageScenery(Scenery.PASSAGEWAY_20845, new Location(3174, 4577, 1)), new Location(3170, 4577, 0)),
    FLOOR_2_PASSAGE_15(new PassageScenery(Scenery.PASSAGEWAY_20835, new Location(3147, 4583, 1)), new Location(3151, 4583, 0)),
    FLOOR_2_PASSAGE_16(new PassageScenery(Scenery.PASSAGEWAY_20834, new Location(3144, 4581, 1)), new Location(3144, 4577, 2)),
    FLOOR_2_PASSAGE_17(new PassageScenery(Scenery.PASSAGEWAY_20859, new Location(3145, 4590, 1)), new Location(3145, 4594, 0)),
    FLOOR_2_PASSAGE_18(new PassageScenery(Scenery.PASSAGEWAY_20858, new Location(3162, 4589, 1)), new Location(3166, 4589, 0)),
    FLOOR_2_PASSAGE_19(new PassageScenery(Scenery.PASSAGEWAY_20865, new Location(3154, 4597, 1)), new Location(3151, 4597, 1)),
    FLOOR_2_PASSAGE_20(new PassageScenery(Scenery.PASSAGEWAY_20866, new Location(3152, 4597, 1)), new Location(3155, 4597, 1)),
    FLOOR_2_PASSAGE_21(new PassageScenery(Scenery.PASSAGEWAY_20867, new Location(3176, 4598, 1)), new Location(3179, 4598, 1)),
    FLOOR_2_PASSAGE_22(new PassageScenery(Scenery.PASSAGEWAY_20868, new Location(3178, 4598, 1)), new Location(3175, 4598, 1)),
    FLOOR_2_PASSAGE_23(new PassageScenery(Scenery.STAIRS_20619, new Location(3193, 4598, 1)), new Location(3189, 4598, 0)),
    FLOOR_2_PASSAGE_24(new PassageScenery(Scenery.PASSAGEWAY_20830, new Location(3141, 4564, 1)), new Location(3141, 4560, 2)),
    FLOOR_2_PASSAGE_25(new PassageScenery(Scenery.PASSAGEWAY_20831, new Location(3144, 4567, 1)), new Location(3144, 4571, 2)),

    FLOOR_3_PASSAGE_1(new PassageScenery(Scenery.STAIRS_20684, new Location(3141, 4554, 2)), new Location(3141, 4550, 1)),
    FLOOR_3_PASSAGE_2(new PassageScenery(Scenery.PASSAGEWAY_20829, new Location(3141, 4561, 2)), new Location(3141, 4565, 1)),
    FLOOR_3_PASSAGE_3(new PassageScenery(Scenery.STAIRS_20684, new Location(3158, 4567, 2)), new Location(3158, 4563, 1)),
    FLOOR_3_PASSAGE_4(new PassageScenery(Scenery.PASSAGEWAY_20832, new Location(3144, 4570, 2)), new Location(3144, 4566, 1)),
    FLOOR_3_PASSAGE_5(new PassageScenery(Scenery.PASSAGEWAY_20833, new Location(3144, 4578, 2)), new Location(3144, 4582, 1)),
    FLOOR_3_PASSAGE_6(new PassageScenery(Scenery.PASSAGEWAY_20721, new Location(3180, 4577, 2)), new Location(3176, 4577, 1)),
    FLOOR_3_PASSAGE_7(new PassageScenery(Scenery.PASSAGEWAY_20853, new Location(3186, 4579, 2)), new Location(3186, 4585, 1)),
    FLOOR_3_PASSAGE_8(new PassageScenery(Scenery.PASSAGEWAY_20852, new Location(3190, 4577, 2)), new Location(3194, 4577, 1));

    private final PassageScenery passage;
    private final Location destination;

    Passage(PassageScenery passage, Location destination) {
        this.passage = passage;
        this.destination = destination;
    }

    public PassageScenery getPassage() {
        return passage;
    }

    public Location getDestination() {
        return destination;
    }

    public static HashMap<Location, Passage> getAllPassages() {
        HashMap<Location, Passage> map = new HashMap<>();
        for (Passage passage : Passage.values()) {
            map.put(passage.passage.getLocation(), passage);
        }
        return map;
    }
}
