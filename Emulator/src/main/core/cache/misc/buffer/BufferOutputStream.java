package core.cache.misc.buffer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public final class BufferOutputStream extends OutputStream {

	private final ByteBuffer buffer;

	public BufferOutputStream(ByteBuffer buffer) throws IOException, SecurityException {
		super();
		this.buffer = buffer;
	}

	@Override
	public void write(int b) throws IOException {
		buffer.put((byte) b);
	}

	@Override
	public void flush() {

	}

	@Override
	public void close() {

	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

}