package tv.moplayer.data

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.flow.first
import tv.moplayer.data.repository.RuleBasedRecommendationEngine
import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.ContentType

class RecommendationEngineTest {
    @Test
    fun seedByHistory_filters_by_group() {
        val engine = RuleBasedRecommendationEngine()

        val history = listOf(
            ContentItem("h1", "History A", ContentType.MOVIE, group = "Action"),
            ContentItem("h2", "History B", ContentType.MOVIE, group = "Drama")
        )
        val catalog = listOf(
            ContentItem("c1", "Movie 1", ContentType.MOVIE, group = "Action"),
            ContentItem("c2", "Movie 2", ContentType.MOVIE, group = "Comedy"),
            ContentItem("c3", "Movie 3", ContentType.MOVIE, group = "Drama")
        )

        engine.seedByHistory(history, catalog)
        val rec = kotlinx.coroutines.runBlocking { engine.recommend(10).first() }

        assertEquals(2, rec.size)
    }
}
