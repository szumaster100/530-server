package core.game.node.entity.skill;

import content.data.items.SkillingTool;
import content.global.skill.gathering.SkillingResource;
import core.game.node.Node;
import core.game.node.entity.impl.Animator.Priority;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.update.flag.context.Animation;

public abstract class SkillPulse<T extends Node> extends Pulse {

    protected final Player player;

    protected T node;

    protected SkillingTool tool;

    protected SkillingResource resource;

    protected boolean resetAnimation = true;

    public SkillPulse(Player player, T node) {
        super(1, player, node);
        this.player = player;
        this.node = node;
        super.stop();
    }

    @Override
    public void start() {
        if (checkRequirements()) {
            super.start();
            message(0);
        }
    }

    @Override
    public boolean pulse() {
        if (!checkRequirements()) {
            return true;
        }
        animate();
        return reward();
    }

    @Override
    public void stop() {
        if (resetAnimation) {
            player.animate(new Animation(-1, Priority.HIGH));
        }
        super.stop();
        message(1);
    }

    public abstract boolean checkRequirements();

    public abstract void animate();

    public abstract boolean reward();

    public void message(int type) {

    }
}
