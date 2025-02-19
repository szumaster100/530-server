package content.minigame.mta.room

import content.minigame.mta.MTAType
import content.minigame.mta.MTAZone
import core.api.sendMessage
import core.api.sendString
import core.game.interaction.Option
import core.game.node.Node
import core.game.node.entity.Entity
import core.game.node.entity.npc.NPC
import core.game.node.entity.player.Player
import core.game.node.entity.skill.Skills
import core.game.node.item.GroundItem
import core.game.node.item.GroundItemManager
import core.game.node.item.Item
import core.game.system.task.Pulse
import core.game.world.GameWorld.Pulser
import core.game.world.map.Direction
import core.game.world.map.Location
import core.game.world.map.build.DynamicRegion
import core.game.world.map.path.Pathfinder
import core.net.packet.PacketRepository
import core.net.packet.context.CameraContext
import core.net.packet.out.CameraViewPacket
import core.tools.RandomFunction
import org.rs.consts.Components
import org.rs.consts.NPCs
import java.util.*

class TelekineticTheatre
    @JvmOverloads
    constructor(
        val player: Player? = null,
    ) : MTAZone("Telekinetic Theatre", arrayOf()) {
        private val mazes: MutableList<Maze> = ArrayList(20)

        private var region: DynamicRegion? = null

        var base: Location? = null

        var maze: Maze? = null

        private var statue: GroundItem? = null

        private var guardian: NPC? = null

        var solved: Int = 0

        init {
            if (player != null) {
                mazes.addAll(listOf(*Maze.values()))
                this.solved = player.getSavedData().activityData.getSolvedMazes()
            }
        }

        override fun update(player: Player?) {
            sendString(
                player!!,
                "" + player.getSavedData().activityData.getPizazzPoints(type!!.ordinal),
                type!!.overlay.id,
                4,
            )
            sendString(player, "" + solved, Components.MAGICTRAINING_TELE_198, 7)
        }

        override fun leave(
            entity: Entity,
            logout: Boolean,
        ): Boolean {
            if (entity is Player) {
                val player = entity.asPlayer()
                if (player == null || player !== this.player) {
                    return super.leave(entity, logout)
                }
                if (statue != null) {
                    GroundItemManager.destroy(statue)
                }
                if (guardian != null) {
                    guardian!!.clear()
                }
                player.removeAttribute("camera")
            }
            return super.leave(entity, logout)
        }

        override fun interact(
            e: Entity,
            target: Node,
            option: Option,
        ): Boolean {
            return super.interact(e, target, option)
        }

        override fun parseCommand(
            player: Player,
            name: String,
            arguments: Array<String>,
        ): Boolean {
            when (name) {
                "childs" -> {
                    var i = 0
                    while (i < 8) {
                        player.packetDispatch.sendString("child=$i", 198, i)
                        i++
                    }
                }

                "reset" -> {
                    if (player.getAttribute("camera", false)) {
                        PacketRepository.send(
                            CameraViewPacket::class.java,
                            CameraContext(player, CameraContext.CameraType.RESET, 0, 0, 400, 1, 20),
                        )
                        player.setAttribute("camera", false)
                        return true
                    }
                    return true
                }
            }
            return false
        }

        override fun configure() {
            uid = name.hashCode()
            region = DynamicRegion.create(13463)
            base =
                Location.create(
                    region!!.getBorders().southWestX,
                    region!!.getBorders().southWestY,
                    0,
                )
            registerRegion(region!!.getId())
        }

        fun setUp() {
            var maze: Maze? = this.maze ?: RandomFunction.getRandomElement(mazes.toTypedArray())
            while (maze == null || maze == this.maze) {
                maze = RandomFunction.getRandomElement(mazes.toTypedArray())
            }
            mazes.remove(maze)
            this.maze = maze
            setNodes()
            player?.teleport(base!!.transform(maze.base))
        }

        fun setNodes() {
            if (statue != null) {
                GroundItemManager.destroy(statue)
            }
            if (guardian != null) {
                guardian!!.clear()
            }
            moveStatue(base!!.transform(maze!!.statueLocation))
            guardian = NPC.create(3098, base!!.transform(maze!!.guardianLocation))
            guardian!!.setWalks(true)
            guardian!!.init()
        }

        fun moveStatue(location: Location?) {
            if (statue != null) {
                GroundItemManager.destroy(statue)
            }
            GroundItemManager.create(createGroundItem(location).also { statue = it })
        }

        fun createGroundItem(location: Location?): GroundItem {
            val item: GroundItem =
                object : GroundItem(
                    Item(STATUE),
                    location,
                    player,
                ) {
                    override fun isActive(): Boolean {
                        return !isRemoved
                    }
                }
            return item
        }

        private fun win() {
            solved++
            GroundItemManager.destroy(statue)
            var points = 2
            player!!.dialogueInterpreter.sendDialogue(
                "Congratulations! You have received two Telekinetic Pizazz Points!",
            )
            if (solved >= 5) {
                mazes.addAll(Arrays.asList(*Maze.values()))
                solved = 0
                points += 8
                player.getSkills().addExperience(Skills.MAGIC, 1000.0, true)
                player.inventory.add(Item(563, 10))
                player.getSavedData().activityData.setSolvedMazes(0)
                player.dialogueInterpreter.addAction { player, buttonId ->
                    player.dialogueInterpreter.sendDialogue(
                        "Congratulations on solving five mazes in a row, have 8 bonus points,",
                        "10 law runes and extra magic XP!",
                    )
                }
            }
            player.getSavedData().activityData.setSolvedMazes(solved)
            incrementPoints(player, MTAType.TELEKINETIC.ordinal, points)
            this@TelekineticTheatre.update(player)
            player.setAttribute("camera", false)
            PacketRepository.send(
                CameraViewPacket::class.java,
                CameraContext(player, CameraContext.CameraType.RESET, 0, 0, 400, 1, 20),
            )
            val mazeGuard = NPC.create(NPCs.MAZE_GUARDIAN_3102, base!!.transform(maze!!.endLocation))
            mazeGuard.init()
        }

        fun moveStatue() {
            val dir = dir
            if (dir == null) {
                sendMessage(player!!, "Invalid move!")
                return
            }
            player!!.lock()
            Pulser.submit(
                object : Pulse(1, player) {
                    var win: Boolean = false

                    override fun pulse(): Boolean {
                        if (win) {
                            win()
                            return true
                        }
                        val next = statue!!.location.transform(dir, 1)
                        val path = Pathfinder.find(statue!!.location, next)
                        val end = !path.isSuccessful || path.isMoveNear || path.points.size > 2
                        if (end) {
                            return true
                        }
                        if (next == base!!.transform(maze!!.endLocation)) {
                            win = true
                        }
                        moveStatue(next)
                        return end
                    }

                    override fun stop() {
                        player.unlock()
                        super.stop()
                    }
                },
            )
        }

        fun observe(player: Player) {
            if (player.getAttribute("camera", false)) {
                PacketRepository.send(
                    CameraViewPacket::class.java,
                    CameraContext(player, CameraContext.CameraType.RESET, 0, 0, 400, 1, 20),
                )
                player.setAttribute("camera", false)
                return
            }
            player.sendMessage("Click observe on the guardian to reset your camera, or use the command ::reset")
            val l = base!!.transform(maze!!.cameraLocation)
            var x = l.x
            var y = l.y
            val yInc = -10
            val xInc = 0
            val speed = 95
            var height = 798
            player.setAttribute("camera", true)
            if (maze == Maze.FOURTH) {
                x += 11
                y += 15
                height = 799
                PacketRepository.send(
                    CameraViewPacket::class.java,
                    CameraContext(player, CameraContext.CameraType.POSITION, x + xInc, y + yInc, height, 1, speed),
                )
                PacketRepository.send(
                    CameraViewPacket::class.java,
                    CameraContext(player, CameraContext.CameraType.ROTATION, x - 55, y - 25, height, 1, speed),
                )
                return
            }
            PacketRepository.send(
                CameraViewPacket::class.java,
                CameraContext(player, CameraContext.CameraType.POSITION, x + xInc, y + yInc, height, 1, speed),
            )
            PacketRepository.send(
                CameraViewPacket::class.java,
                CameraContext(player, CameraContext.CameraType.ROTATION, x + xInc, y + yInc, height, 1, speed),
            )
        }

        fun reset(player: Player?) {
            moveStatue(base!!.transform(maze!!.statueLocation))
        }

        private val dir: Direction?

            get() {
                val myX = player!!.location.localX
                val myY = player.location.localY

                val data = maze!!.data
                if (myY >= data[0]) {
                    return Direction.NORTH
                } else if (myY <= data[1]) {
                    return Direction.SOUTH
                } else if (myX <= data[2]) {
                    return Direction.WEST
                } else if (myX >= data[3]) {
                    return Direction.EAST
                }
                return null
            }

        enum class Maze(
            val base: Location,
            val statueLocation: Location,
            val guardianLocation: Location,
            val endLocation: Location,
            val cameraLocation: Location,
            val data: IntArray,
        ) {
            FIRST(
                Location(8, 54, 0),
                Location(15, 41, 0),
                Location(8, 39, 0),
                Location(19, 50, 0),
                Location(15, 45, 0),
                intArrayOf(51, 40, 9, 20),
            ),
            SECOND(
                Location(34, 49, 1),
                Location(22, 53, 1),
                Location(26, 48, 1),
                Location(13, 44, 1),
                Location(17, 48, 1),
                intArrayOf(54, 43, 12, 23),
            ),
            THIRD(
                Location(54, 34, 1),
                Location(48, 22, 1),
                Location(45, 15, 1),
                Location(57, 22, 1),
                Location(53, 17, 1),
                intArrayOf(23, 12, 47, 58),
            ),

            FOURTH(
                Location(50, 42, 1),
                Location(46, 49, 1),
                Location(57, 41, 1),
                Location(55, 49, 1),
                Location(50, 53, 1),
                intArrayOf(59, 48, 45, 56),
            ),
            FITH(
                Location(46, 32, 0),
                Location(45, 14, 0),
                Location(45, 23, 0),
                Location(47, 18, 0),
                Location(45, 13, 0),
                intArrayOf(19, 8, 40, 51),
            ),

            SIXTH(
                Location(26, 26, 0),
                Location(15, 16, 0),
                Location(23, 24, 0),
                Location(14, 20, 0),
                Location(14, 15, 0),
                intArrayOf(21, 10, 9, 10),
            ),
            SEVENTH(
                Location(51, 52, 0),
                Location(40, 48, 0),
                Location(55, 50, 0),
                Location(39, 56, 0),
                Location(43, 51, 0),
                intArrayOf(57, 46, 37, 48),
            ),
            EIGTH(
                Location(31, 37, 2),
                Location(18, 54, 2),
                Location(28, 41, 2),
                Location(17, 54, 2),
                Location(19, 49, 2),
                intArrayOf(55, 44, 14, 25),
            ),
            NINTH(
                Location(40, 16, 2),
                Location(20, 10, 2),
                Location(34, 15, 2),
                Location(11, 19, 2),
                Location(16, 14, 2),
                intArrayOf(20, 9, 10, 21),
            ),
            TENTH(
                Location(27, 29, 1),
                Location(23, 20, 1),
                Location(31, 25, 1),
                Location(25, 16, 1),
                Location(23, 20, 1),
                intArrayOf(26, 15, 17, 28),
            ),
        }

        companion object {
            const val STATUE: Int = 6888

            fun start(player: Player) {
                setZone(player)
            }

            fun setZone(player: Player): TelekineticTheatre {
                val zone = TelekineticTheatre(player)
                zone.configure()
                zone.setUp()
                player.setAttribute("tele-zone", zone)
                return zone
            }

            fun getZone(player: Player): TelekineticTheatre {
                var zone = player.getAttribute<TelekineticTheatre>("tele-zone", null)
                if (zone == null) {
                    zone = setZone(player)
                }
                return zone
            }
        }
    }
