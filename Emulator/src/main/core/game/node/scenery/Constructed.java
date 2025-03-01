package core.game.node.scenery;

import core.game.node.item.Item;
import core.game.world.map.Location;

public class Constructed extends Scenery {

    private Scenery replaced;

    private Item[] items;

    public Constructed(int id, int x, int y, int z) {
        super(id, Location.create(x, y, z), 10, 0);
    }

    public Constructed(int id, Location location) {
        super(id, location, 10, 0);
    }

    public Constructed(int id, Location location, int rotation) {
        super(id, location, 10, rotation);
    }

    public Constructed(int id, int x, int y, int z, int type, int rotation) {
        super(id, Location.create(x, y, z), type, rotation);
    }

    public Constructed(int id, int type, int rotation) {
        super(id, Location.create(0, 0, 0), type, rotation);
    }

    public Constructed(int id, Location location, int type, int rotation) {
        super(id, location, type, rotation);
    }

    @Override
    public boolean isPermanent() {
        return false;
    }

    @Override
    public Constructed asConstructed() {
        return this;
    }

    public Scenery getReplaced() {
        return replaced;
    }

    public void setReplaced(Scenery replaced) {
        this.replaced = replaced;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }
}
