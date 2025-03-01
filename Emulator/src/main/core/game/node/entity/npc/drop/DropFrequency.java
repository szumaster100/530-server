package core.game.node.entity.npc.drop;

import java.util.Arrays;
import java.util.HashMap;

public enum DropFrequency {
    ALWAYS,
    COMMON,
    UNCOMMON,
    RARE,
    VERY_RARE;

    static int[] RATES = {1, 5, 15, 30, 60};

    final static HashMap<DropFrequency, Integer> rateMap = new HashMap<>();

    static {
        Arrays.stream(DropFrequency.values()).forEach(freq -> rateMap.putIfAbsent(freq, RATES[freq.ordinal()]));
    }

    public static int rate(DropFrequency frequency) {
        return rateMap.get(frequency);
    }

}