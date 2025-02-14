package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.system.communication.ClanRepository;
import core.net.packet.Context;

public final class ClanContext implements Context {

	private final Player player;

	private final ClanRepository clan;

	private final boolean leave;

	public ClanContext(Player player, ClanRepository clan, boolean leave) {
		this.player = player;
		this.clan = clan;
		this.leave = leave;
	}

	public ClanRepository getClan() {
		return clan;
	}

	public boolean isLeave() {
		return leave;
	}

	@Override
	public Player getPlayer() {
		return player;
	}
}