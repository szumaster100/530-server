package core.game.node.entity.player.link;

import core.api.ContainerListener;
import core.game.container.Container;
import core.game.container.ContainerEvent;
import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;

import java.util.ArrayList;
import java.util.List;

import static core.api.ContentAPIKt.*;

public final class SkullManager {

    public enum SkullIcon {
        NONE(-1),
        WHITE(0),
        RED(1),
        BH_RED5(2),
        BH_BLUE4(3),
        BH_GREEN3(4),
        BH_GREY2(5),
        BH_BROWN1(6),
        SCREAM(7);

        public final int id;

        SkullIcon(int id) {
            this.id = id;
        }

        public static SkullIcon forId(int id) {
            switch (id) {
                case 0:
                    return SkullIcon.WHITE;
                case 1:
                    return SkullIcon.RED;
                case 2:
                    return SkullIcon.BH_RED5;
                case 3:
                    return SkullIcon.BH_BLUE4;
                case 4:
                    return SkullIcon.BH_GREEN3;
                case 5:
                    return SkullIcon.BH_GREY2;
                case 6:
                    return SkullIcon.BH_BROWN1;
                case 7:
                    return SkullIcon.SCREAM;
                default:
                    return SkullIcon.NONE;
            }
        }
    }

    private final Player player;

    private boolean wilderness = false;

    private boolean wildernessDisabled = false;

    private int level;

    private final List<Player> skullCauses = new ArrayList<Player>();

    private boolean skulled;

    private boolean skullCheckDisabled;

    private boolean deepWilderness;

    public SkullManager(Player player) {
        this.player = player;
    }

    public void checkSkull(Entity other) {
        if (!(other instanceof Player) || !wilderness || skullCheckDisabled) {
            return;
        }
        Player o = (Player) other;
        for (Player p : o.getSkullManager().skullCauses) {
            if (p == player) {
                return;
            }
        }
        if (skullCauses.contains(o)) {
            return;
        }
        skullCauses.add(o);
        removeTimer(player, "skulled");
        registerTimer(player, spawnTimer("skulled", 2000));
    }

    public void setSkullIcon(int skullIcon) {
        player.getAppearance().setSkullIcon(skullIcon);
        player.updateAppearance();
    }

    public void reset() {
        skullCauses.clear();
        setSkullIcon(-1);
        setSkulled(false);
        player.getAppearance().sync();
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        if (!deepWilderness && level >= 49)
            setDeepWilderness(true);
        else if (deepWilderness && level < 48)
            setDeepWilderness(false);

        if (level > 20)
            player.getLocks().lockTeleport(1_000_000);
        else
            player.getLocks().unlockTeleport();

        this.level = level;
    }

    public boolean isWilderness() {
        return wilderness;
    }

    public void setWilderness(boolean wilderness) {
        this.wilderness = wilderness;
    }

    public boolean isSkullCheckDisabled() {
        return skullCheckDisabled;
    }

    public void setSkullCheckDisabled(boolean skullCheckDisabled) {
        this.skullCheckDisabled = skullCheckDisabled;
    }

    public boolean isWildernessDisabled() {
        return wildernessDisabled;
    }

    public boolean hasWildernessProtection() {
        return level < 49;
    }

    public void setWildernessDisabled(boolean wildernessDisabled) {
        this.wildernessDisabled = wildernessDisabled;
    }

    public boolean isSkulled() {
        return skulled || deepWilderness;
    }

    public boolean isDeepWilderness() {
        return deepWilderness;
    }

    public void setDeepWilderness(boolean deepWildy) {
        if (deepWildy) {
            updateDWSkullIcon();
        } else {
            removeDWSkullIcon();
        }
        setSkullCheckDisabled(deepWildy);
        deepWilderness = deepWildy;
    }

    public static final long DEEP_WILD_DROP_RISK_THRESHOLD = 100000;

    public void updateDWSkullIcon() {
        if (player.getAttribute("deepwild-value-listener") == null) {
            ContainerListener listener = new ContainerListener() {
                @Override
                public void update(Container c, ContainerEvent event) {
                    refresh(c);
                }

                @Override
                public void refresh(Container c) {
                    updateDWSkullIcon();
                }
            };
            player.setAttribute("deepwild-value-listener", listener);
            player.getInventory().getListeners().add(listener);
            player.getEquipment().getListeners().add(listener);
        }
        long value = 0;
        long maxValue = 0;
        for (Item item : player.getInventory().toArray()) {
            if (item != null) {
                long alchValue = item.getAlchemyValue();
                value += alchValue;
                maxValue = Math.max(maxValue, alchValue);
            }
        }
        for (Item item : player.getEquipment().toArray()) {
            if (item != null) {
                long alchValue = item.getAlchemyValue();
                value += alchValue;
                maxValue = Math.max(maxValue, alchValue);
            }
        }

        value -= maxValue;
        player.setAttribute("deepwild-value-risk", value);
        SkullIcon skull = SkullIcon.BH_BROWN1;
        if (value >= DEEP_WILD_DROP_RISK_THRESHOLD) {
            skull = SkullIcon.RED;
        }
        setSkullIcon(skull.id);
    }

    public void removeDWSkullIcon() {
        setSkullIcon(skulled ? 0 : -1);
        ContainerListener listener = player.getAttribute("deepwild-value-listener");
        if (listener != null) {
            player.getInventory().getListeners().remove(listener);
            player.getEquipment().getListeners().remove(listener);
        }
        player.removeAttribute("deepwild-value-listener");
        player.removeAttribute("deepwild-value-risk");
    }

    public void setSkulled(boolean skulled) {
        this.skulled = skulled;
    }
}
