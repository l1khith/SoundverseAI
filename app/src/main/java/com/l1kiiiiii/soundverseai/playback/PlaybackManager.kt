package com.l1kiiiiii.soundverseai.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PlaybackManager — Thread-safe singleton ExoPlayer wrapper.
 *
 * Key improvements over the naive implementation:
 *  1. Uses a stable Google-hosted HTTPS asset (no redirect chains, no cleartext issues).
 *  2. Tracks real hardware playback state via [Player.Listener.onIsPlayingChanged] instead
 *     of manually flipping a boolean — this correctly handles buffering, audio focus loss,
 *     and headphone disconnection without extra code.
 *  3. Exposes [togglePlayback] for ergonomic use from the ViewModel.
 */
class PlaybackManager private constructor(context: Context) {

    companion object {
        /**
         * High-stability HTTPS test asset hosted by the ExoPlayer/Media3 team directly.
         * Streams reliably on physical devices, emulators, and restricted networks.
         */
        private const val STREAM_URL =
            "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"

        @Volatile
        private var INSTANCE: PlaybackManager? = null

        fun getInstance(context: Context): PlaybackManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PlaybackManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Backed by real hardware state — updated by Player.Listener, not manual assignment.
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val player: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .build()
        .apply {
            // Wire the stable stream URL
            setMediaItem(MediaItem.fromUri(STREAM_URL))
            prepare()

            // Track actual playback hardware state reactively.
            // onIsPlayingChanged fires on play/pause/buffering/audio-focus changes.
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlayingNow: Boolean) {
                    _isPlaying.value = isPlayingNow
                }
            })
        }

    /** Begin or resume playback. */
    fun play() {
        player.play()
    }

    /** Pause playback. */
    fun pause() {
        player.pause()
    }

    /**
     * Toggle between play and pause — convenient for a single play/pause button.
     * State is authoritative from the hardware listener, not guessed locally.
     */
    fun togglePlayback() {
        if (player.isPlaying) player.pause() else player.play()
    }

    /**
     * Release all ExoPlayer resources.
     * Called from [SoundverseViewModel.onCleared] when the ViewModel is destroyed.
     */
    fun release() {
        player.release()
        _isPlaying.value = false
        INSTANCE = null
    }
}
