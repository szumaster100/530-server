package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.net.packet.Context;

public final class BuildItemContext implements Context {

	private final Player player;

	private final Item item;

	private final int oldAmount;

	public BuildItemContext(Player player, Item item) {
		this(player, item, 0);
	}

	public BuildItemContext(Player player, Item item, int oldAmount) {
		this.player = player;
		this.item = item;
		this.oldAmount = oldAmount;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public Item getItem() {
		return item;
	}

	public int getOldAmount() {
		return oldAmount;
	}

}