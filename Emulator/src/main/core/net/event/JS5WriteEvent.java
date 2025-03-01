package core.net.event;

import core.cache.Cache;
import core.net.IoSession;
import core.net.IoWriteEvent;

import java.nio.ByteBuffer;

public final class JS5WriteEvent extends IoWriteEvent {

	private static byte[] cachedReference;

	public JS5WriteEvent(IoSession session, Object context) {
		super(session, context);
	}

	@Override
	public void write(IoSession session, Object context) {
		int[] request = (int[]) context;
		int container = request[0];
		int archive = request[1];
		boolean priority = request[2] == 1;
		if (archive == 255 && container == 255) {
			session.queue(getReferenceData());
			return;
		}
		ByteBuffer response = Cache.getArchiveData(container, archive, priority, session.getJs5Encryption());
		if (response != null) {
			session.queue(response);
		}
	}

	private static ByteBuffer getReferenceData() {
		if (cachedReference == null) {
			cachedReference = Cache.generateReferenceData();
		}
		ByteBuffer buffer = ByteBuffer.allocate(cachedReference.length << 2);
		buffer.put((byte) 255);
		buffer.putShort((short) 255);
		buffer.put((byte) 0);
		buffer.putInt(cachedReference.length);
		int offset = 10;
		for (int index = 0; index < cachedReference.length; index++) {
			if (offset == 512) {
				buffer.put((byte) 255);
				offset = 1;
			}
			buffer.put(cachedReference[index]);
			offset++;
		}
		buffer.flip();
		return buffer;
	}

}