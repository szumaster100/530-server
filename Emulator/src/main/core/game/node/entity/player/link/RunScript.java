package core.game.node.entity.player.link;

import core.game.node.entity.player.Player;

public abstract class RunScript {

    protected Player player;

    protected Object value;

    public RunScript() {

    }

    public abstract boolean handle();

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
