package core.game.node.entity.player.info;

import core.game.node.entity.player.Player;
import core.tools.StringUtils;

public class UIDInfo {

    private String ip;

    private String compName;

    private String mac;

    private String serial;

    public UIDInfo() {

    }

    public UIDInfo(String ip, String compName, String mac, String serial) {
        this.ip = ip;
        this.compName = compName;
        this.mac = mac.replace(":", "-");
        this.serial = serial;
    }

    public void translate(UIDInfo other) {
        ip = other.ip;
        compName = other.compName;
        mac = other.mac.replace(":", "-");
        serial = other.serial;
    }

    public String toString(Player player, Player target) {
        boolean admin = player.isAdmin();
        String format = toString();
        if (!admin) {// formats for non-admins
            String[] tokens = format.split("serial=");
            format = format.replace("serial=", "uid=").replace(tokens[tokens.length - 1], "*****");
        }
        player.sendMessage("[----------Info Debug----------]");
        String[] lines = StringUtils.splitIntoLine(format, 60);
        player.sendMessages(lines);
        player.sendMessage("[-------------------------------]");
        return format;
    }

    public String getCompName() {
        return compName;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public String getSerial() {
        return serial;
    }

    @Override
    public String toString() {
        // make sure serials always at end
        return "[ip=" + ip + ", compName=" + compName + ", mac=" + mac + ", serial=" + serial + "]";
    }

}
