package content.region.misthalin.handlers.museum.dialogue;

import org.rs.consts.NPCs;
import core.game.dialogue.Dialogue;
import core.game.dialogue.FaceAnim;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;

@Initializable
public class SchoolChildDialogue extends Dialogue {

    public SchoolChildDialogue() {}

    public SchoolChildDialogue(Player player) {
        super(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        npc(FaceAnim.CHILD_FRIENDLY, getRandomForceChat());
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        end();
        return true;
    }

    private String getRandomForceChat() {
        String[] forceChat = {
            "Can you find my teacher? I need the toilet!",
            "I wonder what they're doing behind that rope.",
            "Teacher! Can we go to the Natural History Exhibit now?",
            "*sniff* They won't let me take an arrowhead as a souvenir.",
            "Yaaay! A day off school.",
            "I wanna be an archaeologist when I grow up!",
            "Sada... Sram... Sa-ra-do-min is bestest!",
            "*cough* It's so dusty in here.",
            "Maz... Zar... Za-mor-ak is bestest!"
        };
        int randomIndex = (int) (Math.random() * forceChat.length);
        return forceChat[randomIndex];
    }

    @Override
    public int[] getIds() {
        return new int[]{
            NPCs.SCHOOLGIRL_10,
            NPCs.SCHOOLBOY_5945,
            NPCs.SCHOOLBOY_5946,
            NPCs.SCHOOLGIRL_5984
        };
    }
}
