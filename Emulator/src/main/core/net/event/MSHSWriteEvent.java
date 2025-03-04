package core.net.event;

import core.ServerConstants;
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
		ByteBuffer	buffer = ByteBuffer.allocate(2 + ServerConstants.MS_SECRET_KEY.length());
		buffer.put((byte) 88);
		ByteBufferUtils.putString(ServerConstants.MS_SECRET_KEY, buffer);
		session.queue((ByteBuffer) buffer.flip());
	}

}