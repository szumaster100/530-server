package core.net;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public final class ServerSocketConnection {

	private final Selector selector;

	private final ServerSocketChannel channel;

	private final SocketChannel socket;

	public ServerSocketConnection(Selector selector, ServerSocketChannel channel) {
		this.selector = selector;
		this.channel = channel;
		this.socket = null;
	}

	public ServerSocketConnection(Selector selector, SocketChannel channel) {
		this.selector = selector;
		this.socket = channel;
		this.channel = null;
	}

	public boolean isClient() {
		return socket != null;
	}

	public Selector getSelector() {
		return selector;
	}

	public ServerSocketChannel getChannel() {
		return channel;
	}

	public SocketChannel getSocket() {
		return socket;
	}

}