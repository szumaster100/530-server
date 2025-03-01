package core.plugin;

import core.game.node.entity.player.Player;

public interface Plugin<T> {

    public Plugin<T> newInstance(T arg) throws Throwable;

    Object fireEvent(String identifier, Object... args);

    public default void handleSelectionCallback(int skill, Player player){}

}