package content.global.skill.summoning.familiar;

import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.item.Item;

public class FamiliarSpecial {

    private Node node;

    private int interfaceID;

    private int component;

    private Item item;

    public FamiliarSpecial(Node node) {
        this(node, -1, -1, null);
    }

    public FamiliarSpecial(Node node, int interfaceID, int component, Item item) {
        this.node = node;
        this.interfaceID = interfaceID;
        this.component = component;
        this.item = item;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public int getInterfaceID() {
        return interfaceID;
    }

    public void setInterfaceID(int interfaceID) {
        this.interfaceID = interfaceID;
    }

    public int getComponent() {
        return component;
    }

    public void setComponent(int component) {
        this.component = component;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Entity getTarget() {
        return (Entity) node;
    }

}