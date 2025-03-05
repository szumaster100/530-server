package core.game.node.entity.player.link;

import core.ServerConstants;
import core.game.node.entity.Entity;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import org.rs.consts.Sounds;

import static core.api.ContentAPIKt.*;

public class TeleportManager {

    public static final int WILDY_TELEPORT = 1 << 16 | 8;

    private final static int[] HOME_ANIMATIONS = {1722, 1723, 1724, 1725, 2798, 2799, 2800, 3195, 4643, 4645, 4646, 4847, 4848, 4849, 4850, 4851, 4852, 65535};

    private final static int[] HOME_GRAPHICS = {775, 800, 801, 802, 803, 804, 1703, 1704, 1705, 1706, 1707, 1708, 1709, 1710, 1711, 1712, 1713, 65535};

    private final Entity entity;

    private Pulse lastTeleport;

    private Pulse currentTeleport;

    private int teleportType;

    public TeleportManager(Entity entity) {
        this.entity = entity;
        lastTeleport = TeleportType.HOME.getPulse(entity, ServerConstants.HOME_LOCATION);
    }

    public boolean send(Location location) {
        return send(location, entity instanceof Player ? getType((Player) entity) : TeleportType.NORMAL, 0);
    }

    public boolean send(Location location, TeleportType type) {
        return send(location, type, 0);
    }

