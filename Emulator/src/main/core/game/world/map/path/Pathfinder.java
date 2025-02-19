package core.game.world.map.path;

import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.item.GroundItem;
import core.game.node.scenery.Scenery;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;

public abstract class Pathfinder {
    public static final int PREVENT_NORTH = 0x12c0120;
    public static final int PREVENT_EAST = 0x12c0180;
    public static final int PREVENT_NORTHEAST = 0x12c01e0;
    public static final int PREVENT_SOUTH = 0x12c0102;
    public static final int PREVENT_SOUTHEAST = 0x12c0183;
    public static final int PREVENT_WEST = 0x12c0108;
    public static final int PREVENT_SOUTHWEST = 0x12c010e;
    public static final int PREVENT_NORTHWEST = 0x12c0138;

    public static final SmartPathfinder SMART = new SmartPathfinder();

    public static final DumbPathfinder DUMB = new DumbPathfinder();

    public static final ProjectilePathfinder PROJECTILE = new ProjectilePathfinder();

    public static final int SOUTH_FLAG = 0x1;

    public static final int WEST_FLAG = 0x2;

    public static final int NORTH_FLAG = 0x4;

    public static final int EAST_FLAG = 0x8;

    public static final int SOUTH_WEST_FLAG = SOUTH_FLAG | WEST_FLAG;

    public static final int NORTH_WEST_FLAG = NORTH_FLAG | WEST_FLAG;

    public static final int SOUTH_EAST_FLAG = SOUTH_FLAG | EAST_FLAG;

    public static final int NORTH_EAST_FLAG = NORTH_FLAG | EAST_FLAG;

    public static int flagForDirection(Direction d) {
        switch (d) {
            case NORTH_WEST:
                return NORTH_WEST_FLAG;
            case NORTH:
                return NORTH_FLAG;
            case NORTH_EAST:
                return NORTH_EAST_FLAG;
            case WEST:
                return WEST_FLAG;
            case EAST:
                return EAST_FLAG;
            case SOUTH_WEST:
                return SOUTH_WEST_FLAG;
            case SOUTH:
                return SOUTH_FLAG;
            case SOUTH_EAST:
                return SOUTH_EAST_FLAG;
            default:
                return 0;
        }
    }

    public abstract Path find(Location location, int size, Location end, int sizeX, int sizeY, int rotation, int type, int walkingFlag, boolean near, ClipMaskSupplier clipMaskSupplier);

    public static Path find(Entity mover, Node destination) {
        return find(mover, destination, true, SMART);
    }

    public static Path find(Entity mover, Node destination, boolean near, Pathfinder finder) {
        ClipMaskSupplier cms = null;
        if (mover instanceof NPC) {
            cms = ((NPC) mover).behavior.getClippingSupplier(((NPC) mover));
        }
        if (cms == null)
            cms = RegionManager::getClippingFlag;
        return find(mover.getLocation(), mover.size(), destination, near, finder, cms);
    }

    public static Path findWater(Entity mover, Node destination, boolean near, Pathfinder finder) {
        return find(mover.getLocation(), mover.size(), destination, near, finder, RegionManager::getWaterClipFlag);
    }

    public static Path find(Entity mover, Node destination, boolean near, Pathfinder finder, ClipMaskSupplier clipMaskSupplier) {
        return find(mover.getLocation(), mover.size(), destination, near, finder, clipMaskSupplier);
    }

    public static Path find(Location start, Node destination) {
        return find(start, destination, true, SMART);
    }

    public static Path find(Location start, Node destination, int moverSize) {
        return find(start, moverSize, destination, true, SMART, RegionManager::getClippingFlag);
    }

    public static Path find(Location start, Node destination, boolean near, Pathfinder finder) {
        return find(start, 1, destination, near, finder, RegionManager::getClippingFlag);
    }

