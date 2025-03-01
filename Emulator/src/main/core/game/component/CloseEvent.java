package core.game.component;

import core.game.node.entity.player.Player;

public interface CloseEvent {

    boolean close(Player player, Component c);

}