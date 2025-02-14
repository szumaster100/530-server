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
public class TeacherDialogue extends Dialogue {

    public TeacherDialogue() {}

    public TeacherDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        if (npc.getLocation().getZ() == 0) {
            sendNPCDialogue(player, NPCs.TEACHER_5950, "Stop pulling, we've plenty of time to see everything.", FaceAnim.ANGRY);
            stage = 0;
        } else {
            sendNPCDialogue(player, NPCs.SCHOOLGIRL_5951, "That man over there talks funny, miss.", FaceAnim.HALF_GUILTY);
            stage = 1;
        }
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                sendNPCDialogue(player, NPCs.SCHOOLGIRL_5951, "Aww, but miss, it's sooo exciting.", FaceAnim.CHILD_FRIENDLY);
                stage = END_DIALOGUE;
                break;
            case 1:
                sendNPCDialogue(player, NPCs.TEACHER_5950, "That's because he's an art critic, dear. They have some very funny ideas.", FaceAnim.HALF_GUILTY);
                stage = END_DIALOGUE;
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.TEACHER_AND_PUPIL_5947};
    }
}
