package content.global.travel.carpet;

import content.global.skill.agility.AgilityHandler;
import core.api.Container;
import core.game.container.impl.EquipmentContainer;
import core.game.dialogue.Dialogue;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Animation;
import core.plugin.Initializable;
import kotlin.Unit;
import org.rs.consts.Animations;
import org.rs.consts.Items;
import org.rs.consts.Quests;
import org.rs.consts.Sounds;

import static core.api.ContentAPIKt.*;
import static core.api.quest.QuestAPIKt.hasRequirement;
import static core.tools.GlobalsKt.colorize;

/**
 * The dialogue plugin used for the rug merchant.
 *
 * @author Vexia
 */
@Initializable
public final class RugMerchantDialogue extends Dialogue {

    /**
     * The ids of the rug merchants.
     */
    private static final int[] IDS = new int[]{2291, 2292, 2293, 2294, 2296, 2298, 3020};

    /**
     * The floating animation.
     */
    private static final Animation FLOATING_ANIMATION = new Animation(330);

    /**
     * The current reg destination.
     */
    private RugDestination current;

    /**
     * The rug destination options,.
     */
    private RugDestination[] options;

    /**
     * The destination o travel to.
     */
    private RugDestination destination;

    /**
     * Constructs a new {@code RugMerchantDialogue} {@code Object}.
     */
    public RugMerchantDialogue() {
    }

    @Override
    public void init() {
        super.init();
    }

    /**
     * Constructs a new {@code RugMerchantDialogue} {@code Object}.
     *
     * @param player the player.
     */
    public RugMerchantDialogue(final Player player) {
        super(player);
    }

    @Override
    public Dialogue newInstance(Player player) {
        return new RugMerchantDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
        npc = (NPC) args[0];
        current = RugDestination.forId(npc.getId());
        if (args.length >= 2) {
            options = getDestination(npc.getId());
            sendOptions(options);
            stage = 11;
            return true;
        }
        player("Hello.");
        return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
        switch (stage) {
            default:
                handleDefault(buttonId);
                break;
        }
        return true;
    }

    /**
     * Handles the default dialogue.
     *
     * @param buttonId the buttonId.
     */
    private final void handleDefault(final int buttonId) {
        int actualPrice = (inEquipment(player, Items.RING_OF_CHAROS_4202, 1) || inEquipment(player, Items.RING_OF_CHAROSA_6465, 1)) ? 100 : 200;
        switch (stage) {
            case 0:
                npc("Greetings, desert traveller. Do you require the services", "of Ali Morrisane's flying carpet fleet?");
                stage++;
                break;
            case 1:
                options("Yes, please.", "No thanks.");
                stage++;
                break;
            case 2:
                switch (buttonId) {
                    case 1:
                        player("Yes, please.");
                        stage = 10;
                        break;
                    case 2:
                        player("Actually, I've changed my mind.");
                        stage = 20;
                        break;
                }
                break;
            case 8:
                options("Yes.", "No.");
                stage++;
                break;
            case 9:
                if (buttonId == 2) {
                    end();
                } else {
                    destination = options[0];
                    player("Yes, please.");
                    stage = 11;
                }
                break;
            case 10:
                sendOptions((options = getDestination(npc.getId())));
                stage = 11;
                break;
            case 11:
                if (!player.getInventory().contains(995, actualPrice)) {
                    npc("A travel on one of my rugs costs " + actualPrice + " gold coins.");
                    stage = 20;
                    return;
                }
                end();
                destination = options.length == 1 ? options[0] : options[buttonId - 1];
                if (destination == RugDestination.UZER && !hasRequirement(player, Quests.THE_GOLEM))
                    break;
                else if (destination == RugDestination.BEDABIN_CAMP && !hasRequirement(player, Quests.THE_TOURIST_TRAP))
                    break;
                else if (destination == RugDestination.SOPHANEM && !hasRequirement(player, Quests.ICTHLARINS_LITTLE_HELPER))
                    break;
                if (player.getEquipment().get(EquipmentContainer.SLOT_WEAPON) != null) {
                    player.sendMessage(colorize("%RYou must unequip all your weapons before you can fly on a carpet."));
                } else {
                    if (player.getInventory().remove(new Item(995, actualPrice))) {
                        destination.travel(current, player);
                    }
                }
                break;
            case 20:
                end();
                break;
        }
    }

