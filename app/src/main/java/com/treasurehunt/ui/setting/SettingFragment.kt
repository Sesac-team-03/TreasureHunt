package com.treasurehunt.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.navercorp.nid.NaverIdLoginSDK
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSettingBinding
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingViewModel by viewModels()

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
        setBackButton()
        setLogoutButton()
        setDeleteUserButton()
        setSwitchState()
        updateSwitchState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setBackButton() {
        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setLogoutButton() {
        binding.btnLogout.setOnClickListener {
            val naverToken = NaverIdLoginSDK.getAccessToken()
            if (naverToken.isNullOrEmpty()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteLogoutLocalData()
                    NaverIdLoginSDK.logout()
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.action_settingFragment_to_logInFragment)
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.deleteLogoutLocalData()
                    Firebase.auth.signOut()
                    findNavController().navigate(R.id.action_settingFragment_to_logInFragment)
                }
            }
        }
    }

    private fun setDeleteUserButton() {
        binding.btnUserDelete.setOnClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_deleteUserFragment)
        }
    }

    private fun setSwitchState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getSwitchState().collect {
                binding.swAutoLogin.isChecked = it
            }
        }
    }

    private fun updateSwitchState() {
        binding.swAutoLogin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updateSwitchState(isChecked)
                    binding.swAutoLogin.showSnackbar(R.string.setting_sb_auto_login_checked)
                }
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.updateSwitchState(isChecked)
                    binding.swAutoLogin.showSnackbar(R.string.setting_sb_auto_login_unchecked)
                }
            }
        }
    }
}