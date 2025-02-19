package content.global.skill.summoning.familiar;

import content.global.skill.summoning.SummoningPouch;
import content.global.skill.summoning.SummoningScroll;
import content.global.skill.summoning.pet.Pet;
import core.cache.def.impl.NPCDefinition;
import core.game.interaction.MovementPulse;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.BattleState;
import core.game.node.entity.combat.CombatPulse;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.CombatSwingHandler;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.impl.Projectile;
import core.game.node.entity.impl.PulseType;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.SkillBonus;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.map.path.Pathfinder;
import core.game.world.map.zone.ZoneRestriction;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.plugin.Plugin;
import core.tools.Log;
import core.tools.RandomFunction;
import org.rs.consts.Sounds;

import java.util.ArrayList;
import java.util.List;

import static core.api.ContentAPIKt.*;

public abstract class Familiar extends NPC implements Plugin<Object> {

    protected static final Graphics SMALL_SUMMON_GRAPHICS = Graphics.create(1314);
    protected static final Graphics LARGE_SUMMON_GRAPHICS = Graphics.create(1315);
    protected static final Animation SPECIAL_ANIMATION = Animation.create(7660);
    protected static final Graphics SPECIAL_GRAPHICS = Graphics.create(1316);

    protected Player owner;
    protected int ticks;
    protected int maximumTicks;
    protected int specialPoints = 60;
    private final int pouchId;
    private final int specialCost;
    private final SummoningPouch pouch;
    private CombatSwingHandler combatHandler;
    protected boolean combatFamiliar;
    protected boolean charged;
    protected List<SkillBonus> boosts = new ArrayList<>(20);
    private final int attackStyle;
    private boolean firstCall = true;

    public Familiar(Player owner, int id, int ticks, int pouchId, int specialCost, final int attackStyle) {
        super(id, null);
        this.owner = owner;
        this.maximumTicks = ticks;
        this.ticks = ticks;
        this.pouchId = pouchId;
        this.pouch = SummoningPouch.get(pouchId);
        this.specialCost = specialCost;
        this.combatFamiliar = NPCDefinition.forId(getOriginalId() + 1).getName().equals(getName());
        this.attackStyle = attackStyle;
    }

    public Familiar(Player owner, int id, int ticks, int pouchId, int specialCost) {
        this(owner, id, ticks, pouchId, specialCost, WeaponInterface.STYLE_DEFENSIVE);
    }

    public void init(Location loc, boolean call) {
        location = loc;
        if (location == null) {
            location = owner.getLocation();
            setInvisible(true);
        }
        super.init();
        startFollowing();
        sendConfiguration();
        if (call) {
            call();
        }
        owner.getInterfaceManager().openInfoBars();
        if (getZoneMonitor().isInZone("Wilderness")) {
            transform();
        }
    }

    @Override
    public void init() {
        init(getSpawnLocation(), true);
    }

    @Override
    public void handleTickActions() {
        if (ticks-- % 50 == 0) {
            updateSpecialPoints(-15);
            owner.getSkills().updateLevel(Skills.SUMMONING, -1, 0);
            if (!getText().isEmpty()) {
                super.sendChat(getText());
            }
        }
        sendTimeRemaining();
        switch (ticks) {
            case 100:
                owner.getPacketDispatch().sendMessage("<col=ff0000>You have 1 minute before your familiar vanishes.");
                break;
            case 50:
                owner.getPacketDispatch().sendMessage("<col=ff0000>You have 30 seconds before your familiar vanishes.");
                break;
            case 0:
                if (isBurdenBeast() && !((BurdenBeast) this).getContainer().isEmpty()) {
                    owner.getPacketDispatch().sendMessage("<col=ff0000>Your familiar has dropped all the items it was holding.");
                } else {
                    owner.getPacketDispatch().sendMessage("<col=ff0000>Your familiar has vanished.");
                }
                dismiss();
                return;
        }
        CombatPulse combat = owner.getProperties().getCombatPulse();
        if (!isInvisible() && !getProperties().getCombatPulse().isAttacking() && (combat.isAttacking() || owner.inCombat())) {
            Entity victim = combat.getVictim();
            if (victim == null) {
                victim = owner.getAttribute("combat-attacker");
            }
            if (combat.getVictim() != this && victim != null && !victim.isInvisible() && getProperties().isMultiZone() && owner.getProperties().isMultiZone() && isCombatFamiliar() && !isBurdenBeast() && !isPeacefulFamiliar()) {
                getProperties().getCombatPulse().attack(victim);
            }
        }
        if ((!isInvisible() && owner.getLocation().getDistance(getLocation()) > 12) || (isInvisible() && ticks % 25 == 0)) {
            if (!call()) {
                setInvisible(true);
            }
        } else if (!getPulseManager().hasPulseRunning()) {
            startFollowing();
        }
        handleFamiliarTick();
    }

