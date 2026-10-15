package com.example.bibliasacra.data.repository

import com.example.bibliasacra.data.local.BibleDatabase
import com.example.bibliasacra.data.local.BookmarkEntity
import com.example.bibliasacra.data.local.CachedVerseEntity
import com.example.bibliasacra.data.local.HighlightEntity
import com.example.bibliasacra.data.model.Bookmark
import com.example.bibliasacra.data.model.Chapter
import com.example.bibliasacra.data.model.Highlight
import com.example.bibliasacra.data.model.HighlightColor
import com.example.bibliasacra.data.model.Translation
import com.example.bibliasacra.data.model.Verse
import com.example.bibliasacra.data.remote.BibleApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BibleRepository(
    private val database: BibleDatabase,
    private val apiClient: BibleApiClient = BibleApiClient()
) {
    private val highlightDao = database.highlightDao()
    private val bookmarkDao = database.bookmarkDao()
    private val cachedVerseDao = database.cachedVerseDao()

    suspend fun getChapter(translation: Translation, book: String, chapter: Int): Chapter {
        // 1. Check local Room cache
        val localVerses = cachedVerseDao.getCachedVerses(translation.id, book, chapter)
        if (localVerses.isNotEmpty()) {
            return Chapter(
                book = book,
                chapter = chapter,
                translationId = translation.id,
                translationName = translation.name,
                verses = localVerses.map { Verse(it.verseNumber, it.text) }
            )
        }

        // 2. Fetch from network
        val remoteChapter = apiClient.fetchChapter(translation, book, chapter)

        // 3. Save to local cache for offline reading
        val entities = remoteChapter.verses.map {
            CachedVerseEntity(
                translationId = translation.id,
                book = book,
                chapter = chapter,
                verseNumber = it.verseNumber,
                text = it.text
            )
        }
        cachedVerseDao.insertAll(entities)

        return remoteChapter
    }

    // Highlights
    fun getAllHighlights(): Flow<List<Highlight>> {
        return highlightDao.getAllHighlights().map { list -> list.map { it.toModel() } }
    }

    fun getHighlightsForChapter(book: String, chapter: Int): Flow<List<Highlight>> {
        return highlightDao.getHighlightsForChapter(book, chapter).map { list -> list.map { it.toModel() } }
    }

    suspend fun toggleHighlight(book: String, chapter: Int, verseNumber: Int, color: HighlightColor, note: String = "") {
        val existing = highlightDao.getHighlight(book, chapter, verseNumber)
        if (existing != null && existing.colorName == color.name) {
            highlightDao.deleteHighlight(book, chapter, verseNumber)
        } else {
            highlightDao.insertHighlight(
                HighlightEntity(
                    id = existing?.id ?: 0,
                    book = book,
                    chapter = chapter,
                    verseNumber = verseNumber,
                    colorName = color.name,
                    note = note.ifBlank { existing?.note ?: "" },
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun updateHighlightNote(book: String, chapter: Int, verseNumber: Int, note: String) {
        val existing = highlightDao.getHighlight(book, chapter, verseNumber)
        if (existing != null) {
            highlightDao.insertHighlight(existing.copy(note = note))
        } else {
            highlightDao.insertHighlight(
                HighlightEntity(
                    book = book,
                    chapter = chapter,
                    verseNumber = verseNumber,
                    colorName = HighlightColor.YELLOW.name,
                    note = note,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteHighlight(highlight: Highlight) {
        highlightDao.delete(HighlightEntity.fromModel(highlight))
    }

    // Bookmarks
    fun getAllBookmarks(): Flow<List<Bookmark>> {
        return bookmarkDao.getAllBookmarks().map { list -> list.map { it.toModel() } }
    }

    fun getBookmarksForChapter(book: String, chapter: Int): Flow<List<Bookmark>> {
        return bookmarkDao.getBookmarksForChapter(book, chapter).map { list -> list.map { it.toModel() } }
    }

    suspend fun toggleBookmark(book: String, chapter: Int, verseNumber: Int, previewText: String) {
        val existing = bookmarkDao.getBookmark(book, chapter, verseNumber)
        if (existing != null) {
            bookmarkDao.deleteBookmark(book, chapter, verseNumber)
        } else {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    book = book,
                    chapter = chapter,
                    verseNumber = verseNumber,
                    previewText = previewText,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    suspend fun deleteBookmark(bookmark: Bookmark) {
        bookmarkDao.delete(BookmarkEntity.fromModel(bookmark))
    }

    suspend fun searchVerses(query: String): List<CachedVerseEntity> {
        if (query.isBlank()) return emptyList()
        return cachedVerseDao.searchVerses(query.trim())
    }
}
