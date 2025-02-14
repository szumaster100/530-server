package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class WindowsPaneContext implements Context {

	private final Player player;

	private final int windowId;

	private final int type;

	public WindowsPaneContext(Player player, int windowId, int type) {
		this.player = player;
		this.windowId = windowId;
		this.type = type;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getWindowId() {
		return windowId;
	}

	public int getType() {
		return type;
	}

}
