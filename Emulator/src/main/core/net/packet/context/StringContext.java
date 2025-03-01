package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class StringContext implements Context {

	private Player player;

	private String string;

	private int interfaceId;

	private int lineId;

	public StringContext(Player player, String string, int interfaceId, int lineId) {
		this.player = player;
		this.string = string;
		this.interfaceId = interfaceId;
		this.lineId = lineId;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public String getString() {
		return string;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getLineId() {
		return lineId;
	}
}
