package core.game.node.entity.player.info;

import core.ServerConstants;
import core.game.node.entity.player.Player;

public enum Rights {

    REGULAR_PLAYER,

    PLAYER_MODERATOR,

    ADMINISTRATOR() {
        @Override
        public boolean isVisible(Player player) {

            return player.getAttribute("visible_rank", ADMINISTRATOR) == ADMINISTRATOR;
        }
    };

    public static int getChatIcon(Player player) {

        Rights c = player.getAttribute("visible_rank", player.getDetails().getRights());

        if (c != Rights.REGULAR_PLAYER && c != null) {
            return c.toInteger();
        }

        if (ServerConstants.IRONMAN_ICONS) {
            if (player.getIronmanManager().isIronman()) {

                return player.getIronmanManager().getMode().icon;
            }
        }

        return 0;
    }

    public static boolean isHidden(final Player player) {

        return player.getAttribute("visible_rank", player.getDetails().getRights()) != player.getDetails().getRights();
    }

    public final int toInteger() {

        return ordinal();
    }

    public static Rights forId(int id) {

        if (id < 0) {
            id = 0;
        }

        return values()[id];
    }

    public boolean isVisible(Player username) {

        return true;
    }
}