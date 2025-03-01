package core.net;

import java.nio.channels.CancelledKeyException;

public abstract class IoWriteEvent implements Runnable {

	private final IoSession session;

	private final Object context;

	public IoWriteEvent(IoSession session, Object context) {
		this.session = session;
		this.context = context;
	}

	@Override
	public void run() {
		try {
			write(session, context);
		} catch (Throwable t) {
			if (!(t instanceof CancelledKeyException)) {
				t.printStackTrace();
			}
			session.disconnect();
		}
	}

	public abstract void write(IoSession session, Object context);

}