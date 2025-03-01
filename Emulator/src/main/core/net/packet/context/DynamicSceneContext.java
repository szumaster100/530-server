package core.net.packet.context;

import core.game.node.entity.player.Player;

public final class DynamicSceneContext extends SceneGraphContext {

	public DynamicSceneContext(Player player, boolean login) {
		super(player, login);
	}

}