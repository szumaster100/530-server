package content.minigame.pestcontrol;

import org.rs.consts.Components;
import content.global.skill.herblore.herbs.Herbs;
import core.cache.def.impl.ItemDefinition;
import core.game.component.Component;
import core.game.component.ComponentDefinition;
import core.game.component.ComponentPlugin;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.GroundItemManager;
import core.game.node.item.Item;
import core.plugin.Initializable;
import core.plugin.Plugin;
import core.tools.RandomFunction;

import java.util.ArrayList;
import java.util.List;

import static core.api.ContentAPIKt.removeAttribute;
import static core.api.ContentAPIKt.setAttribute;

@Initializable
public final class PCRewardInterface extends ComponentPlugin {

    public static final String RED = "<col=FF0000>";

    public static final String GREEN = "<col=04B404>";

    public static final String WHITE = "<col=FFFFFF>";

    public static final int[] SKILL_HEADER = new int[]{10, 12, 11, 15, 13, 16, 14};

    public static final int[] SKILL_ARRAY = new int[]{Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGE, Skills.MAGIC, Skills.HITPOINTS, Skills.PRAYER};

    public static final int[] SKILL_POINTS = new int[]{1, 10, 100};

    public static final int[] CHARM_POINTS = new int[]{2, 28, 56};

    public static final int[] CHARM_AMOUNTS = new int[]{1, 14, 28};

    public static void open(final Player player) {
        removeAttribute(player, "pc-reward");
        sendString(player, "Points: " + player.getSavedData().activityData.getPestPoints(), 105);
        clear(player);
        player.getInterfaceManager().open(new Component(267));
    }

    private static void sendSkills(final Player player) {
        for (int skill : SKILL_ARRAY) {
            sendString(player, getSkillCondition(player, skill), getSkillChild(skill));
        }
    }

    private static void sendString(final Player player, String string, int child) {
        player.getPacketDispatch().sendString(string, 267, child);
    }

    public static void clear(final Player player) {
        sendSkills(player);
        for (Reward reward : Reward.values()) {
            if (reward.isSkillReward()) {
                continue;
            }
            if (reward.charm) {
                sendString(player, (player.getSavedData().activityData.getPestPoints() < 2 ? RED + "You need 2 points." : GREEN + reward.getName()), reward.getHeader());
                continue;
            }
            sendString(player, (player.getSavedData().activityData.getPestPoints() < reward.getPoints() ? player.getSavedData().activityData.getPestPoints() < 1 ? RED + ("You need at least 1 point.") : RED + ("You need " + reward.getPoints() + " points.") : (GREEN + reward.getName())), reward.getHeader());
        }
    }

    public static double calculateExperience(final Player player, final int skillId) {
        int level = player.getSkills().getStaticLevel(skillId);

        double divideBy = 30;
        if (skillId == Skills.PRAYER) {

            divideBy = 67;
        } else if (skillId == Skills.MAGIC || skillId == Skills.RANGE) {

            divideBy = 29;
        }
        return (int) ((level * level) / divideBy) * (player.getSkills().experienceMultiplier / 2);
    }

    public static String getSkillCondition(final Player player, final int skillId) {
        if (player.getSkills().getStaticLevel(skillId) < 25) {
            return RED + "Must reach level 25 first.";
        }
        return GREEN + getSkillXp(player, skillId);
    }

    public static String getSkillXp(final Player player, int skillId) {
        return Skills.SKILL_NAME[skillId] + " - " + (int) calculateExperience(player, skillId) + " xp";
    }

    public static int getSkillChild(final int skill) {
        return SKILL_HEADER[skill];
    }

    public static Reward getReward(final Player player) {
        return player.getAttribute("pc-reward", null);
    }

    public static boolean hasReward(final Player player) {
        return getReward(player) != null;
    }

    public static int getCachedOption(final Player player) {
        return player.getAttribute("pc-reward:option", 0);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ComponentDefinition.forId(Components.PEST_REWARDS_267).setPlugin(this);
        return this;
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
        switch (button) {
            case 96:
                confirm(player);
                return true;
            default:
                if (button >= 34 && button <= 86) {
                    if (player.getSavedData().activityData.getPestPoints() == 0) {
                        player.getPacketDispatch().sendMessage("You don't have enough points.");
                        return true;
                    }
                    select(player, button);
                }
                break;
        }
        return true;
    }

