package core.net.packet;

public interface OutgoingPacket<Context> {

	public void send(Context context);

}