package core.game.node.entity.state;

import core.game.node.entity.Entity;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;

import java.nio.ByteBuffer;

public abstract class StatePulse extends Pulse {

    protected final Entity entity;

    public StatePulse(Entity entity, int ticks) {
        super(ticks, entity);
        super.stop();
        this.entity = entity;
    }

    public abstract boolean isSaveRequired();

    public abstract void save(ByteBuffer buffer);

    public abstract StatePulse parse(Entity entity, ByteBuffer buffer);

    public abstract StatePulse create(Entity entity, Object... args);

    public boolean canRun(Entity entity) {
        return true;
    }

    public void remove() {

    }

    public void run() {
        if (isRunning()) {
            return;
        }
        restart();
        start();
        GameWorld.getPulser().submit(this);
    }

}