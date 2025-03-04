package content.minigame.vinesweeper

import content.minigame.vinesweeper.FlagsHandler.Companion.FARMER_FLAG_LINES
import content.minigame.vinesweeper.VinesweeperListener.Companion.FARMERS
import content.minigame.vinesweeper.VinesweeperListener.Companion.FARMER_CLEAR_RADIUS
import content.minigame.vinesweeper.VinesweeperListener.Companion.HOLES
import content.minigame.vinesweeper.VinesweeperListener.Companion.NUMBERS
import content.minigame.vinesweeper.VinesweeperListener.Companion.RABBITS
import content.minigame.vinesweeper.VinesweeperListener.Companion.SEED_LOCS
import content.minigame.vinesweeper.VinesweeperListener.Companion.populateSeeds
import content.minigame.vinesweeper.VinesweeperListener.Companion.scheduleNPCs
import content.minigame.vinesweeper.VinesweeperListener.Companion.sendPoints
import content.minigame.vinesweeper.dialogue.BlinkinDialogueFile
import content.minigame.vinesweeper.dialogue.MrsWinkinDialogueFile
import core.api.*
import core.api.MapArea
import core.game.component.Component
import core.game.dialogue.FaceAnim
import core.game.interaction.IntType
import core.game.interaction.InteractionListener
import core.game.interaction.InterfaceListener
import core.game.interaction.MovementPulse
import core.game.node.entity.Entity
import core.game.node.entity.combat.DeathTask
import core.game.node.entity.npc.AbstractNPC
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.GroundItemManager
import core.game.node.item.Item
import core.game.node.scenery.Scenery
import core.game.node.scenery.SceneryBuilder
import core.game.system.task.Pulse
import core.game.world.GameWorld
import core.game.world.GameWorld.ticks
import core.game.world.map.Location
import core.game.world.map.RegionManager
import core.game.world.map.zone.ZoneBorders
import core.game.world.update.flag.context.Animation
import core.game.world.update.flag.context.Graphics
import core.plugin.Initializable
import core.tools.RandomFunction
import org.rs.consts.*
import kotlin.math.min

