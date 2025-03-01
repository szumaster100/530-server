package content.global.skill.runecrafting.runes;

import content.global.skill.runecrafting.items.Talisman;
import content.global.skill.runecrafting.scenery.Altar;
import core.game.node.item.Item;
import org.rs.consts.Items;

public enum CombinationRune {
    MIST(   new Item(Items.MIST_RUNE_4695), 6, 8.0, new Altar[]{Altar.WATER, Altar.AIR}, Rune.AIR, Rune.WATER),
    DUST(   new Item(Items.DUST_RUNE_4696), 10, 8.3, new Altar[]{Altar.EARTH, Altar.AIR}, Rune.AIR, Rune.EARTH),
    MUD(    new Item(Items.MUD_RUNE_4698), 13, 9.3, new Altar[]{Altar.EARTH, Altar.WATER}, Rune.WATER, Rune.EARTH),
    SMOKE(  new Item(Items.SMOKE_RUNE_4697), 15, 8.5, new Altar[]{Altar.FIRE, Altar.AIR}, Rune.AIR, Rune.FIRE),
    STEAM(  new Item(Items.STEAM_RUNE_4694), 19, 9.3, new Altar[]{Altar.WATER, Altar.FIRE}, Rune.WATER, Rune.FIRE),
    LAVA(   new Item(Items.LAVA_RUNE_4699), 23, 10.0, new Altar[]{Altar.FIRE, Altar.EARTH}, Rune.EARTH, Rune.FIRE);

    private final Item rune;
    private final int level;
    private final double experience;
    private final Altar[] altars;
    private final Rune[] runes;

    CombinationRune(final Item rune, final int level, final double experience, final Altar[] altars, final Rune... runes) {
        this.rune = rune;
        this.level = level;
        this.experience = experience;
        this.altars = altars;
        this.runes = runes;
    }

    public Item getRune() {
        return rune;
    }

    public int getLevel() {
        return level;
    }

    public double getExperience() {
        return experience;
    }

    public Rune[] getRunes() {
        return runes;
    }

    public double getHighExperience() {
        return (experience % 1 == 0) ? experience + 5 : experience + 8;
    }

    public Altar[] getAltars() {
        return altars;
    }

    public static CombinationRune forAltar(final Altar altar, final Item item) {
        for (var rune : values()) {
            for (var alt : rune.getAltars()) {
                if (alt == altar) {
                    String altarElement = alt.name();
                    String talismanElement = item.getName().contains("talisman")
                        ? Talisman.forItem(item).name()
                        : Rune.forItem(item).name();

                    if (altarElement.equals(talismanElement)) {
                        continue;
                    }

                    for (var r : rune.getRunes()) {
                        if (r.name().equals(talismanElement)) {
                            return rune;
                        }
                    }
                }
            }
        }
        return null;
    }
}
