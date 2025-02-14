package content.region.kandarin.handlers.npc;

import core.cache.def.impl.NPCDefinition;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;
import core.plugin.Plugin;

@Initializable
public class RasoloNPC extends OptionHandler {

    private static final int NPC_ID = 1972;

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        registerNPCOptions();
        return null;
    }

    private void registerNPCOptions() {
        NPCDefinition.forId(NPC_ID).getHandlers().put("option:talk-to", this);
        NPCDefinition.forId(NPC_ID).getHandlers().put("option:trade", this);
    }

    @Override
    public boolean handle(Player player, Node node, String option) {
        switch (option) {
            case "trade":
                openShopForPlayer(player);
                break;
            case "talk-to":
                openDialogueForPlayer(player);
                break;
            default:
                return false;
        }
        return true;
    }

    private void openShopForPlayer(Player player) {
        new NPC(NPC_ID).openShop(player);
    }

    private void openDialogueForPlayer(Player player) {
        player.getDialogueInterpreter().open(NPC_ID);
    }
}
