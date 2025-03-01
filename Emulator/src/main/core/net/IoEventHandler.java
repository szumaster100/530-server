package core.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;

public class IoEventHandler {

	protected final ExecutorService service;

	public IoEventHandler(ExecutorService service) {
		this.service = service;
	}

	public void connect(SelectionKey key) throws IOException {

	}

	public void accept(SelectionKey key, Selector selector) throws IOException {
		SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
		sc.configureBlocking(false);
		sc.socket().setTcpNoDelay(true);
		sc.register(selector, SelectionKey.OP_READ);
	}

	public void read(SelectionKey key) throws IOException {
		ReadableByteChannel channel = (ReadableByteChannel) key.channel();
		ByteBuffer buffer = ByteBuffer.allocate(100_000);
		IoSession session = (IoSession) key.attachment();
		try {
			if (channel.read(buffer) == -1) {
				if (session != null) {
					session.disconnect();
				}
				key.cancel();
				return;
			}
		} catch (IOException e) {
			if (e.getMessage().contains("reset by peer") && session != null) {
				session.disconnect();
			} else {
				key.cancel();
				return;
			}
		}
		buffer.flip();
		if (session == null) {
			key.attach(session = new IoSession(key, service));
		}
		service.execute(session.getProducer().produceReader(session, buffer));
	}

	public void write(SelectionKey key) {
		IoSession session = (IoSession) key.attachment();
		key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
		session.write();
	}

	public void disconnect(SelectionKey key, Throwable t) {
		try {
			IoSession session = (IoSession) key.attachment();
			String cause = "" + t;
			if (t != null && !(t instanceof ClosedChannelException || cause.contains("De externe host") || cause.contains("De software op uw") || cause.contains("An established connection was aborted") || cause.contains("An existing connection") || cause.contains("AsynchronousClose"))) {
				t.printStackTrace();
			}
			if (session != null) {
				session.disconnect();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}