package core.game.node.entity.player.link;

import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
import core.net.packet.IoBuffer;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;

import static core.api.ContentAPIKt.setVarp;

public final class Settings {

    private final Player player;

    private double runEnergy = 100.0;

    private double weight;

    private int brightness = 2;

    private int musicVolume;

    private int soundEffectVolume;

    private int areaSoundVolume;

    private boolean singleMouseButton;

    private boolean disableChatEffects;

    private boolean splitPrivateChat;

    private boolean acceptAid;

    private boolean runToggled;

    private int publicChatSetting = 0;

    private int privateChatSetting = 0;

    private int clanChatSetting = 0;

    private int tradeSetting = 0;

    private int assistSetting = 0;

    private boolean specialToggled;

    private int specialEnergy = 100;

    private int attackStyleIndex = 0;

    public Settings(Player player) {
        this.player = player;
    }

    public void update() {
        setVarp(player, 166, brightness + 1);
        setVarp(player, 168, musicVolume);
        setVarp(player, 169, soundEffectVolume);
        setVarp(player, 872, areaSoundVolume);
        setVarp(player, 170, singleMouseButton ? 1 : 0);
        setVarp(player, 171, disableChatEffects ? 1 : 0);
        setVarp(player, 287, splitPrivateChat ? 1 : 0);
        setVarp(player, 427, acceptAid ? 1 : 0);
        setVarp(player, 172, !player.getProperties().isRetaliating() ? 1 : 0);
        setVarp(player, 173, runToggled ? 1 : 0);
        setVarp(player, 1054, clanChatSetting);
        setVarp(player, 1055, assistSetting);
        setVarp(player, 300, specialEnergy * 10);
        setVarp(player, 43, attackStyleIndex);
        player.getPacketDispatch().sendRunEnergy();
        updateChatSettings();
    }

    public void toggleAttackStyleIndex(int index) {
        this.attackStyleIndex = index;
        setVarp(player, 43, attackStyleIndex);
    }

    public void updateChatSettings() {
        player.getSession().write(new IoBuffer(232).put(publicChatSetting).put(privateChatSetting).put(tradeSetting));
    }

    public void updateChatSettings(int pub, int priv, int trade) {
        boolean update = false;
        if (publicChatSetting != pub) {
            publicChatSetting = pub;
            update = true;
        }
        if (tradeSetting != trade) {
            tradeSetting = trade;
            update = true;
        }
        if (update) {
            updateChatSettings();
        }
    }

    public void setChatSettings(int pub, int priv, int trade) {
        publicChatSetting = pub;
        privateChatSetting = priv;
        tradeSetting = trade;
    }

    public void save(ByteBuffer buffer) {
        buffer.put((byte) 1).put((byte) brightness).put((byte) musicVolume).put((byte) soundEffectVolume).put((byte) areaSoundVolume).put((byte) (singleMouseButton ? 1 : 0)).put((byte) (disableChatEffects ? 1 : 0)).put((byte) (splitPrivateChat ? 1 : 0)).put((byte) (acceptAid ? 1 : 0)).put((byte) (runToggled ? 1 : 0)).put((byte) publicChatSetting).put((byte) privateChatSetting).put((byte) clanChatSetting).put((byte) tradeSetting).put((byte) assistSetting).put(((byte) runEnergy));
        if (!player.getProperties().isRetaliating()) {
            buffer.put((byte) 2);
        }
        if (specialEnergy != 100) {
            buffer.put((byte) 3).put((byte) specialEnergy);
        }
        if (attackStyleIndex != 0) {
            buffer.put((byte) 4).put((byte) attackStyleIndex);
        }
        buffer.put((byte) 0);
    }

