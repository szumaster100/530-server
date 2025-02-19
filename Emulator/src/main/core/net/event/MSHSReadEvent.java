package core.net.event;

import core.net.EventProducer;
import core.net.IoReadEvent;
import core.net.IoSession;
import core.net.producer.RegistryEventProducer;

import java.nio.ByteBuffer;

public final class MSHSReadEvent extends IoReadEvent {

	private static final EventProducer REGISTRY_PRODUCER = new RegistryEventProducer();

	public MSHSReadEvent(IoSession session, ByteBuffer buffer) {
		super(session, buffer);
	}

	@Override
	public void read(IoSession session, ByteBuffer buffer) {
		int opcode = buffer.get() & 0xFF;
		if (opcode == 14) {
			session.setProducer(REGISTRY_PRODUCER);
			session.write(true);
		}
	}

}