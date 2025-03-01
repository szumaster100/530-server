package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

import static core.api.ContentAPIKt.sendNPCDialogue;
import static core.tools.DialogueHelperKt.END_DIALOGUE;

@Initializable
public class PupilDialogue extends Dialogue {

    public PupilDialogue() {}

    public PupilDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        sendNPCDialogue(player, NPCs.SCHOOLBOY_5949, "Teacher! Sir! I need the toilet!", FaceAnim.CHILD_GUILTY);
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                sendNPCDialogue(player, NPCs.TEACHER_AND_PUPIL_5948, "I told you to go before we got here.", FaceAnim.HALF_GUILTY);
                stage++;
                break;
            case 1:
                sendNPCDialogue(player, NPCs.SCHOOLBOY_5949, "But sir, I didn't need to go then!", FaceAnim.CHILD_GUILTY);
                stage++;
                break;
            case 2:
                sendNPCDialogue(player, NPCs.TEACHER_AND_PUPIL_5948, "Alright, come on then.", FaceAnim.HALF_GUILTY);
                stage = END_DIALOGUE;
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.TEACHER_AND_PUPIL_5944};
    }
}
