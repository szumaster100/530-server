package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.node.scenery.Scenery;
import core.net.packet.Context;

public final class BuildSceneryContext implements Context {

	private final Player player;

	private final Scenery scenery;

	public BuildSceneryContext(Player player, Scenery scenery) {
		this.player = player;
		this.scenery = scenery;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Scenery getScenery() {
		return scenery;
	}

}