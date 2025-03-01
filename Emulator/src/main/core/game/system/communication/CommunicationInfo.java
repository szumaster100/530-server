package core.game.system.communication;

import core.auth.UserAccountInfo;
import core.cache.misc.buffer.ByteBufferUtils;
import core.game.node.entity.player.Player;
import core.game.system.mysql.SQLTable;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.repository.Repository;
import core.net.amsc.MSPacketRepository;
import core.net.amsc.WorldCommunicator;
import core.net.packet.PacketRepository;
import core.net.packet.context.ContactContext;
import core.net.packet.out.ContactPackets;
import core.tools.Log;
import core.tools.StringUtils;
import core.worker.ManagementEvents;
import org.jetbrains.annotations.NotNull;
import proto.management.PrivateMessage;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.Map.Entry;

import static core.api.ContentAPIKt.log;
import static core.api.ContentAPIKt.setVarp;

public final class CommunicationInfo {

    public static final int MAX_LIST_SIZE = 200;

    private Map<String, Contact> contacts = new HashMap<>();

    private final List<String> blocked = new ArrayList<>(20);

    private String clanName = "";

    private String currentClan = "";

    private ClanRank joinRequirement = ClanRank.ANY_FRIEND;

    private ClanRank messageRequirement = ClanRank.ANYONE;

    private ClanRank kickRequirement = ClanRank.ONLY_ME;

    private ClanRank lootRequirement = ClanRank.NO_ONE;

    private boolean lootShare;

    private ClanRepository clan = null;

    private Pulse lootSharePulse;

    public CommunicationInfo() {

    }

    public void save(SQLTable table) {
        String contacts = "";
        String blocked = "";
        for (int i = 0; i < this.blocked.size(); i++) {
            blocked += (i == 0 ? "" : ",") + this.blocked.get(i);
        }
        int count = 0;
        for (Entry<String, Contact> entry : this.contacts.entrySet()) {
            contacts += "{" + entry.getKey() + "," + entry.getValue().getRank().ordinal() + "}" + (count == this.contacts.size() - 1 ? "" : "~");
            count++;
        }
        table.getColumn("blocked").updateValue(blocked);
        table.getColumn("contacts").updateValue(contacts);
        table.getColumn("clanName").updateValue(clanName);
        table.getColumn("currentClan").updateValue(currentClan);
        table.getColumn("clanReqs").updateValue(joinRequirement.ordinal() + "," + messageRequirement.ordinal() + "," + kickRequirement.ordinal() + "," + lootRequirement.ordinal());
    }

    public void parse(SQLTable table) {
        String[] tokens = null;
        if (table.getColumn("contacts").getValue() != null) {
            String contacts = (String) table.getColumn("contacts").getValue();
            if (!contacts.isEmpty()) {
                String[] datas = contacts.split("~");
                Contact contact = null;
                for (String d : datas) {
                    tokens = d.replace("{", "").replace("}", "").split(",");
                    if (tokens.length == 0) {
                        continue;
                    }
                    contact = new Contact(tokens[0]);
                    contact.setRank(ClanRank.values()[Integer.valueOf(tokens[1])]);
                    this.contacts.put(tokens[0], contact);
                }
            }
        }
        if (table.getColumn("blocked").getValue() != null) {
            String blocked = (String) table.getColumn("blocked").getValue();
            if (!blocked.isEmpty()) {
                tokens = blocked.split(",");
                for (String name : tokens) {
                    this.blocked.add(name);
                }
            }
        }
        clanName = ((String) table.getColumn("clanName").getValue());
        currentClan = (String) table.getColumn("currentClan").getValue();
        String clanReqs = (String) table.getColumn("clanReqs").getValue();
        if (clanReqs == "") {
            return;
        }
        tokens = clanReqs.split(",");
        ClanRank rank = null;
        int ordinal = 0;
        for (int i = 0; i < tokens.length; i++) {
            ordinal = Integer.parseInt(tokens[i]);
            if (ordinal < 0 || ordinal > ClanRank.values().length - 1) {
                continue;
            }
            rank = ClanRank.values()[ordinal];
            switch (i) {
                case 0:
                    joinRequirement = rank;
                    break;
                case 1:
                    messageRequirement = rank;
                    break;
                case 2:
                    if (ordinal < 3 || ordinal > 8) {
                        break;
                    }
                    kickRequirement = rank;
                    break;
                case 3:
                    lootRequirement = rank;
                    break;
            }
        }
    }

