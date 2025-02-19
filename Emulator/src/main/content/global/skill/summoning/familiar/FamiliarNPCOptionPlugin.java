package content.global.skill.summoning.familiar;

import core.cache.def.impl.NPCDefinition;
import core.game.interaction.OptionHandler;
import core.game.node.Node;
import core.game.node.entity.player.Player;
import core.plugin.Initializable;
import core.plugin.Plugin;

@Initializable
public final class FamiliarNPCOptionPlugin extends OptionHandler {

	@Override
	public Plugin<Object> newInstance(Object arg) throws Throwable {
		NPCDefinition.setOptionHandler("pick-up", this);
		NPCDefinition.setOptionHandler("interact-with", this);
		NPCDefinition.setOptionHandler("interact", this);
		NPCDefinition.setOptionHandler("store", this);
		NPCDefinition.setOptionHandler("withdraw", this);
		return this;
	}

	@Override
	public boolean handle(final Player player, Node node, String option) {
		if (!(node instanceof content.global.skill.summoning.familiar.Familiar)) {
			return false;
		}
		content.global.skill.summoning.familiar.Familiar familiar = (Familiar) node;
		if (familiar.getOwner() != player) {
			player.getPacketDispatch().sendMessage("This is not your familiar.");
			return true;
		}
		switch (option) {
		case "pick-up":
			player.faceLocation(familiar.getFaceLocation(player.getLocation()));
			player.getFamiliarManager().pickup();
			break;
		case "interact-with":
			player.getDialogueInterpreter().open(343823);
			break;
		case "interact":
			player.getDialogueInterpreter().open(node.getId(), node);
			break;
		case "store":
		case "withdraw":
			if (!familiar.isBurdenBeast()) {
				player.getPacketDispatch().sendMessage("This is not a beast of burden.");
				break;
			}
			((BurdenBeast) familiar).openInterface();
			break;
		}
		return true;
	}

}
