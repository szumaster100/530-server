package core.cache;

import core.cache.misc.ContainersInformation;
import core.tools.StringUtils;

import java.nio.ByteBuffer;

public final class CacheFileManager {
	private CacheFile cacheFile;
	private ContainersInformation information;
	private boolean discardFilesData;
	private byte[][][] filesData;

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

	public CacheFile getCacheFile() {
		return cacheFile;
	}

	public int getContainersSize() {
		return information.getContainers().length;
	}

	public int getFilesSize(int containerId) {
		if (!validContainer(containerId)) {
			return -1;
		}
		return information.getContainers()[containerId].getFiles().length;
	}

	public void resetFilesData() {
		filesData = new byte[information.getContainers().length][][];
	}

	public boolean validFile(int containerId, int fileId) {
		if (!validContainer(containerId)) {
			return false;
		}
		if (fileId < 0 || information.getContainers()[containerId] == null || information.getContainers()[containerId].getFiles().length <= fileId) {
			return false;
		}
		return true;

	}

	public boolean validContainer(int containerId) {
		if (containerId < 0 || information.getContainers().length <= containerId) {
			return false;
		}
		return true;
	}

	public int[] getFileIds(int containerId) {
		if (!validContainer(containerId)) {
			return null;
		}
		return information.getContainers()[containerId].getFilesIndexes();
	}

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

	public boolean isDiscardFilesData() {
		return discardFilesData;
	}

	public void setDiscardFilesData(boolean discardFilesData) {
		this.discardFilesData = discardFilesData;
	}

	public byte[][][] getFilesData() {
		return filesData;
	}

	public void setFilesData(byte[][][] filesData) {
		this.filesData = filesData;
	}

	public void setCacheFile(CacheFile cacheFile) {
		this.cacheFile = cacheFile;
	}

	public void setInformation(ContainersInformation information) {
		this.information = information;
	}
}