    public void toggleLootshare(final Player player) {
        if (lootShare) {
            lootShare = false;
            setVarp(player, 1083, 0);
        } else if (lootSharePulse != null) {
            lootSharePulse.stop();
            lootSharePulse = null;
            setVarp(player, 1083, 0);
        } else if (!lootShare) {
            setVarp(player, 1083, 2);
            lootSharePulse = new Pulse(GameWorld.getSettings().isDevMode() ? 5 : 200, player) {
                @Override
                public boolean pulse() {
                    lootShare = true;
                    lootSharePulse = null;
                    setVarp(player, 1083, 1);
                    return true;
                }
            };
            GameWorld.getPulser().submit(lootSharePulse);
        }
    }

    public void parsePrevious(ByteBuffer buffer) {
        int size = buffer.get() & 0xFF;
        for (int i = 0; i < size; i++) {
            String name = ByteBufferUtils.getString(buffer);
            Contact contact = new Contact(name);
            contact.setRank(ClanRank.ANY_FRIEND);
            contacts.put(name, contact);
        }
        size = buffer.get() & 0xFF;
        for (int i = 0; i < size; i++) {
            blocked.add(ByteBufferUtils.getString(buffer));
        }
        if (buffer.get() == 1) {
            ByteBufferUtils.getString(buffer);
        }
    }

    public static void sendMessage(Player player, String target, String message) {
        PrivateMessage.Builder builder = PrivateMessage.newBuilder();
        builder.setSender(player.getName());
        builder.setRank(player.getDetails().getRights().ordinal());
        builder.setMessage(message);
        builder.setReceiver(target);
        ManagementEvents.publish(builder.build());
    }

    public static void add(Player player, String contact) {
        CommunicationInfo info = player.getDetails().getCommunication();
        if (contact.isEmpty()) {
            player.sendMessage("Unable to find a player by that name. Please try again.");
            return;
        }
        if (WorldCommunicator.isEnabled()) {
            MSPacketRepository.sendContactUpdate(player.getName(), contact, false, false, null);
            return;
        }
        if (info.contacts.size() >= MAX_LIST_SIZE) {
            player.getPacketDispatch().sendMessage("Your friend list is full.");
            return;
        }
        if (info.contacts.containsKey(contact)) {
            player.getPacketDispatch().sendMessage(StringUtils.formatDisplayName(contact) + " is already on your friend list.");
            return;
        }
        ClanRepository clan = ClanRepository.get(player.getName(), false);
        if (clan != null) {
            clan.rank(contact, ClanRank.ANY_FRIEND);
        }
        info.contacts.put(contact, new Contact(contact));
        Player target = Repository.getPlayerByName(contact);
        if (target != null) {
            if (showActive(player, target)) {
                PacketRepository.send(ContactPackets.class, new ContactContext(player, contact, GameWorld.getSettings().getWorldId()));
            }
            if (player.getSettings().getPrivateChatSetting() == 1 && showActive(target, player)) {
                PacketRepository.send(ContactPackets.class, new ContactContext(target, player.getName(), GameWorld.getSettings().getWorldId()));
            }
        }
    }

