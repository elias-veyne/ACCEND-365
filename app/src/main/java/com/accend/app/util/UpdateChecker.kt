package com.accend.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class UpdateChecker(private val currentVersion: String = "0.1.0") {
    suspend fun latestVersion(): String? = withContext(Dispatchers.IO) {
        runCatching {
            val connection = URL("https://api.github.com/repos/elias-veyne/ACCEND-365/releases/latest").openConnection() as HttpURLConnection
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.requestMethod = "GET"
            if (connection.responseCode !in 200..299) return@runCatching null
            JSONObject(connection.inputStream.bufferedReader().use { it.readText() }).optString("tag_name").removePrefix("v").takeIf { it.isNotBlank() }
        }.getOrNull()
    }

    suspend fun isUpdateAvailable(): Boolean {
        val latest = latestVersion() ?: return false
        return latest != currentVersion
    }
}