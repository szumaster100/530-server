package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

import static core.tools.DialogueHelperKt.END_DIALOGUE;

@Initializable
public class NaturalHistorianDialogue extends Dialogue {

    public NaturalHistorianDialogue() {}

    public NaturalHistorianDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        npcl(FaceAnim.HALF_GUILTY, "Hello there and welcome to the Natural History exhibit of the Varrock Museum!");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                playerl(FaceAnim.HALF_GUILTY, "Hello. So what is it you do here?");
                stage++;
                break;
            case 1:
                npcl(FaceAnim.HALF_GUILTY, "Well, we look after all of the research in this section.");
                stage++;
                break;
            case 2:
                npcl(FaceAnim.HALF_GUILTY, "When I'm not doing that, I'm teaching people like yourself about how wonderful the natural world is.");
                stage++;
                break;
            case 3:
                playerl(FaceAnim.HALF_GUILTY, "Nice. So then, I've got a few questions for you.");
                stage++;
                break;
            case 4:
                npcl(FaceAnim.HALF_GUILTY, "Well, fire away and I'll give you an insight into the exciting world of animals.");
                stage = END_DIALOGUE;
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.NATURAL_HISTORIAN_5970};
    }
}
