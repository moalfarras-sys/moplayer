package tv.moplayer.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import tv.moplayer.data.parser.M3uParser
import tv.moplayer.data.service.ServerInputValidator
import tv.moplayer.data.xtream.XtreamRemoteService
import tv.moplayer.domain.contracts.EpgProvider
import tv.moplayer.domain.contracts.PlaylistIngestor
import tv.moplayer.domain.contracts.RecommendationEngine
import tv.moplayer.domain.contracts.SupabaseSyncGateway
import tv.moplayer.domain.contracts.XtreamClient
import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.ContentType
import tv.moplayer.domain.model.EpgProgram
import tv.moplayer.domain.model.LiveChannel
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType
import tv.moplayer.domain.model.ThemeConfig
import tv.moplayer.domain.model.WidgetConfig
import java.io.File
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class InMemoryPlaylistIngestor(
    private val parser: M3uParser = M3uParser(),
    private val xtreamRemote: XtreamRemoteService = XtreamRemoteService()
) : PlaylistIngestor {
    private val live = MutableStateFlow<List<LiveChannel>>(emptyList())
    private val moviesFlow = MutableStateFlow<List<ContentItem>>(emptyList())
    private val seriesFlow = MutableStateFlow<List<ContentItem>>(emptyList())

    override suspend fun ingest(server: ServerProfile): Result<Unit> {
        ServerInputValidator.validate(server).getOrElse { return Result.failure(it) }

        return runCatching {
            when (server.type) {
                ServerType.M3U_URL -> {
                    if (server.baseUrl.contains("#EXTM3U", ignoreCase = true)) {
                        ingestM3u(server.baseUrl)
                    } else {
                        ingestM3u(loadM3uFromUrl(server.baseUrl))
                    }
                }
                ServerType.M3U_FILE -> ingestM3u(loadM3uFromFile(server.baseUrl))
                ServerType.SMART_DETECT -> ingestSmart(server)
                ServerType.XTREAM -> ingestXtream(server)
            }
        }
    }

    override fun liveChannels(): Flow<List<LiveChannel>> = live
    override fun movies(): Flow<List<ContentItem>> = moviesFlow
    override fun series(): Flow<List<ContentItem>> = seriesFlow

    private fun ingestSmart(server: ServerProfile) {
        val input = server.baseUrl.trim()
        when {
            input.contains("#EXTM3U", ignoreCase = true) -> ingestM3u(input)
            File(input).exists() -> ingestM3u(loadM3uFromFile(input))
            looksLikeXtreamGetUrl(input) -> ingestXtream(extractXtreamFromGetUrl(server, input))
            looksLikeXtreamApiUrl(input) && !server.username.isNullOrBlank() && !server.encryptedPassword.isNullOrBlank() -> {
                ingestXtream(server.copy(type = ServerType.XTREAM))
            }
            input.startsWith("http://") || input.startsWith("https://") -> ingestM3u(loadM3uFromUrl(input))
            else -> throw IllegalArgumentException("Smart detect could not determine input type")
        }
    }

    private fun ingestM3u(content: String) {
        val parsed = parser.parse(content)
        live.value = parsed.liveChannels
        moviesFlow.value = parsed.movies
        seriesFlow.value = parsed.series
    }

    private fun ingestXtream(server: ServerProfile) {
        val payload = xtreamRemote.fetch(server)
        live.value = payload.live.ifEmpty {
            listOf(LiveChannel("xtream-live-1", "Xtream Sports", "Sports", null, server.baseUrl, "xtream-sports"))
        }
        moviesFlow.value = payload.movies.ifEmpty {
            listOf(
                ContentItem(
                    "xtream-movie-1",
                    "Xtream Blockbuster",
                    ContentType.MOVIE,
                    group = "Action",
                    streamUrl = server.baseUrl
                )
            )
        }
        seriesFlow.value = payload.series.ifEmpty {
            listOf(
                ContentItem(
                    "xtream-series-1",
                    "Xtream Series",
                    ContentType.SERIES,
                    group = "Drama",
                    streamUrl = server.baseUrl
                )
            )
        }
    }

    private fun loadM3uFromFile(path: String): String {
        val file = File(path)
        if (!file.exists()) throw IllegalArgumentException("M3U file does not exist: $path")
        return file.readText()
    }

    private fun loadM3uFromUrl(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 20_000
        }
        return conn.inputStream.bufferedReader().use { it.readText() }.ifBlank {
            throw IllegalStateException("Server returned empty playlist")
        }
    }

    private fun looksLikeXtreamGetUrl(input: String): Boolean {
        val lowered = input.lowercase()
        return lowered.contains("/get.php") && lowered.contains("username=") && lowered.contains("password=")
    }

    private fun looksLikeXtreamApiUrl(input: String): Boolean {
        return input.lowercase().contains("/player_api.php")
    }

    private fun extractXtreamFromGetUrl(original: ServerProfile, getUrl: String): ServerProfile {
        val uri = URI(getUrl)
        val hostBase = "${uri.scheme}://${uri.authority}"
        val params = parseQuery(uri.rawQuery ?: "")
        val username = params["username"]
        val password = params["password"]
        if (username.isNullOrBlank() || password.isNullOrBlank()) {
            throw IllegalArgumentException("Xtream URL must include username/password")
        }
        return original.copy(
            type = ServerType.XTREAM,
            baseUrl = hostBase,
            username = username,
            encryptedPassword = password
        )
    }

    private fun parseQuery(raw: String): Map<String, String> {
        if (raw.isBlank()) return emptyMap()
        return raw.split("&")
            .mapNotNull { pair ->
                val idx = pair.indexOf("=")
                if (idx <= 0) return@mapNotNull null
                pair.substring(0, idx) to pair.substring(idx + 1)
            }
            .toMap()
    }

    private fun samplePlaylist(base: String): String {
        return if (base.contains("#EXTM3U")) {
            base
        } else {
            """
            #EXTM3U
            #EXTINF:-1 group-title="News",News One
            https://example.com/live/news.m3u8
            #EXTINF:-1 group-title="Movies",Cinema Gold
            https://example.com/vod/cinema.mp4
            #EXTINF:-1 group-title="Series",Drama Episode 1
            https://example.com/series/drama_s01e01.mp4
            """.trimIndent()
        }
    }
}

