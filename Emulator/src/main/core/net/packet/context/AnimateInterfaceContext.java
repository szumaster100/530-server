package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class AnimateInterfaceContext implements Context {

	private Player player;

	private int animationId;

	private int interfaceId;

	private int childId;

	public AnimateInterfaceContext(Player player, int animationId, int interfaceId, int childId) {
		this.player = player;
		this.animationId = animationId;
		this.interfaceId = interfaceId;
		this.childId = childId;
	}

	public int getAnimationId() {
		return animationId;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getChildId() {
		return childId;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
