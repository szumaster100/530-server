package core.net;

import core.cache.crypto.ISAACPair;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.info.ClientInfo;
import core.game.system.task.Pulse;
import core.auth.AuthResponse;
import core.game.world.GameWorld;
import core.net.producer.HSEventProducer;
import core.net.producer.LoginEventProducer;
import core.game.world.repository.Repository;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IoSession {

	private static final EventProducer HANDSHAKE_PRODUCER = new HSEventProducer();

	private final SelectionKey key;

	public int isaacInputOpcode = 0;

	private final ExecutorService service;

	private EventProducer producer = HANDSHAKE_PRODUCER;

	private List<ByteBuffer> writingQueue = new ArrayList<>(20);

	private ByteBuffer readingQueue;

	private Lock writingLock = new ReentrantLock();

	private ISAACPair isaacPair;

	private int nameHash;

	private long serverKey;

	private int js5Encryption;

	private Object object;

	private boolean active = true;

	private long lastPing;

	private final String address;

	private final JS5Queue js5Queue;

	private ClientInfo clientInfo;

	public String associatedUsername;

	public IoSession(SelectionKey key, ExecutorService service) {
		this.key = key;
		this.service = service;
		this.address = getRemoteAddress().replaceAll("/", "").split(":")[0];
		this.js5Queue = new JS5Queue(this);
	}

	public void write(Object context) {
		write(context, false);
	}

	public void write(Object context, boolean instant) {
		if (context == null) {
			throw new IllegalStateException("Invalid writing context!");
		}
		if (!(context instanceof AuthResponse) && producer instanceof LoginEventProducer) {

			return;
		}
		if (instant) {
			producer.produceWriter(this, context).run();
			return;
		}
		service.execute(producer.produceWriter(this, context));
	}

	public void queue(ByteBuffer buffer) {
		try {
			writingLock.tryLock(1000L, TimeUnit.MILLISECONDS);
		} catch (Exception e){
			e.printStackTrace();
			writingLock.unlock();
		}
		writingQueue.add(buffer);
		writingLock.unlock();
		write();
	}

	public void write() {
		if (!key.isValid()) {
			disconnect();
			return;
		}
		try {
			writingLock.tryLock(1000L, TimeUnit.MILLISECONDS);
		} catch (Exception e){
			e.printStackTrace();
			writingLock.unlock();
			return;
		}
		SocketChannel channel = (SocketChannel) key.channel();
		try {
			while (!writingQueue.isEmpty()) {
				ByteBuffer buffer = writingQueue.get(0);
				channel.write(buffer);
				if (buffer.hasRemaining()) {
					key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
					break;
				}
				writingQueue.remove(0);
			}
		} catch (IOException e) {
			disconnect();
		}
		writingLock.unlock();
	}

	public void disconnect() {
		try {
			if (!active) {
				return;
			}
			active = false;
			key.cancel();
			SocketChannel channel = (SocketChannel) key.channel();
			channel.socket().close();
			if (getPlayer() != null) {
				try {
					getPlayer().clear();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			object = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getAddress() {
		return address;
	}

	public String getRemoteAddress() {
		try {
			return ((SocketChannel) key.channel()).getRemoteAddress().toString();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public EventProducer getProducer() {
		return producer;
	}

	public void setProducer(EventProducer producer) {
		this.producer = producer;
	}

	public ByteBuffer getReadingQueue() {
		synchronized (this) {
			return readingQueue;
		}
	}

	public void setReadingQueue(ByteBuffer readingQueue) {
		synchronized (this) {
			this.readingQueue = readingQueue;
		}
	}

	public Lock getWritingLock() {
		return writingLock;
	}

	public SelectionKey getKey() {
		return key;
	}

	public boolean isActive() {
		return active;
	}

	public int getJs5Encryption() {
		return js5Encryption;
	}

	public void setJs5Encryption(int js5Encryption) {
		this.js5Encryption = js5Encryption;
	}

	public Player getPlayer() {
		if (object == null) {
			object = Repository.getPlayerByName(associatedUsername);
		}
		return object instanceof Player ? ((Player) object) : null;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public long getLastPing() {
		return lastPing;
	}

	public void setLastPing(long lastPing) {
		this.lastPing = lastPing;
	}

	public int getNameHash() {
		return nameHash;
	}

	public void setNameHash(int nameHash) {
		this.nameHash = nameHash;
	}

	public long getServerKey() {
		return serverKey;
	}

	public void setServerKey(long serverKey) {
		this.serverKey = serverKey;
	}

	public ISAACPair getIsaacPair() {
		return isaacPair;
	}

	public void setIsaacPair(ISAACPair isaacPair) {
		this.isaacPair = isaacPair;
	}

	public JS5Queue getJs5Queue() {
		return js5Queue;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

}