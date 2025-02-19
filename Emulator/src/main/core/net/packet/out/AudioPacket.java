package core.net.packet.out;

import core.game.node.entity.player.link.audio.Audio;
import core.game.world.map.Location;
import core.net.packet.IoBuffer;
import core.net.packet.OutgoingPacket;
import core.net.packet.context.DefaultContext;

public class AudioPacket implements OutgoingPacket<DefaultContext> {

    public static IoBuffer write(IoBuffer buffer, Audio audio, Location loc) {
		if(loc == null) {
            buffer.put((byte) 172);
            buffer.putShort(audio.id);
            buffer.put((byte) audio.loops);
            buffer.putShort(audio.delay);
        } else {
            buffer.put((byte) 97);
            buffer.put((byte) (loc.getChunkOffsetX() << 4 | loc.getChunkOffsetY()));
            buffer.putShort(audio.id);
            buffer.put((byte) (audio.radius << 4 | audio.loops & 7));
            buffer.put((byte) audio.delay);
        }
        return buffer;
    }

	//208 music effect
	//4 music
	//172 sound effect
	@Override
	public void send(DefaultContext context) {
		final Audio audio = (Audio) context.getObjects()[0];
        final Location loc = (Location) context.getObjects()[1];
        IoBuffer buffer;
        if(loc == null) {
            buffer = new IoBuffer();
        } else {
            buffer = UpdateAreaPosition.getBuffer(context.getPlayer(), loc.getChunkBase());
        }
        write(buffer, audio, loc);
        buffer.cypherOpcode(context.getPlayer().getSession().getIsaacPair().getOutput());
        context.getPlayer().getSession().write(buffer);
	}

}
