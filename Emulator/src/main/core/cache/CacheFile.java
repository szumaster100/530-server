package core.cache;

import core.cache.crypto.XTEACryption;
import core.cache.misc.ContainersInformation;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Represents a file within the cache system, responsible for handling
 * container data, decryption, and unpacking.
 */
public final class CacheFile {

	private int indexFileId;
	private byte[] cacheFileBuffer;
	private int maxContainerSize;
	private RandomAccessFile indexFile;
	private RandomAccessFile dataFile;

	/**
	 * Constructs a CacheFile instance.
	 *
	 * @param indexFileId      The ID of the index file.
	 * @param indexFile        The RandomAccessFile for the index file.
	 * @param dataFile         The RandomAccessFile for the data file.
	 * @param maxContainerSize The maximum allowed container size.
	 * @param cacheFileBuffer  A buffer used for reading cache data.
	 */
	public CacheFile(int indexFileId, RandomAccessFile indexFile, RandomAccessFile dataFile, int maxContainerSize, byte[] cacheFileBuffer) {
		this.cacheFileBuffer = cacheFileBuffer;
		this.indexFileId = indexFileId;
		this.maxContainerSize = maxContainerSize;
		this.indexFile = indexFile;
		this.dataFile = dataFile;
	}

	/**
	 * Retrieves and unpacks the data of a given container.
	 *
	 * @param containerId The ID of the container.
	 * @param xteaKeys    The XTEA decryption keys, or null if not needed.
	 * @return The unpacked container data, or null if not found.
	 */
	public final byte[] getContainerUnpackedData(int containerId, int[] xteaKeys) {
		byte[] packedData = getContainerData(containerId);
		if (packedData == null) {
			return null;
		}
		if (xteaKeys != null && (xteaKeys[0] != 0 || xteaKeys[1] != 0 || xteaKeys[2] != 0 || xteaKeys[3] != 0)) {
			packedData = XTEACryption.decrypt(xteaKeys, ByteBuffer.wrap(packedData), 5, packedData.length).array();
		}
		return ContainersInformation.unpackCacheContainer(packedData);
	}

	/**
	 * Retrieves the raw container data.
	 *
	 * @param containerId The ID of the container.
	 * @return The raw container data, or null if retrieval fails.
	 */
	public final byte[] getContainerData(int containerId) {
		synchronized (dataFile) {
			try {
				if (indexFile.length() < (6 * containerId + 6)) {
					return null;
				}
				indexFile.seek(6 * containerId);
				indexFile.read(cacheFileBuffer, 0, 6);
				int containerSize = (cacheFileBuffer[2] & 0xff) + (((0xff & cacheFileBuffer[0]) << 16) + (cacheFileBuffer[1] << 8 & 0xff00));
				int sector = ((cacheFileBuffer[3] & 0xff) << 16) - (-(0xff00 & cacheFileBuffer[4] << 8) - (cacheFileBuffer[5] & 0xff));
				if (containerSize < 0 || containerSize > maxContainerSize) {
					return null;
				}
				if (sector <= 0 || dataFile.length() / 520L < sector) {
					return null;
				}
				byte data[] = new byte[containerSize];
				int dataReadCount = 0;
				int part = 0;
				while (containerSize > dataReadCount) {
					if (sector == 0) {
						return null;
					}
					dataFile.seek(520 * sector);
					int dataToReadCount = containerSize - dataReadCount;
					if (dataToReadCount > 512) {
						dataToReadCount = 512;
					}
					dataFile.read(cacheFileBuffer, 0, 8 + dataToReadCount);
					int currentContainerId = (0xff & cacheFileBuffer[1]) + (0xff00 & cacheFileBuffer[0] << 8);
					int currentPart = ((cacheFileBuffer[2] & 0xff) << 8) + (0xff & cacheFileBuffer[3]);
					int nextSector = (cacheFileBuffer[6] & 0xff) + (0xff00 & cacheFileBuffer[5] << 8) + ((0xff & cacheFileBuffer[4]) << 16);
					int currentIndexFileId = cacheFileBuffer[7] & 0xff;
					if (containerId != currentContainerId || currentPart != part || indexFileId != currentIndexFileId) {
						return null;
					}
					if (nextSector < 0 || (dataFile.length() / 520L) < nextSector) {
						return null;
					}
					for (int index = 0; dataToReadCount > index; index++) {
						data[dataReadCount++] = cacheFileBuffer[8 + index];
					}
					part++;
					sector = nextSector;
				}
				return data;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Gets the index file ID.
	 *
	 * @return The index file ID.
	 */
	public int getIndexFileId() {
		return indexFileId;
	}

	/**
	 * Retrieves and unpacks the data of a given container without XTEA decryption.
	 *
	 * @param containerId The ID of the container.
	 * @return The unpacked container data, or null if not found.
	 */
	public final byte[] getContainerUnpackedData(int containerId) {
		return getContainerUnpackedData(containerId, null);
	}
}
