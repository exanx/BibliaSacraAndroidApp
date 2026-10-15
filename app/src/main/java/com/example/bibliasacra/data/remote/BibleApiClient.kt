package com.example.bibliasacra.data.remote

import com.example.bibliasacra.data.model.Chapter
import com.example.bibliasacra.data.model.Translation
import com.example.bibliasacra.data.model.Verse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class BibleApiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    // In-memory cache for full book JSONs so chapters load instantly
    private val bookCache = mutableMapOf<String, String>()

    suspend fun fetchChapter(translation: Translation, book: String, chapter: Int): Chapter = withContext(Dispatchers.IO) {
        if (translation.baseUrl != null) {
            fetchFromStaticJson(translation, book, chapter)
        } else {
            fetchFromBibleApi(translation, book, chapter)
        }
    }

    private fun fetchFromStaticJson(translation: Translation, book: String, chapter: Int): Chapter {
        val encodedBook = URLEncoder.encode(book, "UTF-8").replace("+", "%20")
        val bookUrl = "${translation.baseUrl}/$encodedBook.json"
        
        val jsonString = bookCache[bookUrl] ?: run {
            val request = Request.Builder().url(bookUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw Exception("Network error loading $book: ${response.code}")
                val body = response.body?.string() ?: throw Exception("Empty response for $book")
                bookCache[bookUrl] = body
                body
            }
        }

        val jsonObject = JSONObject(jsonString)
        val verses = mutableListOf<Verse>()

        if (jsonObject.has("chapters")) {
            val chaptersArray = jsonObject.getJSONArray("chapters")
            for (i in 0 until chaptersArray.length()) {
                val chapObj = chaptersArray.getJSONObject(i)
                val chapNum = chapObj.optString("chapter", "")
                if (chapNum == chapter.toString()) {
                    val versesArray = chapObj.getJSONArray("verses")
                    for (v in 0 until versesArray.length()) {
                        val vObj = versesArray.getJSONObject(v)
                        val vNum = vObj.optString("verse", (v + 1).toString()).toIntOrNull() ?: (v + 1)
                        val text = vObj.optString("text", "").replace("\n", " ").trim()
                        verses.add(Verse(vNum, text))
                    }
                    break
                }
            }
        } else if (jsonObject.has(chapter.toString())) {
            val chapObj = jsonObject.getJSONObject(chapter.toString())
            val keys = chapObj.keys()
            val sortedKeys = mutableListOf<Int>()
            while (keys.hasNext()) {
                val k = keys.next()
                k.toIntOrNull()?.let { sortedKeys.add(it) }
            }
            sortedKeys.sort()
            for (k in sortedKeys) {
                val text = chapObj.optString(k.toString(), "").replace("\n", " ").trim()
                verses.add(Verse(k, text))
            }
        }

        if (verses.isEmpty()) {
            throw Exception("Chapter $chapter of $book was not found in ${translation.name}")
        }

        return Chapter(
            book = book,
            chapter = chapter,
            translationId = translation.id,
            translationName = translation.name,
            verses = verses
        )
    }

    private fun fetchFromBibleApi(translation: Translation, book: String, chapter: Int): Chapter {
        val encodedRef = URLEncoder.encode("$book $chapter", "UTF-8")
        val url = "https://bible-api.com/$encodedRef?translation=${translation.id}"
        val request = Request.Builder().url(url).build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Error fetching scripture: ${response.code}")
            val body = response.body?.string() ?: throw Exception("Scripture body is empty")
            val root = JSONObject(body)
            val versesArray = root.getJSONArray("verses")
            val verses = mutableListOf<Verse>()
            for (i in 0 until versesArray.length()) {
                val vObj = versesArray.getJSONObject(i)
                val vNum = vObj.optInt("verse", i + 1)
                val text = vObj.optString("text", "").replace("\n", " ").trim()
                verses.add(Verse(vNum, text))
            }
            return Chapter(
                book = book,
                chapter = chapter,
                translationId = translation.id,
                translationName = root.optString("translation_name", translation.name),
                verses = verses
            )
        }
    }
}
