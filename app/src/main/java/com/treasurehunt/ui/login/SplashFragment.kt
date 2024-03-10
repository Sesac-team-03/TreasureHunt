package com.treasurehunt.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : PreloadFragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    override val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        initSplashScreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSplashScreen() {
        binding.animationView.setAnimation(R.raw.iv_treasure)
        binding.animationView.playAnimation()

        lifecycleScope.launch {
            delay(2000)
            handleAutoLogin()
        }
    }

    private fun handleAutoLogin() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.initLocalData()
                preloadProfileImage(currentUser)
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            }
        }
    }
}