package com.l1kiiiiii.soundverseai

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.l1kiiiiii.soundverseai.navigation.Screen
import com.l1kiiiiii.soundverseai.notification.SoundverseMessagingService
import com.l1kiiiiii.soundverseai.ui.components.MockProfileDrawerContent
import com.l1kiiiiii.soundverseai.ui.screens.CreateBlankStateScreen
import com.l1kiiiiii.soundverseai.ui.screens.ExportStateScreen
import com.l1kiiiiii.soundverseai.ui.theme.SoundverseAITheme
import com.l1kiiiiii.soundverseai.viewmodel.SoundverseViewModel

/**
 * MainActivity — App entry point and navigation orchestrator.
 *
 * Responsibilities:
 *  1. POST_NOTIFICATIONS runtime permission request at startup (Android 13+).
 *  2. SoundverseAppLayout hosts a spring-physics sliding drawer wrapping the NavHost.
 *  3. Foreground FCM interception via BroadcastReceiver → shows AlertDialog.
 *  4. Deep link handling: soundverse://export routes directly to ExportStateScreen.
 *  5. Mock notification trigger via the notification bell on the top bar.
 */
class MainActivity : ComponentActivity() {

    companion object {
        /**
         * Tracks whether the app is currently in the foreground.
         * Accessed by [SoundverseMessagingService] to decide notification routing.
         */
        @Volatile
        var isAppInForeground: Boolean = false
    }

    private val viewModel: SoundverseViewModel by viewModels()

    // ── Permission launcher ───────────────────────────────────────────────────
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> /* Silently record result; no blocking UI needed */ }

    // ── Foreground FCM broadcast receiver ─────────────────────────────────────
    private val foregroundNotificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SoundverseMessagingService.ACTION_FOREGROUND_NOTIFICATION) {
                viewModel.showForegroundNotificationDialog()
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Request notification permission on Android 13+
        requestNotificationPermission()

        // 2. Print FCM registration token to Logcat for test-message targeting.
        //    Search for "FCM_TEST_TOKEN" in the Logcat tab to copy your device token.
        com.google.firebase.messaging.FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    android.util.Log.d("FCM_TEST_TOKEN", "Your Device Token: ${task.result}")
                } else {
                    android.util.Log.w("FCM_TEST_TOKEN", "Failed to fetch token", task.exception)
                }
            }

        setContent {
            SoundverseAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // 2. Foreground dialog state
                    val showDialog by viewModel.showForegroundDialog.collectAsState()

                    if (showDialog) {
                        ForegroundNotificationDialog(
                            onViewClicked = {
                                viewModel.dismissForegroundNotificationDialog()
                                navController.navigate(Screen.ExportState.route) {
                                    launchSingleTop = true
                                }
                            },
                            onDismissClicked = {
                                viewModel.dismissForegroundNotificationDialog()
                            }
                        )
                    }

                    // 3. Handle deep link intent on first launch
                    LaunchedEffect(Unit) {
                        handleIncomingIntent(intent, navController)
                    }

                    // 4. Spring-physics gesture container wrapping the NavHost
                    SoundverseAppLayout(
                        navController = navController,
                        viewModel     = viewModel
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep links that arrive while the activity is already running
        setIntent(intent)
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        isAppInForeground = true

        // Register foreground broadcast receiver
        val filter = IntentFilter(SoundverseMessagingService.ACTION_FOREGROUND_NOTIFICATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(foregroundNotificationReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(foregroundNotificationReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        isAppInForeground = false
        try {
            unregisterReceiver(foregroundNotificationReceiver)
        } catch (_: IllegalArgumentException) { /* not registered */ }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Permission helpers
    // ─────────────────────────────────────────────────────────────────────────

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Intent handling
    // ─────────────────────────────────────────────────────────────────────────

    private fun handleIncomingIntent(intent: Intent?, navController: NavHostController) {
        intent ?: return
        val data = intent.data
        if (data != null && data.scheme == "soundverse" && data.host == "export") {
            // Deep link → go directly to export screen
            navController.navigate(Screen.ExportState.route) {
                launchSingleTop = true
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SoundverseAppLayout
//
// The definitive gesture container. Renders the mock profile drawer as an
// underlay, then slides the foreground NavHost panel over it using spring
// physics. A scrim tap-target closes the drawer when it is open.
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun SoundverseAppLayout(
    navController: NavHostController,
    viewModel: SoundverseViewModel
) {
    val screenWidth  = with(LocalConfiguration.current) { screenWidthDp.dp }
    val maxOffsetPx  = with(LocalDensity.current) { screenWidth.toPx() * 0.75f }

    var isMenuExpanded by remember { mutableStateOf(false) }

    // Spring-physics animated translation for the foreground panel
    val animatedOffset by animateFloatAsState(
        targetValue   = if (isMenuExpanded) maxOffsetPx else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessLow
        ),
        label = "MenuSpringAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090E))
    ) {
        // ── Underlay: Profile Drawer (revealed as foreground slides right) ──
        MockProfileDrawerContent(modifier = Modifier.fillMaxWidth(0.75f))

        // ── Foreground: NavHost panel ────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    translationX = animatedOffset
                    // Non-linear scale-down towards 0.92f as drawer opens
                    val scaleFactor = 1f - (animatedOffset / maxOffsetPx) * 0.08f
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                .pointerInput(isMenuExpanded) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            // Snap decision: stay open if dragged past half, else close
                            isMenuExpanded = animatedOffset > maxOffsetPx / 2
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (isMenuExpanded && dragAmount < -10) {
                                isMenuExpanded = false
                            } else if (!isMenuExpanded && dragAmount > 10) {
                                isMenuExpanded = true
                            }
                        }
                    )
                }
        ) {
            NavHost(
                navController    = navController,
                startDestination = Screen.Chat.route
            ) {
                composable(Screen.Chat.route) {
                    CreateBlankStateScreen(
                        viewModel          = viewModel,
                        onProfileMenuClick = { isMenuExpanded = !isMenuExpanded },
                        onTryNowClicked    = { navController.navigate(Screen.ExportState.route) },
                        onNotificationClicked = { viewModel.showForegroundNotificationDialog() }
                    )
                }

                composable(Screen.ExportState.route) {
                    ExportStateScreen(
                        onBackPressed = { navController.popBackStack() },
                        viewModel     = viewModel
                    )
                }
            }

            // Invisible scrim to close the drawer by tapping outside
            if (isMenuExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication        = null
                        ) {
                            isMenuExpanded = false
                        }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Foreground Notification AlertDialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun ForegroundNotificationDialog(
    onViewClicked: () -> Unit,
    onDismissClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissClicked,
        title = {
            Text(
                text  = "🎵 Your Track is Ready!",
                style = MaterialTheme.typography.titleMedium
            )
        },
        text = {
            Text(
                text  = "Your Soundverse creation has been processed and is ready to share. Tap View to go to the Export screen.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onViewClicked) {
                Text(
                    text  = "View",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissClicked) {
                Text(
                    text  = "Dismiss",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        containerColor     = MaterialTheme.colorScheme.surface,
        titleContentColor  = MaterialTheme.colorScheme.onSurface,
        textContentColor   = MaterialTheme.colorScheme.onSurfaceVariant
    )
}