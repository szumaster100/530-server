package core.game.world.update.flag;

import core.game.node.entity.Entity;
import core.net.packet.IoBuffer;

public abstract class UpdateFlag<T> implements Comparable<UpdateFlag<?>> {

    protected T context;

    public UpdateFlag(T context) {
        this.context = context;
    }

    public abstract void write(IoBuffer buffer);

    public void writeDynamic(IoBuffer buffer, Entity e) {
        write(buffer);
    }

    public abstract int data();

    public abstract int ordinal();

    @Override
    public int compareTo(UpdateFlag<?> flag) {
        if (flag.ordinal() == ordinal()) {
            return 0;
        }
        if (flag.ordinal() < ordinal()) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UpdateFlag)) {
            return false;
        }
        return ((UpdateFlag<?>) o).data() == data() && ((UpdateFlag<?>) o).ordinal() == ordinal();
    }

    public T getContext() {
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }
}
