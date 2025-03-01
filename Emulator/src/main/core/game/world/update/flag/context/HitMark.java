package core.game.world.update.flag.context;

import core.game.node.entity.Entity;

public class HitMark {

    private final int damage;

    private final int type;

    private int lifepoints;

    private final Entity entity;

    public boolean showHealthBar = true;

    public HitMark(int damage, int type, Entity entity) {
        this.damage = damage;
        this.type = type;
        this.entity = entity;
    }

    public HitMark(int damage, int type, Entity entity, boolean showHealthBar) {
        this.damage = damage;
        this.type = type;
        this.entity = entity;
        this.showHealthBar = showHealthBar;
    }

    public int getDamage() {
        return damage;
    }

    public int getType() {
        return type;
    }

    public Entity getEntity() {
        return entity;
    }

    public int getLifepoints() {
        return lifepoints;
    }

    public void setLifepoints(int lifepoints) {
        this.lifepoints = lifepoints;
    }
}
