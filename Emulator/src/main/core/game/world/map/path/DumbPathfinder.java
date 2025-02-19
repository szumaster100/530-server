package core.game.world.map.path;

import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.game.world.map.Point;

import java.util.ArrayList;
import java.util.List;

public final class DumbPathfinder extends Pathfinder {

    private boolean found;

    private int z;

    private int x;

    private int y;

    @Override
    public Path find(Location start, int size, Location end, int sizeX, int sizeY, int rotation, int type, int walkingFlag, boolean near, ClipMaskSupplier clipMaskSupplier) {
        Path path = new Path();
        z = start.getZ();
        x = start.getX();
        y = start.getY();
        List<Point> points = new ArrayList<>(20);
        path.setSuccessful(true);
        while (x != end.getX() || y != end.getY()) {
            Direction[] directions = getDirection(x, y, end);
            if (type != 0) {
                if ((type < 5 || type == 10) && canDoorInteract(x, y, size, end.getX(), end.getY(), type - 1, rotation, z, clipMaskSupplier)) {
                    break;
                }
                if (type < 10 && canDecorationInteract(x, y, size, end.getX(), end.getY(), type - 1, rotation, z, clipMaskSupplier)) {
                    break;
                }
            }
            if (sizeX != 0 && sizeY != 0) {
                if (canInteract(x, y, size, end.getX(), end.getY(), sizeX, sizeY, walkingFlag, z, clipMaskSupplier)) {
                    break;
                }
                if (directions.length > 1) { // Ensures we approach the location
                    // correctly (non-diagonal).
                    Direction dir = directions[0];
                    if (x + dir.getStepX() == end.getX() && y + dir.getStepY() == end.getY()) {
                        directions[0] = directions[directions.length - 1];
                        directions[directions.length - 1] = dir;
                    }
                }
            }
            found = true;
            if (size < 2) {
                checkSingleTraversal(points, clipMaskSupplier, directions);
            } else if (size == 2) {
                checkDoubleTraversal(points, clipMaskSupplier, directions);
            } else {
                checkVariableTraversal(points, directions, size, clipMaskSupplier);
            }
            if (!found) {
                path.setMoveNear(x != start.getX() || y != start.getY());
                path.setSuccessful(false);
                break;
            }
        }
        if (!points.isEmpty()) {
            Direction last = null;
            for (int i = 0; i < points.size() - 1; i++) {
                Point p = points.get(i);
                path.getPoints().add(p);
            }
            path.getPoints().add(points.get(points.size() - 1));
        }
        return path;
    }

