package core.game.node.entity.impl;

import core.game.node.entity.Entity;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.GroundItem;
import core.game.node.item.Item;
import core.game.world.map.Direction;
import core.game.world.map.Location;
import core.game.world.map.Point;
import core.game.world.map.RegionManager;
import core.tools.Log;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import static core.api.ContentAPIKt.hasTimerActive;
import static core.api.ContentAPIKt.log;

public final class WalkingQueue {

    private final Deque<Point> walkingQueue = new ArrayDeque<Point>();

    private final Entity entity;

    private int walkDir = -1;

    private int runDir = -1;

    private boolean running = false;

    private boolean runDisabled;

    private Location footPrint;

    public ArrayList<GroundItem> routeItems = new ArrayList<GroundItem>();

    public WalkingQueue(Entity entity) {
        this.entity = entity;
        this.footPrint = entity.getLocation();
    }

    public void update() {
        boolean isPlayer = entity instanceof Player;
        this.walkDir = -1;
        this.runDir = -1;
        if (entity.getLocation() == null) {
            return;
        }
        if (updateTeleport()) {
            return;
        }
        if (isPlayer && updateRegion(entity.getLocation(), true)) {
            return;
        }
        if (hasTimerActive(entity, "frozen"))
            return;
        Point point = walkingQueue.poll();
        boolean drawPath = entity.getAttribute("routedraw", false);
        if (point == null) {
            updateRunEnergy(false);
            if (isPlayer && drawPath) {
                for (GroundItem item : routeItems) {
                    if (item != null) {
                        RegionManager.getRegionPlane(item.getLocation()).remove(item);
                    }
                }
                routeItems.clear();
            }
            return;
        }
        if (isPlayer && ((Player) entity).getSettings().getRunEnergy() < 1.0) {
            running = false;
            ((Player) entity).getSettings().setRunToggled(false);
        }
        Point runPoint = null;
        if (point.getDirection() == null) {
            point = walkingQueue.poll();
        }
        int walkDirection = -1;
        int runDirection = -1;
        if (isRunningBoth() && (point == null || !point.isRunDisabled())) {
            runPoint = walkingQueue.poll();
        }
        if (point != null) {
            if (point.getDirection() == null) {
                return;
            }
            walkDirection = point.getDirection().ordinal();
        }
        if (runPoint != null) {
            runDirection = runPoint.getDirection().ordinal();
        }
        int diffX = 0;
        int diffY = 0;
        if (walkDirection != -1) {
            diffX = point.getDiffX();
            diffY = point.getDiffY();
        }
        if (runDirection != -1) {
            footPrint = entity.getLocation().transform(diffX, diffY, 0);
            diffX += runPoint.getDiffX();
            diffY += runPoint.getDiffY();
            updateRunEnergy(true);
        } else {
            updateRunEnergy(false);
        }
        if (diffX != 0 || diffY != 0) {
            Location walk = entity.getLocation();
            if (point != null) {
                walk = walk.transform(point.getDiffX(), point.getDiffY(), 0);
                if (!entity.getZoneMonitor().move(entity.getLocation(), walk)) {
                    reset();

                    return;
                }
            }
            Location dest = entity.getLocation().transform(diffX, diffY, 0);
            if (runPoint != null) {
                if (!entity.getZoneMonitor().move(walk, dest)) {
                    dest = dest.transform(-runPoint.getDiffX(), -runPoint.getDiffY(), 0);
                    runPoint = null;
                    runDirection = -1;
                    reset();

                }
            }
            if (runPoint != null) {
                entity.setDirection(runPoint.getDirection());
            } else if (point != null) {
                entity.setDirection(point.getDirection());
            }
            footPrint = entity.getLocation();
            entity.setLocation(dest);
            RegionManager.move(entity);
        }
        this.walkDir = walkDirection;
        this.runDir = runDirection;
    }

    private double getEnergyDrainRate(Player player) {
        double rate = 0.55;
        if (player.getSettings().getWeight() > 0.0) {
            rate *= 1 + (player.getSettings().getWeight() / 100);
        }
        if (hasTimerActive(player, "hamstrung")) {
            rate *= 4;
        }
        return rate;
    }

    private double getEnergyRestore(Player player) {
        double rate = 100 / ((175 - (player.getSkills().getLevel(Skills.AGILITY))) / 0.6);
        return rate;
    }

    public void updateRunEnergy(boolean decrease) {
        if (!(entity instanceof Player)) {
            return;
        }
        Player p = (Player) entity;
        if (!decrease && p.getSettings().getRunEnergy() >= 100.0) {
            return;
        }
        double drain = decrease ? getEnergyDrainRate(p) : -getEnergyRestore(p);
        p.getSettings().updateRunEnergy(drain);
    }

