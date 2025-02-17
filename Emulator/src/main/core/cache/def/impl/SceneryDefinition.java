package core.cache.def.impl;

import core.cache.Cache;
import core.cache.def.Definition;
import core.cache.misc.buffer.ByteBufferUtils;
import core.game.interaction.OptionHandler;
import core.game.node.entity.player.Player;
import core.game.node.scenery.Scenery;
import core.game.world.GameWorld;
import core.tools.Log;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.getVarp;
import static core.api.ContentAPIKt.log;

public class SceneryDefinition extends Definition<Scenery> {
    private static final Map<Integer, SceneryDefinition> DEFINITIONS = new HashMap<Integer, SceneryDefinition>();
    private static final Map<String, OptionHandler> OPTION_HANDLERS = new HashMap<>();
    private short[] originalColors;
    public int[] configObjectIds;
    private int[] modelIds;
    private int[] modelConfiguration;
    static int anInt3832;
    int[] anIntArray3833 = null;
    private int anInt3834;
    int anInt3835;
    static int anInt3836;
    private byte aByte3837;
    int anInt3838 = -1;
    boolean mirrored;
    private int contrast;
    private int modelSizeZ;
    static int anInt3842;
    static int anInt3843;
    int anInt3844;
    boolean aBoolean3845;
    static int anInt3846;
    private byte aByte3847;
    private byte aByte3849;
    int anInt3850;
    int anInt3851;
    public boolean blocksLand;
    public boolean aBoolean3853;
    int anInt3855;
    public boolean ignoreOnRoute;
    int anInt3857;
    private byte[] recolourPalette;
    int[] anIntArray3859;
    int anInt3860;
    int varbitIndex;
    private short[] modifiedColors;
    int anInt3865;
    boolean aBoolean3866;
    boolean aBoolean3867;
    public boolean projectileClipped;
    private int[] anIntArray3869;
    boolean aBoolean3870;
    public int sizeY;
    boolean castsShadow;
    boolean membersOnly;
    public boolean boolean1;
    private int anInt3875;
    public int animationId;
    private int anInt3877;
    private int brightness;
    public int solid;
    private int anInt3881;
    private int anInt3882;
    private int offsetX;
    Object loader;
    private int offsetZ;
    public int sizeX;
    public boolean aBoolean3891;
    int offsetMultiplier;
    public int interactive;
    boolean aBoolean3894;
    boolean aBoolean3895;
    int anInt3896;
    int configId;
    private byte[] aByteArray3899;
    int anInt3900;
    private int modelSizeX;
    int anInt3904;
    int anInt3905;
    boolean aBoolean3906;
    int[] anIntArray3908;
    private byte contouredGround;
    int anInt3913;
    private byte aByte3914;
    private int offsetY;
    private int[][] anIntArrayArray3916;
    private int modelSizeY;
    private short[] modifiedTextureColours;
    private short[] originalTextureColours;
    int anInt3921;
    private Object aClass194_3922;
    boolean aBoolean3923;
    boolean aBoolean3924;
    int walkingFlag;
    private boolean hasHiddenOptions;
    private short mapIcon;

    public SceneryDefinition() {
        anInt3835 = -1;
        anInt3860 = -1;
        varbitIndex = -1;
        aBoolean3866 = false;
        anInt3851 = -1;
        anInt3865 = 255;
        aBoolean3845 = false;
        aBoolean3867 = false;
        anInt3850 = 0;
        anInt3844 = -1;
        anInt3881 = 0;
        anInt3857 = -1;
        castsShadow = true;
        anInt3882 = -1;
        anInt3834 = 0;
        options = new String[5];
        anInt3875 = 0;
        mirrored = false;
        anIntArray3869 = null;
        sizeY = 1;
        boolean1 = false;
        projectileClipped = true;
        offsetX = 0;
        aBoolean3895 = true;
        contrast = 0;
        aBoolean3870 = false;
        offsetZ = 0;
        aBoolean3853 = true;
        blocksLand = false;
        solid = 2;
        anInt3855 = -1;
        brightness = 0;
        anInt3904 = 0;
        sizeX = 1;
        animationId = -1;
        ignoreOnRoute = false;
        aBoolean3891 = false;
        anInt3905 = 0;
        name = "null";
        anInt3913 = -1;
        aBoolean3906 = false;
        membersOnly = false;
        aByte3914 = (byte) 0;
        offsetY = 0;
        anInt3900 = 0;
        interactive = -1;
        aBoolean3894 = false;
        contouredGround = (byte) 0;
        anInt3921 = 0;
        modelSizeX = 128;
        configId = -1;
        anInt3877 = 0;
        walkingFlag = 0;
        offsetMultiplier = 64;
        aBoolean3923 = false;
        aBoolean3924 = false;
        modelSizeZ = 128;
        modelSizeY = 128;
        mapIcon = -1;
    }

