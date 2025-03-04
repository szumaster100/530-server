package core.game.bots.impl

import core.game.bots.*
import core.game.interaction.DestinationFlag
import core.game.interaction.IntType
import core.game.interaction.InteractionListeners
import core.game.interaction.MovementPulse
import core.game.node.entity.skill.Skills
import core.game.node.item.Item
import core.game.world.map.Location
import core.tools.RandomFunction
import org.rs.consts.Items
import kotlin.random.Random

@PlayerCompatible
@ScriptName("Guild Sharks")
@ScriptDescription("Start in the fishing guild with a harpoon", "in your inventory.")
@ScriptIdentifier("guild_sharks")
class SharkCatcher : Script() {
    val pause = (1000..3000)
    val limit = 5000
    var myCounter = 0

    internal enum class Sets(
        val equipment: List<Item>,
    ) {
        SET_1(listOf(Item(10721), Item(8283), Item(10412), Item(10414), Item(88), Item(1007))),
        SET_2(listOf(Item(10721), Item(8283), Item(10412), Item(10414), Item(88), Item(1007))),
        SET_3(listOf(Item(10721), Item(8283), Item(10412), Item(10414), Item(88), Item(1019))),
        SET_4(listOf(Item(10721), Item(8283), Item(10412), Item(10414), Item(88), Item(1019))),
        SET_5(listOf(Item(10721), Item(8283), Item(10412), Item(10414), Item(88), Item(3765))),
        SET_6(listOf(Item(10721), Item(8283), Item(10412), Item(10414), Item(88), Item(3765))),
    }

    private var state = State.INIT
    private var tick = 0
    var fishCounter = 0
    var overlay: ScriptAPI.BottingOverlay? = null

    override fun tick() {
        val fishguild = Location.create(2596, 3410, 0)
        if (tick++ >= 500) {
            scriptAPI.teleport(fishguild)
            state = State.FIND_SPOT
            return
        }
        when (state) {
            State.INIT -> {
                overlay = scriptAPI.getOverlay()
                overlay!!.init()
                overlay!!.setTitle("Fishing")
                overlay!!.setTaskLabel("Sharks Caught:")
                overlay!!.setAmount(0)
                state = State.FIND_SPOT
            }

            State.BANKING -> {
                fishCounter += bot.inventory.getAmount(Items.RAW_SHARK_383)
                scriptAPI.bankItem(Items.RAW_SHARK_383)
                state = State.FIND_SPOT
            }

            State.STOP -> {
                val botAmount = bot.bank.getAmount(Items.RAW_SHARK_383) + 1
                var geAmount = 0
                val totalAmount = (geAmount + botAmount) + 1
                if ((totalAmount > limit) && myCounter++ == 300) {
                    bot.randomWalk(5, 5)
                    myCounter = 0
                    return
                } else if (myCounter++ == 300) {
                    myCounter = 0
                    State.TELE_FISH
                }
                return
            }

            State.IDLE -> {
                if (Random.nextBoolean()) {
                    state = State.FIND_SPOT
                } else if (myCounter++ >= RandomFunction.random(1, 25)) {
                    myCounter = 0
                    state = State.FIND_SPOT
                }
            }

            State.FISHING -> {
                bot.interfaceManager.close()
                if (Random.nextBoolean()) {
                    tick = 0
                    val spot = scriptAPI.getNearestNode(334, false)
                    spot?.let { InteractionListeners.run(spot.id, IntType.NPC, "harpoon", bot, spot) }
                    if (bot.inventory.isFull) {
                        state = State.FIND_BANK
                    } else {
                        state = State.IDLE
                    }
                } else {
                    state = State.IDLE
                }
                overlay!!.setAmount(fishCounter + bot.inventory.getAmount(Items.RAW_SHARK_383))
            }

            State.FIND_SPOT -> {
                val spot = scriptAPI.getNearestNode(334, false)
                if (spot != null) {
                    bot.walkingQueue.reset()
                    state = State.FISHING
                } else {
                    if (bot.location.x < 2591) {
                        scriptAPI.walkTo(Location.create(2604, 3421, 0))
                    } else {
                        scriptAPI.walkTo(Location.create(2608, 3414, 0))
                    }
                }
            }

            State.FIND_BANK -> {
                val bank = scriptAPI.getNearestGameObject(bot.location, 2213)

                class BankingPulse : MovementPulse(bot, bank, DestinationFlag.OBJECT) {
                    override fun pulse(): Boolean {
                        bot.faceLocation(bank!!.location)
                        state = State.BANKING
                        return true
                    }
                }
                if (bank != null) {
                    bot.pulseManager.run(BankingPulse())
                } else {
                    if (bot.location.x != 2596) {
                        scriptAPI.walkTo(Location.create(2596, 3415, 0))
                    } else if (bot.location.x < 2591) {
                        scriptAPI.walkTo(Location.create(2591, 3415, 0))
                    } else if (bot.location.x < 2587) {
                        scriptAPI.walkTo(Location.create(2587, 3420, 0))
                    }
                }
            }

            State.TELEPORT_GE -> {
                scriptAPI.teleportToGE()
                state = State.SELL_GE
            }

            State.SELL_GE -> {
                val botAmount = bot.bank.getAmount(Items.RAW_SHARK_383) + 1
                var geAmount = 0
                val totalAmount = (geAmount + botAmount) + 1
                if (totalAmount > limit) {
                    scriptAPI.walkTo(Location.create(3164, 3487, 0))
                    scriptAPI.sellOnGE(Items.RAW_SHARK_383)
                    state = State.STOP
                } else {
                    scriptAPI.walkTo(Location.create(3164, 3487, 0))
                    scriptAPI.sellOnGE(Items.RAW_SHARK_383)
                    state = State.TELE_FISH
                }
            }

            State.TELE_FISH -> {
                scriptAPI.teleport(fishguild)
                state = State.FIND_SPOT
            }
        }
    }

    init {
        val setUp = RandomFunction.random(Sets.values().size)
        equipment.addAll(Sets.values()[setUp].equipment)
        inventory.add(Item(311))
        skills[Skills.FISHING] = 90
    }

    enum class State {
        FISHING,
        BANKING,
        FIND_BANK,
        FIND_SPOT,
        TELEPORT_GE,
        SELL_GE,
        TELE_FISH,
        IDLE,
        STOP,
        INIT,
    }

    override fun newInstance(): Script {
        val script = SharkCatcher()
        script.bot = AIPlayer(bot.startLocation)
        script.state = State.FIND_SPOT
        return script
    }
}
