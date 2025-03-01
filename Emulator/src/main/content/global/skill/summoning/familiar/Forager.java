package content.global.skill.summoning.familiar;

import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.world.GameWorld;
import core.tools.RandomFunction;

public abstract class Forager extends BurdenBeast {

    private final Item[] items;

    private int passiveDelay;

    public Forager(Player owner, int id, int ticks, int pouchId, int specialCost, int attackStyle, final Item... items) {
        super(owner, id, ticks, pouchId, specialCost, 30, attackStyle);
        this.items = items;
        setRandomPassive();
    }

    public Forager(Player owner, int id, int ticks, int pouchId, int specialCost, final Item... items) {
        this(owner, id, ticks, pouchId, specialCost, WeaponInterface.STYLE_DEFENSIVE, items);
    }

    @Override
    public void handleFamiliarTick() {
        super.handleFamiliarTick();
        if (items != null && items.length > 0 && passiveDelay < GameWorld.getTicks()) {
            if (RandomFunction.random(getRandom()) < 4) {
                produceItem(items[RandomFunction.random(items.length)]);
            }
            setRandomPassive();
        }
    }

    public boolean produceItem(final Item item) {
        if (!container.hasSpaceFor(item)) {
            owner.getPacketDispatch().sendMessage("Your familar is too full to collect items.");
            return false;
        }
        owner.getPacketDispatch().sendMessage(item.getAmount() == 1 ? "Your familar has produced an item." : "Your familiar has produced items.");
        return container.add(item);
    }

    public boolean produceItem() {
        if (items == null || items.length == 0) {
            return false;
        }
        return produceItem(items[RandomFunction.random(items.length)]);
    }

    public void handlePassiveAction() {

    }

    public int getRandom() {
        return 11;
    }

    public void setRandomPassive() {
        passiveDelay = GameWorld.getTicks() + RandomFunction.random(100, 440);
    }
}
