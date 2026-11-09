package com.streamplayer.app.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.datasource.DefaultDataSource
import com.streamplayer.app.data.StreamSource

class PlayerManager(private val context: Context) {
    var exoPlayer: ExoPlayer? = null
        private set

    private var streamSources: List<StreamSource> = emptyList()
    private var currentStreamIndex = 0
    var onStreamFallback: ((String, Int) -> Unit)? = null

    init {
        initPlayer()
    }

    private fun initPlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    handleStreamFailure()
                }
            })
        }
    }

    fun playStreams(sources: List<StreamSource>) {
        streamSources = sources.sortedBy { it.priority }
        currentStreamIndex = 0
        playCurrentStream()
    }

    fun playSingleUrl(url: String) {
        streamSources = listOf(StreamSource("Direct", url, 1))
        currentStreamIndex = 0
        playCurrentStream()
    }

    private fun playCurrentStream() {
        if (streamSources.isEmpty()) return
        val source = streamSources[currentStreamIndex]
        onStreamFallback?.invoke(source.source, source.priority)

        exoPlayer?.let { player ->
            player.stop()
            val mediaItem = MediaItem.fromUri(Uri.parse(source.url))
            player.setMediaItem(mediaItem)
            player.prepare()
            player.playWhenReady = true
        }
    }

    fun handleStreamFailure() {
        if (currentStreamIndex < streamSources.size - 1) {
            currentStreamIndex++
            playCurrentStream()
        }
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
