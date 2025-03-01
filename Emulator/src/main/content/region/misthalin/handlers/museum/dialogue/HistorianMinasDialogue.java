package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

import static core.tools.DialogueHelperKt.END_DIALOGUE;

@Initializable
public class HistorianMinasDialogue extends Dialogue {

    public HistorianMinasDialogue() {}

    public HistorianMinasDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        player(FaceAnim.HALF_GUILTY, "Hello");
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                npcl(FaceAnim.HALF_GUILTY, "Hello there, welcome to Varrock Museum! Can I help you?");
                stage++;
                break;
            case 1:
                playerl(FaceAnim.HALF_GUILTY, "Tell me about the Museum.");
                stage++;
                break;
            case 2:
                npcl(FaceAnim.HALF_GUILTY, "Well, as you can see we have recently expanded a great deal to cope with the influx of finds from the Dig Site.");
                stage++;
                break;
            case 3:
                npc(FaceAnim.HALF_GUILTY, "Also, of course, to prepare for the new dig we're", "opening soon.");
                stage = END_DIALOGUE;
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.HISTORIAN_MINAS_5931};
    }
}
