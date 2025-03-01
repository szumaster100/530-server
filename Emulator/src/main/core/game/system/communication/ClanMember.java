package core.game.system.communication;

public final class ClanMember {

    private ClanRank rank = ClanRank.ANY_FRIEND;

    public ClanMember(ClanRank rank) {
        this.rank = rank;
    }

    public ClanRank getRank() {
        return rank;
    }

    public void setRank(ClanRank rank) {
        this.rank = rank;
    }

}