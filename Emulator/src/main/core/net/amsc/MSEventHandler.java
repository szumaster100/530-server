package core.net.amsc;

import core.net.IoEventHandler;
import core.net.IoSession;
import core.tools.Log;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;

import static core.api.ContentAPIKt.log;

public final class MSEventHandler extends IoEventHandler {

	public MSEventHandler() {
		super(Executors.newSingleThreadExecutor());
	}

	@Override
	public void connect(SelectionKey key) throws IOException {
		SocketChannel ch = (SocketChannel) key.channel();
		try {
			if (ch.finishConnect()) {
				key.interestOps(key.interestOps() ^ SelectionKey.OP_CONNECT);
				key.interestOps(key.interestOps() | SelectionKey.OP_READ);
				IoSession session = (IoSession) key.attachment();
				key.attach(session = new IoSession(key, service));
				WorldCommunicator.register(session);
				return;
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
		log(this.getClass(), Log.ERR,  "Failed connecting to Management Server!");
		WorldCommunicator.terminate();
	}

	@Override
	public void accept(SelectionKey key, Selector selector) throws IOException {
		super.write(key);
	}

	@Override
	public void read(SelectionKey key) throws IOException {
		super.read(key);
	}

	@Override
	public void write(SelectionKey key) {
		super.write(key);
	}

	@Override
	public void disconnect(SelectionKey key, Throwable t) {
		super.disconnect(key, t);
		WorldCommunicator.terminate();
	}

}