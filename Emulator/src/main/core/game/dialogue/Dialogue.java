package core.game.dialogue;

import core.game.component.Component;
import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.plugin.Plugin;
import core.plugin.PluginManifest;
import core.plugin.PluginType;
import core.tools.Log;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.rs.consts.Components;

import static core.api.ContentAPIKt.log;
import static core.game.dialogue.DialogueUtilsKt.splitLines;
import static core.tools.DialogueHelperKt.DIALOGUE_INITIAL_OPTIONS_HANDLE;
import static core.tools.DialogueHelperKt.START_DIALOGUE;

@PluginManifest(type = PluginType.DIALOGUE)
public abstract class Dialogue implements Plugin<Player> {
    protected static final String RED = "<col=8A0808>";
    protected static final String BLUE = "<col=08088A>";
    protected Player player;
    protected DialogueInterpreter interpreter;
    public DialogueFile file;
    protected ObjectArrayList<String> optionNames = new ObjectArrayList<>(10);
    protected ObjectArrayList<DialogueFile> optionFiles = new ObjectArrayList<>(10);

    protected final int TWO_OPTIONS = Components.MULTI2_228;
    protected final int THREE_OPTIONS = Components.MULTI3_230;
    protected final int FOUR_OPTIONS = Components.MULTI4_232;
    protected final int FIVE_OPTIONS = Components.MULTI5_234;

    protected NPC npc;
    public int stage;
    protected boolean finished;

    public Dialogue() {
        //
    }

    public Dialogue(Player player) {
        this.player = player;
        if (player != null) {
            this.interpreter = player.getDialogueInterpreter();
        }
    }

    public void init() {
        for (int id : getIds()) {
            DialogueInterpreter.add(id, this);
        }
    }

    public boolean close() {
        player.getInterfaceManager().closeChatbox();
        player.getInterfaceManager().openChatbox(Components.CHATDEFAULT_137);
        if (file != null) file.end();
        finished = true;
        return true;
    }

    public void sendNormalDialogue(Entity entity, FaceAnim expression, String... messages) {
        interpreter.sendDialogues(entity, expression, messages);
    }

    public void increment() {
        stage++;
    }

    public int getAndIncrement() {
        return stage++;
    }

    public void end() {
        if (interpreter != null) {
            interpreter.close();
        }
    }

    public void finish() {
        setStage(-1);
    }

    @Override
    public Object fireEvent(String identifier, Object... args) {
        return null;
    }

    @Override
    public Dialogue newInstance(Player player) {
        try {
            return (Dialogue) Class.forName(this.getClass().getCanonicalName())
                .getDeclaredConstructor(Player.class)
                .newInstance(player);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean open(Object... args) {
        player.getDialogueInterpreter().activeTopics.clear();
        if (args.length > 0 && args[0] instanceof NPC) {
            npc = (NPC) args[0];
        }

        if (npc == null) {
            log(this.getClass(), Log.WARN, args[0].getClass().getSimpleName() + " is not assigning an NPC.");
        }

        player.getDialogueInterpreter().handle(0, 0);
        return true;
    }

    public abstract boolean handle(int interfaceId, int buttonId);

    public abstract int[] getIds();

    public Component npc(final String... messages) {
        return npc == null
            ? interpreter.sendDialogues(getIds()[0], getIds()[0] > 8591 ? FaceAnim.OLD_NORMAL : FaceAnim.FRIENDLY, messages)
            : interpreter.sendDialogues(npc, npc.getId() > 8591 ? FaceAnim.OLD_NORMAL : FaceAnim.FRIENDLY, messages);
    }

    public Component npc(int id, final String... messages) {
        return interpreter.sendDialogues(id, FaceAnim.FRIENDLY, messages);
    }

    public Component sendDialogue(String... messages) {
        return interpreter.sendDialogue(messages);
    }

    public Component npc(FaceAnim expression, final String... messages) {
        return npc == null
            ? interpreter.sendDialogues(getIds()[0], expression, messages)
            : interpreter.sendDialogues(npc, expression, messages);
    }

    public Component player(final String... messages) {
        return interpreter.sendDialogues(player, null, messages);
    }

    public Component player(FaceAnim expression, final String... messages) {
        return interpreter.sendDialogues(player, expression, messages);
    }

    public void options(final String... options) {
        interpreter.sendOptions("Select an Option", options);
    }

    public boolean isFinished() {
        return finished;
    }

    public Player getPlayer() {
        return player;
    }

    public void setStage(int i) {
        this.stage = i;
    }

    public void next() {
        this.stage++;
    }

    public void loadFile(DialogueFile file) {
        if (file != null) {
            this.file = file.load(player, npc, interpreter);
            this.file.setDialogue(this);
            stage = START_DIALOGUE;
        }
    }

    public void addOption(String name, DialogueFile file) {
        optionNames.add("Talk about " + name);
        optionFiles.add(file);
    }

    public boolean sendChoices() {
        if (optionNames.size() == 1) {
            loadFile(optionFiles.get(0));
            return false;
        } else if (optionNames.isEmpty()) {
            stage = START_DIALOGUE;
            return false;
        } else {
            options(optionNames.toArray(new String[0]));
            stage = DIALOGUE_INITIAL_OPTIONS_HANDLE;
            return true;
        }
    }

    public Component npcl(FaceAnim expr, String msg) {
        return npc(expr, splitLines(msg, 54));
    }

    public Component playerl(FaceAnim expr, String msg) {
        return player(expr, splitLines(msg, 54));
    }

    public boolean showTopics(Topic<?>... topics) {
        ObjectArrayList<String> validTopics = new ObjectArrayList<>();
        for (Topic<?> topic : topics) {
            if (topic instanceof IfTopic && !((IfTopic<?>) topic).getShowCondition()) continue;
            interpreter.activeTopics.add(topic);
            validTopics.add(topic.getText());
        }

        if (validTopics.isEmpty()) {
            return true;
        } else if (validTopics.size() == 1) {
            Topic<?> topic = interpreter.activeTopics.get(0);
            if (topic.getToStage() instanceof DialogueFile) {
                DialogueFile topicFile = (DialogueFile) topic.getToStage();
                interpreter.getDialogue().loadFile(topicFile);
            } else if (topic.getToStage() instanceof Integer) {
                stage = (Integer) topic.getToStage();
            }
            player(topic.getText());
            interpreter.activeTopics.clear();
            return false;
        } else {
            options(validTopics.toArray(new String[0]));
            return false;
        }
    }
}
