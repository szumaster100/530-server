package core.game.system.communication;

import core.ServerConfig;
import core.game.activity.ActivityPlugin;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.info.PlayerDetails;
import core.game.node.entity.player.info.PlayerMonitor;
import core.game.node.entity.player.info.Rights;
import core.game.world.GameWorld;
import core.game.world.repository.Repository;
import core.net.amsc.WorldCommunicator;
import core.net.packet.PacketRepository;
import core.net.packet.context.ClanContext;
import core.net.packet.context.MessageContext;
import core.net.packet.out.CommunicationMessage;
import core.net.packet.out.UpdateClanChat;
import core.worker.ManagementEvents;
import proto.management.ClanJoinNotification;
import proto.management.ClanLeaveNotification;

import java.util.*;

public final class ClanRepository {

    private static final int MAX_MEMBERS = 100;

    private static final Map<String, ClanRepository> CLAN_REPOSITORY = new HashMap<>();

    private final String owner;

    private String name = "Chat disabled";

    private ClanRank joinRequirement = ClanRank.ANY_FRIEND;

    private ClanRank messageRequirement = ClanRank.ANYONE;

    private ClanRank kickRequirement = ClanRank.ONLY_ME;

    private ClanRank lootRequirement = ClanRank.NO_ONE;

    private final Map<String, ClanRank> ranks = new HashMap<>();

    private final Map<String, Long> banned = new HashMap<>();

    private List<ClanEntry> players = new ArrayList<>(MAX_MEMBERS);

    private ActivityPlugin clanWar;

    public ClanRepository(String owner) {
        this.owner = owner;
    }

    public boolean enter(Player player) {
        if (!owner.equals(ServerConfig.SERVER_NAME.toLowerCase()) && players.size() >= MAX_MEMBERS) {
            player.getPacketDispatch().sendMessage("The channel you tried to join is full.:clan:");
            return false;
        }
        if (!player.getName().equals(owner) && player.getDetails().getRights() != Rights.ADMINISTRATOR) {
            if (isBanned(player.getName())) {
                player.getPacketDispatch().sendMessage("You are temporarily banned from this clan channel.:clan:");
                return false;
            }
            Player o = Repository.getPlayerByName(owner);
            if (o != null) {
                if (o.getCommunication().getBlocked().contains(player.getName())) {
                    player.getPacketDispatch().sendMessage("You do not have a high enough rank to join this clan channel.:clan:");
                    return false;
                }
            }
            ClanRank rank = getRank(player);
            if (rank.ordinal() < joinRequirement.ordinal()) {
                player.getPacketDispatch().sendMessage("You do not have a high enough rank to join this clan channel.:clan:");
                return false;
            }
        }
        ClanEntry entry = new ClanEntry(player);
        if (!players.contains(entry)) {
            players.add(entry);
        }

        ClanJoinNotification.Builder event = ClanJoinNotification.newBuilder();
        event.setUsername(player.getName());
        event.setWorld(GameWorld.getSettings().getWorldId());
        event.setClanName(owner);
        ManagementEvents.publish(event.build());

        player.getPacketDispatch().sendMessage("Now talking in clan channel " + name + ":clan:");
        player.getPacketDispatch().sendMessage("To talk, start each line of chat with the / symbol.:clan:");
        update();
        return true;
    }

    public void clean(boolean disable) {
        if (WorldCommunicator.isEnabled()) {
            return;
        }
        for (Iterator<ClanEntry> it = players.iterator(); it.hasNext(); ) {
            ClanEntry entry = it.next();
            Player player = entry.getPlayer();
            boolean remove = disable;
            if (!remove) {
                remove = getRank(player).ordinal() < joinRequirement.ordinal();
            }
            if (remove) {
                leave(player, false);
                player.getCommunication().setClan(null);
                it.remove();
            }
        }
        if (players.isEmpty()) {
            banned.clear();
        }
        update();
    }

    public boolean isBanned(String name) {
        if (banned.containsKey(name)) {
            long time = banned.get(name);
            if (time > System.currentTimeMillis()) {
                return true;
            }
            banned.remove(name);
        }
        return false;
    }

    public void message(Player player, String message) {
        if (player.getLocks().isLocked("cc_message") || isBanned(player.getName())) {
            return;
        }
        player.getLocks().lock("cc_message", 1);
        if (!player.getName().equals(owner) && player.getDetails().getRights() != Rights.ADMINISTRATOR) {
            ClanRank rank = getRank(player);
            if (rank.ordinal() < messageRequirement.ordinal()) {
                player.getPacketDispatch().sendMessage("You do not have a high enough rank to talk in this clan channel.:clan:");
                return;
            }
        }
        PlayerMonitor.logChat(player, "clan", message);
        for (Iterator<ClanEntry> it = players.iterator(); it.hasNext(); ) {
            ClanEntry entry = it.next();
            Player p = entry.getPlayer();
            if (p != null) {
                PacketRepository.send(CommunicationMessage.class, new MessageContext(p, player, MessageContext.CLAN_MESSAGE, message));
            }
        }
    }

