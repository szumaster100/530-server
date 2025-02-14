package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class GameMessageContext implements Context {

	private Player player;

	private String message;

	public GameMessageContext(Player player, String message) {
		this.player = player;
		this.message = message;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public String getMessage() {
		return message;
	}
}
