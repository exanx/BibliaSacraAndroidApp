package com.example.bibliasacra.ui.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bibliasacra.data.model.HighlightColor
import com.example.bibliasacra.data.model.Verse
import com.example.bibliasacra.ui.ChapterUiState
import com.example.bibliasacra.ui.components.VerseActionToolbar

@Composable
fun ReaderScreen(
    book: String,
    chapterNumber: Int,
    uiState: ChapterUiState,
    highlights: Map<Int, HighlightColor>,
    bookmarks: Set<Int>,
    selectedVerseNumber: Int?,
    spokenVerseNumber: Int?,
    fontSizeSp: Int,
    onVerseClick: (Int) -> Unit,
    onHighlight: (HighlightColor) -> Unit,
    onBookmarkToggle: (String) -> Unit,
    onNoteClick: () -> Unit,
    onSpeakClick: () -> Unit,
    onClearSelection: () -> Unit,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            is ChapterUiState.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Loading $book $chapterNumber…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            is ChapterUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Unable to load scripture",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.message,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRetry,
                        modifier = Modifier.testTag("retry_load_chapter_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Retry")
                    }
                }
            }

            is ChapterUiState.Success -> {
                val chapter = uiState.chapter
                val selectedVerseText = chapter.verses.find { it.verseNumber == selectedVerseNumber }?.text ?: ""

                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp)
                    ) {
                        // Chapter Title Header
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = chapter.book,
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Light,
                                        letterSpacing = 2.sp
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "CHAPTER ${chapter.chapter}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 3.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .width(48.dp)
                                        .height(2.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Verses
                        items(chapter.verses, key = { it.verseNumber }) { verse ->
                            VerseItem(
                                verse = verse,
                                highlightColor = highlights[verse.verseNumber],
                                isBookmarked = bookmarks.contains(verse.verseNumber),
                                isSelected = selectedVerseNumber == verse.verseNumber,
                                isSpoken = spokenVerseNumber == verse.verseNumber,
                                fontSizeSp = fontSizeSp,
                                onClick = { onVerseClick(verse.verseNumber) }
                            )
                        }

                        // Bottom Chapter Navigation Bar
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = onPreviousChapter,
                                    modifier = Modifier.testTag("prev_chapter_btn")
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Previous")
                                }

                                Text(
                                    text = "${chapter.book} ${chapter.chapter}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )

                                Button(
                                    onClick = onNextChapter,
                                    modifier = Modifier.testTag("next_chapter_btn")
                                ) {
                                    Text("Next")
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Floating Verse Action Toolbar at Bottom
                if (selectedVerseNumber != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        VerseActionToolbar(
                            visible = true,
                            book = book,
                            chapter = chapterNumber,
                            verseNumber = selectedVerseNumber,
                            verseText = selectedVerseText,
                            isBookmarked = bookmarks.contains(selectedVerseNumber),
                            onHighlight = onHighlight,
                            onBookmarkToggle = { onBookmarkToggle(selectedVerseText) },
                            onNoteClick = onNoteClick,
                            onSpeakClick = onSpeakClick,
                            onDismiss = onClearSelection
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VerseItem(
    verse: Verse,
    highlightColor: HighlightColor?,
    isBookmarked: Boolean,
    isSelected: Boolean,
    isSpoken: Boolean,
    fontSizeSp: Int,
    onClick: () -> Unit
) {
    val highlightBg = when (highlightColor) {
        HighlightColor.YELLOW -> Color(0xFFFBBF24).copy(alpha = 0.28f)
        HighlightColor.GREEN -> Color(0xFF34D399).copy(alpha = 0.28f)
        HighlightColor.BLUE -> Color(0xFF60A5FA).copy(alpha = 0.28f)
        HighlightColor.PINK -> Color(0xFFF472B6).copy(alpha = 0.28f)
        HighlightColor.PURPLE -> Color(0xFFA78BFA).copy(alpha = 0.28f)
        null -> Color.Transparent
    }

    val itemBg = when {
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        isSpoken -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f)
        highlightColor != null -> highlightBg
        else -> Color.Transparent
    }

    val borderModifier = when {
        isSelected -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
        isSpoken -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(itemBg)
            .then(borderModifier)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("verse_item_${verse.verseNumber}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            crossAxisAlignment = Alignment.Top
        ) {
            // Verse number badge
            Text(
                text = "${verse.verseNumber}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = (fontSizeSp * 0.65).sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected || isSpoken) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .padding(top = 2.dp, end = 8.dp)
                    .widthIn(min = 22.dp)
            )

            // Verse text with optional bookmark badge
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = verse.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSizeSp.sp,
                        lineHeight = (fontSizeSp * 1.6).sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                if (isBookmarked) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Bookmarked",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Bookmarked",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
