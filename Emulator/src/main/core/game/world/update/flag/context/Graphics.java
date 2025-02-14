package core.game.world.update.flag.context;

import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.chunk.GraphicUpdateFlag;

public class Graphics {

    private final int id;

    private final int height;

    private final int delay;

    public Graphics(int id) {
        this(id, 0, 0);
    }

    public Graphics(int id, int height) {
        this(id, height, 0);
    }

    public Graphics(int id, int height, int delay) {
        this.id = id;
        this.height = height;
        this.delay = delay;
    }

    public static Graphics create(int id) {
        return new Graphics(id, 0, 0);
    }

    public static Graphics create(int id, int height) {
        return new Graphics(id, height, 0);
    }

    public static void send(Graphics graphics, Location l) {
        RegionManager.getRegionChunk(l).flag(new GraphicUpdateFlag(graphics, l));
    }

    public int getId() {
        return id;
    }

    public int getHeight() {
        return height;
    }

    public int getDelay() {
        return delay;
    }

    @Override
    public String toString() {
        return "Graphics [id=" + id + ", height=" + height + ", delay=" + delay + "]";
    }
}