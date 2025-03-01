package core.net.packet.context;

import core.game.container.Container;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.net.packet.Context;

public final class ContainerContext implements Context {

	private Player player;

	private final int interfaceId;

	private final int childId;

	private final int containerId;

	private Item[] items;

	public int[] ids;

	private final int length;

	private final boolean split;

	private final int[] slots;

	private boolean clear;

	public ContainerContext(Player player, int interfaceId, int childId, boolean clear) {
		this(player, interfaceId, childId, 0, null, 1, false);
		this.clear = clear;
	}

	public ContainerContext(Player player, int interfaceId, int childId, int containerId, Container container, boolean split) {
		this(player, interfaceId, childId, containerId, container.toArray(), container.toArray().length, split);
	}

	public ContainerContext(Player player, int interfaceId, int childId, int containerId, Item[] items, boolean split) {
		this(player, interfaceId, childId, containerId, items, items.length, split);
	}

	public ContainerContext(Player player, int interfaceId, int childId, int containerId, Item[] items, int length, boolean split) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.containerId = containerId;
		this.items = items;
		this.length = length;
		this.split = split;
		this.slots = null;
	}

	public ContainerContext(Player player, int interfaceId, int childId, int containerId, int[] items) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.containerId = containerId;
		this.ids = items;
		this.length = items.length;
		this.split = false;
		this.slots = null;
	}

	public ContainerContext(Player player, int interfaceId, int childId, int containerId, Item[] items, boolean split, int... slots) {
		this.player = player;
		this.interfaceId = interfaceId;
		this.childId = childId;
		this.containerId = containerId;
		this.items = items;
		this.length = items.length;
		this.split = split;
		this.slots = slots;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getContainerId() {
		return containerId;
	}

	public Item[] getItems() {
		return items;
	}

	public int getLength() {
		return length;
	}

	public boolean isSplit() {
		return split;
	}

	public int[] getSlots() {
		return slots;
	}

	public int getChildId() {
		return childId;
	}

	public boolean isClear() {
		return clear;
	}

	public void setClear(boolean clear) {
		this.clear = clear;
	}

}