class VinesweeperListener :
    InteractionListener,
    InterfaceListener,
    MapArea {
    override fun defineAreaBorders(): Array<ZoneBorders> {
        return arrayOf(getRegionBorders(6473))
    }

    override fun areaEnter(entity: Entity) {
        if (entity is Player) {
            openOverlay(entity, Components.RABBIT_OVERLAY_689)
            sendUpdatedPoints(entity)
        }
    }

    override fun areaLeave(
        entity: Entity,
        logout: Boolean,
    ) {
        if (entity is Player) {
            entity.interfaceManager.closeOverlay()
            if (!logout) {
                sendMessage(entity, "Winkin's Farm thanks you for your visit.")
                sendMessage(entity, "Leftover ogleroots and flags have been returned to the establishment.")
                sendMessage(
                    entity,
                    "You have been reimbursed at a rate of 10gp per ogleroot and the flags have been collected.",
                )
                val flags = entity.inventory.getAmount(Item(Items.FLAG_12625))
                if (flags > 0) {
                    entity.setAttribute("/save:vinesweeper:stored-flags", flags)
                    entity.inventory.remove(Item(Items.FLAG_12625, flags))
                }
                val roots = entity.inventory.getAmount(Item(Items.OGLEROOT_12624))
                if (roots > 0) {
                    entity.inventory.remove(Item(Items.OGLEROOT_12624, roots))
                    entity.inventory.add(Item(Items.COINS_995, roots * 10))
                }
            }
        }
    }

    override fun defineListeners() {
        populateSeeds()
        on(org.rs.consts.Scenery.PORTAL_29534, IntType.SCENERY, "enter") { player, _ ->
            val x = player.getAttribute("vinesweeper:return-tele:x", 3052)
            val y = player.getAttribute("vinesweeper:return-tele:y", 3304)
            teleport(player, Location(x, y))
            return@on true
        }
        on(SIGNS, IntType.SCENERY, "read") { player, node ->
            player.interfaceManager.open(Component(INSTRUCTION_SIGNS[node.id]!!))
            return@on true
        }
        on(HOLES, IntType.SCENERY, "dig") { player, node ->
            if (!inInventory(player, Items.SPADE_952, 1)) {
                sendMessage(player, "You need a spade to dig here.")
            } else {
                player.visualize(
                    Animation(Animations.HUMAN_VINESWEEPER_DIG_8709),
                    Graphics(org.rs.consts.Graphics.VINESWEEPER_DIRT_DIG_1543),
                )
                dig(player, node.location)
            }
            return@on true
        }
        onUseWith(IntType.SCENERY, Items.SPADE_952, *HOLES) { player, _, with ->
            player.visualize(
                Animation(Animations.HUMAN_VINESWEEPER_DIG_8709),
                Graphics(org.rs.consts.Graphics.VINESWEEPER_DIRT_DIG_1543),
            )
            dig(player, with.location)
            return@onUseWith true
        }
        on(HOLES, IntType.SCENERY, "flag") { player, node ->
            val hole = node as Scenery
            var count = 0
            if (player.location != node.location) {
                plantFlag(player, hole)
                return@on true
            }
            GameWorld.Pulser.submit(
                object : MovementPulse(player, hole, hole.destinationFlag) {
                    override fun pulse(): Boolean {
                        when (count++) {
                            0 -> {
                                player.faceLocation(hole.location)
                                return false
                            }

                            1 -> {
                                plantFlag(player, hole)
                                return true
                            }
                        }
                        return true
                    }
                },
            )
            return@on true
        }
        onUseWith(IntType.SCENERY, Items.FLAG_12625, *HOLES) { player, _, with ->
            val hole = with as Scenery
            plantFlag(player, hole)
            return@onUseWith true
        }
        on(HOLES, IntType.SCENERY, "inspect") { player, node ->
            player.animate(Animation(Animations.HUMAN_INSPECT_HOLE_8710))
            player.lock(5)
            GameWorld.Pulser.submit(
                object : Pulse(5) {
                    override fun pulse(): Boolean {
                        val msg =
                            when (RandomFunction.random(0, 7)) {
                                0 -> "You don't see anything interesting. You can't be sure if there's a seed there or not."
                                1 -> "You get some mud in your eye and it stings! You have no idea what is in the hole."
                                2 -> "The mud seems to be too thick to see what is there."
                                3 -> "A slimy worm wriggles out of the mud, making you jump and lose concentration. You're not sure if there is a seed here or not."
                                else ->
                                    if (isSeed(node.location)) {
                                        "You notice a seed hidden in the dirt."
                                    } else {
                                        "You are certain there is no seed planted here."
                                    }
                            }
                        sendDialogue(player, msg)
                        return true
                    }
                },
            )
            return@on true
        }
        on(NPCs.MRS_WINKIN_7132, IntType.NPC, "talk-to") { player, npc ->
            openDialogue(player, MrsWinkinDialogueFile(), npc)
            return@on true
        }
        on(NPCs.MRS_WINKIN_7132, IntType.NPC, "trade") { player, _ ->
            player.interfaceManager.open(Component(Components.RABBIT_SHOP_686))
            return@on true
        }
        on(NPCs.MRS_WINKIN_7132, IntType.NPC, "buy flags") { player, npc ->
            val dialogue = MrsWinkinDialogueFile()
            dialogue.stage = 20
            openDialogue(player, dialogue, npc)
            return@on true
        }
        on(RABBITS, IntType.NPC, "feed") { player, node ->
            val rabbit = node as NPC
            feedRabbit(player, rabbit)
            return@on true
        }
        onUseWith(IntType.NPC, Items.OGLEROOT_12624, *RABBITS) { player, _, with ->
            val rabbit = with as NPC
            feedRabbit(player, rabbit)
            return@onUseWith true
        }
        on(NPCs.FARMER_BLINKIN_7131, IntType.NPC, "talk-to") { player, npc ->

            openDialogue(player, BlinkinDialogueFile(), npc)
            return@on true
        }
        on(NPCs.FARMER_BLINKIN_7131, IntType.NPC, "buy-flags") { player, npc ->
            val dialogue = BlinkinDialogueFile()
            dialogue.stage = 21
            openDialogue(player, dialogue, npc)
            return@on true
        }
        on(NPCs.FARMER_BLINKIN_7131, IntType.NPC, "buy-roots") { player, npc ->
            val dialogue = BlinkinDialogueFile()
            dialogue.stage = 40
            openDialogue(player, dialogue, npc)
            return@on true
        }
    }

    override fun defineInterfaceListeners() {
        on(IFACE) { player, _, opcode, buttonID, _, _ ->
            when (opcode) {
                Opcode.VALUE.value -> {
                    when (buttonID) {
                        TRADE_FOR_XP_BUTTON -> {
                            player.packetDispatch.sendInterfaceConfig(686, 60, false)
                        }

                        XP_CONFIRM -> {
                            player.packetDispatch.sendInterfaceConfig(686, 60, true)
                            val level = player.skills.getStaticLevel(Skills.FARMING)

                            val points_per_xp =
                                if (level < 40) {
                                    2.0 * (40.0 - level.toDouble()) / 10.0
                                } else {
                                    1.0
                                }
                            val points = player.getAttribute("vinesweeper:points", 0)
                            val xp = points / points_per_xp
                            player.skills.addExperience(Skills.FARMING, xp)
                            setAttribute(player, "/save:vinesweeper:points", 0)
                            sendUpdatedPoints(player)
                        }

                        XP_DENY -> {
                            player.packetDispatch.sendInterfaceConfig(686, 60, true)
                        }

                        else -> {
                            val reward = REWARDS[buttonID] ?: return@on true
                            sendMessage(player, "${Item(reward.itemID).name}: ${reward.points} vinesweeper points.")
                        }
                    }
                }

                Opcode.BUY1.value -> {
                    buy(player, buttonID, 1)
                    return@on true
                }

                Opcode.BUY5.value -> {
                    buy(player, buttonID, 5)
                    return@on true
                }

                Opcode.BUY10.value -> {
                    buy(player, buttonID, 10)
                    return@on true
                }

                Opcode.BUYX.value -> {
                    setAttribute(player, "runscript") { amount: Int ->
                        buy(player, buttonID, amount)
                    }
                    player.dialogueInterpreter.sendInput(false, "Enter the amount:")
                    return@on true
                }

                else -> {}
            }
            return@on true
        }
    }

    data class SeedDestination(
        val player: Player,
        val loc: Location,
        val alive: Boolean,
    ) {
        override fun equals(other: Any?): Boolean {
            return if (other is SeedDestination) {
                loc == other.loc
            } else {
                false
            }
        }

        override fun hashCode(): Int {
            return loc.hashCode()
        }
    }

    companion object {
        val AVACH_NIMPORTO_LOC = Location.create(1637, 4709)

        val SIGNS =
            intArrayOf(
                org.rs.consts.Scenery.INSTRUCTION_SIGN_29461,
                org.rs.consts.Scenery.INSTRUCTION_SIGN_29462,
                org.rs.consts.Scenery.INSTRUCTION_SIGN_29463,
                org.rs.consts.Scenery.INSTRUCTION_SIGN_29464,
            )

        val HOLES =
            intArrayOf(
                org.rs.consts.Scenery.HOLE_29476,
                org.rs.consts.Scenery.HOLE_29477,
                org.rs.consts.Scenery.HOLE_29478,
            )

        val NUMBERS = intArrayOf(29447, 29448, 29449, 29450, 29451, 29452, 29453, 29454, 29455)

        val IFACE = 686
        val TRADE_FOR_XP_BUTTON = 53
        val XP_CONFIRM = 72
        val XP_DENY = 73

        enum class Opcode(
            val value: Int,
        ) {
            VALUE(155),
            BUY1(196),
            BUY5(124),
            BUY10(199),
            BUYX(234),
        }

        data class Reward(
            val itemID: Int,
            val points: Int,
        )

        val REWARDS =
            hashMapOf(
                18 to Reward(Items.TOMATO_SEED_5322, 10),
                19 to Reward(Items.SWEETCORN_SEED_5320, 150),
                20 to Reward(Items.STRAWBERRY_SEED_5323, 165),
                21 to Reward(Items.WATERMELON_SEED_5321, 680),
                22 to Reward(Items.GUAM_SEED_5291, 10),
                23 to Reward(Items.MARRENTILL_SEED_5292, 10),
                24 to Reward(Items.RANARR_SEED_5295, 4000),
                25 to Reward(Items.KWUARM_SEED_5299, 1000),
                26 to Reward(Items.TARROMIN_SEED_5293, 10),
                27 to Reward(Items.NASTURTIUM_SEED_5098, 10),
                28 to Reward(Items.WOAD_SEED_5099, 30),
                29 to Reward(Items.LIMPWURT_SEED_5100, 70),
                30 to Reward(Items.ASGARNIAN_SEED_5308, 5),
                31 to Reward(Items.KRANDORIAN_SEED_5310, 20),
                32 to Reward(Items.REDBERRY_SEED_5101, 5),
                33 to Reward(Items.CADAVABERRY_SEED_5102, 5),
                34 to Reward(Items.DWELLBERRY_SEED_5103, 5),
                35 to Reward(Items.JANGERBERRY_SEED_5104, 10),
                36 to Reward(Items.WHITEBERRY_SEED_5105, 25),
                37 to Reward(Items.POISON_IVY_SEED_5106, 30),
                38 to Reward(Items.ACORN_5312, 100),
                39 to Reward(Items.WILLOW_SEED_5313, 1800),
                40 to Reward(Items.MAPLE_SEED_5314, 12000),
                41 to Reward(Items.PINEAPPLE_SEED_5287, 10000),
                42 to Reward(Items.YEW_SEED_5315, 29000),
                43 to Reward(Items.PALM_TREE_SEED_5289, 35000),
                44 to Reward(Items.SPIRIT_SEED_5317, 55000),
                45 to Reward(Items.COMPOST_POTION4_6470, 5000),
                46 to Reward(Items.FLAG_12625, 50),
            )

        fun buy(
            player: Player,
            buttonID: Int,
            amount: Int,
        ) {
            val reward = REWARDS[buttonID] ?: return
            val cost = amount * reward.points
            val points = player.getAttribute("vinesweeper:points", 0)
            if (cost in 1 until points) {
                val item = Item(reward.itemID, amount)
                if (!player.inventory.add(item)) {
                    GroundItemManager.create(item, player)
                }
                player.incrementAttribute("/save:vinesweeper:points", -cost)
                sendUpdatedPoints(player)
            } else {
                sendMessage(player, "You don't have enough points for that.")
            }
        }

        val TUTORIAL = 685

        val INSTRUCTION_SIGNS =
            hashMapOf(
                29463 to 684,
                29464 to 687,
                29462 to 688,
                29461 to 690,
            )

        val RABBITS = intArrayOf(NPCs.RABBIT_7125, NPCs.RABBIT_7126, NPCs.RABBIT_7127)

        val FARMERS = intArrayOf(NPCs.FARMER_7128, NPCs.FARMER_7129, NPCs.FARMER_7130)

        val MAX_SEEDS = 300

        val FARMER_CLEAR_RADIUS = 3

        val VINESWEEPER_BORDERS = ZoneBorders(1600, 4672, 1663, 4735)

        fun sendUpdatedPoints(player: Player) {
            val points = player.getAttribute("vinesweeper:points", 0)
            setVarbit(player, 4449, points)
        }

        var SEED_LOCS: HashSet<Location> = HashSet()

        fun isSeed(loc: Location): Boolean {
            val scenery = getScenery(loc)
            return scenery != null && SEED_LOCS.contains(scenery.location)
        }

        fun populateSeeds() {
            val holes = countHoles()
            val seeds = min(1.0 * MAX_SEEDS, holes * 0.13).toInt()
            var tries = 0
            while (SEED_LOCS.size < seeds && tries++ < 1000) {
                val loc = VINESWEEPER_BORDERS.getRandomLoc()
                val scenery = getScenery(loc)
                if (scenery != null && HOLES.contains(scenery.id)) {
                    SEED_LOCS.add(loc)
                }
            }
        }

        private fun countHoles(): Int {
            val northEastX = VINESWEEPER_BORDERS.northEastX
            val northEastY = VINESWEEPER_BORDERS.northEastY
            val southWestX = VINESWEEPER_BORDERS.southWestX
            val southWestY = VINESWEEPER_BORDERS.southWestY
            var holeCount = 0
            for (x in southWestX..northEastX) {
                for (y in southWestY..northEastY) {
                    val scenery = getScenery(x, y, 0)
                    if (scenery != null && HOLES.contains(scenery.id)) {
                        holeCount++
                    }
                }
            }
            return holeCount
        }

        fun plantFlag(
            player: Player,
            hole: Scenery,
        ) {
            if (player.inventory.remove(Item(Items.FLAG_12625, 1))) {
                player.lock()
                player.visualize(
                    Animation(Animations.HUMAN_PLANT_FLAG_8711),
                    Graphics(org.rs.consts.Graphics.VINESWEEPER_PLANT_FLAG_1541),
                )
                sendMessage(player, "You add a flag to the patch.")
                GameWorld.Pulser.submit(
                    object : Pulse(2, player) {
                        override fun pulse(): Boolean {
                            SceneryBuilder.replace(hole, hole.transform(org.rs.consts.Scenery.FLAG_29457))
                            scheduleNPCs(player, hole.location, true, true)
                            player.unlock()
                            return true
                        }
                    },
                )
            } else {
                sendMessage(player, "You do not have a flag to place in the patch.")
            }
        }

        fun feedRabbit(
            player: Player,
            rabbit: NPC,
        ) {
            val respawnDelay = 50
            if (rabbit.isInvisible || DeathTask.isDead(rabbit)) {
                return
            }
            if (rabbit.getAttribute("dead", 0) > ticks) {
                return
            }
            if (player.inventory.remove(Item(Items.OGLEROOT_12624, 1))) {
                rabbit.setAttribute("dead", ticks + respawnDelay)
                player.skills.addExperience(Skills.HUNTER, 30.0)
                rabbit.sendChat("Squeak!")
                rabbit.animate(Animation(Animations.RABBIT_EAT_OGLEROOT_8734))
                rabbit.lock(4)
                GameWorld.Pulser.submit(
                    object : Pulse(3, player) {
                        override fun pulse(): Boolean {
                            rabbit.finalizeDeath(player)
                            rabbit.properties.teleportLocation = rabbit.properties.spawnLocation
                            return true
                        }
                    },
                )
            } else {
                sendMessage(player, "You don't have any ogleroots to feed the rabbit.")
            }
        }

        fun dig(
            player: Player,
            loc: Location,
        ) {
            if (isSeed(loc)) {
                val oldPoints = player.getAttribute("vinesweeper:points", 0)
                setAttribute(player, "/save:vinesweeper:points", Math.max(oldPoints - 10, 0))
                sendUpdatedPoints(player)
                sendMessage(player, "Oh dear! It looks like you dug up a potato seed by mistake.")
                scheduleNPCs(player, loc, false, false)
                val scenery = getScenery(loc)
                if (scenery != null) {
                    SceneryBuilder.replace(scenery, scenery.transform(org.rs.consts.Scenery.DEAD_PLANT_29456))
                }
            } else {
                player.incrementAttribute("/save:vinesweeper:points", 1)
                sendUpdatedPoints(player)
                var count = 0
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (isSeed(loc.transform(dx, dy, 0))) {
                            count += 1
                        }
                    }
                }
                val scenery = getScenery(loc)
                if (scenery != null) {
                    SceneryBuilder.replace(scenery, scenery.transform(NUMBERS[count]))
                }
                if (count == 0) {
                    for (dx in -1..1) {
                        for (dy in -1..1) {
                            val newLoc = loc.transform(dx, dy, 0)
                            if (isHole(newLoc)) dig(player, newLoc)
                        }
                    }
                }
            }
        }

        fun sendPoints(
            npc: NPC,
            dest: SeedDestination,
        ) {
            val level = dest.player.skills.getStaticLevel(Skills.FARMING)
            val points = RandomFunction.random(level, 4 * level)
            dest.player.incrementAttribute("/save:vinesweeper:points", points)
            dest.player.inventory.add(Item(Items.FLAG_12625, 1))
            sendUpdatedPoints(dest.player)
            for (neighbor in RegionManager.getLocalPlayers(npc)) {
                if (neighbor != dest.player) {
                    neighbor.incrementAttribute("/save:vinesweeper:points", points / 2)
                    sendUpdatedPoints(neighbor)
                }
            }
        }

        fun isHole(loc: Location): Boolean {
            val scenery = getScenery(loc)
            return scenery != null && HOLES.contains(scenery.id)
        }

        fun scheduleNPCs(
            player: Player,
            loc: Location,
            alive: Boolean,
            rabbit: Boolean,
        ) {
            val dest = SeedDestination(player, loc, alive)
            val ids =
                if (rabbit) {
                    RABBITS + FARMERS
                } else {
                    FARMERS
                }
            for (npc in findLocalNPCs(player, ids, 30)) {
                if (npc is VinesweeperNPC) {
                    npc.seedDestinations.add(dest)
                    npc.resetWalk()
                }
            }
        }

        object VinesweeperTeleport {
            @JvmStatic
            fun teleport(
                npc: NPC,
                player: Player,
            ) {
                if (hasTimerActive(player, "teleblock")) {
                    sendNPCDialogue(player, npc.id, "I can't do that, you're teleblocked!", FaceAnim.OLD_ANGRY1)
                    return
                }
                when (npc.id) {
                    NPCs.TECLYN_2861 -> visualize(npc, Animations.ATTACK_437, org.rs.consts.Graphics.CURSE_CAST_108)
                    else -> visualize(npc, Animations.BALLER_THROW_201, org.rs.consts.Graphics.CURSE_CAST_108)
                }
                stopWalk(npc)
                npc.faceTemporary(player, 1)
                lock(player, 3)
                playAudio(player, Sounds.CURSE_ALL_125, 0, 1)
                spawnProjectile(npc, player, org.rs.consts.Graphics.CURSE_PROJECTILE_109)
                sendChat(npc, "Avach nimporto!")
                GameWorld.Pulser.submit(
                    object : Pulse(1, player) {
                        var counter = 0

                        override fun pulse(): Boolean {
                            when (counter++) {
                                2 -> {
                                    player.savedData.globalData.setEssenceTeleporter(npc.id)
                                    setAttribute(player, "/save:vinesweeper:return-tele:x", npc.location.x)
                                    setAttribute(player, "/save:vinesweeper:return-tele:y", npc.location.y)
                                    player.properties.teleportLocation = AVACH_NIMPORTO_LOC
                                }

                                3 -> {
                                    visualize(player, -1, org.rs.consts.Graphics.CURSE_IMPACT_110)
                                    return true
                                }
                            }
                            return false
                        }
                    },
                )
            }
        }
    }
}

