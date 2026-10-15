package com.example.bibliasacra.ui.quotes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bibliasacra.data.model.QuoteInspirations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteCardSheet(
    initialBook: String,
    initialChapter: Int,
    onNavigateTo: (book: String, chapter: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedInspiration by remember {
        mutableStateOf(QuoteInspirations.FAMOUS_VERSES.first())
    }

    val sampleQuotesMap = remember {
        mapOf(
            "John" to "For God so loved the world, as to give his only begotten Son; that whosoever believeth in him, may not perish, but may have life everlasting.",
            "Psalms" to "The Lord ruleth me: and I shall want nothing. He hath set me in a place of pasture.",
            "Philippians" to "I can do all things in him who strengtheneth me.",
            "Romans" to "And we know that to them that love God, all things work together unto good, to such as, according to his purpose, are called to be saints.",
            "Proverbs" to "Have confidence in the Lord with all thy heart, and lean not upon thy own prudence. In all thy ways think on him, and he will direct thy steps.",
            "Wisdom" to "The souls of the just are in the hand of God, and the torment of death shall not touch them.",
            "Sirach" to "Son, when thou comest to the service of God, stand in justice and in fear, and prepare thy soul for temptation."
        )
    }

    val cardGradients = listOf(
        listOf(Color(0xFF312E81), Color(0xFF4F46E5)), // Indigo Royalty
        listOf(Color(0xFF0F172A), Color(0xFF1E293B)), // Obsidian Slate
        listOf(Color(0xFF78350F), Color(0xFFD97706)), // Golden Parchment
        listOf(Color(0xFF064E3B), Color(0xFF059669)), // Eden Emerald
        listOf(Color(0xFF581C87), Color(0xFF9333EA))  // Imperial Liturgical Violet
    )
    var selectedGradientIndex by remember { mutableIntStateOf(0) }

    val quoteText = sampleQuotesMap[selectedInspiration.book]
        ?: "Trust in the Lord with all your heart, and do not lean on your own understanding."
    val referenceText = "${selectedInspiration.book} ${selectedInspiration.chapter}:${selectedInspiration.fromVerse}" +
            if (selectedInspiration.fromVerse != selectedInspiration.toVerse) "-${selectedInspiration.toVerse}" else ""

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .testTag("quote_generator_modal")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scripture Quote Card",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Inspiration Chips
            Text(
                text = "Inspiration Presets",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(QuoteInspirations.FAMOUS_VERSES) { item ->
                    val isSelected = selectedInspiration == item
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedInspiration = item },
                        label = { Text("${item.book} ${item.chapter}:${item.fromVerse} (${item.themeTag})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Gradient Palette Selector
            Text(
                text = "Card Theme",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                cardGradients.forEachIndexed { index, gradient ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(gradient))
                            .border(
                                width = if (selectedGradientIndex == index) 2.5.dp else 1.dp,
                                color = if (selectedGradientIndex == index) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                            .clickable { selectedGradientIndex = index }
                            .testTag("quote_palette_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // The Quote Card Preview
            Surface(
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("quote_preview_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(cardGradients[selectedGradientIndex]))
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "“$quoteText”",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 18.sp,
                                lineHeight = 28.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(1.5.dp)
                                .background(Color.White.copy(alpha = 0.4f))
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = referenceText,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        )
                        Text(
                            text = "BIBLIA SACRA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                letterSpacing = 2.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Actions: Copy, Share, Jump to chapter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Scripture Quote", "“$quoteText”\n— $referenceText (Biblia Sacra)")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Quote copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("copy_quote_btn")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy")
                }

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "“$quoteText”\n\n— $referenceText\nBiblia Sacra")
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Scripture Quote"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_quote_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FilledTonalButton(
                onClick = {
                    onNavigateTo(selectedInspiration.book, selectedInspiration.chapter)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_in_reader_btn")
            ) {
                Text("Read in Chapter (${selectedInspiration.book} ${selectedInspiration.chapter})")
            }
        }
    }
}
