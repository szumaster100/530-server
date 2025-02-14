package core.cache.misc;

import core.cache.bzip2.BZip2Decompressor;
import core.cache.gzip.GZipDecompressor;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

public final class ContainersInformation {

	private Container informationContainer;

	private int protocol;

	private int revision;

	private int[] containersIndexes;

	private FilesContainer[] containers;

	private boolean filesNamed;

	private boolean whirpool;

	private final byte[] data;

	public ContainersInformation(byte[] informationContainerPackedData) {
		this.data = Arrays.copyOf(informationContainerPackedData, informationContainerPackedData.length);
		informationContainer = new Container();
		informationContainer.setVersion((informationContainerPackedData[informationContainerPackedData.length - 2] << 8 & 0xff00) + (informationContainerPackedData[-1 + informationContainerPackedData.length] & 0xff));
		CRC32 crc32 = new CRC32();
		crc32.update(informationContainerPackedData);
		informationContainer.setCrc((int) crc32.getValue());
		decodeContainersInformation(unpackCacheContainer(informationContainerPackedData));
	}

	public static final byte[] unpackCacheContainer(byte[] packedData) {
		ByteBuffer buffer = ByteBuffer.wrap(packedData);
		int compression = buffer.get() & 0xFF;
		int containerSize = buffer.getInt();
		if (containerSize < 0 || containerSize > 5000000) {
			return null;
			// throw new RuntimeException();
		}
		if (compression == 0) {
			byte unpacked[] = new byte[containerSize];
			buffer.get(unpacked, 0, containerSize);
			return unpacked;
		}
		int decompressedSize = buffer.getInt();
		if (decompressedSize < 0 || decompressedSize > 20000000) {
			return null;
			// throw new RuntimeException();
		}
		byte decompressedData[] = new byte[decompressedSize];
		if (compression == 1) {
			BZip2Decompressor.decompress(decompressedData, packedData, containerSize, 9);
		} else {
			GZipDecompressor.decompress(buffer, decompressedData);
		}
		return decompressedData;
	}

	public int[] getContainersIndexes() {
		return containersIndexes;
	}

	public FilesContainer[] getContainers() {
		return containers;
	}

	public Container getInformationContainer() {
		return informationContainer;
	}

	public int getRevision() {
		return revision;
	}

	public void decodeContainersInformation(byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		protocol = buffer.get() & 0xFF;
		if (protocol != 5 && protocol != 6) {
			throw new RuntimeException();
		}
		revision = protocol < 6 ? 0 : buffer.getInt();
		int nameHash = buffer.get() & 0xFF;
		filesNamed = (0x1 & nameHash) != 0;
		whirpool = (0x2 & nameHash) != 0;
		containersIndexes = new int[buffer.getShort() & 0xFFFF];
		int lastIndex = -1;
		for (int index = 0; index < containersIndexes.length; index++) {
			containersIndexes[index] = (buffer.getShort() & 0xFFFF) + (index == 0 ? 0 : containersIndexes[index - 1]);
			if (containersIndexes[index] > lastIndex) {
				lastIndex = containersIndexes[index];
			}
		}
		containers = new FilesContainer[lastIndex + 1];
		for (int index = 0; index < containersIndexes.length; index++) {
			containers[containersIndexes[index]] = new FilesContainer();
		}
		if (filesNamed) {
			for (int index = 0; index < containersIndexes.length; index++) {
				containers[containersIndexes[index]].setNameHash(buffer.getInt());
			}
		}
		byte[][] filesHashes = null;
		if (whirpool) {
			filesHashes = new byte[containers.length][];
			for (int index = 0; index < containersIndexes.length; index++) {
				filesHashes[containersIndexes[index]] = new byte[64];
				buffer.get(filesHashes[containersIndexes[index]], 0, 64);
			}
		}
		for (int index = 0; index < containersIndexes.length; index++) {
			containers[containersIndexes[index]].setCrc(buffer.getInt());
		}
		for (int index = 0; index < containersIndexes.length; index++) {
			containers[containersIndexes[index]].setVersion(buffer.getInt());
		}
		for (int index = 0; index < containersIndexes.length; index++) {
			containers[containersIndexes[index]].setFilesIndexes(new int[buffer.getShort() & 0xFFFF]);
		}
		for (int index = 0; index < containersIndexes.length; index++) {
			int lastFileIndex = -1;
			for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]].getFilesIndexes().length; fileIndex++) {
				containers[containersIndexes[index]].getFilesIndexes()[fileIndex] = (buffer.getShort() & 0xFFFF) + (fileIndex == 0 ? 0 : containers[containersIndexes[index]].getFilesIndexes()[fileIndex - 1]);
				if (containers[containersIndexes[index]].getFilesIndexes()[fileIndex] > lastFileIndex) {
					lastFileIndex = containers[containersIndexes[index]].getFilesIndexes()[fileIndex];
				}
			}
			containers[containersIndexes[index]].setFiles(new Container[lastFileIndex + 1]);
			for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]].getFilesIndexes().length; fileIndex++) {
				containers[containersIndexes[index]].getFiles()[containers[containersIndexes[index]].getFilesIndexes()[fileIndex]] = new Container();
			}
		}
		if (whirpool) {
			for (int index = 0; index < containersIndexes.length; index++) {
				for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]].getFilesIndexes().length; fileIndex++) {
					containers[containersIndexes[index]].getFiles()[containers[containersIndexes[index]].getFilesIndexes()[fileIndex]].setVersion(filesHashes[containersIndexes[index]][containers[containersIndexes[index]].getFilesIndexes()[fileIndex]]);
				}
			}
		}
		if (filesNamed) {
			for (int index = 0; index < containersIndexes.length; index++) {
				for (int fileIndex = 0; fileIndex < containers[containersIndexes[index]].getFilesIndexes().length; fileIndex++) {
					containers[containersIndexes[index]].getFiles()[containers[containersIndexes[index]].getFilesIndexes()[fileIndex]].setNameHash(buffer.getInt());
				}
			}
		}
	}

	public boolean isWhirpool() {
		return whirpool;
	}

	public byte[] getData() {
		return data;
	}

}
