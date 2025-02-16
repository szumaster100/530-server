package core.cache.def.impl;

import core.api.EquipmentSlot;
import core.cache.Cache;
import core.cache.def.Definition;
import core.cache.misc.buffer.ByteBufferUtils;
import core.game.container.Container;
import core.game.interaction.OptionHandler;
import core.game.node.entity.player.Player;
import core.game.node.entity.skill.Skills;
import core.game.node.item.Item;
import core.game.node.item.ItemPlugin;
import core.game.system.config.ItemConfigParser;
import core.tools.Log;
import core.tools.StringUtils;
import org.rs.consts.Items;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static core.api.ContentAPIKt.equipSlot;
import static core.api.ContentAPIKt.log;

public class ItemDefinition extends Definition<Item> {
    private static final Map<Integer, ItemDefinition> DEFINITIONS = new HashMap<>();
    private static final Map<String, OptionHandler> OPTION_HANDLERS = new HashMap<>();
    private int interfaceModelId;
    private int modelZoom;
    private int modelRotationX;
    private int modelRotationY;
    private int modelOffsetX;
    private int modelOffsetY;
    private boolean stackable;
    private int value = 1;
    private boolean membersOnly;
    private int maleWornModelId1 = -1;
    private int femaleWornModelId1 = -1;
    private int maleWornModelId2 = -1;
    private int femaleWornModelId2 = -1;
    private int maleWornModelId3 = -1;
    private int femaleWornModelId3 = -1;
    private int maleWornModelId4 = -1;
    private int femaleWornModelId4 = -1;
    private String[] groundActions;
    private short[] originalModelColors;
    private short[] modifiedModelColors;
    private short[] textureColour1;
    private short[] textureColour2;
    private byte[] unknownArray1;
    private int[] unknownArray2;
    private int[][] unknownArray3;
    private boolean unnoted = true;
    private int colourEquip1 = -1;
    private int colourEquip2;
    private int noteId = -1;
    private int noteTemplateId = -1;
    private int[] stackIds;
    private int[] stackAmounts;
    private int teamId;
    private int lendId = -1;
    private int lendTemplateId = -1;
    private int recolourId = -1;
    private int recolourTemplateId = -1;
    private int equipId;
    private HashMap<Integer, Integer> itemRequirements;
    private HashMap<Integer, Object> clientScriptData;
    private int itemType;

    public ItemDefinition() {
        groundActions = new String[]{null, null, "take", null, null};
        options = new String[]{null, null, null, null, "drop"};
    }

    public static void parse() {
        for (int itemId = 0; itemId < Cache.getItemDefinitionsSize(); itemId++) {
            byte[] data = Cache.getIndexes()[19].getFileData(itemId >>> 8, itemId & 0xFF);
            if (data == null) {
                ItemDefinition.getDefinitions().put(itemId, new ItemDefinition());
                continue;
            }
            ItemDefinition def = ItemDefinition.parseDefinition(itemId, ByteBuffer.wrap(data));
            if (def == null) {
                log(ItemDefinition.class, Log.ERR, "Could not load item definitions for id " + itemId + " - no definitions found!");
                return;
            }
            if (itemId == 14958)
                def.setStackable(true);

            ItemDefinition.getDefinitions().put(itemId, def);
        }
        ItemDefinition.defineTemplates();
    }

    public static ItemDefinition forId(int itemId) {
        ItemDefinition def = DEFINITIONS.get(itemId);
        if (def != null) {
            return def;
        }
        return new ItemDefinition();
    }

