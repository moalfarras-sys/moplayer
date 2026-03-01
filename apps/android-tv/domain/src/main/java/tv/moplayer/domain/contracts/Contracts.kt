package tv.moplayer.domain.contracts

import kotlinx.coroutines.flow.Flow
import tv.moplayer.domain.model.ContentItem
import tv.moplayer.domain.model.EpgProgram
import tv.moplayer.domain.model.LiveChannel
import tv.moplayer.domain.model.ServerProfile
import tv.moplayer.domain.model.ThemeConfig
import tv.moplayer.domain.model.WidgetConfig

interface PlaylistIngestor {
    suspend fun ingest(server: ServerProfile): Result<Unit>
    fun liveChannels(): Flow<List<LiveChannel>>
    fun movies(): Flow<List<ContentItem>>
    fun series(): Flow<List<ContentItem>>
}

interface XtreamClient {
    suspend fun validate(server: ServerProfile): Result<Unit>
    suspend fun sync(server: ServerProfile): Result<Unit>
}

interface EpgProvider {
    suspend fun sync(server: ServerProfile): Result<Unit>
    fun currentAndNext(channelId: String): Flow<Pair<EpgProgram?, EpgProgram?>>
}

interface RecommendationEngine {
    fun recommend(limit: Int = 20): Flow<List<ContentItem>>
}

interface PlaybackGateway {
    suspend fun play(streamUrl: String, title: String)
    suspend fun pause()
    suspend fun stop()
    suspend fun seekTo(positionMs: Long)
}

interface SupabaseSyncGateway {
    suspend fun syncServer(server: ServerProfile): Result<Unit>
    fun themeUpdates(): Flow<ThemeConfig>
    fun widgetUpdates(): Flow<WidgetConfig>
    fun featureFlagUpdates(): Flow<Map<String, Boolean>>
}