@Initializable
class VinesweeperNPC : AbstractNPC {
    var seedDestinations: ArrayList<VinesweeperListener.SeedDestination> = ArrayList()

    constructor() : super(RABBITS[0], null, true)
    private constructor(id: Int, location: Location) : super(id, location)

    override fun construct(
        id: Int,
        location: Location,
        vararg objects: Any?,
    ): AbstractNPC {
        return VinesweeperNPC(id, location)
    }

    init {
        walkRadius = 22
    }

    override fun getIds(): IntArray {
        return RABBITS + FARMERS
    }

    override fun handleTickActions() {
        val dest = seedDestinations.find { sd -> sd.loc == location }
        if (locks.isMovementLocked || locks.isInteractionLocked) return
        if (dest != null) {
            for (npc in RegionManager.getRegionPlane(location).npcs) {
                if (npc is VinesweeperNPC) {
                    npc.seedDestinations.remove(dest)
                    npc.resetWalk()
                }
            }
            val scenery = getScenery(dest.loc)
            if (scenery != null) {
                if (id in RABBITS) {
                    handleRabbitSeed(scenery, dest)
                } else {
                    if (dest.alive) {
                        handleFarmerFlag(scenery, dest)
                    } else {
                        handleFarmerSeed(scenery, dest)
                    }
                }
            }
            seedDestinations.remove(dest)
        }
        super.handleTickActions()
    }