    @Override
    public boolean isAttackable(Entity entity, CombatStyle style, boolean message) {
        if (entity == owner) {
            if (message) {
                owner.getPacketDispatch().sendMessage("You can't just betray your own familiar like that!");
            }
            return false;
        }
        if (entity instanceof Player) {
            if (!owner.isAttackable(entity, style, message)) {
                return false;
            }
        }
        if (!getProperties().isMultiZone()) {
            if (entity instanceof Player && !((Player) entity).getProperties().isMultiZone()) {
                if (message) {
                    ((Player) entity).getPacketDispatch().sendMessage("You have to be in multicombat to attack a player's familiar.");
                }
                return false;
            }
            if (entity instanceof Player) {
                if (message) {
                    ((Player) entity).getPacketDispatch().sendMessage("This familiar is not in the a multicombat zone.");
                }
            }
            return false;
        }
        if (entity instanceof Player) {
            if (!((Player) entity).getSkullManager().isWilderness()) {
                if (message) {
                    ((Player) entity).getPacketDispatch().sendMessage("You have to be in the wilderness to attack a player's familiar.");
                }
                return false;
            }
            if (!owner.getSkullManager().isWilderness()) {
                if (message) {
                    ((Player) entity).getPacketDispatch().sendMessage("This familiar's owner is not in the wilderness.");
                }
                return false;
            }
        }
        return super.isAttackable(entity, style, message);
    }

    @Override
    public void onRegionInactivity() {
        call();
    }

    @Override
    public CombatSwingHandler getSwingHandler(boolean swing) {
        if (combatHandler != null) {
            return combatHandler;
        }
        return super.getSwingHandler(swing);
    }

    public abstract Familiar construct(Player owner, int id);

    protected abstract boolean specialMove(FamiliarSpecial special);

    protected void handleFamiliarTick() {
    }

    protected void configureFamiliar() {

    }

    protected String getText() {
        return "";
    }

    public void transform() {
        if (isCombatFamiliar()) {
            transform(getOriginalId() + 1);
        }
    }

    public void refreshTimer() {
        ticks = maximumTicks;
    }

    private void sendTimeRemaining() {
        int minutes = ticks / 100;
        int centiminutes = ticks % 100;
        setVarbit(owner, 4534, minutes);
        setVarbit(owner, 4290, centiminutes > 49 ? 1 : 0);
    }

    public boolean executeSpecialMove(FamiliarSpecial special) {
        if (special.getNode() == this) {
            return false;
        }
        if (specialCost > specialPoints) {
            owner.getPacketDispatch().sendMessage("Your familiar does not have enough special move points left.");
            return false;
        }
        SummoningScroll scroll = SummoningScroll.forPouch(pouchId);
        if (scroll == null) {
            owner.getPacketDispatch().sendMessage("Invalid scroll for pouch " + pouchId + " - report!");
            return false;
        }
        if (!owner.getInventory().contains(scroll.getItemId(), 1)) {
            owner.getPacketDispatch().sendMessage("You do not have enough scrolls left to do this special move.");
            return false;
        }
        if (owner.getLocation().getDistance(getLocation()) > 15) {
            owner.getPacketDispatch().sendMessage("Your familiar is too far away to use that scroll, or it cannot see you.");
            return false;
        }
        if (specialMove(special)) {
            setAttribute("special-delay", GameWorld.getTicks() + 3);
            owner.getInventory().remove(new Item(scroll.getItemId()));
            playAudio(owner, Sounds.SPELL_4161);
            visualizeSpecialMove();
            updateSpecialPoints(specialCost);
            owner.getSkills().addExperience(Skills.SUMMONING, scroll.getExperience(), true);
        }
        return true;
    }

    public void visualizeSpecialMove() {
        owner.visualize(Animation.create(7660), Graphics.create(1316));
    }

    public void sendFamiliarHit(final Entity target, final int maxHit, final Graphics graphics) {
        final int ticks = 2 + (int) Math.floor(getLocation().getDistance(target.getLocation()) * 0.5);
        getProperties().getCombatPulse().setNextAttack(4);
        GameWorld.getPulser().submit(new Pulse(ticks, this, target) {
            @Override
            public boolean pulse() {
                BattleState state = new BattleState(Familiar.this, target);
                int hit = 0;
                if (getCombatStyle().getSwingHandler().isAccurateImpact(Familiar.this, target)) {
                    hit = RandomFunction.randomize(maxHit);
                }
                state.setEstimatedHit(hit);
                target.getImpactHandler().handleImpact(owner, hit, CombatStyle.MELEE, state);
                if (graphics != null) {
                    target.graphics(graphics);
                }
                return true;
            }
        });
    }

    public void projectile(final Entity target, final int projectileId) {
        Projectile.magic(this, target, projectileId, 40, 36, 51, 10).send();
    }

    public void sendFamiliarHit(final Entity target, final int maxHit) {
        sendFamiliarHit(target, maxHit, null);
    }

    public boolean canAttack(Entity target, boolean message) {
        if (!target.isAttackable(owner, owner.getProperties().getCombatPulse().getStyle(), true)) {
            return false;
        }
        if (target.getLocation().getDistance(getLocation()) > 8) {
            if (message) {
                owner.getPacketDispatch().sendMessage("That target is too far.");
            }
            return false;
        }
        if (target.getLocks().isInteractionLocked() || !target.isAttackable(this, CombatStyle.MAGIC, true)) {
            return false;
        }
        return isCombatFamiliar();
    }