class BasicXtreamClient(
    private val remote: XtreamRemoteService = XtreamRemoteService()
) : XtreamClient {
    override suspend fun validate(server: ServerProfile): Result<Unit> {
        ServerInputValidator.validate(server).getOrElse { return Result.failure(it) }
        return runCatching {
            if (server.type == ServerType.XTREAM) remote.fetch(server)
            Unit
        }
    }
    override suspend fun sync(server: ServerProfile): Result<Unit> = runCatching {
        if (server.type == ServerType.XTREAM) remote.fetch(server)
        Unit
    }
}

class BasicEpgProvider : EpgProvider {
    override suspend fun sync(server: ServerProfile): Result<Unit> = Result.success(Unit)
    override fun currentAndNext(channelId: String): Flow<Pair<EpgProgram?, EpgProgram?>> {
        val now = System.currentTimeMillis()
        val current = EpgProgram(channelId, "Now Playing", now - 20 * 60_000, now + 40 * 60_000, "Current show")
        val next = EpgProgram(channelId, "Up Next", now + 40 * 60_000, now + 100 * 60_000, "Next show")
        return MutableStateFlow(current to next)
    }
}

class RuleBasedRecommendationEngine : RecommendationEngine {
    private val recommended = MutableStateFlow<List<ContentItem>>(emptyList())

    fun seedByHistory(history: List<ContentItem>, catalog: List<ContentItem>) {
        val groups = history.mapNotNull { it.group }.toSet()
        recommended.value = catalog.filter { it.group != null && groups.contains(it.group) }.take(20)
    }

    override fun recommend(limit: Int): Flow<List<ContentItem>> = MutableStateFlow(recommended.value.take(limit))
}

class InMemorySupabaseSyncGateway : SupabaseSyncGateway {
    private val theme = MutableStateFlow(ThemeConfig("default", "#6BC7FF", 24f, 0.12f, true))
    private val widgets = MutableStateFlow(WidgetConfig(weatherEnabled = true, matchesEnabled = true, leagues = listOf("EPL", "LaLiga")))
    private val flags = MutableStateFlow(mapOf("live_preview_muted" to true, "pip_enabled" to true))

    override suspend fun syncServer(server: ServerProfile): Result<Unit> = Result.success(Unit)
    override fun themeUpdates(): Flow<ThemeConfig> = theme
    override fun widgetUpdates(): Flow<WidgetConfig> = widgets
    override fun featureFlagUpdates(): Flow<Map<String, Boolean>> = flags

    fun broadcastTheme(config: ThemeConfig) { theme.value = config }
    fun broadcastWidgets(config: WidgetConfig) { widgets.value = config }
    fun broadcastFlags(config: Map<String, Boolean>) { flags.value = config }
}
