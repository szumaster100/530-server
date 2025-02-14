package core.game.node.entity.impl;

import core.game.interaction.Clocks;
import core.game.node.entity.Entity;
import core.game.world.GameWorld;
import core.game.world.update.flag.EntityFlag;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;

public final class Animator {

    public static final Animation RESET_A = new Animation(-1);

    public static final Graphics RESET_G = new Graphics(-1);

    private Entity entity;

    private Animation animation;

    private Graphics graphics;

    private Priority priority = Priority.LOW;

    private int ticks;

    public Animator(Entity entity) {
        this.entity = entity;
    }

    public static enum Priority {

        LOW,

        MID,

        HIGH,

        VERY_HIGH;
    }

    public boolean animate(Animation animation) {
        return animate(animation, null);
    }

    public boolean graphics(Graphics graphics) {
        return animate(null, graphics);
    }

    public boolean animate(Animation animation, Graphics graphics) {
        if (animation != null) {
            if (ticks > GameWorld.getTicks() && priority.ordinal() > animation.getPriority().ordinal()) {
                return false;
            }
            if (animation.getId() == 0) {
                animation.setId(-1);
            }
            this.animation = animation;
            if (animation.getId() != -1) {
                ticks = GameWorld.getTicks() + animation.getDuration();
            } else {
                ticks = 0;
            }
            entity.clocks[Clocks.getANIMATION_END()] = ticks;
            entity.getUpdateMasks().register(EntityFlag.Animate, animation);
            priority = animation.getPriority();
        }
        if (graphics != null) {
            this.graphics = graphics;
            entity.getUpdateMasks().register(EntityFlag.SpotAnim, graphics);
        }
        return true;
    }

    public void stop() {
        animate(RESET_A);
    }

    public void forceAnimation(Animation animation) {
        ticks = -1;
        animate(animation);
        priority = Priority.HIGH;
    }

    public void reset() {
        animate(RESET_A);
        entity.clocks[Clocks.getANIMATION_END()] = 0;
        ticks = 0;
    }

    public boolean isAnimating() {
        return animation != null && animation.getId() != -1 && ticks > GameWorld.getTicks();
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
