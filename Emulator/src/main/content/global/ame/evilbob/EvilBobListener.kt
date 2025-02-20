package content.global.ame.evilbob

import content.data.GameAttributes
import content.data.RandomEvent
import core.api.*
import core.game.dialogue.FaceAnim
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.QueueStrength
import core.game.node.entity.Entity
import core.game.world.GameWorld
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import core.game.world.map.zone.ZoneRestriction
import org.rs.consts.Animations
import org.rs.consts.Items
import org.rs.consts.NPCs
import org.rs.consts.Sounds

class EvilBobListener :
    InteractionListener,
    MapArea {
    override fun defineListeners() {
        on(EvilBobUtils.evilBob, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, EvilBobDialogue(), node.asNpc())
            return@on true
        }

        on(EvilBobUtils.servant, IntType.NPC, "talk-to") { player, node ->
            openDialogue(player, ServantDialogue(), node.asNpc())
            return@on true
        }

        on(EvilBobUtils.fishingSpot, IntType.SCENERY, "net") { player, _ ->
            if (getAttribute(player, GameAttributes.RE_BOB_DIAL_INDEX, false) ||
                getAttribute(player, GameAttributes.RE_BOB_COMPLETE, false)
            ) {
                sendDialogue(
                    player,
                    "You don't know if this is a good place to go fishing. Perhaps you should ask someone, like one of the human servants.",
                )
            } else if (!inInventory(player, Items.SMALL_FISHING_NET_303)) {
                sendNPCDialogue(
                    player,
                    NPCs.SERVANT_2481,
                    "You'll need a fishing net. There are plenty scattered around the beach.",
                    FaceAnim.SAD,
                )
            } else if (freeSlots(player) == 0) {
                sendDialogue(player, "You don't have enough space in your inventory.")
            } else if (getAttribute(player, GameAttributes.RE_BOB_SCORE, false)) {
                sendNPCDialogue(
                    player,
                    NPCs.SERVANT_2481,
                    "You've already got a fish. Come over here to uncook it, then serve it to Evil Bob.",
                    FaceAnim.SAD,
                )
            } else {
                lock(player, 6)
                animate(player, EvilBobUtils.fishAnim)
                sendMessage(player, "You cast out your net...")
                runTask(player, 6) {
                    when (getAttribute(player, GameAttributes.RE_BOB_ZONE, EvilBobUtils.northFishingZone.toString())) {
                        EvilBobUtils.northFishingZone.toString() -> {
                            if (inBorders(player, EvilBobUtils.northFishingZone)) {
                                addItem(player, Items.FISHLIKE_THING_6202)
                            } else {
                                addItem(player, Items.FISHLIKE_THING_6206)
                            }
                        }

                        EvilBobUtils.southFishingZone.toString() -> {
                            if (inBorders(player, EvilBobUtils.southFishingZone)) {
                                addItem(player, Items.FISHLIKE_THING_6202)
                            } else {
                                addItem(player, Items.FISHLIKE_THING_6206)
                            }
                        }

                        EvilBobUtils.eastFishingZone.toString() -> {
                            if (inBorders(player, EvilBobUtils.eastFishingZone)) {
                                addItem(player, Items.FISHLIKE_THING_6202)
                            } else {
                                addItem(player, Items.FISHLIKE_THING_6206)
                            }
                        }

                        EvilBobUtils.westFishingZone.toString() -> {
                            if (inBorders(player, EvilBobUtils.westFishingZone)) {
                                addItem(player, Items.FISHLIKE_THING_6202)
                            } else {
                                addItem(player, Items.FISHLIKE_THING_6206)
                            }
                        }
                    }
                    sendItemDialogue(
                        player,
                        Items.FISHLIKE_THING_6202,
                        "You catch a... what is this?? Is this a fish?? And it's cooked already??",
                    )
                    resetAnimator(player)
                    setAttribute(player, GameAttributes.RE_BOB_SCORE, true)
                }
            }
            return@on true
        }

        onUseWith(IntType.SCENERY, EvilBobUtils.fishlikeThings, EvilBobUtils.uncookingPot) { player, _, _ ->
            lock(player, 2)
            animate(player, EvilBobUtils.cookAnim)
            playAudio(player, Sounds.UNCOOKING_2322)
            if (removeItem(player, Items.FISHLIKE_THING_6202)) addItem(player, Items.RAW_FISHLIKE_THING_6200)
            if (removeItem(player, Items.FISHLIKE_THING_6206)) addItem(player, Items.RAW_FISHLIKE_THING_6204)
            return@onUseWith true
        }

        onUseWith(IntType.NPC, EvilBobUtils.fishlikeThings, EvilBobUtils.evilBob) { player, _, _ ->
            openDialogue(player, EvilBobDialogue(), NPCs.EVIL_BOB_2479)
            return@onUseWith true
        }

        onUseWith(IntType.NPC, EvilBobUtils.rawFishlikeThings, EvilBobUtils.evilBob) { player, _, _ ->
            openDialogue(player, EvilBobDialogue(), NPCs.EVIL_BOB_2479)
            return@onUseWith true
        }

        on(EvilBobUtils.fishlikeThings, IntType.ITEM, "Eat") { player, _ ->
            sendMessage(player, "It looks vile and smells even worse. You're not eating that!")
            return@on true
        }

        on(EvilBobUtils.exitPortal, IntType.SCENERY, "enter") { player, portal ->
            if (getAttribute(player, GameAttributes.RE_BOB_COMPLETE, false)) {
                lock(player, 12)
                queueScript(player, 0, QueueStrength.SOFT) { stage: Int ->
                    when (stage) {
                        0 -> {
                            forceMove(player, player.location, portal.location, 0, 50, null, 819)
                            return@queueScript delayScript(player, 3)
                        }

                        1 -> {
                            player.faceLocation(Location.create(3421, 4777, 0))
                            animate(player, Animations.RASPBERRY_2110)
                            sendChat(player, "Be seeing you!")
                            return@queueScript delayScript(player, 2)
                        }

                        2 -> {
                            animate(player, EvilBobUtils.teleAnim)
                            player.graphics(EvilBobUtils.telegfx)
                            playAudio(player, Sounds.TP_ALL_200)
                            return@queueScript delayScript(player, 2)
                        }

                        3 -> {
                            sendMessage(player, "Welcome back to ${GameWorld.settings!!.name}.")
                            teleport(player, getAttribute(player, RandomEvent.save(), Location.create(3222, 3219, 0)))
                            EvilBobUtils.reward(player)
                            EvilBobUtils.cleanup(player)
                            resetAnimator(player)
                            return@queueScript stopExecuting(player)
                        }

                        else -> return@queueScript stopExecuting(player)
                    }
                }
            } else {
                sendNPCDialogue(player, NPCs.EVIL_BOB_2479, "You're going nowhere, human!", FaceAnim.CHILD_NEUTRAL)
            }
            return@on true
        }
    }

    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(ZoneBorders(3400, 4762, 3443, 4793))
    }

    override fun getRestrictions(): Array<ZoneRestriction> {
        return arrayOf(ZoneRestriction.RANDOM_EVENTS, ZoneRestriction.CANNON, ZoneRestriction.FOLLOWERS)
    }

    override fun areaEnter(entity: Entity) {
        entity.locks.lockTeleport(100000)
    }
}
