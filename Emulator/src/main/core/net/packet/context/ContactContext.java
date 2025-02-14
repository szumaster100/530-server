package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class ContactContext implements Context {

	public static final int UPDATE_STATE_TYPE = 0;

	public static final int UPDATE_FRIEND_TYPE = 1;

	public static final int IGNORE_LIST_TYPE = 2;

	private final Player player;

	private int type;

	private String name;

	private int worldId;

	public ContactContext(Player player, int type) {
		this.player = player;
		this.type = type;
	}

	public ContactContext(Player player, String name, int worldId) {
		this.player = player;
		this.name = name;
		this.worldId = worldId;
		this.type = UPDATE_FRIEND_TYPE;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOnline() {
		return worldId > 0;
	}

	public int getWorldId() {
		return worldId;
	}

	public void setWorldId(int worldId) {
		this.worldId = worldId;
	}

}