package com.treasurehunt.ui.profile

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.treasurehunt.R
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.databinding.FragmentProfileBinding
import com.treasurehunt.util.FILENAME_EXTENSION_PNG
import com.treasurehunt.util.STORAGE_LOCATION_PROFILE_IMAGE
import com.treasurehunt.util.extractDigits
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val NULL_STRING = "null"

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            setImageLauncher(result)
        }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            handleAccessAlbum(isGranted)
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
        initProfile()
        syncProfile()
        setAlbumPermission()
        setEditButton()
        initTabLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setImageLauncher(result: ActivityResult) {
        if (result.data?.data != null) {
            val uri = result.data?.data
            viewModel.addImage(uri.toString())
            viewModel.setProfileUri(uri.toString())
            Glide.with(requireContext()).load(uri).into(binding.ivProfileImage)
        }
    }

    private fun handleAccessAlbum(isGranted: Boolean) {
        if (isGranted) {
            imageLauncher.launch(viewModel.getImage())
        }
    }

    private fun setAlbumPermission() {
        binding.ibCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun setEditButton() {
        binding.ibEdit.setOnClickListener {
            showEditView()
        }
        binding.tvCancel.setOnClickListener {
            hideEditView()
            syncProfile()
        }
        binding.tvCompleted.setOnClickListener {
            if (binding.etNickname.text.isEmpty()) {
                return@setOnClickListener
            } else {
                saveProfile()
                hideEditView()
            }
        }
    }

    private fun showEditView() {
        binding.groupProfileBox.visibility = View.GONE
        binding.groupEditProfileBox.visibility = View.VISIBLE
        binding.etNickname.setText(binding.tvNickname.text)
    }

    private fun hideEditView() {
        binding.groupProfileBox.visibility = View.VISIBLE
        binding.groupEditProfileBox.visibility = View.GONE
    }

    private fun initProfile() {
        viewModel.getUserData()
    }

    private fun syncProfile() {
        viewModel.userData.observe(viewLifecycleOwner) { userDTO ->
            updateProfile(userDTO)
        }
    }

    private suspend fun uploadProfileImage(uri: Uri) {
        val uid = Firebase.auth.currentUser!!.uid
        val filename = uri.toString().extractDigits()
        val profileImageStorageRef = Firebase.storage.reference.child(uid).child(STORAGE_LOCATION_PROFILE_IMAGE)
            .child("$filename$FILENAME_EXTENSION_PNG")
        val uploadTask = profileImageStorageRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            viewModel.setProfileUri(taskSnapshot.storage.toString())
        }.addOnFailureListener {
            binding.root.showSnackbar(R.string.savelog_sb_upload_failure)
        }
        uploadTask.await()
    }

    private fun saveProfile() {
        lifecycleScope.launch {
            val userData = viewModel.userData.value ?: return@launch
            val email = userData.email ?: ""
            val nickname = binding.etNickname.text.toString()
            if (viewModel.imageUri.value.isNullOrEmpty()) {
                viewModel.updateUserData(UserDTO(email, nickname, userData.profileImage.toString()))
            } else {
                uploadProfileImage(viewModel.imageUri.value!!.toUri())
                viewModel.updateUserData(
                    UserDTO(
                        email,
                        nickname,
                        viewModel.profileUri.value.toString()
                    )
                )
            }
        }

    }

    private fun updateProfile(userDTO: UserDTO) {
        if (userDTO.email != null) {
            binding.tvAccount.text = userDTO.email
        }
        if (userDTO.nickname.isNullOrEmpty()) {
            binding.tvNickname.text = Firebase.auth.currentUser!!.uid.substring(0, 16)
        } else {
            binding.tvNickname.text = userDTO.nickname
        }
        loadProfileImage(userDTO)
    }

    private fun loadProfileImage(userDTO: UserDTO) {
        if (userDTO.profileImage.toString().contains(getString(R.string.profile_check_url))) {
            Glide.with(requireContext()).load(userDTO.profileImage.toString())
                .into(binding.ivProfileImage)
        } else if (userDTO.profileImage.isNullOrEmpty() || userDTO.profileImage == NULL_STRING) {
            Glide.with(requireContext()).load(R.drawable.ic_no_profile_image)
                .into(binding.ivProfileImage)
        } else {
            val storageRef =
                Firebase.storage.getReferenceFromUrl(userDTO.profileImage.toString())
            Glide.with(requireContext()).load(storageRef).into(binding.ivProfileImage)
        }
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