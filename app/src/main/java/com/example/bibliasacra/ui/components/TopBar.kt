package com.example.bibliasacra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bibliasacra.data.model.Translation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibleTopBar(
    book: String,
    chapter: Int,
    translation: Translation,
    isPlayingAudio: Boolean,
    onBookPickerClick: () -> Unit,
    onAudioToggle: () -> Unit,
    onSearchClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onHighlightsClick: () -> Unit,
    onQuoteClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                    .clickable { onBookPickerClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
                    .testTag("book_chapter_picker_pill")
            ) {
                Text(
                    text = "$book $chapter",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(
                        text = translation.shortName,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Book and Chapter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        actions = {
            // Audio TTS Button
            IconButton(
                onClick = onAudioToggle,
                modifier = Modifier.testTag("audio_toggle_btn")
            ) {
                Icon(
                    imageVector = if (isPlayingAudio) Icons.Default.PauseCircle else Icons.Default.VolumeUp,
                    contentDescription = if (isPlayingAudio) "Pause Audio" else "Listen to Scripture",
                    tint = if (isPlayingAudio) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            // Quote Generator
            IconButton(
                onClick = onQuoteClick,
                modifier = Modifier.testTag("quote_generator_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.FormatQuote,
                    contentDescription = "Create Quote Card",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Highlights
            IconButton(
                onClick = onHighlightsClick,
                modifier = Modifier.testTag("highlights_nav_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Brush,
                    contentDescription = "Saved Highlights",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Bookmarks
            IconButton(
                onClick = onBookmarksClick,
                modifier = Modifier.testTag("bookmarks_nav_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmarks",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Search
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("search_nav_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Scripture",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            // Settings
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("settings_nav_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}
