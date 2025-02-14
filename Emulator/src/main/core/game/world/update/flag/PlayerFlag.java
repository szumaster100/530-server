package core.game.world.update.flag;

import core.game.world.map.Location;
import core.game.world.map.RegionChunk;
import core.game.world.map.Viewport;

public final class PlayerFlag {

    private boolean updateSceneGraph;

    private RegionChunk[][] lastViewport = new RegionChunk[Viewport.CHUNK_SIZE][Viewport.CHUNK_SIZE];

    private Location lastSceneGraph;

    public PlayerFlag() {

    }

    public boolean isUpdateSceneGraph() {
        return updateSceneGraph;
    }

    public void setUpdateSceneGraph(boolean updateSceneGraph) {
        this.updateSceneGraph = updateSceneGraph;
    }

    public Location getLastSceneGraph() {
        return lastSceneGraph;
    }

    public void setLastSceneGraph(Location lastSceneGraph) {
        this.lastSceneGraph = lastSceneGraph;
    }

    public RegionChunk[][] getLastViewport() {
        return lastViewport;
    }

    public void setLastViewport(RegionChunk[][] lastViewport) {
        this.lastViewport = lastViewport;
    }

}
