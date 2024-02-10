package com.treasurehunt.ui.savelog

import android.content.Context
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await

class ImageUploadWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val uid = inputData.getString("uid")
        val uriString = inputData.getString("uri")

        uid ?: return Result.failure()
        uriString ?: return Result.failure()

        val uri = Uri.parse(uriString)
        val result = uploadImage(uid, uri)
        return if (result) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private suspend fun uploadImage(uid: String, uri: Uri): Boolean {
        val storage = Firebase.storage
        val storageRef = storage.getReference("${uid}/log_images")
        val fileName = uri.toString().replace("[^0-9]".toRegex(), "")
        val mountainsRef = storageRef.child("${fileName}.png")

        return try {
            mountainsRef.putFile(uri).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}