    public static void main(String... args) throws Throwable {
        GameWorld.prompt(false);
        // if (true) {
        // for (int id = 0; id <= 27325; id++) {
        // ObjectDefinition def = ObjectDefinition.forId(id);
        // if (def.mapIcon > 69) {

        // def.mapIcon);
        // }
        // }
        // return; 2105
        // }

    }

    public static void parse() throws Throwable {
        for (int objectId = 0; objectId < Cache.getObjectDefinitionsSize(); objectId++) {
            byte[] data = Cache.getIndexes()[16].getFileData(getContainerId(objectId), objectId & 0xff);
            if (data == null) {
                SceneryDefinition.getDefinitions().put(objectId, new SceneryDefinition());
                //SystemLogger.logErr("Could not load object definitions for id " + objectId + " - no data!");
                continue;
            }
            SceneryDefinition def = SceneryDefinition.parseDefinition(objectId, ByteBuffer.wrap(data));
            if (def == null) {
                //	SystemLogger.logErr("Could not load object definitions for id " + objectId + " - no definitions found!");
                return;
            }
            SceneryDefinition.getDefinitions().put(objectId, def);
            data = null;
        }
    }

    public static SceneryDefinition forId(int objectId) {
        SceneryDefinition def = DEFINITIONS.get(objectId);
        if (def != null) {
            return def;
        }
        DEFINITIONS.put(objectId, def = new SceneryDefinition());
        def.id = objectId;
        return def;
    }