    public static ItemDefinition parseDefinition(int itemId, ByteBuffer buffer) {
        ItemDefinition def = new ItemDefinition();
        def.id = itemId;
        while (true) {
            int opcode = buffer.get() & 0xFF;
            if (opcode == 0) {
                break;
            } else if (opcode == 1) {
                def.interfaceModelId = buffer.getShort() & 0xFFFF;
            } else if (opcode == 2) {
                def.name = ByteBufferUtils.getString(buffer);
            } else if (opcode == 3) {
                def.handlers.put("examine", ByteBufferUtils.getString(buffer)); // Examine
                // info.
            } else if (opcode == 4) {
                def.modelZoom = buffer.getShort() & 0xFFFF;
            } else if (opcode == 5) {
                def.modelRotationX = buffer.getShort() & 0xFFFF;
            } else if (opcode == 6) {
                def.modelRotationY = buffer.getShort() & 0xFFFF;
            } else if (opcode == 7) {
                def.modelOffsetX = buffer.getShort() & 0xFFFF;
                if (def.modelOffsetX > 32767)
                    def.modelOffsetX -= 65536;
            } else if (opcode == 8) {
                def.modelOffsetY = buffer.getShort() & 0xFFFF;
                if (def.modelOffsetY > 32767) {
                    def.modelOffsetY -= 65536;
                }
            } else if (opcode == 10) {
                // buffer.getShort(); //10 is unused opcode.
            } else if (opcode == 11) {
                def.stackable = true;
            } else if (opcode == 12) {
                def.value = ((buffer.get() & 0xFF) << 24) + ((buffer.get() & 0xFF) << 16) + ((buffer.get() & 0xFF) << 8) + (buffer.get() & 0xFF);
            } else if (opcode == 16) {
                def.membersOnly = true;
            } else if (opcode == 23) {
                def.maleWornModelId1 = buffer.getShort() & 0xFFFF;
                // buffer.get();
            } else if (opcode == 24) {
                def.femaleWornModelId1 = buffer.getShort() & 0xFFFF;
            } else if (opcode == 25) {
                def.maleWornModelId2 = buffer.getShort() & 0xFFFF;
                // buffer.get();
            } else if (opcode == 26) {
                def.femaleWornModelId2 = buffer.getShort() & 0xFFFF;
            } else if (opcode >= 30 && opcode < 35) {
                def.groundActions[opcode - 30] = ByteBufferUtils.getString(buffer);
            } else if (opcode >= 35 && opcode < 40) {
                def.options[opcode - 35] = ByteBufferUtils.getString(buffer);
            } else if (opcode == 40) {
                int length = buffer.get() & 0xFF;
                def.originalModelColors = new short[length];
                def.modifiedModelColors = new short[length];
                for (int index = 0; index < length; index++) {
                    def.originalModelColors[index] = buffer.getShort();
                    def.modifiedModelColors[index] = buffer.getShort();
                }
            } else if (opcode == 41) {
                int length = buffer.get() & 0xFF;
                def.textureColour1 = new short[length];
                def.textureColour2 = new short[length];
                for (int index = 0; index < length; index++) {
                    def.textureColour1[index] = buffer.getShort();
                    def.textureColour2[index] = buffer.getShort();
                }
            } else if (opcode == 42) {
                int length = buffer.get() & 0xFF;
                def.unknownArray1 = new byte[length];
                for (int index = 0; index < length; index++)
                    def.unknownArray1[index] = buffer.get();
            } else if (opcode == 65) {
                def.unnoted = true;
            } else if (opcode == 78) {
                def.colourEquip1 = buffer.getShort() & 0xFFFF;
            } else if (opcode == 79) {
                def.colourEquip2 = buffer.getShort() & 0xFFFF;
            } else if (opcode == 90) {
                def.setMaleWornModelId3(buffer.getShort());
            } else if (opcode == 91) {
                def.setFemaleWornModelId3(buffer.getShort());
            } else if (opcode == 92) {
                def.setMaleWornModelId4(buffer.getShort());
            } else if (opcode == 93) {
                def.setFemaleWornModelId4(buffer.getShort());
            } else if (opcode == 95) {
                buffer.getShort();
            } else if (opcode == 96) {
                def.itemType = buffer.get();
            } else if (opcode == 97) {
                def.noteId = buffer.getShort() & 0xFFFF;
            } else if (opcode == 98) {
                def.noteTemplateId = buffer.getShort() & 0xFFFF;
            } else if (opcode >= 100 && opcode < 110) {
                if (def.stackIds == null) {
                    def.stackIds = new int[10];
                    def.stackAmounts = new int[10];
                }
                def.stackIds[opcode - 100] = buffer.getShort() & 0xFFFF;
                def.stackAmounts[opcode - 100] = buffer.getShort() & 0xFFFF;
            } else if (opcode == 110) {
                buffer.getShort();
            } else if (opcode == 111) {
                buffer.getShort();
            } else if (opcode == 112) {
                buffer.getShort();
            } else if (opcode == 113) {
                buffer.get();
            } else if (opcode == 114) {
                buffer.get();
            } else if (opcode == 115) {
                def.teamId = buffer.get();
            } else if (opcode == 121) {
                def.lendId = buffer.getShort() & 0xFFFF;
            } else if (opcode == 122) {
                def.lendTemplateId = buffer.getShort() & 0xFFFF;
            } else if (opcode == 125) {
                buffer.get();
                buffer.get();
                buffer.get();
            } else if (opcode == 126) {
                buffer.get();
                buffer.get();
                buffer.get();
            } else if (opcode == 127) {
                buffer.get();
                buffer.getShort();
            } else if (opcode == 128) {
                buffer.get();
                buffer.getShort();
            } else if (opcode == 129) {
                buffer.get();
                buffer.getShort();
            } else if (opcode == 130) {
                buffer.get();
                buffer.getShort();
            } else if (opcode == 249) {
                int length = buffer.get() & 0xFF;
                if (def.clientScriptData == null) {
                    def.clientScriptData = new HashMap<Integer, Object>();
                }
                for (int index = 0; index < length; index++) {
                    boolean string = (buffer.get() & 0xFF) == 1;
                    int key = ByteBufferUtils.getMedium(buffer);
                    Object value = string ? ByteBufferUtils.getString(buffer) : buffer.getInt();
                    def.clientScriptData.put(key, value);
                }
            } else {

                break;
            }
        }
        return def;
    }

