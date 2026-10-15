package com.example.bibliasacra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bibliasacra.ui.BibleViewModel
import com.example.bibliasacra.ui.bookmarks.BookmarksSheet
import com.example.bibliasacra.ui.components.BibleTopBar
import com.example.bibliasacra.ui.components.NoteDialog
import com.example.bibliasacra.ui.highlights.HighlightsSheet
import com.example.bibliasacra.ui.quotes.QuoteCardSheet
import com.example.bibliasacra.ui.reader.ReaderScreen
import com.example.bibliasacra.ui.search.SearchSheet
import com.example.bibliasacra.ui.selector.BookChapterPickerSheet
import com.example.bibliasacra.ui.settings.SettingsSheet
import com.example.bibliasacra.ui.theme.BibliaSacraTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BibleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val activeTheme by viewModel.activeTheme.collectAsStateWithLifecycle()
            val currentBook by viewModel.currentBook.collectAsStateWithLifecycle()
            val currentChapter by viewModel.currentChapter.collectAsStateWithLifecycle()
            val currentTranslation by viewModel.currentTranslation.collectAsStateWithLifecycle()
            val chapterState by viewModel.chapterState.collectAsStateWithLifecycle()
            val chapterHighlights by viewModel.chapterHighlights.collectAsStateWithLifecycle()
            val chapterBookmarks by viewModel.chapterBookmarks.collectAsStateWithLifecycle()
            val selectedVerseNumber by viewModel.selectedVerse.collectAsStateWithLifecycle()
            val fontSizeSp by viewModel.fontSizeSp.collectAsStateWithLifecycle()

            val isPlayingAudio by viewModel.ttsManager.isPlaying.collectAsStateWithLifecycle()
            val spokenVerseNumber by viewModel.ttsManager.currentSpokenVerse.collectAsStateWithLifecycle()

            val allHighlights by viewModel.allHighlights.collectAsStateWithLifecycle()
            val allBookmarks by viewModel.allBookmarks.collectAsStateWithLifecycle()
            val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

            val isBookPickerOpen by viewModel.isBookPickerOpen.collectAsStateWithLifecycle()
            val isHighlightsSheetOpen by viewModel.isHighlightsSheetOpen.collectAsStateWithLifecycle()
            val isBookmarksSheetOpen by viewModel.isBookmarksSheetOpen.collectAsStateWithLifecycle()
            val isQuoteGeneratorOpen by viewModel.isQuoteGeneratorOpen.collectAsStateWithLifecycle()
            val isSearchSheetOpen by viewModel.isSearchSheetOpen.collectAsStateWithLifecycle()
            val isSettingsSheetOpen by viewModel.isSettingsSheetOpen.collectAsStateWithLifecycle()
            val isNoteDialogOpen by viewModel.isNoteDialogOpen.collectAsStateWithLifecycle()

            // Back handler: dismiss selection or open modals before exiting
            BackHandler(
                enabled = selectedVerseNumber != null || isBookPickerOpen || isHighlightsSheetOpen ||
                        isBookmarksSheetOpen || isQuoteGeneratorOpen || isSearchSheetOpen || isSettingsSheetOpen
            ) {
                if (selectedVerseNumber != null) {
                    viewModel.clearSelection()
                } else {
                    viewModel.isBookPickerOpen.value = false
                    viewModel.isHighlightsSheetOpen.value = false
                    viewModel.isBookmarksSheetOpen.value = false
                    viewModel.isQuoteGeneratorOpen.value = false
                    viewModel.isSearchSheetOpen.value = false
                    viewModel.isSettingsSheetOpen.value = false
                }
            }

            BibliaSacraTheme(preset = activeTheme) {
                Scaffold(
                    topBar = {
                        BibleTopBar(
                            book = currentBook,
                            chapter = currentChapter,
                            translation = currentTranslation,
                            isPlayingAudio = isPlayingAudio,
                            onBookPickerClick = { viewModel.isBookPickerOpen.value = true },
                            onAudioToggle = { viewModel.toggleAudio() },
                            onSearchClick = { viewModel.isSearchSheetOpen.value = true },
                            onBookmarksClick = { viewModel.isBookmarksSheetOpen.value = true },
                            onHighlightsClick = { viewModel.isHighlightsSheetOpen.value = true },
                            onQuoteClick = { viewModel.isQuoteGeneratorOpen.value = true },
                            onSettingsClick = { viewModel.isSettingsSheetOpen.value = true }
                        )
                    },
                    contentWindowInsets = WindowInsets.safeDrawing,
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    ReaderScreen(
                        book = currentBook,
                        chapterNumber = currentChapter,
                        uiState = chapterState,
                        highlights = chapterHighlights,
                        bookmarks = chapterBookmarks,
                        selectedVerseNumber = selectedVerseNumber,
                        spokenVerseNumber = spokenVerseNumber,
                        fontSizeSp = fontSizeSp,
                        onVerseClick = { viewModel.selectVerse(it) },
                        onHighlight = { color -> viewModel.toggleHighlightForSelected(color) },
                        onBookmarkToggle = { previewText -> viewModel.toggleBookmarkForSelected(previewText) },
                        onNoteClick = { viewModel.isNoteDialogOpen.value = true },
                        onSpeakClick = { viewModel.toggleAudio() },
                        onClearSelection = { viewModel.clearSelection() },
                        onPreviousChapter = { viewModel.previousChapter() },
                        onNextChapter = { viewModel.nextChapter() },
                        onRetry = { viewModel.loadChapter() },
                        modifier = Modifier.padding(innerPadding)
                    )

                    // Modals & Sheets
                    if (isBookPickerOpen) {
                        BookChapterPickerSheet(
                            currentBook = currentBook,
                            currentChapter = currentChapter,
                            onSelect = { b, c -> viewModel.navigateTo(b, c) },
                            onDismiss = { viewModel.isBookPickerOpen.value = false }
                        )
                    }

                    if (isHighlightsSheetOpen) {
                        HighlightsSheet(
                            highlights = allHighlights,
                            onNavigateToVerse = { b, c -> viewModel.navigateTo(b, c) },
                            onDeleteHighlight = { viewModel.deleteHighlight(it) },
                            onDismiss = { viewModel.isHighlightsSheetOpen.value = false }
                        )
                    }

                    if (isBookmarksSheetOpen) {
                        BookmarksSheet(
                            bookmarks = allBookmarks,
                            onNavigateToVerse = { b, c -> viewModel.navigateTo(b, c) },
                            onDeleteBookmark = { viewModel.deleteBookmark(it) },
                            onDismiss = { viewModel.isBookmarksSheetOpen.value = false }
                        )
                    }

                    if (isQuoteGeneratorOpen) {
                        QuoteCardSheet(
                            initialBook = currentBook,
                            initialChapter = currentChapter,
                            onNavigateTo = { b, c -> viewModel.navigateTo(b, c) },
                            onDismiss = { viewModel.isQuoteGeneratorOpen.value = false }
                        )
                    }

                    if (isSearchSheetOpen) {
                        SearchSheet(
                            searchResults = searchResults,
                            onSearch = { query -> viewModel.search(query) },
                            onNavigateToVerse = { b, c -> viewModel.navigateTo(b, c) },
                            onDismiss = { viewModel.isSearchSheetOpen.value = false }
                        )
                    }

                    if (isSettingsSheetOpen) {
                        SettingsSheet(
                            currentTranslation = currentTranslation,
                            currentTheme = activeTheme,
                            fontSizeSp = fontSizeSp,
                            onSelectTranslation = { trans -> viewModel.setTranslation(trans) },
                            onSelectTheme = { theme -> viewModel.setTheme(theme) },
                            onFontSizeChange = { size -> viewModel.setFontSize(size) },
                            onAudioSpeedChange = { speed -> viewModel.ttsManager.setSpeed(speed) },
                            onDismiss = { viewModel.isSettingsSheetOpen.value = false }
                        )
                    }

                    if (isNoteDialogOpen && selectedVerseNumber != null) {
                        val existingNote = allHighlights.find {
                            it.book == currentBook && it.chapter == currentChapter && it.verseNumber == selectedVerseNumber
                        }?.note ?: ""

                        NoteDialog(
                            book = currentBook,
                            chapter = currentChapter,
                            verseNumber = selectedVerseNumber!!,
                            initialNote = existingNote,
                            onSave = { note -> viewModel.saveNoteForSelected(note) },
                            onDismiss = { viewModel.isNoteDialogOpen.value = false }
                        )
                    }
                }
            }
        }
    }
}
