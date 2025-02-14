package core.game.node.entity.player.info;

public final class ClientInfo {

    private int displayMode;

    private int windowMode;

    private int screenWidth;

    private int screenHeight;

    public ClientInfo(int displayMode, int windowMode, int screenWidth, int screenHeight) {
        this.displayMode = displayMode;
        this.windowMode = windowMode;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public boolean isHighDetail() {
        return displayMode > 0; // ?
    }

    public boolean isResizable() {
        return windowMode > 1;
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }

    public int getWindowMode() {
        return windowMode;
    }

    public void setWindowMode(int windowMode) {
        this.windowMode = windowMode;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(int screenWidth) {
        this.screenWidth = screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(int screenHeight) {
        this.screenHeight = screenHeight;
    }
}