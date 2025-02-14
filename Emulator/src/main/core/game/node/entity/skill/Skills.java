package core.game.node.entity.skill;

import content.data.GameAttributes;
import content.global.handlers.item.equipment.gloves.BrawlingGloves;
import content.global.handlers.item.equipment.gloves.BrawlingGlovesManager;
import core.game.event.DynamicSkillLevelChangeEvent;
import core.game.event.XPGainEvent;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.ImpactHandler;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.info.PlayerMonitor;
import core.game.node.entity.player.link.request.assist.AssistSession;
import core.game.node.item.Item;
import core.game.world.GameWorld;
import core.net.packet.PacketRepository;
import core.net.packet.context.SkillContext;
import core.net.packet.out.SkillLevel;
import core.plugin.type.ExperiencePlugins;
import kotlin.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.rs.consts.Items;
import org.rs.consts.Sounds;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import static core.api.ContentAPIKt.getWorldTicks;
import static core.api.ContentAPIKt.playAudio;
import static java.lang.Math.floor;
import static java.lang.Math.max;

public final class Skills {

    public double experienceMultiplier = 1.0;

    public static final String[] SKILL_NAME = {"Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter", "Construction", "Summoning"};

    public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2, HITPOINTS = 3, RANGE = 4, PRAYER = 5, MAGIC = 6, COOKING = 7, WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11, CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15, AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19, RUNECRAFTING = 20, HUNTER = 21, CONSTRUCTION = 22, SUMMONING = 23;

    public static final int NUM_SKILLS = 24;

    private final Entity entity;

    private final double[] experience;

    private double[] lastUpdateXp = null;

    private int lastUpdate = GameWorld.getTicks();

    private final int[] staticLevels;

    private final int[] dynamicLevels;

    private double prayerPoints = 1.;

    private int lifepoints = 10;

    private int lifepointsIncrease = 0;

    private double experienceGained = 0;

    private boolean lifepointsUpdate;

    private int combatMilestone;

    private int skillMilestone;

    public int lastTrainedSkill = -1;

    public int lastXpGain = 0;

    public Skills(Entity entity) {
        this.entity = entity;
        this.experience = new double[24];
        this.staticLevels = new int[24];
        this.dynamicLevels = new int[24];
        for (int i = 0; i < 24; i++) {
            this.staticLevels[i] = 1;
            this.dynamicLevels[i] = 1;
        }
        this.experience[HITPOINTS] = 1154;
        this.dynamicLevels[HITPOINTS] = 10;
        this.staticLevels[HITPOINTS] = 10;
        entity.getProperties().setCombatLevel(3);
    }

    public boolean isCombat(int skill) {
        if ((skill >= ATTACK && skill <= MAGIC) || (skill == SUMMONING)) {
            return true;
        }
        return false;
    }

    public void configure() {
        updateCombatLevel();
    }

    public void pulse() {
        if (lifepoints < 1) {
            return;
        }
    }

    public void copy(Skills skills) {
        for (int i = 0; i < 24; i++) {
            this.staticLevels[i] = skills.staticLevels[i];
            this.dynamicLevels[i] = skills.dynamicLevels[i];
            this.experience[i] = skills.experience[i];
        }
        prayerPoints = skills.prayerPoints;
        lifepoints = skills.lifepoints;
        lifepointsIncrease = skills.lifepointsIncrease;
        experienceGained = skills.experienceGained;
    }

