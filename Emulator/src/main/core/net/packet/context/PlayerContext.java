package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class PlayerContext implements Context {

	private final Player player;

	public PlayerContext(Player player) {
		this.player = player;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

}