package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class InteractionOptionContext implements Context {

	private final Player player;

	private final int index;

	private boolean remove = false;

	private final String name;

	public InteractionOptionContext(Player player, int index, String name) {
		this.player = player;
		this.index = index;
		this.name = name;
		this.remove = false;
	}

	public InteractionOptionContext(Player player, int index, String name, boolean remove){
		this.player = player;
		this.index = index;
		this.name = name;
		this.remove = remove;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public boolean isRemove() {
		return remove;
	}

	public int getIndex() {
		return index;
	}

	public String getName() {
		return name;
	}

}