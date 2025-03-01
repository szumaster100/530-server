package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class CameraContext implements Context {

	public static enum CameraType {
		POSITION(154),
		ROTATION(125),
		SET(187), SHAKE(27), RESET(24);

		private final int opcode;

		private CameraType(int opcode) {
			this.opcode = opcode;
		}

		public int opcode() {
			return opcode;
		}
	}

	private final Player player;

	private final CameraType type;

	private final int x;

	private final int y;

	private final int height;

	private final int zoomSpeed;

	private final int speed;

	public CameraContext(Player player, CameraType type, int x, int y, int height, int speed, int zoomSpeed) {
		this.player = player;
		this.type = type;
		this.x = x;
		this.y = y;
		this.height = height;
		this.speed = speed;
		this.zoomSpeed = zoomSpeed;
	}

	public CameraContext transform(final Player player, final int x, final int y) {
		return new CameraContext(player, type, this.x + x, this.y + y, height, speed, zoomSpeed);
	}

	public CameraContext transform(final int heightOffset) {
		return new CameraContext(player, type, x, y, height + heightOffset, speed, zoomSpeed);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public CameraType getType() {
		return type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getHeight() {
		return height;
	}

	public int getSpeed() {
		return speed;
	}

	public int getZoomSpeed() {
		return zoomSpeed;
	}

}