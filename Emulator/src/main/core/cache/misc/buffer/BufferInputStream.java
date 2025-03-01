package core.cache.misc.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class BufferInputStream extends InputStream {

	private final ByteBuffer buffer;

	public BufferInputStream(ByteBuffer buffer) throws IOException {
		this.buffer = buffer;
	}

	@Override
	public int read() throws IOException {
		return buffer.get();
	}

	public ByteBuffer getBuffer() {
		return buffer;
	}

}
