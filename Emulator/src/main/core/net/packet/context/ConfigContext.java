package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class ConfigContext implements Context {

	private Player player;

	private int id;

	private int value;

	private boolean cs2;

	public ConfigContext(Player player, int id, int value) {
		this(player, id, value, false);
	}

	public ConfigContext(Player player, int id, int value, boolean cs2) {
		this.player = player;
		this.id = id;
		this.value = value;
		this.cs2 = cs2;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Context setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public int getId() {
		return id;
	}

	public int getValue() {
		return value;
	}

	public boolean isCs2() {
		return cs2;
	}
}
