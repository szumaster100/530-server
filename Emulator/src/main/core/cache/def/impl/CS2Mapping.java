package core.cache.def.impl;

import core.cache.Cache;
import core.cache.misc.buffer.ByteBufferUtils;
import core.game.world.GameWorld;

import java.io.BufferedWriter;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class CS2Mapping {
	private static final Map<Integer, CS2Mapping> maps = new HashMap<>();
	private final int scriptId;
	private int unknown;
	private int unknown1;
	private String defaultString;
	private int defaultInt;
	private HashMap<Integer, Object> map;
	private Object[] array;

	public CS2Mapping(int scriptId) {
		this.scriptId = scriptId;
	}

	public static void main(String... args) throws Throwable {
		GameWorld.prompt(false);
		BufferedWriter bw = Files.newBufferedWriter(Paths.get("./cs2.txt"));
		for (int i = 0; i < 10000; i++) {
			CS2Mapping mapping = forId(i);
			if (mapping == null) {
				continue;
			}
			if (mapping.map == null) {
				continue;
			}
			bw.append("ScriptAPI - " + i + " [");
			for (int index : mapping.map.keySet()) {
				bw.append(mapping.map.get(index) + ": " + index + " ");
			}
			bw.append("]");
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}

	public static CS2Mapping forId(int scriptId) {
		CS2Mapping mapping = maps.get(scriptId);
		if (mapping != null) {
			return mapping;
		}
		mapping = new CS2Mapping(scriptId);
		byte[] bs = Cache.getIndexes()[17].getFileData(scriptId >>> 8, scriptId & 0xFF);
		if (bs != null) {
			mapping.load(ByteBuffer.wrap(bs));
		} else {
			return null;
		}
		maps.put(scriptId, mapping);
		return mapping;
	}

	private void load(ByteBuffer buffer) {
		int opcode;
		while ((opcode = buffer.get() & 0xFF) != 0) {
			switch (opcode) {
			case 1:
				unknown = buffer.get() & 0xFF;
				break;
			case 2:
				unknown1 = buffer.get() & 0xFF;
				break;
			case 3:
				defaultString = ByteBufferUtils.getString(buffer);
				break;
			case 4:
				defaultInt = buffer.getInt();
				break;
			case 5:
			case 6:
				int size = buffer.getShort() & 0xFFFF;
				String string = null;
				int val = 0;
				map = new HashMap<>(size);
				array = new Object[size];

				for (int i = 0; i < size; i++) {
					int key = buffer.getInt();
					if (opcode == 5) {
						string = ByteBufferUtils.getString(buffer);
						array[i] = string;
						map.put(key, string);
					} else {
						val = buffer.getInt();
						array[i] = val;
						map.put(key, val);
					}
				}
				break;
			}
		}
	}

	public Object[] getArray() {
		return array;
	}

	public int getScriptId() {
		return scriptId;
	}

	public int getUnknown() {
		return unknown;
	}

	public void setUnknown(int unknown) {
		this.unknown = unknown;
	}

	public int getUnknown1() {
		return unknown1;
	}

	public void setUnknown1(int unknown1) {
		this.unknown1 = unknown1;
	}

	public String getDefaultString() {
		return defaultString;
	}

	public void setDefaultString(String defaultString) {
		this.defaultString = defaultString;
	}

	public int getDefaultInt() {
		return defaultInt;
	}

	public void setDefaultInt(int defaultInt) {
		this.defaultInt = defaultInt;
	}

	public HashMap<Integer, Object> getMap() {
		return map;
	}

	public void setMap(HashMap<Integer, Object> map) {
		this.map = map;
	}
}