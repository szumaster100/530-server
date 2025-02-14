package content.global.activity.ttrail;

import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.emote.Emotes;
import core.game.world.map.zone.ZoneBorders;
import core.game.world.update.flag.context.Graphics;

public abstract class EmoteClueScroll extends ClueScrollPlugin {

    private final Emotes emote;

    private final Emotes commenceEmote;

    private final int[][] equipment;

    private final String clue;

    public EmoteClueScroll(String name, int clueId, ClueLevel level, Emotes emote, Emotes commenceEmote, int[][] equipment, final String clue, ZoneBorders... borders) {
        super(name, clueId, level, 345, borders);
        this.emote = emote;
        this.commenceEmote = commenceEmote;
        this.equipment = equipment;
        this.clue = clue;
    }

    @Override
    public boolean actionButton(Player player, int interfaceId, int buttonId, int slot, int itemId, int opcode) {
        if (!player.getInventory().contains(clueId, 1)) {
            return false;
        }
        final Emotes emote = Emotes.forId(buttonId);
        if (emote == null) {
            return false;
        }
        if (emote == this.emote) {
            NPC oldUri = player.getAttribute("uri", null);
            if (oldUri != null && oldUri.isActive()) {
                return false;
            }
            spawnUri(player);
        } else if (emote == commenceEmote) {
            player.setAttribute("commence-emote", true);
        }
        if (this.emote == emote) {
            return super.actionButton(player, interfaceId, buttonId, slot, itemId, opcode);
        } else {
            return false;
        }
    }

    @Override
    public void read(Player player) {
        for (int i = 1; i < 9; i++) {
            player.getPacketDispatch().sendString("", interfaceId, i);
        }
        super.read(player);
        player.getPacketDispatch().sendString(clue.replace("<br>", "<br><br>"), interfaceId, 1);
    }

    public void spawnUri(Player player) {
        boolean doubleAgent = level == ClueLevel.HARD && player.getAttribute("killed-agent", 0) != clueId;

        int id = 5141;
        if (doubleAgent) {
            boolean wilderness = player.getSkullManager().isWilderness();
            if (wilderness) {
                id = 5141;
                doubleAgent = false;
            } else {
                id = 5145;
            }
        }
        final NPC uri = NPC.create(id, player.getLocation().transform(1, 0, 0));
        player.setAttribute("uri", uri);
        player.removeAttribute("commence-emote");
        uri.setAttribute("double-agent", doubleAgent);
        uri.setAttribute("player", player);
        uri.setAttribute("clue", this);
        uri.init();
        uri.graphics(Graphics.create(86));
        uri.faceTemporary(player, 1);
        if (doubleAgent) {
            uri.sendChat("I expect you to die!");
            uri.getProperties().getCombatPulse().attack(player);
        }
    }

    public boolean hasCommencedEmote() {
        return commenceEmote != null;
    }

    public Emotes getEmote() {
        return emote;
    }

    public int[][] getEquipment() {
        return equipment;
    }

    public Emotes getCommenceEmote() {
        return commenceEmote;
    }

}
