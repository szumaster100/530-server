package core.game.world.map;

import core.cache.Cache;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.music.MusicZone;
import core.game.system.communication.CommunicationInfo;
import core.game.system.config.XteaParser;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.build.DynamicRegion;
import core.game.world.map.build.LandscapeParser;
import core.game.world.map.build.MapscapeParser;
import core.game.world.map.zone.RegionZone;
import core.game.world.repository.Repository;
import core.tools.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static core.api.ContentAPIKt.log;

public class Region {

	public static final int SIZE = 64;

	private final int x;

	private final int y;

	private final RegionPlane[] planes = new RegionPlane[4];

	private final Pulse activityPulse;

	private final List<RegionZone> regionZones = new ArrayList<>(20);

	private int music = -1;

	private final List<MusicZone> musicZones = new ArrayList<>(20);

	private final HashMap<String,Long> tolerances = new HashMap<>();

	private boolean active;

	private int objectCount;

	private boolean hasFlags;

	private boolean loaded;

	private int viewAmount;

	private boolean build;

	private boolean updateAllPlanes;

	public Region(int x, int y) {
		this.x = x;
		this.y = y;
		for (int plane = 0; plane < 4; plane++) {
			planes[plane] = new RegionPlane(this, plane);
		}
		this.activityPulse = new Pulse(50) {
			@Override
			public boolean pulse() {
				flagInactive();
				return true;
			}
		};
		activityPulse.stop();
	}

	public Location getBaseLocation() {
		return Location.create(x << 6, y << 6, 0);
	}

	public void add(RegionZone zone) {
		regionZones.add(zone);
		for (RegionPlane plane : planes) {
			for (NPC npc : plane.getNpcs()) {
				npc.getZoneMonitor().updateLocation(npc.getLocation());
			}
			for (Player p : plane.getPlayers()) {
				p.getZoneMonitor().updateLocation(p.getLocation());
			}
		}
	}

	public void remove(RegionZone zone) {
		regionZones.remove(zone);
		for (RegionPlane plane : planes) {
			for (NPC npc : plane.getNpcs()) {
				npc.getZoneMonitor().updateLocation(npc.getLocation());
			}
			for (Player p : plane.getPlayers()) {
				p.getZoneMonitor().updateLocation(p.getLocation());
			}
		}
	}

	public void add(Player player) {
		planes[player.getLocation().getZ()].add(player);
		tolerances.put(player.getUsername(), System.currentTimeMillis());
		flagActive();
	}

	public void add(NPC npc) {
		planes[npc.getLocation().getZ()].add(npc);
	}

	public void remove(NPC npc) {
		RegionPlane plane = npc.getViewport().getCurrentPlane();
		if (plane != null && plane != planes[npc.getLocation().getZ()]) {
			plane.remove(npc);
		}
		planes[npc.getLocation().getZ()].remove(npc);
	}

	public void remove(Player player) {
		player.getViewport().getCurrentPlane().remove(player);
		tolerances.remove(player.getUsername());
		checkInactive();
	}

	public boolean isTolerated(Player player){
		return System.currentTimeMillis() - tolerances.getOrDefault(player.getUsername(), System.currentTimeMillis()) > TimeUnit.MINUTES.toMillis(10);
	}

	public boolean checkInactive() {
		return isInactive(true);
	}

	public boolean isInactive(boolean runPulse) {
		if (isViewed()) {
			return false;
		}
		for (RegionPlane p : planes) {
			if (!p.getPlayers().isEmpty()) {
				return false;
			}
		}
		if (runPulse) {
			if (!activityPulse.isRunning()) {
				activityPulse.restart();
				activityPulse.start();
				GameWorld.getPulser().submit(activityPulse);
			}
		}
		return true;
	}

	public boolean isPendingRemoval() {
		return activityPulse.isRunning();
	}

	public void flagActive() {
		activityPulse.stop();
		if (!active) {
			active = true;
			load(this);
			for (RegionPlane r : planes) {
				for (NPC n : r.getNpcs()) {
					if (n.isActive()) {
						Repository.addRenderableNPC(n);
					}
				}
			}
		}
	}

	public boolean flagInactive(boolean force) {
		if (unload(this, force)) {
			active = false;
			return true;
		} else {
			return false;
		}
	}

	public boolean flagInactive() {
            return flagInactive(false);
	}

	public static void load(Region r) {
		load(r, r.build);
	}

