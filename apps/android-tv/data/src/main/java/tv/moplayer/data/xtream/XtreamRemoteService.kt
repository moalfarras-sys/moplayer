package tv.moplayer.data.xtream

import org.json.JSONArray
import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.ContentType
import tv.moplayer.domain.model.LiveChannel
import tv.moplayer.domain.model.ServerProfile
import java.net.HttpURLConnection
import java.net.URL

class XtreamRemoteService {
    fun fetch(server: ServerProfile): XtreamPayload {
        val user = server.username ?: return XtreamPayload(emptyList(), emptyList(), emptyList())
        val pass = server.encryptedPassword ?: return XtreamPayload(emptyList(), emptyList(), emptyList())
        val base = server.baseUrl.trimEnd('/')

        val liveJson = request("$base/player_api.php?username=$user&password=$pass&action=get_live_streams")
        val moviesJson = request("$base/player_api.php?username=$user&password=$pass&action=get_vod_streams")
        val seriesJson = request("$base/player_api.php?username=$user&password=$pass&action=get_series")

        return XtreamPayload(
            live = parseLive(liveJson, base, user, pass),
            movies = parseVod(moviesJson, ContentType.MOVIE),
            series = parseVod(seriesJson, ContentType.SERIES)
        )
    }

    private fun request(url: String): String {
        val conn = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 15_000
        }
        return conn.inputStream.bufferedReader().use { it.readText() }
    }

    private fun parseLive(raw: String, base: String, user: String, pass: String): List<LiveChannel> {
        val array = JSONArray(raw)
        val out = mutableListOf<LiveChannel>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val streamId = obj.optString("stream_id")
            val name = obj.optString("name", "Channel")
            val group = obj.optString("category_name", "Live")
            val logo = obj.optString("stream_icon", null)
            val epg = obj.optString("epg_channel_id", null)
            out += LiveChannel(
                id = "xtream-live-$streamId",
                name = name,
                group = group,
                logoUrl = logo,
                streamUrl = "$base/live/$user/$pass/$streamId.ts",
                epgChannelId = epg
            )
        }
        return out
    }

    private fun parseVod(raw: String, type: ContentType): List<ContentItem> {
        val array = JSONArray(raw)
        val out = mutableListOf<ContentItem>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val id = obj.optString("stream_id", obj.optString("series_id", i.toString()))
            out += ContentItem(
                id = "xtream-${type.name.lowercase()}-$id",
                title = obj.optString("name", "Untitled"),
                type = type,
                posterUrl = obj.optString("stream_icon", null),
                group = obj.optString("category_name", if (type == ContentType.MOVIE) "Movies" else "Series"),
                description = obj.optString("plot", null),
                year = obj.optString("year", "").toIntOrNull(),
                rating = obj.optString("rating", "").toDoubleOrNull()
            )
        }
        return out
    }
}

data class XtreamPayload(
    val live: List<LiveChannel>,
    val movies: List<ContentItem>,
    val series: List<ContentItem>
)
