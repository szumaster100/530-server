package core.net.event;

import core.auth.AuthResponse;
import core.game.node.entity.player.Player;
import core.net.EventProducer;
import core.net.IoSession;
import core.net.IoWriteEvent;
import core.net.producer.GameEventProducer;

import java.nio.ByteBuffer;

public final class LoginWriteEvent extends IoWriteEvent {

	private static final EventProducer GAME_PRODUCER = new GameEventProducer();

	public LoginWriteEvent(IoSession session, Object context) {
		super(session, context);
	}

	@Override
	public void write(IoSession session, Object context) {
		AuthResponse response = (AuthResponse) context;
		ByteBuffer buffer = ByteBuffer.allocate(500);
		buffer.put((byte) response.ordinal());
		switch (response.ordinal()) {
			case 2: //successful login
				buffer.put(getWorldResponse(session));
				session.setProducer(GAME_PRODUCER);
				break;
			//Could add a case here to auto-restart the server in case the login server goes offline (case 8)
			//Possibly a risk for malicious attacks though
			case 21: //Moving world
				buffer.put((byte) session.getServerKey());
				break;
		}
		buffer.flip();
		session.queue(buffer);
	}

	private static ByteBuffer getWorldResponse(IoSession session) {
		ByteBuffer buffer = ByteBuffer.allocate(150);
		Player player = session.getPlayer();
		buffer.put((byte) player.getDetails().getRights().ordinal());
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.put((byte) 1);
		buffer.put((byte) 0);
		buffer.put((byte) 0);
		buffer.putShort((short) player.getIndex());
		buffer.put((byte) (1)); // Enable all G.E boxes
		buffer.put((byte) 1);
		buffer.flip();
		return buffer;

	}
}