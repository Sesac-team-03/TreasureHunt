package com.treasurehunt.ui.login

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.storage

abstract class PreloadFragment : Fragment() {

    protected abstract val viewModel: LoginViewModel

    protected suspend fun preloadProfileImage(currentUser: FirebaseUser?) {
        if (currentUser == null) return

        val storageUrl = viewModel.getProfileImageStorageUrl(currentUser.uid) ?: return
        val storageRef = Firebase.storage.getReferenceFromUrl(storageUrl)

        Glide.with(requireContext())
            .load(storageRef)
            .preload()
    }
}