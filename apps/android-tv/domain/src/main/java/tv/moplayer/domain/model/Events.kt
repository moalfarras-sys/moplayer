package tv.moplayer.domain.model

sealed interface AppEvent

data class FocusEvent(
    val fromId: String,
    val toId: String,
    val timestamp: Long = System.currentTimeMillis()
) : AppEvent

data class PlaybackEvent(
    val contentId: String,
    val state: PlaybackState,
    val positionMs: Long,
    val timestamp: Long = System.currentTimeMillis()
) : AppEvent

data class SyncEvent(
    val source: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
) : AppEvent

data class FeatureFlagEvent(
    val key: String,
    val enabled: Boolean,
    val timestamp: Long = System.currentTimeMillis()
) : AppEvent

enum class PlaybackState { IDLE, BUFFERING, READY, PLAYING, PAUSED, ENDED, ERROR }