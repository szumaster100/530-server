package core.game.system.command.sets

import content.global.skill.slayer.SlayerManager
import content.global.skill.slayer.SlayerMaster
import content.global.skill.slayer.SlayerUtils
import content.global.skill.slayer.Tasks
import core.api.setVarp
import core.game.node.entity.npc.NPC
import core.game.system.command.Privilege
import core.plugin.Initializable

@Initializable
class SlayerCommandSet : CommandSet(Privilege.ADMIN) {
    override fun defineCommands() {
        define(name = "finishtask", Privilege.ADMIN) { player, _ ->
            notify(player, "Kill the npc that spawned to finish your task.")
            SlayerManager.getInstance(player).amount = 1
            val finisher =
                NPC(
                    SlayerManager
                        .getInstance(player)
                        .task
                        ?.npcs
                        ?.get(0) ?: 0,
                    player.location,
                )
            finisher.isRespawn = false
            finisher.init()
        }

        define(name = "setslayerpoints", Privilege.ADMIN) { player, args ->
            if (args.size < 2) {
                reject(player, "Usage: ::setslayerpoints amount")
            }

            val amount = args[1].toIntOrNull()
            if (amount == null) {
                reject(player, "Amount needs to be a valid integer!")
            }

            SlayerManager.getInstance(player).slayerPoints = amount!!
            notify(player, "Set slayer points to $amount.")
        }

        define(
            name = "setslayertask",
            privilege = Privilege.ADMIN,
            usage = "::setslayertask <lt>npc id<gt> [amount]",
            description = "Set the slayer task to npc. Amount optional.",
        ) { player, args ->
            if (args.size < 2) reject(player, "Usage: ::setslayertask <lt>npc id<gt> [amount]")

            val npc = (args[1].toIntOrNull() ?: reject(player, "Must enter valid npc id")) as Int
            val task = (Tasks.forId(npc) ?: reject(player, "Must enter valid npc id")) as Tasks
            val amount =
                args.getOrNull(2)?.toIntOrNull()?.let {
                    if (it !in
                        1..255
                    ) {
                        reject(player, "Amount must be an integer: 1-255.")
                    } else {
                        it
                    }
                } as Int?
            val slayer = SlayerManager.getInstance(player)
            if (slayer.hasTask()) {
                slayer.task = task
            } else {
                SlayerUtils.assign(
                    player,
                    task,
                    SlayerMaster.values().random(),
                )
            }
            if (amount != null) slayer.amount = amount
            setVarp(player, 2502, slayer.flags.taskFlags shr 4)
        }
    }
}
