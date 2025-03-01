package core.cache.def.impl;

import core.cache.Cache;
import core.cache.def.Definition;
import core.cache.misc.buffer.ByteBufferUtils;
import core.game.interaction.OptionHandler;
import core.game.node.entity.npc.NPC;
import core.game.node.entity.npc.drop.NPCDropTables;
import core.game.node.entity.player.Player;
import core.game.system.config.NPCConfigParser;
import core.game.world.GameWorld;
import core.game.world.update.flag.context.Animation;
import core.game.world.update.flag.context.Graphics;
import core.tools.StringUtils;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.getVarp;

public final class NPCDefinition extends Definition<NPC> {
	private static final Map<Integer, NPCDefinition> DEFINITIONS = new HashMap<>();
	private static final Map<String, OptionHandler> OPTION_HANDLERS = new HashMap<>();
	public int size = 1;
	private int combatLevel;
	public int headIcons;
	public boolean isVisibleOnMap;
	private NPCDropTables dropTables = new NPCDropTables(this);
	public int anInt833;
	public int anInt836;
	public int anInt837;
	public boolean aBoolean841;
	public int anInt842;
	public int configFileId;
	public int[] childNPCIds;
	public int anInt846;
	public int anInt850;
	public byte aByte851;
	public boolean aBoolean852;
	public int anInt853;
	public byte aByte854;
	public boolean aBoolean856;
	public boolean aBoolean857;
	public short[] aShortArray859;
	public byte[] aByteArray861;
	public short aShort862;
	public boolean aBoolean863;
	public int anInt864;
	public short[] aShortArray866;
	public int[] anIntArray868;
	public int anInt869;
	public int anInt870;
	public int anInt871;
	public int anInt872;
	public int anInt874;
	public int anInt875;
	public int anInt876;
	public int anInt879;
	public short[] aShortArray880;
	public int anInt884;
	public int configId;
	public int anInt889;
	public int[] anIntArray892;
	public short aShort894;
	public short[] aShortArray896;
	public int anInt897;
	public int anInt899;
	public int anInt901;
	public int standAnimation;
	public int walkAnimation;
	public int renderAnimationId;
	private int combatDistance;

	private Graphics[] combatGraphics = new Graphics[3];

	private int turnAnimation;
	private int turn180Animation;
	private int turnCWAnimation;
	private int turnCCWAnimation;

	public NPCDefinition(int id) {
		this.id = id;
		anInt842 = -1;
		configFileId = -1;
		anInt837 = -1;
		anInt846 = -1;
		anInt853 = 32;
		standAnimation = -1;
		walkAnimation = -1;
		combatLevel = 0;
		anInt836 = -1;
		name = "null";
		anInt869 = 0;
		anInt850 = 255;
		anInt871 = -1;
		aBoolean852 = true;
		aShort862 = (short) 0;
		anInt876 = -1;
		aByte851 = (byte) -96;
		anInt875 = 0;
		anInt872 = -1;
		aBoolean857 = true;
		anInt870 = -1;
		anInt874 = -1;
		anInt833 = -1;
		anInt864 = 128;
		headIcons = -1;
		aBoolean856 = false;
		configId = -1;
		aByte854 = (byte) -16;
		aBoolean863 = false;
		isVisibleOnMap = true;
		anInt889 = -1;
		anInt884 = -1;
		aBoolean841 = true;
		anInt879 = -1;
		anInt899 = 128;
		aShort894 = (short) 0;
		options = new String[5];
		anInt897 = 0;
		anInt901 = -1;
		anIntArray868 = new int[0];
	}

	public static NPCDefinition forId(int id) {
		NPCDefinition def = DEFINITIONS.get(id);
		if (def == null) {
			def = new NPCDefinition(id);
			byte[] data = Cache.getIndexes()[18].getFileData(id >>> 7, id & 0x7f);
			if (data == null) {
				if (id != -1) {

				}
			} else {
				def.parse(ByteBuffer.wrap(data));
			}
			DEFINITIONS.put(id, def);
		}
		return def;
	}

	public static void main(String... args) throws Throwable {
		GameWorld.prompt(false);

		// for (int i = 0; i < 11000; i++) {
		// ItemDefinition def = ItemDefinition.forId(i);
		// if (def.getMaleWornModelId1() >= 1250 && def.getMaleWornModelId1() <=
		// 1550) {

		// def.getMaleWornModelId1());
		// }
		// }
	}

