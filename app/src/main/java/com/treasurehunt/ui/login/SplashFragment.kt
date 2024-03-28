package com.treasurehunt.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSplashBinding
import com.treasurehunt.util.preloadProfileImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SPLASH_ANIMATION_DURATION = 2000L

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

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
            delay(SPLASH_ANIMATION_DURATION)
            handleAutoLogin()
        }
    }

    private fun handleAutoLogin() {
        val currentUser = Firebase.auth.currentUser
        viewLifecycleOwner.lifecycleScope.launch {
            val isAutoLoginOn = viewModel.getSwitchState().first()
            if (currentUser != null && isAutoLoginOn) {
                viewModel.initLocalData(currentUser.uid)
                preloadProfileImage(viewModel.getProfileImageStorageUrl(currentUser.uid))
                findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
            }
        }
    }
}