    public static void remove(Player player, String contact, boolean block) {
        if (contact.isEmpty()) {
            player.sendMessage("Unable to find a player by that name. Please try again.");
            return;
        }
        if (WorldCommunicator.isEnabled()) {
            MSPacketRepository.sendContactUpdate(player.getName(), contact, true, block, null);
            return;
        }
        CommunicationInfo info = player.getDetails().getCommunication();
        if (block) {
            info.blocked.remove(contact);
            Player target = Repository.getPlayerByName(contact);
            if (target != null && hasContact(target, player.getName())) {
                int worldId = 0;
                if (showActive(target, player)) {
                    worldId = GameWorld.getSettings().getWorldId();
                }
                PacketRepository.send(ContactPackets.class, new ContactContext(target, player.getName(), worldId));
            }
        } else {
            info.contacts.remove(contact);
            ClanRepository clan = ClanRepository.get(player.getName(), false);
            if (clan != null) {
                clan.rank(contact, ClanRank.ANYONE);
            }
            if (player.getSettings().getPrivateChatSetting() == 1) {
                Player target = Repository.getPlayerByName(contact);
                if (target != null) {
                    PacketRepository.send(ContactPackets.class, new ContactContext(target, player.getName(), 0));
                }
            }
        }
    }

    public static void block(Player player, String contact) {
        if (contact.isEmpty()) {
            player.sendMessage("Unable to find a player by that name. Please try again.");
            return;
        }
        if (WorldCommunicator.isEnabled()) {
            MSPacketRepository.sendContactUpdate(player.getName(), contact, false, true, null);
            return;
        }
        CommunicationInfo info = player.getDetails().getCommunication();
        if (info.blocked.size() >= MAX_LIST_SIZE) {
            player.getPacketDispatch().sendMessage("Your ignore list is full.");
            return;
        }
        if (info.blocked.contains(contact)) {
            player.getPacketDispatch().sendMessage(StringUtils.formatDisplayName(contact) + " is already on your ignore list.");
            return;
        }
        info.blocked.add(contact);
        Player target = Repository.getPlayerByName(contact);
        if (target != null && hasContact(target, player.getName())) {
            PacketRepository.send(ContactPackets.class, new ContactContext(target, player.getName(), 0));
        }
    }

    public static void updateClanRank(Player player, String contact, ClanRank clanRank) {
        if (contact.isEmpty()) {
            player.sendMessage("Unable to find a player by that name. Please try again.");
            return;
        }
        CommunicationInfo info = player.getDetails().getCommunication();
        Contact c = info.contacts.get(contact);
        if (c == null) {
            log(CommunicationInfo.class, Log.ERR, "Could not find contact " + contact + " to update clan rank!");
            return;
        }
        c.setRank(clanRank);
        ClanRepository clan = ClanRepository.get(player.getName());
        if (clan != null) {
            clan.rank(contact, clanRank);
        }
        int worldId = 0;
        if (CommunicationInfo.showActive(player, contact)) {
            worldId = c.getWorldId();
        }
        PacketRepository.send(ContactPackets.class, new ContactContext(player, contact, worldId));
    }

    public static boolean hasContact(Player player, String contact) {
        return player.getDetails().getCommunication().contacts.containsKey(contact);
    }

    public static boolean showActive(Player player, String name) {
        Player p = Repository.getPlayerByName(name);
        if (p != null) {
            return showActive(player, p);
        }
        return false;
    }

    public static boolean showActive(Player player, Player target) {
        if (target.getName().equals(player.getName())) {
            return false;
        }
        if (target.getCommunication().getBlocked().contains(player.getName())) {
            return false;
        }
        switch (target.getSettings().getPrivateChatSetting()) {
            case 1:
                if (!hasContact(target, player.getName())) {
                    return false;
                }
                return true;
            case 2:
                return false;
        }
        return true;
    }

    public Map<String, Contact> getContacts() {
        return contacts;
    }

    public List<String> getBlocked() {
        return blocked;
    }

    public String getClanName() {
        return clanName;
    }

    public void setClanName(String clanName) {
        this.clanName = clanName;
    }

    public String getCurrentClan() {
        return currentClan == null ? "" : currentClan;
    }

    @Deprecated
    public void setCurrentClan(String currentClan) {
        this.currentClan = currentClan;
    }

    public ClanRank getJoinRequirement() {
        return joinRequirement;
    }

    public void setJoinRequirement(ClanRank joinRequirement) {
        this.joinRequirement = joinRequirement;
    }