    public static void defineTemplates() {
        int equipId = 0;
        for (int i = 0; i < Cache.getItemDefinitionsSize(); i++) {
            ItemDefinition def = forId(i);
            if (def.noteTemplateId != -1) {
                def.transferNoteDefinition(forId(def.noteId), forId(def.noteTemplateId));
            }
            if (def.lendTemplateId != -1) {
                def.transferLendDefinition(forId(def.lendId), forId(def.lendTemplateId));
            }
            if (def.recolourTemplateId != -1) {
                def.transferRecolourDefinition(forId(def.recolourId), forId(def.recolourTemplateId));
            }
            if (def != null && (def.maleWornModelId1 >= 0 || def.maleWornModelId2 >= 0)) {
                def.equipId = equipId++;
            }
        }
        forId(2513).equipId = forId(3140).equipId;
    }

    public void transferNoteDefinition(ItemDefinition reference, ItemDefinition templateReference) {
        membersOnly = reference.membersOnly;
        interfaceModelId = templateReference.interfaceModelId;
        originalModelColors = templateReference.originalModelColors;
        name = reference.name;
        modelOffsetY = templateReference.modelOffsetY;
        textureColour1 = templateReference.textureColour1;
        value = reference.value;
        modelRotationY = templateReference.modelRotationY;
        stackable = true;
        unnoted = false;
        modifiedModelColors = templateReference.modifiedModelColors;
        modelRotationX = templateReference.modelRotationX;
        modelZoom = templateReference.modelZoom;
        textureColour1 = templateReference.textureColour1;
        handlers.put(ItemConfigParser.TRADEABLE, true);
    }

