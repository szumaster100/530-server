package core.cache;

import core.ServerConstants;
import core.cache.def.impl.ItemDefinition;
import core.cache.def.impl.SceneryDefinition;
import core.tools.Log;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import static core.api.ContentAPIKt.log;

public final class Cache {

	private static CacheFileManager[] cacheFileManagers;

	private static CacheFile referenceFile;

	private Cache(String location) {
		try {
			init(location);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static void init(String path) throws Throwable {
		log(Cache.class, Log.FINE, "Initializing cache...");
		byte[] cacheFileBuffer = new byte[520];
		RandomAccessFile containersInformFile = new RandomAccessFile(path + File.separator + "main_file_cache.idx255", "r");
		RandomAccessFile dataFile = new RandomAccessFile(path + File.separator + "main_file_cache.dat2", "r");
		referenceFile = new CacheFile(255, containersInformFile, dataFile, 500000, cacheFileBuffer);
		int length = (int) (containersInformFile.length() / 6);
		cacheFileManagers = new CacheFileManager[length];
		for (int i = 0; i < length; i++) {
			File f = new File(path + File.separator + "main_file_cache.idx" + i);
			if (f.exists() && f.length() > 0) {
				CacheFile cacheFile = new CacheFile(i, new RandomAccessFile(f, "r"), dataFile, 1000000, cacheFileBuffer);
				cacheFileManagers[i] = new CacheFileManager(cacheFile, true);
				if (cacheFileManagers[i].getInformation() == null) {
					log(Cache.class, Log.ERR, "Error loading cache index " + i + ": no information.");
					cacheFileManagers[i] = null;
				}
			}
		}
		ItemDefinition.parse();
		SceneryDefinition.parse();
	}

	public static void init() {
		try {
			init(ServerConstants.CACHE_PATH);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static ByteBuffer getArchiveData(int index, int archive, boolean priority, int encryptionValue) {
		byte[] data = index == 255 ? referenceFile.getContainerData(archive) : cacheFileManagers[index].getCacheFile().getContainerData(archive);
		if (data == null || data.length < 1) {
			log(Cache.class, Log.ERR, "Invalid JS-5 request - " + index + ", " + archive + ", " + priority + ", " + encryptionValue + "!");
			return null;
		}
		int compression = data[0] & 0xff;
		int length = ((data[1] & 0xff) << 24) + ((data[2] & 0xff) << 16) + ((data[3] & 0xff) << 8) + (data[4] & 0xff);
		int settings = compression;
		if (!priority) {
			settings |= 0x80;
		}
		int realLength = compression != 0 ? length + 4 : length;

		realLength += (index != 255 && compression != 0 && data.length - length == 9) ? 2 : 0;
		ByteBuffer buffer = ByteBuffer.allocate((realLength + 5) + (realLength / 512) + 10);
		buffer.put((byte) index);
		buffer.putShort((short) archive);
		buffer.put((byte) settings);
		buffer.putInt(length);
		for (int i = 5; i < realLength + 5; i++) {
			if (buffer.position() % 512 == 0) {
				buffer.put((byte) 255);
			}
			if (data.length > i)
				buffer.put(data[i]);
			else
				buffer.put((byte) 0);
		}
		if (encryptionValue != 0) {
			for (int i = 0; i < buffer.position(); i++) {
				buffer.put(i, (byte) (buffer.get(i) ^ encryptionValue));
			}
		}
		buffer.flip();
		return buffer;
	}

	public static final byte[] generateReferenceData() {
		ByteBuffer buffer = ByteBuffer.allocate(cacheFileManagers.length * 8);
		for (int index = 0; index < cacheFileManagers.length; index++) {
			if (cacheFileManagers[index] == null) {
				buffer.putInt(index == 24 ? 609698396 : 0);
				buffer.putInt(0);
				continue;
			}
			buffer.putInt(cacheFileManagers[index].getInformation().getInformationContainer().getCrc());
			buffer.putInt(cacheFileManagers[index].getInformation().getRevision());
		}
		return buffer.array();
	}

	public static final CacheFileManager[] getIndexes() {
		return cacheFileManagers;
	}

	public static final CacheFile getReferenceFile() {
		return referenceFile;
	}

	public static final int getInterfaceDefinitionsComponentsSize(int interfaceId) {
		return getIndexes()[3].getFilesSize(interfaceId);
	}

	public static final int getInterfaceDefinitionsSize() {
		return getIndexes()[3].getContainersSize();
	}

	public static final int getNPCDefinitionsSize() {
		int lastContainerId = getIndexes()[18].getContainersSize() - 1;
		return lastContainerId * 128 + getIndexes()[18].getFilesSize(lastContainerId);
	}

	public static final int getGraphicDefinitionsSize() {
		int lastContainerId = getIndexes()[21].getContainersSize() - 1;
		return lastContainerId * 256 + getIndexes()[21].getFilesSize(lastContainerId);
	}

	public static final int getAnimationDefinitionsSize() {
		int lastContainerId = getIndexes()[20].getContainersSize() - 1;
		return lastContainerId * 128 + getIndexes()[20].getFilesSize(lastContainerId);
	}

	public static final int getObjectDefinitionsSize() {
		int lastContainerId = getIndexes()[16].getContainersSize() - 1;
		return lastContainerId * 256 + getIndexes()[16].getFilesSize(lastContainerId);
	}

	public static final int getItemDefinitionsSize() {
		int lastContainerId = getIndexes()[19].getContainersSize() - 1;
		return lastContainerId * 256 + getIndexes()[19].getFilesSize(lastContainerId);
	}

}