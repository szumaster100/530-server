package core.plugin.type;

import java.util.ArrayList;
import java.util.List;

public class Managers {

    private static List<ManagerPlugin> plugins = new ArrayList<>(20);
    public static void register(ManagerPlugin plugin){
        if(plugin != null){
            plugins.add(plugin);
        }
    }

    public static void tick(){
        for(ManagerPlugin p : plugins){
            p.tick();
        }
    }
}
