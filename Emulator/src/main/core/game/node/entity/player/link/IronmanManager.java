package core.game.node.entity.player.link;

import core.game.node.entity.player.Player;

public class IronmanManager {

    private final Player player;

    private IronmanMode mode = IronmanMode.NONE;

    public IronmanManager(Player player) {
        this.player = player;
    }

    public boolean checkRestriction() {
        return checkRestriction(IronmanMode.STANDARD);
    }

    public boolean checkRestriction(IronmanMode mode) {
        if (isIronman() && this.mode.ordinal() >= mode.ordinal()) {
            player.sendMessage("You can't do that as an Ironman.");
            return true;
        }
        return false;
    }

    public boolean isIronman() {
        return mode != IronmanMode.NONE;
    }

    public Player getPlayer() {
        return player;
    }

    public IronmanMode getMode() {
        return mode;
    }

    public void setMode(IronmanMode mode) {
        this.mode = mode;
    }

}
