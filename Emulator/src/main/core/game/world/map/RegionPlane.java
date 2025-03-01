package core.game.world.map;

import core.game.node.Node;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.GroundItem;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import core.game.world.map.build.DynamicRegion;
import core.game.world.map.build.RegionFlags;
import core.game.world.update.flag.chunk.ItemUpdateFlag;
import core.net.packet.PacketRepository;
import core.net.packet.context.BuildItemContext;
import core.net.packet.out.ClearGroundItem;
import core.net.packet.out.ConstructGroundItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class RegionPlane {

	public static final int REGION_SIZE = 64;

	public static final int CHUNK_SIZE = REGION_SIZE >> 3;

	public static final Scenery NULL_OBJECT = new Scenery(0, Location.create(0, 0, 0));

	private final int plane;

	private final Region region;

	private final RegionFlags flags;

	private final RegionFlags projectileFlags;

	private final RegionChunk[][] chunks;

	private final List<NPC> npcs;

	private final List<Player> players;

	private Scenery[][] objects;

	public RegionPlane(Region region, int plane) {
		this.plane = plane;
		this.region = region;
		this.players = new CopyOnWriteArrayList<Player>();
		this.npcs = new CopyOnWriteArrayList<NPC>();
		Location base = region.getBaseLocation();
		this.flags = new RegionFlags(plane, base.getX(), base.getY());
		this.projectileFlags = new RegionFlags(plane, base.getX(), base.getY(), true);
		this.objects = new Scenery[REGION_SIZE][REGION_SIZE];
		this.chunks = new RegionChunk[CHUNK_SIZE][CHUNK_SIZE];
	}

	public void pulse() {
		Arrays.stream(chunks).forEach(regionChunks -> {Arrays.stream(regionChunks).filter(Objects::nonNull).forEach(RegionChunk::resetFlags);});
	}

	public void add(Scenery object, int x, int y, boolean landscape) {
		setChunkObject(x, y, object);
		if (landscape) {
			objects[x][y] = object;
		}
		if (object != null) {
			object.setRenderable(true);
		}
	}

	public RegionChunk getRegionChunk(int chunkX, int chunkY) {
		RegionChunk r = chunks[chunkX][chunkY];
		if (r != null) {
			return r;
		}
		if (region.isBuild()) {
			return chunks[chunkX][chunkY] = new BuildRegionChunk(region.getBaseLocation().transform(chunkX << 3, chunkY << 3, plane), 0, this);
		}
		return chunks[chunkX][chunkY] = new RegionChunk(region.getBaseLocation().transform(chunkX << 3, chunkY << 3, plane), 0, this);
	}

	public void setRegionChunk(int chunkX, int chunkY, RegionChunk chunk) {
		chunks[chunkX][chunkY] = chunk;
	}

	public void remove(int x, int y) {
		remove(x, y, -1);
	}

	public void remove(int x, int y, int objectId) {
		int chunkX = x / CHUNK_SIZE;
		int chunkY = y / CHUNK_SIZE;
		int offsetX = x - chunkX * CHUNK_SIZE;
		int offsetY = y - chunkY * CHUNK_SIZE;
		RegionChunk chunk = getRegionChunk(chunkX, chunkY);
		Scenery remove = new Scenery(0, region.getBaseLocation().transform(x, y, plane), 22, 0);
		remove.setRenderable(false);
		if (chunk instanceof BuildRegionChunk) {
			int index = ((BuildRegionChunk) chunk).getIndex(offsetX, offsetY, objectId);
			((BuildRegionChunk) chunk).getObjects(index)[offsetX][offsetY] = remove;
			return;
		}
		chunk.getObjects()[offsetX][offsetY] = remove;
	}

	private void setChunkObject(int x, int y, Scenery object) {
		int chunkX = x / CHUNK_SIZE;
		int chunkY = y / CHUNK_SIZE;
		int offsetX = x - chunkX * CHUNK_SIZE;
		int offsetY = y - chunkY * CHUNK_SIZE;
		RegionChunk r = getRegionChunk(chunkX, chunkY);
		if (r instanceof BuildRegionChunk) {
			((BuildRegionChunk) r).store(object);
			return;
		}
		r.getObjects()[offsetX][offsetY] = object;
	}

	public Scenery[][] getObjects() {
		return objects;
	}

        public List<Scenery> getObjectList() {
            ArrayList<Scenery> list = new ArrayList();
            for (int x = 0; x < REGION_SIZE; x++) {
                for (int y = 0; y < REGION_SIZE; y++) {
                    if (objects[x][y] != null)
                        list.add(objects[x][y]);
                }
            }
            return list;
        }

	public void clear() {
		for (RegionChunk[] c : chunks) {
			for (RegionChunk chunk : c) {
				if (chunk != null) {
					chunk.clear();
				}
			}
		}
		if (region instanceof DynamicRegion && objects != null) {
			for (int x = 0; x < REGION_SIZE; x++) {
				for (int y = 0; y < REGION_SIZE; y++) {
					objects[x][y] = null;
				}
			}
			objects = null;
		}
	}

	public void add(NPC npc) {
		npcs.add(npc);
	}

	public void add(Player player) {
		players.add(player);
	}

	public void add(GroundItem item) {
		Location l = item.getLocation();
		RegionChunk c = getRegionChunk(l.getLocalX() / RegionChunk.SIZE, l.getLocalY() / RegionChunk.SIZE);
		if (!c.getItems().add(item)) {
			return;
		}
		GroundItem g = (GroundItem) item;
		if (g.isPrivate()) {
			if (g.getDropper() != null) {
				PacketRepository.send(ConstructGroundItem.class, new BuildItemContext(g.getDropper(), item));
			}
			return;
		}
		c.flag(new ItemUpdateFlag(g, ItemUpdateFlag.CONSTRUCT_TYPE));
	}

	public void remove(NPC npc) {
		npcs.remove(npc);
	}

	public void remove(Player player) {
		players.remove(player);
	}

	public void remove(GroundItem item) {
		Location l = item.getLocation();
		RegionChunk c = getRegionChunk(l.getLocalX() / RegionChunk.SIZE, l.getLocalY() / RegionChunk.SIZE);
		if (!c.getItems().remove(item)) {
			return;
		}
		if (item.isPrivate()) {
			if (item.getDropper() != null && item.getDropper().isPlaying() && item.getDropper().getLocation().withinDistance(l)) {
				PacketRepository.send(ClearGroundItem.class, new BuildItemContext(item.getDropper(), item));
			}
			return;
		}
		c.flag(new ItemUpdateFlag(item, ItemUpdateFlag.REMOVE_TYPE));
	}

	public RegionFlags getFlags() {
		return flags;
	}

	public RegionFlags getProjectileFlags() {
		return projectileFlags;
	}

	public List<NPC> getNpcs() {
		return npcs;
	}

	public List<Node> getEntities()
	{
	    List<Node> entities = new ArrayList<>(npcs);
		Arrays.stream(getObjects()).forEach(o -> entities.addAll(Arrays.asList(o)));
	    return  entities;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public int getPlane() {
		return plane;
	}

	public Region getRegion() {
		return region;
	}

	public Scenery getChunkObject(int x, int y) {
		return getChunkObject(x, y, -1);
	}

	public Scenery getChunkObject(int x, int y, int objectId) {
		int chunkX = x / CHUNK_SIZE;
		int chunkY = y / CHUNK_SIZE;
		int offsetX = x - chunkX * CHUNK_SIZE;
		int offsetY = y - chunkY * CHUNK_SIZE;
		RegionChunk chunk = getRegionChunk(chunkX, chunkY);
		if (chunk instanceof BuildRegionChunk) {
			BuildRegionChunk brc = (BuildRegionChunk) chunk;
			return brc.get(offsetX, offsetY, brc.getIndex(offsetX, offsetY, objectId));
		}
		return getRegionChunk(chunkX, chunkY).getObjects()[offsetX][offsetY];
	}

	public List<GroundItem> getChunkItems(int x, int y) {
		int chunkX = x / CHUNK_SIZE;
		int chunkY = y / CHUNK_SIZE;
		return getRegionChunk(chunkX, chunkY).getItems();
	}

	public GroundItem getItem(int itemId, Location l, Player player) {
		GroundItem groundItem = null;
		for (Item item : getChunkItems(l.getLocalX(), l.getLocalY())) {
			GroundItem g = (GroundItem) item;
			if (g.getId() == itemId && l.equals(g.getLocation()) && !g.isRemoved()) {
				if (groundItem != null && (!g.isPrivate() || player == null)) {
					continue;
				}
				if ((!g.isPrivate() || player == null || g.droppedBy(player))) {
					groundItem = g;
				}
			}
		}
		return groundItem;
	}

	public RegionChunk[][] getChunks() {
		return chunks;
	}

}
