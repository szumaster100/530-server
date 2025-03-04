package content.global.skill.summoning.familiar;

import content.global.skill.summoning.SummoningPouch;
import core.game.component.CloseEvent;
import core.game.component.Component;
import core.game.container.Container;
import core.game.container.access.InterfaceContainer;
import core.game.node.entity.combat.equipment.WeaponInterface;
import core.game.node.entity.player.Player;
import core.game.node.item.GroundItem;
import core.game.node.item.GroundItemManager;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
public abstract class BurdenBeast extends Familiar {

	protected Container container;

	public BurdenBeast(Player owner, int id, int ticks, int pouchId, int specialCost, int containerSize, int attackStyle) {
		super(owner, id, ticks, pouchId, specialCost, attackStyle);
		this.container = new Container(containerSize).register(new BurdenContainerListener(owner));
	}

	public BurdenBeast(Player owner, int id, int ticks, int pouchId, int specialCost, int containerSize) {
		this(owner, id, ticks, pouchId, specialCost, containerSize, WeaponInterface.STYLE_DEFENSIVE);
	}

	@Override
	public void dismiss() {
		if (owner.getInterfaceManager().hasMainComponent(671)) {
			owner.getInterfaceManager().close();
		}

        for (Item item : container.toArray()) {
            if (item != null) {
                GroundItemManager.create(new GroundItem(item, location, 500, owner));
            }
        }

		container.clear();
		super.dismiss();
	}

	@Override
	public boolean isBurdenBeast() {
		return true;
	}

	@Override
	public boolean isPoisonImmune() {
		return true;
	}

	public boolean isAllowed(Player owner, Item item) {
		if (item.getValue() > 50000) {
			owner.getPacketDispatch().sendMessage("This item is too valuable to trust to this familiar.");
			return false;
		}
		if (!item.getDefinition().isTradeable()) {
			owner.getPacketDispatch().sendMessage("You can't trade this item, not even to your familiar.");
			return false;
		}
		if ((!SummoningPouch.get(getPouchId()).abyssal && (item.getId() == 1436 || item.getId() == 7936)) || !item.getDefinition().getConfiguration(ItemConfigParser.BANKABLE, true)) {
			owner.getPacketDispatch().sendMessage("You can't store " + item.getName().toLowerCase() + " in this familiar.");
			return false;
		}
		if (SummoningPouch.get(this.getPouchId()).abyssal) {
			if (!item.getName().toLowerCase().contains("essence")) {
				owner.getPacketDispatch().sendMessage("You can only give unnoted essence to this familiar.");
				return false;
			}
			if (item.getId() == 1437 || item.getId() == 7937) {
				owner.getPacketDispatch().sendMessage("You can't give noted essence to this familiar.");
				return false;
			}
		}
		return true;
	}

	public void transfer(Item item, int amount, boolean withdraw) {
		if (this instanceof Forager && !withdraw) {
			owner.getPacketDispatch().sendMessage("You can't store your items in this familiar.");
			return;
		}
		if (item == null || owner == null) {
			return;
		}
		if (!withdraw && !isAllowed(owner, new Item(item.getId(), item.getDefinition().isStackable() ? amount : 1))) {
			return;
		}
		Container to = withdraw ? owner.getInventory() : container;
		Container from = withdraw ? container : owner.getInventory();
		int fromAmount = from.getAmount(item);
		if (amount > fromAmount) {
			amount = fromAmount;
		}
		int maximum = to.getMaximumAdd(item);
		if (amount > maximum) {
			amount = maximum;
		}
		if (amount < 1) {
			if (withdraw) {
				owner.getPacketDispatch().sendMessage("Not enough space in your inventory.");
			} else {
				owner.getPacketDispatch().sendMessage("Your familiar can't carry any more items.");
			}
			return;
		}
		if (!item.getDefinition().isStackable() && item.getSlot() > -1) {
			from.replace(null, item.getSlot());
			to.add(new Item(item.getId(), 1));
			amount--;
		}
		if (amount > 0) {
			item = new Item(item.getId(), amount);
			if (from.remove(item)) {
				to.add(item);
			}
		}
	}

	public void withdrawAll() {
		for (int i = 0; i < container.capacity(); i++) {
			Item item = container.get(i);
			if (item == null) {
				continue;
			}
			int amount = owner.getInventory().getMaximumAdd(item);
			if (item.getAmount() > amount) {
				item = new Item(item.getId(), amount);
				container.remove(item, false);
				owner.getInventory().add(item, false);
			} else {
				container.replace(null, i, false);
				owner.getInventory().add(item, false);
			}
		}
		container.update();
		owner.getInventory().update();
	}

	public void openInterface() {
		if (getContainer().itemCount() == 0 && this instanceof Forager) {
			owner.getPacketDispatch().sendMessage("Your familiar is not carrying any items that you can withdraw.");
			return;
		}
		owner.getInterfaceManager().open(new Component(671)).setCloseEvent(new CloseEvent() {
			@Override
			public boolean close(Player player, Component c) {
				player.getInterfaceManager().closeSingleTab();
				return true;
			}
		});
		container.shift();
		owner.getInterfaceManager().openSingleTab(new Component(665));
		InterfaceContainer.generateItems(owner, owner.getInventory().toArray(), new String[] { "Examine", "Store-X", "Store-All", "Store-10", "Store-5", "Store-1" }, 665, 0, 7, 4, 93);
		InterfaceContainer.generateItems(owner, container.toArray(), new String[] { "Examine", "Withdraw-X", "Withdraw-All", "Withdraw-10", "Withdraw-5", "Withdraw-1" }, 671, 27, 5, 6, 30);
		container.refresh();
	}

	public Container getContainer() {
		return container;
	}

}
