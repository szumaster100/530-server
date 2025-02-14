package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class WalkOptionContext implements Context {

	private final Player player;

	private final String option;

	public WalkOptionContext(Player player, String option) {
		this.player = player;
		this.option = option;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public String getOption() {
		return option;
	}

}