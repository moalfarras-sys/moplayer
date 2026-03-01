package tv.moplayer.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import tv.moplayer.data.parser.M3uParser

class M3uParserTest {
    @Test
    fun parse_splits_live_movies_series() {
        val m3u = """
            #EXTM3U
            #EXTINF:-1 group-title="News",News Channel
            https://x/live.m3u8
            #EXTINF:-1 group-title="Movies",Movie A
            https://x/movie.mp4
            #EXTINF:-1 group-title="Series",Series A
            https://x/series.mp4
        """.trimIndent()

        val parsed = M3uParser().parse(m3u)

        assertEquals(1, parsed.liveChannels.size)
        assertEquals(1, parsed.movies.size)
        assertEquals(1, parsed.series.size)
        assertTrue(parsed.liveChannels.first().name.contains("News"))
    }
}