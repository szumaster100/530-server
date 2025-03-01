package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class InterfaceContext implements Context {

	private Player player;

	private final int windowId;

	private int componentId;

	private final int interfaceId;

	private final boolean walkable;

	public InterfaceContext(Player player, int windowId, int componentId, int interfaceId, boolean walkable) {
		this.player = player;
		this.windowId = windowId;
		this.componentId = componentId;
		this.interfaceId = interfaceId;
		this.walkable = walkable;
	}

	public InterfaceContext transform(Player player, int id) {
		return new InterfaceContext(player, windowId, componentId, id, walkable);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Context setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public int getWindowId() {
		return windowId;
	}

	public void setComponentId(int componentId) {
		this.componentId = componentId;
	}

	public int getComponentId() {
		return componentId;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public boolean isWalkable() {
		return walkable;
	}

}