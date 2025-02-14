package core.game.interaction;

import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;

public final class NodeUsageEvent {

    private final Player player;

    private final int componentId;

    private final Node used;

    private final Node with;

    public NodeUsageEvent(Player player, int componentId, Node used, Node with) {
        this.player = player;
        this.componentId = componentId;
        this.used = used;
        this.with = with;
    }

    public Item getBaseItem() {
        return with instanceof Item ? (Item) with : null;
    }

    public Item getUsedItem() {
        return used instanceof Item ? (Item) used : null;
    }

    public Player getPlayer() {
        return player;
    }

    public int getComponentId() {
        return componentId;
    }

    public Node getUsed() {
        return used;
    }

    public Node getUsedWith() {
        return with;
    }

}
