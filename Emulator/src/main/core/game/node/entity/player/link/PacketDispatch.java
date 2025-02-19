package core.game.node.entity.player.link;

import core.game.node.entity.player.Player;
import core.game.node.scenery.Scenery;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.game.world.map.Location;
import core.game.world.map.RegionManager;
import core.game.world.update.flag.EntityFlag;
import core.game.world.update.flag.chunk.AnimateSceneryUpdateFlag;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.net.packet.PacketRepository;
import core.net.packet.context.*;
import core.net.packet.out.*;
import core.tools.Log;

import static core.api.ContentAPIKt.log;
import static core.api.ContentAPIKt.setVarp;

public final class PacketDispatch {

    private final Player player;

    private final PlayerContext context;

    public PacketDispatch(Player player) {
        this.player = player;
        this.context = new PlayerContext(player);
    }

    public void sendVarp(int index, int value) {
        PacketRepository.send(Config.class, new ConfigContext(player, index, value));
    }

    public void sendVarcUpdate(short index, int value) {
        PacketRepository.send(VarcUpdate.class, new VarcUpdateContext(player, index, value));
    }

    public void sendMessage(String message) {
        if (message == null) {
            return;
        }
        if (player.getAttribute("chat_filter") != null && !message.contains("<col=CC6600>") && !message.contains("<col=FFFF00>")) {
            return;
        }
        if (message.length() > 255) {
            log(this.getClass(), Log.ERR, "Message length out of bounds (" + message + ")!");
            message = message.substring(0, 255);
        }
        PacketRepository.send(GameMessage.class, new GameMessageContext(player, message));
    }

    public void sendMessages(final String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }

    public void sendMessage(final String message, int ticks) {
        GameWorld.getPulser().submit(new Pulse(ticks, player) {
            @Override
            public boolean pulse() {
                sendMessage(message);
                return true;
            }
        });
    }

    public void sendIfaceSettings(int settingsHash, int childId, int interfaceId, int offset, int length) {
        PacketRepository.send(AccessMask.class, new AccessMaskContext(player, settingsHash, childId, interfaceId, offset, length));
    }

    public void sendWindowsPane(int windowId, int type) {
        PacketRepository.send(WindowsPane.class, new WindowsPaneContext(player, windowId, type));
    }

    public void sendSystemUpdate(int time) {
        PacketRepository.send(SystemUpdatePacket.class, new SystemUpdateContext(player, time));
    }

    public void sendMusic(int musicId) {
        PacketRepository.send(MusicPacket.class, new MusicContext(player, musicId, false));
    }

    public void sendTempMusic(int musicId) {
        PacketRepository.send(MusicPacket.class, new MusicContext(player, musicId, true));
    }

    public void sendScriptConfig(int id, int value, String types, java.lang.Object... parameters) {
        PacketRepository.send(CSConfigPacket.class, new CSConfigContext(player, id, value, types, parameters));
    }

    public void sendRunScript(int id, String string, java.lang.Object... objects) {
        PacketRepository.send(RunScriptPacket.class, new RunScriptContext(player, id, string, objects));
    }

    public void sendString(String string, int interfaceId, int lineId) {
        PacketRepository.send(StringPacket.class, new StringContext(player, string, interfaceId, lineId));
    }

    public void sendRunEnergy() {
        PacketRepository.send(RunEnergy.class, getContext());
    }

    public void sendLogout() {
        PacketRepository.send(Logout.class, getContext());
    }

    public void sendAnimationInterface(int animationId, int interfaceId, int childId) {
        PacketRepository.send(AnimateInterface.class, new AnimateInterfaceContext(player, animationId, interfaceId, childId));
    }

    public void sendPlayerOnInterface(int interfaceId, int childId) {
        PacketRepository.send(DisplayModel.class, new DisplayModelContext(player, interfaceId, childId));
    }

    public void sendNpcOnInterface(int npcId, int interfaceId, int childId) {
        PacketRepository.send(DisplayModel.class, new DisplayModelContext(player, npcId, interfaceId, childId));
    }

    public void sendModelOnInterface(int modelID, int interfaceId, int childId, int zoom) {
        PacketRepository.send(DisplayModel.class, new DisplayModelContext(player, DisplayModelContext.ModelType.MODEL, modelID, zoom, interfaceId, childId, new java.lang.Object()));
    }

    public void sendAngleOnInterface(int interfaceId, int childId, int zoom, int pitch, int yaw) {
        PacketRepository.send(InterfaceSetAngle.class, new DefaultContext(player, pitch, zoom, yaw, interfaceId, childId));
    }

    public void sendItemOnInterface(int itemId, int amount, int interfaceId, int childId) {
        PacketRepository.send(DisplayModel.class, new DisplayModelContext(player, DisplayModelContext.ModelType.ITEM, itemId, amount, interfaceId, childId));
    }

