package com.example.financeflow.data.remote

import com.example.financeflow.BuildConfig
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIService @Inject constructor() {

    companion object {
        const val CONFIG_ERROR_MESSAGE = "AI service configuration missing"
        const val CONNECTION_ERROR_MESSAGE = "Unable to connect to AI assistant"
        private const val MODEL_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
    }

    suspend fun generateResponse(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()
        Log.d("GeminiDebug", "API Key Length = ${apiKey.length}")
        Log.d("GeminiDebug", "API Key = ${maskKey(apiKey)}")
        if (apiKey.isBlank()) {
            Log.e("AIService", "BuildConfig.GEMINI_API_KEY is empty. Check local.properties and Gradle sync.")
            return@withContext Result.failure(IllegalStateException(CONFIG_ERROR_MESSAGE))
        }

        val endpoint = "$MODEL_ENDPOINT?key=$apiKey"
        val url = URL(endpoint)
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json")
            doOutput = true
            connectTimeout = 15000
            readTimeout = 20000
        }

        val payload = JSONObject().apply {
            put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", prompt)))))
        }

        try {
            connection.outputStream.use { stream ->
                stream.write(payload.toString().toByteArray())
            }

            val responseCode = connection.responseCode
            val responseText = if (responseCode in 200..299) {
                BufferedReader(InputStreamReader(connection.inputStream)).use { it.readText() }
            } else {
                BufferedReader(InputStreamReader(connection.errorStream)).use { it.readText() }
            }

            if (responseCode !in 200..299) {
                Log.e("AIService", "AI request failed: $responseCode $responseText")
                return@withContext Result.failure(IllegalStateException(CONNECTION_ERROR_MESSAGE))
            }

            val json = JSONObject(responseText)
            val text = json
                .optJSONArray("candidates")
                ?.optJSONObject(0)
                ?.optJSONObject("content")
                ?.optJSONArray("parts")
                ?.optJSONObject(0)
                ?.optString("text")
                ?.trim()
                .orEmpty()

            if (text.isBlank()) {
                Log.e("AIService", "Empty AI response")
                return@withContext Result.failure(IllegalStateException(CONNECTION_ERROR_MESSAGE))
            }

            Result.success(text)
        } catch (e: Exception) {
            Log.e("AIService", e.message ?: "AI request error")
            Result.failure(IllegalStateException(CONNECTION_ERROR_MESSAGE))
        } finally {
            connection.disconnect()
        }
    }

    private fun maskKey(key: String): String {
        if (key.isBlank()) return "<empty>"
        if (key.length <= 6) return "${"*".repeat(key.length)}"
        val start = key.take(4)
        val end = key.takeLast(3)
        return "$start${"*".repeat(key.length - 7)}$end"
    }
}
