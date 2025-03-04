package core.game.node.entity.player;

import content.global.handlers.item.equipment.EquipmentDegrade;
import content.global.skill.construction.HouseManager;
import content.global.skill.runecrafting.pouch.PouchManager;
import content.global.skill.summoning.familiar.FamiliarManager;
import core.GlobalStatistics;
import core.ServerConstants;
import core.api.EquipmentSlot;
import core.cache.def.impl.ItemDefinition;
import core.game.component.Component;
import core.game.container.Container;
import core.game.container.ContainerType;
import core.game.container.impl.BankContainer;
import core.game.container.impl.EquipmentContainer;
import core.game.container.impl.InventoryListener;
import core.game.dialogue.DialogueInterpreter;
import core.game.ge.ExchangeHistory;
import core.game.ge.GrandExchangeOffer;
import core.game.interaction.InteractPlugin;
import core.game.interaction.InteractionListeners;
import core.game.interaction.QueueStrength;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.CombatSwingHandler;
import core.game.node.entity.combat.DeathTask;
import core.game.node.entity.combat.ImpactHandler.HitsplatType;
import core.game.node.entity.combat.equipment.special.ChinchompaSwingHandler;
import core.game.node.entity.combat.equipment.special.SalamanderSwingHandler;
import core.game.node.entity.combat.graves.Grave;
import core.game.node.entity.combat.graves.GraveController;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.info.*;
import core.game.node.entity.player.info.login.LoginConfiguration;
import core.game.node.entity.player.info.login.PlayerParser;
import core.game.node.entity.player.link.*;
import core.game.node.entity.player.link.appearance.Appearance;
import core.game.node.entity.player.link.diary.DiaryManager;
import core.game.node.entity.player.link.emote.EmoteManager;
import core.game.node.entity.player.link.music.MusicPlayer;
import core.game.node.entity.player.link.prayer.Prayer;
import core.game.node.entity.player.link.prayer.PrayerType;
import core.game.node.entity.player.link.quest.QuestRepository;
import core.game.node.entity.player.link.request.RequestManager;
import core.game.node.entity.skill.Skills;
import core.game.node.entity.state.State;
import core.game.node.entity.state.StateRepository;
import core.game.node.item.GroundItem;
import core.game.node.item.GroundItemManager;
import core.game.node.item.Item;
import core.game.system.communication.CommunicationInfo;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.*;
import core.game.world.map.build.DynamicRegion;
import core.game.world.map.path.Pathfinder;
import core.game.world.map.zone.ZoneType;
import core.game.world.repository.Repository;
import core.game.world.update.MapChunkRenderer;
import core.game.world.update.NPCRenderer;
import core.game.world.update.PlayerRenderer;
import core.game.world.update.UpdateSequence;
import core.game.world.update.flag.EntityFlag;
import core.game.world.update.flag.PlayerFlag;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.net.IoSession;
import core.net.packet.PacketRepository;
import core.net.packet.context.DynamicSceneContext;
import core.net.packet.context.SceneGraphContext;
import core.net.packet.context.SkillContext;
import core.net.packet.out.BuildDynamicScene;
import core.net.packet.out.SkillLevel;
import core.net.packet.out.UpdateSceneGraph;
import core.plugin.Plugin;
import core.tools.Log;
import core.tools.RandomFunction;
import core.tools.StringUtils;
import core.tools.TickUtilsKt;
import core.worker.ManagementEvents;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.rs.consts.Items;
import org.rs.consts.Sounds;
import proto.management.ClanLeaveNotification;
import proto.management.PlayerStatusUpdate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static core.api.ContentAPIKt.*;
import static core.api.utils.PermadeathKt.Permadeath;
import static core.tools.GlobalsKt.colorize;

/**
 * Represents a player in the game.
 */
public class Player extends Entity {

    /**
     * Player details containing account-related information.
     */
    private PlayerDetails details;

    /**
     * Flag indicating whether the player is inside a wardrobe.
     */
    public boolean inWardrobe = false;

    /**
     * The player's starting location.
     */
    public Location startLocation = null;

    /**
     * Flag indicating if the player is new (total level below 50).
     */
    public boolean newPlayer = getSkills().getTotalLevel() < 50;

    /**
     * The player's drop log.
     */
    public BankContainer dropLog = new BankContainer(this);

    /**
     * Manages equipment degradation for the player. */
    public EquipmentDegrade degrader = new EquipmentDegrade();

    /**
     * Manages pouches for storing resources. */
    public PouchManager pouchManager = new PouchManager(this);

