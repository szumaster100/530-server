package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class IntegerContext implements Context {

	private Player player;

	private int integer;

	public IntegerContext(Player player, int integer) {
		this.player = player;
		this.setInteger(integer);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

}
