package com.example.bibliasacra.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HighlightDao {
    @Query("SELECT * FROM highlights ORDER BY timestamp DESC")
    fun getAllHighlights(): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights WHERE book = :book AND chapter = :chapter")
    fun getHighlightsForChapter(book: String, chapter: Int): Flow<List<HighlightEntity>>

    @Query("SELECT * FROM highlights WHERE book = :book AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getHighlight(book: String, chapter: Int, verseNumber: Int): HighlightEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHighlight(highlight: HighlightEntity): Long

    @Query("DELETE FROM highlights WHERE book = :book AND chapter = :chapter AND verseNumber = :verseNumber")
    suspend fun deleteHighlight(book: String, chapter: Int, verseNumber: Int)

    @Delete
    suspend fun delete(highlight: HighlightEntity)
}

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE book = :book AND chapter = :chapter")
    fun getBookmarksForChapter(book: String, chapter: Int): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE book = :book AND chapter = :chapter AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getBookmark(book: String, chapter: Int, verseNumber: Int): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE book = :book AND chapter = :chapter AND verseNumber = :verseNumber")
    suspend fun deleteBookmark(book: String, chapter: Int, verseNumber: Int)

    @Delete
    suspend fun delete(bookmark: BookmarkEntity)
}

@Dao
interface CachedVerseDao {
    @Query("SELECT * FROM cached_verses WHERE translationId = :translationId AND book = :book AND chapter = :chapter ORDER BY verseNumber ASC")
    suspend fun getCachedVerses(translationId: String, book: String, chapter: Int): List<CachedVerseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(verses: List<CachedVerseEntity>)

    @Query("SELECT * FROM cached_verses WHERE text LIKE '%' || :query || '%' LIMIT 100")
    suspend fun searchVerses(query: String): List<CachedVerseEntity>
}
