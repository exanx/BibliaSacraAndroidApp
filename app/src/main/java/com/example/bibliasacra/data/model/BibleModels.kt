package com.example.bibliasacra.data.model

data class Verse(
    val verseNumber: Int,
    val text: String
)

data class Chapter(
    val book: String,
    val chapter: Int,
    val translationId: String,
    val translationName: String,
    val verses: List<Verse>
)

data class Translation(
    val id: String,
    val name: String,
    val shortName: String,
    val baseUrl: String? = null,
    val isDefault: Boolean = false
) {
    companion object {
        val CPDV = Translation(
            id = "cpdv",
            name = "Catholic Public Domain Version",
            shortName = "CPDV",
            baseUrl = "https://exanx.github.io/bible-json/CPDV-73-Books",
            isDefault = true
        )
        val DRB = Translation(
            id = "drb",
            name = "Douay-Rheims Bible",
            shortName = "DRV",
            baseUrl = "https://exanx.github.io/bible-json/DRB-73-Books"
        )
        val WEB = Translation(
            id = "web",
            name = "World English Bible (Catholic)",
            shortName = "WEBC",
            baseUrl = null
        )
        val KJV = Translation(
            id = "kjv",
            name = "King James Version (w/ Deuterocanon)",
            shortName = "KJV/D",
            baseUrl = null
        )

        val ALL = listOf(CPDV, DRB, WEB, KJV)
    }
}

enum class HighlightColor(val hex: Long, val displayName: String) {
    YELLOW(0xFFFBBF24, "Yellow"),
    GREEN(0xFF34D399, "Green"),
    BLUE(0xFF60A5FA, "Blue"),
    PINK(0xFFF472B6, "Pink"),
    PURPLE(0xFFA78BFA, "Purple")
}

data class Highlight(
    val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val color: HighlightColor,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Bookmark(
    val id: Long = 0,
    val book: String,
    val chapter: Int,
    val verseNumber: Int,
    val previewText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class QuoteInspiration(
    val book: String,
    val chapter: Int,
    val fromVerse: Int,
    val toVerse: Int,
    val themeTag: String
)

object QuoteInspirations {
    val FAMOUS_VERSES = listOf(
        QuoteInspiration("John", 3, 16, 16, "Love of God"),
        QuoteInspiration("Psalms", 23, 1, 6, "The Good Shepherd"),
        QuoteInspiration("Philippians", 4, 13, 13, "Strength"),
        QuoteInspiration("Romans", 8, 28, 28, "Providence"),
        QuoteInspiration("Proverbs", 3, 5, 6, "Trust in the Lord"),
        QuoteInspiration("Jeremiah", 29, 11, 11, "Hope & Future"),
        QuoteInspiration("Isaiah", 40, 31, 31, "Renewed Strength"),
        QuoteInspiration("Genesis", 1, 1, 3, "Creation"),
        QuoteInspiration("Matthew", 6, 33, 33, "Kingdom of God"),
        QuoteInspiration("Psalms", 46, 1, 2, "Refuge & Fortress"),
        QuoteInspiration("Joshua", 1, 9, 9, "Courage"),
        QuoteInspiration("1 Corinthians", 13, 4, 8, "Charity"),
        QuoteInspiration("Micah", 6, 8, 8, "Justice & Mercy"),
        QuoteInspiration("Wisdom", 3, 1, 3, "Souls of the Just"),
        QuoteInspiration("Sirach", 2, 1, 5, "Patience in Trial"),
        QuoteInspiration("Ephesians", 2, 8, 9, "Grace")
    )
}
