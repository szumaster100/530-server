package core.game.world.map.zone;

import java.util.Objects;

public final class RegionZone {

    private final MapZone zone;

    private final ZoneBorders borders;

    public RegionZone(MapZone zone, ZoneBorders borders) {
        this.zone = zone;
        this.borders = borders;
    }

    public ZoneBorders getBorders() {
        return borders;
    }

    public MapZone getZone() {
        return zone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegionZone that = (RegionZone) o;
        return Objects.equals(zone, that.zone) && Objects.equals(borders, that.borders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(zone, borders);
    }
}
