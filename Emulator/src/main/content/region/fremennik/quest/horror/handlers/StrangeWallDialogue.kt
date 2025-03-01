package content.region.fremennik.quest.horror.handlers

import core.api.*
import core.game.dialogue.DialogueFile
import core.game.node.item.Item
import org.rs.consts.Items

class StrangeWallDialogue(
    private var items: Int,
) : DialogueFile() {
    override fun handle(
        componentID: Int,
        buttonID: Int,
    ) {
        when (stage) {
            0 -> {
                sendDialogue(player!!, "I don't think I'll get that back if I put it in there.")
                stage++
            }

            1 -> {
                setTitle(player!!, 2)
                sendDialogueOptions(player!!, "Really place the rune into the door?", "Yes", "No")
                stage++
            }

            2 ->
                when (buttonID) {
                    1 ->
                        when (items) {
                            882 -> {
                                end()
                                if (!removeItem(player!!, Item(Items.BRONZE_ARROW_882, 1), Container.INVENTORY)) {
                                    sendMessage(player!!, "Nothing interesting happens.")
                                } else {
                                    sendMessage(player!!, "you place an arrow into the slot in the wall.")
                                    setAttribute(player!!, HorrorFromTheDeepUtils.USE_ARROW, 1)
                                    player!!.incrementAttribute(HorrorFromTheDeepUtils.UNLOCK_DOOR)
                                }
                            }

                            1277 -> {
                                end()
                                if (!removeItem(player!!, Item(Items.BRONZE_SWORD_1277, 1), Container.INVENTORY)) {
                                    sendMessage(player!!, "Nothing interesting happens.")
                                } else {
                                    sendMessage(player!!, "you place an a sword into the slot in the wall.")
                                    setAttribute(player!!, HorrorFromTheDeepUtils.USE_SWORD, 1)
                                    player!!.incrementAttribute(HorrorFromTheDeepUtils.UNLOCK_DOOR)
                                }
                            }

                            556 -> {
                                end()
                                if (!removeItem(player!!, Item(Items.AIR_RUNE_556, 1), Container.INVENTORY)) {
                                    sendMessage(player!!, "Nothing interesting happens.")
                                } else {
                                    sendMessage(player!!, "you place an air rune into the slot in the wall.")
                                    setAttribute(player!!, HorrorFromTheDeepUtils.USE_AIR_RUNE, 1)
                                    player!!.incrementAttribute(HorrorFromTheDeepUtils.UNLOCK_DOOR)
                                }
                            }

                            554 -> {
                                end()
                                if (!removeItem(player!!, Item(Items.FIRE_RUNE_554, 1), Container.INVENTORY)) {
                                    sendMessage(player!!, "Nothing interesting happens.")
                                } else {
                                    sendMessage(player!!, "you place a fire rune into the slot in the wall.")
                                    setAttribute(player!!, HorrorFromTheDeepUtils.USE_FIRE_RUNE, 1)
                                    player!!.incrementAttribute(HorrorFromTheDeepUtils.UNLOCK_DOOR)
                                }
                            }

                            557 -> {
                                end()
                                if (!removeItem(player!!, Item(Items.EARTH_RUNE_557, 1), Container.INVENTORY)) {
                                    sendMessage(player!!, "Nothing interesting happens.")
                                } else {
                                    sendMessage(player!!, "you place an earth rune into the slot in the wall.")
                                    setAttribute(player!!, HorrorFromTheDeepUtils.USE_EARTH_RUNE, 1)
                                    player!!.incrementAttribute(HorrorFromTheDeepUtils.UNLOCK_DOOR)
                                }
                            }

                            555 -> {
                                end()
                                if (!removeItem(player!!, Item(Items.WATER_RUNE_555, 1), Container.INVENTORY)) {
                                    sendMessage(player!!, "Nothing interesting happens.")
                                } else {
                                    sendMessage(player!!, "you place a water rune into the slot in the wall.")
                                    setAttribute(player!!, HorrorFromTheDeepUtils.USE_WATER_RUNE, 1)
                                    player!!.incrementAttribute(HorrorFromTheDeepUtils.UNLOCK_DOOR)
                                }
                            }
                        }

                    2 -> {
                        end()
                    }
                }
        }
    }
}