    public void select(final Player player, final int button) {
        final Reward reward = Reward.forButton(button);
        final int option = reward.getOption(button);
        if (!reward.checkRequirements(player, option)) {
            return;
        }
        cacheReward(player, reward, option);
    }

    public boolean deselect(final Player player) {
        return deselect(player, getReward(player));
    }

    public boolean deselect(final Player player, final Reward reward) {
        if (reward == null) {
            return false;
        }
        clear(player);
        reward.deselect(player, getCachedOption(player));
        return true;
    }

    public void cacheReward(final Player player, final Reward reward, final int option) {
        deselect(player);
        reward.select(player, option);
        sendString(player, "<col=F7DF22>Confirm:", 106);
        sendString(player, reward.getName(), 104);
        setAttribute(player, "pc-reward", reward);
        setAttribute(player, "pc-reward:option", option);
    }

    public void confirm(final Player player) {
        if (!hasReward(player)) {
            player.getPacketDispatch().sendMessage("Please choose a reward.");
            return;
        }
        final Reward reward = getReward(player);
        if ((reward.charm || !reward.isSkillReward()) && player.getInventory().freeSlots() == 0) {
            player.getPacketDispatch().sendMessage("You don't have enough inventory space.");
            return;
        }
        final int option = getCachedOption(player);
        final int points = reward.getPoints(option);
        String message;
        player.getInterfaceManager().close();
        if (player.getSavedData().activityData.getPestPoints() >= points) {
            player.getSavedData().activityData.decreasePestPoints(points);
            if (reward.isSkillReward()) {
                final double experience = ((int) calculateExperience(player, reward.getSkill()) * points);
                player.getSkills().addExperience(reward.getSkill(), experience);
                message = "The Void Knight has granted you " + (int) (experience * player.getSkills().experienceMultiplier) + " " + reward.getName() + ".";
            } else {
                if (!reward.checkItemRequirement(player, option)) {
                    return;
                }
                if (!reward.charm) {
                    if (reward.getReward().length > 1) {
                        Item[] pack = reward.constructPack();
                        for (Item i : pack) {
                            if (!player.getInventory().add(i)) {
                                GroundItemManager.create(i, player);
                            }
                        }
                    } else {
                        if (!player.getInventory().add(reward.getReward()[0])) {
                            GroundItemManager.create(reward.getReward()[0], player);
                        }
                    }
                } else {
                    Item charm = new Item(reward.getReward()[0].getId());
                    int amt = CHARM_AMOUNTS[option - 1];
                    for (int i = 0; i < amt; i++) {
                        if (!player.getInventory().add(charm)) {
                            GroundItemManager.create(charm, player);
                        }
                    }
                }
                message = "The Void Knight has given you a " + reward.getName() + ".";
            }
            player.getDialogueInterpreter().sendDialogue(message, "<col=571D07>Remaining Void Knight Commendation Points: " + player.getSavedData().activityData.getPestPoints());
        }
    }

    public enum Reward {

        ATTACK(Skills.ATTACK, new int[]{10, 34, 49, 56}),

        STRENGTH(Skills.STRENGTH, new int[]{11, 35, 50, 57}),

        DEFENCE(Skills.DEFENCE, new int[]{12, 36, 51, 58}),

        RANGE(Skills.RANGE, new int[]{13, 37, 52, 59}),

        MAGIC(Skills.MAGIC, new int[]{14, 38, 53, 60}),

        HITPOINTS(Skills.HITPOINTS, new int[]{15, 39, 54, 61}),

        PRAYER(Skills.PRAYER, new int[]{16, 40, 55, 62}),

