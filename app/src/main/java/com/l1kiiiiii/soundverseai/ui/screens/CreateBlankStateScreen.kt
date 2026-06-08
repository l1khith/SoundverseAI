package com.l1kiiiiii.soundverseai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundBottom
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundTop
import com.l1kiiiiii.soundverseai.ui.theme.BottomBarSurface
import com.l1kiiiiii.soundverseai.ui.theme.CardSurface
import com.l1kiiiiii.soundverseai.ui.theme.GradientEnd
import com.l1kiiiiii.soundverseai.ui.theme.GradientStart
import com.l1kiiiiii.soundverseai.ui.theme.InputSurface
import com.l1kiiiiii.soundverseai.ui.theme.PrimaryAccent
import com.l1kiiiiii.soundverseai.ui.theme.StrokeSubtle
import com.l1kiiiiii.soundverseai.ui.theme.TextMuted
import com.l1kiiiiii.soundverseai.ui.theme.TextPrimary
import com.l1kiiiiii.soundverseai.viewmodel.ChatMessage
import com.l1kiiiiii.soundverseai.viewmodel.SoundverseViewModel

/**
 * CreateBlankStateScreen — pixel-faithful implementation of "Create - Blank State.jpg".
 *
 * Layout hierarchy:
 *  ┌─────────────────────────────────────────────┐
 *  │  ElasticNavigationContainer (swipe drawer)  │
 *  │  ┌──────────────────────────────────────┐   │
 *  │  │ TopBar: ≡  PULSE PLAYGROUND ▼  🔔   │   │
 *  │  ├──────────────────────────────────────┤   │
 *  │  │ LazyColumn: chat bubbles             │   │
 *  │  ├──────────────────────────────────────┤   │
 *  │  │ Bottom input tray                    │   │
 *  │  └──────────────────────────────────────┘   │
 *  └─────────────────────────────────────────────┘
 */
@Composable
fun CreateBlankStateScreen(
    viewModel: SoundverseViewModel,
    onProfileMenuClick: () -> Unit,
    onTryNowClicked: () -> Unit,
    onNotificationClicked: () -> Unit
) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    val inputText    by viewModel.inputText.collectAsState()
    val lazyListState = rememberLazyListState()

    // Auto-scroll to bottom on new messages
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            lazyListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BackgroundTop,
                        0.6f to BackgroundBottom,
                        1.0f to BackgroundBottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // ── Top Bar ──────────────────────────────────────────────
            ChatTopBar(
                onProfileMenuClick    = onProfileMenuClick,
                onNotificationClicked = onNotificationClicked
            )

            // ── Chat Messages ─────────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = lazyListState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 24.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = chatMessages,
                    key   = { it.id }
                ) { message ->
                    AnimatedVisibility(
                        visible = true,
                        enter   = fadeIn(tween(300)) + slideInVertically(
                            animationSpec = tween(300),
                            initialOffsetY = { it / 2 }
                        )
                    ) {
                        ChatBubble(
                            message       = message,
                            onTryNowClick = onTryNowClicked
                        )
                    }
                }
            }

            // ── Bottom Input Tray ─────────────────────────────────────
            ChatInputTray(
                value         = inputText,
                onValueChange = viewModel::onInputChanged,
                onSend        = viewModel::sendMessage,
                modifier      = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .navigationBarsPadding()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Top Bar
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChatTopBar(
    onProfileMenuClick: () -> Unit,
    onNotificationClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hamburger / menu icon — tapping opens the profile drawer
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardSurface.copy(alpha = 0.6f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null,
                    onClick           = onProfileMenuClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement    = Arrangement.spacedBy(4.dp),
                horizontalAlignment    = Alignment.CenterHorizontally,
                modifier               = Modifier.padding(8.dp)
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(18.dp)
                            .height(2.dp)
                            .background(TextPrimary, RoundedCornerShape(1.dp))
                    )
                }
            }
        }

        // Centered title
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text       = "PULSE PLAYGROUND",
                    color      = TextPrimary,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector        = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint               = TextPrimary,
                    modifier           = Modifier.size(18.dp)
                )
            }
        }

        // Notification bell
        IconButton(onClick = onNotificationClicked) {
            Icon(
                imageVector        = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint               = TextPrimary,
                modifier           = Modifier.size(24.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Chat Bubble
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onTryNowClick: () -> Unit
) {
    if (message.isAssistant) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            // Avatar — glowing circle with "S" monogram
            AssistantAvatar()
            Spacer(Modifier.width(10.dp))

            Column {
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart    = 4.dp,
                                topEnd      = 14.dp,
                                bottomEnd   = 14.dp,
                                bottomStart = 14.dp
                            )
                        )
                        .background(CardSurface)
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Text(
                        text       = message.text,
                        color      = TextPrimary,
                        fontSize   = 14.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Normal
                    )
                }

                if (message.showTryNow) {
                    Spacer(Modifier.height(10.dp))
                    TryNowButton(onClick = onTryNowClick)
                }
            }
        }
    } else {
        // User bubble — right aligned
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart    = 14.dp,
                            topEnd      = 4.dp,
                            bottomEnd   = 14.dp,
                            bottomStart = 14.dp
                        )
                    )
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text       = message.text,
                    color      = TextPrimary,
                    fontSize   = 14.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
private fun AssistantAvatar() {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(
                Brush.sweepGradient(
                    colors = listOf(
                        GradientStart,
                        PrimaryAccent,
                        GradientEnd,
                        GradientStart
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Inner dark circle to create ring effect
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(BackgroundBottom),
            contentAlignment = Alignment.Center
        ) {
            // Dot grid pattern representing the Soundverse logo dots
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        Brush.sweepGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        ),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun TryNowButton(onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "try_now_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(50.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                pressed = true
                onClick()
            }
            .padding(horizontal = 28.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = "Try Now",
            color      = TextPrimary,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Bottom Chat Input Tray — matches "layer_2", "group_323", "rectangle_52" assets
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ChatInputTray(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        BottomBarSurface
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(InputSurface)
                .border(
                    width = 1.dp,
                    color = StrokeSubtle,
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // layer_2 — attachment / image icon
            AttachmentIconButton(
                icon    = "🖼",
                contentDescription = "Attach media",
                onClick = {}
            )

            Spacer(Modifier.width(4.dp))

            // Text field
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text  = "What would you like to create?",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
                BasicTextField(
                    value         = value,
                    onValueChange = onValueChange,
                    textStyle     = TextStyle(
                        color     = TextPrimary,
                        fontSize  = 13.sp
                    ),
                    cursorBrush  = SolidColor(PrimaryAccent),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() }),
                    singleLine      = true,
                    modifier        = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.width(4.dp))

            // group_323 — magic wand / enhance icon
            AttachmentIconButton(icon = "✨", contentDescription = "Enhance", onClick = {})

            // Plus icon
            AttachmentIconButton(icon = "＋", contentDescription = "Add", onClick = {})

            // More options
            AttachmentIconButton(icon = "···", contentDescription = "More options", onClick = {})

            Spacer(Modifier.width(6.dp))

            // rectangle_52 — Purple send button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
                    .clickable(onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = "▶",
                    color    = TextPrimary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun AttachmentIconButton(
    icon: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Text(
        text     = icon,
        color    = TextMuted,
        fontSize = 16.sp,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(4.dp)
    )
}
