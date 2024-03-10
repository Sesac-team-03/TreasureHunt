package com.treasurehunt.ui.login

import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser

abstract class PreloadFragment : Fragment() {

    protected abstract val viewModel: LoginViewModel

    protected suspend fun preloadProfileImage(currentUser: FirebaseUser?) {
        if (currentUser == null) return

        val profileImageStorageUrl = viewModel.getProfileImageStorageUrl(currentUser.uid) ?: return
        Glide.with(requireContext())
            .load(profileImageStorageUrl)
            .preload()
    }
}