    public static SceneryDefinition parseDefinition(int objectId, ByteBuffer buffer) {
        SceneryDefinition def = new SceneryDefinition();
        def.id = objectId;
//		SystemLogger.logErr("----------------------------------------------------\n\n\n");
        while (true) {
            if (!buffer.hasRemaining()) {
                log(SceneryDefinition.class, Log.ERR, "Buffer empty for " + objectId);
                break;
            }
            int opcode = buffer.get() & 0xFF;
            //SystemLogger.logErr("Parsing object " + objectId + " op " + opcode);
            if (opcode == 1 || opcode == 5) {
                int length = buffer.get() & 0xff;
                if (def.modelIds == null) {
                    def.modelIds = new int[length];
                    if (opcode == 1) {
                        def.modelConfiguration = new int[length];
                    }
                    for (int i = 0; i < length; i++) {
                        def.modelIds[i] = buffer.getShort() & 0xFFFF;
                        if (opcode == 1) {
                            def.modelConfiguration[i] = buffer.get() & 0xFF;
                        }
                    }
                } else {
                    buffer.position(buffer.position() + (length * (opcode == 1 ? 3 : 2)));
                }
            } else if (opcode == 2) {
                def.name = ByteBufferUtils.getString(buffer);
            } else if (opcode == 14) {
                def.sizeX = buffer.get() & 0xFF;
            } else if (opcode == 15) {
                def.sizeY = buffer.get() & 0xFF;
            } else if (opcode == 17) {
                def.projectileClipped = false;
                def.solid = 0;
            } else if (opcode == 18) {
                def.projectileClipped = false;
            } else if (opcode == 19) {
                def.interactive = buffer.get() & 0xFF;
            } else if (opcode == 21) {
                def.contouredGround = (byte) 1;
            } else if (opcode == 22) {
                def.aBoolean3867 = true;
            } else if (opcode == 23) {
                def.boolean1 = true;
            } else if (opcode == 24) {
                def.animationId = buffer.getShort() & 0xFFFF;
                if (def.animationId == 65535) {
                    def.animationId = -1;
                }
            } else if (opcode == 27) {
                def.solid = 1;
            } else if (opcode == 28) {
                def.offsetMultiplier = ((buffer.get() & 0xFF) << 2);
            } else if (opcode == 29) {
                def.brightness = buffer.get();
            } else if (opcode == 39) {
                def.contrast = buffer.get() * 5;
            } else if (opcode >= 30 && opcode < 35) {
                def.options[opcode - 30] = ByteBufferUtils.getString(buffer);
                if (def.options[opcode - 30].equals("Hidden")) {
                    def.options[opcode - 30] = null;
                    def.hasHiddenOptions = true;
                }
            } else if (opcode == 40) {
                int length = buffer.get() & 0xFF;
                def.originalColors = new short[length];
                def.modifiedColors = new short[length];
                for (int i = 0; i < length; i++) {
                    def.originalColors[i] = buffer.getShort();
                    def.modifiedColors[i] = buffer.getShort();
                }
            } else if (opcode == 41) {
                int length = buffer.get() & 0xFF;
                def.originalTextureColours = new short[length];
                def.modifiedTextureColours = new short[length];
                for (int i = 0; i < length; i++) {
                    def.originalTextureColours[i] = buffer.getShort();
                    def.modifiedTextureColours[i] = buffer.getShort();
                }
            } else if (opcode == 42) {
                int length = buffer.get() & 0xFF;
                def.recolourPalette = new byte[length];
                for (int i = 0; i < length; i++) {
                    def.recolourPalette[i] = buffer.get();
                }
            } else if (opcode == 60) {
                def.mapIcon = buffer.getShort();
            } else if (opcode == 62) {
                def.mirrored = true;
            } else if (opcode == 64) {
                def.castsShadow = false;
            } else if (opcode == 65) {
                def.modelSizeX = buffer.getShort() & 0xFFFF;
            } else if (opcode == 66) {
                def.modelSizeZ = buffer.getShort() & 0xFFFF;
            } else if (opcode == 67) {
                def.modelSizeY = buffer.getShort() & 0xFFFF;
            } else if (opcode == 68) {
                buffer.getShort();
            } else if (opcode == 69) {
                def.walkingFlag = buffer.get() & 0xFF;
            } else if (opcode == 70) {
                def.offsetX = buffer.getShort() << 2;
            } else if (opcode == 71) {
                def.offsetZ = buffer.getShort() << 2;
            } else if (opcode == 72) {
                def.offsetY = buffer.getShort() << 2;
            } else if (opcode == 73) {
                def.blocksLand = true;
            } else if (opcode == 74) {
                def.ignoreOnRoute = true;
            } else if (opcode == 75) {
                def.anInt3855 = buffer.get() & 0xFF;
            } else if (opcode == 77 || opcode == 92) {
                def.varbitIndex = buffer.getShort() & 0xFFFF;
                if (def.varbitIndex == 65535) {
                    def.varbitIndex = -1;
                }
                def.configId = buffer.getShort() & 0xFFFF;
                if (def.configId == 65535) {
                    def.configId = -1;
                }
                int defaultId = -1;
                if (opcode == 92) {
                    defaultId = buffer.getShort() & 0xFFFF;
                    if (defaultId == 65535) {
                        defaultId = -1;
                    }
                }
                int childrenAmount = buffer.get() & 0xFF;
                def.configObjectIds = new int[childrenAmount + 2];
                for (int index = 0; childrenAmount >= index; index++) {
                    def.configObjectIds[index] = buffer.getShort() & 0xFFFF;
                    if (def.configObjectIds[index] == 65535) {
                        def.configObjectIds[index] = -1;
                    }
                }
                def.configObjectIds[childrenAmount + 1] = defaultId;
            } else if (opcode == 78) {
                def.anInt3860 = buffer.getShort() & 0xFFFF;
                def.anInt3904 = buffer.get() & 0xFF;
            } else if (opcode == 79) {
                def.anInt3900 = buffer.getShort() & 0xFFFF;
                def.anInt3905 = buffer.getShort() & 0xFFFF;
                def.anInt3904 = buffer.get() & 0xFF;
                int length = buffer.get() & 0xFF;
                def.anIntArray3859 = new int[length];
                for (int i = 0; i < length; i++) {
                    def.anIntArray3859[i] = buffer.getShort() & 0xFFFF;
                }
            } else if (opcode == 81) {
                def.contouredGround = (byte) 2;
                def.anInt3882 = 256 * buffer.get() & 0xFF;
            } else if (opcode == 82 || opcode == 88) {
                // Nothing.
            } else if (opcode == 89) {
                def.aBoolean3895 = false;
            } else if (opcode == 90) {
                def.aBoolean3870 = true;
            } else if (opcode == 91) {
                def.membersOnly = true;
            } else if (opcode == 93) {
                def.contouredGround = (byte) 3;
                def.anInt3882 = buffer.getShort() & 0xFFFF;
            } else if (opcode == 94) {
                def.contouredGround = (byte) 4;
            } else if (opcode == 95) {
                def.contouredGround = (byte) 5;
            } else if (opcode == 96 || opcode == 97) {
                //
            } else if (opcode == 100) {
                buffer.get();
                buffer.getShort();
            } else if (opcode == 101) {
                buffer.get();
            } else if (opcode == 102) {
                buffer.getShort();
            } else if (opcode == 249) { // cs2 scripts
                int length = buffer.get() & 0xFF;
                for (int i = 0; i < length; i++) {
                    boolean string = buffer.get() == 1;
                    ByteBufferUtils.getMedium(buffer); // script id
                    if (!string) {
                        buffer.getInt(); // Value
                    } else {
                        ByteBufferUtils.getString(buffer); // value
                    }
                }
            } else {
                if (opcode != 0) {
                    log(SceneryDefinition.class, Log.ERR, "Unhandled object definition opcode: " + opcode);
                }
                break;
            }
        }
        def.configureObject();
        if (def.ignoreOnRoute) {
            def.solid = 0;
            def.projectileClipped = false;
        }
        return def;
    }

