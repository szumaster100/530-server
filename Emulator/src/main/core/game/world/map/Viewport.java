package core.game.world.map;

import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

public final class Viewport {

	public static final int CHUNK_SIZE = 5;

	private Region region;

	private RegionChunk[][] chunks = new RegionChunk[CHUNK_SIZE][CHUNK_SIZE];

	private RegionPlane currentPlane;

	private List<RegionPlane> viewingPlanes = new LinkedList<>();

	public Viewport() {

	}

	public void updateViewport(Entity entity) {
		RegionChunk chunk = RegionManager.getRegionChunk(entity.getLocation());
		int center = chunks.length >> 1;
		if (chunks[center][center] == chunk) {
			return;
		}
		int offset = center * -8;
		Location l = chunk.getCurrentBase();
		for (int x = 0; x < chunks.length; x++) {
			for (int y = 0; y < chunks[x].length; y++) {
				chunks[x][y] = RegionManager.getRegionChunk(l.transform(offset + (8 * x), offset + (8 * y), 0));
			}
		}
	}

	public void remove(Entity entity) {
		if (region == null) {
			return;
		}
		if (entity instanceof Player) {
			region.remove((Player) entity);
			Region region = null;
			for (RegionPlane r : viewingPlanes) {
				if (region != r.getRegion()) {
					region = r.getRegion();
					region.decrementViewAmount();
					region.checkInactive();
				}
			}
		} else {
			region.remove((NPC) entity);
		}
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public RegionChunk[][] getChunks() {
		return chunks;
	}

	public void setChunks(RegionChunk[][] chunks) {
		this.chunks = chunks;
	}

	public List<RegionPlane> getViewingPlanes() {
		return viewingPlanes;
	}

	public void setViewingPlanes(List<RegionPlane> regions) {
		this.viewingPlanes = regions;
	}

	public RegionPlane getCurrentPlane() {
		return currentPlane;
	}

	public void setCurrentPlane(RegionPlane currentPlane) {
		this.currentPlane = currentPlane;
	}

}