    public static Path find(Location start, int moverSize, Node destination, boolean near, Pathfinder finder, ClipMaskSupplier clipMaskSupplier) {
        if (destination instanceof Scenery) {
            Scenery scenery = (Scenery) destination;
            int type = scenery.getType();
            int rotation = scenery.getRotation();
            if (type == 10 || type == 11 || type == 22) {
                int sizeX = scenery.getDefinition().sizeX;
                int sizeY = scenery.getDefinition().sizeY;
                if (rotation % 2 != 0) {
                    sizeX = scenery.getDefinition().sizeY;
                    sizeY = scenery.getDefinition().sizeX;
                }
                int walkingFlag = scenery.getDefinition().getWalkingFlag();
                if (rotation != 0) {
                    walkingFlag = (walkingFlag << rotation & 0xf) + (walkingFlag >> 4 - rotation);
                }
                return finder.find(start, moverSize, destination.getLocation(), sizeX, sizeY, 0, 0, walkingFlag, near, clipMaskSupplier);
            }
            return finder.find(start, moverSize, destination.getLocation(), 0, 0, rotation, 1 + type, 0, near, clipMaskSupplier);
        }
        int size = 0;
        if (destination instanceof Entity) {
            size = destination.size();
        } else if (destination instanceof GroundItem && !RegionManager.isTeleportPermitted(destination.getLocation())) {
            size = 1;
        }
        return finder.find(start, moverSize, destination.getLocation(), size, size, 0, 0, 0, near, clipMaskSupplier);
    }

