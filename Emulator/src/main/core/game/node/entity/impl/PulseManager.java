package core.game.node.entity.impl;

import core.game.interaction.MovementPulse;
import core.game.node.entity.Entity;
import core.game.node.entity.combat.CombatPulse;
import core.game.node.entity.combat.DeathTask;
import core.game.node.entity.player.Player;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;

import java.util.ArrayList;
import java.util.HashMap;

public final class PulseManager {

    private final HashMap<PulseType, Pulse> currentPulses = new HashMap<>();

    public void run(Pulse pulse) {
        run(pulse, PulseType.STANDARD);
    }

    public void run(Pulse pulse, PulseType pulseType) {
        ArrayList<PulseType> toRemove = new ArrayList<>(currentPulses.size());
        currentPulses.forEach((key, value) -> {
            if (value != null && !value.isRunning()) {
                toRemove.add(key);
            }
        });
        for (PulseType t : toRemove) currentPulses.remove(t);

        if (currentPulses.get(PulseType.STRONG) != null) {

            return;
        }

        if (!clear(pulseType)) {
            return;
        }

        if (pulseType == PulseType.STRONG) {
            clear();
        }

        currentPulses.put(pulseType, pulse);
        pulse.start();
        if (pulse.isRunning()) {
            GameWorld.getPulser().submit(pulse);
        }
    }

    public void clear() {
        currentPulses.forEach((type, pulse) -> {
            if (type != PulseType.STRONG && pulse != null) pulse.stop();
        });
    }

    public boolean clear(PulseType pulseType) {
        Pulse pulse = currentPulses.get(pulseType);

        if (pulse != null) {
            pulse.stop();
            currentPulses.remove(pulseType);
        }
        return true;
    }

    public Pulse runUnhandledAction(final Player player, PulseType pulseType) {
        Pulse pulse = new Pulse(1, player) {
            @Override
            public boolean pulse() {
                player.getPacketDispatch().sendMessage("Nothing interesting happens.");
                return true;
            }
        };
        run(pulse, pulseType);
        return pulse;
    }

    public boolean isMovingPulse() {
        if (!hasPulseRunning()) {
            return false;
        }

        Pulse current = getCurrent();
        return current instanceof MovementPulse || current instanceof CombatPulse;
    }

    public boolean hasPulseRunning() {
        return getCurrent() != null && getCurrent().isRunning();
    }

    public static void cancelDeathTask(Entity e) {
        if (!DeathTask.isDead(e) || e.getPulseManager().getCurrent() == null) {
            return;
        }
        e.getPulseManager().getCurrent().stop();
    }

    public Pulse getCurrent() {
        PulseType[] types = PulseType.values();
        for (PulseType type : types) {
            if (currentPulses.get(type) != null) {
                return currentPulses.get(type);
            }
        }
        return null;
    }

}