    final void configureObject() {
        if (interactive == -1) {
            interactive = 0;
            if (modelIds != null && (getModelConfiguration() == null || getModelConfiguration()[0] == 10)) {
                interactive = 1;
            }
            for (int i = 0; i < 5; i++) {
                if (options[i] != null) {
                    interactive = 1;
                    break;
                }
            }
        }
        if (configObjectIds != null) {
            for (int i = 0; i < configObjectIds.length; ++i) {
                SceneryDefinition def = forId(configObjectIds[i]);
                def.varbitIndex = varbitIndex;
            }
        }
        if (anInt3855 == -1) {
            anInt3855 = solid == 0 ? 0 : 1;
        }
        // Manual changes
        if (id == 31017) {
            sizeX = sizeY = 2;
        }
        if (id == 29292) {
            projectileClipped = false;
        }
    }

    public boolean hasActions() {
        if (interactive > 0) {
            return true;
        }
        if (configObjectIds == null) {
            return hasOptions(false);
        }
        for (int i = 0; i < configObjectIds.length; i++) {
            if (configObjectIds[i] != -1) {
                SceneryDefinition def = forId(configObjectIds[i]);
                if (def.hasOptions(false)) {
                    return true;
                }
            }
        }
        return hasOptions(false);
    }

    public SceneryDefinition getChildObject(Player player) {
        if (configObjectIds == null || configObjectIds.length < 1) {
            return this;
        }
        int configValue = -1;
        if (player != null) {
            if (varbitIndex != -1) {
                VarbitDefinition def = VarbitDefinition.forSceneryId(varbitIndex);
                if (def != null) {
                    configValue = def.getValue(player);
                }
            } else if (configId != -1) {
                configValue = getVarp(player, configId);
            }
        } else {
            configValue = 0;
        }
        SceneryDefinition childDef = getChildObjectAtIndex(configValue);
        if (childDef != null) childDef.varbitIndex = this.varbitIndex;
        return childDef;
    }

