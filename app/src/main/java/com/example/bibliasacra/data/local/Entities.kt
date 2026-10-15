package com.example.bibliasacra.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bibliasacra.data.model.Highlight
import com.example.bibliasacra.data.model.HighlightColor
import com.example.bibliasacra.data.model.Bookmark

@Entity(tableName = "highlights")
data class HighlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val colorName: String,
    val note: String,
    val timestamp: Long
) {
    fun toModel(): Highlight = Highlight(
        id = id,
        book = book,
        chapter = chapter,
        verseNumber = verseNumber,
        color = try { HighlightColor.valueOf(colorName) } catch (e: Exception) { HighlightColor.YELLOW },
        note = note,
        timestamp = timestamp
    )

    companion object {
        fun fromModel(model: Highlight): HighlightEntity = HighlightEntity(
            id = model.id,
            book = model.book,
            chapter = model.chapter,
            verseNumber = model.verseNumber,
            colorName = model.color.name,
            note = model.note,
            timestamp = model.timestamp
        )
    }
}

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val previewText: String,
    val timestamp: Long
) {
    fun toModel(): Bookmark = Bookmark(
        id = id,
        book = book,
        chapter = chapter,
        verseNumber = verseNumber,
        previewText = previewText,
        timestamp = timestamp
    )

    companion object {
        fun fromModel(model: Bookmark): BookmarkEntity = BookmarkEntity(
            id = model.id,
            book = model.book,
            chapter = model.chapter,
            verseNumber = model.verseNumber,
            previewText = model.previewText,
            timestamp = model.timestamp
        )
    }
}

@Entity(tableName = "cached_verses", primaryKeys = ["translationId", "book", "chapter", "verseNumber"])
data class CachedVerseEntity(
    val translationId: String,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val text: String
)
