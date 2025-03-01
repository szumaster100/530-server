package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class SceneGraphContext implements Context {

	private final Player player;

	private final boolean login;

	public SceneGraphContext(Player player, boolean login) {
		this.player = player;
		this.login = login;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public boolean isLogin() {
		return login;
	}

}