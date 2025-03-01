package core.cache;

import core.cache.misc.ContainersInformation;
import core.tools.StringUtils;

import java.nio.ByteBuffer;

/**
 * Manages cache files, allowing retrieval and processing of container and file data.
 */
public final class CacheFileManager {
	private CacheFile cacheFile;
	private ContainersInformation information;
	private boolean discardFilesData;
	private byte[][][] filesData;

	/**
	 * Constructs a CacheFileManager.
	 *
	 * @param cacheFile         The cache file to manage.
	 * @param discardFilesData  Whether to discard file data after retrieval.
	 */
	public CacheFileManager(CacheFile cacheFile, boolean discardFilesData) {
		this.cacheFile = cacheFile;
		this.discardFilesData = discardFilesData;
		byte[] informContainerPackedData = Cache.getReferenceFile().getContainerData(cacheFile.getIndexFileId());
		if (informContainerPackedData == null) {
			return;
		}
		information = new ContainersInformation(informContainerPackedData);
		resetFilesData();
	}

	/**
	 * Gets the associated cache file.
	 *
	 * @return The cache file.
	 */
	public CacheFile getCacheFile() {
		return cacheFile;
	}

	/**
	 * Gets the number of containers.
	 *
	 * @return The number of containers.
	 */
	public int getContainersSize() {
		return information.getContainers().length;
	}

	/**
	 * Gets the number of files in a container.
	 *
	 * @param containerId The container ID.
	 * @return The number of files, or -1 if invalid.
	 */
	public int getFilesSize(int containerId) {
		if (!validContainer(containerId)) {
			return -1;
		}
		return information.getContainers()[containerId].getFiles().length;
	}

	/**
	 * Resets stored file data.
	 */
	public void resetFilesData() {
		filesData = new byte[information.getContainers().length][][];
	}

	/**
	 * Checks if a file is valid.
	 *
	 * @param containerId The container ID.
	 * @param fileId      The file ID.
	 * @return True if valid, otherwise false.
	 */
	public boolean validFile(int containerId, int fileId) {
		if (!validContainer(containerId)) {
			return false;
		}
		if (fileId < 0 || information.getContainers()[containerId] == null || information.getContainers()[containerId].getFiles().length <= fileId) {
			return false;
		}
		return true;

	}

	/**
	 * Checks if a container is valid.
	 *
	 * @param containerId The container ID.
	 * @return True if valid, otherwise false.
	 */
	public boolean validContainer(int containerId) {
		if (containerId < 0 || information.getContainers().length <= containerId) {
			return false;
		}
		return true;
	}

	/**
	 * Retrieves file IDs in a container.
	 *
	 * @param containerId The container ID.
	 * @return Array of file IDs, or null if invalid.
	 */
	public int[] getFileIds(int containerId) {
		if (!validContainer(containerId)) {
			return null;
		}
		return information.getContainers()[containerId].getFilesIndexes();
	}

	/**
	 * Retrieves an archive ID by name.
	 *
	 * @param name The archive name.
	 * @return The archive ID, or -1 if not found.
	 */
	public int getArchiveId(String name) {
		if (name == null) {
			return -1;
		}
		int hash = StringUtils.getNameHash(name);
		for (int containerIndex = 0; containerIndex < information.getContainersIndexes().length; containerIndex++) {
			if (information.getContainers()[information.getContainersIndexes()[containerIndex]].getNameHash() == hash) {
				return information.getContainersIndexes()[containerIndex];
			}
		}
		return -1;
	}

	/**
	 * Retrieves file data.
	 *
	 * @param containerId The container ID.
	 * @param fileId      The file ID.
	 * @return The file data.
	 */
	public byte[] getFileData(int containerId, int fileId) {
		return getFileData(containerId, fileId, null);
	}

