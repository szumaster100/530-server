package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.net.packet.Context;

public final class AreaPositionContext implements Context {

	private final Player player;

	private final Location location;

	private final int offsetX;

	private final int offsetY;

	public AreaPositionContext(Player player, Location location, int offsetX, int offsetY) {
		this.player = player;
		this.location = location;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Location getLocation() {
		return location;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

}