package content.region.misthalin.quest.romeo.cutscene;

import content.region.misthalin.quest.romeo.dialogue.JulietDialogue;
import core.game.activity.ActivityPlugin;
import core.game.activity.CutscenePlugin;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.map.build.DynamicRegion;
import core.net.packet.PacketRepository;
import core.net.packet.context.CameraContext;
import core.net.packet.context.CameraContext.CameraType;
import core.net.packet.out.CameraViewPacket;
import core.plugin.Initializable;
import core.plugin.ClassScanner;

@Initializable
public final class JulietCutscene extends CutscenePlugin {

    private static final Location SPAWN_LOCATION = Location.create(3166, 3431, 1);

    public JulietCutscene() {
        this(null);
    }

    public JulietCutscene(final Player player) {
        super("Juliet Cutscene");
        this.player = player;
        ClassScanner.definePlugin(new JulietDialogue());
    }

    @Override
    public boolean start(final Player player, final boolean login, java.lang.Object... args) {
        NPC juliet = NPC.create(637, base.transform(29, 39, 1));
        juliet.setWalks(false);
        npcs.add(juliet);
        npcs.add(NPC.create(3325, base.transform(29, 38, 1)));
        npcs.add(NPC.create(3324, base.transform(26, 41, 1)));
        final Scenery door = RegionManager.getObject(getBase().transform(29, 41, 1));
        SceneryBuilder.remove(door);
        for (NPC npc : npcs) {
            npc.init();
        }
        return super.start(player, login, args);
    }

    @Override
    public void open() {
        int x = player.getLocation().getX();
        int y = player.getLocation().getY();
        CameraContext rot = null;
        CameraContext pos = null;
        int height = 390;
        int speed = 100;
        int other = 1;
        pos = new CameraContext(player, CameraType.POSITION, x, y - 4, height, other, speed);
        rot = new CameraContext(player, CameraType.ROTATION, x, y - 4, height, other, speed);
        PacketRepository.send(CameraViewPacket.class, pos);
        PacketRepository.send(CameraViewPacket.class, rot);
        for (NPC npc : npcs) {
            npc.face(player);
        }
        npcs.get(1).face(npcs.get(0));
        player.face(npcs.get(0));
        player.getDialogueInterpreter().open(npcs.get(0).getId(), npcs.get(0), this);
        player.lock();
        player.getLocks().lockMovement(1000000);
    }

    @Override
    public void configure() {
        region = DynamicRegion.create(12597);
        setRegionBase();
        registerRegion(region.getId());
    }

    @Override
    public Location getStartLocation() {
        return getBase().transform(30, 39, 1);
    }

    @Override
    public ActivityPlugin newInstance(Player p) throws Throwable {
        return new JulietCutscene(p);
    }

    @Override
    public Location getSpawnLocation() {
        return SPAWN_LOCATION;
    }

    public NPC getPhillipia() {
        return npcs.get(1);
    }

}
