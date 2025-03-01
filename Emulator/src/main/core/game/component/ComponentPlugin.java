package core.game.component;

import core.game.node.entity.player.Player;
import core.plugin.Plugin;

public abstract class ComponentPlugin implements Plugin<Object> {

    public abstract boolean handle(final Player player, Component component, final int opcode, final int button, int slot, int itemId);

    public void open(Player player, Component component) {
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

}
