package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class MinimapStateContext implements Context {

	private final Player player;

	private final int state;

	public MinimapStateContext(Player player, int state) {
		this.player = player;
		this.state = state;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getState() {
		return state;
	}

}