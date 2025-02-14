package core.game.interaction;

import core.cache.def.impl.ItemDefinition;
import core.cache.def.impl.NPCDefinition;
import core.cache.def.impl.SceneryDefinition;
import core.game.node.Node;
import core.game.node.entity.npc.NPC;
import core.game.node.item.Item;
import core.game.node.scenery.Scenery;

public final class Option {

    public static final Option _P_ATTACK = new Option("Attack", 0);

    public static final Option _P_FOLLOW = new Option("Follow", 2);

    public static final Option _P_TRADE = new Option("Trade with", 3);

    public static final Option _P_GIVETO = new Option("Give-to", 3);

    public static final Option _P_PICKPOCKET = new Option("Pickpocket", 4);

    public static final Option _P_EXAMINE = new Option("Examine", 7);

    public static final Option _P_ASSIST = new Option("Req Assist", 6);

    public static final Option NULL = new Option("null", 0);

    private final String name;

    private final int index;

    private OptionHandler handler;

    public Option(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static OptionHandler defaultHandler(Node node, int nodeId, String name) {
        name = name.toLowerCase();
        if (node instanceof NPC) {
            return NPCDefinition.getOptionHandler(nodeId, name);
        }
        if (node instanceof Scenery) {
            return SceneryDefinition.getOptionHandler(nodeId, name);
        }
        if (node instanceof Item) {
            return ItemDefinition.getOptionHandler(nodeId, name);
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public OptionHandler getHandler() {
        return handler;
    }

    public Option setHandler(OptionHandler handler) {
        this.handler = handler;
        return this;
    }
}
