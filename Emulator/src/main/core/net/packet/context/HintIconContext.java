package core.net.packet.context;

import core.game.node.Node;
import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.world.map.Location;
import core.net.packet.Context;

public final class HintIconContext implements Context {

	private final Player player;

	private final int slot;

	private int targetType;

	private int arrowId;

	private final int index;

	private final int modelId;

	private final Location location;

	private int height;

	public HintIconContext(Player player, int slot, int arrowId, Node target, int modelId) {
		this(player, slot, arrowId, -1, target, modelId);
		targetType = 2;
		if (target instanceof Entity) {
			targetType = target instanceof Player ? 10 : 1;
		}
	}

	public HintIconContext(Player player, int slot, int arrowId, int targetType, Node target, int modelId) {
		this(player, slot, arrowId, targetType, target, modelId, 0);
	}

	public HintIconContext(Player player, int slot, int arrowId, int targetType, Node target, int modelId, int height) {
		this.player = player;
		this.slot = slot;
		this.targetType = targetType;
		this.arrowId = arrowId;
		this.modelId = modelId;
		this.height = height;
		if (target instanceof Entity) {
			this.index = ((Entity) target).getIndex();
			this.location = null;
		} else {
			this.location = target.getLocation();
			this.index = -1;
		}
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getSlot() {
		return slot;
	}

	public int getTargetType() {
		return targetType;
	}

	public int getArrowId() {
		return arrowId;
	}

	public int getIndex() {
		return index;
	}

	public int getModelId() {
		return modelId;
	}

	public Location getLocation() {
		return location;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public void setArrowId(int arrowId) {
		this.arrowId = arrowId;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}