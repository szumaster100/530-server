package core.net.event;

import core.net.IoReadEvent;
import core.net.IoSession;
import core.net.lobby.WorldList;
import core.net.registry.AccountRegister;
import core.tools.Log;
import core.tools.RandomFunction;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.log;

public final class HSReadEvent extends IoReadEvent {

	static Map<String, Integer> count = new HashMap<>();

	public HSReadEvent(IoSession session, ByteBuffer buffer) {
		super(session, buffer);
	}

	@Override
	public void read(IoSession session, ByteBuffer buffer) {
		Integer amount = count.get(session.getAddress());
		if (amount == null) {
			amount = 0;
		}
		count.put(session.getAddress(), amount + 1);
		int opcode = buffer.get() & 0xFF;
		switch (opcode) {
		case 14:
			session.setNameHash(buffer.get() & 0xFF);
			session.setServerKey(RandomFunction.RANDOM.nextLong());
			session.write(true);
			break;
		case 15:
			int revision = buffer.getInt();
			//int sub_revision = buffer.getInt();
			buffer.flip();

			if (revision != 530 ){// || sub_revision != Constants.CLIENT_BUILD) {
				session.disconnect();
				break;
			}
			session.write(false);
			break;
		case 147:
		case 186:
		case 36:
			AccountRegister.read(session, opcode, buffer);
			break;
		case 255: // World list
			int updateStamp = buffer.getInt();
			WorldList.sendUpdate(session, updateStamp);
			break;
		default:
			log(this.getClass(), Log.FINE, "PKT " + opcode);
			session.disconnect();
			break;
		}
	}

}