        HERB_PACK("Herb Pack", 30, new Item[]{Herbs.HARRALANDER.herb, Herbs.RANARR.herb, Herbs.TOADFLAX.herb, Herbs.IRIT.herb, Herbs.AVANTOE.herb, Herbs.KWUARM.herb, Herbs.GUAM.herb, Herbs.MARRENTILL.herb}, new int[]{32, 45}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                if (player.getSkills().getLevel(Skills.HERBLORE) < 25) {
                    player.getPacketDispatch().sendMessage("You need level 25 herblore to purchase this pack.");
                    return false;
                }
                return true;
            }
        },

        MINERAL_PACK("Mineral Pack", 15, new Item[]{new Item(453), new Item(440)}, new int[]{47, 46}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                if (player.getSkills().getLevel(Skills.MINING) < 25) {
                    player.getPacketDispatch().sendMessage("You need level 25 mining to purchase this pack.");
                    return false;
                }
                return true;
            }
        },

        SEED_PACK("Seed Pack", 15, new Item[]{new Item(5320), new Item(5322), new Item(5100)}, new int[]{33, 48}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                if (player.getSkills().getLevel(Skills.FARMING) < 25) {
                    player.getPacketDispatch().sendMessage("You need level 25 farming to purchase this pack.");
                    return false;
                }
                return true;
            }
        },

        VOID_MACE("Void Knight Mace", 250, new Item[]{new Item(8841)}, new int[]{28, 41}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_TOP("Void Knight Top", 250, new Item[]{new Item(8839)}, new int[]{29, 42}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_ROBES("Void Knight Robes", 250, new Item[]{new Item(8840)}, new int[]{30, 43}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_GLOVES("Void Knight Gloves", 150, new Item[]{new Item(8842)}, new int[]{31, 44}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_MAGE_HELM("Void Knight Mage Helm", 200, new Item[]{new Item(11663)}, new int[]{63, 67}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_RANGER_HELM("Void Knight Ranger Helm", 200, new Item[]{new Item(11664)}, new int[]{64, 68}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_MELEE_HELM("Void Knight Melee Helm", 200, new Item[]{new Item(11665)}, new int[]{65, 69}) {
            @Override
            public boolean checkItemRequirement(final Player player, final int option) {
                return hasVoidSkills(player);
            }
        },

        VOID_KNIGHT_SEAL("Void Knight Seal", 10, new Item[]{new Item(11666)}, new int[]{66, 70}),

        SPINNER_CHARM("Spinner Charm", new Item(12166), 71, 75, 76, 77),

        RAVAGER_CHARM("Ravager Charm", new Item(12164), 72, 81, 82, 83),

        TORCHER_CHARM("Torcher Charm", new Item(12167), 74, 78, 79, 80),

        SHIFTER_CHAR("Shifter Charm", new Item(12165), 73, 84, 85, 86);

        private static final int MAX_BUILD = 18;
        private static final int MIN_BUILD = 13;
        private final int[] VOID_SKILLS = new int[]{Skills.HITPOINTS, Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, Skills.RANGE, Skills.MAGIC, Skills.PRAYER};
        private final int[] childs;
        private int skill;
        private Item[] reward;
        private String name;
        private int points;
        private boolean charm = false;

        Reward(final int[] childs) {
            this.childs = childs;
        }

        Reward(int skill, final int[] childs) {
            this.skill = skill;
            this.childs = childs;
        }

        Reward(String name, int points, final Item[] reward, final int[] childs) {
            this.name = name;
            this.points = points;
            this.reward = reward;
            this.childs = childs;
        }

        Reward(final String name, final Item charm, final int... childs) {
            this.name = name;
            this.charm = true;
            this.reward = new Item[]{charm};
            this.childs = childs;
        }

        public static Reward forButton(final int button) {
            for (Reward reward : values()) {
                for (int i : reward.getChilds()) {
                    if (i == button) {
                        return reward;
                    }
                }
            }
            return null;
        }

        public boolean checkRequirements(final Player player, final int option) {
            if (player.getSavedData().activityData.getPestPoints() < getPoints(option)) {
                player.getPacketDispatch().sendMessage("You don't have enough points.");
                return false;
            }
            return isSkillReward() ? checkSkillRequirement(player, option) : checkItemRequirement(player, option);
        }

        public void select(final Player player, final int option) {
            if (isSkillReward()) {
                skillSelect(player, option);
            } else {
                itemSelect(player, option);
            }
        }

        public void deselect(final Player player, final int option) {
            if (isSkillReward()) {
                skillDeselect(player, option);
            } else {
                itemDeselect(player, option);
            }
        }

        public final void skillSelect(final Player player, final int option) {
            sendString(player, WHITE + getSkillXp(player, skill), getHeader());
            sendString(player, WHITE + getOptionString(option), getChilds()[option]);
        }

        public final void itemSelect(final Player player, final int option) {
            sendString(player, WHITE + getName(), getHeader());
            if (charm) {
                sendString(player, WHITE + getOptionString(option), getChilds()[option]);
            }
        }

        public final void skillDeselect(final Player player, final int option) {
            sendString(player, "<col=784F1C>" + getOptionString(option), getChilds()[option]);
        }

        public final void itemDeselect(final Player player, final int option) {
            if (charm) {
                sendString(player, "<col=784F1C>" + getOptionString(option), getChilds()[option]);
            }
        }

        public String getOptionString(int option) {
            if (charm) {
                return (option == 1 ? "(2 Pts)" : option == 2 ? "(28 Pts)" : "(56 Pts)");
            }
            return (option == 1 ? "(1 Pt)" : option == 2 ? "(10 Pts)" : "(100 Pts)");
        }

        public int getOption(int button) {
            int index = 0;
            for (int i : getChilds()) {
                if (i == button) {
                    return index;
                }
                index++;
            }
            return -1;
        }

        public int getPoints(final int option) {
            if (charm) {
                return CHARM_POINTS[option - 1];
            }
            return isSkillReward() ? SKILL_POINTS[option - 1] : getPoints();
        }

        public boolean hasVoidSkills(final Player player) {
            for (int skill : VOID_SKILLS) {
                if (player.getSkills().getLevel(skill) < (skill != Skills.PRAYER ? 42 : 22)) {
                    player.getPacketDispatch().sendMessage("You need level 42 in hitpoints, attack, defence, strength, ranged, magic, and");
                    player.getPacketDispatch().sendMessage("22 prayer to purchase the " + name.toLowerCase().replace("_", " ").replace("void knight", "").trim() + ".");
                    return false;
                }
            }
            return true;
        }

        public String getName() {
            return isSkillReward() ? Skills.SKILL_NAME[skill] + " xp" : name;
        }

        public boolean isCharm() {
            return charm;
        }

        public boolean checkSkillRequirement(final Player player, final int option) {
            if (player.getSkills().getLevel(skill) < 25) {
                player.getPacketDispatch().sendMessage("The Void Knights will not offer training in skills which you have a level of");
                player.getPacketDispatch().sendMessage("less than 25.");
                return false;
            }
            return true;
        }

        public boolean checkItemRequirement(final Player player, final int option) {

            return true;
        }

        public Item[] constructPack() {
            final int build = this == SEED_PACK || this == HERB_PACK ? RandomFunction.random(MIN_BUILD, MAX_BUILD) : RandomFunction.random(38, 43);
            int left = build;
            List<Item> pack = new ArrayList<>(20);
            int amt = 0;
            for (Item i : getReward()) {
                amt = this == SEED_PACK || this == HERB_PACK ? RandomFunction.random(1, 5) : RandomFunction.random(16, 25);
                if (amt > left) {
                    amt = left;
                }
                if (amt < 1) {
                    continue;
                }
                pack.add(new Item(this != SEED_PACK ? ItemDefinition.forId(i.getId()).getNoteId() : i.getId(), amt));
                left -= amt;
            }
            return pack.toArray(new Item[]{});
        }

        public boolean isSkillReward() {
            return getChilds().length > 2 && !charm;
        }

        public int getSkill() {
            return skill;
        }

        public int getHeader() {
            return childs[0];
        }

        public int[] getChilds() {
            return childs;
        }

        public int getPoints() {
            return points;
        }

        public Item[] getReward() {
            return reward;
        }
    }

}
