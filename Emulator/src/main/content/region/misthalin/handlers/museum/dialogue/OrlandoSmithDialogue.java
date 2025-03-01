package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

import static core.tools.DialogueHelperKt.END_DIALOGUE;

@Initializable
public class OrlandoSmithDialogue extends Dialogue {

    public OrlandoSmithDialogue() {}

    public OrlandoSmithDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npcl(FaceAnim.HALF_GUILTY, "G'day there, mate.");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                playerl(FaceAnim.FRIENDLY, "Good day. Are you alright? You look a little lost.");
                stage++;
                break;
            case 1:
                npcl(FaceAnim.HALF_GUILTY, "Well, mate, to tell you the truth, I think I've come a gutser with these displays.");
                stage++;
                break;
            case 2:
                playerl(FaceAnim.FRIENDLY, "Come a what?");
                stage++;
                break;
            case 3:
                npcl(FaceAnim.HALF_GUILTY, "Gutser and no mistake. Me boss asked me to put together a quiz for the visitors.");
                stage++;
                break;
            case 4:
                npcl(FaceAnim.HALF_GUILTY, "But to be deadset with you, I wasn't paying much attention to me boss over there and I've done a bit of a rush job.");
                stage++;
                break;
            case 5:
                playerl(FaceAnim.FRIENDLY, "You mean the natural historian?");
                stage++;
                break;
            case 6:
                npcl(FaceAnim.HALF_GUILTY, "Yep, that's the bloke. Say, mate, you do me a favour?");
                stage++;
                break;
            case 7:
                playerl(FaceAnim.FRIENDLY, "Perhaps. What do you need?");
                stage++;
                break;
            case 8:
                npcl(FaceAnim.HALF_GUILTY, "Well, you look like a pretty smart cobber. Could you take a look at the display plaques and give 'em a runthrough?");
                stage++;
                break;
            case 9:
                options("Sure thing.", "No thanks.");
                stage++;
                break;
            case 10:
                switch (buttonId) {
                    case 1:
                        player("Sure thing.");
                        stage = 11;
                        break;
                    case 2:
                        player("No thanks I'm too busy.");
                        stage = 13;
                        break;
                }
                break;
            case 11:
                npcl(FaceAnim.HALF_GUILTY, "Bonza, mate! I reckon three questions per case should be bang to rights.");
                stage++;
                break;
            case 12:
                npcl(FaceAnim.HALF_GUILTY, "Take a gander at each case and I'll look over your shoulder to give some advice.");
                stage = END_DIALOGUE;
                break;
            case 13:
                npcl(FaceAnim.HALF_GUILTY, "Fair dinkum mate. I'm sure I'll get someone else to help me.");
                stage = END_DIALOGUE;
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.ORLANDO_SMITH_5965};
    }
}