    /**
     * Sends the rug travelling destination options.
     *
     * @param destinations the destinations.
     */
    private void sendOptions(RugDestination[] destinations) {
        String[] options = new String[destinations.length];
        if (destinations.length == 1) {
            npc("Travel back to " + destinations[0].getName() + "?");
            stage = 8;
            return;
        }
        for (int i = 0; i < destinations.length; i++) {
            options[i] = destinations[i].getName();
        }
        setTitle(player, options.length);
        interpreter.sendOptions("Where do you wish to travel?", options);
    }

    /**
     * Gets the rug destinations for an npc id.
     *
     * @param npcId the npc id.
     * @return the rug destinations.
     */
    public static RugDestination[] getDestination(int npcId) {
        switch (npcId) {
            case 2291:
                return new RugDestination[]{RugDestination.UZER, RugDestination.BEDABIN_CAMP, RugDestination.NORTH_POLLNIVNEACH};
            case 2292:
                return new RugDestination[]{RugDestination.SHANTAY_PASS};
            case 2294:
                return new RugDestination[]{RugDestination.SHANTAY_PASS};
            case 2293:
                return new RugDestination[]{RugDestination.SHANTAY_PASS};
            case 3020:
                return new RugDestination[]{RugDestination.NARDAH, RugDestination.SOPHANEM};
            default:
                return new RugDestination[]{RugDestination.SOUTH_POLLNIVNEACH};
        }
    }

    @Override
    public int[] getIds() {
        return IDS;
    }

    /**
     * A destination for a rug.
     */
    public enum RugDestination {
        SHANTAY_PASS(2291, Location.create(3308, 3110, 0), "Shantay Pass"), BEDABIN_CAMP(2292, Location.create(3180, 3045, 0), "Bedabin Camp", Location.create(3305, 3107, 0), Location.create(3299, 3107, 0), Location.create(3285, 3088, 0), Location.create(3285, 3073, 0), Location.create(3268, 3073, 0), Location.create(3263, 3068, 0), Location.create(3246, 3068, 0), Location.create(3246, 3057, 0), Location.create(3232, 3057, 0), Location.create(3215, 3057, 0), Location.create(3200, 3057, 0), Location.create(3179, 3057, 0), Location.create(3179, 3047, 0), Location.create(3180, 3045, 0)), NORTH_POLLNIVNEACH(2294, Location.create(3349, 3003, 0), "North Pollnivneach", new Location(3308, 3096, 0), new Location(3308, 3079, 0), new Location(3308, 3066, 0), new Location(3311, 3057, 0), new Location(3319, 3042, 0), new Location(3332, 3033, 0), new Location(3341, 3020, 0), new Location(3350, 3009, 0), new Location(3351, 3003, 0), new Location(3349, 3003, 0)), UZER(2293, Location.create(3469, 3113, 0), "Uzer", Location.create(3308, 3105, 0), Location.create(3325, 3105, 0), Location.create(3332, 3105, 0), Location.create(3332, 3080, 0), Location.create(3341, 3080, 0), Location.create(3341, 3082, 0), Location.create(3358, 3082, 0), Location.create(3370, 3082, 0), Location.create(3382, 3082, 0), Location.create(3396, 3082, 0), Location.create(3432, 3082, 0), Location.create(3432, 3093, 0), Location.create(3440, 3093, 0), Location.create(3454, 3107, 0), Location.create(3469, 3107, 0), Location.create(3469, 3113, 0)), NARDAH(2296, Location.create(3401, 2916, 0), "Nardah", new Location(3351, 2942, 0), new Location(3350, 2936, 0), new Location(3362, 2936, 0), new Location(3380, 2928, 0), new Location(3392, 2920, 0), new Location(3397, 2916, 0), new Location(3401, 2916, 0)), SOPHANEM(2298, Location.create(3285, 2813, 0), "Sophanem", Location.create(3351, 2934, 0), Location.create(3351, 2928, 0), Location.create(3351, 2919, 0), Location.create(3346, 2902, 0), Location.create(3339, 2884, 0), Location.create(3328, 2877, 0), Location.create(3328, 2862, 0), Location.create(3328, 2845, 0), Location.create(3318, 2838, 0), Location.create(3307, 2828, 0), Location.create(3292, 2817, 0), Location.create(3285, 2818, 0), Location.create(3285, 2813, 0)), SOUTH_POLLNIVNEACH(3020, Location.create(3351, 2942, 0), "South Pollnivneach");

