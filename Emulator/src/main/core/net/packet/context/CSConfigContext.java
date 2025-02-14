package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class CSConfigContext implements Context {

	private Player player;

	private int value;

	private int id;
	private final Object[] parameters;
	private final String types;

	public CSConfigContext(Player player, int id, int value, String types, Object[] parameters) {
		this.player = player;
		this.value = value;
		this.id = id;
		this.parameters = parameters;
		this.types = types;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Context setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public int getValue() {
		return value;
	}

	public int getId() {
		return id;
	}

	public String getTypes() {
		return types;
	}

	public Object[] getParameters() {
		return parameters;
	}
}