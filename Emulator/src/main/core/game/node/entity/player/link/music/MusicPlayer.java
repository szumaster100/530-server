package core.game.node.entity.player.link.music;

import core.game.node.entity.player.Player;
import core.game.node.entity.player.link.emote.Emotes;
import core.game.world.GameWorld;
import core.net.packet.PacketRepository;
import core.net.packet.context.MusicContext;
import core.net.packet.context.StringContext;
import core.net.packet.out.MusicPacket;
import core.net.packet.out.StringPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import static core.api.ContentAPIKt.setVarp;

public final class MusicPlayer {

    public static final int TUTORIAL_MUSIC = 62;
    public static final int DEFAULT_MUSIC_ID = 76;
    private static final int[] CONFIG_IDS = {20, 21, 22, 23, 24, 25, 298, 311, 346, 414, 464, 598, 662, 721, 906, 1009, 1104, 1136, 1180, 1202};

    private final Player player;
    private final Object2ObjectOpenHashMap<Integer, MusicEntry> unlocked;
    private int currentMusicId;
    private boolean playing;
    private boolean looping;

    public MusicPlayer(Player player) {
        this.player = player;
        this.unlocked = new Object2ObjectOpenHashMap<>();
    }

    public void init() {
        refreshList();
        setVarp(player, 19, looping ? 1 : 0);
        int value = 0;
        for (int i = 0; i < CONFIG_IDS.length; i++) {
            value |= 2 << i;
        }
        player.getPacketDispatch().sendIfaceSettings(value, 1, 187, 0, CONFIG_IDS.length * 64);
        if (!unlocked.containsKey(TUTORIAL_MUSIC)) {
            unlock(TUTORIAL_MUSIC, false);
        }
        if (!isMusicPlaying()) {
            playDefault();
        }
        if (!hasAirGuitar() && player.getEmoteManager().isUnlocked(Emotes.AIR_GUITAR)) {
            player.getPacketDispatch().sendMessage("As you no longer have all music unlocked, the Air Guitar emote is locked again.");
            player.getEmoteManager().lock(Emotes.AIR_GUITAR);
        }
    }

    public void clearUnlocked() {
        this.unlocked.clear();
    }

    public boolean hasAirGuitar() {
        return unlocked.size() >= 200 || unlocked.size() == MusicEntry.getSongs().size();
    }

    public boolean hasUnlocked(int musicId) {
        MusicEntry entry = MusicEntry.forId(musicId);
        return entry != null && hasUnlockedIndex(entry.getIndex());
    }

    public boolean hasUnlockedIndex(int index) {
        return unlocked.containsKey(index);
    }

    public void refreshList() {
        int[] values = new int[CONFIG_IDS.length];
        for (MusicEntry entry : unlocked.values()) {
            int listIndex = entry.getIndex();
            int index = listIndex / 32;
            if (index >= CONFIG_IDS.length) {
                continue;
            }
            values[index] |= 1 << (listIndex & 31);
        }
        for (int i = 0; i < CONFIG_IDS.length; i++) {
            setVarp(player, CONFIG_IDS[i], values[i]);
        }
    }

    public void playDefault() {
        MusicEntry entry = MusicEntry.forId(DEFAULT_MUSIC_ID);
        if (entry != null) {
            play(entry);
        }
    }

    public void replay() {
        MusicEntry entry = MusicEntry.forId(currentMusicId);
        if (entry != null) {
            play(entry);
        }
    }

    public void play(MusicEntry entry) {
        if (!looping || currentMusicId != entry.getId()) {
            PacketRepository.send(MusicPacket.class, new MusicContext(player, entry.getId(), false));
            PacketRepository.send(StringPacket.class, new StringContext(player, entry.getName() + (player.isDebug() ? (" (" + entry.getId() + ")") : ""), 187, 14));
            currentMusicId = entry.getId();
            playing = true;
        }
    }

    public void unlock(int id) {
        unlock(id, true);
    }

    public void unlock(int id, boolean play) {
        MusicEntry entry = MusicEntry.forId(id);
        if (entry == null) {
            return;
        }
        if (!unlocked.containsKey(entry.getIndex())) {
            unlocked.put(entry.getIndex(), entry);
            player.getPacketDispatch().sendMessage("<col=FF0000>You have unlocked a new music track: " + entry.getName() + ".</col>");
            refreshList();
            if (!player.getEmoteManager().isUnlocked(Emotes.AIR_GUITAR) && hasAirGuitar()) {
                player.getEmoteManager().unlock(Emotes.AIR_GUITAR);
                if (unlocked.size() >= 200) {
                    player.getPacketDispatch().sendMessage("You've unlocked 200 music tracks and can use a new emote!");
                } else {
                    player.getPacketDispatch().sendMessage("You've unlocked all music tracks and can use a new emote!");
                }
            }
        }
        if (play) {
            play(entry);
        }
    }

    public void tick() {
        if (GameWorld.getTicks() % 20 == 0) {
            if (!isPlaying()) {
                try {
                    PacketRepository.send(MusicPacket.class, new MusicContext(player, currentMusicId, false));
                } catch (Exception e) {
                    // Handle exception gracefully
                }
            }
        }
    }

    public void toggleLooping() {
        looping = !looping;
        setVarp(player, 19, looping ? 1 : 0);
    }

    private boolean isMusicPlaying() {
        return currentMusicId > 0 && playing;
    }

    public Object2ObjectOpenHashMap<Integer, MusicEntry> getUnlocked() {
        return unlocked;
    }

    public int getCurrentMusicId() {
        return currentMusicId;
    }

    public void setCurrentMusicId(int currentMusicId) {
        this.currentMusicId = currentMusicId;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isLooping() {
        return looping;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
        setVarp(player, 19, looping ? 1 : 0);
    }
}
