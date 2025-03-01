package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class MusicContext implements Context {

	private Player player;

	private int musicId;

	private boolean secondary;

	public MusicContext(Player player, int musicId) {
		this(player, musicId, false);
	}

	public MusicContext(Player player, int musicId, boolean temporary) {
		this.player = player;
		this.musicId = musicId;
		this.secondary = temporary;
	}

	public final int getMusicId() {
		return musicId;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public boolean isSecondary() {
		return secondary;
	}

	public void setSecondary(boolean secondary) {
		this.secondary = secondary;
	}

}