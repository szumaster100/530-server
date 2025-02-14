package core.net;

import java.nio.ByteBuffer;

public interface EventProducer {

	IoReadEvent produceReader(IoSession session, ByteBuffer buffer);

	IoWriteEvent produceWriter(IoSession session, Object context);

}