package tv.moplayer.data.parser

import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.ContentType
import tv.moplayer.domain.model.LiveChannel

class M3uParser {
    fun parse(content: String): ParsedPlaylist {
        val lines = content.lines()
        val liveChannels = mutableListOf<LiveChannel>()
        val movies = mutableListOf<ContentItem>()
        val series = mutableListOf<ContentItem>()

        var currentName = ""
        var currentGroup = "General"
        var currentLogo: String? = null

        lines.forEachIndexed { index, raw ->
            val line = raw.trim()
            if (line.startsWith("#EXTINF", ignoreCase = true)) {
                currentName = line.substringAfterLast(",", "Unnamed")
                currentGroup = extractAttr(line, "group-title") ?: "General"
                currentLogo = extractAttr(line, "tvg-logo")
            } else if (line.startsWith("http", ignoreCase = true)) {
                val id = "item-$index"
                val type = detectType(currentGroup, currentName)
                when (type) {
                    ContentType.LIVE -> liveChannels += LiveChannel(
                        id = id,
                        name = currentName,
                        group = currentGroup,
                        logoUrl = currentLogo,
                        streamUrl = line,
                        epgChannelId = extractSlug(currentName)
                    )

                    ContentType.MOVIE -> movies += ContentItem(
                        id = id,
                        title = currentName,
                        type = ContentType.MOVIE,
                        posterUrl = currentLogo,
                        group = currentGroup,
                        streamUrl = line
                    )

                    ContentType.SERIES, ContentType.EPISODE -> series += ContentItem(
                        id = id,
                        title = currentName,
                        type = ContentType.SERIES,
                        posterUrl = currentLogo,
                        group = currentGroup,
                        streamUrl = line
                    )
                }
            }
        }

        return ParsedPlaylist(liveChannels, movies, series)
    }

    private fun detectType(group: String, title: String): ContentType {
        val token = "$group $title".lowercase()
        return when {
            token.contains("series") || token.contains("????") -> ContentType.SERIES
            token.contains("movie") || token.contains("film") || token.contains("????") -> ContentType.MOVIE
            else -> ContentType.LIVE
        }
    }

    private fun extractAttr(line: String, key: String): String? {
        val pattern = "$key=\""
        val start = line.indexOf(pattern)
        if (start == -1) return null
        val begin = start + pattern.length
        val end = line.indexOf('"', begin)
        if (end == -1) return null
        return line.substring(begin, end)
    }

    private fun extractSlug(value: String): String =
        value.lowercase().replace("[^a-z0-9]+".toRegex(), "-").trim('-')
}

data class ParsedPlaylist(
    val liveChannels: List<LiveChannel>,
    val movies: List<ContentItem>,
    val series: List<ContentItem>
)