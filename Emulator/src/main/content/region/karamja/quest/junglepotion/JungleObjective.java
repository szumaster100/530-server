package content.region.karamja.quest.junglepotion;

import content.global.skill.herblore.herbs.Herbs;
import core.game.node.entity.player.Player;
import core.game.node.scenery.Scenery;
import core.game.node.scenery.SceneryBuilder;
import core.game.system.task.Pulse;
import core.game.world.update.flag.context.Animation;
import core.tools.RandomFunction;
import core.tools.StringUtils;

import static core.api.ContentAPIKt.sendMessage;

public enum JungleObjective {
    JUNGLE_VINE(2575, Herbs.SNAKE_WEED, 10, "It grows near vines in an area to the south west where", "the ground turns soft and the water kisses your feet.") {
        @Override
        public void search(final Player player, final Scenery scenery) {
            final Animation animation = Animation.create(2094);
            player.animate(animation);
            player.getPulseManager().run(new Pulse(animation.getDefinition().getDurationTicks(), player, scenery) {
                @Override
                public boolean pulse() {
                    boolean success = RandomFunction.random(3) == 1;
                    if (success) {
                        sendMessage(player, "You search the vine...");
                        switchObject(scenery);
                        findHerb(player);
                        return true;
                    }
                    player.animate(animation);
                    return false;
                }
            });
        }
    },
    PALM_TREE(2577, Herbs.ARDRIGAL, 20, "You are looking for Ardrigal. It is related to the palm", "and grows in its brothers shady profusion."),
    SITO_FOIL(2579, Herbs.SITO_FOIL, 30, "You are looking for Sito Foil, and it grows best where", "the ground has been blackened by the living flame."),
    VOLENCIA_MOSS(2581, Herbs.VOLENCIA_MOSS, 40, "You are looking for Volencia Moss. It clings to rocks", "for its existence. It is difficult to see, so you must", "search for it well."),
    ROGUES_PURSE(32106, Herbs.ROGUES_PUSE, 50, "It inhabits the darkness of the underground, and grows", "in the caverns to the north. A secret entrance to the", "caverns is set into the northern cliffs, be careful Bwana.") {
        @Override
        public void search(final Player player, final Scenery scenery) {
            final Animation animation = Animation.create(2097);
            player.animate(animation);
            player.getPulseManager().run(new Pulse(animation.getDefinition().getDurationTicks(), player, scenery) {
                @Override
                public boolean pulse() {
                    boolean success = RandomFunction.random(4) == 1;
                    if (success) {
                        switchObject(scenery);
                        findHerb(player);
                        return true;
                    }
                    player.animate(animation, 1);
                    return false;
                }
            });
        }
    };

    private final int objectId;

    private final Herbs herb;

    private final int stage;

    private final String[] clue;

    JungleObjective(int objectId, Herbs herb, int stage, final String... clue) {
        this.objectId = objectId;
        this.herb = herb;
        this.stage = stage;
        this.clue = clue;
    }

    public void search(final Player player, final Scenery scenery) {
        findHerb(player);
        switchObject(scenery);
    }

    public void switchObject(Scenery scenery) {
        if (scenery.isActive()) {
            SceneryBuilder.replace(scenery, scenery.transform(scenery.getId() + 1), 80);
        }
    }

    public void findHerb(final Player player) {
        player.getInventory().add(getHerb().herb);
        player.getDialogueInterpreter().sendItemMessage(getHerb().herb, "You find a grimy herb.");
    }

    public static JungleObjective forStage(int stage) {
        for (JungleObjective o : values()) {
            if (o.getStage() == stage) {
                return o;
            }
        }
        return null;
    }

    public static JungleObjective forId(int objectId) {
        for (JungleObjective s : values()) {
            if (s.getObjectId() == objectId) {
                return s;
            }
        }
        return null;
    }

    public String getName() {
        return StringUtils.formatDisplayName(herb.product.getName().replace("Clean", "").trim());
    }

    public int getObjectId() {
        return objectId;
    }

    public Herbs getHerb() {
        return herb;
    }

    public int getStage() {
        return stage;
    }

    public String[] getClue() {
        return clue;
    }
}