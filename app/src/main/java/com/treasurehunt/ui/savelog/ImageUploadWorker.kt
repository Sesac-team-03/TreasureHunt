package com.treasurehunt.ui.savelog

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.util.NOTIFICATION_ID
import com.treasurehunt.util.NOTIFICATION_ID_STRING
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class ImageUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    //TODO: handle notification permission

    override suspend fun doWork(): Result {
        val uid = inputData.getString("uid") ?: return Result.failure()
        val urls = inputData.getStringArray("urls")?.asList() ?: return Result.failure()
        val uris = urls.map { Uri.parse(it) }
        val result = uploadImages(uid, uris)

        return if (result) {
            builder.setProgress(100, 100, false)
                .setContentText("업로드 성공")
            notificationManager.notify(NOTIFICATION_ID, builder.build())
            Result.success()
        } else {
            builder.setContentText("업로드 실패")
            notificationManager.notify(NOTIFICATION_ID, builder.build())
            Result.failure()
        }
    }

    private suspend fun uploadImages(uid: String, uris: List<Uri>): Boolean {
        val storageRef = Firebase.storage.getReference("${uid}/log_images")
        val fileNames = uris.map {
            it.toString().replace("[^0-9]".toRegex(), "")
        }
        val refs = fileNames.map {
            storageRef.child("$it.png")
        }

        return try {
            sendNotification()
            Tasks.whenAll(execUploadTasks(uris, refs)).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun execUploadTasks(uris: List<Uri>, refs: List<StorageReference>): List<UploadTask> {
        var totalByteCount = 0L
        var bytesTransferred = 0L

        return uris.mapIndexed { i, uri ->
            refs[i].putFile(uri).apply {
                totalByteCount += snapshot.totalByteCount * 2
                addOnProgressListener {
                    bytesTransferred += it.bytesTransferred
                    builder.setProgress(
                        100,
                        (bytesTransferred * 100 / totalByteCount).toInt(),
                        false
                    )
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                }
            }
        }
    }

    private fun sendNotification() {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE)

        builder = NotificationCompat.Builder(context, NOTIFICATION_ID_STRING)
            .setSmallIcon(R.drawable.ic_chest_open)
            .setContentTitle("업로드 알리미")
            .setContentText("업로드 진행 중")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}