    /**
     * Manages variable persistence for the player.
     */
    public VarpManager varpManager = new VarpManager(this);

    /**
     *
     * Stores player-specific variables.
     */
    public HashMap<Integer, Integer> varpMap = new HashMap<>();

    /**
     * Stores variable states that should be saved.
     */
    public HashMap<Integer, Boolean> saveVarp = new HashMap<>();

    /**
     * Stores various player states.
     */
    public HashMap<String, State> states = new HashMap<>();

    /**
     * Stores functions to be executed when the player logs out.
     */
    public HashMap<String, Function1<Player, Unit>> logoutListeners = new HashMap<>();

    /**
     * The player's inventory, limited to 28 slots.
     */
    private final Container inventory = new Container(28).register(new InventoryListener(this));

    /**
     * The player's equipped items.
     */
    private final EquipmentContainer equipment = new EquipmentContainer(this);

    /**
     * The player's primary and secondary banks.
     */
    private final BankContainer bank = new BankContainer(this);
    private final BankContainer bankSecondary = new BankContainer(this);

    /**
     * Flag to determine if the secondary bank is in use.
     */
    public boolean useSecondaryBank = false;

    /**
     * Containers for Blast Furnace resources.
     */
    public final Container blastCoal = new Container(225, ContainerType.NEVER_STACK);
    public final Container blastOre = new Container(28, ContainerType.NEVER_STACK);
    public final Container blastBars = new Container(28, ContainerType.NEVER_STACK);

    /**
     * Handles packet dispatching for the player.
     */
    private final PacketDispatch packetDispatch = new PacketDispatch(this);

    /**
     * Manages the player's spell book.
     */
    private final SpellBookManager spellBookManager = new SpellBookManager();

    /**
     * Stores player rendering information.
     */
    private final RenderInfo renderInfo = new RenderInfo(this);

    /**
     * Manages interface interactions.
     */
    private final InterfaceManager interfaceManager = new InterfaceManager(this);

    /**
     * Handles the player's emotes.
     */
    private final EmoteManager emoteManager = new EmoteManager(this);

    /**
     * Stores player-specific flags.
     */
    private final PlayerFlag playerFlag = new PlayerFlag();

    /**
     * Manages the player's appearance.
     */
    private final Appearance appearance = new Appearance(this);

    /**
     * Manages player settings.
     */
    private final Settings settings = new Settings(this);

    /**
     * Manages player dialogues.
     */
    private final DialogueInterpreter dialogueInterpreter = new DialogueInterpreter(this);

    /**
     * Manages hint icons displayed to the player.
     */
    private final HintIconManager hintIconManager = new HintIconManager();

    /**
     * Handles player quest progress.
     */
    public QuestRepository questRepository = new QuestRepository(this);

    /**
     * Manages the player's prayers.
     */
    private final Prayer prayer = new Prayer(this);

    /**
     * Handles skull effects on the player.
     */
    private final SkullManager skullManager = new SkullManager(this);

    /**
     * Manages the player's familiar (if any).
     */
    private final FamiliarManager familiarManager = new FamiliarManager(this);

    /**
     * Stores persistent player data.
     */
    public SavedData savedData = new SavedData(this);

    /**
     * Manages player requests (e.g., trades, duels).
     */
    private final RequestManager requestManager = new RequestManager(this);

    /**
     * Handles the player's music playback.
     */
    private final MusicPlayer musicPlayer = new MusicPlayer(this);

    /**
     * Manages the player's house.
     */
    private final HouseManager houseManager = new HouseManager();

    /**
     * Handles the player's bank PIN system.
     */
    private final BankPinManager bankPinManager = new BankPinManager(this);

    /**
     * Manages player diary progress.
     */
    private final DiaryManager diaryManager = new DiaryManager(this);

    /**
     * Manages Ironman status and restrictions.
     */
    private final IronmanManager ironmanManager = new IronmanManager(this);

    /**
     * Indicates whether the player is currently active.
     */
    private boolean playing;

    /**
     * Indicates whether the player is invisible.
     */
    private boolean invisible;

    /**
     * Indicates if the player is artificially created (e.g., NPC-like entities).
     */
    protected boolean artificial;

    /**
     * Stores a custom state string.
     */
    private String customState = "";

    /**
     * Tracks the player's archery target count.
     */
    private int archeryTargets = 0;

    /**
     * Tracks the total number of archery shots.
     */
    private int archeryTotal = 0;

    /**
     * The player's save file version.
     */
    public int version = ServerConstants.CURRENT_SAVEFILE_VERSION;

    /**
     * Operation counts for player actions.
     */
    public byte[] opCounts = new byte[255];