    public void sendItemZoomOnInterface(int itemId, int zoom, int interfaceId, int childId) {
        PacketRepository.send(DisplayModel.class, new DisplayModelContext(player, DisplayModelContext.ModelType.ITEM, itemId, zoom, interfaceId, childId, zoom));
    }

    public void sendInterSetItemsOptionsScript(int interfaceId, int componentId, int key, int width, int height, String... options) {
        sendInterSetItemsOptionsScript(interfaceId, componentId, key, false, width, height, options);
    }

    public void sendInterSetItemsOptionsScript(int interfaceId, int componentId, int key, boolean negativeKey, int width, int height, String... options) {
        java.lang.Object[] parameters = new java.lang.Object[6 + options.length];
        int index = 0;
        for (int count = options.length - 1; count >= 0; count--)
            parameters[index++] = options[count];
        parameters[index++] = -1;
        parameters[index++] = 0;
        parameters[index++] = height;
        parameters[index++] = width;
        parameters[index++] = key;
        parameters[index++] = interfaceId << 16 | componentId;
        sendRunScript(negativeKey ? 695 : 150, parameters);
    }

    public void sendRunScript(int scriptId, java.lang.Object... params) {
        String parameterTypes = "";
        if (params != null) {
            for (int count = params.length - 1; count >= 0; count--) {
                if (params[count] instanceof String)
                    parameterTypes += "s"; // string
                else
                    parameterTypes += "i"; // integer
            }
        }
        sendRunScript(scriptId, parameterTypes, params);
    }

    public void sendItemZoomOnInterface(int itemId, int amount, int zoom, int interfaceId, int childId) {
        PacketRepository.send(DisplayModel.class, new DisplayModelContext(player, DisplayModelContext.ModelType.ITEM, itemId, amount, interfaceId, childId, zoom));
    }

    public void sendInterfaceConfig(int interfaceId, int childId, boolean hide) {
        PacketRepository.send(InterfaceConfig.class, new InterfaceConfigContext(player, interfaceId, childId, hide));
    }

    public void sendAnimation(int id) {
        player.getUpdateMasks().register(EntityFlag.Animate, new Animation(id));
    }

    public void sendAnimation(int id, int delay) {
        player.getUpdateMasks().register(EntityFlag.Animate, new Animation(id, delay));
    }

    public void sendGraphic(int id) {
        player.getUpdateMasks().register(EntityFlag.SpotAnim, new Graphics(id));
    }

    public void sendPositionedGraphic(int id, int height, int delay, Location location) {
        PacketRepository.send(PositionedGraphic.class, new PositionedGraphicContext(player, new Graphics(id, height, delay), location, 0, 0));
    }

    public void sendGlobalPositionGraphic(int id, Location location) {
        for (Player player : RegionManager.getLocalPlayers(location)) {
            player.getPacketDispatch().sendPositionedGraphic(id, 0, 0, location);
        }
    }

    public void sendPositionedGraphics(Graphics graphics, Location location) {
        PacketRepository.send(PositionedGraphic.class, new PositionedGraphicContext(player, graphics, location, 0, 0));
    }

    public void sendSceneryAnimation(Scenery scenery, Animation animation) {
        animation = new Animation(animation.getId(), animation.getDelay(), animation.getPriority());
        animation.setObject(scenery);
        RegionManager.getRegionChunk(scenery.getLocation()).flag(new AnimateSceneryUpdateFlag(animation));
    }

    public void sendSceneryAnimation(Scenery scenery, Animation animation, boolean global) {
        if (global) {
            sendSceneryAnimation(scenery, animation);
            return;
        }
        animation.setObject(scenery);
        PacketRepository.send(AnimateObjectPacket.class, new AnimateObjectContext(player, animation));
    }

    public void sendGraphic(int id, int height) {
        player.getUpdateMasks().register(EntityFlag.SpotAnim, new Graphics(id, height));
    }
    public void sendLeftShiftedVarbit(int varpIndex, int offset, int value) {
        setVarp(player, varpIndex, (value << offset));
    }

    public void sendRightShiftedVarbit(int varpIndex, int offset, int value) {
        setVarp(player, varpIndex, (value >> offset));
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerContext getContext() {
        return context;
    }

    public void sendScriptConfigs(int id, int value, String type, java.lang.Object... params) {
        PacketRepository.send(CSConfigPacket.class, new CSConfigContext(player, id, value, type, params));
    }

    public void resetInterface(int id) {
        PacketRepository.send(ResetInterface.class, new InterfaceContext(player, 0, 0, id, false));
    }

    public void sendRepositionOnInterface(int id, int component, int x, int y) {
        PacketRepository.send(RepositionChild.class, new ChildPositionContext(player, id, component, x, y));
    }

}
