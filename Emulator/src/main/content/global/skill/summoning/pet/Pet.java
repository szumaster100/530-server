package content.global.skill.summoning.pet;

import content.global.skill.summoning.familiar.Familiar;
import content.global.skill.summoning.familiar.FamiliarSpecial;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.world.GameWorld;

import static core.api.ContentAPIKt.setVarp;

public final class Pet extends Familiar {

    private final int itemId;

    private final PetDetails details;

    private double growthRate;

    private final Pets pet;

    private int hasWarned = 0;

    public Pet(Player owner, final PetDetails details, int itemId, int id) {
        super(owner, id, -1, -1, -1);
        this.pet = Pets.forId(itemId);
        this.details = details;
        this.itemId = itemId;
        this.growthRate = pet.getGrowthRate();
    }

    @Override
    public void sendConfiguration() {
        setVarp(owner, 1175, ((int) details.getGrowth() << 1) | ((int) details.getHunger() << 9));
        setVarp(owner, 1174, getId());
        setVarp(owner, 448, itemId);
    }

    @Override
    public void handleTickActions() {
        final PetDetails petDetails = details;
        double hunger = petDetails.getHunger();
        if (hunger >= 75.0 && hunger <= 90.0 && hasWarned < 1) {
            owner.sendMessage("<col=ff0000>Your pet is getting hungry.</col>");
            hasWarned = 1;
        } else if (hunger >= 90.0 && hasWarned < 2) {
            owner.getPacketDispatch().sendMessage("<col=ff0000>Your pet is starving, feed it before it runs off.</col>");
            hasWarned = 2;
        }
        if (hunger >= 100.0 && growthRate != 0 && pet.getFood().length != 0) {
            owner.getFamiliarManager().removeDetails(this.getItemIdHash());
            owner.getFamiliarManager().dismiss();
            owner.getFamiliarManager().setFamiliar(null);
            setVarp(owner, 1175, 0);
            owner.sendMessage("<col=ff0000>Your pet has run away.</col>");
            return;
        }
        double growth = petDetails.getGrowth();
        double growthrate = pet.getGrowthRate();
        if (growthrate > 0.000) {
            if (GameWorld.getSettings().isDevMode()) {
                growthrate *= 100;
            }
            petDetails.updateGrowth(growthrate);
            if (growth == 100.0) {
                growNextStage();
            }
        }
        if ((!isInvisible() && owner.getLocation().getDistance(getLocation()) > 12) || (isInvisible() && ticks % 25 == 0)) {
            if (!call()) {
                setInvisible(true);
            }
        } else if (!getPulseManager().hasPulseRunning()) {
            startFollowing();
        }
        setVarp(owner, 1175, ((int) details.getGrowth() << 1) | ((int) details.getHunger() << 9));
    }

    public void growNextStage() {
        if (pet == null) {
            return;
        }
        int newItemId = pet.getNextStageItemId(itemId);
        if (newItemId == -1) {
            return;
        }
        if (pet.isKitten(itemId)) {
            owner.incrementAttribute("/save:stats_manager:cats_raised");
        }
        owner.getFamiliarManager().removeDetails(this.getItemIdHash());
        owner.getFamiliarManager().dismiss();
        owner.getPacketDispatch().sendMessage("<col=ff0000>Your pet has grown larger.</col>");
        int npcId = pet.getNpcId(newItemId);
        details.updateGrowth(-100.0);
        Pet newPet = new Pet(owner, details, newItemId, npcId);
        newPet.growthRate = growthRate;
        newPet.hasWarned = hasWarned;
        owner.getFamiliarManager().setFamiliar(newPet);
        owner.getFamiliarManager().spawnFamiliar();
    }

    @Override
    public Familiar construct(Player owner, int id) {
        return this;
    }

    @Override
    protected boolean specialMove(FamiliarSpecial special) {
        return false;
    }

    @Override
    public boolean isCombatFamiliar() {
        return false;
    }

    public int getItemId() {
        return itemId;
    }

    public int getItemIdHash() {
        Item item = new Item(itemId);
        item.setCharge(details.getIndividual());
        return item.getIdHash();
    }

    public PetDetails getDetails() {
        return details;
    }

    public Pets getPet() {
        return pet;
    }


    @Override
    public int[] getIds() {
        return new int[]{761, 762, 763, 764, 765, 766, 3505, 3598, 6969, 7259, 7260, 6964, 7249, 7251, 6960, 7241, 7243, 6962, 7245, 7247, 6966, 7253, 7255, 6958, 7237, 7239, 6915, 7277, 7278, 7279, 7280, 7018, 7019, 7020, 6908, 7313, 7316, 6947, 7293, 7295, 7297, 7299, 6911, 7261, 7263, 7265, 7267, 7269, 6919, 7301, 7303, 7305, 7307, 6949, 6952, 6955, 6913, 7271, 7273, 6945, 7319, 7321, 7323, 7325, 7327, 6922, 6942, 7210, 7212, 7214, 7216, 7218, 7220, 7222, 7224, 7226, 6900, 6902, 6904, 6906, 768, 769, 770, 771, 772, 773, 3504, 6968, 7257, 7258, 6965, 7250, 7252, 6961, 7242, 7244, 6963, 7246, 7248, 6967, 7254, 7256, 6859, 7238, 7240, 6916, 7281, 7282, 7283, 7284, 7015, 7016, 7017, 6909, 7314, 7317, 6948, 7294, 7296, 7298, 7300, 6912, 7262, 7264, 7266, 7268, 7270, 6920, 7302, 7304, 7306, 7308, 6950, 6953, 6956, 6914, 7272, 7274, 6946, 7320, 7322, 7324, 7326, 7328, 6923, 6943, 7211, 7213, 7215, 7217, 7219, 7221, 7223, 7225, 7227, 6901, 6903, 6905, 6907, 774, 775, 776, 777, 778, 779, 3503, 6951, 6954, 6957};
    }

}
