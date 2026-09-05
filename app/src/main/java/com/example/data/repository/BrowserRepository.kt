package com.example.data.repository

import com.example.data.local.BookmarkEntity
import com.example.data.local.BrowserDao
import com.example.data.local.DownloadEntity
import com.example.data.local.DownloadStatus
import com.example.data.local.HistoryEntity
import kotlinx.coroutines.flow.Flow

class BrowserRepository(private val dao: BrowserDao) {

    val bookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()
    val history: Flow<List<HistoryEntity>> = dao.getAllHistory()
    val downloads: Flow<List<DownloadEntity>> = dao.getAllDownloads()

    fun isBookmarked(url: String): Flow<Boolean> = dao.isBookmarked(url)

    fun searchHistory(query: String): Flow<List<HistoryEntity>> = dao.searchHistory(query)

    suspend fun addBookmark(title: String, url: String, domain: String) {
        dao.insertBookmark(BookmarkEntity(title = title, url = url, domain = domain))
    }

    suspend fun removeBookmark(id: Long) {
        dao.deleteBookmark(id)
    }

    suspend fun removeBookmarkByUrl(url: String) {
        dao.deleteBookmarkByUrl(url)
    }

    suspend fun addHistory(title: String, url: String, domain: String) {
        if (url.startsWith("http://") || url.startsWith("https://")) {
            dao.insertHistory(HistoryEntity(title = title.ifEmpty { domain }, url = url, domain = domain))
        }
    }

    suspend fun removeHistory(id: Long) {
        dao.deleteHistory(id)
    }

    suspend fun clearHistory() {
        dao.clearHistory()
    }

    suspend fun addDownload(fileName: String, url: String, filePath: String, mimeType: String, sizeBytes: Long) {
        dao.insertDownload(
            DownloadEntity(
                fileName = fileName,
                url = url,
                filePath = filePath,
                mimeType = mimeType,
                sizeBytes = sizeBytes,
                status = DownloadStatus.COMPLETED
            )
        )
    }

    suspend fun removeDownload(id: Long) {
        dao.deleteDownload(id)
    }
}
