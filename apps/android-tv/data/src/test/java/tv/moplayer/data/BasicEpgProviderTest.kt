package tv.moplayer.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import tv.moplayer.data.repository.BasicEpgProvider
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ServerType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class BasicEpgProviderTest {

    @Test
    fun sync_loads_xmltv_and_returns_current_next() = runBlocking {
        val server = MockWebServer()
        val fmt = SimpleDateFormat("yyyyMMddHHmmss Z", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val now = System.currentTimeMillis()
        val nowStart = fmt.format(Date(now - 30 * 60_000))
        val nowStop = fmt.format(Date(now + 30 * 60_000))
        val nextStart = fmt.format(Date(now + 30 * 60_000))
        val nextStop = fmt.format(Date(now + 90 * 60_000))
        val xml = """
            <tv>
              <programme channel="news.one" start="$nowStart" stop="$nowStop">
                <title>Current News</title>
                <desc>Current segment</desc>
              </programme>
              <programme channel="news.one" start="$nextStart" stop="$nextStop">
                <title>Next News</title>
                <desc>Next segment</desc>
              </programme>
            </tv>
        """.trimIndent()
        server.enqueue(MockResponse().setResponseCode(200).setBody(xml))
        server.start()
        try {
            val provider = BasicEpgProvider()
            val profile = ServerProfile(
                id = UUID.randomUUID().toString(),
                name = "EPG",
                type = ServerType.M3U_URL,
                baseUrl = "https://example.com/list.m3u8",
                externalEpgUrl = server.url("/epg.xml").toString()
            )
            val syncResult = provider.sync(profile)
            assertEquals(true, syncResult.isSuccess)

            val pair = provider.currentAndNext("news.one").first()
            assertNotNull(pair.first ?: pair.second)
        } finally {
            server.shutdown()
        }
    }
}
