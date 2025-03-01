package core.game.activity;

import core.game.node.entity.player.Player;
import core.game.world.GameWorld;
import core.tools.Log;

import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.log;

public final class ActivityManager {

    private static final Map<String, ActivityPlugin> ACTIVITIES = new HashMap<>();

    private ActivityManager() {

    }

    public static void register(ActivityPlugin plugin) {
        plugin.register();
        ACTIVITIES.put(plugin.getName(), plugin);
        if (!plugin.isInstanced()) {
            plugin.configure();
        }
    }

    public static boolean start(Player player, String name, boolean login, Object... args) {
        ActivityPlugin plugin = ACTIVITIES.get(name);
        if (plugin == null) {
            if (GameWorld.getSettings().isDevMode()) {
                log(ActivityManager.class, Log.ERR, "Unhandled activity - " + name + "!");
            }
            return false;
        }
        try {
            if (plugin.isInstanced()) {
                (plugin = plugin.newInstance(player)).configure();
            }
            return plugin.start(player, login, args);
        } catch (Throwable e) {
            e.printStackTrace();
            if (GameWorld.getSettings().isDevMode()) {
                player.getPacketDispatch().sendMessage("Error starting activity " + (plugin == null ? null : plugin.getName()) + "!");
            }
        }
        return false;
    }

    public static ActivityPlugin getActivity(String name) {
        return ACTIVITIES.get(name);
    }
}