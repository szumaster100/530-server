package core.game.system.command;

import core.game.node.entity.player.Player;
import core.game.node.entity.player.info.Rights;
import core.game.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public enum CommandSet {

    PLAYER(),

    MODERATOR() {
        @Override
        public boolean validate(Player player) {
            return player.getDetails().getRights().ordinal() > 0;
        }
    },

    ADMINISTRATOR() {
        @Override
        public boolean validate(Player player) {
            return player.getDetails().getRights().equals(Rights.ADMINISTRATOR);
        }
    },

    DEVELOPER() {
        @Override
        public boolean validate(Player player) {
            return player.getDetails().getRights().equals(Rights.ADMINISTRATOR);
        }
    },

    BETA() {
        @Override
        public boolean validate(Player player) {
            return GameWorld.getSettings().isBeta() || ADMINISTRATOR.validate(player) || GameWorld.getSettings().isDevMode();
        }
    };

    private final List<CommandPlugin> plugins = new ArrayList<>(20);

    private CommandSet() {
    }

    public boolean validate(final Player player) {
        return true;
    }

    public boolean interpret(final Player player, final String name, final String... arguments) {
        if (player == null) {
            return false;
        }
        if (!validate(player)) {
            return false;
        }
        if (player.getZoneMonitor().parseCommand(player, name, arguments)) {
            return true;
        }
        for (int i = 0; i < plugins.size(); i++) {
            CommandPlugin plugin = plugins.get(i);
            if (!plugin.validate(player)) {
                continue;
            }
            if (plugin.parse(player, name, arguments)) {
                return true;
            }
        }
        return false;
    }

    public List<CommandPlugin> getPlugins() {
        return plugins;
    }
}