    public void transferLendDefinition(ItemDefinition reference, ItemDefinition templateReference) {
        femaleWornModelId1 = reference.femaleWornModelId1;
        maleWornModelId2 = reference.maleWornModelId2;
        membersOnly = reference.membersOnly;
        interfaceModelId = templateReference.interfaceModelId;
        textureColour2 = reference.textureColour2;
        groundActions = reference.groundActions;
        unknownArray1 = reference.unknownArray1;
        modelRotationX = templateReference.modelRotationX;
        modelRotationY = templateReference.modelRotationY;
        originalModelColors = reference.originalModelColors;
        name = reference.name;
        maleWornModelId1 = reference.maleWornModelId1;
        colourEquip1 = reference.colourEquip1;
        teamId = reference.teamId;
        modelOffsetY = templateReference.modelOffsetY;
        clientScriptData = reference.clientScriptData;
        modifiedModelColors = reference.modifiedModelColors;
        colourEquip2 = reference.colourEquip2;
        modelOffsetX = templateReference.modelOffsetX;
        textureColour1 = reference.textureColour1;
        value = 0;
        modelZoom = templateReference.modelZoom;
        options = new String[5];
        femaleWornModelId2 = reference.femaleWornModelId2;
        if (reference.options != null) {
            options = reference.options.clone();
        }
    }

    public void transferRecolourDefinition(ItemDefinition reference, ItemDefinition templateReference) {
        femaleWornModelId2 = reference.femaleWornModelId2;
        options = new String[5];
        modelRotationY = templateReference.modelRotationY;
        name = reference.name;
        maleWornModelId1 = reference.maleWornModelId1;
        modelOffsetY = templateReference.modelOffsetY;
        femaleWornModelId1 = reference.femaleWornModelId1;
        maleWornModelId2 = reference.maleWornModelId2;
        modelOffsetX = templateReference.modelOffsetX;
        unknownArray1 = reference.unknownArray1;
        stackable = reference.stackable;
        modelRotationX = templateReference.modelRotationX;
        textureColour1 = reference.textureColour1;
        colourEquip1 = reference.colourEquip1;
        textureColour2 = reference.textureColour2;
        modifiedModelColors = reference.modifiedModelColors;
        modelZoom = templateReference.modelZoom;
        colourEquip2 = reference.colourEquip2;
        teamId = reference.teamId;
        value = 0;
        groundActions = reference.groundActions;
        originalModelColors = reference.originalModelColors;
        membersOnly = reference.membersOnly;
        clientScriptData = reference.clientScriptData;
        interfaceModelId = templateReference.interfaceModelId;
        if (reference.options != null) {
            options = reference.options.clone();
        }
    }

    public boolean hasRequirement(Player player, boolean wield, boolean message) {
        Map<Integer, Integer> requirements = getConfiguration(ItemConfigParser.REQUIREMENTS);
        if (requirements == null) {
            return true;
        }
        for (int skill : requirements.keySet()) {
            if (skill < 0 || skill >= Skills.SKILL_NAME.length) {
                continue;
            }
            int level = requirements.get(skill);
            if (player.getSkills().getStaticLevel(skill) < level) {
                if (message) {
                    String name = Skills.SKILL_NAME[skill];
                    player.getPacketDispatch().sendMessage("You need a" + (StringUtils.isPlusN(name) ? "n " : " ") + name + " level of " + level + " to " + (wield ? "wear " : "use ") + "this.");
                }
                return false;
            }
        }
        return true;
    }

    public static boolean canEnterEntrana(Player player) {
        Container[] container = new Container[]{player.getInventory(), player.getEquipment()};
        for (Container c : container) {
            for (Item i : c.toArray()) {
                if (i == null) {
                    continue;
                }
                if (!i.getDefinition().isAllowedOnEntrana()) {
                    return false;
                }
            }
        }
        return true;
    }

