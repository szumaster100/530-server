package core.game.world.map;

public enum MapDistance {

	RENDERING(15),

	SOUND(5), ;

	private final int distance;

	private MapDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}
}