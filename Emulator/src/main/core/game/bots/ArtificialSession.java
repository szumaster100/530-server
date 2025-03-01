package core.game.bots;

import core.net.IoSession;

import java.nio.ByteBuffer;

public final class ArtificialSession extends IoSession {

    private static final ArtificialSession SINGLETON = new ArtificialSession();

    private ArtificialSession() {
        super(null, null);
    }

    @Override
    public String getRemoteAddress() {
        return "127.0.0.1";
    }

    @Override
    public void write(Object context, boolean instant) {

    }

    @Override
    public void queue(ByteBuffer buffer) {
    }

    @Override
    public void write() {
    }

    @Override
    public void disconnect() {
    }

    public static ArtificialSession getSingleton() {
        return SINGLETON;
    }
}