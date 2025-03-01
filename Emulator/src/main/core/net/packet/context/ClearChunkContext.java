package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.world.map.RegionChunk;
import core.net.packet.Context;

public final class ClearChunkContext implements Context {

	private final Player player;

	private final RegionChunk chunk;

	public ClearChunkContext(Player player, RegionChunk chunk) {
		this.player = player;
		this.chunk = chunk;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public RegionChunk getChunk() {
		return chunk;
	}

}
