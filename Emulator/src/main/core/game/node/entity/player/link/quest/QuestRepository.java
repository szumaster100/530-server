package core.game.node.entity.player.link.quest;

import core.game.node.entity.player.Player;
import core.tools.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static core.api.ContentAPIKt.*;

public final class QuestRepository {

    private static final Map<String, Quest> QUESTS = new TreeMap<>();

    private final Map<Integer, Integer> quests = new HashMap<>();

    private final Player player;

    private int points;

    public QuestRepository(final Player player) {
        this.player = player;
        for (Quest quest : QUESTS.values()) {
            quests.put(quest.getIndex(), 0);
        }
    }

    public void parse(JSONObject questData) {
        points = Integer.parseInt(questData.get("points").toString());
        JSONArray questArray = (JSONArray) questData.get("questStages");
        questArray.forEach(quest -> {
            JSONObject q = (JSONObject) quest;
            quests.put(Integer.parseInt(q.get("questId").toString()), Integer.parseInt(q.get("questStage").toString()));
        });
        syncPoints();
    }

    public void syncronizeTab(Player player) {
        setVarp(player, 101, points);
        int[] config = null;
        for (Quest quest : QUESTS.values()) {
            config = quest.getConfig(player, getStage(quest));

            // {questVarpId, questVarbitId, valueToSet}
            if (config.length == 3) {
                // This is to set quests with VARPBIT, ignoring VARP value
                setVarbit(player, config[1], config[2]);
            } else {
                // This is the original VARP quests
                // {questVarpId, valueToSet}
                setVarp(player, config[0], config[1]);
            }

            quest.updateVarps(player);
        }
    }

    public void setStage(Quest quest, int stage) {
        int oldStage = getStage(quest);
        if (oldStage < stage) {
            quests.put(quest.getIndex(), stage);
        } else {
            log(this.getClass(), Log.WARN, String.format("Nonmonotonic QuestRepository.setStage call for player \"%s\", quest \"%s\", old stage %d, new stage %d", player.getName(), quest.getName(), oldStage, stage));
        }
    }

    public void setStageNonmonotonic(Quest quest, int stage) {
        quests.put(quest.getIndex(), stage);
    }

    public void incrementPoints(int value) {
        points += value;
    }

    public void dockPoints(int value) {
        points -= value;
    }

    public void syncPoints() {
        int points = 0;
        for (Quest quest : QUESTS.values()) {
            if (getStage(quest) >= 100) {
                points += quest.getQuestPoints();
            }
        }
        this.points = points;
    }

    public int getAvailablePoints() {
        int points = 0;
        for (Quest quest : QUESTS.values()) {
            points += quest.getQuestPoints();
        }
        return points;
    }

    public Quest forButtonId(int buttonId) {
        for (Quest q : QUESTS.values()) {
            if (q.getButtonId() == buttonId) {
                return q;
            }
        }
        return null;
    }

    public Quest forIndex(int index) {
        for (Quest q : QUESTS.values()) {
            if (q.getIndex() == index) {
                return q;
            }
        }
        return null;
    }

    public boolean hasCompletedAll() {
        return getPoints() >= getAvailablePoints();
    }

    public boolean isComplete(String name) {
        Quest quest = getQuest(name);
        if (quest == null) {
            log(this.getClass(), Log.ERR, "Error can't check if quest is complete for " + name);
            return false;
        }
        return quest.getStage(player) >= 100;
    }

    public boolean hasStarted(String name) {
        Quest quest = getQuest(name);
        if (quest == null) {
            log(this.getClass(), Log.ERR, "Error can't check if quest is complete for " + name);
            return false;
        }
        return quest.getStage(player) > 0;
    }

    public int getStage(String name) {
        var quest = QUESTS.get(name);
        if (quest == null) {
            return 0;
        }
        return getStage(quest);
    }

    public int getStage(Quest quest) {
        return quests.get(quest.getIndex());
    }

    public Quest getQuest(String name) {
        return QUESTS.get(name);
    }

    public int getPoints() {
        return points;
    }

    public Player getPlayer() {
        return player;
    }

    public static void register(Quest quest) {
        QUESTS.put(quest.getName(), quest);
    }

    public static Map<String, Quest> getQuests() {
        return QUESTS;
    }

    public Map<Integer, Integer> getQuestList() {
        return quests;
    }

}
