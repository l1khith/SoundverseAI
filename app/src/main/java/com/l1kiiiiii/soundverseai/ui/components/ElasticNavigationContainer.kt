package com.l1kiiiiii.soundverseai.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundTop
import com.l1kiiiiii.soundverseai.ui.theme.CardSurface
import com.l1kiiiiii.soundverseai.ui.theme.GradientEnd
import com.l1kiiiiii.soundverseai.ui.theme.GradientStart
import com.l1kiiiiii.soundverseai.ui.theme.TextMuted
import com.l1kiiiiii.soundverseai.ui.theme.TextPrimary
import kotlin.math.roundToInt

/**
 * ElasticNavigationContainer
 *
 * Implements a fluid, spring-physics side profile drawer that responds to
 * horizontal drag gestures:
 *
 * - Right drag > 35% screen width  → slides chat view off to the right,
 *   revealing the mock profile drawer beneath.
 * - Left drag or rejected drag     → springs back to origin with a bouncy damper.
 *
 * Uses [graphicsLayer] to non-linearly scale the chat content from 1.0f → 0.92f
 * as the drawer opens, mimicking the YouTube Shorts / iOS home-screen feel.
 */
@Composable
fun ElasticNavigationContainer(
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp * 3f // rough px approximation for fraction
    val drawerRevealFraction = 0.35f

    // Track raw drag offset
    var rawOffset by remember { mutableFloatStateOf(0f) }
    // Whether the drawer is fully open
    var isDrawerOpen by remember { mutableStateOf(false) }

    // Animated offset — springs to 0 (closed) or full screen width (open)
    val animatedOffset by animateFloatAsState(
        targetValue = if (isDrawerOpen) screenWidthPx else rawOffset.coerceAtLeast(0f),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "drawer_offset"
    )

    // Non-linear scale: 1.0 → 0.92 as drawer opens
    val contentScale by animateFloatAsState(
        targetValue = if (isDrawerOpen) 0.92f else {
            val fraction = (rawOffset / screenWidthPx).coerceIn(0f, 1f)
            1f - fraction * 0.08f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "content_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundTop)
    ) {
        // ── Backdrop: Mock Profile Drawer ─────────────────────────────────
        ProfileDrawerContent(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .align(Alignment.CenterStart)
        )

        // ── Foreground: Chat Content ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffset
                    scaleX = contentScale
                    scaleY = contentScale
                    // Clip to avoid content leaking outside when scaled
                    clip = false
                }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val screenWidthFraction = rawOffset / screenWidthPx
                            if (screenWidthFraction > drawerRevealFraction) {
                                isDrawerOpen = true
                            } else {
                                isDrawerOpen = false
                                rawOffset = 0f
                            }
                        },
                        onDragCancel = {
                            isDrawerOpen = false
                            rawOffset = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (!isDrawerOpen) {
                                rawOffset = (rawOffset + dragAmount).coerceAtLeast(0f)
                            } else {
                                // Left swipe while open → close
                                if (dragAmount < 0) {
                                    rawOffset = 0f
                                    isDrawerOpen = false
                                }
                            }
                        }
                    )
                }
        ) {
            content()
        }
    }
}

/**
 * Mock profile side-drawer revealed when the user swipes right past 35% threshold.
 */
@Composable
private fun ProfileDrawerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        com.l1kiiiiii.soundverseai.ui.theme.BackgroundTop,
                        com.l1kiiiiii.soundverseai.ui.theme.BackgroundBottom
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Avatar circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "S",
                color = TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Soundverse User",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "user@soundverse.ai",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        DrawerMenuItem(label = "My Projects")
        DrawerMenuItem(label = "Explore")
        DrawerMenuItem(label = "Settings")
        DrawerMenuItem(label = "Help & Support")
        DrawerMenuItem(label = "Sign Out")
    }
}

@Composable
private fun DrawerMenuItem(label: String) {
    Text(
        text = label,
        color = TextPrimary,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}
