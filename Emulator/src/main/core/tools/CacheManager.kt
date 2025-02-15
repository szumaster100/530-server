package core.tools

import com.displee.cache.CacheLibrary
import com.displee.cache.index.Index
import com.displee.cache.index.archive.Archive

object CacheManager {
    lateinit var cacheLibrary: CacheLibrary

    fun getIndex(index: Int): Index {
        return cacheLibrary.index(index)
    }

    @JvmStatic
    fun getData(
        index: Int,
        archive: Int,
        file: Int,
    ): ByteArray? {
        return cacheLibrary.data(index, archive, file, null)
    }

    @JvmStatic
    fun getData(
        index: Index,
        archive: Archive,
        file: Int,
    ): ByteArray? {
        return cacheLibrary.data(index.id, archive.id, file, null)
    }

    @JvmStatic
    fun getData(
        index: Index,
        archive: String,
    ): ByteArray? {
        return cacheLibrary.data(index.id, archive, 0)
    }

    @JvmStatic
    fun getData(
        index: Index,
        archive: String,
        xtea: IntArray,
    ): ByteArray? {
        return cacheLibrary.data(index.id, archive, 0, xtea)
    }

    @JvmStatic
    fun getArchiveId(
        index: Index,
        archive: String,
    ): Int {
        return cacheLibrary.index(index.id).archiveId(archive)
    }

    @JvmStatic
    fun getArchiveCapacity(
        index: Index,
        archive: Index,
    ): Int {
        return cacheLibrary
            .index(index.id)
            .archive(archive.id)
            ?.files()
            ?.size ?: -1
    }

    @JvmStatic
    fun getIndexCapacity(index: Index): Int {
        val lastArchive = (cacheLibrary.index(index.id).archives().last())
        return (lastArchive.files().size) + (lastArchive.id * 256)
    }
}
