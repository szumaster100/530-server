package core.game.system.communication;

public final class Contact {

    private final String username;

    private int worldId;

    private ClanRank rank = ClanRank.ANY_FRIEND;

    public Contact(String username) {
        this.username = username;
    }

    public int getWorldId() {
        return worldId;
    }

    public void setWorldId(int worldId) {
        this.worldId = worldId;
    }

    public ClanRank getRank() {
        return rank;
    }

    public void setRank(ClanRank rank) {
        this.rank = rank;
    }

    public String getUsername() {
        return username;
    }

}