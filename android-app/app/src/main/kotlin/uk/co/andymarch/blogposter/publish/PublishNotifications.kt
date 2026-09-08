package uk.co.andymarch.blogposter.publish

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import uk.co.andymarch.blogposter.R

object PublishNotifications {

    private const val CHANNEL_ID = "publish"
    private const val NOTIFICATION_ID = 1001

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Post publishing",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Progress and results of publishing posts to GitHub"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    fun success(context: Context, title: String) {
        notify(context, "Published", "\"$title\" is live on GitHub")
    }

    fun failure(context: Context, title: String, reason: String) {
        notify(context, "Publish failed", "\"$title\": $reason")
    }

    private fun notify(context: Context, title: String, text: String) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS,
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission) return

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }
}
