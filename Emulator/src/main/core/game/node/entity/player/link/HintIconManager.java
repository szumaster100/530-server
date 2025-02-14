package core.game.node.entity.player.link;

import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.net.packet.PacketRepository;
import core.net.packet.context.HintIconContext;
import core.net.packet.out.HintIcon;

public final class HintIconManager {

    public static final int MAXIMUM_SIZE = 8;

    public static final int DEFAULT_ARROW = 1;

    public static final int DEFAULT_MODEL = -1;

    public static final int ARROW_CIRCLE_MODEL = 40497;

    private final HintIconContext[] hintIcons = new HintIconContext[MAXIMUM_SIZE];

    public HintIconManager() {

    }

    public static int registerHintIcon(Player player, Node target) {
        return registerHintIcon(player, target, DEFAULT_ARROW, DEFAULT_MODEL, player.getHintIconManager().freeSlot());
    }

    public static int registerHeightHintIcon(Player player, Node target, int height) {
        return registerHintIcon(player, target, DEFAULT_ARROW, DEFAULT_MODEL, player.getHintIconManager().freeSlot(), height);
    }

    public static int registerHintIcon(Player player, Node target, int arrowId) {
        return registerHintIcon(player, target, arrowId, DEFAULT_MODEL, player.getHintIconManager().freeSlot());
    }

    public static int registerHintIcon(Player player, Node target, int arrowId, int modelId) {
        return registerHintIcon(player, target, arrowId, modelId, player.getHintIconManager().freeSlot());
    }

    public static int registerHintIcon(Player player, Node target, int arrowId, int modelId, int slot) {
        if (isInvalidSlot(slot) || target == null) {
            return -1;
        }
        return registerHintIcon(player, target, arrowId, modelId, slot, 0);
    }

    public static int registerHintIcon(Player player, Node target, int arrowId, int modelId, int slot, int height) {
        int type = getType(target);
        return registerHintIcon(player, target, arrowId, modelId, slot, height, type);
    }

    public static int registerHintIcon(Player player, Node target, int arrowId, int modelId, int slot, int height, int targetType) {
        if (isInvalidSlot(slot)) {
            return -1;
        }
        HintIconManager manager = player.getHintIconManager();
        HintIconContext icon = new HintIconContext(player, slot, arrowId, targetType, target, modelId, height);
        PacketRepository.send(HintIcon.class, icon);
        manager.hintIcons[slot] = icon;
        return slot;
    }

    public static void removeHintIcon(Player player, int slot) {
        if (isInvalidSlot(slot)) {
            return;
        }
        HintIconManager manager = player.getHintIconManager();
        HintIconContext icon = manager.hintIcons[slot];
        if (icon != null) {
            icon.setTargetType(0);
            PacketRepository.send(HintIcon.class, icon);
            manager.hintIcons[slot] = null;
        }
    }

    public void clear() {
        for (int i = 0; i < hintIcons.length; i++) {
            if (hintIcons[i] != null) {
                removeHintIcon(hintIcons[i].getPlayer(), i);
            }
        }
    }

    public int freeSlot() {
        for (int i = 0; i < hintIcons.length; i++) {
            if (hintIcons[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public HintIconContext getIcon(int slot) {
        return hintIcons[slot];
    }

    private static boolean isInvalidSlot(int slot) {
        return slot < 0;
    }

    private static int getType(Node target) {
        if (target instanceof Entity) {
            return target instanceof Player ? 10 : 1;
        }
        return 2;
    }
}