    override fun getMovementDestination(): Location? {
        if (seedDestinations.size > 0) {
            seedDestinations.sortBy { a -> a.loc.getDistance(location).toInt() }
            return seedDestinations.first().loc
        } else {
            return super.getMovementDestination()
        }
    }

    fun handleRabbitSeed(
        scenery: Scenery,
        dest: VinesweeperListener.SeedDestination,
    ) {
        if (SEED_LOCS.contains(dest.loc)) {
            val replacement = org.rs.consts.Scenery.DEAD_PLANT_29456
            lock(4)
            animate(Animation(Animations.RABBIT_EAT_SEED_8718))
            SceneryBuilder.replace(scenery, scenery.transform(replacement))
            scheduleNPCs(dest.player, dest.loc, alive = false, rabbit = false)
        } else {
            scheduleNPCs(dest.player, dest.loc, alive = true, rabbit = false)
        }
    }

    fun handleFarmerSeed(
        scenery: Scenery,
        dest: VinesweeperListener.SeedDestination,
    ) {
        lock()
        var i = 0
        animate(Animation(Animations.GNOME_FARMER_DIG_SEED_8730))
        GameWorld.Pulser.submit(
            object : Pulse(3) {
                override fun pulse(): Boolean {
                    when (i++) {
                        0 -> {
                            sendChat(FARMER_FLAG_LINES.FIND_PLANT.line)
                        }

                        1 -> {
                            animate(Animation(Animations.GNOME_FARMER_SPADE_SMACK_8732))
                            sendChat(FARMER_FLAG_LINES.DEAD_PLANT.line)
                            SceneryBuilder.replace(scenery, scenery.transform(HOLES[0]))
                        }

                        2 -> {
                            animate(Animation(Animations.GNOME_FARMER_CLEAR_HOLES_8724))
                            farmerClear(dest)
                        }

                        3 -> {
                            unlock()
                            return true
                        }
                    }
                    return false
                }
            },
        )
    }