    /**
     * Count of invalid packets received.
     */
    public int invalidPacketCount = 0;

    /**
     * Constructs a new player with the specified details.
     *
     * @param details The player's account details.
     */
    public Player(PlayerDetails details) {
        super(details.getUsername(), ServerConstants.START_LOCATION);
        super.active = false;
        super.interactPlugin = new InteractPlugin(this);
        this.details = details;
        this.direction = Direction.SOUTH;
    }

    /**
     * Initializes the player.
     */
    @Override
    public void init() {
        if (!artificial)
            log(this.getClass(), Log.INFO, getUsername() + " initialising...");
        if (!artificial) {
            // getProperties().setSpawnLocation(ServerConstants.HOME_LOCATION);
            getDetails().getSession().setObject(this);
        }
        super.init();
        LoginConfiguration.configureLobby(this);
        setAttribute("logged-in-fully", true);
    }

    /**
     * Clears the player's state, handling disconnections and saving progress.
     */
    @Override
    public void clear() {
        if (isArtificial()) {
            finishClear();
            return;
        }
        Repository.getDisconnectionQueue().remove(getName());
        Repository.getDisconnectionQueue().add(this, true);
        details.save();
    }

    /**
     * Final cleanup when the player logs out or is removed.
     */
    public void finishClear() {
        if (!isArtificial())
            GameWorld.getLogoutListeners().forEach((it) -> it.logout(this));
        setPlaying(false);
        getWalkingQueue().reset();
        if (!logoutListeners.isEmpty()) {
            logoutListeners.forEach((key, method) -> method.invoke(this));
        }
        if (familiarManager.hasFamiliar()) {
            familiarManager.getFamiliar().clear();
        }
        interfaceManager.close();
        interfaceManager.closeSingleTab();
        super.clear();
        getZoneMonitor().clear();
        HouseManager.leave(this);
        UpdateSequence.getRendererPlayers().remove(this);
        sendLogoutEvents();
        checkForWealthUpdate(true);
    }

    private void sendLogoutEvents() {
        PlayerStatusUpdate.Builder statusBuilder = PlayerStatusUpdate.newBuilder();
        statusBuilder.setUsername(this.name);
        statusBuilder.setWorld(0); //offline
        statusBuilder.setNotifyFriendsOnly(false);
        ManagementEvents.publish(statusBuilder.build());

        if (getCommunication().getClan() != null) {
            ClanLeaveNotification.Builder event = ClanLeaveNotification.newBuilder();
            event.setUsername(getName());
            event.setWorld(GameWorld.getSettings().getWorldId());
            event.setClanName(getCommunication().getClan().getOwner());
            ManagementEvents.publish(event.build());
        }
    }

