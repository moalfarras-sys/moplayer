package tv.moplayer.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import tv.moplayer.data.repository.InMemoryPlaylistIngestor
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType
import java.io.File
import java.util.UUID

class PlaylistIngestorServerTypesTest {

    @Test
    fun ingest_supports_m3u_url_m3u_file_and_smart_detect() = runBlocking {
        val ingestor = InMemoryPlaylistIngestor()
        val inlineM3u = """
            #EXTM3U
            #EXTINF:-1 group-title="News",News Inline
            https://example.com/live-inline.m3u8
            #EXTINF:-1 group-title="Movies",Movie Inline
            https://example.com/movie-inline.mp4
            #EXTINF:-1 group-title="Series",Series Inline
            https://example.com/series-inline.mp4
        """.trimIndent()

        val m3uUrlProfile = ServerProfile(
            id = UUID.randomUUID().toString(),
            name = "M3U URL",
            type = ServerType.M3U_URL,
            baseUrl = inlineM3u
        )
        assertTrue(ingestor.ingest(m3uUrlProfile).isSuccess)
        assertTrue(ingestor.liveChannels().first().isNotEmpty())

        val tmpFile = File.createTempFile("moplayer-test", ".m3u").apply {
            writeText(
                """
                #EXTM3U
                #EXTINF:-1 group-title="Movies",Movie File
                https://example.com/movie-file.mp4
                """.trimIndent()
            )
            deleteOnExit()
        }
        val m3uFileProfile = ServerProfile(
            id = UUID.randomUUID().toString(),
            name = "M3U File",
            type = ServerType.M3U_FILE,
            baseUrl = tmpFile.absolutePath
        )
        assertTrue(ingestor.ingest(m3uFileProfile).isSuccess)
        assertTrue(ingestor.movies().first().isNotEmpty())

        val smartDetectProfile = ServerProfile(
            id = UUID.randomUUID().toString(),
            name = "Smart Detect",
            type = ServerType.SMART_DETECT,
            baseUrl = inlineM3u
        )
        assertTrue(ingestor.ingest(smartDetectProfile).isSuccess)
        assertTrue(ingestor.series().first().isNotEmpty())
    }

    @Test
    fun ingest_supports_xtream_api_end_to_end() = runBlocking {
        val server = MockWebServer()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val action = request.requestUrl?.queryParameter("action")
                val body = when (action) {
                    "get_live_streams" ->
                        """[{"stream_id":101,"name":"Live One","category_name":"Sports","stream_icon":"https://img/live.png","epg_channel_id":"live.one"}]"""
                    "get_vod_streams" ->
                        """[{"stream_id":202,"name":"Movie One","category_name":"Movies","stream_icon":"https://img/movie.png","plot":"plot","year":"2025","rating":"8.0"}]"""
                    "get_series" ->
                        """[{"series_id":303,"name":"Series One","category_name":"Series","stream_icon":"https://img/series.png","plot":"plot","year":"2024","rating":"7.9"}]"""
                    else -> "[]"
                }
                return MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody(body)
            }
        }
        server.start()

        try {
            val baseUrl = server.url("/").toString().trimEnd('/')
            val profile = ServerProfile(
                id = UUID.randomUUID().toString(),
                name = "Xtream",
                type = ServerType.XTREAM,
                baseUrl = baseUrl,
                username = "demo",
                encryptedPassword = "demo-pass"
            )

            val ingestor = InMemoryPlaylistIngestor()
            val result = ingestor.ingest(profile)
            assertTrue("Xtream ingest failed: ${result.exceptionOrNull()?.message}", result.isSuccess)

            val live = ingestor.liveChannels().first()
            val movies = ingestor.movies().first()
            val series = ingestor.series().first()

            assertEquals(1, live.size)
            assertEquals(1, movies.size)
            assertEquals(1, series.size)
            assertEquals("Live One", live.first().name)
            assertEquals("Movie One", movies.first().title)
            assertEquals("Series One", series.first().title)
        } finally {
            server.shutdown()
        }
    }

    @Test
    fun smart_detect_can_promote_xtream_get_url() = runBlocking {
        val server = MockWebServer()
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val action = request.requestUrl?.queryParameter("action")
                val body = when (action) {
                    "get_live_streams" -> """[{"stream_id":111,"name":"Smart Live","category_name":"Sports"}]"""
                    "get_vod_streams" -> """[{"stream_id":222,"name":"Smart Movie","category_name":"Movies"}]"""
                    "get_series" -> """[{"series_id":333,"name":"Smart Series","category_name":"Series"}]"""
                    else -> "[]"
                }
                return MockResponse().setResponseCode(200).setBody(body)
            }
        }
        server.start()
        try {
            val getUrl = server.url(
                "/get.php?username=demo&password=pass&type=m3u_plus&output=ts"
            ).toString()
            val ingestor = InMemoryPlaylistIngestor()
            val profile = ServerProfile(
                id = UUID.randomUUID().toString(),
                name = "Smart Xtream",
                type = ServerType.SMART_DETECT,
                baseUrl = getUrl
            )
            val result = ingestor.ingest(profile)
            assertTrue("Smart detect Xtream failed: ${result.exceptionOrNull()?.message}", result.isSuccess)
            assertEquals("Smart Live", ingestor.liveChannels().first().first().name)
            assertEquals("Smart Movie", ingestor.movies().first().first().title)
            assertEquals("Smart Series", ingestor.series().first().first().title)
        } finally {
            server.shutdown()
        }
    }
}
