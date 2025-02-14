package content.global.skill.summoning.familiar;

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

public final class RemoteViewer {

    public static final String DIALOGUE_NAME = "remote-view";

    public static final int HEIGHT = 1000;

    private final Player player;
    private final Familiar familiar;
    private final Animation animation;
    private final ViewType type;

    public RemoteViewer(Player player, Familiar familiar, Animation animation, ViewType type) {
        this.player = player;
        this.familiar = familiar;
        this.animation = animation;
        this.type = type;
    }

    public static RemoteViewer create(final Player player, Familiar familiar, Animation animation, ViewType type) {
        return new RemoteViewer(player, familiar, animation, type);
    }

    public void startViewing() {
        player.lock();
        familiar.animate(animation);
        player.getPacketDispatch().sendMessage("You send the " + familiar.getName().toLowerCase() + " to fly " +
            (type == ViewType.STRAIGHT_UP ? "directly up" : "to the " + type.name().toLowerCase()) + "...");

        GameWorld.getPulser().submit(new Pulse(5) {
            @Override
            public boolean pulse() {
                view();
                return true;
            }
        });
    }

    private void view() {
        if (!canView()) {
            return;
        }
        sendCamera(type.getXOffset(), type.getYOffset(), type.getXRot(), type.getYRot());

        GameWorld.getPulser().submit(new Pulse(13) {
            @Override
            public boolean pulse() {
                reset();
                return true;
            }
        });
    }

    private boolean canView() {
        player.getPacketDispatch().sendMessage("There seems to be an obstruction in the direction; the familiar cannot fly there");
        return familiar.isActive();
    }

    private void reset() {
        familiar.call();
        player.unlock();
        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.RESET, 0, 0, HEIGHT, 1, 100));
    }

    private void sendCamera(int xOffset, int yOffset, final int xRot, final int yRot) {
        final Location location = type.getLocationTransform(player);
        final int x = location.getX() + xOffset;
        final int y = location.getY() + yOffset;

        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.POSITION, x, y, HEIGHT, 1, 100));

        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraType.ROTATION, x + xRot, y + yRot, HEIGHT, 1, 90));
    }

    public static void openDialogue(final Player player, final Familiar familiar) {
        player.getDialogueInterpreter().open(DIALOGUE_NAME, familiar);
    }

    public Player getPlayer() {
        return player;
    }

    public Familiar getFamiliar() {
        return familiar;
    }

    public Animation getAnimation() {
        return animation;
    }

    public ViewType getType() {
        return type;
    }

    public enum ViewType {

        NORTH(Direction.NORTH, 0, 0, 0, 0),

        EAST(Direction.WEST, 0, 0, 0, 0),

        SOUTH(Direction.SOUTH, 0, 0, 0, 0),

        WEST(Direction.EAST, 0, 0, 0, 0),

        STRAIGHT_UP(null, 0, 0, 0, 0);

        private final Direction direction;
        private final int[] data;

        ViewType(Direction direction, int... data) {
            this.direction = direction;
            this.data = data;
        }

        public Location getLocationTransform(final Player player) {
            if (this == STRAIGHT_UP) {
                return player.getLocation();
            }
            return player.getLocation().transform(direction, 10);
        }

        public Direction getDirection() {
            return direction;
        }

        public int getXOffset() {
            return data[0];
        }

        public int getYOffset() {
            return data[1];
        }

        public int getXRot() {
            return data[2];
        }

        public int getYRot() {
            return data[3];
        }

        public int[] getData() {
            return data;
        }
    }
}
