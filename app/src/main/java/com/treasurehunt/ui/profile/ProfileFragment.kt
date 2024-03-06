package com.treasurehunt.ui.profile

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.treasurehunt.R
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.databinding.FragmentProfileBinding
import com.treasurehunt.ui.profile.adapter.ProfileViewPagerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            setAddImage(isGranted)
        }
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            addImage(result)
        }

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
        viewLifecycleOwner.lifecycleScope.launch {
            syncProfile()
        }
        setEditButton()
        setCancelButton()
        setCompleteButton()
        setEditProfileImageButton()
        initTabLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setAddImage(isGranted: Boolean) {
        if (isGranted) {
            pickImagesLauncher.launch(viewModel.getImagePick())
        }
    }

    private fun addImage(result: ActivityResult) {
        val contentUri: String = result.data?.data?.toString() ?: return
        viewModel.addImageContentUri(contentUri)
        loadProfileImage(contentUri)
    }

    private fun loadProfileImage(contentUri: String) {
        binding.ivProfileImage.run {
            Glide.with(context)
                .load(contentUri)
                .into(this)
        }
    }

    private suspend fun syncProfile() {
        viewModel.uiState.collect {
            updateProfile(it.user ?: return@collect)
        }
    }

    private fun updateProfile(user: UserDTO) {
        binding.tvAccount.text = user.email.orEmpty()

        binding.tvNickname.text = user.nickname ?: getString(R.string.profile_guest)

        loadProfileImage(user)
    }

    private fun loadProfileImage(user: UserDTO) {
        binding.ivProfileImage.run {
            try {
                val profileImageStorageRef = user.profileImage?.let {
                    Firebase.storage.getReferenceFromUrl(it)
                }
                Glide.with(context)
                    .load(profileImageStorageRef)
                    .placeholder(R.drawable.ic_no_profile_image)
                    .into(this)
            } catch (e: Exception) {
                setImageResource(R.drawable.ic_no_profile_image)
            }
        }
    }

    private fun setEditButton() {
        binding.ibEdit.setOnClickListener {
            showEditView()
        }
    }

    private fun showEditView() {
        binding.groupProfileBox.visibility = View.GONE
        binding.groupEditProfileBox.visibility = View.VISIBLE
        binding.etNickname.setText(binding.tvNickname.text)
    }

    private fun setCancelButton() {
        binding.btnCancel.setOnClickListener {
            hideEditView()
            updateProfile(viewModel.uiState.value.user ?: return@setOnClickListener)
        }
    }

    private fun hideEditView() {
        binding.groupProfileBox.visibility = View.VISIBLE
        binding.groupEditProfileBox.visibility = View.GONE
    }

    private fun setCompleteButton() {
        binding.btnComplete.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.saveProfile(nickname = binding.etNickname.text.toString())
            }
            hideEditView()
        }
    }

    private fun setEditProfileImageButton() {
        binding.ibEditProfileImage.setOnClickListener {
            requestAlbumAccessPermission()
        }
    }

    private fun requestAlbumAccessPermission() {
        val permissionId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }
        requestPermissionLauncher.launch(permissionId)
    }

    private fun initTabLayout() {
        val tabLayout = binding.tlNotification
        val viewPager = binding.vpNotification

        viewPager.adapter = ProfileViewPagerAdapter(this@ProfileFragment)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getString(ProfileViewPagerAdapter.Tab.entries[position].restId)
        }
    }
}