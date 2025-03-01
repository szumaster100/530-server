package core.game.system.task;

import core.game.node.Node;

public abstract class NodeTask {

    private final int ticks;

    private Pulse pulse;

    public NodeTask() {
        this(-1);
    }

    public NodeTask(int ticks) {
        this.ticks = ticks;
    }

    public void start(Node node, Node... n) {

    }

    public abstract boolean exec(Node node, Node... n);

    public void stop(Node node, Node... n) {

    }

    public boolean removeFor(String s, Node node, Node... n) {
        return true;
    }

    public Pulse schedule(final Node node, final Node... n) {
        pulse = new Pulse(ticks, node) {

            @Override
            public void start() {
                super.start();
                NodeTask.this.start(node, n);
            }

            @Override
            public boolean pulse() {
                return exec(node, n);
            }

            @Override
            public void stop() {
                super.stop();
                NodeTask.this.stop(node, n);
            }

            @Override
            public boolean removeFor(String s) {
                return NodeTask.this.removeFor(s, node, n);
            }
        };
        pulse.start();
        return pulse;
    }

    public Pulse getPulse() {
        return pulse;
    }

    public int getTicks() {
        return ticks;
    }

}