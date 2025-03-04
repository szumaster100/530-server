package core.cache.def.impl;

import core.ServerConstants;
import core.cache.Cache;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class GraphicDefinition {
	private static final Map<Integer, GraphicDefinition> graphicDefinitions = new HashMap<>();
	public short[] aShortArray1435;
	public short[] aShortArray1438;
	public int anInt1440;
	public boolean aBoolean1442;
	public int defaultModel;
	public int anInt1446;
	public boolean aBoolean1448 = false;
	public int anInt1449;
	public int animationId;
	public int anInt1451;
	public int graphicsId;
	public int anInt1454;
	public short[] aShortArray1455;
	public short[] aShortArray1456;
	public byte byteValue;
	public int intValue;

	public static final GraphicDefinition forId(int gfxId) {
		GraphicDefinition def = graphicDefinitions.get(gfxId);
		if (def != null) {
			return def;
		}
		byte[] data = Cache.getIndexes()[21].getFileData(gfxId >>> 8, gfxId & 0xff);
		def = new GraphicDefinition();
		def.graphicsId = gfxId;
		if (data != null) {
			def.readValueLoop(ByteBuffer.wrap(data));
		}
		graphicDefinitions.put(gfxId, def);
		return def;
	}

	public static final void main(String... s) {
		try {
			Cache.init(ServerConstants.CACHE_PATH);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		// 5046 - 5050 are related anims & 2148
		GraphicDefinition d = GraphicDefinition.forId(803);

		for (int i = 0; i < 5000; i++) {
			GraphicDefinition def = GraphicDefinition.forId(i);
			if (def == null) {
				continue;
			}
			if ((def.animationId > 2000 && def.animationId < 2200) || (def.defaultModel >= 1300 && def.defaultModel < 1500)) {

			}
		}
	}

	private void readValueLoop(ByteBuffer buffer) {
		for (;;) {
			int opcode = buffer.get() & 0xFF;
			if (opcode == 0) {
				break;
			}
			readValues(buffer, opcode);
		}
	}

	public void readValues(ByteBuffer buffer, int opcode) {
		if (opcode != 1) {
			if (opcode == 2)
				animationId = buffer.getShort();
			else if (opcode == 4)
				anInt1446 = buffer.getShort() & 0xFFFF;
			else if (opcode != 5) {
				if ((opcode ^ 0xffffffff) != -7) {
					if (opcode == 7)
						anInt1440 = buffer.get() & 0xFF;
					else if ((opcode ^ 0xffffffff) == -9)
						anInt1451 = buffer.get() & 0xFF;
					else if (opcode != 9) {
						if (opcode != 10) {
							if (opcode == 11) { // added opcode
								// aBoolean1442 = true;
								byteValue = (byte) 1;
							} else if (opcode == 12) { // added opcode
								// aBoolean1442 = true;
								byteValue = (byte) 4;
							} else if (opcode == 13) { // added opcode
								// aBoolean1442 = true;
								byteValue = (byte) 5;
							} else if (opcode == 14) { // added opcode
								// aBoolean1442 = true;
								// aByte2856 = 2;
								byteValue = (byte) 2;
								intValue = (buffer.get() & 0xFF) * 256;
							} else if (opcode == 15) {
								// aByte2856 = 3;
								byteValue = (byte) 3;
								intValue = buffer.getShort() & 0xFFFF;
							} else if (opcode == 16) {
								// aByte2856 = 3;
								byteValue = (byte) 3;
								intValue = buffer.getInt();
							} else if (opcode != 40) {
								if ((opcode ^ 0xffffffff) == -42) {
									int i = buffer.get() & 0xFF;
									aShortArray1455 = new short[i];
									aShortArray1435 = new short[i];
									for (int i_0_ = 0; i > i_0_; i_0_++) {
										aShortArray1455[i_0_] = (short) (buffer.getShort() & 0xFFFF);
										aShortArray1435[i_0_] = (short) (buffer.getShort() & 0xFFFF);
									}
								}
							} else {
								int i = buffer.get() & 0xFF;
								aShortArray1438 = new short[i];
								aShortArray1456 = new short[i];
								for (int i_1_ = 0; ((i ^ 0xffffffff) < (i_1_ ^ 0xffffffff)); i_1_++) {
									aShortArray1438[i_1_] = (short) (buffer.getShort() & 0xFFFF);
									aShortArray1456[i_1_] = (short) (buffer.getShort() & 0xFFFF);
								}
							}
						} else
							aBoolean1448 = true;
					} else {
						// aBoolean1442 = true;
						byteValue = (byte) 3;
						intValue = 8224;
					}
				} else
					anInt1454 = buffer.getShort() & 0xFFFF;
			} else
				anInt1449 = buffer.getShort() & 0xFFFF;
		} else
			defaultModel = buffer.getShort();
	}

	public GraphicDefinition() {
		byteValue = 0;
		intValue = -1;
		anInt1446 = 128;
		aBoolean1442 = false;
		anInt1449 = 128;
		anInt1451 = 0;
		animationId = -1;
		anInt1454 = 0;
		anInt1440 = 0;
	}

}