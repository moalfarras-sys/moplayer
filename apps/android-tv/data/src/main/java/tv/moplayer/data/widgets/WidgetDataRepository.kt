package tv.moplayer.data.widgets

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class WeatherSummary(val city: String, val tempC: Double, val condition: String)
data class MatchSummary(val title: String)

class WidgetDataRepository {
    private val weather = MutableStateFlow(WeatherSummary("Berlin", 16.0, "Clear"))
    private val matches = MutableStateFlow(listOf(MatchSummary("EPL: MCI 2-1 ARS"), MatchSummary("LaLiga: RMA 1-0 BAR")))

    fun weather(): Flow<WeatherSummary> = weather
    fun matches(): Flow<List<MatchSummary>> = matches

    suspend fun refresh() {
        // Placeholder for real API calls through WeatherApiService / FootballApiService.
    }
}