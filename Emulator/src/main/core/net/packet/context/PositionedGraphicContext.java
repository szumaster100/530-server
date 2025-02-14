package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Graphics;
import core.net.packet.Context;

public final class PositionedGraphicContext implements Context {

	private final Player player;

	private final Graphics graphics;

	private final Location location;

	public int sceneX, sceneY;
	public int offsetX, offsetY;

	public PositionedGraphicContext(Player player, Graphics graphics, Location location, int offsetX, int offsetY) {
		this.player = player;
		this.graphics = graphics;
		this.location = location;
		this.sceneX = location.getSceneX(player.getPlayerFlags().getLastSceneGraph());
		this.sceneY = location.getSceneY(player.getPlayerFlags().getLastSceneGraph());
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Graphics getGraphic() {
		return graphics;
	}

	public Location getLocation() {
		return location;
	}

}