    public boolean send(Location location, TeleportType type, int teleportType) {
        if (teleportType == WILDY_TELEPORT || type == TeleportType.OBELISK) {
            if (hasTimerActive(entity, "teleblock")) return false;
        } else {
            if (!entity.getZoneMonitor().teleport(teleportType, null)) {
                return false;
            }
            if (teleportType != -1 && entity.isTeleBlocked()) {
                if (entity.isPlayer())
                    entity.asPlayer().sendMessage("A magical force has stopped you from teleporting.");
                return false;
            }
        }
        if (teleportType != -1) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                p.getDialogueInterpreter().close();
            }
        }
        if (entity.getAttribute("tablet-spell", false)) {
            type = TeleportType.TELETABS;
        }
        this.teleportType = teleportType;
        entity.getWalkingQueue().reset();
        lastTeleport = currentTeleport;
        currentTeleport = type.getPulse(entity, location);
        entity.getPulseManager().clear();
        if (type == TeleportType.HOME) {
            entity.getPulseManager().run(type.getPulse(entity, location));
        } else {
            entity.lock(12);
            entity.getImpactHandler().setDisabledTicks(teleportType == -1 ? 5 : 12);
            GameWorld.getPulser().submit(currentTeleport);
        }
        if (entity instanceof Player) {
            ((Player) entity).getInterfaceManager().close();
        }
        return true;
    }

    public static void fireRandom(Entity entity, Location location) {
        if (entity instanceof Player && entity.getTeleporter().getTeleportType() == 0) {
            Player p = (Player) entity;
        }
    }

    private static int getAudio(int count) {
        switch (count) {
            case 0:
                return 193;
            case 4:
                return 194;
            case 11:
                return 195;
        }
        return -1;
    }

    public final Entity getEntity() {
        return entity;
    }

    public final Pulse getLastTeleport() {
        return lastTeleport;
    }

    public final Pulse getCurrentTeleport() {
        return currentTeleport;
    }

    public enum TeleportType {

        NORMAL(new TeleportSettings(8939, 8941, 1576, 1577)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_ALL_200);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                        } else if (delay == 4) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_REVERSE_201);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.unlock();
                        entity.lock(4);
                    }
                };
            }
        },

        ANCIENT(new TeleportSettings(1979, -1, 392, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.BLOCK_TP_197, 0, 7);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 4) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                        } else if (delay == 5) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.getAnimator().forceAnimation(new Animation(-1));
                        entity.graphics(new Graphics(-1));
                        entity.unlock();
                    }
                };
            }
        },

        LUNAR(new TeleportSettings(1816, -1, 747, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            entity.graphics(new Graphics(getSettings().getStartGfx(), 120));
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                        } else if (delay == 4) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.getAnimator().forceAnimation(new Animation(-1));
                        entity.graphics(new Graphics(-1));
                        entity.unlock();
                    }
                };
            }
        },

        TELETABS(new TeleportSettings(4731, -1, 678, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.POH_TABLET_BREAK_979);
                            entity.getAnimator().forceAnimation(new Animation(4069));
                        } else if (delay == 2) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                        } else if (delay == 4) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote()));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.getAnimator().forceAnimation(new Animation(-1));
                        entity.graphics(new Graphics(-1));
                        entity.unlock();
                        entity.lock(2);
                    }
                };
            }
        },

        HOME(new TeleportSettings(4847, 4857, 800, 804)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int count;
                    Player player;

                    @Override
                    public boolean pulse() {
                        switch (count) {
                            case 18:
                                player.getProperties().setTeleportLocation(location);
                                return true;
                            default:
                                playGlobalAudio(entity.getLocation(), getAudio(count));
                                player.getPacketDispatch().sendGraphic(HOME_GRAPHICS[count]);
                                player.getPacketDispatch().sendAnimation(HOME_ANIMATIONS[count]);
                                break;
                        }
                        count++;
                        return false;
                    }

                    @Override
                    public void start() {
                        player = ((Player) entity);

                        super.start();
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.getAnimator().forceAnimation(new Animation(-1));
                        player.graphics(new Graphics(-1));
                    }
                };
            }
        },

        OBELISK(new TeleportSettings(8939, 8941, 661, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            entity.lock();
                            entity.getAnimator().forceAnimation(new Animation(1816));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                        } else if (delay == 4) {
                            entity.getAnimator().forceAnimation(Animation.RESET);
                            entity.unlock();
                            return true;
                        }
                        delay++;
                        return false;
                    }
                };
            }
        },

        TELE_OTHER(new TeleportSettings(1816, -1, 342, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.TELE_OTHER_CAST_199);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                        } else if (delay == 4) {
                            entity.getAnimator().forceAnimation(new Animation(-1));
                            entity.unlock();
                            return true;
                        }
                        delay++;
                        return false;
                    }
                };
            }
        },

        FAIRY_RING(new TeleportSettings(-1, -1, -1, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                entity.graphics(Graphics.create(569));
                return new TeleportPulse(entity) {
                    int delay;

                    @Override
                    public boolean pulse() {
                        switch (++delay) {
                            case 2:
                                entity.animate(Animation.create(3265));
                                if (entity instanceof Player) {
                                    playAudio(entity.asPlayer(), Sounds.FT_FAIRY_TP_1098);
                                }
                                break;
                            case 4:
                                entity.animate(Animation.create(3266));
                                entity.getProperties().setTeleportLocation(location);
                                entity.unlock();
                                entity.lock(2);
                                return true;
                        }
                        return false;
                    }

                };
            }
        },

        PURO_PURO(new TeleportSettings(6601, 1118, -1, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(1118));
                        } else if (delay == 9) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            entity.getAnimator().forceAnimation(new Animation(-1));
                            entity.unlock();
                            return true;
                        }
                        delay++;
                        return false;
                    }
                };
            }
        },

        ECTOPHIAL(new TeleportSettings(8939, 8941, 1587, 1588)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_ALL_200);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                        } else if (delay == 4) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_REVERSE_201);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }
                };
            }
        },

        CHRISTMAS(new TeleportSettings(7534, -1, 1292, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_ALL_200);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                        } else if (delay == 4) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_REVERSE_201);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.unlock();
                        entity.lock(4);
                    }
                };
            }
        },

        CABBAGE(new TeleportSettings(9984, 9986, 1731, 1732)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            if (entity instanceof Player) {
                                playAudio(entity.asPlayer(), 5036);
                            }
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 5) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                            if (entity instanceof Player) {
                                playAudio(entity.asPlayer(), 5034);
                            }
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.unlock();
                        entity.lock(4);
                    }
                };
            }
        },

        ENTRANA_MAGIC_DOOR(new TeleportSettings(10100, 9013, 1745, 1747)) { //

            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_ALL_200);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(getSettings().getStartGfx()));
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            fireRandom(entity, location);
                        } else if (delay == 4) {
                            playGlobalAudio(entity.getLocation(), Sounds.TP_REVERSE_201);
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getEndEmote(), Priority.HIGH));
                            entity.graphics(new Graphics(getSettings().getEndGfx()));
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.unlock();
                        entity.lock(4);
                    }
                };
            }
        },

        RANDOM_EVENT_OLD(new TeleportSettings(714, -1, -1, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;
                    Player player;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(308, 100, 50));
                        } else if (delay == 4) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            entity.getAnimator().forceAnimation(new Animation(-1));
                            entity.unlock();
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void start() {

                        super.start();
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.getAnimator().forceAnimation(new Animation(-1));
                        entity.graphics(new Graphics(-1));
                    }
                };
            }
        },

        MINIGAME(new TeleportSettings(6601, 1118, -1, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;
                    Player player;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            entity.getAnimator().forceAnimation(new Animation(getSettings().getStartEmote()));
                            entity.graphics(new Graphics(1118));
                        } else if (delay == 9) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                            entity.getAnimator().forceAnimation(new Animation(-1));
                            entity.unlock();
                            return true;
                        }
                        delay++;
                        return false;
                    }

                    @Override
                    public void start() {

                        super.start();
                    }

                    @Override
                    public void stop() {
                        super.stop();
                        entity.getAnimator().forceAnimation(new Animation(-1));
                        entity.graphics(new Graphics(-1));
                    }
                };
            }
        },

        INSTANT(new TeleportSettings(-1, -1, -1, -1)) {
            @Override
            public Pulse getPulse(final Entity entity, final Location location) {
                return new TeleportPulse(entity) {
                    int delay = 0;

                    @Override
                    public boolean pulse() {
                        if (delay == 0) {
                            entity.lock();
                        } else if (delay == 3) {
                            entity.getProperties().setTeleportLocation(Location.create(location));
                        } else if (delay == 4) {
                            entity.getAnimator().forceAnimation(Animation.RESET);
                            entity.unlock();
                            return true;
                        }
                        delay++;
                        return false;
                    }
                };
            }
        };

        private TeleportSettings settings;

        public abstract Pulse getPulse(final Entity entity, final Location location);

        TeleportType(TeleportSettings settings) {
            this.settings = settings;
        }

        public final TeleportSettings getSettings() {
            return settings;
        }
    }

    static class TeleportSettings {
        private int startAnim;
        private int endAnim;
        private int startGFX;
        private int endGFX;

        public TeleportSettings(int startAnim, int endAnim, int startGfx, int endGfx) {
            this.startAnim = startAnim;
            this.endAnim = endAnim;
            this.startGFX = startGfx;
            this.endGFX = endGfx;
        }

        public final int getStartEmote() {
            return startAnim;
        }

        public final int getEndEmote() {
            return endAnim;
        }

        public final int getStartGfx() {
            return startGFX;
        }

        public final int getEndGfx() {
            return endGFX;
        }
    }

    public static TeleportType getType(Player player) {
        switch (player.getSpellBookManager().getSpellBook()) {
            case 193:
                return TeleportType.ANCIENT;
            case 430:
                return TeleportType.LUNAR;
        }
        return TeleportType.NORMAL;
    }

    public int getTeleportType() {
        return teleportType;
    }

    public void setTeleportType(int teleportType) {
        this.teleportType = teleportType;
    }
}

