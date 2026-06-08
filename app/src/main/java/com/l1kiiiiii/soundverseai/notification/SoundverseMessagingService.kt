package com.l1kiiiiii.soundverseai.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.l1kiiiiii.soundverseai.MainActivity

/**
 * SoundverseMessagingService — Firebase Cloud Messaging handler.
 *
 * Foreground rule: When a FCM push arrives while the app is in the foreground,
 * the service broadcasts an intent to MainActivity which shows an AlertDialog
 * instead of posting the system notification.
 *
 * Deep link rule: Notification taps route to soundverse://export via MainActivity's
 * intent filter, bypassing any dialogs and going directly to the ExportStateScreen.
 */
class SoundverseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID   = "soundverse_channel"
        const val CHANNEL_NAME = "Soundverse Notifications"
        const val ACTION_FOREGROUND_NOTIFICATION =
            "com.l1kiiiiii.soundverseai.FOREGROUND_NOTIFICATION"
        const val EXTRA_NOTIFICATION_TITLE = "notification_title"
        const val EXTRA_NOTIFICATION_BODY  = "notification_body"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // In a real app: send token to your backend server here
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Soundverse"
        val body  = remoteMessage.notification?.body  ?: "Your track is ready to share!"

        if (MainActivity.isAppInForeground) {
            // Broadcast to MainActivity to show an AlertDialog
            val broadcastIntent = Intent(ACTION_FOREGROUND_NOTIFICATION).apply {
                putExtra(EXTRA_NOTIFICATION_TITLE, title)
                putExtra(EXTRA_NOTIFICATION_BODY, body)
                setPackage(packageName)
            }
            sendBroadcast(broadcastIntent)
        } else {
            // App is backgrounded — post a standard system notification
            postSystemNotification(title, body)
        }
    }

    private fun postSystemNotification(title: String, body: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Soundverse push notifications"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Deep link pending intent — routes to soundverse://export
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            android.net.Uri.parse("soundverse://export"),
            this,
            MainActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
