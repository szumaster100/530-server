package core.net.registry;

import core.ServerConstants;
import core.auth.UserAccountInfo;
import core.cache.misc.buffer.ByteBufferUtils;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.net.Constants;
import core.net.IoSession;
import core.net.packet.in.Login;
import core.tools.Log;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static core.api.ContentAPIKt.log;

public class AccountRegister {

	private static final Pattern PATTERN = Pattern.compile("[a-z0-9_]{1,12}");

	public static void read(final IoSession session, int opcode, ByteBuffer buffer) {
		int day,month,year,country;
		UserAccountInfo info = UserAccountInfo.createDefault();
		switch (opcode) {
			case 147://details
				day = buffer.get();
				month = buffer.get();
				year = buffer.getShort();
				country = buffer.getShort();
				response(session, RegistryResponse.SUCCESS);
				break;
			case 186://username
				final String username = ByteBufferUtils.getString(buffer).replace(" ", "_").toLowerCase().replace("|", "");
				info.setUsername(username);
				if (username.length() <= 0 || username.length() > 12) {
					response(session, RegistryResponse.INVALID_USERNAME);
					break;
				}
				if (invalidUsername(username)) {

					response(session,RegistryResponse.INVALID_USERNAME);
					break;
				}

				if (!GameWorld.getAuthenticator().canCreateAccountWith(info)) {
					response(session, RegistryResponse.NOT_AVAILABLE_USER);
					return;
				}
				response(session, RegistryResponse.SUCCESS);
				break;
			case 36://Register details
				buffer.get(); //Useless size being written that is already written in the RSA block
				buffer = Login.decryptRSABuffer(buffer, ServerConstants.EXPONENT, ServerConstants.MODULUS);
				if(buffer.get() != 10){ //RSA header (aka did this decrypt properly)
					log(AccountRegister.class, Log.ERR, "Decryption failed during registration :(");
					response(session, RegistryResponse.CANNOT_CREATE);
					break;
				}
				buffer.getShort(); // random data
				int revision = buffer.getShort();//revision?
				if (revision != Constants.REVISION) {
					response(session, RegistryResponse.CANNOT_CREATE);
					break;
				}
				final String name = ByteBufferUtils.getString(buffer).replace(" ", "_").toLowerCase().replace("|", "");
				buffer.getInt();
				String password = ByteBufferUtils.getString(buffer);
				info.setUsername(name);
				info.setPassword(password);
				if (password.length() < 5 || password.length() > 20) {
					response(session, RegistryResponse.INVALID_PASS_LENGTH);
					break;
				}
				if (password.equals(name)) {
					response(session, RegistryResponse.PASS_SIMILAR_TO_USER);
					break;
				}
				if (invalidUsername(name)) {
					response(session, RegistryResponse.INVALID_USERNAME);
					break;
				}
				buffer.getInt();
				buffer.getShort();
				day = buffer.get();
				month = buffer.get();
				buffer.getInt();
				year = buffer.getShort();
				country = buffer.getShort();
				buffer.getInt();
				if (!GameWorld.getAuthenticator().canCreateAccountWith(info)) {
					response(session, RegistryResponse.CANNOT_CREATE);
					return;
				}
				GameWorld.getAuthenticator().createAccountWith(info);
				GameWorld.getPulser().submit(new Pulse() {
					@Override
					public boolean pulse() {
						response(session, RegistryResponse.SUCCESS);
						return true;
					}
				});
				break;
			default:
				log(AccountRegister.class, Log.ERR, "Unhandled account registry opcode = " + opcode);
				break;
		}
	}

	private static void response(IoSession session, RegistryResponse response) {
		ByteBuffer buf = ByteBuffer.allocate(100);
		buf.put((byte) response.getId());
		session.queue(buf.flip());
	}

	public static boolean invalidUsername(final String username) {
		Matcher matcher = PATTERN.matcher(username);
		return !matcher.matches();
	}
}
