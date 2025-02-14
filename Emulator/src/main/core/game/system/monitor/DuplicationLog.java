package core.game.system.monitor;

import core.ServerConfig;

public final class DuplicationLog extends MessageLog {

    public static long LOGGING_DURATION = 5 * 24 * 60 * 60_000;

    public static final int DUPE_TALK = 0x1;

    public static final int NW_INCREASE = 0x8;

    private int flag;

    private long lastIncreaseFlag;

    public DuplicationLog() {
        super(-1, false);
    }

    @Override
    public void write(String playerName) {
        String priority = "low";
        switch (getProbability()) {
            case 3:
                priority = "high";
                break;
            case 2:
                priority = "mid";
        }
        super.write(ServerConfig.LOGS_PATH + "duplicationflags/" + priority + "prior/" + playerName + ".log");
    }

    public int getProbability() {
        int probability = 0;
        if ((flag & NW_INCREASE) != 0) {
            probability += 2;
        }
        if ((flag & DUPE_TALK) != 0) {
            probability++;
        }
        return probability;
    }

    public boolean isLoggingFlagged() {
        return (flag & NW_INCREASE) != 0 && (System.currentTimeMillis() - lastIncreaseFlag) < LOGGING_DURATION;
    }

    public void flag(int flag) {
        this.flag |= flag;
        if (flag == NW_INCREASE) {
            lastIncreaseFlag = System.currentTimeMillis();
        }
    }

    public int getFlag() {
        return flag;
    }

    public long getLastIncreaseFlag() {
        return lastIncreaseFlag;
    }

    public void setLastIncreaseFlag(long lastIncreaseFlag) {
        this.lastIncreaseFlag = lastIncreaseFlag;
    }

}