package core.game.container.access;

import core.api.IfaceSettingsBuilder;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.net.packet.PacketRepository;
import core.net.packet.context.ContainerContext;
import core.net.packet.out.ContainerPacket;

public class InterfaceContainer {

    private static final int CLIENT_SCRIPT_INDEX = 150;

    private static int index = 600; // 93

    private static int generate(Player player, Item[] itemArray, String[] options, int interfaceIndex, int childIndex, int x, int y, int key) {
        Object[] clientScript = new Object[options.length + 7];
        player.getPacketDispatch().sendRunScript(CLIENT_SCRIPT_INDEX, generateScriptArguments(options.length), populateScript(clientScript, options, interfaceIndex << 16 | childIndex, x, y, key));
        int settings = new IfaceSettingsBuilder().enableAllOptions().build();
        player.getPacketDispatch().sendIfaceSettings(settings, childIndex, interfaceIndex, 0, itemArray.length);
        PacketRepository.send(ContainerPacket.class, new ContainerContext(player, -1, -2, key, itemArray, itemArray.length, false));
        return increment();
    }

    public static int generateOptions(Player player, String[] options, int interfaceIndex, int childIndex, int x, int y, int key) {
        player.getPacketDispatch().sendRunScript(CLIENT_SCRIPT_INDEX, generateScriptArguments(options.length), populateScript(new Object[options.length + 7], options, interfaceIndex << 16 | childIndex, x, y, key));
        int settings = new IfaceSettingsBuilder().enableAllOptions().build();
        player.getPacketDispatch().sendIfaceSettings(settings, childIndex, interfaceIndex, 0, 28);
        return increment();
    }

    public static int generate(Player player, int interfaceId, int childId, int itemLength, String... options) {
        return generate(player, interfaceId, childId, itemLength, 7, 3, options);
    }

    public static int generate(Player player, int interfaceId, int childId, int itemLength, int x, int y, String... options) {
        int key = increment();
        Object[] clientScript = new Object[options.length + 7];
        player.getPacketDispatch().sendRunScript(CLIENT_SCRIPT_INDEX, generateScriptArguments(options.length), populateScript(clientScript, options, interfaceId << 16 | childId, x, y, key));
        int settings = new IfaceSettingsBuilder().enableAllOptions().build();
        player.getPacketDispatch().sendIfaceSettings(settings, childId, interfaceId, 0, itemLength);
        return key;
    }

    private static int increment() {
        if (index == 6999) {
            index = 600;
        }
        return index++;
    }

    private static Object[] populateScript(Object[] script, String[] options, int hash, int x, int y, int key) {
        int offset = 0;
        for (String option : options) {
            script[offset++] = option;
        }
        System.arraycopy(new Object[]{-1, 0, x, y, key, hash}, 0, script, offset, 6);
        return script;
    }

    private static String generateScriptArguments(int length) {
        StringBuilder builder = new StringBuilder("IviiiI");
        while (length > 0) {
            builder.append("s");
            length--;
        }
        return builder.toString();
    }

    public static int generateItems(Player player, Item[] itemArray, String[] options, int interfaceIndex, int childIndex) {
        return generateItems(player, itemArray, options, interfaceIndex, childIndex, 7, 3, increment());
    }

    public static int generateItems(Player player, Item[] itemArray, String[] options, int interfaceIndex, int childIndex, int key) {
        return generateItems(player, itemArray, options, interfaceIndex, childIndex, 7, 3, key);
    }

    public static int generateItems(Player player, Item[] itemArray, String[] options, int interfaceIndex, int childIndex, int x, int y) {
        return generateItems(player, itemArray, options, interfaceIndex, childIndex, x, y, increment());
    }

    public static int generateItems(Player player, Item[] itemArray, String[] options, int interfaceIndex, int childIndex, int x, int y, int key) {
        return generate(player, itemArray, options, interfaceIndex, childIndex, x, y, key);
    }

}
