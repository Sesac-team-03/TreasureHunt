package com.treasurehunt.ui.login

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSplashScreen()
    }

    private fun initSplashScreen() {
        binding.animationView.setAnimation(R.raw.iv_treasure)
        binding.animationView.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            if (Firebase.auth.currentUser == null) findNavController().navigate(R.id.action_splashFragment_to_logInFragment)
            else {
                lifecycleScope.launch {
                    viewModel.castingRemoteData()
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            }
        }, 4000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}