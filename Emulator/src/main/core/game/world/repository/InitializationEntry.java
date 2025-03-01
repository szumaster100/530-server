package core.game.world.repository;

import core.game.node.Node;

public class InitializationEntry {

    private final Node node;

    private boolean removal;

    public InitializationEntry(Node node) {
        this(node, false);
    }

    public InitializationEntry(Node node, boolean removal) {
        this.node = node;
        this.removal = removal;
    }

    public Node initialize() {
        node.setActive(true);
        return node;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InitializationEntry)) {
            return false;
        }
        return ((InitializationEntry) o).node.equals(node);
    }

    @Override
    public int hashCode() {
        if (node != null) {
            return node.hashCode();
        }
        return super.hashCode();
    }

    public Node getNode() {
        return node;
    }

    public boolean isRemoval() {
        return removal;
    }

    public void setRemoval(boolean removal) {
        this.removal = removal;
    }
}