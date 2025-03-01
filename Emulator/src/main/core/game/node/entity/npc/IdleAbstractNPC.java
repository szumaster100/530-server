package core.game.node.entity.npc;

import core.game.node.entity.Entity;
import core.game.node.entity.combat.CombatStyle;
import core.game.node.entity.combat.DeathTask;
import core.game.node.entity.combat.InteractionType;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.world.map.Location;

public abstract class IdleAbstractNPC extends AbstractNPC {

    private int activeId;

    private boolean idle = true;

    protected int timeout = 30;

    public IdleAbstractNPC(int idleId, int activeId, Location location) {
        super(idleId, location);
        this.activeId = activeId;
    }

    @Override
    public void handleTickActions() {
        super.handleTickActions();
        if (!idle && !getProperties().getCombatPulse().isAttacking()) {
            long time = getAttribute("combat-time", 0l);
            if ((System.currentTimeMillis() - time) > (timeout * 600)) {
                goIdle();
            }
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public boolean isAttackable(Entity entity, CombatStyle style, boolean message) {
        if (isInvisible()) {
            return false;
        }
        if (getTask() != null && entity instanceof Player && getTask().levelReq > entity.getSkills().getLevel(Skills.SLAYER)) {
            if (message) {
                ((Player) entity).getPacketDispatch().sendMessage("You need a higher slayer level to know how to wound this monster.");
            }
        }
        if (DeathTask.isDead(this)) {
            return false;
        }
        if (!entity.getZoneMonitor().continueAttack(this, style, message)) {
            return false;
        }
        return true;
    }

    public boolean inDisturbingRange(Entity disturber) {
        if (idle && disturber.getSwingHandler(false).canSwing(disturber, this) != InteractionType.NO_INTERACT) {
            return true;
        }
        return false;
    }

    public boolean canDisturb(Entity disturber) {
        return idle;
    }

    public void disturb(Entity disturber) {
        if (disturber != null) {
            disturber.getProperties().getCombatPulse().setCombatFlags(this);
        } else {
            setAttribute("combat-time", System.currentTimeMillis() + 10000);
        }
        getProperties().getCombatPulse().attack(disturber);
        transform(activeId);
        idle = false;
    }

    public void goIdle() {
        if (idle) {
            return;
        }
        idle = true;
        reTransform();
    }

    public boolean isIdle() {
        return idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

}