	public NPCDefinition getChildNPC(Player player) {
		if (childNPCIds == null || childNPCIds.length < 1) {
			return this;
		}
		int configValue = -1;
		if (player != null) {
			if (configFileId != -1) {
				configValue = VarbitDefinition.forNpcId(configFileId).getValue(player);
			} else if (configId != -1) {
				configValue = getVarp(player, configId);
			}
		} else {
			configValue = 0;
		}
		if (configValue < 0 || configValue >= childNPCIds.length - 1 || childNPCIds[configValue] == -1) {
			int objectId = childNPCIds[childNPCIds.length - 1];
			if (objectId != -1) {
				return forId(objectId);
			}
			return this;
		}
		return forId(childNPCIds[configValue]);
	}

	private void parse(ByteBuffer buffer) {
		while (true) {
			int opcode = buffer.get() & 0xFF;
			if (opcode == 0) {
				break;
			}
			parseOpcode(buffer, opcode);
		}
	}

	private void parseOpcode(ByteBuffer buffer, int opcode) {
		switch (opcode) {
		case 1:
			int length = buffer.get() & 0xFF;
			anIntArray868 = new int[length];
			for (int i_66_ = 0; i_66_ < length; i_66_++) {
				anIntArray868[i_66_] = buffer.getShort() & 0xFFFF;
				if ((anIntArray868[i_66_] ^ 0xffffffff) == -65536)
					anIntArray868[i_66_] = -1;
			}
			break;
		case 2:
			name = ByteBufferUtils.getString(buffer);
			break;
		case 12:
			size = buffer.get() & 0xFF;
			break;
		case 13:
			standAnimation = buffer.getShort();
			break;
		case 14:
			walkAnimation = buffer.getShort();
			break;
		case 15:
			turnAnimation = buffer.getShort();
			break;
		case 16:
			buffer.getShort(); // Another turn animation
			break;
		case 17:
			walkAnimation = buffer.getShort();
			turn180Animation = buffer.getShort();
			turnCWAnimation = buffer.getShort();
			turnCCWAnimation = buffer.getShort();
			break;
		case 30:
		case 31:
		case 32:
		case 33:
		case 34:
			options[opcode - 30] = ByteBufferUtils.getString(buffer);
			break;
		case 40:
			length = buffer.get() & 0xFF;
			aShortArray859 = new short[length];
			aShortArray896 = new short[length];
			for (int i_65_ = 0; (length ^ 0xffffffff) < (i_65_ ^ 0xffffffff); i_65_++) {
				aShortArray896[i_65_] = (short) (buffer.getShort() & 0xFFFF);
				aShortArray859[i_65_] = (short) (buffer.getShort() & 0xFFFF);
			}
			break;
		case 41:
			length = buffer.get() & 0xFF;
			aShortArray880 = new short[length];
			aShortArray866 = new short[length];
			for (int i_54_ = 0; (i_54_ ^ 0xffffffff) > (length ^ 0xffffffff); i_54_++) {
				aShortArray880[i_54_] = (short) (buffer.getShort() & 0xFFFF);
				aShortArray866[i_54_] = (short) (buffer.getShort() & 0xFFFF);
			}
			break;
		case 42:
			length = buffer.get() & 0xFF;
			aByteArray861 = new byte[length];
			for (int i_55_ = 0; length > i_55_; i_55_++) {
				aByteArray861[i_55_] = (byte) buffer.get();
			}
			break;
		case 60:
			length = buffer.get() & 0xFF;
			anIntArray892 = new int[length];
			for (int i_64_ = 0; (i_64_ ^ 0xffffffff) > (length ^ 0xffffffff); i_64_++) {
				anIntArray892[i_64_] = buffer.getShort() & 0xFFFF;
			}
			break;
		case 93:
			isVisibleOnMap = false;
			break;
		case 95:
			setCombatLevel(buffer.getShort() & 0xFFFF);
			break;
		case 97:
			anInt864 = buffer.getShort() & 0xFFFF;
			break;
		case 98:
			anInt899 = buffer.getShort() & 0xFFFF;
			break;
		case 99:
			aBoolean863 = true;
			break;
		case 100:
			anInt869 = buffer.get();
			break;
		case 101:
			anInt897 = buffer.get() * 5;
			break;
		case 102:
			headIcons = buffer.getShort() & 0xFFFF;
			break;
		case 103:
			anInt853 = buffer.getShort() & 0xFFFF;
			break;
		case 106:
		case 118:
			configFileId = buffer.getShort() & 0xFFFF;
			if (configFileId == 65535) {
				configFileId = -1;
			}
			configId = buffer.getShort() & 0xFFFF;
			if (configId == 65535) {
				configId = -1;
			}
			int defaultValue = -1;
			if ((opcode ^ 0xffffffff) == -119) {
				defaultValue = buffer.getShort() & 0xFFFF;
				if (defaultValue == 65535) {
					defaultValue = -1;
				}
			}
			length = buffer.get() & 0xFF;
			childNPCIds = new int[2 + length];
			for (int i = 0; length >= i; i++) {
				childNPCIds[i] = buffer.getShort() & 0xFFFF;
				if (childNPCIds[i] == 65535) {
					childNPCIds[i] = -1;
				}
			}
			childNPCIds[length + 1] = defaultValue;
			break;
		case 107:
			aBoolean841 = false;
			break;
		case 109:
			aBoolean852 = false;
			break;
		case 111:
			aBoolean857 = false;
			break;
		case 113:
			aShort862 = (short) (buffer.getShort() & 0xFFFF);
			aShort894 = (short) (buffer.getShort() & 0xFFFF);
			break;
		case 114:
			aByte851 = (byte) (buffer.get());
			aByte854 = (byte) (buffer.get());
			break;
		case 115:
			buffer.get();// & 0xFF;
			buffer.get();// & 0xFF;
			break;
		case 119:
			buffer.get();
			break;
		case 121:
			length = buffer.get() & 0xFF;
			for (int i = 0; i < length; i++) {
				buffer.get();
				buffer.get();
				buffer.get();
				buffer.get();
			}
			break;
		case 122:
			buffer.getShort();
			break;
		case 123:
			buffer.getShort();
			break;
		case 125:
			buffer.get();
			break;
		case 126:
			buffer.getShort();
		case 127:
			renderAnimationId = buffer.getShort();
			break;
		case 128:
			buffer.get();
			break;
		case 134:
			buffer.getShort();
			buffer.getShort();
			buffer.getShort();
			buffer.getShort();
			buffer.get();
			break;
		case 135:
			buffer.get();
			buffer.getShort();
			break;
		case 136:
			buffer.get();
			buffer.getShort();
			break;
		case 137:
			buffer.getShort();
			break;
		case 249:
			length = buffer.get() & 0xFF;
			for (int i = 0; i < length; i++) {
				boolean string = buffer.get() == 1;
				ByteBufferUtils.getMedium(buffer); // script id
				if (!string) {
					buffer.getInt(); // Value
				} else {
					ByteBufferUtils.getString(buffer); // value
				}
			}
			break;
		default:
			//SystemLogger.logErr("Unhandled NPC definition opcode: " + opcode);
		}
	}

