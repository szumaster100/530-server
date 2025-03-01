package core.game.node.entity.player.link.diary;

import content.global.skill.smithing.smelting.Bar;
import core.game.component.Component;
import core.game.container.impl.EquipmentContainer;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DiaryManager {

    private final Diary[] diarys = new Diary[]{
        new Diary(DiaryType.KARAMJA),
        new Diary(DiaryType.VARROCK),
        new Diary(DiaryType.LUMBRIDGE),
        new Diary(DiaryType.FALADOR),
        new Diary(DiaryType.FREMENNIK),
        new Diary(DiaryType.SEERS_VILLAGE)
    };

    private final Player player;

    public DiaryManager(Player player) {
        this.player = player;
    }

    public void parse(JSONArray data) {
        for (int i = 0; i < data.size(); i++) {
            JSONObject diary = (JSONObject) data.get(i);
            String name = (String) diary.keySet().toArray()[0];
            name = name.replace("_", "' ");
            for (int ii = 0; ii < diarys.length; ii++) {
                if (diarys[ii].getType().getName().equalsIgnoreCase(name)) {
                    diarys[ii].parse((JSONObject) diary.get(name.replace("' ", "_")));
                }
            }
        }
    }

    public void openTab() {
        player.getInterfaceManager().openTab(2, new Component(259));
        for (Diary diary : diarys) {
            diary.drawStatus(player);
        }
    }

    public void updateTask(Player player, DiaryType type, int level, int index, boolean complete) {
        getDiary(type).updateTask(player, level, index, complete);
    }

    public void finishTask(Player player, DiaryType type, int level, int index) {
        if (!player.isArtificial()) {
            getDiary(type).finishTask(player, level, index);
        }
    }

    public boolean hasCompletedTask(DiaryType type, int level, int index) {
        return getDiary(type).isComplete(level, index);
    }

    public void setStarted(DiaryType type, int level) {
        getDiary(type).setLevelStarted(level);
    }

    public void setCompleted(DiaryType type, int level, int index) {
        getDiary(type).setCompleted(level, index);
    }

    public Diary getDiary(DiaryType type) {
        if (type == null) {
            return null;
        }
        for (Diary diary : diarys) {
            if (diary.getType() == type) {
                return diary;
            }
        }
        return null;
    }

    public int getKaramjaGlove() {
        if (!hasGlove()) {
            return -1;
        }
        for (int i = 0; i < 3; i++) {
            if (player.getEquipment().containsItem(DiaryType.KARAMJA.getRewards()[i][0])) {
                return i;
            }
        }
        return -1;
    }

    public int getArmour() {
        if (!hasArmour()) {
            return -1;
        }
        for (int i = 0; i < 3; i++) {
            if (player.getEquipment().containsItem(DiaryType.VARROCK.getRewards()[i][0])) {
                return i;
            }
        }
        return -1;
    }

    public boolean checkMiningReward(int reward) {
        int level = player.getAchievementDiaryManager().getArmour();
        if (level == -1) {
            return false;
        }
        if (reward == 453) {
            return true;
        }
        return level == 0 && reward <= 442 || level == 1 && reward <= 447 || level == 2 && reward <= 449;
    }

    public boolean checkSmithReward(Bar type) {
        int level = player.getAchievementDiaryManager().getArmour();
        if (level == -1) {
            return false;
        }
        return level == 0 && type.ordinal() <= Bar.STEEL.ordinal() || level == 1 && type.ordinal() <= Bar.MITHRIL.ordinal() || level == 2 && type.ordinal() <= Bar.ADAMANT.ordinal();
    }

    public boolean hasGlove() {
        Item glove = player.getEquipment().get(EquipmentContainer.SLOT_HANDS);
        return glove != null && (glove.getId() == DiaryType.KARAMJA.getRewards()[0][0].getId() || glove.getId() == DiaryType.KARAMJA.getRewards()[1][0].getId() || glove.getId() == DiaryType.KARAMJA.getRewards()[2][0].getId());
    }

    public boolean hasArmour() {
        Item plate = player.getEquipment().get(EquipmentContainer.SLOT_CHEST);
        return plate != null && (plate.getId() == DiaryType.VARROCK.getRewards()[0][0].getId() || plate.getId() == DiaryType.VARROCK.getRewards()[1][0].getId() || plate.getId() == DiaryType.VARROCK.getRewards()[2][0].getId());
    }

    public boolean isComplete(DiaryType type) {
        return diarys[type.ordinal()].isComplete();
    }

    public Player getPlayer() {
        return player;
    }

    public Diary[] getDiarys() {
        return diarys;
    }

    public void resetRewards() {
        for (Diary diary : diarys) {
            for (Item[] axis : diary.getType().getRewards()) {
                for (Item item : axis) {
                    if (player.getInventory().containsItem(item)) {
                        player.getInventory().remove(item);
                    }
                    if (player.getBank().containsItem(item)) {
                        player.getBank().remove(item);
                    }
                    if (player.getEquipment().containsItem(item)) {
                        player.getEquipment().remove(item);
                    }
                }
            }
        }
    }
}
