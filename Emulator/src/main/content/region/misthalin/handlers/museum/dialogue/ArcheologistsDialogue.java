package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

@Initializable
public class ArcheologistsDialogue extends Dialogue {

    public ArcheologistsDialogue() {}

    public ArcheologistsDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        npcl(FaceAnim.HALF_GUILTY, "Hello there; I see you're qualified. Come to help us out?");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                end();
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{
            NPCs.BARNABUS_HURMA_5932,
            NPCs.MARIUS_GISTE_5933,
            NPCs.CADEN_AZRO_5934,
            NPCs.THIAS_LEACKE_5935,
            NPCs.SINCO_DOAR_5936,
            NPCs.TINSE_TORPE_5937
        };
    }
}
