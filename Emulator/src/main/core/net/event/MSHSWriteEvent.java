package core.net.event;

import core.ServerConfig;
import core.cache.misc.buffer.ByteBufferUtils;
import core.net.IoSession;
import core.net.IoWriteEvent;

import java.nio.ByteBuffer;

public final class MSHSWriteEvent extends IoWriteEvent {

	public MSHSWriteEvent(IoSession session, Object context) {
		super(session, context);
	}

	@Override
	public void write(IoSession session, Object context) {
		ByteBuffer	buffer = ByteBuffer.allocate(2 + ServerConfig.MS_SECRET_KEY.length());
		buffer.put((byte) 88);
		ByteBufferUtils.putString(ServerConfig.MS_SECRET_KEY, buffer);
		session.queue((ByteBuffer) buffer.flip());
	}

}