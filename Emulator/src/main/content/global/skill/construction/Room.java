package content.global.skill.construction;

import core.game.node.entity.player.Player;
import core.game.node.scenery.Constructed;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.world.map.*;
import core.tools.Log;

import static core.api.ContentAPIKt.log;

public final class Room {

    public static final int CHAMBER = 0x0;

    public static final int ROOF = 0x1;

    public static final int DUNGEON = 0x2;

    public static final int LAND = 0x4;

    private RoomProperties properties;

    private RegionChunk chunk;

    private Hotspot[] hotspots;

    private Direction rotation = Direction.NORTH;

    public Room(RoomProperties properties) {
        this.properties = properties;
    }

    public static Room create(Player player, RoomProperties properties) {
        Room room = new Room(properties);
        room.configure(player.getHouseManager().getStyle());
        return room;
    }

    public void configure(HousingStyle style) {
        this.hotspots = new Hotspot[properties.hotspots.length];
        for (int i = 0; i < hotspots.length; i++) {
            hotspots[i] = properties.hotspots[i].copy();
        }
        decorate(style);
    }

    public void decorate(HousingStyle style) {
        Region region = RegionManager.forId(style.getRegionId());
        Region.load(region, true);
        chunk = region.getPlanes()[style.getPlane()].getRegionChunk(properties.chunkX, properties.chunkY);
    }

    public Hotspot getHotspot(BuildHotspot hotspot) {
        for (Hotspot h : hotspots) {
            if (h.getHotspot() == hotspot) {
                return h;
            }
        }
        return null;
    }

    public boolean isBuilt(BuildHotspot hotspot) {
        Hotspot h = getHotspot(hotspot);
        return h != null && h.getDecorationIndex() > -1;
    }

