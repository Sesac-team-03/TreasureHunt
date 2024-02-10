package com.treasurehunt.ui.savelog

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.util.NOTIFICATION_ID
import com.treasurehunt.util.NOTIFICATION_ID_STRING

class CoroutineUploadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
//        uploadImages()
//
//        val remotePlaceId = getRemotePlaceId()
//        val log = getLogFor(remotePlaceId)
//        val remoteLogId = insertLog(log)
//
//        updatePlaceWithLog(remotePlaceId, remoteLogId)
//        updateUser(remotePlaceId, remoteLogId)

        // failure when?

        val isNotificationPermitted = inputData.getBoolean("IS_NOTIFICATION_PERMITTED", false)

        if (isNotificationPermitted) {
            sendUploadNotification(true)
        }

        return Result.success()
    }


    private fun sendUploadNotification(isSuccessful: Boolean) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.areNotificationsEnabled()) {
            val launchIntent =
                context.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
            val pendingIntent =
                PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE)
            val text = if (isSuccessful) "업로드 성공" else "업로드 실패"
            var builder = NotificationCompat.Builder(context, NOTIFICATION_ID_STRING)
                .setSmallIcon(R.drawable.ic_chest_open)
                .setContentTitle("업로드 알리미")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            val notification = builder.build()
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }
}