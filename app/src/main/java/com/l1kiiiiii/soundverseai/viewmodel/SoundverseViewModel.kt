package com.l1kiiiiii.soundverseai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.l1kiiiiii.soundverseai.playback.PlaybackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Data class representing a single chat bubble entry in the conversation list.
 *
 * @param id          Unique identifier.
 * @param text        Message body text.
 * @param isAssistant True = assistant bubble (avatar shown), false = user bubble.
 * @param showTryNow  If true, a "Try Now" gradient CTA button is shown beneath the text.
 */
data class ChatMessage(
    val id: Int,
    val text: String,
    val isAssistant: Boolean,
    val showTryNow: Boolean = false
)

/**
 * SoundverseViewModel — central state engine.
 *
 * Owns:
 *  - [chatMessages]          : Immutable list of chat bubbles (from Create - Blank State design).
 *  - [isPlaying]             : Mirror of PlaybackManager's playback state.
 *  - [showForegroundDialog]  : Controls visibility of the foreground notification alert dialog.
 *  - [inputText]             : Current value of the bottom chat text field.
 */
class SoundverseViewModel(application: Application) : AndroidViewModel(application) {

    private val playbackManager = PlaybackManager.getInstance(application)

    // ── Chat history ─────────────────────────────────────────────────────────
    private val _chatMessages = MutableStateFlow(buildInitialChatHistory())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    // ── Playback ─────────────────────────────────────────────────────────────
    val isPlaying: StateFlow<Boolean> = playbackManager.isPlaying

    // ── Foreground notification dialog ───────────────────────────────────────
    private val _showForegroundDialog = MutableStateFlow(false)
    val showForegroundDialog: StateFlow<Boolean> = _showForegroundDialog.asStateFlow()

    // ── Input field ──────────────────────────────────────────────────────────
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    // ── Playback controls ─────────────────────────────────────────────────────
    fun play()           = playbackManager.play()
    fun pause()          = playbackManager.pause()
    fun togglePlayback() = playbackManager.togglePlayback()

    // ── Dialog controls ───────────────────────────────────────────────────────
    fun showForegroundNotificationDialog()   { _showForegroundDialog.value = true  }
    fun dismissForegroundNotificationDialog() { _showForegroundDialog.value = false }

    // ── Input handling ────────────────────────────────────────────────────────
    fun onInputChanged(text: String) { _inputText.value = text }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return
        val newMessage = ChatMessage(
            id = _chatMessages.value.size + 1,
            text = text,
            isAssistant = false
        )
        _chatMessages.value = _chatMessages.value + newMessage
        _inputText.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.release()
    }

    // ── Initial chat data from "Create - Blank State.jpg" ────────────────────
    private fun buildInitialChatHistory(): List<ChatMessage> = listOf(
        ChatMessage(
            id = 1,
            text = "Hello there! I'm Soundverse Assistant, your music AI co-pilot. Let's get started with your project.",
            isAssistant = true,
            showTryNow = false
        ),
        ChatMessage(
            id = 2,
            text = "You can write a prompt in the text box below. Try mentioning instruments, scene, story, genre, scale etc to generate an audio clip. More things you mention, the better the output will be.",
            isAssistant = true,
            showTryNow = false
        ),
        ChatMessage(
            id = 3,
            text = "For e.g. you can write \"Compose a hauntingly beautiful piano solo that captures the essence of melancholy and nostalgia. The melody should evoke a sense of longing and introspection, while the harmonies add depth and emotion to the piece.\"",
            isAssistant = true,
            showTryNow = true
        )
    )
}
