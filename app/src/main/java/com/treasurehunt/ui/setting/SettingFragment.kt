package com.treasurehunt.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.navercorp.nid.NaverIdLoginSDK
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backProfile()
        setLogout()
        showDeleteDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun backProfile() {
        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setLogout() {
        binding.tvLogout.setOnClickListener {
            val naverToken = NaverIdLoginSDK.getAccessToken() ?: ""
            if (naverToken.isNotEmpty()) {
                NaverIdLoginSDK.logout()
                findNavController().navigate(R.id.action_settingFragment_to_logInFragment)
            } else {
                Firebase.auth.signOut()
                findNavController().navigate(R.id.action_settingFragment_to_logInFragment)
            }
        }
    }

    private fun showDeleteDialog() {
        binding.tvUserDelete.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_deleteUserFragment)
        }
    }
}