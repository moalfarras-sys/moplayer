package tv.moplayer.data.supabase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.json.JSONArray
import org.json.JSONObject
import tv.moplayer.domain.contracts.SupabaseSyncGateway
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ThemeConfig
import tv.moplayer.domain.model.WidgetConfig
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class RestSupabaseSyncGateway(
    private val baseUrl: String,
    private val anonKey: String
) : SupabaseSyncGateway {
    private val theme = MutableStateFlow(ThemeConfig("default", "#6BC7FF", 24f, 0.12f, true))
    private val widgets = MutableStateFlow(WidgetConfig(weatherEnabled = true, matchesEnabled = true, leagues = listOf("EPL")))
    private val flags = MutableStateFlow<Map<String, Boolean>>(emptyMap())

    override suspend fun syncServer(server: ServerProfile): Result<Unit> = runCatching {
        val url = "$baseUrl/rest/v1/servers"
        val payload = JSONObject()
            .put("id", server.id)
            .put("name", server.name)
            .put("type", server.type.name)
            .put("base_url", server.baseUrl)
            .put("username", server.username)
            .put("encrypted_password", server.encryptedPassword)
            .put("external_epg_url", server.externalEpgUrl)
            .put("is_default", server.isDefault)

        request(url, "POST", payload.toString(), extraHeaders = mapOf("Prefer" to "resolution=merge-duplicates"))
        Unit
    }

    override fun themeUpdates(): Flow<ThemeConfig> = theme
    override fun widgetUpdates(): Flow<WidgetConfig> = widgets
    override fun featureFlagUpdates(): Flow<Map<String, Boolean>> = flags

    suspend fun refreshTheme(): Result<Unit> = runCatching {
        val raw = request("$baseUrl/rest/v1/themes?select=*&limit=1", "GET", null)
        val arr = JSONArray(raw)
        if (arr.length() > 0) {
            val row = arr.getJSONObject(0)
            theme.value = ThemeConfig(
                id = row.optString("id", "default"),
                accentHex = row.optString("accent_hex", "#6BC7FF"),
                blurStrength = row.optDouble("blur_strength", 24.0).toFloat(),
                glassOpacity = row.optDouble("glass_opacity", 0.12).toFloat(),
                darkMode = row.optBoolean("dark_mode", true)
            )
        }
        Unit
    }

    suspend fun refreshFlags(): Result<Unit> = runCatching {
        val raw = request("$baseUrl/rest/v1/feature_flags?select=key,enabled", "GET", null)
        val arr = JSONArray(raw)
        val map = mutableMapOf<String, Boolean>()
        for (i in 0 until arr.length()) {
            val row = arr.getJSONObject(i)
            map[row.optString("key")] = row.optBoolean("enabled", false)
        }
        flags.value = map
        Unit
    }

    private fun request(url: String, method: String, body: String?, extraHeaders: Map<String, String> = emptyMap()): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 10_000
            readTimeout = 15_000
            setRequestProperty("apikey", anonKey)
            setRequestProperty("Authorization", "Bearer $anonKey")
            setRequestProperty("Content-Type", "application/json")
            extraHeaders.forEach { (k, v) -> setRequestProperty(k, v) }
            doInput = true
            doOutput = body != null
        }

        if (body != null) {
            OutputStreamWriter(conn.outputStream).use { it.write(body) }
        }

        val stream = if (conn.responseCode in 200..299) conn.inputStream else conn.errorStream
        return stream?.bufferedReader()?.use { it.readText() } ?: ""
    }
}
