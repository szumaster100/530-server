package core.game.world.map;

import core.game.node.entity.player.Player;
import core.game.node.item.GroundItem;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.world.map.build.DynamicRegion;
import core.game.world.map.build.LandscapeParser;
import core.game.world.update.flag.UpdateFlag;
import core.net.packet.IoBuffer;
import core.net.packet.out.ClearScenery;
import core.net.packet.out.ConstructGroundItem;
import core.net.packet.out.ConstructScenery;
import core.net.packet.out.UpdateAreaPosition;
import core.tools.Log;

import java.util.ArrayList;
import java.util.List;

import static core.api.ContentAPIKt.log;

public class RegionChunk {

	public static final int SIZE = 8;

	protected Location base;

	protected Location currentBase;

	protected RegionPlane plane;

	protected List<GroundItem> items;

	protected Scenery[][] objects;

	protected int rotation;

	private List<UpdateFlag<?>> flags = new ArrayList<>(20);

	public RegionChunk(Location base, int rotation, RegionPlane plane) {
		this.base = base;
		this.currentBase = base;
		this.rotation = rotation;
		this.plane = plane;
		this.objects = new Scenery[SIZE][SIZE];
	}

	public BuildRegionChunk copy(RegionPlane plane) {
		return new BuildRegionChunk(base, rotation, plane, this.objects);
	}

	public void flag(UpdateFlag<?> flag) {
		flags.add(flag);
	}

	public void clear() {
		flags.clear();
		if (items != null && plane.getRegion() instanceof DynamicRegion) {
			items.clear();
			items = null;
		}
	}

	public void synchronize(Player player) {
		IoBuffer buffer = UpdateAreaPosition.getChunkUpdateBuffer(player, currentBase);
		if (appendUpdate(player, buffer)) {
			player.getSession().write(buffer);
		}
	}