    public void toggleWardrobe(boolean intoWardrobe) {
        class wardrobePulse extends Pulse {
            final Player player;
            boolean first = true;

            wardrobePulse(Player player) {
                this.player = player;
            }

            @Override
            public boolean pulse() {
                if (first) {
                    player.visualize(new Animation(1241), new Graphics(1181, 0, 0));
                    first = false;
                    return !player.inWardrobe;
                }
                if (player.inWardrobe) {
                    player.visualize(new Animation(1241), new Graphics(1182));
                } else {
                    player.visualize(new Animation(1241), new Graphics(1183, 0, 0));
                    player.getPulseManager().run(new Pulse(1) {
                        @Override
                        public boolean pulse() {
                            player.getAnimator().reset();
                            player.packetDispatch.sendInterfaceConfig(548, 69, false);
                            return true;
                        }
                    });
                }
                return !player.inWardrobe;
            }
        }
        if (intoWardrobe) {
            packetDispatch.sendInterfaceConfig(548, 69, true);
            GameWorld.getPulser().submit(new wardrobePulse(this));
            inWardrobe = true;
        } else {
            inWardrobe = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        musicPlayer.tick();
        if (getAttribute("fire:immune", 0) > 0) {
            int time = getAttribute("fire:immune", 0) - GameWorld.getTicks();
            if (time == TickUtilsKt.secondsToTicks(30)) {
                sendMessage(colorize("%RYou have 30 seconds remaining on your antifire potion."));
                playAudio(this, Sounds.CLOCK_TICK_1_3120, 0, 3);
            }
            if (time == 0) {
                sendMessage(colorize("%RYour antifire potion has expired."));
                removeAttribute("fire:immune");
                playAudio(this, Sounds.DRAGON_POTION_FINISHED_2607);
            }
        }
        if (getAttribute("poison:immunity", 0) > 0) {
            int time = getAttribute("poison:immunity", 0) - GameWorld.getTicks();
            debug(time + "");
            if (time == TickUtilsKt.secondsToTicks(30)) {
                sendMessage(colorize("%RYou have 30 seconds remaining on your antipoison potion."));
                playAudio(this, Sounds.CLOCK_TICK_1_3120, 0, 3);
            }
            if (time == 0) {
                sendMessage(colorize("%RYour antipoison potion has expired."));
                removeAttribute("poison:immunity");
                playAudio(this, Sounds.DRAGON_POTION_FINISHED_2607);
            }
        }
        if (getAttribute("infinite-special", false)) {
            settings.setSpecialEnergy(100);
        }

        // Decrements prayer points
        getPrayer().tick();

        // Update wealth tracking
        checkForWealthUpdate(false);
    }

    private void checkForWealthUpdate(boolean force) {
        if (isArtificial()) return;
        long previousWealth = getAttribute("last-wealth", -1L);
        long lastWealthCheck = getAttribute("last-wealth-check", -1L);

        long nowTime = System.currentTimeMillis();
        if (force || nowTime - lastWealthCheck >= TimeUnit.MINUTES.toMillis(5)) {
            long totalWealth = 0L;
            // Calculate total wealth from inventory, bank, and other sources.
            for (Item i : inventory.toArray()) {
                if (i == null) continue;
                totalWealth += (long) i.getDefinition().getValue() * i.getAmount();
            }
            for (Item i : bank.toArray()) {
                if (i == null) break;
                totalWealth += (long) i.getDefinition().getValue() * i.getAmount();
            }
            for (Item i : bankSecondary.toArray()) {
                if (i == null) break;
                totalWealth += (long) i.getDefinition().getValue() * i.getAmount();
            }
            ExchangeHistory ge = ExchangeHistory.getInstance(this);
            for (int i = 0; i < 6; i++) {
                GrandExchangeOffer offer = ge.getOffer(i);
                if (offer != null) {
                    totalWealth += offer.cacheValue();
                }
            }

            // Add cannon items' value if the player is constructing a cannon.
            if (this.getAttribute("dmc", null) != null) {
                totalWealth += ItemDefinition.forId(Items.CANNON_BASE_6).getValue();
                totalWealth += ItemDefinition.forId(Items.CANNON_STAND_8).getValue();
                totalWealth += ItemDefinition.forId(Items.CANNON_BARRELS_10).getValue();
                totalWealth += ItemDefinition.forId(Items.CANNON_FURNACE_12).getValue();
            }

            long diff = previousWealth == -1 ? 0L : totalWealth - previousWealth;
            setAttribute("/save:last-wealth", totalWealth);
            setAttribute("/save:last-wealth-check", nowTime);

            if (diff != 0)
                PlayerMonitor.logWealthChange(this, totalWealth, diff);
        }
    }

    @Override
    public void update() {
        super.update();
        if (playerFlag.isUpdateSceneGraph()) {
            updateSceneGraph(false);
        }
        PlayerRenderer.render(this);
        NPCRenderer.render(this);
        MapChunkRenderer.render(this);
    }

    @Override
    public void reset() {
        super.reset();
        playerFlag.setUpdateSceneGraph(false);
        renderInfo.updateInformation();
        if (getSkills().isLifepointsUpdate()) {
            PacketRepository.send(SkillLevel.class, new SkillContext(this, Skills.HITPOINTS));
            getSkills().setLifepointsUpdate(false);
        }
        if (getAttribute("flagged-for-save", false)) {
            PlayerParser.saveImmediately(this);
            removeAttribute("flagged-for-save");
        }
        Arrays.fill(opCounts, (byte) 0);
    }

    @Override
    public int getClientIndex() {
        return this.getIndex() | 0x8000;
    }

    @Override
    public void onAttack(Entity e) {
        if (e instanceof Player) {
            Player p = (Player) e;
            if (skullManager.isWildernessDisabled()) {
                return;
            }
        }
    }

    @Override
    public CombatSwingHandler getSwingHandler(boolean swing) {
        CombatStyle style = getProperties().getCombatPulse().getStyle();
        int weaponId = equipment.getNew(3).getId();
        if (swing) {
            if (getProperties().getSpell() != null || getProperties().getAutocastSpell() != null) {
                return CombatStyle.MAGIC.getSwingHandler();
            }
            if (settings.isSpecialToggled()) {
                CombatSwingHandler handler;
                if ((handler = style.getSwingHandler().getSpecial(weaponId)) != null) {
                    return handler;
                }
                packetDispatch.sendMessage("Unhandled special attack for item " + weaponId + "!");
            }
        }
        if (style == CombatStyle.RANGE && weaponId == 10033 || weaponId == 10034) {
            return ChinchompaSwingHandler.getInstance();
        }
        if (weaponId >= 10146 && weaponId <= 10149) {
            return SalamanderSwingHandler.Companion.getINSTANCE();
        }
        return style.getSwingHandler();
    }

    @Override
    public void commenceDeath(Entity killer) {
        if (!isPlaying()) return;
        super.commenceDeath(killer);
        if (prayer.get(PrayerType.RETRIBUTION)) {
            prayer.startRetribution(killer);
        }
    }

    @Override
    public void finalizeDeath(Entity killer) {
        if (!isPlaying())
            return; //if the player has already been full cleared, it has already disconnected. This code is probably getting called because something is maintaining a stale reference.
        GlobalStatistics.incrementDeathCount();
        settings.setSpecialEnergy(100);
        settings.updateRunEnergy(settings.getRunEnergy() - 100);
        Player k = killer instanceof Player ? (Player) killer : this;
        if (!k.isActive()) {
            k = this;
        }
        if (this.isArtificial() && killer instanceof Player) {
            setAttribute("dead", true);
        }
        if (this.isArtificial() && killer instanceof NPC) {
            return;
        }
        if (killer instanceof Player && killer.getName() != getName()

            && getWorldTicks() - killer.getAttribute("/save:last-murder-news", 0) >= 500) {
            Item wep = getItemFromEquipment((Player) killer, EquipmentSlot.WEAPON);
            sendNews(killer.getUsername() + " has murdered " + getUsername() + " with " + (wep == null ? "their fists." : (StringUtils.isPlusN(wep.getName()) ? "an " : "a ") + wep.getName()));
            killer.setAttribute("/save:last-murder-news", getWorldTicks());
        }
        getPacketDispatch().sendMessage("Oh dear, you are dead!");
        packetDispatch.sendTempMusic(90);
        if (!getZoneMonitor().handleDeath(killer) && (!getProperties().isSafeZone() && getZoneMonitor().getType() != ZoneType.SAFE.getId()) && getDetails().getRights() != Rights.ADMINISTRATOR) {

            if (this.getIronmanManager().getMode().equals(IronmanMode.HARDCORE)) {

                if (getAttributes().containsKey("permadeath")) {
                    Permadeath(this);
                    return;
                }
            }
            GroundItemManager.create(new Item(Items.BONES_526), this.getAttribute("/save:original-loc", location), k);
            final Container[] c = DeathTask.getContainers(this);

            for (Item i : getEquipment().toArray()) {
                if (i == null) continue;
                InteractionListeners.run(i.getId(), this, i, false);
                Plugin equipPlugin = i.getDefinition().getConfiguration("equipment", null);
                if (equipPlugin != null) equipPlugin.fireEvent("unequip");
            }

            boolean canCreateGrave = GraveController.allowGenerate(this);
            if (canCreateGrave) {
                Grave g = GraveController.produceGrave(GraveController.getGraveType(this));
                g.initialize(this, location, Arrays.stream(c[1].toArray()).filter(Objects::nonNull).toArray(Item[]::new)); //note: the amount of code required to filter nulls from an array in Java is atrocious.
            } else {
                StringBuilder itemsLost = new StringBuilder();
                int coins = 0;
                for (Item item : c[1].toArray()) {
                    boolean stayPrivate = false;
                    if (item == null) continue;
                    if (killer instanceof Player)
                        itemsLost.append(getItemName(item.getId())).append("(").append(item.getAmount()).append("), ");
                    if (GraveController.shouldCrumble(item.getId()))
                        continue;
                    if (GraveController.shouldRelease(item.getId()))
                        continue;
                    if (!item.getDefinition().isTradeable()) {
                        if (killer instanceof Player) {
                            int value = item.getDefinition().getAlchemyValue(true);
                            if (getStatLevel(killer, Skills.MAGIC) < 55) value /= 2;
                            coins += Math.max(0, value - 250);
                            continue;
                        } else stayPrivate = true;
                    }
                    item = GraveController.checkTransform(item);
                    GroundItem gi = GroundItemManager.create(item, location, killer instanceof Player ? (Player) killer : this);
                    gi.setRemainPrivate(stayPrivate);
                }
                if (coins > 0) {
                    GroundItemManager.create(new Item(Items.COINS_995, coins), location, (Player) killer);
                }
                if (killer instanceof Player)
                    PlayerMonitor.log((Player) killer, LogType.PK, "Killed " + name + ", who dropped: " + itemsLost);
                sendMessage(colorize("%RDue to the circumstances of your death, you do not have a grave."));
            }

            equipment.clear();
            inventory.clear();
            inventory.addAll(c[0]);
            familiarManager.dismiss();
        }
        skullManager.setSkulled(false);
        removeAttribute("combat-time");
        getPrayer().reset();
        removeAttribute("original-loc"); //in case you died inside a random event
        interfaceManager.openDefaultTabs(); //in case you died inside a random that had blanked them
        setComponentVisibility(this, 548, 69, false); //reenable the logout button (SD)
        setComponentVisibility(this, 746, 12, false); //re-enable the logout button (HD)
        super.finalizeDeath(killer);
        appearance.sync();
        if (!getSavedData().globalData.isDeathScreenDisabled()) {
            getInterfaceManager().open(new Component(153));
        }
    }

    @Override
    public boolean hasProtectionPrayer(CombatStyle style) {
        if (style == null) {
            return false;
        }
        return prayer.get(style.getProtectionPrayer());
    }

    @Override
    public int getDragonfireProtection(boolean fire) {
        int value = 0;
        if (fire) {
            if (hasFireResistance()) {
                value |= 0x2;
            }
        }
        Item item = equipment.get(EquipmentContainer.SLOT_SHIELD);
        if (item != null && (item.getId() == 11283 || item.getId() == 11284 || (fire && (item.getId() == 1540) || (!fire && (item.getId() == 2890 || item.getId() == 9731))))) {
            value |= 0x4;
        }
        if (prayer.get(PrayerType.PROTECT_FROM_MAGIC)) {
            value |= 0x8;
        }
        setAttribute("fire_resistance", value);
        return value;
    }

    @Deprecated
    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
    }

