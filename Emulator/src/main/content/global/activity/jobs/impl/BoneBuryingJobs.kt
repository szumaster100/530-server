package content.global.activity.jobs.impl

import content.global.activity.jobs.Job
import content.global.activity.jobs.JobType
import core.game.world.map.zone.ZoneBorders
import org.rs.consts.Items

enum class BoneBuryingJobs(
    val buryArea: BuryArea,
    val boneIds: List<Int>,
    override val employer: Employers,
) : Job {
    BURY_DRAYNOR(BuryArea.DRAYNOR_WHEAT_FIELD, Bones.BONES, Employers.PRAYER_TUTOR),
    BURY_LUMBRIDGE_GRAVEYARD(BuryArea.LUMBRIDGE_GRAVEYARD, Bones.BONES, Employers.PRAYER_TUTOR),
    ;

    override val type = JobType.BONE_BURYING
    override val lower = 22
    override val upper = 26

    enum class BuryArea(
        val zoneBorder: ZoneBorders,
        val title: String,
    ) {
        DRAYNOR_WHEAT_FIELD(ZoneBorders(3107, 3265, 3132, 3293), "wheat field east of Draynor Village"),
        LUMBRIDGE_GRAVEYARD(ZoneBorders(3238, 3191, 3252, 3204), "Lumbridge Graveyard"),
    }

    object Bones {
        val BONES = listOf(Items.BONES_526, Items.BONES_2530)
    }
}
