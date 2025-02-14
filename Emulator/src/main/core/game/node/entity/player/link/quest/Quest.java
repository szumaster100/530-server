package core.game.node.entity.player.link.quest;

import core.game.component.Component;
import core.game.node.entity.player.Player;
import core.plugin.Plugin;
import core.plugin.PluginManifest;
import core.plugin.PluginType;

import java.util.Arrays;
import java.util.Random;

import static core.api.ContentAPIKt.playJingle;

@PluginManifest(type = PluginType.QUEST)
public abstract class Quest implements Plugin<Object> {

    public static final String RED = "<col=8A0808>";

    public static final String BRIGHT_RED = "<col=FF0000>";

    public static final String BLUE = "<col=08088A>";

    public static final String BLACK = "<col=000000>";

    public static final int JOURNAL_COMPONENT = 275;

    public static final int REWARD_COMPONENT = 277;

    private final String name;

    private final int index;

    private final int buttonId;

    private final int questPoints;

    private final int[] configs;

    public Quest(String name, int index, int buttonId, int questPoints, int... configs) {
        this.name = name;
        this.index = index;
        this.buttonId = buttonId;
        this.questPoints = questPoints;
        this.configs = configs;
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public abstract Quest newInstance(Object object);

    public void start(Player player) {
        player.getQuestRepository().setStage(this, 10);
        player.getQuestRepository().syncronizeTab(player);
    }

    public void drawJournal(Player player, int stage) {
        for (int i = 0; i < 311; i++) {
            player.getPacketDispatch().sendString("", JOURNAL_COMPONENT, i);
        }
        player.getPacketDispatch().sendString("<col=8A0808>" + getName() + "</col>", JOURNAL_COMPONENT, 2);

    }

    public void finish(Player player) {
        if (player.getQuestRepository().isComplete(name)) {
            throw new IllegalStateException("Tried to complete quest " + name + " twice, which is not allowed!");
        }
        for (int i = 0; i < 18; i++) {
            if (i == 9 || i == 3 || i == 6) {
                continue;
            }
            player.getPacketDispatch().sendString("", 277, i);
        }
        player.getQuestRepository().setStage(this, 100);
        player.getQuestRepository().incrementPoints(getQuestPoints());
        player.getQuestRepository().syncronizeTab(player);
        player.getInterfaceManager().open(new Component(277).setCloseEvent((p, c) -> {
            this.questCloseEvent(p, c);
            return true;
        }));
        player.getPacketDispatch().sendString("" + player.getQuestRepository().getPoints() + "", 277, 7);
        player.getPacketDispatch().sendString("You have completed the " + getName() + " Quest!", 277, 4);
        player.getPacketDispatch().sendMessage("Congratulations! Quest complete!");
        int questJingles[] = {152, 153, 154};
        playJingle(player, questJingles[new Random().nextInt(3)]);
    }

    public void reset(Player player) {
    }

    public void questCloseEvent(Player player, Component component) {
    }

    public void line(Player player, String message, int line) {
        String send = BLUE + "" + message.replace("<n>", "<br><br>").replace("<blue>", BLUE).replace("<red>", RED);
        if (send.contains("<br><br>") || send.contains("<n>")) {
            String[] lines = send.split(send.contains("<br><br>") ? "<br><br>" : "<n>");
            for (int i = 0; i < lines.length; i++) {
                line(player, lines[i].replace("<br><br>", "").replace("<n>", ""), line, false);
                line++;
            }
        } else {
            send = send.replace("!!", RED).replace("??", BLUE).replace("---", BLACK + "<str>").replace("/--", BLUE).replace("%%", BRIGHT_RED).replace("&&", BLUE);
            line(player, send, line, false);
        }
    }

    public void line(Player player, String message, int line, final boolean crossed) {
        String send;
        if (!crossed) {
            send = BLUE + "" + message.replace("<n>", "<br><br>").replace("<blue>", BLUE).replace("<red>", RED);
            send = send.replace("!!", RED).replace("??", BLUE).replace("%%", BRIGHT_RED).replace("&&", BLUE);
        } else {
            send = BLACK + "" + message.replace("<n>", "<br><br>").replace("<blue>", "").replace("<red>", "RED");
            send = send.replace("!!", "").replace("??", "").replace("%%", "").replace("&&", "");
        }
        player.getPacketDispatch().sendString(crossed ? "<str>" + send + "</str>" : send, JOURNAL_COMPONENT, line);
    }

    public void drawReward(Player player, final String string, final int line) {
        player.getPacketDispatch().sendString(string, REWARD_COMPONENT, line);
    }

    public void setStage(Player player, int stage) {
        player.getQuestRepository().setStage(this, stage);
    }

    public int[] getConfig(Player player, int stage) {
        if (configs.length < 4) {
            throw new IndexOutOfBoundsException("Quest -> " + name + " configs array length was not valid. config length = " + configs.length + "!");
        }
        if (configs.length >= 5) {
            // {questVarpId, questVarbitId, valueToSet}
            return new int[]{configs[0], configs[1], stage == 0 ? configs[2] : stage >= 100 ? configs[4] : configs[3]};
        }
        // {questVarpId, valueToSet}
        return new int[]{configs[0], stage == 0 ? configs[1] : stage >= 100 ? configs[3] : configs[2]};
    }

    public void updateVarps(Player player) {
    }

    public boolean isStarted(Player player) {
        return getStage(player) > 0 && getStage(player) < 100;
    }

    public boolean isCompleted(Player player) {
        return getStage(player) >= 100;
    }

    public int getStage(Player player) {
        return player.getQuestRepository().getStage(this);
    }

    public boolean hasRequirements(Player player) {
        return true;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int getButtonId() {
        return buttonId;
    }

    public int getQuestPoints() {
        return questPoints;
    }

    public int[] getConfigs() {
        return configs;
    }

    @Override
    public String toString() {
        return "Quest [name=" + name + ", index=" + index + ", buttonId=" + buttonId + ", questPoints=" + questPoints + ", configs=" + Arrays.toString(configs) + "]";
    }

}