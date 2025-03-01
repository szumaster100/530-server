package core.game.node.entity.player.link;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public final class ActivityData {

    private int pestPoints;

    private int warriorGuildTokens;

    private int bountyHunterRate;

    private int bountyRogueRate;

    private boolean[] barrowBrothers = new boolean[6];

    private int barrowKills;

    private int barrowTunnelIndex;

    private int kolodionStage;

    private int[] godCasts = new int[3];

    private int kolodionBoss;

    private boolean elnockSupplies;

    private long lastBorkBattle;

    private byte borkKills;

    private boolean lostCannon;

    private boolean startedMta;

    private int[] pizazzPoints = new int[4];

    private boolean bonesToPeaches;

    private int solvedMazes;

    private int fogRating;

    private boolean hardcoreDeath;

    boolean topGrabbed;

    public ActivityData() {

    }

    public void parse(JSONObject data) {
        pestPoints = Integer.parseInt(data.get("pestPoints").toString());
        warriorGuildTokens = Integer.parseInt(data.get("warriorGuildTokens").toString());
        bountyHunterRate = Integer.parseInt(data.get("bountyHunterRate").toString());
        bountyRogueRate = Integer.parseInt(data.get("bountyRogueRate").toString());
        barrowKills = Integer.parseInt(data.get("barrowKills").toString());
        JSONArray bb = (JSONArray) data.get("barrowBrothers");
        for (int i = 0; i < bb.size(); i++) {
            barrowBrothers[i] = (boolean) bb.get(i);
        }
        barrowTunnelIndex = Integer.parseInt(data.get("barrowTunnelIndex").toString());
        kolodionStage = Integer.parseInt(data.get("kolodionStage").toString());
        JSONArray gc = (JSONArray) data.get("godCasts");
        for (int i = 0; i < gc.size(); i++) {
            godCasts[i] = Integer.parseInt(gc.get(i).toString());
        }
        kolodionBoss = Integer.parseInt(data.get("kolodionBoss").toString());
        elnockSupplies = (boolean) data.get("elnockSupplies");
        lastBorkBattle = Long.parseLong(data.get("lastBorkBattle").toString());
        startedMta = (boolean) data.get("startedMta");
        lostCannon = (boolean) data.get("lostCannon");
        JSONArray pp = (JSONArray) data.get("pizazzPoints");
        for (int i = 0; i < pp.size(); i++) {
            pizazzPoints[i] = Integer.parseInt(pp.get(i).toString());
        }
        bonesToPeaches = (boolean) data.get("bonesToPeaches");
        solvedMazes = Integer.parseInt(data.get("solvedMazes").toString());
        fogRating = Integer.parseInt(data.get("fogRating").toString());
        borkKills = Byte.parseByte(data.get("borkKills").toString());
        hardcoreDeath = (boolean) data.get("hardcoreDeath");
        topGrabbed = (boolean) data.get("topGrabbed");
    }

    public boolean isElnockSupplies() {
        return elnockSupplies;
    }

    public void setElnockSupplies(boolean elnockSupplies) {
        this.elnockSupplies = elnockSupplies;
    }

    public void increasePestPoints(int pestPoints) {
        if (pestPoints + this.pestPoints > 500) {
            this.pestPoints = 500;
        } else {
            this.pestPoints += pestPoints;
        }
    }

    public void decreasePestPoints(int pestPoints) {
        this.pestPoints -= pestPoints;
    }

    public int getPestPoints() {
        return pestPoints;
    }

    public void setPestPoints(int pestPoints) {
        this.pestPoints = pestPoints;
    }

    public int getWarriorGuildTokens() {
        return warriorGuildTokens;
    }

    public void setWarriorGuildTokens(int warriorGuildTokens) {
        this.warriorGuildTokens = warriorGuildTokens;
    }

    public void updateWarriorTokens(int amount) {
        this.warriorGuildTokens += amount;
    }

    public int getBountyHunterRate() {
        return bountyHunterRate;
    }

    public void updateBountyHunterRate(int rate) {
        this.bountyHunterRate += rate;
    }

    public int getBountyRogueRate() {
        return bountyRogueRate;
    }

    public void updateBountyRogueRate(int rate) {
        this.bountyRogueRate += rate;
    }

    public boolean[] getBarrowBrothers() {
        return barrowBrothers;
    }

    public void setBarrowBrothers(boolean[] barrowBrothers) {
        this.barrowBrothers = barrowBrothers;
    }

    public int getBarrowKills() {
        return barrowKills;
    }

    public void setBarrowKills(int barrowKills) {
        if (barrowKills > 10000) {
            barrowKills = 10000;
        }
        this.barrowKills = barrowKills;
    }

    public int getBarrowTunnelIndex() {
        return barrowTunnelIndex;
    }

    public void setBarrowTunnelIndex(int barrowTunnelIndex) {
        this.barrowTunnelIndex = barrowTunnelIndex;
    }

    public void setKolodionStage(int stage) {
        this.kolodionStage = stage;
    }

    public boolean hasStartedKolodion() {
        return kolodionStage == 1;
    }

    public boolean hasKilledKolodion() {
        return kolodionStage >= 2;
    }

    public boolean hasReceivedKolodionReward() {
        return kolodionStage == 3;
    }

    public int[] getGodCasts() {
        return godCasts;
    }

    public int getKolodionBoss() {
        return kolodionBoss;
    }

    public void setKolodionBoss(int kolodionBoss) {
        this.kolodionBoss = kolodionBoss;
    }

    public long getLastBorkBattle() {
        return lastBorkBattle;
    }

    public void setLastBorkBattle(long lastBorkBattle) {
        this.lastBorkBattle = lastBorkBattle;
    }

    public boolean hasKilledBork() {
        return lastBorkBattle > 0;
    }

    public boolean isLostCannon() {
        return lostCannon;
    }

    public void setLostCannon(boolean lostCannon) {
        this.lostCannon = lostCannon;
    }

    public boolean isStartedMta() {
        return startedMta;
    }

    public void setStartedMta(boolean startedMta) {
        this.startedMta = startedMta;
    }

    public void incrementPizazz(int index) {
        pizazzPoints[index] += 1;
    }

    public void incrementPizazz(int index, int amount) {
        pizazzPoints[index] += amount;
    }

    public void decrementPizazz(int index, int amount) {
        pizazzPoints[index] -= amount;
    }

    public void decrementPizazz(int index) {
        pizazzPoints[index] -= 1;
    }

    public int getPizazzPoints(int index) {
        return pizazzPoints[index];
    }

    public int[] getPizazzPoints() {
        return pizazzPoints;
    }

    public void setPizazzPoints(int[] pizazzPoints) {
        this.pizazzPoints = pizazzPoints;
    }

    public boolean isBonesToPeaches() {
        return bonesToPeaches;
    }

    public void setBonesToPeaches(boolean bonesToPeaches) {
        this.bonesToPeaches = bonesToPeaches;
    }

    public int getSolvedMazes() {
        return solvedMazes;
    }

    public void setSolvedMazes(int solvedMazes) {
        this.solvedMazes = solvedMazes;
    }

    public int getFogRating() {
        return fogRating;
    }

    public void setFogRating(int fogRating) {
        this.fogRating = fogRating;
    }

    public byte getBorkKills() {
        return borkKills;
    }

    public void setBorkKills(byte borkKills) {
        this.borkKills = borkKills;
    }

    public boolean getHardcoreDeath() {
        return hardcoreDeath;
    }

    public void setHardcoreDeath(boolean hardcoreDeath) {
        this.hardcoreDeath = hardcoreDeath;
    }

    public void setTopGrabbed(boolean topGrabbed) {
        this.topGrabbed = topGrabbed;
    }

    public boolean isTopGrabbed() {
        return topGrabbed;
    }

    public int getKolodionStage() {
        return kolodionStage;
    }
}