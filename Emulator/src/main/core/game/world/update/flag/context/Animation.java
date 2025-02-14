package core.game.world.update.flag.context;

import core.cache.def.impl.AnimationDefinition;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.scenery.Scenery;

public class Animation {

    public static final Animation RESET = new Animation(-1, Priority.VERY_HIGH);

    private Priority priority;

    private int id;

    private final int delay;

    private AnimationDefinition definition;

    private Scenery scenery;

    public Animation(int id) {
        this(id, 0, Priority.MID);
    }

    public static Animation create(int id) {
        return new Animation(id, 0, Priority.MID);
    }

    public Animation(int id, Priority priority) {
        this(id, 0, priority);
    }

    public Animation(int id, int delay) {
        this(id, delay, Priority.MID);
    }

    public Animation(int id, int delay, Priority priority) {
        this.id = id;
        this.delay = delay;
        this.priority = priority;
    }

    public AnimationDefinition getDefinition() {
        if (definition == null) {
            definition = AnimationDefinition.forId(id);
        }
        return definition;
    }

    public int getDuration() {
        AnimationDefinition def = getDefinition();
        return def != null ? def.getDurationTicks() : 1;
    }

    public int getId() {
        return id;
    }

    public int getDelay() {
        return delay;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Priority getPriority() {
        return priority;
    }

    public Scenery getObject() {
        return scenery;
    }

    public void setObject(Scenery scenery) {
        this.scenery = scenery;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "Animation [priority=" + priority + ", id=" + id + "]";
    }
}