package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.net.packet.Context;

public final class LocationContext implements Context {

	private final Player player;

	private final Location location;

	private final boolean teleport;

	public LocationContext(Player player, Location location, boolean teleport) {
		this.player = player;
		this.location = location;
		this.teleport = teleport;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}

	public boolean isTeleport() {
		return teleport;
	}

}