package core.game.node.scenery;

import core.cache.def.impl.SceneryDefinition;
import core.cache.def.impl.VarbitDefinition;
import core.game.interaction.DestinationFlag;
import core.game.interaction.InteractPlugin;
import core.game.node.Node;
import core.game.node.entity.impl.GameAttributes;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static core.api.ContentAPIKt.setVarbit;
import static core.api.ContentAPIKt.setVarp;

public class Scenery extends Node {

    private final int id;

    private final int type;

    private int rotation;

    private final SceneryDefinition definition;

    private Pulse restorePulse;

    private Pulse destructionPulse;

    private int charge = 1000;

    private final GameAttributes attributes = new GameAttributes();

    private final Scenery[] childs;

    private Scenery wrapper;

    public Scenery(int id, int x, int y, int z) {
        this(id, Location.create(x, y, z), 10, 0);
    }

    public Scenery(int id, Location location) {
        this(id, location, 10, 0);
    }

    public Scenery(int id, Location location, int rotation) {
        this(id, location, 10, rotation);
    }

    public Scenery(int id, Location location, int rotation, Direction direction) {
        this(id, location, 10, rotation);
    }

    public Scenery(int id, int x, int y, int z, int type, int rotation) {
        this(id, Location.create(x, y, z), type, rotation);
    }

    public Scenery(int id, int type, int rotation) {
        this(id, Location.create(0, 0, 0), type, rotation);
    }

    public Scenery(int id, Location location, int type, int rotation) {
        super(SceneryDefinition.forId(id).getName(), location);
        if (rotation < 0) {
            rotation += 4;
        }
        if (id < 1) {
            type = 22;
        }
        super.destinationFlag = DestinationFlag.OBJECT;
        super.direction = Direction.get(rotation);
        super.interactPlugin = new InteractPlugin(this);
        this.rotation = rotation;
        this.id = id;
        this.location = location;
        this.type = type;
        this.definition = SceneryDefinition.forId(id);
        super.size = definition.sizeX;
        if (definition.configObjectIds != null && definition.configObjectIds.length > 0) {
            this.childs = new Scenery[definition.configObjectIds.length];
            for (int i = 0; i < childs.length; i++) {
                childs[i] = transform(definition.configObjectIds[i]);
                childs[i].wrapper = this;
            }
        } else {
            childs = null;
        }
    }

    public Scenery(Scenery other) {
        this(other.getId(), other.getLocation(), other.getType(), other.getRotation());
    }

    public void remove() {
    }

    public int getSizeX() {
        if (direction.toInteger() % 2 != 0) {
            return definition.sizeY;
        }
        return definition.sizeX;
    }

    public int getSizeY() {
        if (direction.toInteger() % 2 != 0) {
            return definition.sizeX;
        }
        return definition.sizeY;
    }

    @Override
    public void setActive(boolean active) {
        if (super.active && !active && destructionPulse != null) {
            destructionPulse.pulse();
        }
        super.setActive(active);
    }

    public Scenery getChild(Player player) {
        if (childs != null) {
            SceneryDefinition def = definition.getChildObject(player);
            for (Scenery child : childs) {
                if (child.getId() == def.getId()) {
                    return child;
                }
            }
        }
        return this;
    }

    public void setChildIndex(Player player, int index) {
        SceneryDefinition def = getDefinition();
        if (childs == null && wrapper != null) {
            def = wrapper.getDefinition();
        }
        if (def.getVarbitID() > -1) {
            VarbitDefinition config = def.getConfigFile();
            if (config != null) {
                setVarbit(player, def.getVarbitID(), index);
            }
        } else if (def.getConfigId() > -1) {
            setVarp(player, def.getConfigId(), index);
        }
    }

    public Scenery transform(int id) {
        return new Scenery(id, location, type, rotation);
    }

    public Scenery transform(int id, int rotation) {
        return new Scenery(id, location, type, rotation);
    }

    public Scenery transform(int id, int rotation, Location location) {
        return new Scenery(id, location, type, rotation);
    }

    public Scenery transform(int id, int rotation, int type) {
        return new Scenery(id, location, type, rotation);
    }

    public boolean isPermanent() {
        return true;
    }

    public Constructed asConstructed() {
        return new Constructed(id, location, type, rotation);
    }

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rot) {
        rotation = rot;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public SceneryDefinition getDefinition() {
        return definition;
    }

    @Override
    public Location getCenterLocation() {
        return location.transform(getSizeX() >> 1, getSizeY() >> 1, 0);
    }

    @Override
    public boolean equals(java.lang.Object obj) {
        if (obj == null || !(obj instanceof Scenery)) {
            return false;
        }
        Scenery other = (Scenery) obj;
        return other.id == id && other.location.equals(location) && rotation == other.rotation && other.type == type;
    }

    @Override
    public String toString() {
        return "[Scenery " + id + ", " + location + ", type=" + type + ", rot=" + rotation + "]";
    }

    public Pulse getRestorePulse() {
        return restorePulse;
    }

    public void setRestorePulse(Pulse restorePulse) {
        this.restorePulse = restorePulse;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public Pulse getDestructionPulse() {
        return destructionPulse;
    }

    public void setDestructionPulse(Pulse destructionPulse) {
        this.destructionPulse = destructionPulse;
    }

    public GameAttributes getAttributes() {
        return attributes;
    }

    public Scenery[] getChilds() {
        return childs;
    }

    public Scenery getWrapper() {
        if (wrapper == null) {
            return this;
        }
        return wrapper;
    }

    public void setWrapper(Scenery wrapper) {
        this.wrapper = wrapper;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @NotNull
    public List<Location> getOccupiedTiles() {
        List<Location> occupied = new ArrayList<>();
        occupied.add(location);

        int sizeX = getSizeX();
        int sizeY = getSizeY();

        if (rotation % 2 == 1) {
            int tmp = sizeX;
            sizeX = sizeY;
            sizeY = tmp;
        }

        boolean sub = rotation >= 2;

        if (sizeX > 1) {
            for (int i = 1; i < sizeX; i++) {
                int modifier = sub ? -i : i;
                occupied.add(location.transform(modifier, 0, 0));
            }
        }

        if (sizeY > 1) {
            for (int i = 1; i < sizeY; i++) {
                int modifier = sub ? -i : i;
                occupied.add(location.transform(0, modifier, 0));
            }
        }

        return occupied;
    }
}
