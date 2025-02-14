package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class AccessMaskContext implements Context {

	private Player player;

	private int id;

	private int childId;

	private int interfaceId;

	private int offset;

	private int length;

	public AccessMaskContext(Player player, int id, int childId, int interfaceId, int offset, int length) {
		this.player = player;
		this.id = id;
		this.childId = childId;
		this.interfaceId = interfaceId;
		this.offset = offset;
		this.length = length;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public AccessMaskContext transform(Player player, int id) {
		return new AccessMaskContext(player, id, childId, interfaceId, offset, length);
	}

	public Context setPlayer(Player player) {
		this.player = player;
		return this;
	}

	public int getId() {
		return id;
	}

	public int getChildId() {
		return childId;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}
}
