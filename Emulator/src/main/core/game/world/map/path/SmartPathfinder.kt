package core.game.world.map.path

import core.ServerConstants
import core.api.log
import core.api.utils.Vector
import core.game.world.GameWorld
import core.game.world.map.Direction
import core.game.world.map.Location
import core.game.world.map.Point
import core.tools.Log
import java.awt.image.BufferedImage
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import javax.imageio.ImageIO

class SmartPathfinder
    internal constructor() : Pathfinder() {
        private var queueX: IntArray = intArrayOf(0)

        private var queueY: IntArray = intArrayOf(0)

        private var via: Array<IntArray> = Array(104) { IntArray(104) }

        private var cost: Array<IntArray> = Array(104) { IntArray(104) }

        private var writePathPosition = 0

        private var curX = 0

        private var curY = 0

        private var dstX = 0

        private var dstY = 0

        private var foundPath = false

        fun reset() {
            queueX = IntArray(4096)
            queueY = IntArray(4096)
            via = Array(104) { IntArray(104) }
            cost = Array(104) { IntArray(104) }
            writePathPosition = 0
        }

        fun check(
            x: Int,
            y: Int,
            dir: Int,
            currentCost: Int,
            diagonalPenalty: Int = 0,
        ) {
            if (cost[x][y] > currentCost + diagonalPenalty) {
                queueX[writePathPosition] = x
                queueY[writePathPosition] = y
                via[x][y] = dir
                cost[x][y] = currentCost + diagonalPenalty
                writePathPosition = writePathPosition + 1 and 0xfff
            }
        }

        override fun find(
            start: Location?,
            moverSize: Int,
            dest: Location?,
            sizeX: Int,
            sizeY: Int,
            rotation: Int,
            type: Int,
            walkingFlag: Int,
            near: Boolean,
            clipMaskSupplier: ClipMaskSupplier?,
        ): Path {
            reset()
            assert(start != null && dest != null)
            var vec = Vector.betweenLocs(start!!, dest!!)
            var mag = kotlin.math.floor(vec.magnitude())
            var end = dest!!
            if (mag > ServerConstants.MAX_PATHFIND_DISTANCE) {
                try {
                    if (mag < 50.0) { // truncate the path if it's realistically long
                        vec = vec.normalized() * (ServerConstants.MAX_PATHFIND_DISTANCE - 1)
                        end = start!!.transform(vec)
                    } else {
                        throw Exception(
                            "Pathfinding distance exceeds server max! -> " + mag.toString() + " {" + start + "->" + end +
                                "}",
                        )
                    }
                } catch (e: Exception) {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    e.printStackTrace(pw)
                    log(this::class.java, Log.FINE, sw.toString())
                    val p = Path()
                    p.isMoveNear = true
                    return p
                }
            }
            val path = Path()
            foundPath = false
            for (x in 0..103) {
                for (y in 0..103) {
                    via[x][y] = 0
                    cost[x][y] = 99999999
                }
            }
            val z = start!!.z
            val location = Location.create(start.regionX - 6 shl 3, start.regionY - 6 shl 3, z)
            curX = start.sceneX
            curY = start.sceneY
            dstX = end!!.getSceneX(start)
            dstY = end.getSceneY(start)
            var attempts: Int
            var readPosition: Int
            check(curX, curY, 99, 0)
            try {
                if (moverSize < 2) {
                    if (GameWorld.settings?.smartpathfinder_bfs ?: false) {
                        checkSingleTraversal(
                            end,
                            sizeX,
                            sizeY,
                            type,
                            rotation,
                            walkingFlag,
                            location,
                            clipMaskSupplier!!,
                        )
                    } else {
                        checkSingleTraversalAstar(
                            end,
                            sizeX,
                            sizeY,
                            type,
                            rotation,
                            walkingFlag,
                            location,
                            clipMaskSupplier!!,
                        )
                    }
                } else if (moverSize == 2) {
                    checkDoubleTraversal(end, sizeX, sizeY, type, rotation, walkingFlag, location, clipMaskSupplier!!)
                } else {
                    checkVariableTraversal(
                        end,
                        moverSize,
                        sizeX,
                        sizeY,
                        type,
                        rotation,
                        walkingFlag,
                        location,
                        clipMaskSupplier!!,
                    )
                }
            } catch (e: Exception) {
            }
            var debugImg =
                if (false) {
                    BufferedImage(4 * 104 + 2, 104, BufferedImage.TYPE_INT_RGB)
                } else {
                    null
                }
            if (debugImg != null) {
                for (y in 0 until 104) {
                    for (x in 0 until 104) {
                        debugImg.setRGB(x, 103 - y, via[x][y] * (((1 shl 24) - 1) / 12))
                        val c = Math.min(4 * Math.min(cost[x][y], 64), 255)
                        debugImg.setRGB(105 + x, 103 - y, (c shl 16) or (c shl 8) or c)
                        debugImg.setRGB(
                            2 * 105 + x,
                            103 - y,
                            clipMaskSupplier!!.getClippingFlag(location.z, location.x + x, location.y + y),
                        )
                    }
                }
            }

            if (!foundPath) {
                if (near) {
                    var fullCost = 1000
                    var thisCost = 100
                    val depth = 10
                    for (x in dstX - depth..dstX + depth) {
                        for (y in dstY - depth..dstY + depth) {
                            if (x >= 0 && y >= 0 && x < 104 && y < 104 && cost[x][y] < 100) {
                                var diffX = 0
                                if (x < dstX) {
                                    diffX = dstX - x
                                } else if (x > dstX + sizeX - 1) {
                                    diffX = x - (dstX + sizeX - 1)
                                }
                                var diffY = 0
                                if (y < dstY) {
                                    diffY = dstY - y
                                } else if (y > dstY + sizeY - 1) {
                                    diffY = y - (dstY + sizeY - 1)
                                }
                                val totalCost = diffX * diffX + diffY * diffY
                                if (totalCost < fullCost || totalCost == fullCost && cost[x][y] < thisCost) {
                                    fullCost = totalCost
                                    thisCost = cost[x][y]
                                    curX = x
                                    curY = y
                                }
                            }
                        }
                    }
                    if (fullCost == 1000) {
                        return path
                    }
                    path.isMoveNear = true
                }
            }
            readPosition = 0
            queueX[readPosition] = curX
            queueY[readPosition++] = curY
            var previousDirection: Int
            attempts = 0
            var directionFlag = via[curX][curY].also { previousDirection = it }
            while (curX != start.sceneX || curY != start.sceneY) {
                if (++attempts > queueX.size) {
                    return path
                }
                previousDirection = directionFlag
                queueX[readPosition] = curX
                queueY[readPosition++] = curY
                if (directionFlag and WEST_FLAG != 0) {
                    curX++
                } else if (directionFlag and EAST_FLAG != 0) {
                    curX--
                }
                if (directionFlag and SOUTH_FLAG != 0) {
                    curY++
                } else if (directionFlag and NORTH_FLAG != 0) {
                    curY--
                }
                if (debugImg != null) {
                    debugImg.setRGB(3 * 105 + curX, 103 - curY, 0x0000ff)
                }
                directionFlag = via[curX][curY]
            }
            if (debugImg != null) {
                debugImg.setRGB(3 * 105 + start.sceneX, 103 - start.sceneY, 0xff0000)
                debugImg.setRGB(3 * 105 + dstX, 103 - dstY, 0x00ff00)
                if (GameWorld.settings?.smartpathfinder_bfs ?: false) {
                    ImageIO.write(
                        debugImg,
                        "png",
                        File(String.format("bfs_%04d_%04d_%04d_%04d.png", start.x, start.y, end.x, end.y)),
                    )
                } else {
                    ImageIO.write(
                        debugImg,
                        "png",
                        File(String.format("astar_%04d_%04d_%04d_%04d.png", start.x, start.y, end.x, end.y)),
                    )
                }
            }
            val size = readPosition--
            var absX = location.x + queueX[readPosition]
            var absY = location.y + queueY[readPosition]
            path.points.add(Point(absX, absY))
            for (i in 1 until size) {
                readPosition--
                absX = location.x + queueX[readPosition]
                absY = location.y + queueY[readPosition]
                path.points.add(Point(absX, absY))
            }
            path.setSuccessful(true)
            if (end != dest) {
                path.isMoveNear = true
            }
            return path
        }

        class UIntAsPointComparator(
            val end: Location,
        ) : Comparator<UInt> {
            override fun compare(
                p: UInt,
                q: UInt,
            ): Int {
                val pc: UInt = (p and 0x00ff0000u) shr 16
                val px: UInt = (p and 0x0000ff00u) shr 8
                val py: UInt = (p and 0x000000ffu)
                val qc: UInt = (q and 0x00ff0000u) shr 16
                val qx: UInt = (q and 0x0000ff00u) shr 8
                val qy: UInt = (q and 0x000000ffu)
                // val dp = pc.toInt() + Math.abs(end.sceneX - (px.toInt())) + Math.abs(end.sceneY - (py.toInt()))
                // val dq = qc.toInt() + Math.abs(end.sceneX - (qx.toInt())) + Math.abs(end.sceneY - (qy.toInt()))
                val dp =
                    pc.toDouble() +
                        Math
                            .max(Math.abs(end.sceneX - px.toInt()), Math.abs(end.sceneY - py.toInt()))
                            .toDouble()
                val dq =
                    qc.toDouble() +
                        Math
                            .max(Math.abs(end.sceneX - qx.toInt()), Math.abs(end.sceneY - qy.toInt()))
                            .toDouble()
                if (dp < dq) {
                    return -1
                } else if (dq < dp) {
                    return 1
                } else {
                    return 0
                }
            }

            override fun equals(other: Any?): Boolean {
                if (other is UIntAsPointComparator) {
                    return end == other.end
                } else {
                    return false
                }
            }

            override fun hashCode(): Int {
                return end.hashCode()
            }
        }

        private fun checkSingleTraversalAstar(
            end: Location,
            sizeX: Int,
            sizeY: Int,
            type: Int,
            rotation: Int,
            walkingFlag: Int,
            location: Location,
            clipMaskSupplier: ClipMaskSupplier,
        ) {
            val z = location.z
            var queue = PriorityQueue(4096, UIntAsPointComparator(end))
            queue.add(((curX.toUInt()) shl 8) or (curY.toUInt()))
            while (!foundPath && !queue.isEmpty()) {
                val point = queue.poll()
                val curCost = ((point and 0xff0000u) shr 16).toInt()
                curX = ((point and 0x0000ff00u) shr 8).toInt()
                curY = (point and 0x000000ffu).toInt()
                val absX = location.x + curX
                val absY = location.y + curY
                if (curX == dstX && curY == dstY) {
                    foundPath = true
                    break
                }
                if (type != 0) {
                    if ((type < 5 || type == 10) &&
                        canDoorInteract(
                            absX,
                            absY,
                            1,
                            end.x,
                            end.y,
                            type - 1,
                            rotation,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                    if (type < 10 &&
                        canDecorationInteract(
                            absX,
                            absY,
                            1,
                            end.x,
                            end.y,
                            type - 1,
                            rotation,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                }
                if (sizeX != 0 &&
                    sizeY != 0 &&
                    canInteract(
                        absX,
                        absY,
                        1,
                        end.x,
                        end.y,
                        sizeX,
                        sizeY,
                        walkingFlag,
                        z,
                        clipMaskSupplier,
                    )
                ) {
                    foundPath = true
                    break
                }
                val newCost = curCost + 1
                // val orthogonalsFirst = arrayOf(Direction.EAST, Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.NORTH_EAST, Direction.NORTH_WEST, Direction.SOUTH_WEST, Direction.SOUTH_EAST)
                val orthogonalsFirst =
                    arrayOf(
                        Direction.SOUTH,
                        Direction.WEST,
                        Direction.NORTH,
                        Direction.EAST,
                        Direction.SOUTH_WEST,
                        Direction.NORTH_WEST,
                        Direction.SOUTH_EAST,
                        Direction.NORTH_EAST,
                    )
                // val orthogonalsFirst = arrayOf(Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST)
                // for(dir in Direction.values()) {
                for (dir in orthogonalsFirst) {
                    val newSceneX: Int = curX + dir.stepX
                    val newSceneY: Int = curY + dir.stepY
                    if (0 <= newSceneX &&
                        newSceneX < 104 &&
                        0 <= newSceneY &&
                        newSceneY < 104 &&
                        via[newSceneX][newSceneY] == 0
                    ) {
                        if (dir.canMoveFrom(z, absX, absY, clipMaskSupplier)) {
                            val diagonalPenalty = Math.abs(dir.stepX) + Math.abs(dir.stepY) - 1
                            val flag = flagForDirection(dir)
                            check(newSceneX, newSceneY, flag, newCost, diagonalPenalty)
                            if (via[newSceneX][newSceneY] == flag) {
                                queue.add(
                                    ((newCost + diagonalPenalty).toUInt() shl 16) or (newSceneX.toUInt() shl 8) or
                                        newSceneY.toUInt(),
                                )
                            }
                        }
                    }
                }
            }
        }

        private fun checkSingleTraversal(
            end: Location,
            sizeX: Int,
            sizeY: Int,
            type: Int,
            rotation: Int,
            walkingFlag: Int,
            location: Location,
            clipMaskSupplier: ClipMaskSupplier,
        ) {
            var readPosition = 0
            val z = location.z
            while (writePathPosition != readPosition) {
                curX = queueX[readPosition]
                curY = queueY[readPosition]
                readPosition = readPosition + 1 and 0xfff
                if (curX == dstX && curY == dstY) {
                    foundPath = true
                    break
                }
                try {
                    val absX = location.x + curX
                    val absY = location.y + curY
                    if (type != 0) {
                        if ((type < 5 || type == 10) &&
                            canDoorInteract(
                                absX,
                                absY,
                                1,
                                end.x,
                                end.y,
                                type - 1,
                                rotation,
                                z,
                                clipMaskSupplier,
                            )
                        ) {
                            foundPath = true
                            break
                        }
                        if (type < 10 &&
                            canDecorationInteract(
                                absX,
                                absY,
                                1,
                                end.x,
                                end.y,
                                type - 1,
                                rotation,
                                z,
                                clipMaskSupplier,
                            )
                        ) {
                            foundPath = true
                            break
                        }
                    }
                    if (sizeX != 0 &&
                        sizeY != 0 &&
                        canInteract(
                            absX,
                            absY,
                            1,
                            end.x,
                            end.y,
                            sizeX,
                            sizeY,
                            walkingFlag,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                    val thisCost = cost[curX][curY] + 1
                    if (curY > 0 &&
                        via[curX][curY - 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX,
                            absY - 1,
                        ) and 0x12c0102 == 0
                    ) {
                        check(curX, curY - 1, SOUTH_FLAG, thisCost)
                    }
                    if (curX > 0 &&
                        via[curX - 1][curY] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY,
                        ) and 0x12c0108 == 0
                    ) {
                        check(curX - 1, curY, WEST_FLAG, thisCost)
                    }
                    if (curY < 103 &&
                        via[curX][curY + 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX,
                            absY + 1,
                        ) and 0x12c0120 == 0
                    ) {
                        check(curX, curY + 1, NORTH_FLAG, thisCost)
                    }
                    if (curX < 103 &&
                        via[curX + 1][curY] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + 1,
                            absY,
                        ) and 0x12c0180 == 0
                    ) {
                        check(curX + 1, curY, EAST_FLAG, thisCost)
                    }
                    if (curX > 0 &&
                        curY > 0 &&
                        via[curX - 1][curY - 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY - 1,
                        ) and 0x12c010e == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY,
                        ) and 0x12c0108 == 0 &&
                        clipMaskSupplier.getClippingFlag(z, absX, absY - 1) and 0x12c0102 == 0
                    ) {
                        check(curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost)
                    }
                    if (curX > 0 &&
                        curY < 103 &&
                        via[curX - 1][curY + 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY + 1,
                        ) and 0x12c0138 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY,
                        ) and 0x12c0108 == 0 &&
                        clipMaskSupplier.getClippingFlag(z, absX, absY + 1) and 0x12c0120 == 0
                    ) {
                        check(curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost)
                    }
                    if (curX < 103 &&
                        curY > 0 &&
                        via[curX + 1][curY - 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + 1,
                            absY - 1,
                        ) and 0x12c0183 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + 1,
                            absY,
                        ) and 0x12c0180 == 0 &&
                        clipMaskSupplier.getClippingFlag(z, absX, absY - 1) and 0x12c0102 == 0
                    ) {
                        check(curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost)
                    }
                    if (curX < 103 &&
                        curY < 103 &&
                        via[curX + 1][curY + 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + 1,
                            absY + 1,
                        ) and 0x12c01e0 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + 1,
                            absY,
                        ) and 0x12c0180 == 0 &&
                        clipMaskSupplier.getClippingFlag(z, absX, absY + 1) and 0x12c0120 == 0
                    ) {
                        check(curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost)
                    }
                } catch (e: Exception) {
                    // e.printStackTrace()println("curX " + curX + " curY" + curY + " via " + via[curX + 1] + via[curY + 1])
                }
            }
        }

        private fun checkDoubleTraversal(
            end: Location,
            sizeX: Int,
            sizeY: Int,
            type: Int,
            rotation: Int,
            walkingFlag: Int,
            location: Location,
            clipMaskSupplier: ClipMaskSupplier,
        ) {
            var readPosition = 0
            val z = location.z
            while (writePathPosition != readPosition) {
                curX = queueX[readPosition]
                curY = queueY[readPosition]
                readPosition = readPosition + 1 and 0xfff
                if (curX == dstX && curY == dstY) {
                    foundPath = true
                    break
                }
                val absX = location.x + curX
                val absY = location.y + curY
                if (type != 0) {
                    if ((type < 5 || type == 10) &&
                        canDoorInteract(
                            absX,
                            absY,
                            2,
                            end.x,
                            end.y,
                            type - 1,
                            rotation,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                    if (type < 10 &&
                        canDecorationInteract(
                            absX,
                            absY,
                            2,
                            end.x,
                            end.y,
                            type - 1,
                            rotation,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                }
                if (sizeX != 0 &&
                    sizeY != 0 &&
                    canInteract(
                        absX,
                        absY,
                        2,
                        end.x,
                        end.y,
                        sizeX,
                        sizeY,
                        walkingFlag,
                        z,
                        clipMaskSupplier,
                    )
                ) {
                    foundPath = true
                    break
                }
                val thisCost = cost[curX][curY] + 1
                if (curY > 0 &&
                    via[curX][curY - 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX,
                        absY - 1,
                    ) and 0x12c010e == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX + 1, absY - 1) and 0x12c0183 == 0
                ) {
                    check(curX, curY - 1, SOUTH_FLAG, thisCost)
                }
                if (curX > 0 &&
                    via[curX - 1][curY] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX - 1,
                        absY,
                    ) and 0x12c010e == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX - 1, absY + 1) and 0x12c0138 == 0
                ) {
                    check(curX - 1, curY, WEST_FLAG, thisCost)
                }
                if (curY < 102 &&
                    via[curX][curY + 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX,
                        absY + 2,
                    ) and 0x12c0138 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX + 1, absY + 2) and 0x12c01e0 == 0
                ) {
                    check(curX, curY + 1, NORTH_FLAG, thisCost)
                }
                if (curX < 102 &&
                    via[curX + 1][curY] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + 2,
                        absY,
                    ) and 0x12c0183 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX + 2, absY + 1) and 0x12c01e0 == 0
                ) {
                    check(curX + 1, curY, EAST_FLAG, thisCost)
                }
                if (curX > 0 &&
                    curY > 0 &&
                    via[curX - 1][curY - 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX - 1,
                        absY - 1,
                    ) and 0x12c010e == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX - 1,
                        absY,
                    ) and 0x12c0138 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX, absY - 1) and 0x12c0183 == 0
                ) {
                    check(curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost)
                }
                if (curX > 0 &&
                    curY < 102 &&
                    via[curX - 1][curY + 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX - 1,
                        absY + 1,
                    ) and 0x12c010e == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX - 1,
                        absY + 2,
                    ) and 0x12c0138 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX, absY + 2) and 0x12c01e0 == 0
                ) {
                    check(curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost)
                }
                if (curX < 102 &&
                    curY > 0 &&
                    via[curX + 1][curY - 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + 1,
                        absY - 1,
                    ) and 0x12c010e == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + 2,
                        absY,
                    ) and 0x12c01e0 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX + 2, absY - 1) and 0x12c0183 == 0
                ) {
                    check(curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost)
                }
                if (curX < 102 &&
                    curY < 102 &&
                    via[curX + 1][curY + 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + 1,
                        absY + 2,
                    ) and 0x12c0138 == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + 2,
                        absY + 2,
                    ) and 0x12c01e0 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX + 2, absY + 1) and 0x12c0183 == 0
                ) {
                    check(curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost)
                }
            }
        }

        private fun checkVariableTraversal(
            end: Location,
            size: Int,
            sizeX: Int,
            sizeY: Int,
            type: Int,
            rotation: Int,
            walkingFlag: Int,
            location: Location,
            clipMaskSupplier: ClipMaskSupplier,
        ) {
            var readPosition = 0
            val z = location.z
            main@ while (writePathPosition != readPosition) {
                curX = queueX[readPosition]
                curY = queueY[readPosition]
                readPosition = readPosition + 1 and 0xfff
                if (curX == dstX && curY == dstY) {
                    foundPath = true
                    break
                }
                val absX = location.x + curX
                val absY = location.y + curY
                if (type != 0) {
                    if ((type < 5 || type == 10) &&
                        canDoorInteract(
                            absX,
                            absY,
                            size,
                            end.x,
                            end.y,
                            type - 1,
                            rotation,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                    if (type < 10 &&
                        canDecorationInteract(
                            absX,
                            absY,
                            size,
                            end.x,
                            end.y,
                            type - 1,
                            rotation,
                            z,
                            clipMaskSupplier,
                        )
                    ) {
                        foundPath = true
                        break
                    }
                }
                if (sizeX != 0 &&
                    sizeY != 0 &&
                    canInteract(
                        absX,
                        absY,
                        size,
                        end.x,
                        end.y,
                        sizeX,
                        sizeY,
                        walkingFlag,
                        z,
                        clipMaskSupplier,
                    )
                ) {
                    foundPath = true
                    break
                }
                val thisCost = cost[curX][curY] + 1
                south@ do {
                    if (curY > 0 &&
                        via[curX][curY - 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX,
                            absY - 1,
                        ) and 0x12c010e == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + (size - 1),
                            absY - 1,
                        ) and 0x12c0183 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(z, absX + i, absY - 1) and 0x12c018f != 0) {
                                break@south
                            }
                        }
                        check(curX, curY - 1, SOUTH_FLAG, thisCost)
                    }
                } while (false)
                west@ do {
                    if (curX > 0 &&
                        via[curX - 1][curY] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY,
                        ) and 0x12c010e == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY + (size - 1),
                        ) and 0x12c0138 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(z, absX - 1, absY + i) and 0x12c013e != 0) {
                                break@west
                            }
                        }
                        check(curX - 1, curY, WEST_FLAG, thisCost)
                    }
                } while (false)
                north@ do {
                    if (curY < 102 &&
                        via[curX][curY + 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX,
                            absY + size,
                        ) and 0x12c0138 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + (size - 1),
                            absY + size,
                        ) and 0x12c01e0 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(z, absX + i, absY + size) and 0x12c01f8 != 0) {
                                break@north
                            }
                        }
                        check(curX, curY + 1, NORTH_FLAG, thisCost)
                    }
                } while (false)
                east@ do {
                    if (curX < 102 &&
                        via[curX + 1][curY] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + size,
                            absY,
                        ) and 0x12c0183 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + size,
                            absY + (size - 1),
                        ) and 0x12c01e0 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(z, absX + size, absY + i) and 0x12c01e3 != 0) {
                                break@east
                            }
                        }
                        check(curX + 1, curY, EAST_FLAG, thisCost)
                    }
                } while (false)
                southWest@ do {
                    if (curX > 0 &&
                        curY > 0 &&
                        via[curX - 1][curY - 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY + (size - 2),
                        ) and 0x12c0138 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY - 1,
                        ) and 0x12c010e == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + (size - 2),
                            absY - 1,
                        ) and 0x12c0183 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(
                                    z,
                                    absX - 1,
                                    absY + (i - 1),
                                ) and 0x12c013e != 0 ||
                                clipMaskSupplier.getClippingFlag(
                                    z,
                                    absX + (i - 1),
                                    absY - 1,
                                ) and 0x12c018f != 0
                            ) {
                                break@southWest
                            }
                        }
                        check(curX - 1, curY - 1, SOUTH_WEST_FLAG, thisCost)
                    }
                } while (false)
                northWest@ do {
                    if (curX > 0 &&
                        curY < 102 &&
                        via[curX - 1][curY + 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY + 1,
                        ) and 0x12c010e == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX - 1,
                            absY + size,
                        ) and 0x12c0138 == 0 &&
                        clipMaskSupplier.getClippingFlag(z, absX, absY + size) and 0x12c01e0 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(
                                    z,
                                    absX - 1,
                                    absY + (i + 1),
                                ) and 0x12c013e != 0 ||
                                clipMaskSupplier.getClippingFlag(
                                    z,
                                    absX + (i - 1),
                                    absY + size,
                                ) and 0x12c01f8 != 0
                            ) {
                                break@northWest
                            }
                        }
                        check(curX - 1, curY + 1, NORTH_WEST_FLAG, thisCost)
                    }
                } while (false)
                southEast@ do {
                    if (curX < 102 &&
                        curY > 0 &&
                        via[curX + 1][curY - 1] == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + 1,
                            absY - 1,
                        ) and 0x12c010e == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + size,
                            absY - 1,
                        ) and 0x12c0183 == 0 &&
                        clipMaskSupplier.getClippingFlag(
                            z,
                            absX + size,
                            absY + (size - 2),
                        ) and 0x12c01e0 == 0
                    ) {
                        for (i in 1 until size - 1) {
                            if (clipMaskSupplier.getClippingFlag(
                                    z,
                                    absX + size,
                                    absY + (i - 1),
                                ) and 0x12c01e3 != 0 ||
                                clipMaskSupplier.getClippingFlag(
                                    z,
                                    absX + (i + 1),
                                    absY - 1,
                                ) and 0x12c018f != 0
                            ) {
                                break@southEast
                            }
                        }
                        check(curX + 1, curY - 1, SOUTH_EAST_FLAG, thisCost)
                    }
                } while (false)
                if (curX < 102 &&
                    curY < 102 &&
                    via[curX + 1][curY + 1] == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + 1,
                        absY + size,
                    ) and 0x12c0138 == 0 &&
                    clipMaskSupplier.getClippingFlag(
                        z,
                        absX + size,
                        absY + size,
                    ) and 0x12c01e0 == 0 &&
                    clipMaskSupplier.getClippingFlag(z, absX + size, absY + 1) and 0x12c0183 == 0
                ) {
                    for (i in 1 until size - 1) {
                        if (clipMaskSupplier.getClippingFlag(
                                z,
                                absX + (i + 1),
                                absY + size,
                            ) and 0x12c01f8 != 0 ||
                            clipMaskSupplier.getClippingFlag(
                                z,
                                absX + size,
                                absY + (i + 1),
                            ) and 0x12c01e3 != 0
                        ) {
                            continue@main
                        }
                    }
                    check(curX + 1, curY + 1, NORTH_EAST_FLAG, thisCost)
                }
            }
        }
    }
