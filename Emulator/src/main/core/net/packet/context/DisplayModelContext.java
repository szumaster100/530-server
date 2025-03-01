package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public class DisplayModelContext implements Context {

	public static enum ModelType {
		PLAYER, NPC, ITEM, MODEL;
	}

	private final Player player;

	private final ModelType type;

	private final int nodeId;

	private int amount;

	private int zoom;

	private final int interfaceId;

	private final int childId;

	public DisplayModelContext(Player player, int interfaceId, int childId) {
		this(player, ModelType.PLAYER, -1, 0, interfaceId, childId);
	}

	public DisplayModelContext(Player player, int nodeId, int interfaceId, int childId) {
		this(player, ModelType.NPC, nodeId, 0, interfaceId, childId);
	}

	public DisplayModelContext(Player player, ModelType type, int nodeId, int amount, int interfaceId, int childId) {
		this.player = player;
		this.type = type;
		this.nodeId = nodeId;
		this.amount = amount;
		this.interfaceId = interfaceId;
		this.childId = childId;
	}

	public DisplayModelContext(Player player, ModelType type, int nodeId, int zoom, int interfaceId, int childId, Object... object) {
		this.player = player;
		this.type = type;
		this.nodeId = nodeId;
		this.setZoom(zoom);
		this.interfaceId = interfaceId;
		this.childId = childId;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public ModelType getType() {
		return type;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getAmount() {
		return amount;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public int getChildId() {
		return childId;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

}