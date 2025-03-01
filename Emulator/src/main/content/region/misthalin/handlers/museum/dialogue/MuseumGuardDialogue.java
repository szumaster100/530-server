package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

import static core.tools.DialogueHelperKt.END_DIALOGUE;

@Initializable
public class MuseumGuardDialogue extends Dialogue {

    public MuseumGuardDialogue() {}

    public MuseumGuardDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc(FaceAnim.HALF_GUILTY, "Hello there. Come to see the new museum?");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                player(FaceAnim.HALF_GUILTY, "Yes, how do I get in?");
                stage++;
                break;
            case 1:
                npc(FaceAnim.HALF_GUILTY, "Well, the main entrance is 'round the front. Just head", "west then north slightly, you can't miss it!");
                stage++;
                break;
            case 2:
                player(FaceAnim.HALF_GUILTY, "What about these doors?");
                stage++;
                break;
            case 3:
                npc(FaceAnim.HALF_GUILTY, "They're primarily for the workmen bringing finds from the", "Dig Site, but you can go through if you want.");
                stage = END_DIALOGUE;
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.MUSEUM_GUARD_5943, NPCs.MUSEUM_GUARD_5941};
    }
}
