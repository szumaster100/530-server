package content.region.asgarnia.quest.rd.handlers

import content.region.asgarnia.quest.rd.RDUtils
import content.region.asgarnia.quest.rd.RecruitmentDrive
import content.region.asgarnia.quest.rd.cutscene.FailTest
import core.api.*
import core.api.ui.closeDialogue
import core.game.dialogue.DialogueBuilder
import core.game.dialogue.DialogueBuilderFile
import core.game.dialogue.FaceAnim
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.node.entity.player.Player
import core.game.node.item.Item
import core.game.world.map.zone.ZoneBorders
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Scenery

class LogicTest : InteractionListener {
    companion object {
        const val foxFromVarbit = 680
        const val foxToVarbit = 681
        const val chickenFromVarbit = 682
        const val chickenToVarbit = 683
        const val grainFromVarbit = 684
        const val grainToVarbit = 685

        val fromZoneBorder = ZoneBorders(2479, 4967, 2490, 4977)
        val toZoneBorder = ZoneBorders(2471, 4967, 2478, 4977)

        fun countEquipmentItems(player: Player): Int {
            var count = 0
            if (inEquipment(player, Items.GRAIN_5607)) {
                count++
            }
            if (inEquipment(player, Items.FOX_5608)) {
                count++
            }
            if (inEquipment(player, Items.CHICKEN_5609)) {
                count++
            }
            return count
        }

        fun checkFinished(player: Player) {
            if (getVarbit(player, foxToVarbit) == 1 &&
                getVarbit(player, chickenToVarbit) == 1 &&
                getVarbit(
                    player,
                    grainToVarbit,
                ) == 1
            ) {
                sendMessage(player, "Congratulations! You have solved this room's puzzle!")
                setAttribute(player, RecruitmentDrive.stagePass, 1)
            }
        }

        fun checkFail(player: Player): Boolean {
            return (
                (
                    getVarbit(player, foxFromVarbit) == 0 &&
                        getVarbit(
                            player,
                            chickenFromVarbit,
                        ) == 0 &&
                        getVarbit(
                            player,
                            grainFromVarbit,
                        ) == 1
                ) ||
                    (
                        getVarbit(player, foxFromVarbit) == 1 &&
                            getVarbit(
                                player,
                                chickenFromVarbit,
                            ) == 0 &&
                            getVarbit(
                                player,
                                grainFromVarbit,
                            ) == 0
                    ) ||
                    (
                        getVarbit(player, foxToVarbit) == 1 &&
                            getVarbit(
                                player,
                                chickenToVarbit,
                            ) == 1 &&
                            getVarbit(
                                player,
                                grainToVarbit,
                            ) == 0
                    ) ||
                    (
                        getVarbit(player, foxToVarbit) == 0 &&
                            getVarbit(
                                player,
                                chickenToVarbit,
                            ) == 1 &&
                            getVarbit(
                                player,
                                grainToVarbit,
                            ) == 1
                    )
            )
        }

        fun resetStage(player: Player) {
            setVarbit(player, foxFromVarbit, 0)
            setVarbit(player, chickenFromVarbit, 0)
            setVarbit(player, grainFromVarbit, 0)
            setVarbit(player, foxToVarbit, 0)
            setVarbit(player, chickenToVarbit, 0)
            setVarbit(player, grainToVarbit, 0)
            removeItem(player, Items.GRAIN_5607, Container.EQUIPMENT)
            removeItem(player, Items.FOX_5608, Container.EQUIPMENT)
            removeItem(player, Items.CHICKEN_5609, Container.EQUIPMENT)
        }
    }

