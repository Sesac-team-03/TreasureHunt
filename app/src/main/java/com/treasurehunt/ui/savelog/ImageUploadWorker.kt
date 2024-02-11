package com.treasurehunt.ui.savelog

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.UploadTask.TaskSnapshot
import com.google.firebase.storage.storage
import com.treasurehunt.BuildConfig
import com.treasurehunt.R
import com.treasurehunt.util.UPLOAD_NOTIFICATION_ID
import com.treasurehunt.util.UPLOAD_NOTIFICATION_ID_STRING
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class ImageUploadWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val urlStrings = mutableListOf<String>()
    private var totalByteCount = 0L
    private var bytesTransferred = 0L
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    override suspend fun doWork(): Result {
        val uid = inputData.getString(WORK_DATA_UID)
            ?: return Result.failure()
        val uriStrings = inputData.getStringArray(WORK_DATA_URI_STRINGS)?.asList()
            ?: return Result.failure()
        val uris = uriStrings.map { it.toUri() }
        val result = uploadImages(uid, uris)

        return if (result) {
            val outputData = Data.Builder()
                .putStringArray(WORK_DATA_URL_STRINGS, urlStrings.toTypedArray())
                .build()

            notificationManager.updateNotification(
                builder,
                context.getString(R.string.savelog_image_upload_notification_success),
                Triple(100, 100, false)
            )
            Result.success(outputData)
        } else {
            notificationManager.updateNotification(
                builder,
                context.getString(R.string.savelog_image_upload_notification_fail)
            )
            Result.failure()
        }
    }

    private suspend fun uploadImages(uid: String, uris: List<Uri>): Boolean {
        return try {
            val refs = getStorageReferences(uid, uris)
            builder = buildNotification()
            Tasks.whenAll(execUploadTasks(uris, refs)).await()

            true
        } catch (e: Exception) {
            e.printStackTrace()

            false
        }
    }

    private fun getStorageReferences(uid: String, uris: List<Uri>): List<StorageReference> {
        val storageRef = Firebase.storage.getReference("${uid}/log_images")
        val fileNames = uris.map {
            it.toString().replace("[^0-9]".toRegex(), "")
        }

        return fileNames.map {
            storageRef.child("$it.png")
        }
    }

    private fun execUploadTasks(uris: List<Uri>, refs: List<StorageReference>): List<UploadTask> {
        return uris.mapIndexed { i, uri ->
            refs[i].putFile(uri).apply {
                setUrlStrings(i, snapshot.storage.toString())

                setTotalByteCount(snapshot)

                setProgressNotification()
            }
        }
    }

    private fun setUrlStrings(index: Int, urlString: String) {
        urlStrings.add(index, urlString)
    }

    private fun setTotalByteCount(snapshot: TaskSnapshot) {
        totalByteCount += snapshot.totalByteCount
    }

    private fun UploadTask.setProgressNotification() {
        addOnProgressListener {
            bytesTransferred += it.bytesTransferred
            builder.setProgress(
                100,
                (bytesTransferred * 100 / totalByteCount).toInt(),
                false
            )
            notificationManager.sendNotification()
        }
    }

    private fun buildNotification(): NotificationCompat.Builder {
        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(BuildConfig.APPLICATION_ID)
        val pendingIntent =
            PendingIntent.getActivity(context, 0, launchIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, UPLOAD_NOTIFICATION_ID_STRING)
            .setSmallIcon(R.drawable.ic_chest_open)
            .setContentTitle(context.getString(R.string.savelog_image_upload_notification_title))
            .setContentText(context.getString(R.string.savelog_image_upload_notification_progress))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }

    private fun NotificationManager.sendNotification() {
        if (!areNotificationsEnabled()) return

        notificationManager.notify(UPLOAD_NOTIFICATION_ID, builder.build())
    }

    private fun NotificationManager.updateNotification(
        builder: NotificationCompat.Builder,
        text: String? = null,
        progress: Triple<Int, Int, Boolean>? = null
    ) {
        if (!areNotificationsEnabled()) return

        text?.run {
            builder.setContentText(text)
        }

        progress?.run {
            val (max, current, indeterminate) = progress
            builder.setProgress(max, current, indeterminate)
        }

        sendNotification()
    }
}