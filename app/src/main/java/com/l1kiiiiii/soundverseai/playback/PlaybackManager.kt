package com.l1kiiiiii.soundverseai.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PlaybackManager — Singleton ExoPlayer wrapper.
 *
 * Manages a single ExoPlayer instance for the entire app lifecycle.
 * Streams a dummy audio URL and exposes [isPlaying] as a stable StateFlow
 * so composables can observe playback state reactively.
 */
class PlaybackManager private constructor(context: Context) {

    companion object {
        private const val DUMMY_AUDIO_URL =
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"

        @Volatile
        private var INSTANCE: PlaybackManager? = null

        fun getInstance(context: Context): PlaybackManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlaybackManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .build()
        .apply {
            val mediaItem = MediaItem.fromUri(DUMMY_AUDIO_URL)
            setMediaItem(mediaItem)
            prepare()
        }

    /** Begin / resume playback of the dummy stream. */
    fun play() {
        player.play()
        _isPlaying.value = true
    }

    /** Pause playback. */
    fun pause() {
        player.pause()
        _isPlaying.value = false
    }

    /**
     * Release all resources held by ExoPlayer.
     * Call this from the Activity's onDestroy() or when the ViewModel is cleared.
     */
    fun release() {
        player.release()
        _isPlaying.value = false
        INSTANCE = null
    }
}
