package core.game.system.communication;

public enum ClanRank {
    ANYONE(-1, "Anyone"),
    ANY_FRIEND(0, "Any friends"),
    RECRUIT(1, "Recruit+"),
    CORPORAL(2, "Corporal+"),
    SERGEANT(3, "Sergeant+"),
    LIEUTENANT(4, "Lieutenant+"),
    CAPTAIN(5, "Captain+"),
    GENERAL(6, "General+"),
    ONLY_ME(7, "Only me"),
    NO_ONE(127, "No-one");

    private final int value;

    private final String info;

    private ClanRank(int value, String info) {
        this.value = value;
        this.info = info;
    }

    public int getValue() {
        return value;
    }

    public String getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "Rank=[" + name() + "], Info=" + "[" + getInfo() + "]";
    }
}