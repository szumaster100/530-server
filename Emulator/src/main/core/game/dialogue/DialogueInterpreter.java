package core.game.dialogue;

import core.cache.def.impl.ItemDefinition;
import core.cache.def.impl.NPCDefinition;
import core.game.component.Component;
import core.game.event.DialogueCloseEvent;
import core.game.event.DialogueOpenEvent;
import core.game.event.DialogueOptionSelectionEvent;
import core.game.node.entity.Entity;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.player.Player;
import core.game.node.item.Item;
import core.game.system.config.ItemConfigParser;
import core.game.system.task.Pulse;
import core.game.world.GameWorld;
import core.plugin.PluginManifest;
import core.plugin.PluginType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static core.tools.DialogueHelperKt.END_DIALOGUE;

@PluginManifest(type = PluginType.DIALOGUE)
public final class DialogueInterpreter {
    public ObjectArrayList<Topic<?>> activeTopics = new ObjectArrayList<>();
    private static final Int2ObjectOpenHashMap<Dialogue> PLUGINS = new Int2ObjectOpenHashMap<>();
    private final ObjectArrayList<DialogueAction> actions = new ObjectArrayList<>(10);

    private Dialogue dialogue;
    private int key;
    private final Player player;

    public DialogueInterpreter(Player player) {
        this.player = player;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    public boolean open(String dialogueType, Object... args) {
        return open(getDialogueKey(dialogueType), args);
    }

    public boolean open(int dialogueKey, Object... args) {
        key = dialogueKey;
        if (args.length > 0 && args[0] instanceof NPC) {
            NPC npc = (NPC) args[0];
            npc.setDialoguePlayer(player);
            try {
                npc.getWalkingQueue().reset();
                npc.getPulseManager().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (args.length < 1) {
            args = new Object[]{dialogueKey};
        }

        Dialogue plugin = PLUGINS.get(dialogueKey);
        if (plugin == null) {
            return false;
        }

        if (player.isDebug()) {
            player.sendMessage("dialogue opening - " + plugin.getClass().getSimpleName() + ", key=" + dialogueKey);
        }

        this.dialogue = plugin.newInstance(player);
        if (dialogue == null || !dialogue.open(args)) {
            dialogue = null;
            return false;
        }

        if (dialogue != null) {
            player.dispatch(new DialogueOpenEvent(dialogue));
        }

        return true;
    }

    public void open(DialogueFile file, Object... args) {
        this.dialogue = new EmptyDialogue(player, file);
        this.dialogue.open(args);
    }

    public void handle(int componentId, int buttonId) {
        player.setAttribute("chatbox-buttonid", buttonId);

        if ((player.getDialogueInterpreter().getDialogue().file != null && player.getDialogueInterpreter().getDialogue().file.getCurrentStage() == END_DIALOGUE) || player.getDialogueInterpreter().getDialogue().stage == END_DIALOGUE) {
            player.getInterfaceManager().closeChatbox();
            player.getInterfaceManager().openChatbox(137);
            player.getDialogueInterpreter().dialogue.finished = true;
            close();
            return;
        }

        DialogueFile file = dialogue.file;
        if (!activeTopics.isEmpty() && buttonId >= 2) {
            Topic<?> topic = activeTopics.get(buttonId - 2);

            if (!topic.getSkipPlayer()) sendDialogues(player, topic.getExpr(), topic.getText());

            if (topic.getToStage() instanceof DialogueFile) {
                DialogueFile topicFile = (DialogueFile) topic.getToStage();
                dialogue.loadFile(topicFile);
            } else if (topic.getToStage() instanceof Integer) {
                int stage = (Integer) topic.getToStage();
                if (file == null) dialogue.stage = stage;
                else file.setStage(stage);
            }
            activeTopics.clear();

            if (topic.getSkipPlayer()) handle(componentId, buttonId);

            return;
        }

        if (file != null) {
            player.dispatch(new DialogueOptionSelectionEvent(file, file.getStage(), buttonId - 1));
            file.handle(componentId, buttonId - 1);
        } else {
            player.dispatch(new DialogueOptionSelectionEvent(dialogue, dialogue.stage, buttonId - 1));
            dialogue.handle(componentId, buttonId - 1);
        }
    }

    public boolean close() {
        if (dialogue != null) {
            actions.clear();

            if (player.getInterfaceManager().getChatbox() != null && player.getInterfaceManager().getChatbox().getCloseEvent() != null) {
                return true;
            }
            if (dialogue != null) {
                Dialogue d = dialogue;
                dialogue = null;
                d.close();
                player.dispatch(new DialogueCloseEvent(d));
            }
        }
        activeTopics.clear();
        return dialogue == null;
    }

    public static void add(int id, Dialogue plugin) {
        if (PLUGINS.containsKey(id)) {
            throw new IllegalArgumentException("dialogue " + (id & 0xFFFF) + " is already in use - [old=" + PLUGINS.get(id).getClass().getSimpleName() + ", new=" + plugin.getClass().getSimpleName() + "]!");
        }
        PLUGINS.put(id, plugin);
    }

    public Component sendDialogue(String... messages) {
        if (messages.length < 1 || messages.length > 4) {
            return null;
        }
        int interfaceId = 209 + messages.length;
        for (int i = 0; i < messages.length; i++) {
            player.getPacketDispatch().sendString(messages[i], interfaceId, i + 1);
        }
        player.getInterfaceManager().openChatbox(interfaceId);
        player.getPacketDispatch().sendInterfaceConfig(player.getInterfaceManager().getChatbox().getId(), 1, false);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDialogue(int ticks, String... messages) {
        GameWorld.getPulser().submit(new Pulse(ticks, player) {
            @Override
            public boolean pulse() {
                sendDialogue(messages);
                return true;
            }
        });
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDialogues(String... messages) {
        if (messages.length < 1 || messages.length > 5) {
            return null;
        }

        for (int i = 0; i < messages.length; i++) {
            if (messages.length > 5)
                throw new IllegalArgumentException("Invalid message length, should be between 1 and 5, and it is:" + messages[i]);
            player.getPacketDispatch().sendString(messages[i], 214, i + 1);
        }

        player.getInterfaceManager().openChatbox(214);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendPlainMessage(final boolean hideContinue, String... messages) {
        sendDialogue(messages);
        player.getPacketDispatch().sendInterfaceConfig(player.getInterfaceManager().getChatbox().getId(), (messages.length + 1), hideContinue);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDestroyItem(int id, String message) {
        String text = ItemDefinition.forId(id).getConfiguration(ItemConfigParser.DESTROY_MESSAGE, "Are you sure you want to destroy this object?");
        if (text.length() > 200) {
            String[] words = text.split(" ");
            StringBuilder sb = new StringBuilder(words[0]);
            for (int i = 1; i < words.length; i++) {
                if (i == (words.length / 2)) {
                    sb.append("<br>");
                } else {
                    sb.append(" ");
                }
                sb.append(words[i]);
            }
            text = sb.toString();
        }
        player.getPacketDispatch().sendString(text, 94, 7);
        player.getPacketDispatch().sendString(ItemDefinition.forId(id).getName(), 94, 8);
        player.getPacketDispatch().sendItemOnInterface(id, 1, 94, 9);

        player.getInterfaceManager().openChatbox(94);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendPlaneMessageWithBlueTitle(String title, String... messages) {
        player.getPacketDispatch().sendString(title, 372, 0);
        for (int i = 0; i < messages.length; i++) {
            player.getPacketDispatch().sendString(messages[i], 372, i + 1);
        }
        player.getInterfaceManager().openChatbox(372);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendScrollMessageWithBlueTitle(String title, String... messages) {
        for (int i = 0; i < 11; i++) {
            player.getPacketDispatch().sendString(" ", 421, i + 2);
        }
        player.getPacketDispatch().sendString(title, 421, 1);
        for (int i = 0; i < messages.length; i++) {
            player.getPacketDispatch().sendString(messages[i], 421, i + 2);
        }
        player.getInterfaceManager().openChatbox(421);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendItemMessage(int itemId, String... messages) {
        if (messages == null || messages.length == 0) {
            throw new IllegalArgumentException("Messages cannot be null or empty.");
        }

        String combinedMessage = String.join("<br>", messages);

        player.getInterfaceManager().openChatbox(131);
        player.getPacketDispatch().sendString(combinedMessage, 131, 1);

        ItemDefinition itemDef = ItemDefinition.forId(itemId);
        if (itemDef == null) {
            throw new IllegalArgumentException("Invalid itemId: " + itemId);
        }

        player.getPacketDispatch().sendItemOnInterface(itemId, 1, 131, 2);

        player.getPacketDispatch()
            .sendAngleOnInterface(131, 2,
                (int) (itemDef.getModelZoom() / 1.1337),
                itemDef.getModelRotationX(),
                itemDef.getModelRotationY()
            );

        return player.getInterfaceManager().getChatbox();
    }
    public Component sendItemMessage(final Item item, String... messages) {
        return sendItemMessage(item.getId(), messages);
    }

    public Component sendDoubleItemMessage(int first, int second, String message) {
        player.getInterfaceManager().openChatbox(131);
        player.getPacketDispatch().sendString(message, 131, 1);
        player.getPacketDispatch().sendItemOnInterface(first, 1, 131, 0);
        player.getPacketDispatch().sendItemOnInterface(second, 1, 131, 2);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDoubleItemMessage(int first, int second, int zoom, String message) {
        player.getInterfaceManager().openChatbox(131);
        player.getPacketDispatch().sendString(message, 131, 1);
        player.getPacketDispatch().sendItemZoomOnInterface(first, zoom, 131, 0);
        player.getPacketDispatch().sendItemZoomOnInterface(second, zoom, 131, 2);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDoubleItemMessage(Item first, Item second, String message) {
        player.getInterfaceManager().openChatbox(131);
        player.getPacketDispatch().sendString(message, 131, 1);
        player.getPacketDispatch().sendItemOnInterface(first.getId(), first.getAmount(), 131, 0);
        player.getPacketDispatch().sendItemOnInterface(second.getId(), second.getAmount(), 131, 2);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDialogues(Entity entity, FaceAnim expression, String... messages) {
        return sendDialogues(entity, expression == null ? -1 : expression.getAnimationId(), messages);
    }

    public Component sendDialogues(Entity entity, int expression, String... messages) {
        return sendDialogues(entity instanceof Player ? -1 : ((NPC) entity).getShownNPC(player).getId(), expression, false, messages);
    }

    public Component sendDialogues(int npcId, FaceAnim expression, boolean hide, String... messages) {
        return sendDialogues(npcId, expression == null ? -1 : expression.getAnimationId(), hide, messages);
    }

    public Component sendDialogues(Entity entity, FaceAnim expression, boolean hide, String... messages) {
        return sendDialogues(entity.getId(), expression == null ? -1 : expression.getAnimationId(), hide, messages);
    }

    public Component sendDialogues(Entity entity, int expression, boolean hide, String... messages) {
        return sendDialogues(entity.getId(), expression, hide, messages);
    }

    public Component sendDialogues(int npcId, FaceAnim expression, String... messages) {
        return sendDialogues(npcId, expression == null ? -1 : expression.getAnimationId(), false, messages);
    }

    public Component sendDialogues(int ticks, int npcId, FaceAnim expression, String... messages) {
        return sendDialogues(ticks, npcId, expression == null ? -1 : expression.getAnimationId(), false, messages);
    }

    static Pattern GENDERED_SUBSTITUTION = Pattern.compile("@g\\[([^,]*),([^\\]]*)\\]");

    public static String doSubstitutions(Player player, String msg) {
        msg = msg.replace("@name", player.getUsername());
        StringBuilder sb = new StringBuilder();
        Matcher m = GENDERED_SUBSTITUTION.matcher(msg);
        int index = player.isMale() ? 1 : 2;
        while (m.find()) {
            m.appendReplacement(sb, m.group(index));
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public Component sendDialogues(int npcId, int expression, String... messages) {
        return sendDialogues(npcId, expression, false, messages);
    }

    public Component sendDialogues(int npcId, int expression, boolean hide, String... messages) {
        if (messages.length < 1 || messages.length > 4) {
            System.err.println("Invalid amount of messages: " + messages.length);
            return null;
        }
        boolean npc = npcId > -1;
        int interfaceId = (npc ? 240 : 63) + messages.length;
        interfaceId += hide ? 4 : 0;
        if (expression == -1) {
            expression = FaceAnim.HALF_GUILTY.getAnimationId();
        }
        player.getPacketDispatch().sendAnimationInterface(expression, interfaceId, 2);
        player.getPacketDispatch().sendItemOnInterface(-1, 1, interfaceId, 1);
        if (npc) {
            player.getPacketDispatch().sendNpcOnInterface(npcId, interfaceId, 2);
            player.getPacketDispatch().sendString(NPCDefinition.forId(npcId).getName(), interfaceId, 3);
        } else {
            player.getPacketDispatch().sendPlayerOnInterface(interfaceId, 2);
            player.getPacketDispatch().sendString(player.getUsername(), interfaceId, 3);
        }
        for (int i = 0; i < messages.length; i++) {
            player.getPacketDispatch().sendString(doSubstitutions(player, messages[i]), interfaceId, (i + 4));
        }
        player.getInterfaceManager().openChatbox(interfaceId);
        player.getPacketDispatch().sendInterfaceConfig(player.getInterfaceManager().getChatbox().getId(), 3, false);
        return player.getInterfaceManager().getChatbox();
    }

    public Component sendDialogues(int ticks, int npcId, int expression, boolean hide, String... messages) {
        if (messages.length < 1 || messages.length > 4) {
            System.err.println("Invalid amount of messages: " + messages.length);
            return null;
        }

        final boolean npc = npcId > -1;
        final int baseInterfaceId = npc ? 240 : 63;
        final int finalExpression = (expression == -1) ? FaceAnim.HALF_GUILTY.getAnimationId() : expression;
        final int finalInterfaceId = baseInterfaceId + messages.length + (hide ? 4 : 0);

        GameWorld.getPulser().submit(new Pulse(ticks, player) {
            @Override
            public boolean pulse() {
                player.getPacketDispatch().sendAnimationInterface(finalExpression, finalInterfaceId, 2);
                player.getPacketDispatch().sendItemOnInterface(-1, 1, finalInterfaceId, 1);

                if (npc) {
                    player.getPacketDispatch().sendNpcOnInterface(npcId, finalInterfaceId, 2);
                    player.getPacketDispatch().sendString(NPCDefinition.forId(npcId).getName(), finalInterfaceId, 3);
                } else {
                    player.getPacketDispatch().sendPlayerOnInterface(finalInterfaceId, 2);
                    player.getPacketDispatch().sendString(player.getUsername(), finalInterfaceId, 3);
                }

                for (int i = 0; i < messages.length; i++) {
                    player.getPacketDispatch().sendString(doSubstitutions(player, messages[i]), finalInterfaceId, (i + 4));
                }

                player.getInterfaceManager().openChatbox(finalInterfaceId);
                player.getPacketDispatch().sendInterfaceConfig(player.getInterfaceManager().getChatbox().getId(), 3, false);

                return true;
            }
        });

        return null;
    }

    public Component sendOptions(Object title, String... options) {
        int interfaceId = 224 + (2 * options.length);
        if (options.length < 2 || options.length > 5) {
            return null;
        }
        if (title != null) {
            player.getPacketDispatch().sendString(title.toString(), interfaceId, 1);
        }
        for (int i = 0; i < options.length; i++) {
            player.getPacketDispatch().sendString(options[i].toString(), interfaceId, i + 2);
        }
        player.getInterfaceManager().openChatbox(interfaceId);
        return player.getInterfaceManager().getChatbox();
    }

    public void sendInput(boolean string, Object... objects) {
        player.getPacketDispatch().sendRunScript(string ? 109 : 108, "s", objects);
    }

    public void sendBoldInput(Object... objects) {
        player.getPacketDispatch().sendRunScript(102, "s", objects);
    }

    public void sendLongInput(Object... objects) {
        player.getPacketDispatch().sendRunScript(110, "s", objects);
    }

    public void sendMessageInput(String receiver) {
        player.getPacketDispatch().sendRunScript(107, "s", receiver);
    }

    public static boolean contains(int id) {
        return PLUGINS.containsKey(id);
    }

    public Dialogue getDialogue() {
        return dialogue;
    }

    public static int getDialogueKey(String name) {
        return 1 << 16 | name.hashCode();
    }

    public void addAction(DialogueAction action) {
        actions.add(action);
    }

    public List<DialogueAction> getActions() {
        return actions;
    }
}
