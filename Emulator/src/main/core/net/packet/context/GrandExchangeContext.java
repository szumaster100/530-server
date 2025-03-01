package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class GrandExchangeContext implements Context {

	private final Player player;

	public final byte idx;
	public final byte state;
	public final short itemID;
	public final boolean isSell;
	public final int value;
	public final int amt;
	public final int completedAmt;
	public final int totalCoinsExchanged;

	public GrandExchangeContext(Player player, byte idx, byte state, short itemID, boolean isSell, int value, int amt, int completedAmt, int totalCoinsExchanged) {
		this.player = player;
		this.idx = idx;
		this.state = state;
		this.itemID = itemID;
		this.isSell = isSell;
		this.value = value;
		this.amt = amt;
		this.completedAmt = completedAmt;
		this.totalCoinsExchanged = totalCoinsExchanged;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}
