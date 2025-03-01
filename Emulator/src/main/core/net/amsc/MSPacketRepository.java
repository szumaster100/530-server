package core.net.amsc;

import core.game.node.entity.player.Player;
import core.game.system.communication.ClanRank;
import core.net.packet.IoBuffer;
import core.net.packet.PacketHeader;

public final class MSPacketRepository {

	public static void sendContactUpdate(String username, String contact, boolean remove, boolean block, ClanRank rank) {
		IoBuffer buffer = new IoBuffer(block ? 5 : 4, PacketHeader.BYTE);
		buffer.putString(username);
		buffer.putString(contact);
		if (rank != null) {
			buffer.put((byte) 2);
			buffer.put((byte) rank.ordinal());
		} else {
			buffer.put((byte) (remove ? 1 : 0));
		}
		WorldCommunicator.getSession().write(buffer);
	}

	public static void sendClanRename(Player player, String clanName) {
		IoBuffer buffer = new IoBuffer(7, PacketHeader.BYTE);
		buffer.putString(player.getName());
		buffer.putString(clanName);
		WorldCommunicator.getSession().write(buffer);
	}

	public static void setClanSetting(Player player, int type, ClanRank rank) {
		if (!WorldCommunicator.isEnabled()) {
			return;
		}
		IoBuffer buffer = new IoBuffer(8, PacketHeader.BYTE);
		buffer.putString(player.getName());
		buffer.put((byte) type);
		if (rank != null) {
			buffer.put((byte) rank.ordinal());
		}
		WorldCommunicator.getSession().write(buffer);
	}

	public static void sendClanKick(String username, String name) {
		IoBuffer buffer = new IoBuffer(9, PacketHeader.BYTE);
		buffer.putString(username);
		buffer.putString(name);
		WorldCommunicator.getSession().write(buffer);
	}

	public static void sendChatSetting(Player player, int publicSetting, int privateSetting, int tradeSetting) {
		IoBuffer buffer = new IoBuffer(13, PacketHeader.BYTE);
		buffer.putString(player.getName());
		buffer.put(publicSetting);
		buffer.put(privateSetting);
		buffer.put(tradeSetting);
                if (WorldCommunicator.getSession() != null)
	            WorldCommunicator.getSession().write(buffer);
                else
                    player.sendMessage("Privacy settings unavailable at the moment. Please try again later.");
	}
}
