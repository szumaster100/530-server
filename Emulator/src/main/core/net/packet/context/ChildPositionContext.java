package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

import java.awt.*;

public final class ChildPositionContext implements Context {

	private final Player player;

	private final int interfaceId;

	private final int childId;

	private final Point position;

	public ChildPositionContext(Player player, int interfaceId, int childId, int positionX, int positionY) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.position = new Point(positionX, positionY);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getChildId() {
		return childId;
	}

	public Point getPosition() {
		return position;
	}

}