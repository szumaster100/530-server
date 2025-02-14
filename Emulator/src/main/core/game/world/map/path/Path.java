package core.game.world.map.path;

import core.game.node.entity.Entity;
import core.game.world.map.Point;

import java.util.ArrayDeque;
import java.util.Deque;

public class Path {

    private boolean succesful;

    private boolean moveNear;

    private Deque<Point> points = new ArrayDeque<Point>();

    public Path() {
        // Empty constructor to initialize the path object
    }

    public void walk(Entity entity) {
        if (entity.getLocks().isMovementLocked()) {
            return; // If the entity is locked, it cannot move
        }
        entity.getWalkingQueue().reset(); // Reset walking queue before adding new steps
        for (Point step : points) {
            entity.getWalkingQueue().addPath(step.getX(), step.getY()); // Add each step to the walking queue
        }
    }

    public boolean isSuccessful() {
        return succesful;
    }

    public void setSuccessful(boolean successful) {
        this.succesful = successful;
    }

    public Deque<Point> getPoints() {
        return points;
    }

    public void setPoints(Deque<Point> points) {
        this.points = points;
    }

    public boolean isMoveNear() {
        return moveNear;
    }

    public void setMoveNear(boolean moveNear) {
        this.moveNear = moveNear;
    }
}