    public boolean updateTeleport() {
        if (entity.getProperties().getTeleportLocation() != null) {
            reset(false);
            entity.setLocation(entity.getProperties().getTeleportLocation());
            entity.getProperties().setTeleportLocation(null);
            if (entity instanceof Player) {
                Player p = (Player) entity;
                Location last = p.getPlayerFlags().getLastSceneGraph();
                if (last == null) {
                    last = p.getLocation();
                }
                if ((last.getRegionX() - entity.getLocation().getRegionX()) >= 4 || (last.getRegionX() - entity.getLocation().getRegionX()) <= -4) {
                    p.getPlayerFlags().setUpdateSceneGraph(true);
                } else if ((last.getRegionY() - entity.getLocation().getRegionY()) >= 4 || (last.getRegionY() - entity.getLocation().getRegionY()) <= -4) {
                    p.getPlayerFlags().setUpdateSceneGraph(true);
                }
            }
            RegionManager.move(entity);
            footPrint = entity.getLocation();
            entity.getProperties().setTeleporting(true);
            return true;
        }
        return false;
    }

    public boolean updateRegion(Location location, boolean move) {
        Player p = (Player) entity;
        Location lastRegion = p.getPlayerFlags().getLastSceneGraph();
        if (lastRegion == null) {
            lastRegion = location;
        }
        int rx = lastRegion.getRegionX();
        int ry = lastRegion.getRegionY();
        int cx = location.getRegionX();
        int cy = location.getRegionY();
        if ((rx - cx) >= 4) {
            p.getPlayerFlags().setUpdateSceneGraph(true);
        } else if ((rx - cx) <= -4) {
            p.getPlayerFlags().setUpdateSceneGraph(true);
        }
        if ((ry - cy) >= 4) {
            p.getPlayerFlags().setUpdateSceneGraph(true);
        } else if ((ry - cy) <= -4) {
            p.getPlayerFlags().setUpdateSceneGraph(true);
        }
        if (move && p.getPlayerFlags().isUpdateSceneGraph()) {
            RegionManager.move(entity);
            return true;
        }
        return false;
    }

    public void walkBack() {
        entity.getPulseManager().clear();
        reset();
        addPath(footPrint.getX(), footPrint.getY());
    }

    public void addPath(int x, int y) {
        addPath(x, y, runDisabled);
    }

    public void addPath(int x, int y, boolean runDisabled) {
        Point point = walkingQueue.peekLast();
        if (point == null) {
            return;
        }
        boolean drawRoute = entity.getAttribute("routedraw", false);
        if (drawRoute && entity instanceof Player) {
            Player p = (Player) entity;
            GroundItem item = new GroundItem(new Item(13444), Location.create(x, y, p.getLocation().getZ()), p);
            routeItems.add(item);
            RegionManager.getRegionPlane(item.getLocation()).add(item);
        }
        int diffX = x - point.getX(), diffY = y - point.getY();
        int max = Math.max(Math.abs(diffX), Math.abs(diffY));
        for (int i = 0; i < max; i++) {
            if (diffX < 0) {
                diffX++;
            } else if (diffX > 0) {
                diffX--;
            }
            if (diffY < 0) {
                diffY++;
            } else if (diffY > 0) {
                diffY--;
            }
            addPoint(x - diffX, y - diffY, runDisabled);
        }
    }

    public void addPoint(int x, int y, boolean runDisabled) {
        Point point = walkingQueue.peekLast();
        if (point == null) {
            return;
        }
        int diffX = x - point.getX(), diffY = y - point.getY();
        Direction direction = Direction.getDirection(diffX, diffY);
        if (direction != null) {
            walkingQueue.add(new Point(x, y, direction, diffX, diffY, runDisabled));
        }
    }

    public boolean isRunningBoth() {
        if (isRunDisabled()) return false;
        if (entity instanceof Player && ((Player) entity).getSettings().isRunToggled()) {
            return true;
        }
        return running;
    }

    public boolean hasPath() {
        return !walkingQueue.isEmpty();
    }

    public boolean isMoving() {
        return walkDir != -1 || runDir != -1;
    }

    public void reset() {
        reset(running);
    }

    public void reset(boolean running) {
        Location loc = entity.getLocation();

        if (loc == null) {
            log(this.getClass(), Log.ERR,
                "The entity location provided was null."
                    + "Are you sure anything down the stack trace isn't providing an NPC with a null location?"
            );
        }

        walkingQueue.clear();
        walkingQueue.add(new Point(loc.getX(), loc.getY()));
        this.running = running;
    }

    public int getWalkDir() {
        return walkDir;
    }

    public int getRunDir() {
        return runDir;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    public Location getFootPrint() {
        return footPrint;
    }

    public void setFootPrint(Location footPrint) {
        this.footPrint = footPrint;
    }

    public Deque<Point> getQueue() {
        return walkingQueue;
    }

    public boolean isRunDisabled() {
        return runDisabled;
    }

    public void setRunDisabled(boolean runDisabled) {
        this.runDisabled = runDisabled;
    }
}
