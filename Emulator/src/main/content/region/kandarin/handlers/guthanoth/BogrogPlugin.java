package content.region.kandarin.handlers.guthanoth;

import org.rs.consts.NPCs;
import content.region.kandarin.dialogue.guthanoth.BogrogDialogue;
import core.cache.def.impl.NPCDefinition;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.plugin.ClassScanner;
import kotlin.Unit;

import static core.api.ContentAPIKt.sendItemSelect;

@Initializable
public final class BogrogPlugin extends OptionHandler {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        registerPlugin();
        return this;
    }

    private void registerPlugin() {
        NPCDefinition.forId(NPCs.BOGROG_4472).getHandlers().put("option:swap", this);
        ClassScanner.definePlugin(new BogrogDialogue());
    }

    @Override
    public boolean handle(Player player, Node node, String option) {
        if ("swap".equals(option)) {
            openSwap(player);
        }
        return true;
    }

    public static void openSwap(Player player) {
        if (isSummoningLevelInsufficient(player)) {
            player.sendMessage("You need a Summoning level of at least 21 in order to do that.");
        } else {
            displaySwapOptions(player);
        }
    }

    private static boolean isSummoningLevelInsufficient(Player player) {
        return player.getSkills().getStaticLevel(Skills.SUMMONING) < 21;
    }

    private static void displaySwapOptions(Player player) {
        sendItemSelect(player, new String[]{"Value", "Swap 1", "Swap 5", "Swap 10", "Swap X"}, true, (slot, index) -> {
            BogrogPouchSwapper.handle(player, index, slot);
            return Unit.INSTANCE;
        });
    }
}