    public void loadDecorations(int housePlane, BuildRegionChunk chunk, HouseManager house) {
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot spot = hotspots[i];
            int x = spot.getChunkX();
            int y = spot.getChunkY();
            if (spot.getHotspot() == null) {
                continue;
            }
            int index = chunk.getIndex(x, y, spot.getHotspot().getObjectId(house.getStyle()));
            Scenery[][] sceneries = chunk.getObjects(index);
            Scenery scenery = sceneries[x][y];
            if (scenery != null && scenery.getId() == spot.getHotspot().getObjectId(house.getStyle())) {
                if (spot.getDecorationIndex() > -1 && spot.getDecorationIndex() < spot.getHotspot().getDecorations().length) {
                    int id = spot.getHotspot().getDecorations()[spot.getDecorationIndex()].getObjectId(house.getStyle());
                    if (spot.getHotspot().getType() == BuildHotspotType.CREST) {
                        id += house.getCrest().ordinal();
                    }
                    SceneryBuilder.replace(scenery, scenery.transform(id, scenery.getRotation(), chunk.getCurrentBase().transform(x, y, 0)));
                } else if (scenery.getId() == BuildHotspot.WINDOW.getObjectId(house.getStyle()) || (!house.isBuildingMode() && scenery.getId() == BuildHotspot.CHAPEL_WINDOW.getObjectId(house.getStyle()))) {
                    SceneryBuilder.replace(scenery, scenery.transform(house.getStyle().getWindow().getObjectId(house.getStyle()), scenery.getRotation(), scenery.getType()));
                }
                int[] pos = RegionChunk.getRotatedPosition(x, y, scenery.getSizeX(), scenery.getSizeY(), 0, rotation.toInteger());
                spot.setCurrentX(pos[0]);
                spot.setCurrentY(pos[1]);
            }
        }
        if (rotation != Direction.NORTH && chunk.getRotation() == 0) {
            chunk.rotate(rotation);
        }
        if (!house.isBuildingMode()) {
            placeDoors(housePlane, house, chunk);
            removeHotspots(housePlane, house, chunk);
        }
    }

    private void removeHotspots(int housePlane, HouseManager house, BuildRegionChunk chunk) {
        if (properties.isRoof()) return;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                for (int i = 0; i < BuildRegionChunk.ARRAY_SIZE; i++) {
                    Scenery scenery = chunk.get(x, y, i);
                    if (scenery != null) {
                        boolean isBuilt = scenery instanceof Constructed;
                        boolean isWall = scenery.getId() == 13065 || scenery.getId() == house.getStyle().getWallId();
                        boolean isDoor = scenery.getId() == house.getStyle().getDoorId() || scenery.getId() == house.getStyle().getSecondDoorId();
                        if (!isBuilt && !isWall && !isDoor) {
                            SceneryBuilder.remove(scenery);
                            chunk.remove(scenery);
                        }
                    }
                }
            }
        }
    }

    private void placeDoors(int housePlane, HouseManager house, BuildRegionChunk chunk) {
        Room[][][] rooms = house.getRooms();
        int rx = chunk.getCurrentBase().getChunkX();
        int ry = chunk.getCurrentBase().getChunkY();
        for (int i = 0; i < BuildRegionChunk.ARRAY_SIZE; i++) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    Scenery scenery = chunk.get(x, y, i);
                    if (scenery != null && BuildingUtils.isDoorHotspot(scenery)) {
                        boolean edge = false;
                        Room otherRoom = null;
                        switch (scenery.getRotation()) {
                            case 0:
                                edge = rx == 0;
                                otherRoom = edge ? null : rooms[housePlane][rx - 1][ry];
                                break;
                            case 1:
                                edge = ry == 7;
                                otherRoom = edge ? null : rooms[housePlane][rx][ry + 1];
                                break;
                            case 2:
                                edge = rx == 7;
                                otherRoom = edge ? null : rooms[housePlane][rx + 1][ry];
                                break;
                            case 3:
                                edge = ry == 0;
                                otherRoom = edge ? null : rooms[housePlane][rx][ry - 1];
                                break;
                            default:
                                log(this.getClass(), Log.ERR, "Impossible rotation when placing doors??");
                        }
                        int replaceId = getReplaceId(housePlane, house, this, edge, otherRoom, scenery);
                        if (replaceId == -1) {
                            continue;
                        }
                        SceneryBuilder.replace(scenery, scenery.transform(replaceId));
                    }
                }
            }
        }
    }

    private int getReplaceId(int housePlane, HouseManager house, Room room, boolean edge, Room otherRoom, Scenery scenery) {
        boolean thisOutside = !room.getProperties().isChamber();
        if (edge && thisOutside) {

            return -1;
        }
        if (!edge) {
            boolean otherOutside = otherRoom == null || !otherRoom.getProperties().isChamber();
            if (thisOutside == otherOutside) {

                if (otherRoom == null) {
                    return -1;
                }
                boolean exit = otherRoom.getExits()[scenery.getRotation()];
                if (exit) {
                    return -1;
                }
            }
            if (thisOutside != otherOutside && housePlane == 0) {

                if (thisOutside) {
                    return -1;
                }
                return scenery.getId() % 2 != 0 ? house.getStyle().getDoorId() : house.getStyle().getSecondDoorId();
            }
        }
        return room.getProperties().isDungeon() ? 13065 : house.getStyle().getWallId();
    }

    public void setAllDecorationIndex(int index, BuildHotspot hs) {
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot h = hotspots[i];
            if (h.getHotspot() == hs) {
                h.setDecorationIndex(index);
            }
        }
    }

    public Hotspot getStairs() {
        for (Hotspot h : hotspots) {
            if (h.getHotspot().getType() == BuildHotspotType.STAIRCASE
                || h.getHotspot() == BuildHotspot.LADDER || h.getHotspot() == BuildHotspot.TRAPDOOR
                || (h.getHotspot() == BuildHotspot.CENTREPIECE_1 && h.getDecorationIndex() == 4)
                || (h.getHotspot() == BuildHotspot.CENTREPIECE_2 && h.getDecorationIndex() == 2)) {
                return h;
            }
        }
        return null;
    }

    public boolean[] getExits() {
        return getExits(rotation);
    }

    public boolean[] getExits(Direction rotation) {
        boolean[] exits = properties.getExits();
        if (chunk.getRotation() != rotation.toInteger()) {
            boolean[] exit = new boolean[exits.length];
            int offset = rotation.toInteger() - chunk.getRotation();
            for (int i = 0; i < 4; i++) {
                exit[(i + offset) % 4] = exits[i];
            }
            return exit;
        }
        return exits;
    }

    public Hotspot getHotspot(BuildHotspot build, int x, int y) {
        for (int i = 0; i < getHotspots().length; i++) {
            Hotspot h = getHotspots()[i];
            if (h.getCurrentX() == x && h.getCurrentY() == y && h.getHotspot() == build) {
                return h;
            }
        }
        return null;
    }

    public void updateProperties(Player player, RoomProperties properties) {
        this.properties = properties;
        decorate(player.getHouseManager().getStyle());
        if (hotspots.length != properties.hotspots.length) {
            return;
        }
        for (int i = 0; i < hotspots.length; i++) {
            Hotspot h = hotspots[i];
            Hotspot hs = hotspots[i] = properties.hotspots[i].copy();
            hs.setCurrentX(h.getCurrentX());
            hs.setCurrentY(h.getCurrentY());
            hs.setDecorationIndex(h.getDecorationIndex());
        }
    }

    public RegionChunk getChunk() {
        return chunk;
    }

    public void setChunk(RegionChunk chunk) {
        this.chunk = chunk;
    }

    public Hotspot[] getHotspots() {
        return hotspots;
    }

    public void setHotspots(Hotspot[] hotspots) {
        this.hotspots = hotspots;
    }

    public RoomProperties getProperties() {
        return properties;
    }

    public void setRotation(Direction rotation) {
        this.rotation = rotation;
    }

    public Direction getRotation() {
        return rotation;
    }

}
