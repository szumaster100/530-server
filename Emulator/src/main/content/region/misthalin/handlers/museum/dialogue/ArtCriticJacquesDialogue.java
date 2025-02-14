package content.region.misthalin.handlers.museum.dialogue;

import core.game.world.map.Location;
import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

@Initializable
public class ArtCriticJacquesDialogue extends Dialogue {

    public ArtCriticJacquesDialogue() {}

    public ArtCriticJacquesDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        npcl(FaceAnim.HALF_GUILTY, "I sit in the sky like a sphinx misunderstood");
        stage = 0;
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            case 0:
                npc(FaceAnim.HALF_GUILTY, "I combine a heart of snow to the whiteness of swans;", "I hate the movement that moves the lines;", " ", "And I never cry and I never laugh.");
                stage++;
                break;

            case 1:
                end();
                npc.faceLocation(Location.create(3257, 3455, 2));
                break;
        }
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{NPCs.ART_CRITIC_JACQUES_5930};
    }
}
