package core.game.world.map;

public final class Point {

	private final int x;

	private final int y;

	private final int diffX;

	private final int diffY;

	private final Direction direction;

	private boolean runDisabled;

	public Point(int x, int y) {
		this(x, y, null, 0, 0);
	}

	public Point(int x, int y, Direction direction) {
		this(x, y, direction, 0, 0);
	}

	public Point(int x, int y, Direction direction, int diffX, int diffY) {
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.diffX = diffX;
		this.diffY = diffY;
	}

	public Point(int x, int y, Direction direction, int diffX, int diffY, boolean runDisabled) {
		this(x, y, direction, diffX, diffY);
		this.runDisabled = runDisabled;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Direction getDirection() {
		return direction;
	}

	public int getDiffX() {
		return diffX;
	}

	public int getDiffY() {
		return diffY;
	}

	public boolean isRunDisabled() {
		return runDisabled;
	}

	public void setRunDisabled(boolean runDisabled) {
		this.runDisabled = runDisabled;
	}
}