	public boolean loadFilesData(int archiveId, int[] keys) {
		byte[] data = cacheFile.getContainerUnpackedData(archiveId, keys);
		if (data == null) {
			return false;
		}
		if (filesData[archiveId] == null) {
			if (information.getContainers()[archiveId] == null) {
				return false;
			}
			filesData[archiveId] = new byte[information.getContainers()[archiveId].getFiles().length][];
		}
		if (information.getContainers()[archiveId].getFilesIndexes().length == 1) {
			int fileId = information.getContainers()[archiveId].getFilesIndexes()[0];
			filesData[archiveId][fileId] = data;
		} else {
			int readPosition = data.length;
			int amtOfLoops = data[--readPosition] & 0xff;
			readPosition -= amtOfLoops * (information.getContainers()[archiveId].getFilesIndexes().length * 4);
			ByteBuffer buffer = ByteBuffer.wrap(data);
			int filesSize[] = new int[information.getContainers()[archiveId].getFilesIndexes().length];
			buffer.position(readPosition);
			for (int loop = 0; loop < amtOfLoops; loop++) {
				int offset = 0;
				for (int fileIndex = 0; fileIndex < information.getContainers()[archiveId].getFilesIndexes().length; fileIndex++) {
					filesSize[fileIndex] += offset += buffer.getInt();
				}
			}
			byte[][] filesBufferData = new byte[information.getContainers()[archiveId].getFilesIndexes().length][];
			for (int fileIndex = 0; fileIndex < information.getContainers()[archiveId].getFilesIndexes().length; fileIndex++) {
				filesBufferData[fileIndex] = new byte[filesSize[fileIndex]];
				filesSize[fileIndex] = 0;
			}
			buffer.position(readPosition);
			int sourceOffset = 0;
			for (int loop = 0; loop < amtOfLoops; loop++) {
				int dataRead = 0;
				for (int fileIndex = 0; fileIndex < information.getContainers()[archiveId].getFilesIndexes().length; fileIndex++) {
					dataRead += buffer.getInt();
					System.arraycopy(data, sourceOffset, filesBufferData[fileIndex], filesSize[fileIndex], dataRead);
					sourceOffset += dataRead;
					filesSize[fileIndex] += dataRead;
				}
			}
			for (int fileIndex = 0; fileIndex < information.getContainers()[archiveId].getFilesIndexes().length; fileIndex++) {
				filesData[archiveId][information.getContainers()[archiveId].getFilesIndexes()[fileIndex]] = filesBufferData[fileIndex];
			}
		}
		return true;

	}

	public byte[] getFileData(int containerId, int fileId, int[] xteaKeys) {
		if (!validFile(containerId, fileId)) {
			return null;
		}
		if (filesData[containerId] == null || filesData[containerId][fileId] == null) {
			if (!loadFilesData(containerId, xteaKeys)) {
				return null;
			}
		}
		byte[] data = filesData[containerId][fileId];
		if (discardFilesData) {
			if (filesData[containerId].length == 1) {
				filesData[containerId] = null;
			} else {
				filesData[containerId][fileId] = null;
			}
		}
		return data;
	}

	public ContainersInformation getInformation() {
		return information;
	}

	/**
	 * Checks if file data should be discarded.
	 *
	 * @return True if file data is discarded after retrieval.
	 */
	public boolean isDiscardFilesData() {
		return discardFilesData;
	}

	/**
	 * Sets whether file data should be discarded.
	 *
	 * @param discardFilesData True to discard data after retrieval.
	 */
	public void setDiscardFilesData(boolean discardFilesData) {
		this.discardFilesData = discardFilesData;
	}

	/**
	 * Gets all file data.
	 *
	 * @return The file data array.
	 */
	public byte[][][] getFilesData() {
		return filesData;
	}

	/**
	 * Sets the file data.
	 *
	 * @param filesData The file data array.
	 */
	public void setFilesData(byte[][][] filesData) {
		this.filesData = filesData;
	}

	/**
	 * Sets the cache file.
	 *
	 * @param cacheFile The cache file.
	 */
	public void setCacheFile(CacheFile cacheFile) {
		this.cacheFile = cacheFile;
	}

	/**
	 * Sets the container information.
	 *
	 * @param information The container information.
	 */
	public void setInformation(ContainersInformation information) {
		this.information = information;
	}
}