    private void checkSingleTraversal(List<Point> points, ClipMaskSupplier clipMaskSupplier, Direction... directions) {
        for (Direction dir : directions) {
            found = true;
            switch (dir) {
                case NORTH:
                    if ((clipMaskSupplier.getClippingFlag(z, x, y + 1) & PREVENT_NORTH) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x, y + 1, dir));
                    y++;
                    break;
                case NORTH_EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y) & PREVENT_EAST) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y + 1) & PREVENT_NORTH) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 1, y + 1) & PREVENT_NORTHEAST) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x + 1, y + 1, dir));
                    x++;
                    y++;
                    break;
                case EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y) & PREVENT_EAST) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x + 1, y, dir));
                    x++;
                    break;
                case SOUTH_EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y) & PREVENT_EAST) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y - 1) & PREVENT_SOUTH) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 1, y - 1) & PREVENT_SOUTHEAST) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x + 1, y - 1, dir));
                    x++;
                    y--;
                    break;
                case SOUTH:
                    if ((clipMaskSupplier.getClippingFlag(z, x, y - 1) & PREVENT_SOUTH) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x, y - 1, dir));
                    y--;
                    break;
                case SOUTH_WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y) & PREVENT_WEST) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y - 1) & PREVENT_SOUTH) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y - 1) & PREVENT_SOUTHWEST) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x - 1, y - 1, dir));
                    x--;
                    y--;
                    break;
                case WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y) & PREVENT_WEST) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x - 1, y, dir));
                    x--;
                    break;
                case NORTH_WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y) & PREVENT_WEST) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y + 1) & PREVENT_NORTH) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y + 1) & PREVENT_NORTHWEST) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x - 1, y + 1, dir));
                    x--;
                    y++;
                    break;
            }
            if (found) {
                break;
            }
        }
    }

    private void checkDoubleTraversal(List<Point> points, ClipMaskSupplier clipMaskSupplier, Direction... directions) {
        for (Direction dir : directions) {
            found = true;
            switch (dir) {
                case NORTH:
                    if ((clipMaskSupplier.getClippingFlag(z, x, y + 2) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 1, y + 2) & 0x12c01e0) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x, y + 1, dir));
                    y++;
                    break;
                case NORTH_EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y + 2) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 2, y + 2) & 0x12c01e0) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 2, y + 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x + 1, y + 1, dir));
                    x++;
                    y++;
                    break;
                case EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 2, y) & 0x12c0183) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 2, y + 1) & 0x12c01e0) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x + 1, y, dir));
                    x++;
                    break;
                case SOUTH_EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y - 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 2, y) & 0x12c01e0) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 2, y - 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x + 1, y - 1, dir));
                    x++;
                    y--;
                    break;
                case SOUTH:
                    if ((clipMaskSupplier.getClippingFlag(z, x, y - 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + 1, y - 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x, y - 1, dir));
                    y--;
                    break;
                case SOUTH_WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y - 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y - 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x - 1, y - 1, dir));
                    x--;
                    y--;
                    break;
                case WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y + 1) & 0x12c0138) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x - 1, y, dir));
                    x--;
                    break;
                case NORTH_WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y + 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y + 2) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y + 2) & 0x12c01e0) != 0) {
                        found = false;
                        break;
                    }
                    points.add(new Point(x - 1, y + 1, dir));
                    x--;
                    y++;
                    break;
            }
            if (found) {
                break;
            }
        }
    }

    private void checkVariableTraversal(List<Point> points, Direction[] directions, int size, ClipMaskSupplier clipMaskSupplier) {
        for (Direction dir : directions) {
            found = true;
            roar:
            switch (dir) {
                case NORTH:
                    if ((clipMaskSupplier.getClippingFlag(z, x, y + size) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x + (size - 1), y + size) & 0x12c01e0) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x + i, y + size) & 0x12c01f8) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x, y + 1, dir));
                    y++;
                    break;
                case NORTH_EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y + size) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x + size, y + size) & 0x12c01e0) != 0 || (clipMaskSupplier.getClippingFlag(z, x + size, y + 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x + (i + 1), y + size) & 0x12c01f8) != 0 || (clipMaskSupplier.getClippingFlag(z, x + size, y + (i + 1)) & 0x12c01e3) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x + 1, y + 1, dir));
                    x++;
                    y++;
                    break;
                case EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + size, y) & 0x12c0183) != 0 || (clipMaskSupplier.getClippingFlag(z, x + size, y + (size - 1)) & 0x12c01e0) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x + size, y + i) & 0x12c01e3) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x + 1, y, dir));
                    x++;
                    break;
                case SOUTH_EAST:
                    if ((clipMaskSupplier.getClippingFlag(z, x + 1, y - 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + size, y + (size - 2)) & 0x12c01e0) != 0 || (clipMaskSupplier.getClippingFlag(z, x + size, y - 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x + size, y + (i - 1)) & 0x12c01e3) != 0 || (clipMaskSupplier.getClippingFlag(z, x + (i + 1), y - 1) & 0x12c018f) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x + 1, y - 1, dir));
                    x++;
                    y--;
                    break;
                case SOUTH:
                    if ((clipMaskSupplier.getClippingFlag(z, x, y - 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + (size - 1), y - 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x + i, y - 1) & 0x12c018f) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x, y - 1, dir));
                    y--;
                    break;
                case SOUTH_WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y + (size - 2)) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y - 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + (size - 2), y - 1) & 0x12c0183) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x - 1, y + (i - 1)) & 0x12c013e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + (i - 1), y - 1) & 0x12c018f) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x - 1, y - 1, dir));
                    x--;
                    y--;
                    break;
                case WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y + (size - 1)) & 0x12c0138) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x - 1, y + i) & 0x12c013e) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x - 1, y, dir));
                    x--;
                    break;
                case NORTH_WEST:
                    if ((clipMaskSupplier.getClippingFlag(z, x - 1, y + 1) & 0x12c010e) != 0 || (clipMaskSupplier.getClippingFlag(z, x - 1, y + size) & 0x12c0138) != 0 || (clipMaskSupplier.getClippingFlag(z, x, y + size) & 0x12c01e0) != 0) {
                        found = false;
                        break;
                    }
                    for (int i = 1; i < size - 1; i++) {
                        if ((clipMaskSupplier.getClippingFlag(z, x - 1, y + (i + 1)) & 0x12c013e) != 0 || (clipMaskSupplier.getClippingFlag(z, x + (i - 1), y + size) & 0x12c01f8) != 0) {
                            found = false;
                            break roar;
                        }
                    }
                    points.add(new Point(x - 1, y + 1, dir));
                    x--;
                    y++;
                    break;
            }
            if (found) {
                break;
            }
        }
    }

    private static Direction[] getDirection(int startX, int startY, Location end) {
        int endX = end.getX();
        int endY = end.getY();
        if (startX == endX) {
            if (startY > endY) {
                return new Direction[]{Direction.SOUTH};
            } else if (startY < endY) {
                return new Direction[]{Direction.NORTH};
            }
        } else if (startY == endY) {
            if (startX > endX) {
                return new Direction[]{Direction.WEST};
            }
            return new Direction[]{Direction.EAST};
        } else {
            if (startX < endX && startY < endY) {
                return new Direction[]{Direction.NORTH_EAST, Direction.EAST, Direction.NORTH};
            } else if (startX < endX && startY > endY) {
                return new Direction[]{Direction.SOUTH_EAST, Direction.EAST, Direction.SOUTH};
            } else if (startX > endX && startY < endY) {
                return new Direction[]{Direction.NORTH_WEST, Direction.WEST, Direction.NORTH};
            } else if (startX > endX && startY > endY) {
                return new Direction[]{Direction.SOUTH_WEST, Direction.WEST, Direction.SOUTH};
            }
        }
        return new Direction[0];
    }

}
