package core.game.node.entity.npc.agg;

import core.ServerConstants;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.DeathTask;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.entity.player.info.Rights;
import core.game.world.GameWorld;
import core.tools.RandomFunction;

public final class AggressiveHandler {
    private final Entity entity;
    private int radius = 4;
    private int pauseTicks = 0;
    private boolean targetSwitching;
    private final AggressiveBehavior behavior;
    private int chanceRatio = 2;
    private final int[] playerTolerance = new int[ServerConstants.MAX_PLAYERS];
    private boolean allowTolerance = true;

    public AggressiveHandler(Entity entity, AggressiveBehavior behavior) {
        this.entity = entity;
        this.behavior = behavior;
    }

    public boolean selectTarget() {
        if (pauseTicks > GameWorld.getTicks() || entity.getLocks().isInteractionLocked()) {
            return false;
        }
        if ((!targetSwitching && entity.getProperties().getCombatPulse().isAttacking()) || DeathTask.isDead(entity)) {
            return false;
        }
        if (RandomFunction.randomize(10) > chanceRatio) {
            return false;
        }
        Entity target = behavior.getLogicalTarget(entity, behavior.getPossibleTargets(entity, radius));
        if (target instanceof Player) {
            if (target.getAttribute("ignore_aggression", false)) {
                return false;
            }
            if (((Player) target).getRights().equals(Rights.ADMINISTRATOR) && !target.getAttribute("allow_admin_aggression", false)) {
                return false;
            }
        }
        if (target != null) {
            target.setAttribute("aggressor", entity);
            if (entity.getProperties().getCombatPulse().isAttacking()) {
                entity.getProperties().getCombatPulse().setVictim(target);
                entity.face(target);
            } else {
                entity.getProperties().getCombatPulse().attack(target);
            }
            return true;
        }
        return entity.getProperties().getCombatPulse().isAttacking();
    }

    public synchronized void removeTolerance(int index) {
        playerTolerance[index] = 0;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getPauseTicks() {
        return pauseTicks;
    }

    public void setPauseTicks(int pauseTicks) {
        this.pauseTicks = GameWorld.getTicks() + pauseTicks;
    }

    public int[] getPlayerTolerance() {
        return playerTolerance;
    }

    public boolean isTargetSwitching() {
        return targetSwitching;
    }

    public void setTargetSwitching(boolean targetSwitching) {
        this.targetSwitching = targetSwitching;
    }

    public int getChanceRatio() {
        return chanceRatio;
    }

    public void setChanceRatio(int chanceRatio) {
        this.chanceRatio = chanceRatio;
    }

    public boolean isAllowTolerance() {
        boolean configSetting = true;
        if (entity instanceof NPC) {
            configSetting = ((NPC) entity).getDefinition().getConfiguration("can_tolerate", true);
        }
        return allowTolerance && configSetting;
    }

    public void setAllowTolerance(boolean allowTolerance) {
        this.allowTolerance = allowTolerance;
    }

}
