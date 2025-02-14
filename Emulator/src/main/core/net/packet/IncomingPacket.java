package core.net.packet;

import core.game.node.entity.player.Player;

public interface IncomingPacket {

	public void decode(Player player, int opcode, IoBuffer buffer);

}