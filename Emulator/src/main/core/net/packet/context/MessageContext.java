package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.game.node.entity.player.info.Rights;
import core.net.packet.Context;

public final class MessageContext implements Context {

	public static final int SEND_MESSAGE = 71;

	public static final int RECEIVE_MESSAGE = 0;

	public static final int CLAN_MESSAGE = 54;

	private final Player player;

	private final String other;

	private final int chatIcon;

	private final int opcode;

	private final String message;

	public MessageContext(Player player, Player other, int opcode, String message) {
		this.player = player;
		this.other = other.getName();
		this.chatIcon = Rights.getChatIcon(other);
		this.opcode = opcode;
		this.message = message;
	}

	public MessageContext(Player player, String other, int chatIcon, int opcode, String message) {
		this.player = player;
		this.other = other;
		this.chatIcon = chatIcon;
		this.opcode = opcode;
		this.message = message;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public String getOther() {
		return other;
	}

	public int getOpcode() {
		return opcode;
	}

	public String getMessage() {
		return message;
	}

	public int getChatIcon() {
		return chatIcon;
	}
}