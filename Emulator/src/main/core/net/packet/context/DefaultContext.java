package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class DefaultContext implements Context {

	private Player player;

	private Object[] objects;

	public DefaultContext(Player player, Object... objects) {
		this.player = player;
		this.objects = objects;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Object[] getObjects() {
		return objects;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void setObjects(Object[] objects) {
		this.objects = objects;
	}

}