    public void addExperience(int slot, double experience, boolean playerMod) {
        if (lastUpdateXp == null)
            lastUpdateXp = this.experience.clone();
        double mod = getExperienceMod(slot, experience, playerMod, true);
        final Player player = entity instanceof Player ? ((Player) entity) : null;
        final AssistSession assist = entity.getExtension(AssistSession.class);
        if (assist != null && assist.translateExperience(player, slot, experience, mod)) {
            return;
        }
        boolean already200m = this.experience[slot] == 200000000;
        double experienceAdd = (experience * mod);
        //check if a player has brawling gloves and, if equipped, modify xp
        BrawlingGlovesManager bgManager = BrawlingGlovesManager.getInstance(player);
        if (!bgManager.GloveCharges.isEmpty()) {
            Item gloves = BrawlingGloves.forSkill(slot) == null ? null : new Item(BrawlingGloves.forSkill(slot).getId());
            if (gloves == null && (slot == Skills.STRENGTH || slot == Skills.DEFENCE)) {
                gloves = new Item(BrawlingGloves.forSkill(Skills.ATTACK).getId());
            }
            if (gloves != null && player.getEquipment().containsItem(gloves)) {
                experienceAdd += experienceAdd * bgManager.getExperienceBonus();
                bgManager.updateCharges(gloves.getId(), 1);
            }
        }
        //Check for Flame Gloves and ring of Fire
        if (player.getEquipment().containsItem(new Item(Items.FLAME_GLOVES_13660)) || player.getEquipment().containsItem(new Item(Items.RING_OF_FIRE_13659))) {
            if (slot == Skills.FIREMAKING) {
                int count = 0;
                if (player.getEquipment().containsItem(new Item(Items.FLAME_GLOVES_13660))) count += 1;
                if (player.getEquipment().containsItem(new Item(Items.RING_OF_FIRE_13659))) count += 1;
                if (count == 2) experienceAdd += (0.05 * experienceAdd);
                else experienceAdd += (0.02 * experienceAdd);
            }
        }
        this.experience[slot] += experienceAdd;
        if (this.experience[slot] >= 200000000) {
            if (!already200m && !player.isArtificial()) {
                //Repository.sendNews(entity.asPlayer().getUsername() + " has just reached 200m experience in " + SKILL_NAME[slot] + "!");
            }
            this.experience[slot] = 200000000;
        }
        if (entity instanceof Player && this.experience[slot] > 175) {
            if (!player.getAttribute(GameAttributes.TUTORIAL_COMPLETE, false) && slot != HITPOINTS) {
                this.experience[slot] = 175;
            }
        }
        experienceGained += experienceAdd;
        ExperiencePlugins.run(player, slot, experienceAdd);
        int newLevel = getStaticLevelByExperience(slot);
        if (newLevel > staticLevels[slot]) {
            int amount = newLevel - staticLevels[slot];
            if (dynamicLevels[slot] < newLevel) {
                dynamicLevels[slot] += amount;
            }
            if (slot == HITPOINTS) {
                lifepoints += amount;
            }
            staticLevels[slot] = newLevel;

            if (entity instanceof Player) {
                player.updateAppearance();
                LevelUp.levelUp(player, slot, amount);
                updateCombatLevel();
            }
        }
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, slot));
            entity.dispatch(new XPGainEvent(slot, experienceAdd));
        }
        if (GameWorld.getTicks() - lastUpdate >= 200) {
            ArrayList<Pair<Integer, Double>> diffs = new ArrayList<>();
            for (int i = 0; i < this.experience.length; i++) {
                double diff = this.experience[i] - lastUpdateXp[i];
                if (diff != 0.0) {
                    diffs.add(new Pair<>(i, diff));
                }
            }
            PlayerMonitor.logXpGains(player, diffs);
            lastUpdateXp = this.experience.clone();
            lastUpdate = GameWorld.getTicks();
        }
        lastTrainedSkill = slot;
        lastXpGain = getWorldTicks();
    }

    private double getExperienceMod(int slot, double experience, boolean playerMod, boolean multiplyer) {
        //Keywords for people ctrl + Fing the project
        //xprate xp rate xp multiplier skilling rate
        return experienceMultiplier;

    }

    public void addExperience(final int slot, double experience) {
        addExperience(slot, experience, false);
    }

    public int getHighestCombatSkillId() {
        int id = 0;
        int last = 0;
        for (int i = 0; i < 5; i++) {
            if (staticLevels[i] > last) {
                last = staticLevels[i];
                id = i;
            }
        }
        return id;
    }

    public void restore() {
        for (int i = 0; i < 24; i++) {
            int staticLevel = getStaticLevel(i);
            setLevel(i, staticLevel);
        }
        if (entity instanceof Player) {
            playAudio(entity.asPlayer(), Sounds.PRAYER_RECHARGE_2674);
        }
        rechargePrayerPoints();
    }

    public void parse(ByteBuffer buffer) {
        for (int i = 0; i < 24; i++) {
            experience[i] = ((double) buffer.getInt() / 10D);
            dynamicLevels[i] = buffer.get() & 0xFF;
            if (i == HITPOINTS) {
                lifepoints = dynamicLevels[i];
            } else if (i == PRAYER) {
                prayerPoints = dynamicLevels[i];
            }
            staticLevels[i] = buffer.get() & 0xFF;
        }
        experienceGained = buffer.getInt();
    }

    public void parse(JSONArray skillData) {
        for (int i = 0; i < skillData.size(); i++) {
            JSONObject skill = (JSONObject) skillData.get(i);
            int id = Integer.parseInt(skill.get("id").toString());
            dynamicLevels[id] = Integer.parseInt(skill.get("dynamic").toString());
            if (id == HITPOINTS) {
                lifepoints = dynamicLevels[i];
            } else if (id == PRAYER) {
                prayerPoints = dynamicLevels[i];
            }
            staticLevels[id] = Integer.parseInt(skill.get("static").toString());
            experience[id] = Double.parseDouble(skill.get("experience").toString());
        }
    }

    public void correct(double divisor) {
        for (int i = 0; i < staticLevels.length; i++) {
            experience[i] /= divisor;
            staticLevels[i] = getStaticLevelByExperience(i);
            dynamicLevels[i] = staticLevels[i];
            if (i == PRAYER) {
                setPrayerPoints(staticLevels[i]);
            }
            if (i == HITPOINTS) {
                setLifepoints(staticLevels[i]);
            }
        }
        experienceMultiplier = 1.0;
        updateCombatLevel();
    }

    public void parseExpRate(ByteBuffer buffer) {
        experienceMultiplier = buffer.getDouble();
        if (GameWorld.getSettings().getDefault_xp_rate() != experienceMultiplier) {
            experienceMultiplier = GameWorld.getSettings().getDefault_xp_rate();
        }
    }

    public void save(ByteBuffer buffer) {
        for (int i = 0; i < 24; i++) {
            buffer.putInt((int) (experience[i] * 10));
            if (i == HITPOINTS) {
                buffer.put((byte) lifepoints);
            } else if (i == PRAYER) {
                buffer.put((byte) Math.ceil(prayerPoints));
            } else {
                buffer.put((byte) dynamicLevels[i]);
            }
            buffer.put((byte) staticLevels[i]);
        }
        buffer.putInt((int) experienceGained);
    }

    public void saveExpRate(ByteBuffer buffer) {
        buffer.putDouble(experienceMultiplier);
    }

    public void refresh() {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player) entity;
        for (int i = 0; i < 24; i++) {
            PacketRepository.send(SkillLevel.class, new SkillContext(player, i));
        }
        LevelUp.sendFlashingIcons(player, -1);
    }

    public int getStaticLevelByExperience(int slot) {
        double exp = experience[slot];

        int points = 0;
        int output = 0;
        for (byte lvl = 1; lvl < 100; lvl++) {
            points += floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) floor(points / 4);
            if ((output - 1) >= exp) {
                return lvl;
            }
        }
        return 99;
    }

    public int levelFromXP(double exp) {

        int points = 0;
        int output = 0;
        for (byte lvl = 1; lvl < 100; lvl++) {
            points += floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) floor(points / 4);
            if ((output - 1) >= exp) {
                return lvl;
            }
        }
        return 99;
    }

    public int getExperienceByLevel(int level) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) {
                return output;
            }
            output = (int) floor(points / 4);
        }
        return 0;
    }

    @SuppressWarnings("deprecation")
    public boolean updateCombatLevel() {
        boolean update = false;
        int level = calculateCombatLevel();
        update = level != entity.getProperties().getCombatLevel();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            int summon = staticLevels[SUMMONING] / 8;
            if (summon != player.getFamiliarManager().getSummoningCombatLevel()) {
                player.getFamiliarManager().setSummoningCombatLevel(summon);
                update = true;
            }
        }
        entity.getProperties().setCombatLevel(level);
        return update;
    }

    private int calculateCombatLevel() {
        if (entity instanceof NPC) {
            return ((NPC) entity).getDefinition().getCombatLevel();
        }

        double base = 0.25 * (staticLevels[DEFENCE] + staticLevels[HITPOINTS] + floor(0.5 * staticLevels[PRAYER]));
        double meleeBase = 0.325 * (staticLevels[ATTACK] + staticLevels[STRENGTH]);
        double rangeBase = 0.325 * (floor(staticLevels[RANGE] / 2.0) * 3.0);
        double magicBase = 0.325 * (floor(staticLevels[MAGIC] / 2.0) * 3.0);

        return (int) (base + max(meleeBase, max(rangeBase, magicBase)));
    }

    public Entity getEntity() {
        return entity;
    }

    public double getExperience(int slot) {
        return experience[slot];
    }

    public int getStaticLevel(int slot) {
        return staticLevels[slot];
    }

    public void setExperienceGained(double experienceGained) {
        this.experienceGained = experienceGained;
    }

    public double getExperienceGained() {
        return experienceGained;
    }

    public void setLevel(int slot, int level) {
        if (slot == HITPOINTS) {
            lifepoints = level;
        } else if (slot == PRAYER) {
            prayerPoints = level;
        }

        int previousLevel = dynamicLevels[slot];
        dynamicLevels[slot] = level;

        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, slot));
            entity.dispatch(new DynamicSkillLevelChangeEvent(slot, previousLevel, level));
        }
    }

    public int getLevel(int slot, boolean discardAssist) {
        if (!discardAssist) {
            if (entity instanceof Player) {
                final Player p = (Player) entity;
                final AssistSession assist = p.getExtension(AssistSession.class);
                if (assist != null && assist.getPlayer() != p) {
                    Player assister = assist.getPlayer();
                    int index = assist.getSkillIndex(slot);
                    if (index != -1 && !assist.isRestricted()) {

                        // assist.getSkills()[index] + ", " + SKILL_NAME[slot]);
                        if (assist.getSkills()[index]) {
                            int assistLevel = assister.getSkills().getLevel(slot);
                            int playerLevel = dynamicLevels[slot];
                            if (assistLevel > playerLevel) {
                                return assistLevel;
                            }
                        }
                    }
                }
            }
        }
        return dynamicLevels[slot];
    }

    public int getLevel(int slot) {
        return getLevel(slot, false);
    }

    public void setLifepoints(int lifepoints) {
        this.lifepoints = lifepoints;
        if (this.lifepoints < 0) {
            this.lifepoints = 0;
        }
        lifepointsUpdate = true;
    }

    public int getLifepoints() {
        return lifepoints;
    }

    public int getMaximumLifepoints() {
        return staticLevels[HITPOINTS] + lifepointsIncrease;
    }

    public void setLifepointsIncrease(int amount) {
        this.lifepointsIncrease = amount;
    }

    public int heal(int health) {
        lifepoints += health;
        int left = 0;
        if (lifepoints > getMaximumLifepoints()) {
            left = lifepoints - getMaximumLifepoints();
            lifepoints = getMaximumLifepoints();
        }
        lifepointsUpdate = true;
        return left;
    }

    public void healNoRestrictions(int amount) {
        lifepoints += amount;
        lifepointsUpdate = true;
    }

    public int hit(int damage) {
        lifepoints -= damage;
        int left = 0;
        if (lifepoints < 0) {
            left = -lifepoints;
            lifepoints = 0;
        }
        lifepointsUpdate = true;
        return left;
    }

    public double getPrayerPoints() {
        return prayerPoints;
    }

    public void rechargePrayerPoints() {
        prayerPoints = staticLevels[PRAYER];
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, PRAYER));
        }
    }

    public void decrementPrayerPoints(double amount) {
        prayerPoints -= amount;
        if (prayerPoints < 0) {
            prayerPoints = 0;
        }
        // if (prayerPoints > staticLevels[PRAYER]) {
        // prayerPoints = staticLevels[PRAYER];
        // }
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, PRAYER));
        }
    }

    public void incrementPrayerPoints(double amount) {
        prayerPoints += amount;
        if (prayerPoints < 0) {
            prayerPoints = 0;
        }
        if (prayerPoints > staticLevels[PRAYER]) {
            prayerPoints = staticLevels[PRAYER];
        }
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, PRAYER));
        }
    }

    public void setPrayerPoints(double amount) {
        prayerPoints = amount;
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, PRAYER));
        }
    }

    public int updateLevel(int skill, int amount, int maximum) {
        if (amount > 0 && dynamicLevels[skill] > maximum) {
            return -amount;
        }
        int left = (dynamicLevels[skill] + amount) - maximum;
        int level = dynamicLevels[skill] += amount;
        if (level < 0) {
            dynamicLevels[skill] = 0;
        } else if (amount < 0 && level < maximum) {
            dynamicLevels[skill] = maximum;
        } else if (amount > 0 && level > maximum) {
            dynamicLevels[skill] = maximum;
        }
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, skill));
        }
        return left;
    }

    public int updateLevel(int skill, int amount) {
        return updateLevel(skill, amount, amount >= 0 ? getStaticLevel(skill) + amount : getStaticLevel(skill) - amount);
    }

    public void drainLevel(int skill, double drainPercentage, double maximumDrainPercentage) {
        int drain = (int) (dynamicLevels[skill] * drainPercentage);
        int minimum = (int) (staticLevels[skill] * (1.0 - maximumDrainPercentage));
        updateLevel(skill, -drain, minimum);
    }

    public void setStaticLevel(int skill, int level) {
        experience[skill] = getExperienceByLevel(staticLevels[skill] = dynamicLevels[skill] = level);
        if (entity instanceof Player) {
            PacketRepository.send(SkillLevel.class, new SkillContext((Player) entity, skill));
        }
    }

    public int getMasteredSkills() {
        int count = 0;
        for (int i = 0; i < 23; i++) {
            if (getStaticLevel(i) >= 99) {
                count++;
            }
        }
        return count;
    }

    public static int getSkillByName(final String name) {
        for (int i = 0; i < SKILL_NAME.length; i++) {
            if (SKILL_NAME[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public int getTotalLevel() {
        int level = 0;
        for (int i = 0; i < 24; i++) {
            level += getStaticLevel(i);
        }
        return level;
    }

    public int getTotalXp() {
        int total = 0;
        for (int skill = 0; skill < Skills.SKILL_NAME.length; skill++) {
            total += this.getExperience(skill);
        }
        return total;
    }

    public boolean isLifepointsUpdate() {
        return lifepointsUpdate;
    }

    public void setLifepointsUpdate(boolean lifepointsUpdate) {
        this.lifepointsUpdate = lifepointsUpdate;
    }

    public int[] getStaticLevels() {
        return staticLevels;
    }

    public boolean hasLevel(int skillId, int i) {
        return getStaticLevel(skillId) >= i;
    }

    public int getCombatMilestone() {
        return combatMilestone;
    }

    public void setCombatMilestone(int combatMilestone) {
        this.combatMilestone = combatMilestone;
    }

    public int getSkillMilestone() {
        return skillMilestone;
    }

    public void setSkillMilestone(int skillMilestone) {
        this.skillMilestone = skillMilestone;
    }

    public int[] getDynamicLevels() {
        return dynamicLevels;
    }
}