    public SceneryDefinition getChildObjectAtIndex(int index) {
        if (configObjectIds == null || configObjectIds.length < 1) {
            return this;
        }
        if (index < 0 || index >= configObjectIds.length - 1 || configObjectIds[index] == -1) {
            int objectId = configObjectIds[configObjectIds.length - 1];
            if (objectId != -1) {
                return forId(objectId);
            }
            return this;
        }
        return forId(configObjectIds[index]);
    }

    public VarbitDefinition getConfigFile() {
        if (varbitIndex != -1) {
            return VarbitDefinition.forSceneryId(varbitIndex);
        }
        return null;
    }

    public void setOption(int slot, String option) {
        if (slot < 0 || slot >= options.length) {
            throw new IllegalArgumentException(": " + slot);
        }
        options[slot] = option;
    }

    public void removeOption(int slot) {
        if (slot < 0 || slot >= options.length) {
            throw new IllegalArgumentException("Wrong index: " + slot);
        }
        options[slot] = null;
    }

    public void printOptions() {
        System.out.println("Options for object " + id + ":");
        for (int i = 0; i < options.length; i++) {
            System.out.println("Slot " + i + ": " + (options[i] != null ? options[i] : "No options"));
        }
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public void setMirrored(boolean mirrored) {
        this.mirrored = mirrored;
    }

    public short[] getOriginalColors() {
        return originalColors;
    }

    public int[] getConfigObjectIds() {
        return configObjectIds;
    }

    public static int getAnInt3832() {
        return anInt3832;
    }

    public int[] getAnIntArray3833() {
        return anIntArray3833;
    }

    public int getAnInt3834() {
        return anInt3834;
    }

    public int getAnInt3835() {
        return anInt3835;
    }

    public static int getAnInt3836() {
        return anInt3836;
    }

    public byte getaByte3837() {
        return aByte3837;
    }

    public int getAnInt3838() {
        return anInt3838;
    }

    public int getContrast() {
        return contrast;
    }

    public int getModelSizeZ() {
        return modelSizeZ;
    }

    public static int getAnInt3842() {
        return anInt3842;
    }

    public static int getAnInt3843() {
        return anInt3843;
    }

    public int getAnInt3844() {
        return anInt3844;
    }

    public boolean isaBoolean3845() {
        return aBoolean3845;
    }

    public static int getAnInt3846() {
        return anInt3846;
    }

    public byte getaByte3847() {
        return aByte3847;
    }

    public byte getaByte3849() {
        return aByte3849;
    }

    public int getAnInt3850() {
        return anInt3850;
    }

    public int getAnInt3851() {
        return anInt3851;
    }

    public boolean isBlocksLand() {
        return blocksLand;
    }

    public boolean isaBoolean3853() {
        return aBoolean3853;
    }

    public int getAnInt3855() {
        return anInt3855;
    }

    public boolean isFirstBool() {
        return ignoreOnRoute;
    }

    public int getAnInt3857() {
        return anInt3857;
    }

    public byte[] getRecolourPalette() {
        return recolourPalette;
    }

    public int[] getAnIntArray3859() {
        return anIntArray3859;
    }

    public int getAnInt3860() {
        return anInt3860;
    }

    @Override
    public String[] getOptions() {
        return options;
    }

    public int getVarbitID() {
        return varbitIndex;
    }

    public short[] getModifiedColors() {
        return modifiedColors;
    }

    public int getAnInt3865() {
        return anInt3865;
    }

    public boolean isaBoolean3866() {
        return aBoolean3866;
    }

    public boolean isaBoolean3867() {
        return aBoolean3867;
    }

    public boolean isProjectileClipped() {
        return projectileClipped;
    }

    public int[] getAnIntArray3869() {
        return anIntArray3869;
    }

    public boolean isaBoolean3870() {
        return aBoolean3870;
    }

    public int getSizeY() {
        return sizeY;
    }

    public boolean isCastsShadow() {
        return castsShadow;
    }

    public boolean isaBoolean3873() {
        return membersOnly;
    }

    public boolean getThirdBoolean() {
        return boolean1;
    }

    public int getAnInt3875() {
        return anInt3875;
    }

    public int getAddObjectCheck() {
        return animationId;
    }

    public int getAnInt3877() {
        return anInt3877;
    }

    public int getBrightness() {
        return brightness;
    }

    public int getSolid() {
        return solid;
    }

    public int getAnInt3881() {
        return anInt3881;
    }

    public int getAnInt3882() {
        return anInt3882;
    }

    public int getOffsetX() {
        return offsetX;
    }

    public Object getLoader() {
        return loader;
    }

    public int getOffsetZ() {
        return offsetZ;
    }

    public int getSizeX() {
        return sizeX;
    }

    public boolean isaBoolean3891() {
        return aBoolean3891;
    }

    public int getOffsetMultiplier() {
        return offsetMultiplier;
    }

    public int getInteractive() {
        return interactive;
    }

    public boolean isaBoolean3894() {
        return aBoolean3894;
    }

    public boolean isaBoolean3895() {
        return aBoolean3895;
    }

    public int getAnInt3896() {
        return anInt3896;
    }

    public int getConfigId() {
        return configId;
    }

    public byte[] getaByteArray3899() {
        return aByteArray3899;
    }

    public int getAnInt3900() {
        return anInt3900;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getModelSizeX() {
        return modelSizeX;
    }

    public int getAnInt3904() {
        return anInt3904;
    }

    public int getAnInt3905() {
        return anInt3905;
    }

    public boolean isaBoolean3906() {
        return aBoolean3906;
    }

    public int[] getAnIntArray3908() {
        return anIntArray3908;
    }

    public byte getContouredGround() {
        return contouredGround;
    }

    public int getAnInt3913() {
        return anInt3913;
    }

    public byte getaByte3914() {
        return aByte3914;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public int[][] getAnIntArrayArray3916() {
        return anIntArrayArray3916;
    }

    public int getModelSizeY() {
        return modelSizeY;
    }

    public short[] getModifiedTextureColours() {
        return modifiedTextureColours;
    }

    public short[] getOriginalTextureColours() {
        return originalTextureColours;
    }

    public int getAnInt3921() {
        return anInt3921;
    }

    public Object getaClass194_3922() {
        return aClass194_3922;
    }

    public boolean isaBoolean3923() {
        return aBoolean3923;
    }

    public boolean isaBoolean3924() {
        return aBoolean3924;
    }

    public int[] getModelIds() {
        return modelIds;
    }

    public boolean hasAction(String action) {
        if (options == null) {
            return false;
        }
        for (String option : options) {
            if (option == null) {
                continue;
            }
            if (option.equalsIgnoreCase(action)) {
                return true;
            }
        }
        return false;
    }

    public static Map<Integer, SceneryDefinition> getDefinitions() {
        return DEFINITIONS;
    }

    public static OptionHandler getOptionHandler(int nodeId, String name) {
        SceneryDefinition def = forId(nodeId);
        OptionHandler handler = def.getConfiguration("option:" + name);
        if (handler != null) {
            return handler;
        }
        return OPTION_HANDLERS.get(name);
    }

    public static boolean setOptionHandler(String name, OptionHandler handler) {
        return OPTION_HANDLERS.put(name, handler) != null;
    }

    public boolean isHasHiddenOptions() {
        return hasHiddenOptions;
    }

    public void setHasHiddenOptions(boolean hasHiddenOptions) {
        this.hasHiddenOptions = hasHiddenOptions;
    }

    public int getWalkingFlag() {
        return walkingFlag;
    }

    public int[] getModelConfiguration() {
        return modelConfiguration;
    }

    public void setModelConfiguration(int[] modelConfiguration) {
        this.modelConfiguration = modelConfiguration;
    }

    public short getMapIcon() {
        return mapIcon;
    }

    public static int getContainerId(int id) {
        return id >>> 8;
    }
}
