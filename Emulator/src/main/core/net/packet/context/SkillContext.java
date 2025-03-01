package core.net.packet.context;

import core.game.node.entity.player.Player;
import core.net.packet.Context;

public final class SkillContext implements Context {

	private final Player player;

	private final int skillId;

	public SkillContext(Player player, int skillId) {
		this.player = player;
		this.skillId = skillId;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	public int getSkillId() {
		return skillId;
	}

}