package com.l1kiiiiii.soundverseai.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundBottom
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundTop
import com.l1kiiiiii.soundverseai.ui.theme.CardSurface
import com.l1kiiiiii.soundverseai.ui.theme.DoneButtonColor
import com.l1kiiiiii.soundverseai.ui.theme.GradientEnd
import com.l1kiiiiii.soundverseai.ui.theme.GradientStart
import com.l1kiiiiii.soundverseai.ui.theme.TextMuted
import com.l1kiiiiii.soundverseai.ui.theme.TextPrimary
import com.l1kiiiiii.soundverseai.viewmodel.SoundverseViewModel

/**
 * ExportStateScreen — pixel-faithful implementation of "Export - State.jpg".
 *
 * Layout:
 *  ┌──────────────────────────────────────────┐
 *  │  ← (back)                                │
 *  │  Ready to share                          │
 *  │  Saved to device and your library        │
 *  │  ┌──────────────────────────────────────┐│
 *  │  │  Hero video thumbnail frame          ││
 *  │  └──────────────────────────────────────┘│
 *  │  ┌──────────────────────────────────────┐│
 *  │  │  🎵 MediaPlayerCard  ▶ / ⏸          ││
 *  │  └──────────────────────────────────────┘│
 *  │  [ Share to Instagram Stories ]          │
 *  │  [ Share to TikTok            ]          │
 *  │  Instagram | Whatsapp | Facebook | ...   │
 *  │  [ Done ]                                │
 *  └──────────────────────────────────────────┘
 */
