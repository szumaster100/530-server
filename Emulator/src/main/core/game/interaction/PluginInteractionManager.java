package core.game.interaction;

import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class PluginInteractionManager {

    private static final Int2ObjectMap<PluginInteraction> npcInteractions = new Int2ObjectOpenHashMap<>();

    private static final Int2ObjectMap<PluginInteraction> objectInteractions = new Int2ObjectOpenHashMap<>();

    private static final Int2ObjectMap<PluginInteraction> useWithInteractions = new Int2ObjectOpenHashMap<>();

    private static final Int2ObjectMap<PluginInteraction> groundItemInteractions = new Int2ObjectOpenHashMap<>();

    public static void register(PluginInteraction interaction, InteractionType type) {
        for (int id : interaction.ids) {
            switch (type) {
                case OBJECT:
                    objectInteractions.putIfAbsent(id, interaction);
                    break;
                case USEWITH:
                    useWithInteractions.putIfAbsent(id, interaction);
                    break;
                case NPC:
                    npcInteractions.putIfAbsent(id, interaction);
                    break;
                case ITEM:
                    groundItemInteractions.putIfAbsent(id, interaction);
                    break;
            }
        }
    }

    public static boolean handle(Player player, Scenery scenery) {
        PluginInteraction interaction = objectInteractions.get(scenery.getId());
        return interaction != null && interaction.handle(player, scenery);
    }

    public static boolean handle(Player player, NodeUsageEvent event) {
        PluginInteraction interaction = useWithInteractions.get(event.getUsed().asItem().getId());
        return interaction != null && interaction.handle(player, event);
    }

    public static boolean handle(Player player, NPC npc, Option option) {
        PluginInteraction interaction = npcInteractions.get(npc.getId());
        return interaction != null && interaction.handle(player, npc, option);
    }

    public static boolean handle(Player player, Item item, Option option) {
        PluginInteraction interaction = groundItemInteractions.get(item.getId());
        return interaction != null && interaction.handle(player, item, option);
    }

    public enum InteractionType {

        NPC,

        OBJECT,

        USEWITH,

        ITEM;
    }
}