    @Override
    public boolean canAttack(Entity target) {
        return canAttack(target, true);
    }

    public boolean canCombatSpecial(Entity target, boolean message) {
        if (!canAttack(target, message)) {
            return false;
        }
        if (!isOwnerAttackable()) {
            return false;
        }
        if (getAttribute("special-delay", 0) > GameWorld.getTicks()) {
            return false;
        }
        return true;
    }

    public boolean canCombatSpecial(Entity target) {
        return canCombatSpecial(target, true);
    }

    public boolean isOwnerAttackable() {
        if (!owner.getProperties().getCombatPulse().isAttacking() && !owner.inCombat() && !getProperties().getCombatPulse().isAttacking()) {
            owner.getPacketDispatch().sendMessage("Your familiar cannot fight whilst you are not in combat.");
            return false;
        }
        return true;
    }

    public CombatStyle getCombatStyle() {
        return CombatStyle.MAGIC;
    }

    public void adjustPlayerBattle(final BattleState state) {

    }

    public void startFollowing() {
        getPulseManager().run(new MovementPulse(this, owner, Pathfinder.DUMB) {
            @Override
            public boolean pulse() {
                return false;
            }
        }, PulseType.STANDARD);
        face(owner);
    }

    @Override
    public void finalizeDeath(Entity killer) {
        dismiss();
    }

    public boolean isCombatFamiliar() {
        return combatFamiliar;
    }

    public void sendConfiguration() {
        setVarp(owner, 448, getPouchId());
        setVarp(owner, 1174, getOriginalId());
        setVarp(owner, 1175, specialCost << 23);
        sendTimeRemaining();
        updateSpecialPoints(0);
    }

    public boolean call() {
        Location destination = getSpawnLocation();
        if (destination == null) {

            return false;
        }
        setInvisible(owner.getZoneMonitor().isRestricted(ZoneRestriction.FOLLOWERS) && !owner.getLocks().isLocked("enable_summoning"));
        if (isInvisible()) return true;
        getProperties().setTeleportLocation(destination);
        if (!(this instanceof Pet)) {
            if (firstCall) {

                playAudio(owner, Sounds.SUMMON_NPC_188);
                firstCall = false;
            } else {
                playAudio(owner, Sounds.SUMMON_NPC_188);
            }
            if (size() > 1) {
                graphics(LARGE_SUMMON_GRAPHICS);
            } else {
                graphics(SMALL_SUMMON_GRAPHICS);
            }
        }
        if (getProperties().getCombatPulse().isAttacking()) {
            startFollowing();
        } else {
            face(owner);
        }
        if (!isRenderable() && owner.isActive()) {

            getWalkingQueue().update();
            getUpdateMasks().prepare(this);
        }
        return true;
    }

    public Location getSpawnLocation() {
        return RegionManager.getSpawnLocation(owner, this);
    }

    public void dismiss() {
        clear();
        getPulseManager().clear();
        owner.getInterfaceManager().removeTabs(7);
        owner.getFamiliarManager().setFamiliar(null);
        setVarp(owner, 448, -1);
        setVarp(owner, 1176, 0);
        setVarp(owner, 1175, 182986);
        setVarp(owner, 1174, -1);
        owner.getAppearance().sync();
        owner.getInterfaceManager().setViewedTab(3);
    }

    public void updateSpecialPoints(int diff) {
        specialPoints -= diff;
        if (specialPoints > 60) {
            specialPoints = 60;
        }
        setVarp(owner, 1177, specialPoints);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        for (int id : getIds()) {
            if (FamiliarManager.getFamiliars().containsKey(id)) {
                log(this.getClass(), Log.ERR, "Familiar " + id + " was already registered!");
                return null;
            }
            FamiliarManager.getFamiliars().put(id, this);
            configureFamiliar();
        }
        return this;
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    public boolean isCharged() {
        if (charged) {
            owner.getPacketDispatch().sendMessage("Your familiar is already charging its attack!");
            return true;
        }
        return false;
    }

    public int getBoost(int skill) {
        SkillBonus bonus = null;
        for (SkillBonus b : boosts) {
            if (b.getSkillId() == skill) {
                bonus = b;
                break;
            }
        }
        if (bonus == null) {
            return 0;
        }
        return (int) bonus.getBonus();
    }

    public void charge() {
        setCharged(true);
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
    }

    public boolean isBurdenBeast() {
        return false;
    }

    public boolean isPeacefulFamiliar() {
        return pouch.getPeaceful();
    }

    public abstract int[] getIds();

    public int getPouchId() {
        return pouchId;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public CombatSwingHandler getCombatHandler() {
        return combatHandler;
    }

    public void setCombatHandler(CombatSwingHandler combatHandler) {
        this.combatHandler = combatHandler;
    }

    public Animation getViewAnimation() {
        return null;
    }

    public int getAttackStyle() {
        return attackStyle;
    }

    public int getTicks() {
        return ticks;
    }

    public int getSpecialPoints() {
        return specialPoints;
    }
}
