package core.game.node.entity.player.link;

import core.game.node.item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class QuestData {

    private final boolean[] cooksAssistant = new boolean[4];

    private final boolean[] demonSlayer = new boolean[2];

    private final boolean[] draynorLever = new boolean[6];

    private final boolean[] dragonSlayer = new boolean[9];

    private final Item[] desertTreasure = new Item[7];

    private int dragonSlayerPlanks;

    private boolean gardenerAttack;

    private boolean talkedDrezel;

    private int witchsExperimentStage;

    private boolean witchsExperimentKilled;

    public QuestData() {
        Arrays.fill(draynorLever, true);
        populateDesertTreasureNode();
    }

    public void parse(JSONObject data) {
        JSONArray dl = (JSONArray) data.get("draynorLever");
        for (int i = 0; i < dl.size(); i++) {
            draynorLever[i] = (boolean) dl.get(i);
        }
        JSONArray drs = (JSONArray) data.get("dragonSlayer");
        for (int i = 0; i < drs.size(); i++) {
            dragonSlayer[i] = (boolean) drs.get(i);
        }
        dragonSlayerPlanks = Integer.parseInt(data.get("dragonSlayerPlanks").toString());
        JSONArray des = (JSONArray) data.get("demonSlayer");
        for (int i = 0; i < des.size(); i++) {
            demonSlayer[i] = (boolean) des.get(i);
        }
        JSONArray ca = (JSONArray) data.get("cooksAssistant");
        for (int i = 0; i < ca.size(); i++) {
            cooksAssistant[i] = (boolean) ca.get(i);
        }
        gardenerAttack = (boolean) data.get("gardenerAttack");
        talkedDrezel = (boolean) data.get("talkedDrezel");
        JSONArray dtn = (JSONArray) data.get("desertTreasureNode");
        for (int i = 0; i < dtn.size(); i++) {
            JSONObject item = (JSONObject) dtn.get(i);
            desertTreasure[i] = new Item(Integer.parseInt(item.get("id").toString()), Integer.parseInt(item.get("amount").toString()));
        }
        witchsExperimentKilled = (boolean) data.get("witchsExperimentKilled");
        witchsExperimentStage = Integer.parseInt(data.get("witchsExperimentStage").toString());
    }

    private final void saveDesertTreasureNode(ByteBuffer buffer) {
        buffer.put((byte) 8);
        for (int i = 0; i < desertTreasure.length; i++) {
            Item item = desertTreasure[i];
            buffer.putShort((short) item.getId());
            buffer.put((byte) item.getAmount());
        }
    }

    public boolean[] getDraynorLevers() {
        return draynorLever;
    }

    public boolean[] getDragonSlayerItems() {
        return dragonSlayer;
    }

    public boolean getDragonSlayerItem(String name) {
        return name == "lobster" ? dragonSlayer[0] : name == "wizard" ? dragonSlayer[3] : name == "silk" ? dragonSlayer[2] : name == "bowl" ? dragonSlayer[1] : dragonSlayer[0];
    }

    public boolean getDragonSlayerAttribute(String name) {
        return name == "ship" ? dragonSlayer[4] : name == "memorized" ? dragonSlayer[5] : name == "repaired" ? dragonSlayer[6] : name == "ned" ? dragonSlayer[7] : name == "poured" ? dragonSlayer[8] : dragonSlayer[8];
    }

    public void setDragonSlayerAttribute(String name, boolean value) {
        dragonSlayer[(name == "ship" ? 4 : name == "memorized" ? 5 : name == "repaired" ? 6 : name == "ned" ? 7 : name == "poured" ? 8 : 8)] = value;
    }

    public boolean getCookAssist(String name) {
        return name == "milk" ? cooksAssistant[0] : name == "egg" ? cooksAssistant[1] : name == "flour" ? cooksAssistant[2] : name == "gave" ? cooksAssistant[3] : cooksAssistant[3];
    }

    public void setCooksAssistant(String name, boolean value) {
        cooksAssistant[(name == "milk" ? 0 : name == "egg" ? 1 : name == "flour" ? 2 : name == "gave" ? 3 : 3)] = value;
    }

    public int getDragonSlayerPlanks() {
        return dragonSlayerPlanks;
    }

    public void setDragonSlayerPlanks(int i) {
        this.dragonSlayerPlanks = i;
    }

    public boolean[] getDemonSlayer() {
        return demonSlayer;
    }

    public boolean[] getCooksAssistant() {
        return cooksAssistant;
    }

    public boolean isGardenerAttack() {
        return gardenerAttack;
    }

    public void setGardenerAttack(boolean gardenerAttack) {
        this.gardenerAttack = gardenerAttack;
    }

    public boolean isTalkedDrezel() {
        return talkedDrezel;
    }

    public void setTalkedDrezel(boolean talkedDrezel) {
        this.talkedDrezel = talkedDrezel;
    }

    private final void populateDesertTreasureNode() {
        desertTreasure[0] = new Item(1513, 12);
        desertTreasure[1] = new Item(592, 10);
        desertTreasure[2] = new Item(1775, 6);
        desertTreasure[3] = new Item(2353, 6);
        desertTreasure[4] = new Item(526, 2);
        desertTreasure[5] = new Item(973, 2);
        desertTreasure[6] = new Item(565, 1);
    }

    public Item getDesertTreasureItem(int index) {
        if (index < 0 || index > desertTreasure.length) {
            throw new IndexOutOfBoundsException("Index out of bounds, index can only span from 0 - 6.");
        }
        return desertTreasure[index];
    }

    public void setDesertTreasureItem(int index, Item item) {
        if (index < 0 || index > desertTreasure.length) {
            throw new IndexOutOfBoundsException("Index out of bounds, index can only span from 0 - 6.");
        }
        desertTreasure[index] = item;
    }

    public int getWitchsExperimentStage() {
        return witchsExperimentStage;
    }

    public void setWitchsExperimentStage(int witchsExperimentStage) {
        this.witchsExperimentStage = witchsExperimentStage;
    }

    public boolean isWitchsExperimentKilled() {
        return witchsExperimentKilled;
    }

    public void setWitchsExperimentKilled(boolean witchsExperimentKilled) {
        this.witchsExperimentKilled = witchsExperimentKilled;
    }

    public boolean[] getDraynorLever() {
        return draynorLever;
    }

    public boolean[] getDragonSlayer() {
        return dragonSlayer;
    }

    public Item[] getDesertTreasure() {
        return desertTreasure;
    }
}