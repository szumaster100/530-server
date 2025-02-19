package content.region.karamja.handlers.shilo;

import core.game.node.item.Item;
import org.rs.consts.Items;

import java.util.HashMap;
import java.util.Map;

public enum AntiqueItem {

    BONE_KEY(new Item(Items.BONE_KEY_605, 1), 100, "I'll give you 100 coins for the Bone Key...", "That's a great bone key."),
    STONE_PLAQUE(new Item(Items.STONE_PLAQUE_606, 1), 100, "I'll give you 100 coins for the Stone Plaque...", "That's a great stone plaque."),
    TATTERED_SCROLL(new Item(Items.TATTERED_SCROLL_607, 1), 100, "I'll give you 100 coins for the tattered scroll...", "That's a great tattered scroll."),
    CRUMPLED_SCROLL(new Item(Items.CRUMPLED_SCROLL_608, 1), 100, "I'll give you 100 coins for the crumpled scroll...", "That's a great crumpled scroll."),
    LOCATING_CRYSTAL(new Item(Items.LOCATING_CRYSTAL_611, 1), 500, "I'll give you 500 coins for your locating crystal...", "That's a great Locating Crystal."),
    BEADS_OF_THE_DEAD(new Item(Items.BEADS_OF_THE_DEAD_616, 1), 1000, "I'll give you 1000 coins for your 'Beads of the Dead'...", "Impressive necklace there."),
    BERVIRIUS_NOTES(new Item(Items.BERVIRIUS_NOTES_624, 1), 100, "I'll give you 100 coins for your bervirius scroll...", "That's a great copy of Bervirius notes."),
    BLACK_PRISM(new Item(Items.BLACK_PRISM_4808, 1), 5000, "I'll give you 5000 coins for your Black prism...", "Ah you'd like to sell this to me would you? I can offer you 5000 coins!");

    private final Item item;
    private final int price;
    private final String message;
    private final String dialogue;

    AntiqueItem(Item item, int price, String message, String dialogue) {
        this.item = item;
        this.price = price;
        this.message = message;
        this.dialogue = dialogue;
    }

    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public String getMessage() {
        return message;
    }

    public String getDialogue() {
        return dialogue;
    }

    private static final Map<Integer, AntiqueItem> antiqueMap = new HashMap<>();

    static {
        for (AntiqueItem antique : AntiqueItem.values()) {
            antiqueMap.put(antique.getItem().getId(), antique);
        }
    }

    public static AntiqueItem getAntiqueItem(int id) {
        return antiqueMap.get(id);
    }
}
