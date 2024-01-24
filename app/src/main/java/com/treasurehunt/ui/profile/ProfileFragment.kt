package com.treasurehunt.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.treasurehunt.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditButton()
        initProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEditButton() {
        binding.ibEdit.setOnClickListener {
            binding.tvCancel.visibility = View.VISIBLE
            binding.tvCompleted.visibility = View.VISIBLE
            binding.ibEdit.visibility = View.GONE
        }
        binding.tvCancel.setOnClickListener {
            binding.tvCancel.visibility = View.GONE
            binding.tvCompleted.visibility = View.GONE
            binding.ibEdit.visibility = View.VISIBLE
        }
        binding.tvCompleted.setOnClickListener {
            binding.tvCancel.visibility = View.GONE
            binding.tvCompleted.visibility = View.GONE
            binding.ibEdit.visibility = View.VISIBLE
        }
    }

    private fun initProfile() {
        lifecycleScope.launch {
            if (viewModel.getUserData().email.isNotEmpty()) {
                val profileImage = viewModel.getUserData().profileImage
                val nickname = viewModel.getUserData().nickname
                val email = viewModel.getUserData().email
                Glide.with(requireContext()).load(profileImage).into(binding.ivProfileImage)
                binding.tvNickname.text = nickname
                binding.tvAccount.text = email
            } else {
                Log.d("ProfileFragment", viewModel.getUserData().toString())
            }
        }
    }
}