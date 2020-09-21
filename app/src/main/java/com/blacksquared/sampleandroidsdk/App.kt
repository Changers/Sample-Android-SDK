package com.blacksquared.sampleandroidsdk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.blacksquared.sdk.activity.WebActivity
import com.blacksquared.sdk.app.Changers

class App : android.app.Application() {
    override fun onCreate() {
        super.onCreate()

        initializeSdk()
    }

    private fun initializeSdk() {
        Changers.with(this,
                Changers.Settings(
                        notification = createNotification(),
                        appName = BuildConfig.APP_NAME,
                        displayName = getString(R.string.app_name),
                        clientId = BuildConfig.CLIENT_ID,
                        clientSecret = BuildConfig.CLIENT_SECRET,
                        version = BuildConfig.VERSION_NAME
                )
        )
    }

    // create a notification channel, required for Android Oreo or newer
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        // Create the NotificationChannel
        val name = getString(R.string.persistent_channel_name)
        val descriptionText = getString(R.string.persistent_channel_description)
        val importance = NotificationManager.IMPORTANCE_MIN
        val mChannel = NotificationChannel(NOTIFICATION_CHANNEL_PERSISTENT, name, importance)
        mChannel.description = descriptionText

        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }

    // create the notification that's going to be used while motion tracking is active.
    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                Intent(this, WebActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationText = getString(R.string.tracking_notification_message)
        val notificationIcon = R.mipmap.ic_launcher

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_PERSISTENT)
                .setContentIntent(pendingIntent)
                .setContentText(notificationText)
                .setSmallIcon(notificationIcon)
                .setGroup(NOTIFICATION_GROUP_TRACKING)
                .setPriority(NOTIFICATION_GROUP_TRACKING_PRIORITY)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
    }

    companion object {
        val NOTIFICATION_GROUP_TRACKING_PRIORITY = NotificationCompat.PRIORITY_LOW
        const val NOTIFICATION_GROUP_TRACKING = "${BuildConfig.APPLICATION_ID}.TRACKING"
        const val NOTIFICATION_CHANNEL_PERSISTENT =
                "${BuildConfig.APPLICATION_ID}.notification_channel_persistent"
        const val NOTIFICATION_CHANNEL_PERSISTENT_ID =
                "${BuildConfig.APPLICATION_ID}.notification_channel_persistent"
    }
}
