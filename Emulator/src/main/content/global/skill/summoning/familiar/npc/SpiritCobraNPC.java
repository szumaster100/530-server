package content.global.skill.summoning.familiar.npc;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.plugin.Initializable;

@Initializable
public class SpiritCobraNPC extends content.global.skill.summoning.familiar.Familiar {

    public SpiritCobraNPC() {
        this(null, 6802);
    }

    public SpiritCobraNPC(Player owner, int id) {
        super(owner, id, 5600, 12015, 3, WeaponInterface.STYLE_ACCURATE);
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return new SpiritCobraNPC(owner, id);
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        final Item item = (Item) special.getNode();
        final Egg egg = Egg.forEgg(item);
        if (egg == null) {
            owner.getPacketDispatch().sendMessage("You can't use the special move on this item.");
            return false;
        }
        owner.getInventory().replace(egg.getProduct(), item.getSlot());
        return true;
    }

    @Override
    public int[] getIds() {
        return new int[]{6802, 6803};
    }

    public enum Egg {
        COCKATRICE(new Item(1944), new Item(12109)), SARATRICE(new Item(5077), new Item(12113)), ZAMATRICE(new Item(5076), new Item(12115)), GUTHATRICE(new Item(5078), new Item(12111)), CORACATRICE(new Item(11964), new Item(12119)), PENGATRICE(new Item(12483), new Item(12117)), VULATRICE(new Item(11965), new Item(12121));

        private final Item egg;

        private final Item product;

        private Egg(Item egg, Item product) {
            this.egg = egg;
            this.product = product;
        }

        public static Egg forEgg(final Item item) {
            for (Egg egg : values()) {
                if (egg.getEgg().getId() == item.getId()) {
                    return egg;
                }
            }
            return null;
        }

        public Item getEgg() {
            return egg;
        }

        public Item getProduct() {
            return product;
        }

    }
}
