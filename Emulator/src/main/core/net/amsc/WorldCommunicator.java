package core.net.amsc;

import core.game.node.entity.player.info.login.LoginParser;
import core.game.world.GameWorld;
import core.net.EventProducer;
import core.net.IoSession;
import core.net.NioReactor;
import core.net.producer.MSHSEventProducer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class WorldCommunicator {

	private static final EventProducer HANDSHAKE_PRODUCER = new MSHSEventProducer();

	private static ManagementServerState state = ManagementServerState.CONNECTING;

	private static IoSession session;

	private static final WorldStatistics[] WORLDS = new WorldStatistics[10];

	private static final Map<String, LoginParser> loginAttempts = new ConcurrentHashMap<>();

	private static NioReactor reactor;

	public static void register(IoSession session) {
		WorldCommunicator.session = session;
		session.setProducer(HANDSHAKE_PRODUCER);
		session.write(true);
		WORLDS[GameWorld.getSettings().getWorldId() - 1] = new WorldStatistics(GameWorld.getSettings().getWorldId());
		session.setObject(WORLDS[GameWorld.getSettings().getWorldId() - 1]);
	}

	public static void connect() {
		try {
			setState(ManagementServerState.CONNECTING);

				reactor = NioReactor.connect(GameWorld.getSettings().getMsAddress(), 5555);
			//}
			reactor.start();
		} catch (Throwable e) {
			e.printStackTrace();
			terminate();
		}
	}

	private static boolean isLocallyHosted() throws IOException {
		InetAddress address = InetAddress.getByName(GameWorld.getSettings().getMsAddress());
		if (address.isAnyLocalAddress() || address.isLoopbackAddress()) {
			return true;
		}
		return NetworkInterface.getByInetAddress(address) != null;
	}

	public static void terminate() {
		setState(ManagementServerState.NOT_AVAILABLE);
		if (reactor != null) {
			reactor.terminate();
			reactor = null;
		}
	}

	public static LoginParser finishLoginAttempt(String username) {
		return loginAttempts.remove(username);
	}

	public static WorldStatistics getLocalWorld() {
		return WORLDS[GameWorld.getSettings().getWorldId() - 1];
	}

	public static int getWorld(String playerName) {
		for (int i = 0; i < WORLDS.length; i++) {
			if (WORLDS[i].getPlayers().contains(playerName)) {
				return i;
			}
		}
		return -1;
	}

	public static WorldStatistics getWorld(int id) {
		return WORLDS[id - 1];
	}

	public static IoSession getSession() {
		return session;
	}

	public static boolean isEnabled() {
		return state == ManagementServerState.AVAILABLE;
	}

	public static Map<String, LoginParser> getLoginAttempts() {
		return loginAttempts;
	}

	public static ManagementServerState getState() {
		return state;
	}

	public static void setState(ManagementServerState state) {
		if (WorldCommunicator.state != state) {
			WorldCommunicator.state = state;
			state.set();
		}
	}

	public static NioReactor getReactor() {
		return reactor;
	}

	public static void setReactor(NioReactor reactor) {
		WorldCommunicator.reactor = reactor;
	}

}