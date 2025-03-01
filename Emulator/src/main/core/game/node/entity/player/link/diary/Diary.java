package core.game.node.entity.player.link.diary;

import core.cache.def.impl.NPCDefinition;
import core.game.component.Component;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Diary {

    public static final int DIARY_COMPONENT = 275;

    public static final ArrayList<Integer> completedLevels = new ArrayList<Integer>();

    private static final String RED = "<col=8A0808>";

    private static final String BLUE = "<col=08088A>";

    private static final String YELLOW = "<col=F7FE2E>";

    private static final String GREEN = "<col=3ADF00>";

    private final DiaryType type;

    private final boolean[] levelStarted = new boolean[3];

    private final boolean[] levelRewarded = new boolean[3];

    private final boolean[][] taskCompleted;

    public Diary(DiaryType type) {
        this.type = type;
        this.taskCompleted = new boolean[type.getAchievements().length][25];
    }

    public void open(Player player) {
        clear(player);
        sendString(player, "<red>Achievement Diary - " + type.getName(), 2);
        int child = 12;

        sendString(player, (isComplete() ? GREEN : isStarted() ? YELLOW : "<red>") + type.getName() + " Area Tasks", child++);
        //child++;

        if (!type.getInfo().isEmpty() && !this.isStarted()) {
            sendString(player, type.getInfo(), child++);
            child += type.getInfo().split("<br><br>").length;
        }
        child++;

        boolean complete;
        String line;
        for (int level = 0; level < type.getAchievements().length; level++) {
            sendString(player, getStatus(level) + getLevel(level) + "", child++);
            child++;
            for (int i = 0; i < type.getAchievements(level).length; i++) {
                complete = isComplete(level, i);
                line = type.getAchievements(level)[i];
                if (line.contains("<br><br>")) {
                    String[] lines = line.split("<br><br>");
                    for (String l : lines) {
                        sendString(player, complete ? "<str><str>" + l : l, child++);
                    }
                } else {
                    sendString(player, complete ? "<str><str>" + line : line, child++);
                }
                sendString(player, "*", child++);
            }
            child++;
        }
        //	sendString(player, builder.toString(), 11);
        //Changes the size of the scroll bar
        //player.getPacketDispatch().sendRunScript(1207, "i", new Object[] { 330 });
        //sendString(player, builder.toString(), 11);
        if (!player.getInterfaceManager().isOpened()) {
            player.getInterfaceManager().open(new Component(DIARY_COMPONENT));
        }
    }

    private void clear(Player player) {
        for (int i = 0; i < 311; i++) {
            player.getPacketDispatch().sendString("", DIARY_COMPONENT, i);
        }
    }

    public void parse(JSONObject data) {
        JSONArray startedArray = (JSONArray) data.get("startedLevels");
        for (int i = 0; i < startedArray.size(); i++) {
            levelStarted[i] = (boolean) startedArray.get(i);
        }
        JSONArray completedArray = (JSONArray) data.get("completedLevels");
        for (int i = 0; i < completedArray.size(); i++) {
            JSONArray level = (JSONArray) completedArray.get(i);
            boolean completed = true;
            for (int j = 0; j < level.size(); j++) {
                taskCompleted[i][j] = (boolean) level.get(j);
                if (!taskCompleted[i][j]) {
                    completed = !completed;
                }
                completedLevels.add(i);
            }
        }
        JSONArray rewardedArray = (JSONArray) data.get("rewardedLevels");
        for (int i = 0; i < rewardedArray.size(); i++) {
            levelRewarded[i] = (boolean) rewardedArray.get(i);
        }
    }

    public void drawStatus(Player player) {
        if (isStarted()) {
            player.getPacketDispatch().sendString((isComplete() ? GREEN : YELLOW) + type.getName(), 259, type.getChild());
            for (int i = 0; i < 3; i++) {
                player.getPacketDispatch().sendString((isComplete(i) ? GREEN : isStarted(i) ? YELLOW : "<col=FF0000>") + getLevel(i), 259, type.getChild() + (i + 1));
            }
        }
    }

    public void updateTask(Player player, int level, int index, boolean complete) {
        if (!levelStarted[level]) {
            levelStarted[level] = true;
        }
        if (!complete) {
            player.sendMessage("Well done! A " + type.getName() + " task has been updated.");
        } else {
            taskCompleted[level][index] = true;
            int tempLevel = this.type == DiaryType.LUMBRIDGE ? level - 1 : level;
            player.sendMessages("Well done! You have completed "
                + (tempLevel == -1 ? "a beginner" : tempLevel == 0 ? "an easy" : tempLevel == 1 ? "a medium" : "a hard")
                + " task in the " + type.getName() + " area. Your", "Achievement Diary has been updated.");
        }
        if (isComplete(level)) {
            player.sendMessages("Congratulations! You have completed all of the " + getLevel(level).toLowerCase()
                + " tasks in the " + type.getName() + " area.", "Speak to "
                + NPCDefinition.forId(type.getNpc(level)).getName() + " to claim your reward.");
        }
        drawStatus(player);
    }

    public void finishTask(Player player, int level, int index) {
        if (!this.isComplete(level, index)) {
            this.updateTask(player, level, index, true);
            boolean complete = true;
            for (int i = 0; i < taskCompleted[level].length; i++) {
                if (!taskCompleted[level][i]) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                completedLevels.add(level);
            } else if (completedLevels.contains(level)) completedLevels.remove(level);
        }
    }

    public void resetTask(Player player, int level, int index) {
        taskCompleted[level][index] = false;
        if (!isStarted(level)) {
            this.levelStarted[level] = false;
        }
        if (!isComplete(level)) {
            this.levelRewarded[level] = false;
        }
        drawStatus(player);
    }

    public boolean checkComplete(DiaryLevel level) {
        if (type != DiaryType.LUMBRIDGE && level == DiaryLevel.BEGINNER) {
            return false;
        }

        if (level == DiaryLevel.BEGINNER) {
            return completedLevels.contains(level.ordinal());
        }

        return completedLevels.contains(level.ordinal() - 1);
    }

    private void sendString(Player player, String string, int child) {
        player.getPacketDispatch().sendString(string.replace("<blue>", BLUE).replace("<red>", RED), DIARY_COMPONENT, child);
    }

    public void setLevelStarted(int level) {
        this.levelStarted[level] = true;
    }

    public void setCompleted(int level, int index) {
        this.taskCompleted[level][index] = true;
    }

    public boolean isStarted(int level) {
        return this.levelStarted[level];
    }

    public boolean isStarted() {
        for (int j = 0; j < type.getLevelNames().length; j++) {
            if (isStarted(j)) {
                return true;
            }
        }
        return false;
    }

    public boolean isComplete(int level, int index) {
        return taskCompleted[level][index];
    }

    public boolean isComplete(int level) {
        for (int i = 0; i < type.getAchievements(level).length; i++) {
            if (!taskCompleted[level][i]) {
                return false;
            }
        }
        return true;
    }

    public boolean isComplete(int level, boolean cumulative) {
        if (isComplete(level)) {
            return !cumulative || level <= 0 || isComplete(level - 1, true);
        } else {
            return false;
        }
    }

    public boolean isComplete() {
        for (int i = 0; i < taskCompleted.length; i++) {
            for (int x = 0; x < type.getAchievements(i).length; x++) {
                if (!taskCompleted[i][x]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getLevel() {
        return isComplete(2) ? 2 : isComplete(1) ? 1 : isComplete(0) ? 0 : -1;
    }

    public int getReward() {
        return isLevelRewarded(2) ? 2 : isLevelRewarded(1) ? 1 : isLevelRewarded(0) ? 0 : -1;
    }

    public String getLevel(int level) {
        return type.getLevelNames()[level];
    }

    public String getStatus(int level) {
        return !isStarted(level) ? RED : isComplete(level) ? GREEN : YELLOW;
    }

    public void setLevelRewarded(int level) {
        this.levelRewarded[level] = true;
    }

    public boolean isLevelRewarded(int level) {
        return levelRewarded[level];
    }

    public boolean[][] getTaskCompleted() {
        return taskCompleted;
    }

    public DiaryType getType() {
        return type;
    }

    public boolean[] getLevelStarted() {
        return levelStarted;
    }

    public boolean[] getLevelRewarded() {
        return levelRewarded;
    }

    public static boolean removeRewardsFor(Player player, DiaryType type, int level) {
        Item[] rewards = type.getRewards(level);
        //lamps are always the 2nd reward for a level, don't remove lamps
        boolean hasRemoved =
            player.getInventory().remove(rewards[0])
                || player.getBank().remove(rewards[0])
                || player.getEquipment().remove(rewards[0]);

        if (hasRemoved) {
            player.debug("Removed previous reward");
        }

        return hasRemoved;
    }

    public static boolean addRewardsFor(Player player, DiaryType type, int level) {
        Item[] rewards = type.getRewards(level);

        int freeSlots = player.getInventory().freeSlots();
        if (freeSlots < rewards.length)
            return false;

        boolean allRewarded = true;
        for (Item reward : rewards) {
            allRewarded &= player.getInventory().add(reward);
        }

        if (!allRewarded) {
            Arrays.stream(rewards).forEach((item) -> {
                boolean _ignored = player.getInventory().remove(item);
            });
        }

        return allRewarded;
    }

    public static boolean flagRewarded(Player player, DiaryType type, int level) {
        if (level > 0) {
            removeRewardsFor(player, type, level - 1);
        }
        if (addRewardsFor(player, type, level))
            player.getAchievementDiaryManager().getDiary(type).setLevelRewarded(level);
        else {
            player.sendMessage("You do not have enough space in your inventory to claim these rewards.");
            return false;
        }

        return true;
    }

    public static boolean canReplaceReward(Player player, DiaryType type, int level) {
        Item reward = type.getRewards(level)[0];
        boolean claimed = hasCompletedLevel(player, type, level)
            && hasClaimedLevelRewards(player, type, level)
            && !player.hasItem(reward);
        return level == 2 ? claimed : claimed && !hasClaimedLevelRewards(player, type, level + 1);
    }

    public static boolean grantReplacement(Player player, DiaryType type, int level) {
        Item reward = type.getRewards(level)[0]; //Can only replace non-lamp reward
        return canReplaceReward(player, type, level) && player.getInventory().add(reward);
    }

    public static boolean hasCompletedLevel(Player player, DiaryType type, int level) {
        if (level > type.getLevelNames().length - 1)
            return false;
        return player.getAchievementDiaryManager().getDiary(type).isComplete(level, true);
    }

    public static boolean hasClaimedLevelRewards(Player player, DiaryType type, int level) {
        return player.getAchievementDiaryManager().getDiary(type).isLevelRewarded(level);
    }

    public static boolean canClaimLevelRewards(Player player, DiaryType type, int level) {
        if (level == 2)
            // Cannot be a higher level to claim
            return hasCompletedLevel(player, type, level) && !hasClaimedLevelRewards(player, type, level);
        else
            return !hasClaimedLevelRewards(player, type, level + 1) && hasCompletedLevel(player, type, level) && !hasClaimedLevelRewards(player, type, level);
    }

    public static Item[] getRewards(DiaryType type, int level) {
        return type.getRewards(level);
    }
}
