package core.game.node.entity.player.info;

import content.data.GameAttributes;
import core.Util;
import core.auth.UserAccountInfo;
import core.game.node.entity.player.Player;
import core.game.system.communication.CommunicationInfo;
import core.game.world.GameWorld;
import core.net.IoSession;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class PlayerDetails {

    public UserAccountInfo accountInfo = UserAccountInfo.createDefault();

    private final CommunicationInfo communicationInfo = new CommunicationInfo();

    public String getPlayerTime(Player player) {
        Timestamp joinTimestamp = (Timestamp) player.getGameAttributes().getAttributes().get(GameAttributes.JOIN_DATE);

        if (joinTimestamp == null) {
            setJoinDate(player);
            return null;
        }

        long currentTime = System.currentTimeMillis();
        long difference = currentTime - joinTimestamp.getTime();

        long days = TimeUnit.MILLISECONDS.toDays(difference);
        long hours = TimeUnit.MILLISECONDS.toHours(difference) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;

        String d = Util.getTimeUnitString(days, "day", "days");
        String h = Util.getTimeUnitString(hours, "hour", "hours");
        String m = Util.getTimeUnitString(minutes, "minute", "minutes");

        return String.format("%s, %s, %s", d, h, m);
    }

    public void setJoinDate(Player player) {
        long joinTime = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(joinTime);

        player.getGameAttributes().getAttributes().put(GameAttributes.JOIN_DATE, timestamp);

        player.getDetails().accountInfo.setJoinDate(timestamp);
    }

    public int getCredits() {
        return accountInfo.getCredits();
    }

    public void setCredits(int amount) {
        accountInfo.setCredits(amount);
    }

    private final UIDInfo info = new UIDInfo();

    private IoSession session;

    public boolean saveParsed = false;

    public PlayerDetails(String username) {
        accountInfo.setUsername(username);
    }

    public boolean isBanned() {
        return accountInfo.getBanEndTime() > System.currentTimeMillis();
    }

    public boolean isPermMute() {
        return TimeUnit.MILLISECONDS.toDays(accountInfo.getMuteEndTime() - System.currentTimeMillis()) > 1000;
    }

    public boolean isMuted() {
        return accountInfo.getMuteEndTime() > System.currentTimeMillis();
    }

    public Rights getRights() {
        return Rights.values()[accountInfo.getRights()];
    }

    public void setRights(Rights rights) {
        this.accountInfo.setRights(rights.ordinal());
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }

    public void setPassword(final String password) {
        this.accountInfo.setPassword(password);
    }

    public String getUsername() {
        return this.accountInfo.getUsername();
    }

    public int getUid() {
        return accountInfo.getUid();
    }

    public String getPassword() {
        return this.accountInfo.getPassword();
    }

    public String getMacAddress() {
        return info.getMac();
    }

    public String getCompName() {
        return info.getCompName();
    }

    public String getIpAddress() {
        if (session == null) {
            return info.getIp();
        }
        return session.getAddress();
    }

    public String getSerial() {
        return info.getSerial();
    }

    public UIDInfo getInfo() {
        return info;
    }

    public CommunicationInfo getCommunication() {
        return communicationInfo;
    }

    public long getLastLogin() {
        return this.accountInfo.getLastLogin();
    }

    public void setLastLogin(long lastLogin) {
        this.accountInfo.setLastLogin(lastLogin);
    }

    public long getTimePlayed() {
        return this.accountInfo.getTimePlayed();
    }

    public void setTimePlayed(long timePlayed) {
        this.accountInfo.setTimePlayed(timePlayed);
    }

    public void setMuteTime(long muteTime) {
        this.accountInfo.setMuteEndTime(muteTime);
    }

    public long getMuteTime() {
        return this.accountInfo.getMuteEndTime();
    }

    public long getBanTime() {
        return this.accountInfo.getBanEndTime();
    }

    public void setBanTime(long banTime) {
        this.accountInfo.setBanEndTime(banTime);
    }

    public void save() {
        if (!saveParsed) return;
        if (isBanned()) return;
        try {
            accountInfo.setContacts(communicationInfo.getContactString());
            accountInfo.setBlocked(communicationInfo.getBlockedString());
            accountInfo.setClanName(communicationInfo.getClanName());
            accountInfo.setClanReqs(communicationInfo.getClanReqString());
            accountInfo.setCurrentClan(communicationInfo.getCurrentClan());
            GameWorld.getAccountStorage().update(accountInfo);
        } catch (IllegalStateException ignored) {
        }
    }

    public static PlayerDetails getDetails(@NotNull String username) {
        return new PlayerDetails(username);
    }
}
