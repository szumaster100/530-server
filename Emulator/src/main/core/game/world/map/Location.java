package core.game.world.map;

import core.api.utils.Vector;
import core.game.interaction.DestinationFlag;
import core.game.node.Node;
import core.game.world.map.path.Path;
import core.game.world.map.path.Pathfinder;
import core.tools.RandomFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Location extends Node {

	private int x;

	private int y;

	private int z;

	public Location(int x, int y, int z) {
		super(null, null);
		super.destinationFlag = DestinationFlag.LOCATION;
		this.x = x;
		this.y = y;
		if (z < 0) {
			z += 4;
		}
		this.z = z;
	}

	public Location(int x, int y) {
		this(x, y, 0);
	}

	public Location(int x, int y, int z, int randomizer) {
		this(x + RandomFunction.getRandom(randomizer), y + RandomFunction.getRandom(randomizer), z);
	}

	public static Location create(int x, int y, int z) {
		return new Location(x, y, z);
	}

	public static Location create(int x, int y) {
		return new Location(x, y, 0);
	}

	public static Location create(Location location) {
		return create(location.getX(), location.getY(), location.getZ());
	}

	public static Location getDelta(Location location, Location other) {
		return Location.create(other.x - location.x, other.y - location.y, other.z - location.z);
	}

	public static Location getRandomLocation(Location main, int radius, boolean reachable) {
		Location location = RegionManager.getTeleportLocation(main, radius);
		if (!reachable) {
			return location;
		}
		Path path = Pathfinder.find(main, location, false, Pathfinder.DUMB);
		if (!path.isSuccessful()) {
			location = main;
			if (!path.getPoints().isEmpty()) {
				Point p = path.getPoints().getLast();
				location = Location.create(p.getX(), p.getY(), main.getZ());
			}
		}
		return location;
	}

	@Override
	public Location getLocation() {
		return this;
	}

	public boolean isNextTo(Node node) {
		Location l = node.getLocation();
		if (l.getY() == y) {
			return l.getX() - x == -1 || l.getX() - x == 1;
		}
		if (l.getX() == x) {
			return l.getY() - y == -1 || l.getY() - y == 1;
		}
		return false;
	}

	public int getRegionId() {
		return (x >> 6) << 8 | (y >> 6);
	}

	public boolean isInRegion(int region) {
        return getRegionId() == region;
    }

	public Location transform(Direction dir) {
		return transform(dir, 1);
	}

	public Location transform(Direction dir, int steps) {
		return new Location(x + (dir.getStepX() * steps), y + (dir.getStepY() * steps), this.z);
	}

	public Location transform(Location l) {
		return new Location(x + l.getX(), y + l.getY(), this.z + l.getZ());
	}

	public Location transform(int diffX, int diffY, int z) {
		return new Location(x + diffX, y + diffY, this.z + z);
	}

	public boolean withinDistance(Location other) {
		return withinDistance(other, MapDistance.RENDERING.getDistance());
	}

	public boolean withinDistance(Location other, int dist) {
	    if (other.z != z) {
	        return false;
        }

		int a = (other.x - x);
		int b = (other.y - y);
		double product = Math.sqrt((a*a) + (b*b));
		return product <= dist;
	}

	public boolean withinMaxnormDistance(Location other, int dist) {
	    if (other.z != z) {
	        return false;
        }

		int a = Math.abs(other.x - x);
		int b = Math.abs(other.y - y);
		double max = Math.max(a, b);
		return max <= dist;
	}

	public double getDistance(Location other) {
		int xdiff = this.getX() - other.getX();
		int ydiff = this.getY() - other.getY();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public static double getDistance(Location first, Location second) {
		int xdiff = first.getX() - second.getX();
		int ydiff = first.getY() - second.getY();
		return Math.sqrt(xdiff * xdiff + ydiff * ydiff);
	}

	public ArrayList<Location> getSurroundingTiles() {
		ArrayList<Location> locs = new ArrayList<>();

		locs.add(transform(-1,-1,0));//SW
		locs.add(transform(0,-1,0)); //S
		locs.add(transform(1,-1,0)); //SE
		locs.add(transform(1,0,0)); //E
		locs.add(transform(1,1,0)); //NE
		locs.add(transform(0,1,0)); //N
		locs.add(transform(-1,1,0));//NW
		locs.add(transform(-1,0,0));//W

		return locs;
	}

	public ArrayList<Location> getCardinalTiles() {
		ArrayList<Location> locs = new ArrayList<>();

		locs.add(transform(-1, 0, 0));
		locs.add(transform(0, -1, 0));
		locs.add(transform(1, 0, 0));
		locs.add(transform(0, 1, 0));
		return locs;
	}

	public ArrayList<Location> get3x3Tiles() {
		ArrayList<Location> locs = new ArrayList<>();
		locs.add(transform(0,0,0)); //Center
		locs.add(transform(0,1,0)); //N
		locs.add(transform(1,1,0)); //NE
		locs.add(transform(1,0,0)); //E
		locs.add(transform(1,-1,0)); //SE
		locs.add(transform(0,-1,0)); //S
		locs.add(transform(-1,-1,0));//SW
		locs.add(transform(-1,0,0));//W
		locs.add(transform(-1,1,0));//NW
		return locs;
	}

	public int getChunkOffsetX() {
		int x = getLocalX();
		//return x - ((x / RegionChunk.SIZE) * RegionChunk.SIZE);
        return x & 7;
	}

	public int getChunkOffsetY() {
		int y = getLocalY();
		//return y - ((y / RegionChunk.SIZE) * RegionChunk.SIZE);
        return y & 7;
	}

	public Location getChunkBase() {
		return create(getRegionX() << 3, getRegionY() << 3, z);
	}

	public int getRegionX() {
		return x >> 3;
	}

	public int getRegionY() {
		return y >> 3;
	}

	public int getLocalX() {
		return x & 63;
	}

	public int getLocalY() {
		return y & 63;
	}

	public int getSceneX() {
		return x - ((getRegionX() - 6) << 3);
	}

	public int getSceneY() {
		return y - ((getRegionY() - 6) << 3);
	}

	public int getSceneX(Location loc) {
		return x - ((loc.getRegionX() - 6) << 3);
	}

	public int getSceneY(Location loc) {
		return y - ((loc.getRegionY() - 6) << 3);
	}

	public int getChunkX() {
		return getLocalX() >> 3;
	}

	public int getChunkY() {
		return getLocalY() >> 3;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Location)) {
			return false;
		}
		Location loc = (Location) other;
		return loc.x == x && loc.y == y && loc.z == z;
	}

	public boolean equals(int x, int y, int z) {
		return equals(new Location(x, y, z));
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + ", " + z + "]";
	}

	public static Location fromString(String locString) {
		String trimmed = locString.replace("[", "").replace("]", "");
		String[] tokens = trimmed.split(",");
		return Location.create(
				Integer.parseInt(tokens[0].trim()),
				Integer.parseInt(tokens[1].trim()),
				Integer.parseInt(tokens[2].trim())
		);
	}

	@Override
	public int hashCode() {
		return z << 30 | x << 15 | y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z % 4;
	}

	public void setZ(int z) {
		this.z = z;
	}

	@NotNull
	public List<Location> getStepComponents(Direction dir) {
		List<Location> output = new ArrayList<>(2);
		int stepX = dir.getStepX();
		int stepY = dir.getStepY();

		if (stepX != 0) output.add(transform(stepX, 0, 0));
		if (stepY != 0) output.add(transform(0, stepY, 0));
		return output;
	}

	public Direction deriveDirection(Location location) {
		int diffX = location.x - this.x;
		int diffY = location.y - this.y;

		diffX = diffX >= 0 ? Math.min(diffX, 1) : -1;
		diffY = diffY >= 0 ? Math.min(diffY, 1) : -1;

		StringBuilder sb = new StringBuilder();

		if (diffY != 0) {
			if (diffY > 0) {
				sb.append("NORTH");
			} else {
				sb.append("SOUTH");
			}
		}

		if (diffX != 0) {
			if (sb.length() > 0) sb.append("_");
			if (diffX > 0) {
				sb.append("EAST");
			} else {
				sb.append("WEST");
			}
		}

		if (sb.length() == 0) return null;
		return Direction.valueOf(sb.toString());
	}

        public Location transform (Vector vector) {
            return Location.create(this.x + (int) Math.floor(vector.getX()), this.y + (int) Math.floor(vector.getY()));
        }
}
