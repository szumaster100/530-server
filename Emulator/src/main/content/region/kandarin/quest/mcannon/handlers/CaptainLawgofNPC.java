package content.region.kandarin.quest.mcannon.handlers;

import core.game.node.entity.npc.AbstractNPC;
import core.game.world.map.Location;
import core.tools.RandomFunction;

public class CaptainLawgofNPC extends AbstractNPC {

    private static final String[] CHATS = new String[]{"Don't just stand there, do something!", "Stop dawdling solider! You're in the army now!", "Hurry up on that patrol route, trooper!", "Keep an eye open for goblins!"};

    public CaptainLawgofNPC(int id, Location location) {
        super(id, location);
    }

    public CaptainLawgofNPC() {
        super(-1, null);
    }

    @Override
    public AbstractNPC construct(int id, Location location, Object... objects) {
        return new CaptainLawgofNPC(id, location);
    }

    @Override
    public void handleTickActions() {
        super.handleTickActions();
        if (RandomFunction.random(100) < 3) {
            sendChat(RandomFunction.getRandomElement(CHATS));
        }
    }

    @Override
    public int[] getIds() {
        return new int[]{208};
    }

}
