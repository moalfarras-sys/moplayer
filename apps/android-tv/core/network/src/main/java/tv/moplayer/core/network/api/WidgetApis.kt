package tv.moplayer.core.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class WeatherApiResponse(
    val location: LocationDto,
    val current: CurrentWeatherDto
)

data class LocationDto(val name: String)
data class CurrentWeatherDto(val temp_c: Double, val condition: ConditionDto)
data class ConditionDto(val text: String)

interface WeatherApiService {
    @GET("current.json")
    suspend fun current(
        @Query("key") apiKey: String,
        @Query("q") city: String
    ): WeatherApiResponse
}

data class FootballResponse(val response: List<FixtureDto>)
data class FixtureDto(val teams: TeamsDto, val goals: GoalsDto)
data class TeamsDto(val home: TeamDto, val away: TeamDto)
data class TeamDto(val name: String)
data class GoalsDto(val home: Int?, val away: Int?)

interface FootballApiService {
    @GET("fixtures")
    suspend fun liveFixtures(
        @Header("x-apisports-key") apiKey: String,
        @Query("live") live: String = "all",
        @Query("league") leagueId: Int
    ): FootballResponse
}