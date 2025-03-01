package content.global.skill.runecrafting.scenery;

import content.global.skill.runecrafting.items.Talisman;
import content.global.skill.runecrafting.items.Tiara;
import core.game.node.scenery.Scenery;
import core.game.world.map.Location;

public enum MysteriousRuins {
    AIR(    new int[]{2452, 7103, 7104}, Location.create(2983, 3292, 0), Location.create(2841, 4829, 0), Talisman.AIR, Tiara.AIR),
    MIND(   new int[]{2453, 7105, 7106}, Location.create(2980, 3514, 0), Location.create(2793, 4828, 0), Talisman.MIND, Tiara.MIND),
    WATER(  new int[]{2454, 7107, 7108}, Location.create(3184, 3163, 0), Location.create(3494, 4832, 0), Talisman.WATER, Tiara.WATER),
    EARTH(  new int[]{2455, 7109, 7110}, Location.create(3304, 3475, 0), Location.create(2655, 4830, 0), Talisman.EARTH, Tiara.EARTH),
    FIRE(   new int[]{2456, 7111, 7112}, Location.create(3312, 3253, 0), Location.create(2577, 4846, 0), Talisman.FIRE, Tiara.FIRE),
    BODY(   new int[]{2457, 7113, 7114}, Location.create(3052, 3443, 0), Location.create(2521, 4834, 0), Talisman.BODY, Tiara.BODY),
    COSMIC( new int[]{2458, 7115, 7116}, Location.create(2407, 4375, 0), Location.create(2162, 4833, 0), Talisman.COSMIC, Tiara.COSMIC),
    CHAOS(  new int[]{2461, 7121, 7122}, Location.create(3059, 3589, 0), Location.create(2281, 4837, 0), Talisman.CHAOS, Tiara.CHAOS),
    NATURE( new int[]{2460, 7119, 7120}, Location.create(2869, 3021, 0), Location.create(2400, 4835, 0), Talisman.NATURE, Tiara.NATURE),
    LAW(    new int[]{2459, 7117, 7118}, Location.create(2857, 3379, 0), Location.create(2464, 4818, 0), Talisman.LAW, Tiara.LAW),
    DEATH(  new int[]{2462, 7123, 7124}, Location.create(1862, 4639, 0), Location.create(2208, 4830, 0), Talisman.DEATH, Tiara.DEATH),
    BLOOD(  new int[]{2464, 30529, 30530}, Location.create(3561, 9779, 0), Location.create(2467, 4889, 1), Talisman.BLOOD, Tiara.BLOOD);

    private final int[] object;

    private final Location base;

    private final Location end;

    private final Talisman talisman;

    private final Tiara tiara;

    MysteriousRuins(int[] object, Location base, Location end, final Talisman talisman, final Tiara tiara) {
        this.object = object;
        this.base = base;
        this.end = end;
        this.talisman = talisman;
        this.tiara = tiara;
    }

    public int[] getObject() {
        return object;
    }

    public Location getBase() {
        return base;
    }

    public Location getEnd() {
        return end;
    }

    public Talisman getTalisman() {
        for (Talisman talisman : Talisman.values()) {
            if (talisman.name().equals(name())) {
                return talisman;
            }
        }
        return talisman;
    }

    public Tiara getTiara() {
        for (Tiara tiara : Tiara.values()) {
            if (tiara.name().equals(name())) {
                return tiara;
            }
        }
        return tiara;
    }

    public static MysteriousRuins forObject(final Scenery scenery) {
        for (MysteriousRuins ruin : values()) {
            for (int i : ruin.getObject()) {
                if (i == scenery.getId()) {
                    return ruin;
                }
            }
        }
        return null;
    }

    public static MysteriousRuins forTalisman(final Talisman talisman) {
        for (MysteriousRuins ruin : values()) {
            if (ruin.getTalisman() == talisman) {
                return ruin;
            }
        }
        return null;
    }

}
