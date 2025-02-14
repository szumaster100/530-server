package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class RunScriptContext implements Context {

	private Player player;

	private int id;

	private Object[] objects;

	private String string;

	public RunScriptContext(Player player, int id, String string, Object... objects) {
		this.player = player;
		this.id = id;
		this.objects = objects;
		this.string = string;
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

	public Object[] getObjects() {
		return objects;
	}

	public String getString() {
		return string;
	}
}