    @Override
    public void fullRestore() {
        prayer.reset();
        settings.setSpecialEnergy(100);
        settings.updateRunEnergy(-100);
        super.fullRestore();
    }

    @Override
    public boolean isAttackable(Entity entity, CombatStyle style, boolean message) {
        if (entity instanceof NPC && !((NPC) entity).getDefinition().hasAction("attack") && !((NPC) entity).isIgnoreAttackRestrictions(this)) {
            return false;
        }
        if (entity instanceof Player) {
            Player p = (Player) entity;
            if (p.getSkullManager().isWilderness() && skullManager.isWilderness()) {
                if (!GameWorld.getSettings().getWild_pvp_enabled())
                    return false;
                if (p.getSkullManager().hasWildernessProtection())
                    return false;
                if (skullManager.hasWildernessProtection())
                    return false;
                return true;
            } else return false;
        }
        return super.isAttackable(entity, style, message);
    }

    @Override
    public boolean isPoisonImmune() {
        return timers.getTimer("poison:immunity") != null;
    }

    @Override
    public void onImpact(final Entity entity, BattleState state) {
        super.onImpact(entity, state);
        boolean recoil = getEquipment().getNew(EquipmentContainer.SLOT_RING).getId() == 2550;
        if (state.getEstimatedHit() > 0) {
            if (getAttribute("vengeance", false)) {
                removeAttribute("vengeance");
                final int hit = (int) (state.getEstimatedHit() * 0.75);
                sendChat("Taste vengeance!");
                if (hit > -1) {
                    entity.getImpactHandler().manualHit(Player.this, hit, HitsplatType.NORMAL);
                }
            }
            if (recoil) {
                getImpactHandler().handleRecoilEffect(entity, state.getEstimatedHit());
            }
        }
        if (state.getSecondaryHit() > 0) {
            if (recoil) {
                getImpactHandler().handleRecoilEffect(entity, state.getSecondaryHit());
            }
        }
        degrader.checkArmourDegrades(this);
    }