    public static boolean canDecorationInteract(int curX, int curY, int size, int destX, int destY, int rotation, int type, int z, ClipMaskSupplier clipMaskSupplier) {
        if (size != 1) {
            if (destX >= curX && destX <= (curX + size) - 1 && destY <= (destY + size) - 1) {
                return true;
            }
        } else if (destX == curX && curY == destY) {
            return true;
        }
        if (size == 1) {
            int flag = clipMaskSupplier.getClippingFlag(z, curX, curY);
            if (type == 6 || type == 7) {
                if (type == 7) {
                    rotation = rotation + 2 & 0x3;
                }
                if (rotation == 0) {
                    if (curX == 1 + destX && curY == destY && (0x80 & flag) == 0) {
                        return true;
                    }
                    if (destX == curX && curY == destY - 1 && (flag & 0x2) == 0) {
                        return true;
                    }
                } else if (rotation == 1) {
                    if (curX == destX - 1 && curY == destY && (0x8 & flag) == 0) {
                        return true;
                    }
                    if (curX == destX && curY == destY - 1 && (flag & 0x2) == 0) {
                        return true;
                    }
                } else if (rotation == 2) {
                    if (destX - 1 == curX && destY == curY && (flag & 0x8) == 0) {
                        return true;
                    }
                    if (destX == curX && destY + 1 == curY && (0x20 & flag) == 0) {
                        return true;
                    }
                } else if (rotation == 3) {
                    if (destX + 1 == curX && curY == destY && (0x80 & flag) == 0) {
                        return true;
                    }
                    if (destX == curX && curY == destY + 1 && (0x20 & flag) == 0) {
                        return true;
                    }
                }
            }
            if (type == 8) {
                if (destX == curX && curY == destY + 1 && (flag & 0x20) == 0) {
                    return true;
                }
                if (destX == curX && -1 + destY == curY && (0x2 & flag) == 0) {
                    return true;
                }
                if (curX == destX - 1 && curY == destY && (0x8 & flag) == 0) {
                    return true;
                }
                if (curX == destX + 1 && curY == destY && (flag & 0x80) == 0) {
                    return true;
                }
            }
        } else {
            int cornerX = curX + size - 1;
            int cornerY = curY + size - 1;
            if (type == 6 || type == 7) {
                if (type == 7) {
                    rotation = 0x3 & 2 + rotation;
                }
                if (rotation == 0) {
                    if (destX + 1 == curX && destY >= curY && destY <= cornerY && (clipMaskSupplier.getClippingFlag(z, curX, destY) & 0x80) == 0) {
                        return true;
                    }
                    if (destX >= curX && destX <= cornerX && destY - size == curY && (0x2 & clipMaskSupplier.getClippingFlag(z, destX, cornerY)) == 0) {
                        return true;
                    }
                } else if (rotation == 1) {
                    if (-size + destX == curX && destY >= curY && cornerY >= destY && (clipMaskSupplier.getClippingFlag(z, cornerX, destY) & 0x8) == 0) {
                        return true;
                    }
                    if (curX <= destX && cornerX >= destX && -size + destY == curY && (clipMaskSupplier.getClippingFlag(z, destX, cornerY) & 0x2) == 0) {
                        return true;
                    }
                } else if (rotation == 2) {
                    if (curX == destX - size && curY <= destY && destY <= cornerY && (0x8 & clipMaskSupplier.getClippingFlag(z, cornerX, destY)) == 0) {
                        return true;
                    }
                    if (curX <= destX && cornerX >= destX && destY + 1 == curY && (0x20 & clipMaskSupplier.getClippingFlag(z, destX, curY)) == 0) {
                        return true;
                    }
                } else if (rotation == 3) {
                    if (1 + destX == curX && curY <= destY && destY <= cornerY && (0x80 & clipMaskSupplier.getClippingFlag(z, curX, destY)) == 0) {
                        return true;
                    }
                    if (destX >= curX && destX <= cornerX && 1 + destY == curY && (clipMaskSupplier.getClippingFlag(z, destX, curY) & 0x20) == 0) {
                        return true;
                    }
                }
            }
            if (type == 8) {
                if (curX <= destX && destX <= cornerX && 1 + destY == curY && (clipMaskSupplier.getClippingFlag(z, destX, curY) & 0x20) == 0) {
                    return true;
                }
                if (curX <= destX && destX <= cornerX && curY == -size + destY && (0x2 & clipMaskSupplier.getClippingFlag(z, destX, cornerY)) == 0) {
                    return true;
                }
                if (curX == -size + destX && destY >= curY && destY <= cornerY && (0x8 & clipMaskSupplier.getClippingFlag(z, cornerX, destY)) == 0) {
                    return true;
                }
                if (1 + destX == curX && curY <= destY && cornerY >= destY && (clipMaskSupplier.getClippingFlag(z, curX, destY) & 0x80) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canDoorInteract(int curX, int curY, int size, int destX, int destY, int type, int rotation, int z, ClipMaskSupplier clipMaskSupplier) {
        if (size != 1) {
            if (destX >= curX && destX <= size + curX - 1 && destY <= destY + size - 1) {
                return true;
            }
        } else if (curX == destX && destY == curY) {
            return true;
        }

        if (size == 1) {
            if (type == 0) {
                if (rotation == 0) {
                    if (curX == destX - 1 && destY == curY) {
                        return true;
                    }
                    if (destX == curX && 1 + destY == curY && (0x12c0120 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == destX && destY - 1 == curY && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x12c0102) == 0) {
                        return true;
                    }
                } else if (rotation == 1) {
                    if (curX == destX && destY + 1 == curY) {
                        return true;
                    }
                    if (curX == destX - 1 && curY == destY && (0x12c0108 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == 1 + destX && destY == curY && (0x12c0180 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                } else if (rotation == 2) {
                    if (1 + destX == curX && destY == curY) {
                        return true;
                    }
                    if (destX == curX && 1 + destY == curY && (0x12c0120 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == destX && curY == destY - 1 && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x12c0102) == 0) {
                        return true;
                    }
                } else if (rotation == 3) {
                    if (curX == destX && -1 + destY == curY) {
                        return true;
                    }
                    if (curX == -1 + destX && destY == curY && (0x12c0108 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == 1 + destX && destY == curY && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x12c0180) == 0) {
                        return true;
                    }
                }
            } else if (type == 2) {
                if (rotation == 0) {
                    if (destX - 1 == curX && curY == destY) {
                        return true;
                    }
                    if (destX == curX && curY == 1 + destY) {
                        return true;
                    }
                    if (curX == destX + 1 && curY == destY && (0x12c0180 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == destX && destY - 1 == curY && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x12c0102) == 0) {
                        return true;
                    }
                } else if (rotation == 1) {
                    if (curX == destX - 1 && curY == destY && (0x12c0108 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == destX && curY == 1 + destY) {
                        return true;
                    }
                    if (1 + destX == curX && curY == destY) {
                        return true;
                    }
                    if (curX == destX && destY - 1 == curY && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x12c0102) == 0) {
                        return true;
                    }
                } else if (rotation == 2) {
                    if (destX - 1 == curX && destY == curY && (0x12c0108 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (destX == curX && 1 + destY == curY && (0x12c0120 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (1 + destX == curX && curY == destY) {
                        return true;
                    }
                    if (curX == destX && curY == destY - 1) {
                        return true;
                    }
                } else if (rotation == 3) {
                    if (destX - 1 == curX && curY == destY) {
                        return true;
                    }
                    if (destX == curX && curY == destY + 1 && (0x12c0120 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                        return true;
                    }
                    if (curX == 1 + destX && curY == destY && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x12c0180) == 0) {
                        return true;
                    }
                    if (destX == curX && destY - 1 == curY) {
                        return true;
                    }
                }
            } else if (type == 9) {
                if (curX == destX && curY == destY + 1 && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x20) == 0) {
                    return true;
                }
                if (curX == destX && curY == destY - 1 && (clipMaskSupplier.getClippingFlag(z, curX, curY) & 0x2) == 0) {
                    return true;
                }
                if (curX == destX - 1 && curY == destY && (0x8 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                    return true;
                }
                if (destX + 1 == curX && curY == destY && (0x80 & clipMaskSupplier.getClippingFlag(z, curX, curY)) == 0) {
                    return true;
                }
            }
        } else {
            int cornerX = curX - (1 - size);
            int cornerY = -1 + curY + size;
            if (type == 0) {
                if (rotation == 0) {
                    if (destX - size == curX && destY >= curY && destY <= cornerY) {
                        return true;
                    }
                    if (destX >= curX && cornerX >= destX && curY == 1 + destY && (clipMaskSupplier.getClippingFlag(z, destX, curY) & 0x12c0120) == 0) {
                        return true;
                    }
                    if (destX >= curX && cornerX >= destX && destY - size == curY && (clipMaskSupplier.getClippingFlag(z, destX, cornerY) & 0x12c0102) == 0) {
                        return true;
                    }
                } else if (rotation == 1) {
                    if (destX >= curX && cornerX >= destX && destY + 1 == curY) {
                        return true;
                    }
                    if (curX == -size + destX && destY >= curY && cornerY >= destY && (0x12c0108 & clipMaskSupplier.getClippingFlag(z, cornerX, destY)) == 0) {
                        return true;
                    }
                    if (curX == 1 + destX && destY >= curY && cornerY >= destY && (clipMaskSupplier.getClippingFlag(z, curX, destY) & 0x12c0180) == 0) {
                        return true;
                    }
                } else if (rotation == 2) {
                    if (curX == 1 + destX && curY <= destY && destY <= cornerY) {
                        return true;
                    }
                    if (curX <= destX && cornerX >= destX && destY + 1 == curY && (0x12c0120 & clipMaskSupplier.getClippingFlag(z, destX, curY)) == 0) {
                        return true;
                    }
                    if (destX >= curX && destX <= cornerX && destY - size == curY && (0x12c0102 & clipMaskSupplier.getClippingFlag(z, destX, cornerY)) == 0) {
                        return true;
                    }
                } else if (rotation == 3) {
                    if (curX <= destX && destX <= cornerX && curY == -size + destY) {
                        return true;
                    }
                    if (-size + destX == curX && curY <= destY && destY <= cornerY && (clipMaskSupplier.getClippingFlag(z, cornerX, destY) & 0x12c0108) == 0) {
                        return true;
                    }
                    if (1 + destX == curX && curY <= destY && cornerY >= destY && (clipMaskSupplier.getClippingFlag(z, curX, destY) & 0x12c0180) == 0) {
                        return true;
                    }
                }
            }
            if (type == 2) {
                if (rotation == 0) {
                    if (destX - size == curX && curY <= destY && destY <= cornerY) {
                        return true;
                    }
                    if (curX <= destX && destX <= cornerX && curY == 1 + destY) {
                        return true;
                    }
                    if (curX == 1 + destX && curY <= destY && destY <= cornerY && (0x12c0180 & clipMaskSupplier.getClippingFlag(z, curX, destY)) == 0) {
                        return true;
                    }
                    if (curX <= destX && cornerX >= destX && -size + destY == curY && (clipMaskSupplier.getClippingFlag(z, destX, cornerY) & 0x12c0102) == 0) {
                        return true;
                    }
                } else if (rotation == 1) {
                    if (-size + destX == curX && destY >= curY && destY <= cornerY && (clipMaskSupplier.getClippingFlag(z, cornerX, destY) & 0x12c0108) == 0) {
                        return true;
                    }
                    if (destX >= curX && cornerX >= destX && curY == 1 + destY) {
                        return true;
                    }
                    if (destX + 1 == curX && curY <= destY && destY <= cornerY) {
                        return true;
                    }
                    if (destX >= curX && cornerX >= destX && destY + -size == curY && (0x12c0102 & clipMaskSupplier.getClippingFlag(z, destX, cornerY)) == 0) {
                        return true;
                    }
                } else if (rotation == 2) {
                    if (curX == destX - size && curY <= destY && cornerY >= destY && (clipMaskSupplier.getClippingFlag(z, cornerX, destY) & 0x12c0108) == 0) {
                        return true;
                    }
                    if (destX >= curX && destX <= cornerX && 1 + destY == curY && (0x12c0120 & clipMaskSupplier.getClippingFlag(z, destX, curY)) == 0) {
                        return true;
                    }
                    if (1 + destX == curX && destY >= curY && cornerY >= destY) {
                        return true;
                    }
                    if (curX <= destX && destX <= cornerX && curY == -size + destY) {
                        return true;
                    }
                } else if (rotation == 3) {
                    if (destX + -size == curX && destY >= curY && destY <= cornerY) {
                        return true;
                    }
                    if (curX <= destX && cornerX >= destX && curY == 1 + destY && (clipMaskSupplier.getClippingFlag(z, destX, curY) & 0x12c0120) == 0) {
                        return true;
                    }
                    if (1 + destX == curX && destY >= curY && cornerY >= destY && (0x12c0180 & clipMaskSupplier.getClippingFlag(z, curX, destY)) == 0) {
                        return true;
                    }
                    if (destX >= curX && destX <= cornerX && curY == -size + destY) {
                        return true;
                    }
                }
            }
            if (type == 9) {
                if (destX >= curX && destX <= cornerX && curY == 1 + destY && (clipMaskSupplier.getClippingFlag(z, destX, curY) & 0x12c0120) == 0) {
                    return true;
                }
                if (destX >= curX && cornerX >= destX && curY == -size + destY && (0x12c0102 & clipMaskSupplier.getClippingFlag(z, destX, cornerY)) == 0) {
                    return true;
                }
                if (-size + destX == curX && destY >= curY && cornerY >= destY && (0x12c0108 & clipMaskSupplier.getClippingFlag(z, cornerX, destY)) == 0) {
                    return true;
                }
                if (curX == destX + 1 && destY >= curY && cornerY >= destY && (clipMaskSupplier.getClippingFlag(z, curX, destY) & 0x12c0180) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isStandingIn(int x, int y, int moverSizeX, int moverSizeY, int destX, int destY, int sizeX, int sizeY) {
        if (x >= sizeX + destX || moverSizeX + x <= destX) {
            return false;
        }
        if (destY + sizeY <= y || y + moverSizeY <= destY) {
            return false;
        }
        return true;
    }

    public static boolean canInteract(int x, int y, int moverSize, int destX, int destY, int sizeX, int sizeY, int walkFlag, int z, ClipMaskSupplier clipMaskSupplier) {
        if (moverSize > 1) {
            return isStandingIn(x, y, moverSize, moverSize, destX, destY, sizeX, sizeY) || canInteractSized(x, y, moverSize, moverSize, destX, destY, sizeX, sizeY, walkFlag, z);
        }
        int flag = clipMaskSupplier.getClippingFlag(z, x, y);
        int cornerX = destX + sizeX - 1;
        int cornerY = destY + sizeY - 1;
        if (destX <= x && cornerX >= x && y >= destY && y <= cornerY) {
            return true;
        }
        if (x == destX - 1 && destY <= y && y <= cornerY && (0x8 & flag) == 0 && (0x8 & walkFlag) == 0) {
            return true;
        }
        if (x == cornerX + 1 && destY <= y && cornerY >= y && (flag & 0x80) == 0 && (0x2 & walkFlag) == 0) {
            return true;
        }
        if (y == destY - 1 && destX <= x && cornerX >= x && (0x2 & flag) == 0 && (0x4 & walkFlag) == 0) {
            return true;
        }
        if (y == cornerY + 1 && destX <= x && cornerX >= x && (flag & 0x20) == 0 && (0x1 & walkFlag) == 0) {
            return true;
        }
        return false;
    }

    public static boolean canInteractSized(int curX, int curY, int moverSizeX, int moverSizeY, int destX, int destY, int sizeX, int sizeY, int walkingFlag, int z) {
        int fromCornerY = curY + moverSizeY;
        int fromCornerX = curX + moverSizeX;
        int toCornerX = sizeX + destX;
        int toCornerY = sizeY + destY;
        if (destX <= curX && curX < toCornerX) {
            if (destY == fromCornerY && (walkingFlag & 0x4) == 0) {
                int x = curX;
                for (int endX = toCornerX < fromCornerX ? toCornerX : fromCornerX; endX > x; x++) {
                    if ((RegionManager.getClippingFlag(z, x, -1 + fromCornerY) & 0x2) == 0) {
                        return true;
                    }
                }
            } else if (toCornerY == curY && (walkingFlag & 0x1) == 0) {
                int x = curX;
                for (int endX = fromCornerX <= toCornerX ? fromCornerX : toCornerX; x < endX; x++) {
                    if ((RegionManager.getClippingFlag(z, x, curY) & 0x20) == 0) {
                        return true;
                    }
                }
            }
        } else if (destX < fromCornerX && toCornerX >= fromCornerX) {
            if (fromCornerY == destY && (0x4 & walkingFlag) == 0) {
                for (int x = destX; fromCornerX > x; x++) {
                    if ((RegionManager.getClippingFlag(z, x, -1 + (fromCornerY)) & 0x2) == 0) {
                        return true;
                    }
                }
            } else if (toCornerY == curY && (0x1 & walkingFlag) == 0) {
                for (int x = destX; fromCornerX > x; x++) {
                    if ((RegionManager.getClippingFlag(z, x, curY) & 0x20) == 0) {
                        return true;
                    }
                }
            }
        } else if (curY < destY || curY >= toCornerY) {
            if (fromCornerY > destY && toCornerY >= fromCornerY) {
                if (fromCornerX == destX && (walkingFlag & 0x8) == 0) {
                    for (int y = destY; y < fromCornerY; y++) {
                        if ((RegionManager.getClippingFlag(z, -1 + fromCornerX, y) & 0x8) == 0) {
                            return true;
                        }
                    }
                } else if (curX == toCornerX && (0x2 & walkingFlag) == 0) {
                    for (int y = destY; fromCornerY > y; y++) {
                        if ((RegionManager.getClippingFlag(z, curX, y) & 0x80) == 0) {
                            return true;
                        }
                    }
                }
            }
        } else if (destX != fromCornerX || (0x8 & walkingFlag) != 0) {
            if (curX == toCornerX && (walkingFlag & 0x2) == 0) {
                int y = curY;
                for (int endY = fromCornerY <= toCornerY ? fromCornerY : toCornerY; y < endY; y++) {
                    if ((0x80 & RegionManager.getClippingFlag(z, curX, y)) == 0) {
                        return true;
                    }
                }
            }
        } else {
            int y = curY;
            for (int endY = fromCornerY > toCornerY ? toCornerY : fromCornerY; endY > y; y++) {
                if ((RegionManager.getClippingFlag(z, fromCornerX - 1, y) & 0x8) == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
