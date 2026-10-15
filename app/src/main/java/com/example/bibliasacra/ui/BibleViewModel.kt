package com.example.bibliasacra.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliasacra.audio.BibleTtsManager
import com.example.bibliasacra.data.local.BibleDatabase
import com.example.bibliasacra.data.local.CachedVerseEntity
import com.example.bibliasacra.data.model.BibleBook
import com.example.bibliasacra.data.model.BibleCanon
import com.example.bibliasacra.data.model.Bookmark
import com.example.bibliasacra.data.model.Chapter
import com.example.bibliasacra.data.model.Highlight
import com.example.bibliasacra.data.model.HighlightColor
import com.example.bibliasacra.data.model.Translation
import com.example.bibliasacra.data.repository.BibleRepository
import com.example.bibliasacra.ui.theme.ThemePreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface ChapterUiState {
    data object Loading : ChapterUiState
    data class Success(val chapter: Chapter) : ChapterUiState
    data class Error(val message: String) : ChapterUiState
}

class BibleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BibleRepository(BibleDatabase.getDatabase(application))
    val ttsManager = BibleTtsManager(application)

    // Current Reading Location
    private val _currentBook = MutableStateFlow("Genesis")
    val currentBook: StateFlow<String> = _currentBook.asStateFlow()

    private val _currentChapter = MutableStateFlow(1)
    val currentChapter: StateFlow<Int> = _currentChapter.asStateFlow()

    private val _currentTranslation = MutableStateFlow(Translation.CPDV)
    val currentTranslation: StateFlow<Translation> = _currentTranslation.asStateFlow()

    // Chapter Scripture State
    private val _chapterState = MutableStateFlow<ChapterUiState>(ChapterUiState.Loading)
    val chapterState: StateFlow<ChapterUiState> = _chapterState.asStateFlow()

    // Highlights & Bookmarks for current chapter
    private val _chapterHighlights = MutableStateFlow<Map<Int, HighlightColor>>(emptyMap())
    val chapterHighlights: StateFlow<Map<Int, HighlightColor>> = _chapterHighlights.asStateFlow()

    private val _chapterBookmarks = MutableStateFlow<Set<Int>>(emptySet())
    val chapterBookmarks: StateFlow<Set<Int>> = _chapterBookmarks.asStateFlow()

    // Global Collections
    private val _allHighlights = MutableStateFlow<List<Highlight>>(emptyList())
    val allHighlights: StateFlow<List<Highlight>> = _allHighlights.asStateFlow()

    private val _allBookmarks = MutableStateFlow<List<Bookmark>>(emptyList())
    val allBookmarks: StateFlow<List<Bookmark>> = _allBookmarks.asStateFlow()

    // UI Customization
    private val _activeTheme = MutableStateFlow(ThemePreset.AMOLED_BLACK)
    val activeTheme: StateFlow<ThemePreset> = _activeTheme.asStateFlow()

    private val _fontSizeSp = MutableStateFlow(18)
    val fontSizeSp: StateFlow<Int> = _fontSizeSp.asStateFlow()

    // Selected verse for action bar
    private val _selectedVerse = MutableStateFlow<Int?>(null)
    val selectedVerse: StateFlow<Int?> = _selectedVerse.asStateFlow()

    // Dialog sheets
    val isBookPickerOpen = MutableStateFlow(false)
    val isHighlightsSheetOpen = MutableStateFlow(false)
    val isBookmarksSheetOpen = MutableStateFlow(false)
    val isQuoteGeneratorOpen = MutableStateFlow(false)
    val isSearchSheetOpen = MutableStateFlow(false)
    val isSettingsSheetOpen = MutableStateFlow(false)
    val isNoteDialogOpen = MutableStateFlow(false)

    // Search Results
    private val _searchResults = MutableStateFlow<List<CachedVerseEntity>>(emptyList())
    val searchResults: StateFlow<List<CachedVerseEntity>> = _searchResults.asStateFlow()

    init {
        loadChapter()
        observeHighlightsAndBookmarks()
    }

    private fun observeHighlightsAndBookmarks() {
        viewModelScope.launch {
            repository.getAllHighlights().collectLatest { list ->
                _allHighlights.value = list
                updateChapterHighlightsMap()
            }
        }
        viewModelScope.launch {
            repository.getAllBookmarks().collectLatest { list ->
                _allBookmarks.value = list
                updateChapterBookmarksSet()
            }
        }
    }

    private fun updateChapterHighlightsMap() {
        val b = _currentBook.value
        val c = _currentChapter.value
        val map = _allHighlights.value
            .filter { it.book == b && it.chapter == c }
            .associate { it.verseNumber to it.color }
        _chapterHighlights.value = map
    }

    private fun updateChapterBookmarksSet() {
        val b = _currentBook.value
        val c = _currentChapter.value
        val set = _allBookmarks.value
            .filter { it.book == b && it.chapter == c }
            .map { it.verseNumber }
            .toSet()
        _chapterBookmarks.value = set
    }

    fun loadChapter(
        book: String = _currentBook.value,
        chapter: Int = _currentChapter.value,
        translation: Translation = _currentTranslation.value
    ) {
        _currentBook.value = book
        _currentChapter.value = chapter
        _currentTranslation.value = translation
        _selectedVerse.value = null
        ttsManager.pauseOrStop()

        updateChapterHighlightsMap()
        updateChapterBookmarksSet()

        viewModelScope.launch {
            _chapterState.value = ChapterUiState.Loading
            try {
                val chap = repository.getChapter(translation, book, chapter)
                _chapterState.value = ChapterUiState.Success(chap)
            } catch (e: Exception) {
                _chapterState.value = ChapterUiState.Error(e.message ?: "Failed to load scripture")
            }
        }
    }

    fun selectVerse(verseNumber: Int) {
        _selectedVerse.value = if (_selectedVerse.value == verseNumber) null else verseNumber
    }

    fun clearSelection() {
        _selectedVerse.value = null
    }

    fun toggleHighlightForSelected(color: HighlightColor) {
        val v = _selectedVerse.value ?: return
        viewModelScope.launch {
            repository.toggleHighlight(_currentBook.value, _currentChapter.value, v, color)
            updateChapterHighlightsMap()
        }
    }

    fun toggleBookmarkForSelected(previewText: String) {
        val v = _selectedVerse.value ?: return
        viewModelScope.launch {
            repository.toggleBookmark(_currentBook.value, _currentChapter.value, v, previewText)
            updateChapterBookmarksSet()
        }
    }

    fun saveNoteForSelected(note: String) {
        val v = _selectedVerse.value ?: return
        viewModelScope.launch {
            repository.updateHighlightNote(_currentBook.value, _currentChapter.value, v, note)
            updateChapterHighlightsMap()
        }
    }

    fun deleteHighlight(highlight: Highlight) {
        viewModelScope.launch {
            repository.deleteHighlight(highlight)
            updateChapterHighlightsMap()
        }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        viewModelScope.launch {
            repository.deleteBookmark(bookmark)
            updateChapterBookmarksSet()
        }
    }

    fun navigateTo(book: String, chapter: Int) {
        loadChapter(book, chapter)
        isBookPickerOpen.value = false
        isHighlightsSheetOpen.value = false
        isBookmarksSheetOpen.value = false
        isSearchSheetOpen.value = false
        isQuoteGeneratorOpen.value = false
    }

    fun nextChapter() {
        val currentBookObj = BibleCanon.findBook(_currentBook.value) ?: return
        if (_currentChapter.value < currentBookObj.chaptersCount) {
            loadChapter(_currentBook.value, _currentChapter.value + 1)
        } else {
            // Next book
            val all = BibleCanon.ALL_BOOKS
            val idx = all.indexOfFirst { it.name == currentBookObj.name }
            if (idx in 0 until all.size - 1) {
                val nextBook = all[idx + 1]
                loadChapter(nextBook.name, 1)
            }
        }
    }

    fun previousChapter() {
        if (_currentChapter.value > 1) {
            loadChapter(_currentBook.value, _currentChapter.value - 1)
        } else {
            // Previous book
            val all = BibleCanon.ALL_BOOKS
            val idx = all.indexOfFirst { it.name == _currentBook.value }
            if (idx > 0) {
                val prevBook = all[idx - 1]
                loadChapter(prevBook.name, prevBook.chaptersCount)
            }
        }
    }

    fun setTranslation(translation: Translation) {
        loadChapter(translation = translation)
        isSettingsSheetOpen.value = false
    }

    fun setTheme(preset: ThemePreset) {
        _activeTheme.value = preset
    }

    fun setFontSize(size: Int) {
        _fontSizeSp.value = size.coerceIn(12, 32)
    }

    fun toggleAudio() {
        if (ttsManager.isPlaying.value) {
            ttsManager.pauseOrStop()
        } else {
            val state = _chapterState.value
            if (state is ChapterUiState.Success) {
                val startIndex = if (_selectedVerse.value != null) {
                    state.chapter.verses.indexOfFirst { it.verseNumber == _selectedVerse.value }.coerceAtLeast(0)
                } else 0
                ttsManager.startReading(state.chapter.verses, startIndex)
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            _searchResults.value = repository.searchVerses(query)
        }
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
    }
}
