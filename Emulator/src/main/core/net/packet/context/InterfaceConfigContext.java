package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class InterfaceConfigContext implements Context {

	private Player player;

	private int interfaceId;

	private int childId;

	private boolean hide;

	public InterfaceConfigContext(Player player, int interfaceId, int childId, boolean hide) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.hide = hide;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getChildId() {
		return childId;
	}

	public boolean isHidden() {
		return hide;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