	public boolean hasAttackOption() {
		for (String option : options) {
			if (option != null && option.equalsIgnoreCase("attack")) {
				return true;
			}
		}
		return false;
	}

	public static OptionHandler getOptionHandler(int nodeId, String name) {
		NPCDefinition def = forId(nodeId);
		OptionHandler handler = def.getConfiguration("option:" + name);
		if (handler != null) {
			return handler;
		}
		return OPTION_HANDLERS.get(name);
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

	public static boolean setOptionHandler(String name, OptionHandler handler) {
		return OPTION_HANDLERS.put(name, handler) != null;
	}

	public static Map<String, OptionHandler> getOptionHandlers() {
		return OPTION_HANDLERS;
	}

	public static final Map<Integer, NPCDefinition> getDefinitions() {
		return DEFINITIONS;
	}

	public final String getExamine() {
		String string = getConfiguration(NPCConfigParser.EXAMINE, examine);
		if (string != null) {
			return string;
		}
		if (name.length() <= 1) {
			return string;
		}
		return "It's a" + (StringUtils.isPlusN(name) ? "n " : " ") + name + ".";
	}

	public final void setExamine(String examine) {
		this.examine = examine;
	}

	public void initCombatGraphics(Map<String, Object> config) {
		if (config.containsKey(NPCConfigParser.START_GRAPHIC)) {
			combatGraphics[0] = new Graphics((Integer) config.get(NPCConfigParser.START_GRAPHIC), getConfiguration(NPCConfigParser.START_HEIGHT, 0));
		}
		if (config.containsKey(NPCConfigParser.PROJECTILE)) {
			combatGraphics[1] = new Graphics((Integer) config.get(NPCConfigParser.PROJECTILE), getConfiguration(NPCConfigParser.PROJECTILE_HEIGHT, 42));
		}
		if (config.containsKey(NPCConfigParser.END_GRAPHIC)) {
			combatGraphics[2] = new Graphics((Integer) config.get(NPCConfigParser.END_GRAPHIC), getConfiguration(NPCConfigParser.END_HEIGHT, 96));
		}
	}

	public Animation getCombatAnimation(int index) {
		String name = "";
		switch (index) {
		case 0:
			name = NPCConfigParser.MELEE_ANIMATION;
			break;
		case 1:
			name = NPCConfigParser.MAGIC_ANIMATION;
			break;
		case 2:
			name = NPCConfigParser.RANGE_ANIMATION;
			break;
		case 3:
			name = NPCConfigParser.DEFENCE_ANIMATION;
			break;
		case 4:
			name = NPCConfigParser.DEATH_ANIMATION;
			break;
			default:
				break;
		}
		return getConfiguration(name, null);
	}

	public int getSize() {
		return size;
	}

	public int getHeadIcons() {
		return headIcons;
	}

	public boolean isVisibleOnMap() {
		return isVisibleOnMap;
	}

	public int getAnInt833() {
		return anInt833;
	}

	public int getAnInt836() {
		return anInt836;
	}

	public int getAnInt837() {
		return anInt837;
	}

	public boolean isaBoolean841() {
		return aBoolean841;
	}

	public int getAnInt842() {
		return anInt842;
	}

	public int getConfigFileId() {
		return configFileId;
	}

	public int[] getChildNPCIds() {
		return childNPCIds;
	}

	public int getAnInt846() {
		return anInt846;
	}

	public int getAnInt850() {
		return anInt850;
	}

	public byte getaByte851() {
		return aByte851;
	}

	public boolean isaBoolean852() {
		return aBoolean852;
	}

	public int getAnInt853() {
		return anInt853;
	}

	public byte getaByte854() {
		return aByte854;
	}

	public boolean isaBoolean856() {
		return aBoolean856;
	}

	public boolean isaBoolean857() {
		return aBoolean857;
	}

	public short[] getaShortArray859() {
		return aShortArray859;
	}

	public byte[] getaByteArray861() {
		return aByteArray861;
	}

	public short getaShort862() {
		return aShort862;
	}

	public boolean isaBoolean863() {
		return aBoolean863;
	}

	public int getAnInt864() {
		return anInt864;
	}

	public short[] getaShortArray866() {
		return aShortArray866;
	}

	public int[] getAnIntArray868() {
		return anIntArray868;
	}

	public int getAnInt869() {
		return anInt869;
	}

	public int getAnInt870() {
		return anInt870;
	}

	public int getAnInt871() {
		return anInt871;
	}

	public int getAnInt872() {
		return anInt872;
	}

	public int getAnInt874() {
		return anInt874;
	}

	public int getAnInt875() {
		return anInt875;
	}

	public int getAnInt876() {
		return anInt876;
	}

	public int getAnInt879() {
		return anInt879;
	}

	public short[] getaShortArray880() {
		return aShortArray880;
	}

	public int getAnInt884() {
		return anInt884;
	}

	public int getConfigId() {
		if(configFileId != -1) {
			return VarbitDefinition.forNpcId(configFileId).getVarpId();
		} else return configFileId;
	}

	public int getVarbitOffset() {
		if(configFileId != -1){
			return VarbitDefinition.forNpcId(configFileId).getStartBit();
		}
		return -1;
	}

	public int getVarbitSize() {
		if(configFileId != -1){
			return VarbitDefinition.forNpcId(configFileId).getEndBit() - VarbitDefinition.forNpcId(configFileId).getStartBit();
		}
		return -1;
	}

	public int getAnInt889() {
		return anInt889;
	}

	public int[] getAnIntArray892() {
		return anIntArray892;
	}

	public short getaShort894() {
		return aShort894;
	}

	public short[] getaShortArray896() {
		return aShortArray896;
	}

	public int getAnInt897() {
		return anInt897;
	}

	public int getAnInt899() {
		return anInt899;
	}

	public int getAnInt901() {
		return anInt901;
	}

	public int getStandAnimation() {
		return standAnimation;
	}

	public int getWalkAnimation() {
		return walkAnimation;
	}

	public int getTurnAnimation() {
		return turnAnimation;
	}

	public int getTurn180Animation() {
		return turn180Animation;
	}

	public int getTurnCWAnimation() {
		return turnCWAnimation;
	}

	public int getTurnCCWAnimation() {
		return turnCCWAnimation;
	}

	public NPCDropTables getDropTables() {
		return dropTables;
	}

	public void setDropTables(NPCDropTables dropTables) {
		this.dropTables = dropTables;
	}

	public int getCombatLevel() {
		return combatLevel;
	}

	public void setCombatLevel(int combatLevel) {
		this.combatLevel = combatLevel;
	}

	public int getCombatDistance() {
		return combatDistance;
	}

	public void setCombatDistance(int combatDistance) {
		this.combatDistance = combatDistance;
	}

	public Graphics[] getCombatGraphics() {
		return combatGraphics;
	}

	public void setCombatGraphics(Graphics[] combatGraphics) {
		this.combatGraphics = combatGraphics;
	}

	public int getRenderAnimationId() {
		return renderAnimationId;
	}

	public void setRenderAnimationId(int renderAnimationId) {
		this.renderAnimationId = renderAnimationId;
	}
}
