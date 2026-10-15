package com.example.bibliasacra.data.model

data class BibleBook(
    val name: String,
    val testament: String, // "Old Testament" or "New Testament"
    val chaptersCount: Int,
    val isDeuterocanonical: Boolean = false
)

object BibleCanon {
    val OLD_TESTAMENT_BOOKS = listOf(
        BibleBook("Genesis", "Old Testament", 50),
        BibleBook("Exodus", "Old Testament", 40),
        BibleBook("Leviticus", "Old Testament", 27),
        BibleBook("Numbers", "Old Testament", 36),
        BibleBook("Deuteronomy", "Old Testament", 34),
        BibleBook("Joshua", "Old Testament", 24),
        BibleBook("Judges", "Old Testament", 21),
        BibleBook("Ruth", "Old Testament", 4),
        BibleBook("1 Samuel", "Old Testament", 31),
        BibleBook("2 Samuel", "Old Testament", 24),
        BibleBook("1 Kings", "Old Testament", 22),
        BibleBook("2 Kings", "Old Testament", 25),
        BibleBook("1 Chronicles", "Old Testament", 29),
        BibleBook("2 Chronicles", "Old Testament", 36),
        BibleBook("Ezra", "Old Testament", 10),
        BibleBook("Nehemiah", "Old Testament", 13),
        BibleBook("Tobit", "Old Testament", 14, isDeuterocanonical = true),
        BibleBook("Judith", "Old Testament", 16, isDeuterocanonical = true),
        BibleBook("Esther", "Old Testament", 16),
        BibleBook("1 Maccabees", "Old Testament", 16, isDeuterocanonical = true),
        BibleBook("2 Maccabees", "Old Testament", 15, isDeuterocanonical = true),
        BibleBook("Job", "Old Testament", 42),
        BibleBook("Psalms", "Old Testament", 150),
        BibleBook("Proverbs", "Old Testament", 31),
        BibleBook("Ecclesiastes", "Old Testament", 12),
        BibleBook("Song of Solomon", "Old Testament", 8),
        BibleBook("Wisdom", "Old Testament", 19, isDeuterocanonical = true),
        BibleBook("Sirach", "Old Testament", 51, isDeuterocanonical = true),
        BibleBook("Isaiah", "Old Testament", 66),
        BibleBook("Jeremiah", "Old Testament", 52),
        BibleBook("Lamentations", "Old Testament", 5),
        BibleBook("Baruch", "Old Testament", 6, isDeuterocanonical = true),
        BibleBook("Ezekiel", "Old Testament", 48),
        BibleBook("Daniel", "Old Testament", 14),
        BibleBook("Hosea", "Old Testament", 14),
        BibleBook("Joel", "Old Testament", 3),
        BibleBook("Amos", "Old Testament", 9),
        BibleBook("Obadiah", "Old Testament", 1),
        BibleBook("Jonah", "Old Testament", 4),
        BibleBook("Micah", "Old Testament", 7),
        BibleBook("Nahum", "Old Testament", 3),
        BibleBook("Habakkuk", "Old Testament", 3),
        BibleBook("Zephaniah", "Old Testament", 3),
        BibleBook("Haggai", "Old Testament", 2),
        BibleBook("Zechariah", "Old Testament", 14),
        BibleBook("Malachi", "Old Testament", 4)
    )

    val NEW_TESTAMENT_BOOKS = listOf(
        BibleBook("Matthew", "New Testament", 28),
        BibleBook("Mark", "New Testament", 16),
        BibleBook("Luke", "New Testament", 24),
        BibleBook("John", "New Testament", 21),
        BibleBook("Acts", "New Testament", 28),
        BibleBook("Romans", "New Testament", 16),
        BibleBook("1 Corinthians", "New Testament", 16),
        BibleBook("2 Corinthians", "New Testament", 13),
        BibleBook("Galatians", "New Testament", 6),
        BibleBook("Ephesians", "New Testament", 6),
        BibleBook("Philippians", "New Testament", 4),
        BibleBook("Colossians", "New Testament", 4),
        BibleBook("1 Thessalonians", "New Testament", 5),
        BibleBook("2 Thessalonians", "New Testament", 3),
        BibleBook("1 Timothy", "New Testament", 6),
        BibleBook("2 Timothy", "New Testament", 4),
        BibleBook("Titus", "New Testament", 3),
        BibleBook("Philemon", "New Testament", 1),
        BibleBook("Hebrews", "New Testament", 13),
        BibleBook("James", "New Testament", 5),
        BibleBook("1 Peter", "New Testament", 5),
        BibleBook("2 Peter", "New Testament", 3),
        BibleBook("1 John", "New Testament", 5),
        BibleBook("2 John", "New Testament", 1),
        BibleBook("3 John", "New Testament", 1),
        BibleBook("Jude", "New Testament", 1),
        BibleBook("Revelation", "New Testament", 22)
    )

    val ALL_BOOKS = OLD_TESTAMENT_BOOKS + NEW_TESTAMENT_BOOKS

    fun findBook(name: String): BibleBook? {
        val trimmed = name.trim().lowercase()
        return ALL_BOOKS.find { it.name.lowercase() == trimmed }
    }
}