	public static void load(Region r, boolean build) {
		try {
			if (r.isLoaded() && r.isBuild() == build) {
				return;
			}
			r.build = build;
			boolean dynamic = r instanceof DynamicRegion;
			int regionId = dynamic ? ((DynamicRegion) r).getRegionId() : r.getId();
			int regionX = regionId >> 8 & 0xFF;
			int regionY = regionId & 0xFF;
			int mapscapeId = Cache.getIndexes()[5].getArchiveId("m" + regionX + "_"+ regionY);

			if (mapscapeId < 0 && !dynamic) {
				r.setLoaded(true);
				return;
			}

			byte[][][] mapscapeData = new byte[4][SIZE][SIZE];
			for (RegionPlane plane : r.planes) {
				plane.getFlags().setLandscape(new boolean[SIZE][SIZE]);
				//plane.getFlags().setClippingFlags(new int[SIZE][SIZE]);
				//plane.getProjectileFlags().setClippingFlags(new int[SIZE][SIZE]);
			}
			if (mapscapeId > -1) {
				ByteBuffer mapscape = ByteBuffer.wrap(Cache.getIndexes()[5].getCacheFile().getContainerUnpackedData(mapscapeId));
				MapscapeParser.parse(r, mapscapeData, mapscape);
			}
			r.hasFlags = dynamic;
			r.setLoaded(true);
			int landscapeId = Cache.getIndexes()[5].getArchiveId("l" + regionX + "_" + regionY);
			if (landscapeId > -1) {
				byte[] landscape = Cache.getIndexes()[5].getFileData(landscapeId, 0, XteaParser.Companion.getRegionXTEA(regionId));
				if (landscape == null || landscape.length < 4) {
					return;
				}
				r.hasFlags = true;
				try {
					LandscapeParser.parse(r, mapscapeData, ByteBuffer.wrap(landscape), build);
				} catch (Throwable t) {
					new Throwable("Failed parsing region " + regionId + "!", t).printStackTrace();
				}
			}
			MapscapeParser.clipMapscape(r, mapscapeData);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static boolean unload(Region r) {
            return unload(r, false);
        }

	public static boolean unload(Region r, boolean force) {
		if (!force && r.isViewed()) {
			log(CommunicationInfo.class, Log.ERR, "Players viewing region!");
			r.flagActive();
			return false;
		}
		for (RegionPlane p : r.planes) {
			if (!force && !p.getPlayers().isEmpty()) {
				log(CommunicationInfo.class, Log.ERR, "Players still in region!");
				r.flagActive();
				return false;
			}
		}
		for (RegionPlane p : r.planes) {
			p.clear();
			if (!(r instanceof DynamicRegion)) {
				for (NPC n : p.getNpcs()) {
					n.onRegionInactivity();
				}
			}
		}
		if (r.isBuild())
			r.setLoaded(false);
		r.activityPulse.stop();
	        return true;
	}

	public boolean isViewed() {
		synchronized (this) {
			return viewAmount > 0;
		}
	}

	public int incrementViewAmount() {
		synchronized (this) {
			return ++viewAmount;
		}
	}

	public int decrementViewAmount() {
		synchronized (this) {
			if (viewAmount < 1) {
				//log(this.getClass(), Log.ERR,  "View amount is " + (viewAmount - 1));
				viewAmount++;
			}
			return --viewAmount;
		}
	}

	public boolean isActive() {
		return active;
	}

	@Deprecated
	public void setActive(boolean active) {
		this.active = active;
	}

	public int getId() {
		return x << 8 | y;
	}

	public int getRegionId() {
		return getId();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public RegionPlane[] getPlanes() {
		return planes;
	}

	public void setMusic(int music) {
		this.music = music;
	}

	public int getMusic() {
		return this.music;
	}

	public List<RegionZone> getRegionZones() {
		return regionZones;
	}

	public List<MusicZone> getMusicZones() {
		return musicZones;
	}

	public int getObjectCount() {
		return objectCount;
	}

	public void setObjectCount(int objectCount) {
		this.objectCount = objectCount;
	}

	public boolean isHasFlags() {
		return hasFlags;
	}

	public void setHasFlags(boolean hasFlags) {
		this.hasFlags = hasFlags;
	}

	public void setRegionTimeOut(int ticks) {
		activityPulse.setDelay(ticks);
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}

	public void setViewAmount(int viewAmount) {
		this.viewAmount = viewAmount;
	}

	public boolean isBuild() {
		return build;
	}

	public void setBuild(boolean build) {
		this.build = build;
	}

	public boolean isUpdateAllPlanes() {
		return updateAllPlanes;
	}

	public void setUpdateAllPlanes(boolean updateAllPlanes) {
		this.updateAllPlanes = updateAllPlanes;
	}
}
