package core.game.world.map.zone;

public enum ZoneType {

    DEFAULT(0),

    SAFE(1),

    P_O_H(2),

    CASTLE_WARS(3),

    TROUBLE_BREWING(4),

    BARBARIAN_ASSAULT(5);

    private final int id;

    ZoneType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
