package core.net;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a queue for handling JS5 requests.
 * This queue manages requests for retrieving archive data and processes them asynchronously.
 */
public final class JS5Queue {

	/**
	 * Flag indicating whether the request handler should keep running.
	 */
	public static AtomicBoolean RUNNING = new AtomicBoolean(true);

	private static final Js5QueueHandler handler = new Js5QueueHandler();

	/**
	 * A separate thread responsible for handling JS5 requests from the queue.
	 */
	private static class Js5QueueHandler extends Thread {

		private final LinkedBlockingDeque<Js5Request> requests = new LinkedBlockingDeque<>();

		@Override
		public void run() {
			while (RUNNING.get()) {
				try {
					Js5Request request = requests.take();
					JS5Queue queue = request.queue;

					if (queue.session.isActive()) {
						queue.session.write(new int[]{request.index, request.archive, request.priority ? 1 : 0});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	static {
		try {
			handler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Represents a single JS5 request.
	 */
	private static class Js5Request {

		private final JS5Queue queue;
		private final int index;
		private final int archive;
		private final boolean priority;

		/**
		 * Constructs a new JS5 request.
		 *
		 * @param queue    the queue this request belongs to
		 * @param index    the index of the requested archive
		 * @param archive  the specific archive within the index
		 * @param priority whether this request is high priority
		 */
		public Js5Request(JS5Queue queue, int index, int archive, boolean priority) {
			this.queue = queue;
			this.index = index;
			this.archive = archive;
			this.priority = priority;
		}
	}

	private final IoSession session;

	/**
	 * Constructs a new JS5Queue for handling requests for the given session.
	 *
	 * @param session the session associated with this queue
	 */
	public JS5Queue(IoSession session) {
		this.session = session;
	}

	/**
	 * Queues a request for retrieving an archive.
	 *
	 * @param container    the index of the archive
	 * @param archive      the specific archive within the index
	 * @param highPriority whether the request should be handled as high priority
	 */
	public void queue(int container, int archive, boolean highPriority) {
		Js5Request request = new Js5Request(this, container, archive, highPriority);
		handler.requests.add(request);
	}

	/**
	 * Retrieves the session associated with this queue.
	 *
	 * @return the IoSession associated with this queue
	 */
	public IoSession getSession() {
		return session;
	}
}
