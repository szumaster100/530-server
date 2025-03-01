package core.game.node.entity.npc;

import core.game.world.map.Location;
import core.plugin.Plugin;
import core.tools.Log;

import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.log;

public abstract class AbstractNPC extends NPC implements Plugin<Object> {

    protected static Map<Integer, AbstractNPC> mapping = new HashMap<>();

    public AbstractNPC(int id, Location location) {
        this(id, location, true);
    }

    public AbstractNPC(int id, Location location, boolean autowalk) {
        super(id, location);
        super.setWalks(autowalk);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        for (int id : getIds()) {
            if (mapping.containsKey(id)) {
                String name = mapping.get(id).getClass().getSimpleName();
                if (name != getClass().getSimpleName()) {
                    log(this.getClass(), Log.ERR, "[" + getClass().getSimpleName() + "] - Warning: Mapping already contained NPC id " + id + "! (" + name + ")");
                    continue;
                }
            }
            mapping.put(id, this);
        }
        return this;
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    public abstract AbstractNPC construct(int id, Location location, Object... objects);

    public abstract int[] getIds();

    public static AbstractNPC forId(int npcId) {
        return mapping.get(npcId);
    }

}
