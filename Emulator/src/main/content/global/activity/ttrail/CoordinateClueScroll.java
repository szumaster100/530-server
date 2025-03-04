package content.global.activity.ttrail;

import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.world.map.Location;
import core.game.world.update.flag.context.Graphics;

public abstract class CoordinateClueScroll extends MapClueScroll {

    public static final Item SEXTANT = new Item(2574);

    public static final Item WATCH = new Item(2575);

    public static final Item CHART = new Item(2576);

    public static final Location CLOCK_TOWER = new Location(2440, 3161, 0);

    private final String clue;

    public CoordinateClueScroll(String name, int clueId, ClueLevel level, Location location, String clue) {
        super(name, clueId, level, 345, location, 0);
        this.clue = clue;
    }

    @Override
    public void read(Player player) {
        for (int i = 1; i < 9; i++) {
            player.getPacketDispatch().sendString("", interfaceId, i);
        }
        super.read(player);
        player.getPacketDispatch().sendString("<br><br><br><br><br>" + clue.replace("<br>", "<br><br>"), interfaceId, 1);
    }

    @Override
    public void dig(Player player) {
        int killedWizardClueId = player.getAttribute("killed-wizard", -1);
        if (getLevel() == ClueLevel.HARD && (killedWizardClueId == -1 || killedWizardClueId != getClueId())) {
            NPC wizard = player.getAttribute("t-wizard", null);
            if (wizard != null && wizard.isActive()) {
                return;
            }
            spawnWizard(player);
            return;
        }
        super.dig(player);
        player.removeAttribute("killed-wizard");
    }

    private void spawnWizard(Player player) {
        int id = !player.getSkullManager().isWilderness() ? 1264 : 1007;
        final NPC wizard = NPC.create(id, player.getLocation().transform(1, 0, 0));
        player.setAttribute("t-wizard", wizard);
        wizard.setAttribute("clue", this);
        wizard.setAttribute("player", player);
        wizard.init();
        wizard.graphics(Graphics.create(86));
        wizard.faceTemporary(player, 1);
        wizard.sendChat(id == 1264 ? "For Saradomin!" : "Die human!");
        wizard.getProperties().getCombatPulse().attack(player);
    }

    public String getClue() {
        return clue;
    }
}
