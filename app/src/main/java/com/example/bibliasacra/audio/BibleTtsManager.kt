package com.example.bibliasacra.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.bibliasacra.data.model.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class BibleTtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private var currentVersesList = listOf<Verse>()
    private var currentIndex = 0
    private var speechRate = 1.0f

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentSpokenVerse = MutableStateFlow<Int?>(null)
    val currentSpokenVerse: StateFlow<Int?> = _currentSpokenVerse.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setSpeechRate(speechRate)
            isInitialized = true

            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    val verseNum = utteranceId?.toIntOrNull()
                    _currentSpokenVerse.value = verseNum
                }

                override fun onDone(utteranceId: String?) {
                    currentIndex++
                    if (currentIndex < currentVersesList.size) {
                        speakCurrentIndex()
                    } else {
                        _isPlaying.value = false
                        _currentSpokenVerse.value = null
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                    _currentSpokenVerse.value = null
                }
            })
        }
    }

    fun startReading(verses: List<Verse>, startIndex: Int = 0) {
        if (verses.isEmpty()) return
        currentVersesList = verses
        currentIndex = startIndex.coerceIn(0, verses.size - 1)
        _isPlaying.value = true
        speakCurrentIndex()
    }

    private fun speakCurrentIndex() {
        if (!isInitialized || currentIndex !in currentVersesList.indices) {
            _isPlaying.value = false
            _currentSpokenVerse.value = null
            return
        }

        val verse = currentVersesList[currentIndex]
        val textToSpeak = "Verse ${verse.verseNumber}. ${verse.text}"
        _currentSpokenVerse.value = verse.verseNumber

        tts?.speak(
            textToSpeak,
            TextToSpeech.QUEUE_FLUSH,
            null,
            verse.verseNumber.toString()
        )
    }

    fun pauseOrStop() {
        tts?.stop()
        _isPlaying.value = false
        _currentSpokenVerse.value = null
    }

    fun setSpeed(rate: Float) {
        speechRate = rate
        tts?.setSpeechRate(rate)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