    public void parse(ByteBuffer buffer) {
        int opcode;
        while ((opcode = buffer.get() & 0xFF) != 0) {
            switch (opcode) {
                case 1:
                    brightness = buffer.get();
                    musicVolume = buffer.get();
                    soundEffectVolume = buffer.get();
                    areaSoundVolume = buffer.get();
                    singleMouseButton = buffer.get() == 1;
                    disableChatEffects = buffer.get() == 1;
                    splitPrivateChat = buffer.get() == 1;
                    acceptAid = buffer.get() == 1;
                    runToggled = buffer.get() == 1;
                    publicChatSetting = buffer.get();
                    privateChatSetting = buffer.get();
                    clanChatSetting = buffer.get();
                    tradeSetting = buffer.get();
                    assistSetting = buffer.get();
                    runEnergy = buffer.get();
                    break;
                case 2:
                    player.getProperties().setRetaliating(false);
                    break;
                case 3:
                    specialEnergy = buffer.get() & 0xFF;
                    break;
                case 4:
                    attackStyleIndex = buffer.get();
                    break;
            }
        }
    }

    public void parse(JSONObject settingsData) {
        brightness = Integer.parseInt(settingsData.get("brightness").toString());
        musicVolume = Integer.parseInt(settingsData.get("musicVolume").toString());
        soundEffectVolume = Integer.parseInt(settingsData.get("soundEffectVolume").toString());
        areaSoundVolume = Integer.parseInt(settingsData.get("areaSoundVolume").toString());
        singleMouseButton = (boolean) settingsData.get("singleMouse");
        disableChatEffects = (boolean) settingsData.get("disableChatEffects");
        splitPrivateChat = (boolean) settingsData.get("splitPrivate");
        acceptAid = (boolean) settingsData.get("acceptAid");
        runToggled = (boolean) settingsData.get("runToggled");
        publicChatSetting = Integer.parseInt(settingsData.get("publicChatSetting").toString());
        privateChatSetting = Integer.parseInt(settingsData.get("privateChatSetting").toString());
        clanChatSetting = Integer.parseInt(settingsData.get("clanChatSetting").toString());
        tradeSetting = Integer.parseInt(settingsData.get("tradeSetting").toString());
        assistSetting = Integer.parseInt(settingsData.get("assistSetting").toString());
        runEnergy = Double.parseDouble(settingsData.get("runEnergy").toString());
        specialEnergy = Integer.parseInt(settingsData.get("specialEnergy").toString());
        attackStyleIndex = Integer.parseInt(settingsData.get("attackStyle").toString());
        player.getProperties().setRetaliating((boolean) settingsData.get("retaliation"));
    }

    public void toggleSpecialBar() {
        setSpecialToggled(!specialToggled);
    }

    public void setSpecialToggled(boolean enable) {
        specialToggled = !specialToggled;
        setVarp(player, 301, specialToggled ? 1 : 0);
    }

    public boolean isSpecialToggled() {
        return specialToggled;
    }

    public boolean drainSpecial(int amount) {
        if (!specialToggled) {
            return false;
        }
        setSpecialToggled(false);
        if (amount > specialEnergy) {
            player.getPacketDispatch().sendMessage("You do not have enough special attack energy left.");
            return false;
        }
        setSpecialEnergy(specialEnergy - amount);
        return true;
    }

    public void setSpecialEnergy(int value) {
        specialEnergy = value;
        setVarp(player, 300, specialEnergy * 10);
    }

    public int getSpecialEnergy() {
        return specialEnergy;
    }

    public void toggleRetaliating() {
        player.getProperties().setRetaliating(!player.getProperties().isRetaliating());
        setVarp(player, 172, !player.getProperties().isRetaliating() ? 1 : 0);
    }

    public void toggleMouseButton() {
        singleMouseButton = !singleMouseButton;
        setVarp(player, 170, singleMouseButton ? 1 : 0);
    }

    public void toggleChatEffects() {
        disableChatEffects = !disableChatEffects;
        setVarp(player, 171, disableChatEffects ? 1 : 0);
    }

    public void toggleSplitPrivateChat() {
        splitPrivateChat = !splitPrivateChat;
        setVarp(player, 287, splitPrivateChat ? 1 : 0);
    }