    public ClanRank getMessageRequirement() {
        return messageRequirement;
    }

    public void setMessageRequirement(ClanRank messageRequirement) {
        this.messageRequirement = messageRequirement;
    }

    public ClanRank getKickRequirement() {
        return kickRequirement;
    }

    public void setKickRequirement(ClanRank kickRequirement) {
        this.kickRequirement = kickRequirement;
    }

    public ClanRank getLootRequirement() {
        return lootRequirement;
    }

    public void setLootRequirement(ClanRank lootRequirement) {
        this.lootRequirement = lootRequirement;
    }

    public ClanRepository getClan() {
        return clan;
    }

    public void setClan(ClanRepository clan) {
        this.clan = clan;
        this.currentClan = clan == null ? null : clan.getOwner();
    }

    public boolean isLootShare() {
        return lootShare;
    }

    public void setLootShare(boolean lootShare) {
        this.lootShare = lootShare;
    }

    public void parse(@NotNull UserAccountInfo accountInfo) {
        blocked.addAll(Arrays.asList(accountInfo.getBlocked().split(",")));
        clanName = accountInfo.getClanName();
        currentClan = accountInfo.getCurrentClan();

        String clanReqs = accountInfo.getClanReqs();
        ClanRank[] reqs = parseClanRequirements(clanReqs);
        joinRequirement = reqs[0];
        messageRequirement = reqs[1];
        kickRequirement = reqs[2];
        lootRequirement = reqs[3];

        String contacts = accountInfo.getContacts();
        this.contacts = parseContacts(contacts);
    }

    public static ClanRank[] parseClanRequirements(String clanReqs) {
        ClanRank[] requirements = new ClanRank[4];
        String[] tokens = clanReqs.split(",");
        ClanRank rank;
        int ordinal;
        for (int i = 0; i < tokens.length; i++) {
            ordinal = Integer.parseInt(tokens[i]);
            if (ordinal < 0 || ordinal > ClanRank.values().length - 1) {
                continue;
            }
            rank = ClanRank.values()[ordinal];
            switch (i) {
                case 0:
                    requirements[0] = rank;
                    break;
                case 1:
                    requirements[1] = rank;
                    break;
                case 2:
                    if (ordinal < 3 || ordinal > 8) {
                        requirements[2] = ClanRank.NO_ONE;
                        break;
                    }
                    requirements[2] = rank;
                    break;
                case 3:
                    requirements[3] = rank;
                    break;
            }
        }

        return requirements;
    }

    public static HashMap<String, Contact> parseContacts(String contacts) {
        HashMap<String, Contact> theseContacts = new HashMap<>();
        String[] tokens;
        if (!contacts.isEmpty()) {
            String[] datas = contacts.split("~");
            Contact contact;
            for (String d : datas) {
                tokens = d.replace("{", "").replace("}", "").split(",");
                if (tokens.length == 0) {
                    continue;
                }
                contact = new Contact(tokens[0]);
                contact.setRank(ClanRank.values()[Integer.parseInt(tokens[1])]);
                theseContacts.put(tokens[0], contact);
            }
        }

        return theseContacts;
    }

    public String getContactString() {
        StringBuilder sb = new StringBuilder();

        int index = 0;
        for (Contact contact : contacts.values()) {
            sb.append("{");
            sb.append(contact.getUsername());
            sb.append(",");
            sb.append(contact.getRank().ordinal());
            sb.append("}");
            if (index++ < contacts.size() - 1) sb.append("~");
        }

        return sb.toString();
    }

    public String getBlockedString() {
        StringBuilder sb = new StringBuilder();

        int index = 0;
        for (String block : blocked) {
            sb.append(block);
            if (index++ < blocked.size() - 1) sb.append(",");
        }

        return sb.toString();
    }

    public String getClanReqString() {
        return
            joinRequirement.ordinal()
                + "," + messageRequirement.ordinal()
                + "," + kickRequirement.ordinal()
                + "," + lootRequirement.ordinal();
    }
}