    private static final HashSet<Integer> permittedItems = new HashSet(Arrays.asList(
            Items.PENANCE_GLOVES_10553,
            Items.ICE_GLOVES_1580,
            Items.BOOTS_OF_LIGHTNESS_88,
            Items.CLIMBING_BOOTS_3105,
            Items.SPOTTED_CAPE_10069,
            Items.SPOTTIER_CAPE_10071,
            Items.SARADOMIN_CAPE_2412,
            Items.ZAMORAK_CAPE_2414,
            Items.GUTHIX_CAPE_2413,
            Items.SARADOMIN_CLOAK_10446,
            Items.ZAMORAK_CLOAK_10450,
            Items.GUTHIX_CLOAK_10448,
            Items.HOLY_BOOK_3840,
            Items.DAMAGED_BOOK_3839,
            Items.UNHOLY_BOOK_3842,
            Items.DAMAGED_BOOK_3841,
            Items.BOOK_OF_BALANCE_3844,
            Items.DAMAGED_BOOK_3843,
            Items.WIZARD_BOOTS_2579,
            Items.COMBAT_BRACELET1_11124,
            Items.COMBAT_BRACELET2_11122,
            Items.COMBAT_BRACELET3_11120,
            Items.COMBAT_BRACELET4_11118,
            Items.REGEN_BRACELET_11133,
            Items.WARLOCK_CLOAK_14081,
            Items.WARLOCK_LEGS_14077,
            Items.WARLOCK_TOP_14076,
            Items.MONKS_ROBE_542,
            Items.MONKS_ROBE_544,
            Items.HAM_SHIRT_4298,
            Items.HAM_ROBE_4300,
            Items.HAM_HOOD_4302,
            Items.HAM_CLOAK_4304,
            Items.HAM_LOGO_4306,
            Items.GLOVES_4308,
            Items.BOOTS_4310
    ));
    private static final HashSet<Integer> forbiddenItems = new HashSet(Arrays.asList(

            Items.DWARF_CANNON_SET_11967,
            Items.CANNON_BARRELS_10,
            Items.CANNON_BASE_6,
            Items.CANNON_STAND_8,
            Items.CANNON_FURNACE_12,
            Items.COOKING_GAUNTLETS_775,
            Items.CHAOS_GAUNTLETS_777,
            Items.GOLDSMITH_GAUNTLETS_776,
            Items.KARAMJA_GLOVES_1_11136,
            Items.KARAMJA_GLOVES_2_11138,
            Items.KARAMJA_GLOVES_3_11140,
            Items.VYREWATCH_TOP_9634,
            Items.VYREWATCH_LEGS_9636,
            Items.VYREWATCH_SHOES_9638
    ));

    public boolean isAllowedOnEntrana() {
        if (permittedItems.contains(getId())) {
            return true;
        }
        if (forbiddenItems.contains(getId())) {
            return false;
        }
        if (equipSlot(getId()) == EquipmentSlot.AMMO) {
            return true;
        }
        if (getName().toLowerCase().startsWith("ring") || getName().toLowerCase().startsWith("amulet")) {
            return true;
        }
        int[] bonuses = getConfiguration(ItemConfigParser.BONUS);
        return bonuses == null || Arrays.stream(bonuses).allMatch(x -> x == 0);
    }

    public int getRequirement(int skillId) {
        Map<Integer, Integer> requirements = getConfiguration(ItemConfigParser.REQUIREMENTS);
        if (requirements == null) {
            return 0;
        }
        Integer level = requirements.get(skillId);
        return level == null ? 0 : level;
    }

