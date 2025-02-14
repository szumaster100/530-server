package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.world.update.flag.context.Animation;
import core.net.packet.Context;

public class AnimateObjectContext implements Context {

	private final Player player;

	private final Animation animation;

	public AnimateObjectContext(Player player, Animation animation) {
		this.player = player;
		this.animation = animation;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Animation getAnimation() {
		return animation;
	}
}