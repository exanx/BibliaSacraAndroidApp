# Biblia Sacra (Android)

A modern, distraction-free Catholic Bible reading application built for Android using Kotlin and Jetpack Compose with Material Design 3.

## Features

- **Complete 73-Book Catholic Canon**: Full access to all 46 books of the Old Testament (including Deuterocanonical books: Tobit, Judith, 1 & 2 Maccabees, Wisdom, Sirach, and Baruch) and all 27 books of the New Testament.
- **Multiple Translations**:
  - **Catholic Public Domain Version (CPDV)** (Default)
  - **Douay-Rheims Bible (DRB)**
  - **World English Bible: Catholic Edition (WEB)**
  - **King James Version (with Deuterocanon)**
- **Native Audio Narration**: Text-to-Speech (TTS) scripture reading with real-time verse tracking, play/pause, and adjustable speech rate (0.75x–1.5x).
- **Study & Personalization**:
  - **5-Color Verse Highlighting**: Yellow, Green, Blue, Pink, and Purple.
  - **Personal Verse Notes**: Add private reflections or study commentary directly to any verse.
  - **Bookmarks Manager**: Save and easily return to favorite passages.
- **Scripture Quote Card Generator**:
  - Create shareable scripture cards with liturgical gradients and typography.
  - Famous inspiration verse presets (John 3:16, Psalm 23, Philippians 4:13, Romans 8:28, Wisdom 3:1).
  - One-tap Copy and System Share sheet.
- **Visual Themes**:
  - AMOLED Black (pitch-black OLED mode)
  - Pure White (clean daytime reading)
  - Golden Parchment (illuminated manuscript aesthetic)
  - Ancient Scroll (papyrus and gall ink tones)
  - Eden Sage (botanical laurel green)
  - Night Sky (celestial sapphire)
  - Royal Amethyst (liturgical violet)
- **Offline-First Architecture**: Powered by Android Room Database with automatic caching of downloaded chapters and verses.

## Architecture

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Design Pattern**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Persistence**: Room Database (`sancta_biblia_database`)
- **Networking**: OkHttp with Kotlin Coroutines
- **Audio**: Android TextToSpeech engine