    override fun defineListeners() {
        listOf(Scenery.PRECARIOUS_BRIDGE_7286, Scenery.PRECARIOUS_BRIDGE_7287).forEach { bridge ->
            on(bridge, IntType.SCENERY, "cross") { player, _ ->
                when {
                    countEquipmentItems(player) > 1 -> {
                        sendDialogue(
                            player,
                            "I really don't think I should be carrying more than 5Kg across that rickety bridge...",
                        )
                    }

                    checkFail(player) -> {
                        closeDialogue(player).also {
                            openDialogue(player, PatienceTest(2), NPCs.SIR_SPISHYUS_2282)
                        }
                    }

                    else -> {
                        lock(player, 5)
                        sendMessage(player, "You carefully walk across the rickety bridge...")
                        val path =
                            if (bridge ==
                                Scenery.PRECARIOUS_BRIDGE_7286
                            ) {
                                listOf(2476, 4972)
                            } else {
                                listOf(2484, 4972)
                            }
                        player.walkingQueue.reset()
                        player.walkingQueue.addPath(path[0], path[1])
                    }
                }
                return@on true
            }
        }

        on(Scenery.GRAIN_7284, SCENERY, "pick-up") { player, _ ->
            if (getAttribute(player, RecruitmentDrive.stageFail, 0) == 0) {
                if (fromZoneBorder.insideBorder(player)) {
                    replaceSlot(player, EquipmentSlot.CAPE.ordinal, Item(Items.GRAIN_5607), null, Container.EQUIPMENT)
                    setVarbit(player, grainFromVarbit, 1)
                }
                if (toZoneBorder.insideBorder(player)) {
                    replaceSlot(player, EquipmentSlot.CAPE.ordinal, Item(Items.GRAIN_5607), null, Container.EQUIPMENT)
                    setVarbit(player, grainToVarbit, 0)
                }
            }
            return@on true
        }
        onUnequip(Items.GRAIN_5607) { player, _ ->
            if (fromZoneBorder.insideBorder(player)) {
                removeItem(player, Items.GRAIN_5607, Container.EQUIPMENT)
                setVarbit(player, grainFromVarbit, 0)
            }
            if (toZoneBorder.insideBorder(player)) {
                removeItem(player, Items.GRAIN_5607, Container.EQUIPMENT)
                setVarbit(player, grainToVarbit, 1)
                checkFinished(player)
            }
            return@onUnequip true
        }

        on(Scenery.FOX_7277, SCENERY, "pick-up") { player, _ ->
            if (getAttribute(player, RecruitmentDrive.stageFail, 0) == 0) {
                if (fromZoneBorder.insideBorder(player)) {
                    replaceSlot(player, EquipmentSlot.WEAPON.ordinal, Item(Items.FOX_5608), null, Container.EQUIPMENT)
                    setVarbit(player, foxFromVarbit, 1)
                }
                if (toZoneBorder.insideBorder(player)) {
                    replaceSlot(player, EquipmentSlot.WEAPON.ordinal, Item(Items.FOX_5608), null, Container.EQUIPMENT)
                    setVarbit(player, foxToVarbit, 0)
                }
            }
            return@on true
        }
        onUnequip(Items.FOX_5608) { player, _ ->
            if (fromZoneBorder.insideBorder(player)) {
                removeItem(player, Items.FOX_5608, Container.EQUIPMENT)
                setVarbit(player, foxFromVarbit, 0)
            }
            if (toZoneBorder.insideBorder(player)) {
                removeItem(player, Items.FOX_5608, Container.EQUIPMENT)
                setVarbit(player, foxToVarbit, 1)
                checkFinished(player)
            }
            return@onUnequip true
        }

        on(Scenery.CHICKEN_7281, SCENERY, "pick-up") { player, _ ->
            if (getAttribute(player, RecruitmentDrive.stageFail, 0) == 0) {
                if (fromZoneBorder.insideBorder(player)) {
                    replaceSlot(
                        player,
                        EquipmentSlot.SHIELD.ordinal,
                        Item(Items.CHICKEN_5609),
                        null,
                        Container.EQUIPMENT,
                    )
                    setVarbit(player, chickenFromVarbit, 1)
                }
                if (toZoneBorder.insideBorder(player)) {
                    replaceSlot(
                        player,
                        EquipmentSlot.SHIELD.ordinal,
                        Item(Items.CHICKEN_5609),
                        null,
                        Container.EQUIPMENT,
                    )
                    setVarbit(player, chickenToVarbit, 0)
                }
            }
            return@on true
        }
        onUnequip(Items.CHICKEN_5609) { player, _ ->
            if (fromZoneBorder.insideBorder(player)) {
                removeItem(player, Items.CHICKEN_5609, Container.EQUIPMENT)
                setVarbit(player, chickenFromVarbit, 0)
            }
            if (toZoneBorder.insideBorder(player)) {
                removeItem(player, Items.CHICKEN_5609, Container.EQUIPMENT)
                setVarbit(player, chickenToVarbit, 1)
                checkFinished(player)
            }
            return@onUnequip true
        }
    }
}