        /**
         * The npc id.
         */
        private final int npc;

        /**
         * The destination to go to.
         */
        private final Location location;

        /**
         * The name of the destination.
         */
        private final String name;

        /**
         * The location data.
         */
        private final Location[] locData;

        /**
         * Constructs a new {@code RugDestination} {@code Object}.
         *
         * @param npc         the npc.
         * @param destination the destination.
         */
        private RugDestination(int npc, Location destination, String name, Location... locData) {
            this.npc = npc;
            this.location = destination;
            this.name = name;
            this.locData = locData;
        }

        /**
         * Travels a player to a destination.
         *
         * @param player the player.
         */
        public void travel(final RugDestination current, final Player player) {
            player.lock();
            setVarp(player, 499, 0);
            player.getImpactHandler().setDisabledTicks(GameWorld.getTicks() + 200);
            player.getInterfaceManager().removeTabs(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13);
            player.getEquipment().replace(new Item(Items.MAGIC_CARPET_5614), EquipmentContainer.SLOT_WEAPON);
            player.getPacketDispatch().sendInterfaceConfig(548, 69, true);
            playAudio(player, Sounds.CARPET_RISE_1196);
            playJingle(player, 132);

            registerLogoutListener(player, "magic-carpet", (pl) -> {
                removeItem(pl, Items.MAGIC_CARPET_5614, Container.EQUIPMENT);
                return Unit.INSTANCE;
            });

            GameWorld.getPulser().submit(new Pulse(1, player) {
                int count;
                int index;
                Location[] locs = getLocData();

                @Override
                public boolean pulse() {
                    switch (++count) {
                        case 1:
                            if (RugDestination.this == SHANTAY_PASS || RugDestination.this == RugDestination.SOUTH_POLLNIVNEACH) {
                                Location[] temp = new Location[current.getLocData().length + 1];
                                int counter = 0;
                                for (int i = current.getLocData().length - 1; i >= 0; i--) {
                                    temp[counter++] = current.getLocData()[i];
                                }
                                temp[temp.length - 1] = RugDestination.this.getLocation();
                                locs = temp;
                            }
                            player.faceLocation(current.getLocation());
                            break;
                        case 2:
                            AgilityHandler.walk(player, -1, player.getLocation(), current.getLocation(), null, 0.0, null);
                            break;
                        case 3:
                            player.faceLocation(getLocation());
                            break;
                        case 4:
                            setVarp(player, 499, 1);
                            break;
                        case 200:
                            break;
                        case 901:
                            player.getEquipment().replace(null, EquipmentContainer.SLOT_WEAPON);
                            player.getInterfaceManager().restoreTabs();
                            player.getPacketDispatch().sendInterfaceConfig(548, 69, false);
                            player.getImpactHandler().setDisabledTicks(0);
                            player.unlock();
                            player.animate(new Animation(-1));
                            setVarp(player, 499, 0);
                            playAudio(player, Sounds.CARPET_DESCEND_1195);
                            animate(player, Animations.MAGIC_CARPET_LANDING_331, true);
                            clearLogoutListener(player, "magic-carpet");
                            player.getInterfaceManager().openDefaultTabs();
                            break;
                        case 902:
                            player.moveStep();
                            return true;
                        default:
                            if (index > locs.length - 1) {
                                count = 900;
                                break;
                            }
                            if (index == 0 || player.getLocation().equals(locs[index - 1])) {
                                AgilityHandler.walk(player, -1, player.getLocation(), locs[index++], null, 0.0, null, true);
                            }
                            return false;
                    }
                    return false;
                }

                @Override
                public void stop() {
                    super.stop();
                    player.unlock();
                }

            });
        }

        /**
         * Checks if the player has the requirements.
         *
         * @param player the player.
         * @return {@code True} if so.
         */
        public boolean hasRequirements(final Player player) {
            return true;
        }

        /**
         * Gets the rug destination object for the npc id.
         *
         * @param id the id.
         * @return the rug destination.
         */
        public static RugDestination forId(int id) {
            for (RugDestination dest : values()) {
                if (dest.getNpc() == id) {
                    return dest;
                }
            }
            return null;
        }

        public int getNpc() {
            return npc;
        }

        public Location getLocation() {
            return location;
        }

        public String getName() {
            return name;
        }

        public Location[] getLocData() {
            return locData;
        }
    }

}
