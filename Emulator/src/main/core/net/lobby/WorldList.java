package core.net.lobby;

import core.game.world.GameWorld;
import core.net.IoSession;
import core.net.packet.IoBuffer;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class WorldList {

	public static final int COUNTRY_AUSTRALIA = 16;

	public static final int COUNTRY_BELGIUM = 22;

	public static final int COUNTRY_BRAZIL = 31;

	public static final int COUNTRY_CANADA = 38;

	public static final int COUNTRY_DENMARK = 58;

	public static final int COUNTRY_FINLAND = 69;

	public static final int COUNTRY_IRELAND = 101;

	public static final int COUNTRY_MEXICO = 152;

	public static final int COUNTRY_NETHERLANDS = 161;

	public static final int COUNTRY_NORWAY = 162;

	public static final int COUNTRY_SWEDEN = 191;

	public static final int COUNTRY_UK = 77;

	public static final int COUNTRY_USA = 225;

	public static final int FLAG_NON_MEMBERS = 0;

	public static final int FLAG_MEMBERS = 1;

	public static final int FLAG_QUICK_CHAT = 2;

	public static final int FLAG_PVP = 4;

	public static final int FLAG_LOOTSHARE = 8;

	private static final List<WorldDefinition> WORLD_LIST = new ArrayList<WorldDefinition>();

	private static int updateStamp = 0;

	static {
		addWorld(new WorldDefinition(1, 0, FLAG_MEMBERS | FLAG_LOOTSHARE, "2009Scape Classic", "127.0.0.1", "Anywhere, USA", COUNTRY_USA));
	}

	public static void addWorld(WorldDefinition def) {
		WORLD_LIST.add(def);
		flagUpdate();
	}

	public static void sendUpdate(IoSession session, int updateStamp) {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		buf.put((byte) 0);
		buf.putShort((short) 0);
		buf.put((byte) 1);
		IoBuffer buffer = new IoBuffer();
		if (updateStamp != WorldList.updateStamp) {
			buf.put((byte) 1); // Indicates an update occured.
			putWorldListinfo(buffer);
		} else {
			buf.put((byte) 0);
		}
		putPlayerInfo(buffer);
		if (buffer.toByteBuffer().position() > 0) {
			buf.put((ByteBuffer) buffer.toByteBuffer().flip());
		}
		buf.putShort(1, (short) (buf.position() - 3));
		session.queue((ByteBuffer) buf.flip());
	}

	private static void putWorldListinfo(IoBuffer buffer) {
		buffer.putSmart(WORLD_LIST.size());
		putCountryInfo(buffer);
		buffer.putSmart(0);
		buffer.putSmart(WORLD_LIST.size());
		buffer.putSmart(WORLD_LIST.size());
		for (WorldDefinition w : WORLD_LIST) {
			buffer.putSmart(w.getWorldId());
			buffer.put(w.getLocation());
			buffer.putInt(w.getFlag());
			buffer.putJagString(w.getActivity());
			buffer.putJagString(w.getIp());
		}
		buffer.putInt(updateStamp);
	}

	private static void putPlayerInfo(IoBuffer buffer) {
		for (WorldDefinition w : WORLD_LIST) {
			buffer.putSmart(w.getWorldId());
			buffer.putShort(w.getPlayerCount());
		}
	}

	private static void putCountryInfo(IoBuffer buffer) {
		for (WorldDefinition w : WORLD_LIST) {
			buffer.putSmart(w.getCountry());
			buffer.putJagString(w.getRegion());
		}
	}

	public static int getUpdateStamp() {
		return updateStamp;
	}

	public static void flagUpdate() {
		WorldList.updateStamp = GameWorld.getTicks();
	}
}
