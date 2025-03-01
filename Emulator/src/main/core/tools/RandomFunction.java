package core.tools;

import core.game.node.entity.npc.drop.DropFrequency;
import core.game.node.item.ChanceItem;
import core.game.node.item.Item;
import core.game.node.item.WeightedChanceItem;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RandomFunction {

    public static final Random RANDOM = new Random();

    public static final double random(double a, double b) {
        final double min = Math.min(a, b);
        final double max = Math.max(a, b);
        return min + (max - min) * RANDOM.nextDouble();
    }

    public static final int random(int a, int b) {
        final int n = Math.abs(b - a);
        return Math.min(a, b) + (n == 0 ? 0 : random(n));
    }

    public static boolean roll(int chance) {
        if (chance <= 1) return true;
        return random(chance + 1) == chance / 2;
    }

    public static double getSkillSuccessChance(double low, double high, int level) {
        // 99 & 98 numbers should *not* be adjusted for level cap > 99
        int value = (int) (Math.floor(low * ((99 - level) / 98.0)) + Math.floor(high * ((level - 1) / 98.0)) + 1);
        return Math.min(Math.max(value / 256D, 0), 1) * 100.0;
    }

    public static int randomSign(int value) {
        return RANDOM.nextBoolean() ? value : -value;
    }

    public static final int getRandom(int maxValue) {
        return (int) (Math.random() * (maxValue + 1));
    }

    public static final double getRandomDouble(double maxValue) {
        return (Math.random() * (maxValue + 1));
    }

    public static final int random(int maxValue) {
        if (maxValue <= 0) {
            return 0;
        }
        return RANDOM.nextInt(maxValue);
    }

    public static final double randomDouble(double maxValue) {
        return ThreadLocalRandom.current().nextDouble(0.0, maxValue);
    }

    public static final double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static int nextInt(int val) {
        return random(val);
    }

    public static int normalRandDist(int max, int intensity) {
        int sum = 0;
        for (int j = 0; j < intensity; j++) {
            sum += RANDOM.nextInt(max);
        }
        return sum / intensity;
    }

    public static int normalRandDist(int max) {
        return (RANDOM.nextInt(max) + RANDOM.nextInt(max)) / 2;
    }

    public static int linearDecreaseRand(int max) {
        double seed = RANDOM.nextDouble();
        double modifier = RANDOM.nextDouble();
        return (int) (seed * modifier * max);
    }

    public static int normalPlusWeightRandDist(int val, int weight) {
        int normalDistRand = (RANDOM.nextInt(val) + RANDOM.nextInt(val)) / 2;
        return normalDistRand / 2 + weight < val ? normalDistRand / 2 + weight : normalPlusWeightRandDist(val, weight - 1);
    }

    public static final ChanceItem getChanceItem(final ChanceItem[] items) {
        double total = 0;
        for (ChanceItem i : items) {
            total += i.chanceRate;
        }
        final int random = random((int) total);
        double subTotal = 0;
        List<ChanceItem> choices = new ArrayList<>(20);
        for (ChanceItem item : items) {
            choices.add(item);
        }
        Collections.shuffle(choices);
        for (ChanceItem i : choices) {
            subTotal += i.chanceRate;
            if (random < subTotal) {
                return i;
            }
        }
        return null;
    }

    public static List<Item> rollChanceTable(boolean atLeastOne, List<ChanceItem> table) {
        final List<Item> rewards = new ArrayList<>(20);
        final List<Item> always_rewards = new ArrayList<>(20);
        final List<ChanceItem> chanceTable = new ArrayList<ChanceItem>(table);
        boolean isAllAlways = false;
        if (table.stream().filter(item -> item.chanceRate == 1).count() == table.size()) {
            isAllAlways = true;
        }
        if (table.size() == 1) {
            atLeastOne = false;
        }
        if (!isAllAlways) {
            if (atLeastOne) {
                while (rewards.isEmpty()) {
                    Collections.shuffle(chanceTable);
                    for (ChanceItem item : chanceTable) {
                        if (item.chanceRate == 0.0) {
                            item.chanceRate = DropFrequency.rate(item.dropFrequency);
                        }
                        boolean roll = RandomFunction.random(1, (int) item.chanceRate) == 1;
                        if (item.chanceRate != 1) {
                            if (roll) {
                                rewards.add(item.getRandomItem());
                                break;
                            }
                        }
                    }
                }
            } else {
                Collections.shuffle(chanceTable);
                for (ChanceItem item : chanceTable) {
                    if (item.chanceRate == 0.0) {
                        item.chanceRate = DropFrequency.rate(item.dropFrequency);
                    }
                    boolean roll = RandomFunction.random(1, (int) item.chanceRate) == 1;
                    if (item.chanceRate != 1) {
                        if (roll) {
                            rewards.add(item.getRandomItem());
                            break;
                        }
                    }
                }
            }
        }
        table.stream().filter(item -> item.chanceRate == 1).forEach(item -> {
            if (item.chanceRate == 0.0) {
                item.chanceRate = DropFrequency.rate(item.dropFrequency);
            }
            always_rewards.add(item.getRandomItem());
        });
        return Stream.concat(rewards.stream(), always_rewards.stream()).collect(Collectors.toList());
    }

    public static List<Item> rollChanceTable(boolean atLeastOne, ChanceItem... table) {
        return rollChanceTable(atLeastOne, Arrays.asList(table));
    }

    public static Item rollWeightedChanceTable(WeightedChanceItem... table) {
        return rollWeightedChanceTable(new ArrayList<>(Arrays.asList(table)));
    }

    public static Item rollWeightedChanceTable(List<WeightedChanceItem> table) {
        int sumOfWeights = table.stream().mapToInt(item -> item.getWeight()).sum();
        int rand = random(sumOfWeights);
        Collections.shuffle(table);
        for (WeightedChanceItem item : table) {
            if (rand <= item.getWeight())
                return item.getItem();
            rand -= item.getWeight();
        }
        //We should get here if and only if the weighted chance table is empty.

        return null;
    }

    public static <T> T getRandomElement(T[] array) {
        return (T) array[randomize(array.length)];
    }

    public static int randomize(int value) {
        if (value < 1) {
            return 0;
        }
        return RANDOM.nextInt(value);
    }

    public static boolean nextBool() {
        return RANDOM.nextBoolean();
    }
}
