package com.l1kiiiiii.soundverseai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundBottom
import com.l1kiiiiii.soundverseai.ui.theme.BackgroundTop
import com.l1kiiiiii.soundverseai.ui.theme.DividerColor
import com.l1kiiiiii.soundverseai.ui.theme.GradientEnd
import com.l1kiiiiii.soundverseai.ui.theme.GradientStart
import com.l1kiiiiii.soundverseai.ui.theme.PrimaryAccent
import com.l1kiiiiii.soundverseai.ui.theme.TextMuted
import com.l1kiiiiii.soundverseai.ui.theme.TextPrimary

/**
 * MockProfileDrawerContent
 *
 * The side profile drawer that is revealed when the user swipes the
 * foreground panel to the right (or taps the hamburger menu icon).
 *
 * Layout:
 *  ┌──────────────────────────────────┐
 *  │  [Avatar]  Soundverse User       │
 *  │            user@soundverse.ai    │
 *  │  ─────────────────────────────   │
 *  │  🎵 My Projects                  │
 *  │  🔭 Explore                      │
 *  │  ⚙ Settings                     │
 *  │  ❓ Help & Support               │
 *  │  ─────────────────────────────   │
 *  │  🚪 Sign Out                     │
 *  └──────────────────────────────────┘
 *
 * @param modifier Optional [Modifier] — typically [Modifier.fillMaxWidth(0.75f)].
 */
@Composable
fun MockProfileDrawerContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.0f to BackgroundTop,
                        0.5f to BackgroundTop,
                        1.0f to BackgroundBottom
                    )
                )
            )
            .padding(top = 64.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
    ) {
        // ── User Identity Block ──────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar circle with gradient ring
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Inner dark circle creates a ring effect
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BackgroundTop),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "S",
                        color      = TextPrimary,
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text       = "Soundverse User",
                    color      = TextPrimary,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = "user@soundverse.ai",
                    color    = TextMuted,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Subscription badge
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryAccent.copy(alpha = 0.25f),
                            GradientEnd.copy(alpha = 0.15f)
                        )
                    )
                )
                .padding(horizontal = 12.dp, vertical = 5.dp)
        ) {
            Text(
                text       = "✦  Free Plan",
                color      = PrimaryAccent,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(20.dp))

        // ── Primary Navigation Items ─────────────────────────────────────────
        DrawerNavItem(leadingIcon = "🎵", label = "My Projects",   onClick = {})
        DrawerNavItem(leadingIcon = "🔭", label = "Explore",       onClick = {})
        DrawerNavItem(leadingIcon = "🎨", label = "Templates",     onClick = {})
        DrawerNavItem(leadingIcon = "📊", label = "Analytics",     onClick = {})

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        DrawerNavItem(leadingIcon = "⚙",  label = "Settings",      onClick = {})
        DrawerNavItem(leadingIcon = "❓", label = "Help & Support", onClick = {})

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // ── Upgrade CTA ──────────────────────────────────────────────────────
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication        = null
                ) { /* upgrade flow */ }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text       = "✦  Upgrade to Pro",
                color      = TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Sign Out ─────────────────────────────────────────────────────────
        DrawerNavItem(leadingIcon = "🚪", label = "Sign Out", labelColor = TextMuted, onClick = {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// DrawerNavItem
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DrawerNavItem(
    leadingIcon: String,
    label: String,
    labelColor: androidx.compose.ui.graphics.Color = TextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
            .padding(vertical = 11.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text     = leadingIcon,
            fontSize = 18.sp,
            modifier = Modifier.width(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text       = label,
            color      = labelColor,
            fontSize   = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
