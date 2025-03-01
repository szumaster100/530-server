package core.plugin.type;

import core.plugin.Plugin;

public abstract class StartupPlugin implements Plugin<Object> {

    public abstract void run();
}
