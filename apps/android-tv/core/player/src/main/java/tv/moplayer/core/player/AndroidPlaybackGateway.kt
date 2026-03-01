package tv.moplayer.core.player

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import tv.moplayer.domain.contracts.PlaybackGateway

class AndroidPlaybackGateway(
    context: Context
) : PlaybackGateway {
    private val player: ExoPlayer = ExoPlayer.Builder(context).build().apply {
        repeatMode = Player.REPEAT_MODE_OFF
    }

    override suspend fun play(streamUrl: String, title: String) {
        player.setMediaItem(MediaItem.Builder().setUri(streamUrl).setMediaId(title).build())
        player.prepare()
        player.playWhenReady = true
    }

    override suspend fun pause() {
        player.pause()
    }

    override suspend fun stop() {
        player.stop()
        player.clearMediaItems()
    }

    override suspend fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    fun rawPlayer(): ExoPlayer = player

    fun release() {
        player.release()
    }
}

object ExternalPlayerLauncher {
    private const val VLC = "org.videolan.vlc"
    private const val MX = "com.mxtech.videoplayer.ad"

    fun launch(context: Context, streamUrl: String, packageName: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(streamUrl), "video/*")
            setPackage(packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
            true
        } else {
            false
        }
    }

    fun launchVlc(context: Context, streamUrl: String): Boolean = launch(context, streamUrl, VLC)
    fun launchMx(context: Context, streamUrl: String): Boolean = launch(context, streamUrl, MX)
}