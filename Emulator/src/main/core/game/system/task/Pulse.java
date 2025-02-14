package core.game.system.task;

import core.game.node.Node;

public abstract class Pulse implements Runnable {

    public boolean running = true;

    private int delay;

    int ticksPassed;

    protected Node[] checks;

    public Pulse() {
        this(1);
    }

    public Pulse(int delay) {
        this.delay = delay;
    }

    public Pulse(int delay, Node... checks) {
        this.delay = delay;
        this.checks = checks;
    }

    @Override
    public void run() {
        if (update()) {
            //GameWorld.TASKS.remove(this);
        }
    }

    public boolean update() {
        if (hasInactiveNode()) {
            stop();
            return true;
        }
        if (!isRunning()) {
            return true;
        }
        if (++ticksPassed >= delay) {
            ticksPassed = 0;
            try {
                if (pulse()) {
                    stop();
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                stop();
                return true;
            }
            return !isRunning();
        }
        return false;
    }

    public boolean hasInactiveNode() {
        if (checks != null) {
            int size = checks.length;
            for (int i = 0; i < size; i++) {
                Node n = checks[i];
                if (n != null && !n.isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract boolean pulse();

    public boolean removeFor(String pulse) {
        return true;
    }

    public void addNodeCheck(int index, Node n) {
        checks[index] = n;
    }

    public Node getNodeCheck(int index) {
        return checks[index];
    }

    public void stop() {
        running = false;
    }

    public void start() {
        running = true;
    }

    public void restart() {
        ticksPassed = 0;
    }

    public boolean isRunning() {
        return running;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTicksPassed(int ticks) {
        this.ticksPassed = ticks;
    }

    public int getTicksPassed() {
        return ticksPassed;
    }
}