    public void kick(Player player, Player target) {
        ClanRank rank = getRank(player);
        if (target.getDetails().getRights() == Rights.ADMINISTRATOR) {
            player.sendMessage("You can't kick an administrator.:clan:");
            return;
        }
        if (target.getName().equals(player.getName())) {
            player.sendMessage("You can't kick yourself.:clan:");
            return;
        }
        if (player.getDetails().getRights() != Rights.ADMINISTRATOR && rank.ordinal() < kickRequirement.ordinal()) {
            player.getPacketDispatch().sendMessage("You do not have a high enough rank to kick in this clan channel.:clan:");
            return;
        }
        if (target.getName().equals(owner)) {
            player.getPacketDispatch().sendMessage("You can't kick the owner of this clan channel.:clan:");
            return;
        }
        for (ClanEntry e : players) {
            PacketRepository.send(CommunicationMessage.class, new MessageContext(e.getPlayer(), player, MessageContext.CLAN_MESSAGE, "[Attempting to kick/ban " + target.getUsername() + " from this Clan Chat.]"));
        }
        leave(target, true, "You have been kicked from the channel.:clan:");
        target.getCommunication().setClan(null);
        banned.put(target.getName(), System.currentTimeMillis() + (3_600_000));
    }

    public void leave(Player player, boolean remove) {
        leave(player, remove, "You have left the channel.:clan:");
    }

    public void leave(Player player, boolean remove, String message) {
        if (remove) {
            players.remove(new ClanEntry(player));
            update();
            if (players.size() < 1) {
                banned.clear();
            }
        }
        PacketRepository.send(UpdateClanChat.class, new ClanContext(player, this, true));
        player.getPacketDispatch().sendMessage(message);
        if (clanWar != null && !isDefault()) {
            clanWar.fireEvent("leavefc", player);
        }

        ClanLeaveNotification.Builder event = ClanLeaveNotification.newBuilder();
        event.setClanName(owner);
        event.setUsername(player.getName());
        event.setWorld(GameWorld.getSettings().getWorldId());
        ManagementEvents.publish(event.build());
    }

    public void rank(String name, ClanRank rank) {
        boolean update;
        if (rank == ClanRank.ANYONE) {
            update = ranks.remove(name) != null;
        } else {
            update = ranks.put(name, rank) != rank;
        }
        if (update) {
            clean(false);
        }
    }

    public void update() {
        for (Iterator<ClanEntry> it = players.iterator(); it.hasNext(); ) {
            ClanEntry e = it.next();
            if (e.getWorldId() == GameWorld.getSettings().getWorldId() && e.getPlayer() != null) {
                PacketRepository.send(UpdateClanChat.class, new ClanContext(e.getPlayer(), this, false));
            }
        }
    }

    public ClanRank getRank(ClanEntry entry) {
        if (entry.getPlayer() != null) {
            return getRank(entry.getPlayer());
        }
        ClanRank rank = ranks.get(entry.getName());
        if (rank == null) {
            return ClanRank.ANYONE;
        }
        return rank;
    }

    public ClanRank getRank(Player player) {
        ClanRank rank = ranks.get(player.getName());
        if (player.getDetails().getRights() == Rights.ADMINISTRATOR && !player.getName().equals(owner)) {
            return ClanRank.NO_ONE;
        }
        if (rank == null) {
            if (player.getName().equals(owner)) {
                return ClanRank.ONLY_ME;
            }
            return ClanRank.ANYONE;
        }
        return rank;
    }

    public static ClanRepository get(String owner) {
        return get(owner, false);
    }

    public static ClanRepository get(String owner, boolean create) {
        ClanRepository clan = CLAN_REPOSITORY.get(owner);
        if (clan != null) {
            return clan;
        }
        Player player = Repository.getPlayerByName(owner);
        PlayerDetails details = player != null ? player.getDetails() : null;
        if (details == null) {
            details = PlayerDetails.getDetails(owner);
            if (details == null) {
                return null;
            }
        }
        String name = details.getCommunication().getClanName();
        if (name.length() < 1) {
            if (!create) {
                return null;
            }
            name = "Chat disabled";
        }
        CLAN_REPOSITORY.put(owner, clan = new ClanRepository(owner));
        for (Contact c : details.getCommunication().getContacts().values()) {
            clan.ranks.put(c.getUsername(), c.getRank());
        }
        clan.name = name;
        clan.joinRequirement = details.getCommunication().getJoinRequirement();
        clan.messageRequirement = details.getCommunication().getMessageRequirement();
        clan.kickRequirement = details.getCommunication().getKickRequirement();
        clan.lootRequirement = details.getCommunication().getLootRequirement();
        return clan;
    }

    public boolean isDefault() {
        return owner.equals(GameWorld.getSettings().getName().toLowerCase());
    }

    public static ClanRepository getDefault() {
        return get(GameWorld.getSettings().getName().toLowerCase());
    }

    public void delete() {
        CLAN_REPOSITORY.remove(owner);
        clean(true);
    }

    public static Map<String, ClanRepository> getClans() {
        return CLAN_REPOSITORY;
    }

    public List<ClanEntry> getPlayers() {
        return players;
    }

    public ClanRank getJoinRequirement() {
        return joinRequirement;
    }

    public void setJoinRequirement(ClanRank joinRequirement) {
        this.joinRequirement = joinRequirement;
        clean(false);
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
        update();
    }

    public ClanRank getLootRequirement() {
        return lootRequirement;
    }

    public Map<String, Long> getBanned() {
        return banned;
    }

    public void setLootRequirement(ClanRank lootRequirement) {
        this.lootRequirement = lootRequirement;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ClanRank> getRanks() {
        return ranks;
    }

    public ActivityPlugin getClanWar() {
        return clanWar;
    }

    public void setClanWar(ActivityPlugin clanWar) {
        this.clanWar = clanWar;
    }

}