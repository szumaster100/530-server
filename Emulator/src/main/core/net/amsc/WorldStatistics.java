package core.net.amsc;

import java.util.ArrayList;
import java.util.List;

public final class WorldStatistics {

	private final int id;

	private final List<String> players = new ArrayList<>(20);

	public WorldStatistics(int id) {
		this.id = id;
	}

	public List<String> getPlayers() {
		return players;
	}

	public int getId() {
		return id;
	}
}