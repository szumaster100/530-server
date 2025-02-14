package core.game.world.map.zone;

import core.game.node.Node;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.tools.RandomFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class ZoneBorders {

    private final int southWestX;

    private final int southWestY;

    private final int northEastX;

    private final int northEastY;

    private int plane = 0;

    private List<ZoneBorders> exceptions;

    private boolean zeroPlaneCheck;

    public ZoneBorders(int x1, int y1, int x2, int y2) {
        this.southWestX = Math.min(x1, x2);
        this.southWestY = Math.min(y1, y2);
        this.northEastX = Math.max(x1, x2);
        this.northEastY = Math.max(y1, y2);
    }

    public ZoneBorders(int x1, int y1, int x2, int y2, int plane) {
        this.southWestX = Math.min(x1, x2);
        this.southWestY = Math.min(y1, y2);
        this.northEastX = Math.max(x1, x2);
        this.northEastY = Math.max(y1, y2);
        this.setPlane(plane);
    }

    public ZoneBorders(int x1, int y1, int x2, int y2, int plane, boolean zeroPlaneCheck) {
        this(x1, y1, x2, y2, plane);
        this.zeroPlaneCheck = zeroPlaneCheck;
    }

    public ZoneBorders(Location l1, Location l2) {
        this(l1.getX(), l1.getY(), l2.getX(), l2.getY(), l1.getZ());
    }

    public static ZoneBorders forRegion(int regionId) {
        int baseX = ((regionId >> 8) & 0xFF) << 6;
        int baseY = (regionId & 0xFF) << 6;
        int size = 64 - 1;
        return new ZoneBorders(baseX, baseY, baseX + size, baseY + size);
    }

    public boolean insideBorder(Location location) {
        return insideBorder(location.getX(), location.getY(), location.getZ());
    }

    public boolean insideBorder(Node node) {
        return insideBorder(node.getLocation());
    }

    public boolean insideBorder(int x, int y) {
        return insideBorder(x, y, 0);
    }

    public boolean insideBorder(int x, int y, int z) {
        if (zeroPlaneCheck ? z != plane : (plane != 0 && z != plane)) {
            return false;
        }
        if (southWestX <= x && southWestY <= y && northEastX >= x && northEastY >= y) {
            if (exceptions != null) {
                Object[] exceptArray = exceptions.toArray();
                int length = exceptArray.length;
                for (int i = 0; i < length; i++) {
                    ZoneBorders exception = (ZoneBorders) exceptArray[i];
                    if (exception.insideBorder(x, y, z)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    public List<Integer> getRegionIds() {
        List<Integer> regionIds = new ArrayList<>(20);
        int neX = (northEastX >> 6) + 1;
        int neY = (northEastY >> 6) + 1;
        for (int x = southWestX >> 6; x < neX; x++) {
            for (int y = southWestY >> 6; y < neY; y++) {
                int id = y | x << 8;
                regionIds.add(id);
            }
        }
        return regionIds;
    }

    public int getSouthWestX() {
        return southWestX;
    }

    public int getSouthWestY() {
        return southWestY;
    }

    public int getNorthEastX() {
        return northEastX;
    }

    public int getNorthEastY() {
        return northEastY;
    }

    public List<ZoneBorders> getExceptions() {
        return exceptions;
    }

    public Location getWeightedRandomLoc(int intensity) {
        int x = northEastX - southWestX == 0 ? southWestX : RandomFunction.normalRandDist(northEastX - southWestX, intensity) + southWestX;
        int y = northEastY - southWestY == 0 ? southWestY : RandomFunction.normalRandDist(northEastY - southWestY, intensity) + southWestY;
        return new Location(x, y);
    }

    public Location getRandomLoc() {
        int x = northEastX - southWestX == 0 ? southWestX : new Random().nextInt(northEastX - southWestX + 1) + southWestX;
        int y = northEastY - southWestY == 0 ? southWestY : new Random().nextInt(northEastY - southWestY + 1) + southWestY;
        return new Location(x, y, plane);
    }

    public Location getRandomWalkableLoc() {
        Location loc = getRandomLoc();
        int tries = 0; // prevent bad code from DOSing server.
        while (!RegionManager.isTeleportPermitted(loc) && tries < 20) {
            loc = getRandomLoc();
            tries += 1;
        }
        return loc;
    }

    public void addException(ZoneBorders exception) {
        if (exceptions == null) {
            this.exceptions = new ArrayList<>(20);
        }
        exceptions.add(exception);
    }

    @Override
    public String toString() {
        return "ZoneBorders [southWestX=" + southWestX + ", southWestY=" + southWestY + ", northEastX=" + northEastX + ", northEastY=" + northEastY + ", exceptions=" + exceptions + "]";
    }

    public int getPlane() {
        return plane;
    }

    public void setPlane(int plane) {
        this.plane = plane;
    }

    public boolean insideRegion(Node n) {
        return insideBorder(n.getLocation().getRegionX(), n.getLocation().getRegionY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ZoneBorders that = (ZoneBorders) o;
        return southWestX == that.southWestX && southWestY == that.southWestY && northEastX == that.northEastX && northEastY == that.northEastY && plane == that.plane && zeroPlaneCheck == that.zeroPlaneCheck && Objects.equals(exceptions, that.exceptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(southWestX, southWestY, northEastX, northEastY, plane, exceptions, zeroPlaneCheck);
    }
}