	protected boolean appendUpdate(Player player, IoBuffer buffer) {
		boolean updated = false;
		int baseX = currentBase.getLocalX();
		int baseY = currentBase.getLocalY();
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				Scenery dyn = objects[x][y];
				if (dyn == null || plane.getObjects() == null) {
					continue;
				}
				Scenery stat = plane.getObjects()[baseX + x][baseY + y];
				if (!dyn.isRenderable() && stat != null) {
					ClearScenery.write(buffer, stat);
					updated = true;
				}
				else if (dyn != stat) {
					if (stat != null) {
						ClearScenery.write(buffer, stat);
					}
					ConstructScenery.write(buffer, dyn);
					updated = true;
				}
			}
		}
		ArrayList<GroundItem> totalItems = drawItems(items, player);
		for (GroundItem item : totalItems) {
			if (item != null && item.isActive() && item.getLocation() != null) {
				if (!item.isPrivate() || item.droppedBy(player)) {
					ConstructGroundItem.write(buffer, item);
					updated = true;
				}
			}
		}
		return updated;
	}

	public ArrayList<GroundItem> drawItems(List<GroundItem> items, Player player) {
		ArrayList<GroundItem> totalItems = items != null ? new ArrayList<GroundItem>(items) : new ArrayList<GroundItem>();

		if (player.getAttribute("chunkdraw", false)) {
			Location l = currentBase;
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					boolean add = false;
					if (y == 0 || y == SIZE - 1)
						add = true;
					else if (x == 0 || x == SIZE - 1)
						add = true;
					if (add)
						totalItems.add(new GroundItem(new Item(13444), l.transform(x, y, 0), player));
				}
			}
		}

		if (player.getAttribute("regiondraw", false)) {
			Location l = currentBase;
			int localX = l.getLocalX();
			int localY = l.getLocalY();

			for (int x = 0; x < SIZE; x++)
				for (int y = 0; y < SIZE; y++) {
					boolean add = false;
					if (localY == 0 || localY == 56)
						if (localY == 0 && y == 0)
							add = true;
						else if (localY == 56 && y == SIZE - 1)
							add = true;
					if (localX == 0 || localX == 56)
						if (localX == 0 && x == 0)
							add = true;
						else if (localX == 56 && x == SIZE - 1)
							add = true;

					if (add)
						totalItems.add(new GroundItem(new Item(13444), l.transform(x,y,0), player));
				}
		}

		if (player.getAttribute("clippingdraw", false)) {
			Location l = currentBase;
			for (int x = 0; x < SIZE; x++) {
				for (int y = 0; y < SIZE; y++) {
					int flag = RegionManager.getClippingFlag(l.getZ(), l.getX() + x, l.getY() + y);
					if (flag > 0) {
						totalItems.add(new GroundItem(new Item(flag), l.transform(x, y, 0), player));
					}
				}
			}
		}

		return totalItems;
	}

	public void update(Player player) {
		if (isUpdated()) {
			IoBuffer buffer = UpdateAreaPosition.getChunkUpdateBuffer(player, currentBase);
			Object[] flagsArray = flags.toArray();
			int size = flagsArray.length;
			for (int i = 0; i < size; i++) {
				UpdateFlag<?> flag = (UpdateFlag<?>) flagsArray[i];
				flag.writeDynamic(buffer, player);
			}
			player.getSession().write(buffer);
		}
	}

	public void rotate(Direction direction) {
		if (rotation != 0) {
			log(this.getClass(), Log.ERR,  "Region chunk was already rotated!");
			return;
		}
		Scenery[][] copy = new Scenery[SIZE][SIZE];
		Scenery[][] staticCopy = new Scenery[SIZE][SIZE];
		int baseX = currentBase.getLocalX();
		int baseY = currentBase.getLocalY();
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				copy[x][y] = objects[x][y];
				staticCopy[x][y] = plane.getObjects()[baseX + x][baseY + y];
				objects[x][y] = plane.getObjects()[baseX + x][baseY + y] = null;
				plane.getFlags().clearFlag(baseX + x, baseY + y);
			}
		}
		rotation = direction.toInteger();
		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				Scenery object = copy[x][y];
				Scenery stat = staticCopy[x][y];
				boolean match = object == stat;
				if (stat == null) {
					continue;
				}
				int[] pos = getRotatedPosition(x, y, stat.getDefinition().getSizeX(), stat.getDefinition().getSizeY(), stat.getRotation(), rotation);
				if (object != null) {
					object = object.transform(object.getId(), (object.getRotation() + rotation) % 4, object.getLocation().transform(pos[0] - x, pos[1] - y, 0));
					LandscapeParser.flagScenery(plane, baseX + pos[0], baseY + pos[1], object, true, true);
				}
				if (match) {
					stat = object;
				} else {
					stat = stat.transform(stat.getId(), (stat.getRotation() + rotation) % 4, stat.getLocation().transform(pos[0] - x, pos[1] - y, 0));
				}
				plane.getObjects()[baseX + pos[0]][baseY + pos[1]] = stat;
			}
		}
	}

	public static int[] getRotatedPosition(int x, int y, int sizeX, int sizeY, int rotation, int chunkRotation) {
		if ((rotation & 0x1) == 1) {
			int s = sizeX;
			sizeX = sizeY;
			sizeY = s;
		}
		if (chunkRotation == 0) {
			return new int[] { x, y };
		}
		if (chunkRotation == 1) {
			return new int[] { y, 7 - x - (sizeX - 1) };
		}
		if (chunkRotation == 2) {
			return new int[] { 7 - x - (sizeX - 1), 7 - y - (sizeY - 1) };
		}
		return new int[] { 7 - y - (sizeY - 1), x };
	}

	public List<GroundItem> getItems() {
		if (items == null) {
			items = new ArrayList<GroundItem>();
		}
		return items;
	}

	public void setItems(List<GroundItem> items) {
		this.items = items;
	}

	public Scenery[] getObjects(int chunkX, int chunkY) {
		return new Scenery[] { objects[chunkX][chunkY] };
	}

	public Scenery[][] getObjects() {
		return objects;
	}

	public void setObjects(Scenery[][] objects) {
		this.objects = objects;
	}

	public Location getBase() {
		return base;
	}

	public void setBase(Location base) {
		this.base = base;
	}

	public int getRotation() {
		return rotation;
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public boolean isUpdated() {
		return !flags.isEmpty();
	}

	public void resetFlags() {
		flags.clear();
	}

	public RegionPlane getPlane() {
		return plane;
	}

	public Location getCurrentBase() {
		return currentBase;
	}

	public void setCurrentBase(Location currentBase) {
		this.currentBase = currentBase;
	}

	public void rebuildFlags(RegionPlane from) {
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				Location loc = currentBase.transform(x,y,0);
				Location fromLoc = base.transform(x,y,0);
				plane.getFlags().getLandscape()[loc.getLocalX()][loc.getLocalY()] = from.getFlags().getLandscape()[fromLoc.getLocalX()][fromLoc.getLocalY()];
				plane.getFlags().clearFlag(x, y);
				Scenery obj = objects[x][y];
				if (obj != null)
					LandscapeParser.flagScenery(plane, loc.getLocalX(), loc.getLocalY(), obj, false, true);
			}
		}
	}

}