    public void toggleAcceptAid() {
        acceptAid = !acceptAid;
        setVarp(player, 427, acceptAid ? 1 : 0);
    }

    public void toggleRun() {
        setRunToggled(!runToggled);
    }

    public void setRunToggled(boolean enabled) {
        runToggled = enabled;
        setVarp(player, 173, runToggled ? 1 : 0);
    }

    public void updateRunEnergy(double drain) {
        runEnergy -= drain;
        if (runEnergy < 0) {
            runEnergy = 0.0;
        } else if (runEnergy > 100) {
            runEnergy = 100.0;
        }
        player.getPacketDispatch().sendRunEnergy();
    }

    public void updateWeight() {
        weight = 0.0;
        for (int i = 0; i < 28; i++) {
            Item item = player.getInventory().get(i);
            if (item == null) {
                continue;
            }
            double value = item.getDefinition().getConfiguration(ItemConfigParser.WEIGHT, 0.0);
            if (value > 0) {
                weight += value;
            }
        }
        for (int i = 0; i < 11; i++) {
            Item item = player.getEquipment().get(i);
            if (item == null) {
                continue;
            }
            weight += item.getDefinition().getConfiguration(ItemConfigParser.WEIGHT, 0.0);
        }
        player.getPacketDispatch().sendString((int) weight + " kg", 667, 32);
    }

    public double getWeight() {
        return weight;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        setVarp(player, 166, brightness + 1);
    }

    public int getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(int musicVolume) {
        this.musicVolume = musicVolume;
        setVarp(player, 168, musicVolume);
    }

    public int getSoundEffectVolume() {
        return soundEffectVolume;
    }

    public void setSoundEffectVolume(int soundEffectVolume) {
        this.soundEffectVolume = soundEffectVolume;
        setVarp(player, 169, soundEffectVolume);
    }

    public int getAreaSoundVolume() {
        return areaSoundVolume;
    }

    public void setAreaSoundVolume(int areaSoundVolume) {
        this.areaSoundVolume = areaSoundVolume;
        setVarp(player, 872, areaSoundVolume);
    }

    public boolean isSingleMouseButton() {
        return singleMouseButton;
    }

    public boolean isDisableChatEffects() {
        return disableChatEffects;
    }

    public boolean isSplitPrivateChat() {
        return splitPrivateChat;
    }

    public boolean isAcceptAid() {
        if (player.getIronmanManager().isIronman()) {
            return false;
        }
        return acceptAid;
    }

    public boolean isRunToggled() {
        return runToggled;
    }

    public int getPublicChatSetting() {
        return publicChatSetting;
    }

    public void setPublicChatSetting(int publicChatSetting) {
        this.publicChatSetting = publicChatSetting;
        updateChatSettings();
    }

    public int getPrivateChatSetting() {
        return privateChatSetting;
    }

    public void setPrivateChatSetting(int privateChatSetting) {
        this.privateChatSetting = privateChatSetting;
        updateChatSettings();
    }

    public int getClanChatSetting() {
        return clanChatSetting;
    }

    public void setClanChatSetting(int clanChatSetting) {
        this.clanChatSetting = clanChatSetting;
        setVarp(player, 1054, clanChatSetting);
    }

    public int getTradeSetting() {
        return tradeSetting;
    }

    public void setTradeSetting(int tradeSetting) {
        this.tradeSetting = tradeSetting;
        updateChatSettings();
    }

    public int getAssistSetting() {
        return assistSetting;
    }

    public void setAssistSetting(int assistSetting) {
        this.assistSetting = assistSetting;
        setVarp(player, 1055, assistSetting);
    }

    public double getRunEnergy() {
        return runEnergy;
    }

    public void setRunEnergy(double runEnergy) {
        this.runEnergy = runEnergy;
    }

    public int getAttackStyleIndex() {
        return attackStyleIndex;
    }

    public void setAttackStyleIndex(int attackStyleIndex) {
        this.attackStyleIndex = attackStyleIndex;
    }

}
