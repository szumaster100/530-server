package content.region.misthalin.handlers.draynor;

import core.game.activity.ActivityPlugin;
import core.game.activity.CutscenePlugin;
import core.game.component.Component;
import core.game.dialogue.Dialogue;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Animation;
import core.net.packet.PacketRepository;
import core.net.packet.context.CameraContext;
import core.net.packet.context.CameraContext.CameraType;
import core.net.packet.out.CameraViewPacket;
import core.plugin.Initializable;
import kotlin.Unit;

import static core.api.ContentAPIKt.*;

@Initializable
public final class TelescopeCutscene extends CutscenePlugin {

    private static final Component INTERFACE = new Component(386);
    private static final Animation TELESCOPE_ANIM = new Animation(2171);

    public TelescopeCutscene() {
        super("draynor telescope");
    }

    public TelescopeCutscene(Player player) {
        super("draynor telescope");
        this.player = player;
    }

    @Override
    public ActivityPlugin newInstance(Player p) throws Throwable {
        return new TelescopeCutscene(p);
    }

    @Override
    public boolean start(final Player player, boolean login, Object... args) {
        player.animate(TELESCOPE_ANIM);
        player.getDialogueInterpreter().sendPlainMessage(true, "You look through the telescope...");
        return super.start(player, login, args);
    }

    @Override
    public void open() {
        setAttribute(player, "cutscene:original-loc", Location.create(3088, 3254, 0));
        registerLogoutListener(player, "telescope-cutscene", player -> {
            player.setLocation(Location.create(3089, 3252, 1));
            return Unit.INSTANCE;
        });
        player.setDirection(Direction.NORTH);
        player.faceLocation(player.getLocation().transform(0, 1, 0));
        int x = 3104;
        int y = 3175;
        int height = 900;
        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.POSITION, x, y, height, 1, 100));
        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.ROTATION, x + 1, y - 30, height, 1, 100));
        x = 3111;
        y = 3172;
        height = 700;
        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.POSITION, x, y, height, 1, 2));
        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.ROTATION, x - 1, y + 230, height, 1, 1));
        player.getInterfaceManager().open(INTERFACE);
        GameWorld.getPulser().submit(new Pulse(22, player) {
            @Override
            public boolean pulse() {
                TelescopeCutscene.this.stop(true);
                return true;
            }
        });
    }

    @Override
    public void register() {
        super.register();
        new EndDialogue().init();
    }

    @Override
    public void end() {
        player.getInterfaceManager().close();
        clearLogoutListener(player, "telescope-cutscene");
        player.getDialogueInterpreter().open(32389023);
        super.end();
    }

    @Override
    public Location getSpawnLocation() {
        return null;
    }

    @Override
    public Location getStartLocation() {
        return new Location(3104, 3175, 0);
    }

    @Override
    public void configure() {

    }

    public static final class EndDialogue extends Dialogue {

        public EndDialogue(final Player player) {
            super(player);
        }

        public EndDialogue() {

        }

        @Override
        public Dialogue newInstance(Player player) {
            return new EndDialogue(player);
        }

        @Override
        public boolean open(Object... args) {
            player("I see you've got your telescope", "pointing at the Wizard's Tower.");
            return true;
        }

        @Override
        public boolean handle(int interfaceId, int buttonId) {
            switch (stage) {
                case 0:
                    interpreter.sendDialogues(3820, null, "Oh, do I? Well, why does that interest you?");
                    stage = 1;
                    break;
                case 1:
                    player("Well, you robbed a bank, and I bet you're now", "planning something to do with that Tower!");
                    stage = 2;
                    break;
                case 2:
                    interpreter.sendDialogues(3820, null, "No, no, I'm not planning anything like that again.");
                    stage = 3;
                    break;
                case 3:
                    player("Well I'll be watching you...");
                    stage = 4;
                    break;
                case 4:
                    end();
                    break;
            }
            return true;
        }

        @Override
        public int[] getIds() {
            return new int[]{32389023};
        }

    }
}