    fun handleFarmerFlag(
        scenery: Scenery,
        dest: VinesweeperListener.SeedDestination,
    ) {
        val npc = this
        lock()
        var i = 0
        animate(Animation(Animations.GNOME_FARMER_DIG_FLAG_8725))
        GameWorld.Pulser.submit(
            object : Pulse(3) {
                override fun pulse(): Boolean {
                    when (i++) {
                        0 -> {
                            sendChat(FARMER_FLAG_LINES.FIND_FLAG.line)
                        }

                        1 -> {
                            SceneryBuilder.replace(scenery, scenery.transform(HOLES[0]))
                            if (SEED_LOCS.contains(dest.loc)) {
                                sendChat(FARMER_FLAG_LINES.FIND_SEED.line)
                                animate(Animation(Animations.GNOME_FARMER_HOORAY_8731))
                                sendPoints(npc, dest)
                                i++
                            } else {
                                sendChat(FARMER_FLAG_LINES.NO_SEED.line)
                            }
                        }

                        2 -> {
                            sendChat(FARMER_FLAG_LINES.KEEP_FLAG.line)
                            i++
                        }

                        3 -> {
                            animate(Animation(Animations.GNOME_FARMER_CLEAR_HOLES_8724))
                            farmerClear(dest)
                        }

                        4 -> {
                            unlock()
                            return true
                        }
                    }
                    return false
                }
            },
        )
    }

    fun farmerClear(dest: VinesweeperListener.SeedDestination) {
        for (dx in -FARMER_CLEAR_RADIUS..FARMER_CLEAR_RADIUS) {
            for (dy in -FARMER_CLEAR_RADIUS..FARMER_CLEAR_RADIUS) {
                val toClear = getScenery(dest.loc.transform(dx, dy, 0))
                if (toClear != null &&
                    intArrayOf(
                        org.rs.consts.Scenery.DEAD_PLANT_29456,
                        *NUMBERS,
                    ).contains(toClear.id)
                ) {
                    SceneryBuilder.replace(toClear, toClear.transform(HOLES[0]))
                }
            }
        }
        SEED_LOCS.remove(dest.loc)
        populateSeeds()
    }
}