    public void randomWalk(int radiusX, int radiusY) {
        Pathfinder.find(this, this.getLocation().transform(RandomFunction.random(radiusX, (radiusX * -1)), RandomFunction.random(radiusY, (radiusY * -1)), 0), false, Pathfinder.SMART).walk(this);
    }

    public void initReconnect() {
        getInterfaceManager().setChatbox(null);
        getPulseManager().clear();
        getZoneMonitor().getZones().clear();
        getViewport().setCurrentPlane(RegionManager.forId(66666).getPlanes()[3]);
        playerFlag.setLastSceneGraph(null);
        playerFlag.setUpdateSceneGraph(false);
        playerFlag.setLastViewport(new RegionChunk[Viewport.CHUNK_SIZE][Viewport.CHUNK_SIZE]);
        renderInfo.getLocalNpcs().clear();
        renderInfo.getLocalPlayers().clear();
        renderInfo.setLastLocation(null);
        renderInfo.setOnFirstCycle(true);
        Arrays.fill(renderInfo.getAppearanceStamps(), 0);
    }

    public boolean isWearingVoid(CombatStyle style) {
        int helm;
        if (style == CombatStyle.MELEE) {
            helm = Items.VOID_MELEE_HELM_11665;
        } else if (style == CombatStyle.RANGE) {
            helm = Items.VOID_RANGER_HELM_11664;
        } else if (style == CombatStyle.MAGIC) {
            helm = Items.VOID_MAGE_HELM_11663;
        } else {
            return false;
        }
        boolean legs = inEquipment(this, Items.VOID_KNIGHT_ROBE_8840, 1);
        boolean top = inEquipment(this, Items.VOID_KNIGHT_TOP_8839, 1)
            || inEquipment(this, Items.VOID_KNIGHT_TOP_10611, 1);
        boolean gloves = inEquipment(this, Items.VOID_KNIGHT_GLOVES_8842, 1);
        return inEquipment(this, helm, 1) && legs && top && gloves;
    }

