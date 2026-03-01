package tv.moplayer.domain.model

enum class ContentType { LIVE, MOVIE, SERIES, EPISODE }

data class ContentItem(
    val id: String,
    val title: String,
    val type: ContentType,
    val posterUrl: String? = null,
    val group: String? = null,
    val streamUrl: String? = null,
    val description: String? = null,
    val year: Int? = null,
    val durationMinutes: Int? = null,
    val rating: Double? = null
)

data class LiveChannel(
    val id: String,
    val name: String,
    val group: String,
    val logoUrl: String? = null,
    val streamUrl: String,
    val epgChannelId: String? = null
)

data class Movie(val item: ContentItem)
data class Series(val item: ContentItem, val seasons: List<Int>)
data class Episode(val item: ContentItem, val season: Int, val episode: Int)

data class EpgProgram(
    val channelId: String,
    val title: String,
    val startUtcMillis: Long,
    val endUtcMillis: Long,
    val description: String? = null
)

data class ServerProfile(
    val id: String,
    val name: String,
    val type: ServerType,
    val baseUrl: String,
    val username: String? = null,
    val encryptedPassword: String? = null,
    val externalEpgUrl: String? = null,
    val isDefault: Boolean = false
)

enum class ServerType { M3U_URL, M3U_FILE, XTREAM, SMART_DETECT }

data class ThemeConfig(
    val id: String,
    val accentHex: String,
    val blurStrength: Float,
    val glassOpacity: Float,
    val darkMode: Boolean
)

data class WidgetConfig(
    val weatherEnabled: Boolean = true,
    val matchesEnabled: Boolean = true,
    val leagues: List<String> = emptyList()
)