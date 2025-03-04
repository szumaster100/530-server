package core.game.bots.impl

import core.game.bots.Script
import core.game.bots.SkillingBotAssembler
import core.game.interaction.DestinationFlag
import core.game.interaction.IntType
import core.game.interaction.InteractionListeners
import core.game.interaction.MovementPulse
import core.game.node.item.Item
import core.game.world.map.Location
import core.game.world.map.zone.ZoneBorders
import org.rs.consts.Items

class DraynorFisher : Script() {
    val fishingZone = ZoneBorders(3085, 3223, 3089, 3233)
    val bankZone = ZoneBorders(3092, 3240, 3094, 3246)

    var state = State.FISHING

    override fun tick() {
        when (state) {
            State.FISHING -> {
                if (!fishingZone.insideBorder(bot)) {
                    scriptAPI.walkTo(fishingZone.randomLoc)
                } else {
                    val spot = scriptAPI.getNearestNode(316, false)
                    if (spot != null) {
                        InteractionListeners.run(spot.id, IntType.NPC, "net", bot, spot)
                    } else {
                        scriptAPI.walkTo(fishingZone.randomLoc)
                    }
                    if (bot.inventory.getMaximumAdd(Item(4151)) < 5) {
                        state = State.BANKING
                    }
                }
            }

            State.BANKING -> {
                if (!bankZone.insideBorder(bot)) {
                    scriptAPI.walkTo(bankZone.randomLoc)
                } else {
                    val bank = scriptAPI.getNearestNode("Bank booth")
                    if (bank != null) {
                        bot.pulseManager.run(
                            object : MovementPulse(bot, bank, DestinationFlag.OBJECT) {
                                override fun pulse(): Boolean {
                                    bot.inventory.clear()
                                    state = State.FISHING
                                    bot.inventory.add(Item(Items.SMALL_FISHING_NET_303))
                                    return true
                                }
                            },
                        )
                    }
                }
            }
        }
    }

    override fun newInstance(): Script {
        val script = DraynorFisher()
        script.bot = SkillingBotAssembler().produce(SkillingBotAssembler.Wealth.POOR, Location.create(3094, 3242, 0))
        return script
    }

    init {
        inventory.add(Item(Items.SMALL_FISHING_NET_303))
    }

    enum class State {
        FISHING,
        BANKING,
    }
}