    public void updateSceneGraph(boolean login) {
        Region region = getViewport().getRegion();
        if (region instanceof DynamicRegion || region == null && (region = RegionManager.forId(location.getRegionId())) instanceof DynamicRegion) {
            PacketRepository.send(BuildDynamicScene.class, new DynamicSceneContext(this, login));
        } else {
            PacketRepository.send(UpdateSceneGraph.class, new SceneGraphContext(this, login));
        }
    }

    public void toggleDebug() {
        boolean debug = getAttribute("debug", false);
        setAttribute("debug", !debug);
        getPacketDispatch().sendMessage("Your debug mode is toggled to " + !debug + ".");
    }

    public void sendMessages(String... messages) {
        packetDispatch.sendMessages(messages);
    }

    public void sendMessage(String message) {
        sendMessages(message);
    }

    public void sendNotificationMessage(String message) {
        sendMessages("<col=ff0000>" + message + "</col>");
    }

    public boolean spawnZone() {
        return (getLocation().getX() > 3090 && getLocation().getY() < 3500
            && getLocation().getX() < 3099 && getLocation().getY() > 3487);
    }

    public boolean canSpawn() {
        if (!spawnZone()) {
            sendMessage("You can only spawn items inside the Edgeville bank.");
            return true;
        }
        if (inCombat() || getLocks().isInteractionLocked() || getSkullManager().isWilderness() || getAttribute("activity", null) != null) {
            sendMessage("<col=FF0000>You can't spawn items at the moment.");
            return true;
        }
        return false;
    }

    public void sendMessage(String message, int ticks) {
        packetDispatch.sendMessage(message, ticks);
    }

    public void debug(String string) {
        if (getAttribute("debug", false)) {
            packetDispatch.sendMessage(string);
        }
    }

    public boolean isMale() {
        return this.getAppearance().getGender().ordinal() == 0;
    }

    @SuppressWarnings("deprecation")
    public void updateDetails(PlayerDetails details) {
        if (this.details != null) {
            details.setBanTime(this.details.getBanTime());
            details.setMuteTime(this.details.getMuteTime());
            details.setTimePlayed(this.details.getTimePlayed());
        }
        details.getSession().setObject(this);
        this.details = details;
    }

    public boolean allowRemoval() {
        return !(inCombat() || getSkills().getLifepoints() < 1 || DeathTask.isDead(this) || isTeleporting() || scripts.hasTypeInQueue(QueueStrength.SOFT));
    }

    public boolean hasItem(Item item) {
        return getInventory().containsItem(item) || getBank().containsItem(item) || getEquipment().containsItem(item);
    }

    public double getExperienceMod() {
        return getSavedData().globalData.hasDoubleExp() ? 2 : 1;
    }

    public boolean isStaff() {
        return getDetails().getRights() != Rights.REGULAR_PLAYER;
    }

    public boolean isAdmin() {
        return getDetails().getRights() == Rights.ADMINISTRATOR;
    }

    public boolean isDebug() {
        return details.getRights() == Rights.ADMINISTRATOR && getAttribute("debug", false);
    }

    public UIDInfo getUidInfo() {
        return details.getInfo();
    }

    public PlayerDetails getDetails() {
        return details;
    }

    public String getName() {
        return super.getName();
    }

    public IoSession getSession() {
        return details.getSession();
    }

