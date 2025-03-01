package content.global.ame.pickaxehead;

import org.rs.consts.Items;

import java.util.HashMap;
import java.util.Map;

public enum PickaxeHead {
    BRONZE(Items.BRONZE_PICK_HEAD_480, Items.BRONZE_PICKAXE_1265),
    IRON(Items.IRON_PICK_HEAD_482, Items.IRON_PICKAXE_1267),
    STEEL(Items.STEEL_PICK_HEAD_484, Items.STEEL_PICKAXE_1269),
    MITHRIL(Items.MITHRIL_PICK_HEAD_486, Items.MITHRIL_PICKAXE_1273),
    ADAMANT(Items.ADAMANT_PICK_HEAD_488, Items.ADAMANT_PICKAXE_1271),
    RUNE(Items.RUNE_PICK_HEAD_490, Items.RUNE_PICKAXE_1275);

    private final int head;
    private final int pickaxe;

    private static final Map<Integer, PickaxeHead> product = new HashMap<>();

    static {
        for (PickaxeHead pickaxeHead : PickaxeHead.values()) {
            product.put(pickaxeHead.getPickaxe(), pickaxeHead);
        }
    }

    PickaxeHead(int head, int pickaxe) {
        this.head = head;
        this.pickaxe = pickaxe;
    }

    public int getHead() {
        return head;
    }

    public int getPickaxe() {
        return pickaxe;
    }

    public static PickaxeHead fromHeadId(int headId) {
        for (PickaxeHead pickaxeHead : PickaxeHead.values()) {
            if (pickaxeHead.getHead() == headId) {
                return pickaxeHead;
            }
        }
        return null;
    }

    public static Map<Integer, PickaxeHead> getProduct() {
        return product;
    }
}