class SirSpishyusDialogueFile(
    private val dialogueNum: Int = 0,
) : DialogueBuilderFile() {
    override fun create(b: DialogueBuilder) {
        b
            .onPredicate { player -> getAttribute(player, RecruitmentDrive.stagePass, false) }
            .npc(
                FaceAnim.HAPPY,
                "Excellent work, @name.",
                "Please step through the portal to meet your next",
                "challenge.",
            ).end()

        b
            .onPredicate { player -> dialogueNum == 2 || getAttribute(player, RecruitmentDrive.stageFail, false) }
            .betweenStage { _, player, _, _ ->
                setAttribute(player, RecruitmentDrive.stageFail, true)
            }.npc(
                FaceAnim.SAD,
                "No... I am very sorry.",
                "Apparently you are not up to the challenge.",
                "I will return you where you came from, better luck in the",
                "future.",
            ).endWith { _, player ->
                removeAttribute(player, PatienceTest.patience)
                setAttribute(player, RecruitmentDrive.stagePass, false)
                setAttribute(player, RecruitmentDrive.stageFail, false)
                runTask(player, 3) {
                    FailTest(player).start()
                    return@runTask
                }
            }
        b
            .onPredicate { _ -> true }
            .betweenStage { _, player, _, _ ->
                setVarbit(player, RDUtils.VARBIT_FOX_EAST, 0)
                setVarbit(player, RDUtils.VARBIT_FOX_WEST, 0)
                setVarbit(player, RDUtils.VARBIT_CHICKEN_EAST, 0)
                setVarbit(player, RDUtils.VARBIT_CHICKEN_WEST, 0)
                setVarbit(player, RDUtils.VARBIT_GRAIN_EAST, 0)
                setVarbit(player, RDUtils.VARBIT_GRAIN_WEST, 0)
            }.npcl(FaceAnim.FRIENDLY, "Ah, welcome @name.")
            .playerl(FaceAnim.FRIENDLY, "Hello there." + " What am I supposed to be doing in this room?")
            .npcl(
                FaceAnim.FRIENDLY,
                "Well, your task is to take this fox, this chicken and this bag of grain across that bridge there to the other side of the room.",
            ).npcl(FaceAnim.FRIENDLY, "When you have done that, your task is complete.")
            .playerl(FaceAnim.FRIENDLY, "Is that it?")
            .npcl(FaceAnim.FRIENDLY, "Well, it is not quite as simple as that may sound.")
            .npcl(
                FaceAnim.FRIENDLY,
                "Firstly, you may only carry one of the objects across the room at a time, for the bridge is old and fragile.",
            ).npcl(
                FaceAnim.FRIENDLY,
                "Secondly, the fox wants to eat the chicken, and the chicken wants to eat the grain. Should you ever leave the fox unattended with the chicken, or the grain unattended with the chicken, then",
            ).npcl(FaceAnim.FRIENDLY, "one of them will be eaten, and you will be unable to complete the test.")
            .playerl(FaceAnim.FRIENDLY, "Okay, I'll see what I can do.")
    }
}