    public int getRenderAnimationId() {
        return getConfiguration(ItemConfigParser.RENDER_ANIM_ID, 1426);
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public int getInterfaceModelId() {
        return interfaceModelId;
    }

    public void setInterfaceModelId(int interfaceModelId) {
        this.interfaceModelId = interfaceModelId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayerType() {
        return itemType == 0;
    }

    public int getModelZoom() {
        return modelZoom;
    }

    public void setModelZoom(int modelZoom) {
        this.modelZoom = modelZoom;
    }

    public int getModelRotationX() {
        return modelRotationX;
    }

    public void setModelRotationX(int modelRotation1) {
        this.modelRotationX = modelRotation1;
    }

    public int getModelRotationY() {
        return modelRotationY;
    }

    public void setModelRotationY(int modelRotation2) {
        this.modelRotationY = modelRotation2;
    }

    public int getModelOffset1() {
        return modelOffsetX;
    }

    public void setModelOffset1(int modelOffset1) {
        this.modelOffsetX = modelOffset1;
    }

    public int getModelOffset2() {
        return modelOffsetY;
    }

    public void setModelOffset2(int modelOffset2) {
        this.modelOffsetY = modelOffset2;
    }

    public boolean isStackable() {
        return stackable || !this.unnoted;
    }

    public void setStackable(boolean stackable) {
        this.stackable = stackable;
    }

    public int getValue() {
        return value;
    }

    public boolean hasShopCurrencyValue(String currency) {
        return getHandlers().getOrDefault(currency, "0") != "0";
    }

    public boolean hasShopCurrencyValue(int currency) {
        switch (currency) {
            case Items.COINS_995:
                return isTradeable();
            case Items.TOKKUL_6529:
                return hasShopCurrencyValue(ItemConfigParser.TOKKUL_PRICE);
            case Items.ARCHERY_TICKET_1464:
                return hasShopCurrencyValue(ItemConfigParser.ARCHERY_TICKET_PRICE);
            case Items.CASTLE_WARS_TICKET_4067:
                return hasShopCurrencyValue(ItemConfigParser.CASTLE_WARS_TICKET_PRICE);
            default:
                return false;
        }
    }

    public int getMaxValue() {
        if ((int) (value * 1.05) <= 0) {
            return 1;
        }
        return (int) (value * 1.05);
    }

    public int getMinValue() {
        if ((int) (value * .95) <= 0) {
            return 1;
        }
        return (int) (value * .95);
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isMembersOnly() {
        return membersOnly;
    }

    public void setMembersOnly(boolean membersOnly) {
        this.membersOnly = membersOnly;
    }

    public int getMaleWornModelId1() {
        return maleWornModelId1;
    }

    public void setMaleWornModelId1(int maleWornModelId1) {
        this.maleWornModelId1 = maleWornModelId1;
    }

    public int getFemaleWornModelId1() {
        return femaleWornModelId1;
    }

    public void setFemaleWornModelId1(int femaleWornModelId1) {
        this.femaleWornModelId1 = femaleWornModelId1;
    }

    public int getMaleWornModelId2() {
        return maleWornModelId2;
    }

    public void setMaleWornModelId2(int maleWornModelId2) {
        this.maleWornModelId2 = maleWornModelId2;
    }

    public int getFemaleWornModelId2() {
        return femaleWornModelId2;
    }

    public void setFemaleWornModelId2(int femaleWornModelId2) {
        this.femaleWornModelId2 = femaleWornModelId2;
    }

    public String[] getGroundOptions() {
        return groundActions;
    }

    public void setGroundOptions(String[] groundOptions) {
        this.groundActions = groundOptions;
    }

    public String[] getInventoryOptions() {
        return options;
    }

    public void setInventoryOptions(String[] inventoryOptions) {
        this.options = inventoryOptions;
    }

    public short[] getOriginalModelColors() {
        return originalModelColors;
    }

    public void setOriginalModelColors(short[] originalModelColors) {
        this.originalModelColors = originalModelColors;
    }

    public short[] getModifiedModelColors() {
        return modifiedModelColors;
    }

    public void setModifiedModelColors(short[] modifiedModelColors) {
        this.modifiedModelColors = modifiedModelColors;
    }

    public short[] getTextureColour1() {
        return textureColour1;
    }

    public void setTextureColour1(short[] textureColour1) {
        this.textureColour1 = textureColour1;
    }

    public short[] getTextureColour2() {
        return textureColour2;
    }

    public void setTextureColour2(short[] textureColour2) {
        this.textureColour2 = textureColour2;
    }

    public byte[] getUnknownArray1() {
        return unknownArray1;
    }

    public void setUnknownArray1(byte[] unknownArray1) {
        this.unknownArray1 = unknownArray1;
    }

    public int[] getUnknownArray2() {
        return unknownArray2;
    }

    public void setUnknownArray2(int[] unknownArray2) {
        this.unknownArray2 = unknownArray2;
    }

    public boolean isUnnoted() {
        return unnoted;
    }

    public void setUnnoted(boolean unnoted) {
        this.unnoted = unnoted;
    }

    public int getColourEquip1() {
        return colourEquip1;
    }

    public void setColourEquip1(int colourEquip1) {
        this.colourEquip1 = colourEquip1;
    }

    public int getColourEquip2() {
        return colourEquip2;
    }

    public void setColourEquip2(int colourEquip2) {
        this.colourEquip2 = colourEquip2;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getNoteTemplateId() {
        return noteTemplateId;
    }

    public void setNoteTemplateId(int noteTemplateId) {
        this.noteTemplateId = noteTemplateId;
    }

    public int[] getStackIds() {
        return stackIds;
    }

    public void setStackIds(int[] stackIds) {
        this.stackIds = stackIds;
    }

    public int[] getStackAmounts() {
        return stackAmounts;
    }

    public void setStackAmounts(int[] stackAmounts) {
        this.stackAmounts = stackAmounts;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getLendId() {
        return lendId;
    }

    public void setLendId(int lendId) {
        this.lendId = lendId;
    }

    public int getLendTemplateId() {
        return lendTemplateId;
    }

    public int getModelOffsetX() {return modelOffsetX;}

    public int getModelOffsetY() {return modelOffsetY;}

    public void setLendTemplateId(int lendTemplateId) {
        this.lendTemplateId = lendTemplateId;
    }

    public int getRecolourId() {
        return recolourId;
    }

    public void setRecolourId(int recolourId) {
        this.recolourId = recolourId;
    }

    public int getRecolourTemplateId() {
        return recolourTemplateId;
    }

    public void setRecolourTemplateId(int recolourTemplateId) {
        this.recolourTemplateId = recolourTemplateId;
    }

    public int getEquipId() {
        return equipId;
    }

    public void setEquipId(int equipId) {
        this.equipId = equipId;
    }

    public HashMap<Integer, Object> getClientScriptData() {
        return clientScriptData;
    }

    public void setClientScriptData(HashMap<Integer, Object> clientScriptData) {
        this.clientScriptData = clientScriptData;
    }

    public int getAlchemyValue(boolean highAlchemy) {
        if (!unnoted && noteId > -1) {
            return forId(noteId).getAlchemyValue(highAlchemy);
        }
        if (highAlchemy) {
            return getConfiguration(ItemConfigParser.HIGH_ALCHEMY, (int) Math.rint(value * 0.6));
        }
        return getConfiguration(ItemConfigParser.LOW_ALCHEMY, (int) Math.rint(value * 0.4));
    }

    public boolean isAlchemizable() {
        if (!getConfiguration(ItemConfigParser.ALCHEMIZABLE, false)) {
            return false;
        }
        return true;
    }

    public boolean isTradeable() {
        if (hasDestroyAction() && !getName().contains("impling jar")) {
            return false;
        }
        if (!getConfiguration(ItemConfigParser.TRADEABLE, false)) {
            return false;
        }
        return true;
    }

    public boolean hasAction(String optionName) {
        if (options == null) {
            return false;
        }
        for (String action : options) {
            if (action == null) {
                continue;
            }
            if (action.equalsIgnoreCase(optionName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDestroyAction() {
        return hasAction("destroy") || hasAction("dissolve");
    }

    public boolean hasWearAction() {
        if (options == null) {
            return false;
        }
        for (String action : options) {
            if (action == null) {
                continue;
            }
            if (action.equalsIgnoreCase("wield") || action.equalsIgnoreCase("wear") || action.equalsIgnoreCase("equip")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSpecialBar() {
        if (clientScriptData == null) {
            return false;
        }
        Object specialBar = clientScriptData.get(686);
        if (specialBar != null && specialBar instanceof Integer) {
            return (Integer) specialBar == 1;
        }
        return false;
    }

    public int getQuestId() {
        if (clientScriptData == null) {
            return -1;
        }
        Object questId = clientScriptData.get(861);
        if (questId != null && questId instanceof Integer) {
            return (Integer) questId;
        }
        return -1;
    }

    public int getArchiveId() {
        return id >>> 8;
    }

    public int getFileId() {
        return 0xff & id;
    }

    public static Map<Integer, ItemDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public static OptionHandler getOptionHandler(int nodeId, String name) {
        ItemDefinition def = forId(nodeId);
        if (def == null) {
            if (nodeId == 22937)
                log(ItemDefinition.class, Log.ERR, "[ItemDefinition] No definition for item id " + nodeId + "!");
            return null;
        }
        OptionHandler handler = def.getConfiguration("option:" + name);
        if (handler != null) {
            return handler;
        }
        return OPTION_HANDLERS.get(name);
    }

    private static final String[] BONUS_NAMES = {"Stab: ", "Slash: ", "Crush: ", "Magic: ", "Ranged: ", "Stab: ", "Slash: ", "Crush: ", "Magic: ", "Ranged: ", "Summoning: ", "Strength: ", "Prayer: "};

    public static void statsUpdate(Player player) {
        if (!player.getAttribute("equip_stats_open", false)) {
            return;
        }
        int index = 0;
        int[] bonuses = player.getProperties().getBonuses();
        for (int i = 36; i < 50; i++) {
            if (i == 47) {
                continue;
            }
            int bonus = bonuses[index];
            String bonusValue = bonus > -1 ? ("+" + bonus) : Integer.toString(bonus);
            player.getPacketDispatch().sendString(BONUS_NAMES[index++] + bonusValue, 667, i);
        }
        player.getPacketDispatch().sendString("Attack bonus", 667, 34);
    }

    public boolean hasPlugin() {
        return getItemPlugin() != null;
    }

    public ItemPlugin getItemPlugin() {
        return getConfiguration("wrapper", null);
    }

    public void setItemPlugin(ItemPlugin plugin) {
        getHandlers().put("wrapper", plugin);
    }

    public static boolean setOptionHandler(String name, OptionHandler handler) {
        return OPTION_HANDLERS.put(name, handler) != null;
    }

    public static Map<String, OptionHandler> getOptionHandlers() {
        return OPTION_HANDLERS;
    }

    public HashMap<Integer, Integer> getItemRequirements() {
        return itemRequirements;
    }

    public void setItemRequirements(HashMap<Integer, Integer> itemRequirements) {
        this.itemRequirements = itemRequirements;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getFemaleWornModelId3() {
        return femaleWornModelId3;
    }

    public void setFemaleWornModelId3(int femaleWornModelId3) {
        this.femaleWornModelId3 = femaleWornModelId3;
    }

    public int getFemaleWornModelId4() {
        return femaleWornModelId4;
    }

    public void setFemaleWornModelId4(int femaleWornModelId4) {
        this.femaleWornModelId4 = femaleWornModelId4;
    }

    public int getMaleWornModelId3() {
        return maleWornModelId3;
    }

    public void setMaleWornModelId3(int maleWornModelId3) {
        this.maleWornModelId3 = maleWornModelId3;
    }

    public int getMaleWornModelId4() {
        return maleWornModelId4;
    }

    public void setMaleWornModelId4(int maleWornModelId4) {
        this.maleWornModelId4 = maleWornModelId4;
    }

    @Override
    public String getExamine() {
        examine = super.getExamine();
        if (!isUnnoted()) {
            examine = "Swap this note at any bank for the equivalent item.";
        }
        return examine;
    }
}