    public EquipmentContainer getEquipment() {
        return equipment;
    }

    public BankContainer getBank() {
        return useSecondaryBank ? bankSecondary : bank;
    }

    public BankContainer getBankPrimary() {
        return bank;
    }

    public BankContainer getBankSecondary() {
        return bankSecondary;
    }

    public BankContainer getDropLog() {
        return dropLog;
    }

    public Container getInventory() {
        return inventory;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }

    public Rights getRights() {
        return details.getRights();
    }

    public RenderInfo getRenderInfo() {
        return renderInfo;
    }

    public Appearance getAppearance() {
        return appearance;
    }

    public PlayerFlag getPlayerFlags() {
        return playerFlag;
    }

    public PacketDispatch getPacketDispatch() {
        return packetDispatch;
    }

    public SpellBookManager getSpellBookManager() {
        return spellBookManager;
    }

    public Settings getSettings() {
        return settings;
    }

    public InterfaceManager getInterfaceManager() {
        return interfaceManager;
    }

    public boolean hasModalOpen() {
        int[] excludedIds = new int[]{372, 421, InterfaceManager.DEFAULT_CHATBOX}; // Excludes plain message, plain message with scrollbar, and normal chatbox
        Component openedIface = interfaceManager.getOpened();
        Component openChatbox = interfaceManager.getChatbox();

        boolean hasModal = false;

        if (openedIface != null) {
            for (int i = 0; i < excludedIds.length; i++) {
                if (excludedIds[i] == openedIface.getId()) break;
                else if (i == excludedIds.length - 1) hasModal = true;
            }
        }

        if (openChatbox != null) {
            for (int i = 0; i < excludedIds.length; i++) {
                if (excludedIds[i] == openChatbox.getId()) break;
                else if (i == excludedIds.length - 1) hasModal = true;
            }
        }

        return hasModal;
    }

    public DialogueInterpreter getDialogueInterpreter() {
        return dialogueInterpreter;
    }

    public HintIconManager getHintIconManager() {
        return hintIconManager;
    }

    public boolean isArtificial() {
        return artificial;
    }

    public QuestRepository getQuestRepository() {
        return questRepository;
    }

    public Prayer getPrayer() {
        return prayer;
    }

    public SkullManager getSkullManager() {
        return skullManager;
    }

    public FamiliarManager getFamiliarManager() {
        return familiarManager;
    }

    public CommunicationInfo getCommunication() {
        return details.getCommunication();
    }

    public RequestManager getRequestManager() {
        return requestManager;
    }

    public SavedData getSavedData() {
        return savedData;
    }

    public GlobalData getGlobalData() {
        return savedData.globalData;
    }

    public MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public HouseManager getHouseManager() {
        return houseManager;
    }

    public BankPinManager getBankPinManager() {
        return bankPinManager;
    }

    public DiaryManager getAchievementDiaryManager() {
        return diaryManager;
    }

    public IronmanManager getIronmanManager() {
        return ironmanManager;
    }

    public EmoteManager getEmoteManager() {
        return emoteManager;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    @Override
    public String getUsername() {
        return StringUtils.formatDisplayName(getName());
    }

    @Override
    public String toString() {
        return "Player [name=" + name + ", getRights()=" + getRights() + "]";
    }

    public String getCustomState() {
        return customState;
    }

    public void setCustomState(String state) {
        this.customState = state;
    }

    public int getArcheryTargets() {
        return archeryTargets;
    }

    public void setArcheryTargets(int archeryTargets) {
        this.archeryTargets = archeryTargets;
    }

    public int getArcheryTotal() {
        return archeryTotal;
    }

    public void setArcheryTotal(int archeryTotal) {
        this.archeryTotal = archeryTotal;
    }

    public boolean hasActiveState(String key) {
        State state = states.get(key);
        return state != null && state.getPulse() != null;
    }

    public State registerState(String key) {
        return StateRepository.forKey(key, this);
    }

    public void clearState(String key) {
        State state = states.get(key);
        if (state != null) {
            Pulse pulse = state.getPulse();
            if (pulse != null) {
                pulse.stop();
            }
            states.remove(key);
        }
    }

    public void updateAppearance() {
        getUpdateMasks().register(EntityFlag.Appearance, this);
    }

    public void incrementInvalidPacketCount() {
        invalidPacketCount++;
        if (invalidPacketCount >= 5) {
            clear();
            log(this.getClass(), Log.ERR, "Disconnecting " + getName() + " for having a high rate of invalid packets. Potential packet bot misbehaving, or simply really bad connection.");
        }
    }
}
