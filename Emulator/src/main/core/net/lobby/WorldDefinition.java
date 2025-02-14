package core.net.lobby;

import core.game.world.GameWorld;
import core.game.world.repository.Repository;

public class WorldDefinition {

	private final String activity;

	private final int country;

	private final int flag;

	private final String ip;

	private final int location;

	private final String region;

	private final int worldId;

	private int players;

	public WorldDefinition(int worldId, int location, int flag, String activity, String ip, String region, int country) {
		this.worldId = worldId;
		this.location = location;
		this.flag = flag;
		this.activity = activity;
		this.ip = ip;
		this.region = region;
		this.country = country;
	}

	public String getActivity() {
		return activity;
	}

	public int getCountry() {
		return country;
	}

	public int getFlag() {
		return flag;
	}

	public String getIp() {
		return ip;
	}

	public int getLocation() {
		return location;
	}

	public String getRegion() {
		return region;
	}

	public int getWorldId() {
		return worldId;
	}

	public int getPlayerCount() {
		if (worldId == GameWorld.getSettings().getWorldId()) {
			return Repository.getPlayers().size();
		}
		return players;
	}

	public int getPlayers() {
		return players;
	}

	public void setPlayers(int players) {
		this.players = players;
	}

}