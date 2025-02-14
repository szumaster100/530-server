package core.game.world.update.flag.chunk;

import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.node.item.GroundItem;
import core.game.world.update.flag.UpdateFlag;
import core.net.packet.IoBuffer;
import core.net.packet.out.ClearGroundItem;
import core.net.packet.out.ConstructGroundItem;
import core.net.packet.out.UpdateGroundItemAmount;

public final class ItemUpdateFlag extends UpdateFlag<Object> {

    public static final int CONSTRUCT_TYPE = 0;

    public static final int REMOVE_TYPE = 1;

    public static final int UPDATE_AMOUNT_TYPE = 2;

    private final GroundItem item;

    private final int type;

    private int oldAmount;

    public ItemUpdateFlag(GroundItem item, int type) {
        this(item, type, 0);
    }

    public ItemUpdateFlag(GroundItem item, int type, int oldAmount) {
        super(null);
        this.item = item;
        this.type = type;
        this.oldAmount = oldAmount;
    }

    @Override
    public void writeDynamic(IoBuffer buffer, Entity e) {
        if (!isRemove() && item.droppedBy((Player) e)) {
            return;
        }
        write(buffer);
    }

    @Override
    public void write(IoBuffer buffer) {
        if (isRemove()) {
            ClearGroundItem.write(buffer, item);
        } else if (isConstruct()) {
            ConstructGroundItem.write(buffer, item);
        } else {
            UpdateGroundItemAmount.write(buffer, item, oldAmount);
        }
    }

    public boolean isRemove() {
        return type == REMOVE_TYPE;
    }

    public boolean isConstruct() {
        return type == CONSTRUCT_TYPE;
    }

    public boolean isAmountUpdate() {
        return type == UPDATE_AMOUNT_TYPE;
    }

    @Override
    public int data() {
        return 0;
    }

    @Override
    public int ordinal() {
        return 1;
    }

}