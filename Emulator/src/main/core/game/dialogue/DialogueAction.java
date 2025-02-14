package core.game.dialogue;

import core.game.node.entity.player.Player;

public interface DialogueAction {

    void handle(Player player, int buttonId);

}
