package content.global.skill.summoning.familiar;

import content.global.skill.summoning.pet.Pet;
import core.game.interaction.NodeUsageEvent;
import core.game.interaction.UseWithHandler;
import core.plugin.Initializable;
import core.plugin.Plugin;

@Initializable
public final class FamiliarFeedPlugin extends UseWithHandler {

	public FamiliarFeedPlugin() {
		super(321, 363, 341, 15264, 345, 377, 353, 389, 7944, 349, 331, 327, 395, 383, 317, 371, 335, 359, 15270, 1927, 321, 2132, 2134, 2136, 2138, 10816, 9986, 9978, 526, 1059, 225, 221, 592, 12125, 12127, 313, 12129, 2970, 1977, 12130, 13379, 1963, 319, 365, 339, 347, 379, 355, 391, 7946, 351, 329, 325, 397, 385, 315, 373, 333, 361, 1927);
	}

	@Override
	public Plugin<Object> newInstance(Object arg) throws Throwable {
		int[] ids = new int[] { 761, 762, 763, 764, 765, 766, 3505, 3598, 6969, 7259, 7260, 6964, 7249, 7251, 6960, 7241, 7243, 6962, 7245, 7247, 6966, 7253, 7255, 6958, 7237, 7239, 6915, 7277, 7278, 7279, 7280, 7018, 7019, 7020, 6908, 7313, 7316, 6947, 7293, 7295, 7297, 7299, 6911, 7261, 7263, 7265, 7267, 7269, 6919, 7301, 7303, 7305, 7307, 6949, 6952, 6955, 6913, 7271, 7273, 6945, 7319, 7321, 7323, 7325, 7327, 6922, 6942, 7210, 7212, 7214, 7216, 7218, 7220, 7222, 7224, 7226, 6900, 6902, 6904, 6906, 768, 769, 770, 771, 772, 773, 3504, 8214, 6968, 7257, 7258, 6965, 7250, 7252, 6961, 7242, 7244, 6963, 7246, 7248, 6967, 7254, 7256, 6859, 7238, 7240, 6916, 7281, 7282, 7283, 7284, 7015, 7016, 7017, 6909, 7314, 7317, 11413, 6948, 7294, 7296, 7298, 7300, 6912, 7262, 7264, 7266, 7268, 7270, 6920, 7302, 7304, 7306, 7308, 6950, 6953, 6956, 6914, 7272, 7274, 13090, 6946, 7320, 7322, 7324, 7326, 7328, 6923, 6943, 7211, 7213, 7215, 7217, 7219, 7221, 7223, 7225, 7227, 6901, 6903, 6905, 6907, 774, 775, 776, 777, 778, 779, 3503, 8216, 6951, 6954, 6957 };
		for (int i : ids) {
			addHandler(i, NPC_TYPE, this);
		}
		return this;
	}

	@Override
	public boolean handle(NodeUsageEvent event) {
		final Familiar f = (Familiar) event.getUsedWith();
		if (!(f instanceof Pet)) {
			return false;
		}
		event.getPlayer().getFamiliarManager().eat(event.getUsedItem().getId(), (Pet) f);
		return true;
	}

}