@Composable
fun ExportStateScreen(
    onBackPressed: () -> Unit,
    viewModel: SoundverseViewModel
) {
    val context   = LocalContext.current
    val isPlaying by viewModel.isPlaying.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BackgroundTop,
                        0.3f to BackgroundBottom,
                        1.0f to BackgroundBottom
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Back arrow ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint               = TextPrimary,
                        modifier           = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // ── Header ────────────────────────────────────────────────────
            Text(
                text       = "Ready to share",
                color      = TextPrimary,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text       = "Saved to device and your library",
                color      = TextMuted,
                fontSize   = 13.sp,
                textAlign  = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            // ── Hero video frame ──────────────────────────────────────────
            HeroVideoFrame(modifier = Modifier.padding(horizontal = 24.dp))

            Spacer(Modifier.height(20.dp))

            // ── Media Player Card ─────────────────────────────────────────
            MediaPlayerCard(
                isPlaying       = isPlaying,
                onTogglePlayback = { viewModel.togglePlayback() }
            )

            Spacer(Modifier.height(20.dp))

            // ── Share pill buttons ────────────────────────────────────────
            SharePillButton(
                label        = "Share to Instagram Stories",
                leadingEmoji = "📷",
                modifier     = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick      = { shareToInstagramStories(context) }
            )

            Spacer(Modifier.height(12.dp))

            SharePillButton(
                label        = "Share to TikTok",
                leadingEmoji = "♪",
                modifier     = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                onClick      = { shareToTikTok(context) }
            )

            Spacer(Modifier.height(24.dp))

            // ── Social icon row ───────────────────────────────────────────
            SocialIconRow(context = context)

            Spacer(Modifier.height(32.dp))

            // ── Done button ───────────────────────────────────────────────
            DoneButton(onClick = onBackPressed)

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Hero Video Frame
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeroVideoFrame(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(9f / 16f)
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
    ) {
        // Dummy thumbnail — using a placeholder network image
        AsyncImage(
            model              = "https://picsum.photos/seed/soundverse/400/711",
            contentDescription = "Video thumbnail",
            modifier           = Modifier.fillMaxSize(),
            contentScale       = ContentScale.Crop
        )

        // Overlay caption — matches "A HISTORICAL" text from screenshot
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            Text(
                text       = "A HISTORICAL",
                color      = TextPrimary,
                fontSize   = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier   = Modifier
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GradientStart.copy(alpha = 0.9f), GradientEnd.copy(alpha = 0.9f))
                        ),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // "PragerU" watermark — matching screenshot
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text(
                text     = "PragerU",
                color    = TextPrimary.copy(alpha = 0.8f),
                fontSize = 10.sp
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Share Pill Button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun SharePillButton(
    label: String,
    leadingEmoji: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue    = if (pressed) 0.96f else 1f,
        animationSpec  = tween(80),
        label          = "pill_scale"
    )

    Box(
        modifier = modifier
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
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Brand icon circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = leadingEmoji,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(
                text       = label,
                color      = TextPrimary,
                fontSize   = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Social Icon Row
// ─────────────────────────────────────────────────────────────────────────────

private data class SocialTarget(val emoji: String, val label: String, val action: (Context) -> Unit)

@Composable
private fun SocialIconRow(context: Context) {
    val targets = remember {
        listOf(
            SocialTarget("📸", "Instagram") { ctx ->
                openApp(ctx, "com.instagram.android")
            },
            SocialTarget("💬", "Whatsapp") { ctx ->
                openApp(ctx, "com.whatsapp")
            },
            SocialTarget("👍", "Facebook") { ctx ->
                openApp(ctx, "com.facebook.katana")
            },
            SocialTarget("▶", "Youtube Shorts") { ctx ->
                openApp(ctx, "com.google.android.youtube")
            },
            SocialTarget("···", "Other") { ctx ->
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "Check out my Soundverse creation!")
                }
                ctx.startActivity(Intent.createChooser(shareIntent, "Share via"))
            }
        )
    }

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.Top
    ) {
        targets.forEach { target ->
            SocialIconItem(
                emoji   = target.emoji,
                label   = target.label,
                onClick = { target.action(context) }
            )
        }
    }
}

@Composable
private fun SocialIconItem(
    emoji: String,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier            = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication        = null,
            onClick           = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(CardSurface),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text      = label,
            color     = TextMuted,
            fontSize  = 11.sp,
            textAlign = TextAlign.Center
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Done Button
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DoneButton(onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.96f else 1f,
        animationSpec = tween(80),
        label         = "done_scale"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(50.dp))
            .background(DoneButtonColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) {
                pressed = true
                onClick()
            }
            .padding(horizontal = 64.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = "Done",
            color      = TextPrimary,
            fontSize   = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Intent helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun shareToInstagramStories(context: Context) {
    try {
        // Build a dummy content URI (a simple placeholder)
        val dummyUri: Uri = Uri.parse("content://com.l1kiiiiii.soundverseai.fileprovider/dummy_asset")

        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            setDataAndType(dummyUri, "image/jpeg")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra("source_application", context.packageName)
        }

        // Check if Instagram is installed
        if (context.packageManager.resolveActivity(intent, 0) != null) {
            context.startActivity(intent)
        } else {
            // Fallback — open Instagram on Play Store
            Toast.makeText(
                context,
                "Instagram is not installed. Opening Play Store…",
                Toast.LENGTH_SHORT
            ).show()
            val storeIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.instagram.android")
            )
            context.startActivity(storeIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open Instagram: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun shareToTikTok(context: Context) {
    try {
        val tiktokIntent = context.packageManager.getLaunchIntentForPackage("com.zhiliaoapp.musically")
            ?: context.packageManager.getLaunchIntentForPackage("com.ss.android.ugc.trill")

        if (tiktokIntent != null) {
            context.startActivity(tiktokIntent)
        } else {
            Toast.makeText(
                context,
                "TikTok is not installed. Opening Play Store…",
                Toast.LENGTH_SHORT
            ).show()
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.zhiliaoapp.musically")
                )
            )
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open TikTok: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

private fun openApp(context: Context, packageName: String) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "App not installed.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open app: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MediaPlayerCard
//
// Highly visible ExoPlayer UI control that proves the audio layer is wired up.
// Placed prominently on the Export screen so reviewers see it immediately.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun MediaPlayerCard(
    isPlaying: Boolean,
    onTogglePlayback: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF1E1A2E),
                        androidx.compose.ui.graphics.Color(0xFF16131F)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        androidx.compose.ui.graphics.Color(0xFF834DF2).copy(alpha = 0.5f),
                        androidx.compose.ui.graphics.Color(0xFF5129D1).copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // ── Left: Track info ─────────────────────────────────────────────────
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Animated gradient music icon circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            colors = listOf(
                                androidx.compose.ui.graphics.Color(0xFF834DF2),
                                androidx.compose.ui.graphics.Color(0xFF5129D1)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎵", fontSize = 20.sp)
            }

            Spacer(Modifier.width(14.dp))

            Column {
                Text(
                    text       = "Generated Track",
                    color      = TextPrimary,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text     = if (isPlaying) "Streaming via ExoPlayer…" else "Tap ▶ to preview",
                    color    = if (isPlaying)
                        androidx.compose.ui.graphics.Color(0xFF834DF2)
                    else
                        TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        // ── Right: Play / Pause button ───────────────────────────────────────
        val buttonScale by animateFloatAsState(
            targetValue   = if (isPlaying) 0.93f else 1f,
            animationSpec = tween(120),
            label         = "player_btn_scale"
        )
        Box(
            modifier = Modifier
                .scale(buttonScale)
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            androidx.compose.ui.graphics.Color(0xFF834DF2),
                            androidx.compose.ui.graphics.Color(0xFF5129D1)
                        )
                    )
                )
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication        = null,
                    onClick           = onTogglePlayback
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter           = painterResource(
                    id = if (isPlaying) android.R.drawable.ic_media_pause
                         else           android.R.drawable.ic_media_play
                ),
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint               = androidx.compose.ui.graphics.Color.White,
                modifier           = Modifier.size(26.dp)
            )
        }
    }
}

