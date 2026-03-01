package tv.moplayer.core.player

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerController(private val player: ExoPlayer) {
    fun play(url: String) {
        player.setMediaItem(MediaItem.fromUri(url))
        player.prepare()
        player.playWhenReady = true
    }

    fun pause() = player.pause()
    fun stop() = player.stop()
    fun seek(positionMs: Long) = player.seekTo(positionMs)
}