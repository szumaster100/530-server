package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class SystemUpdateContext implements Context {

	private Player player;

	private int time;

	public SystemUpdateContext(Player player, int time) {
		this.player = player;
		this.setTime(time);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

}
