package core.game.node.entity.player.link.music;

import java.util.HashMap;
import java.util.Map;

public final class MusicEntry {

    private static final Map<Integer, MusicEntry> SONGS = new HashMap<>();

    private final int id;

    private final String name;

    private final int index;

    public MusicEntry(int id, String name, int index) {
        this.id = id;
        this.name = name;
        this.index = index;
    }

    public static MusicEntry forId(int id) {
        return SONGS.get(id);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public static Map<Integer, MusicEntry> getSongs() {
        return SONGS;
    }
}