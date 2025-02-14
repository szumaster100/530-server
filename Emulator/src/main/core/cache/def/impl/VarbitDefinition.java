package core.cache.def.impl;

import core.cache.Cache;
import core.game.node.entity.player.Player;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static core.api.ContentAPIKt.getVarbit;

public final class VarbitDefinition {
	private static final Map<Integer, VarbitDefinition> MAPPING = new HashMap<>();
	private static final int[] BITS = new int[32];
    private final int id;
    private int varpId;
    private int startBit;
    private int endBit;

	public VarbitDefinition(int id) {
		this.id = id;
	}

	public VarbitDefinition(int varpId, int id, int startBit, int endBit)
	{
		this.varpId = varpId;
		this.id = id;
		this.startBit = startBit;
		this.endBit = endBit;
	}

	static {
		int flag = 2;
		for (int i = 0; i < 32; i++) {
			BITS[i] = flag - 1;
			flag += flag;
		}

	}

	public static void load() {
// Bank insert mode
					create(304, 7000, 0, 0);
// Bank note mode
					create(115, 7001, 0, 0);

	}

	public static VarbitDefinition forSceneryId(int id) {
		return forId(id);
	}

	public static VarbitDefinition forNpcId(int id){
		return forId(id);
	}

	public static VarbitDefinition forItemId(int id){
		return forId(id);
	}

	public static VarbitDefinition forId(int id){
		VarbitDefinition def = MAPPING.get(id);
		if (def != null) {
			return def;
		}

		def = new VarbitDefinition(id);
		byte[] bs = Cache.getIndexes()[22].getFileData(id >>> 10, id & 0x3ff);
		if (bs != null) {
			ByteBuffer buffer = ByteBuffer.wrap(bs);
			int opcode = 0;
			while ((opcode = buffer.get() & 0xFF) != 0) {
				if (opcode == 1) {
					def.varpId = buffer.getShort() & 0xFFFF;
					def.startBit = buffer.get() & 0xFF;
					def.endBit = buffer.get() & 0xFF;
				}
			}
		}
		MAPPING.put(id, def);
		return def;
	}

	public static void create(int varpId, int varbitId, int startBit, int endBit){
		VarbitDefinition def = new VarbitDefinition(
			varpId,
			varbitId,
			startBit,
			endBit
		);
		MAPPING.put(varbitId, def);
	}

	public int getValue(Player player) {
            return getVarbit(player, id);
	}

	public static Map<Integer, VarbitDefinition> getMapping() {
		return MAPPING;
	}

	public int getId() {
		return id;
	}

	public int getVarpId() {
		return varpId;
	}

	public int getStartBit() {
		return startBit;
	}

	public int getEndBit() {
		return endBit;
	}

        public int getMask() {
                int mask = 0;
                for (int i = startBit; i <= endBit; i++)
                    mask |= (1 << (i - startBit));
                return mask;
        }

	@Override
	public String toString() {
		return "ConfigFileDefinition [id=" + id + ", configId=" + varpId + ", bitShift=" + startBit + ", bitSize=" + endBit + "]";
	}
}
