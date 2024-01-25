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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.treasurehunt.R
import com.treasurehunt.data.remote.model.UserDTO
import com.treasurehunt.databinding.FragmentProfileBinding
import com.treasurehunt.util.showSnackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory }
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAlbumPermission()
        setEditButton()
        initProfile()
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
        binding.ibEdit.visibility = View.GONE
        binding.tvNickname.visibility = View.GONE
        binding.tvCancel.visibility = View.VISIBLE
        binding.tvCompleted.visibility = View.VISIBLE
        binding.ibCamera.visibility = View.VISIBLE
        binding.etNickname.visibility = View.VISIBLE
    }

    private fun hideEditView() {
        binding.ibEdit.visibility = View.VISIBLE
        binding.tvNickname.visibility = View.VISIBLE
        binding.tvCancel.visibility = View.GONE
        binding.tvCompleted.visibility = View.GONE
        binding.ibCamera.visibility = View.GONE
        binding.etNickname.visibility = View.GONE
    }

    private fun initProfile() {
        viewModel.getUserData()
    }

    private suspend fun uploadProfileImage(uri: Uri) {
        val uid = Firebase.auth.currentUser!!.uid
        val storage = Firebase.storage
        val storageRef = storage.getReference("${uid}/profile_image")
        val fileName = uri.toString().replace("[^0-9]".toRegex(), "")
        val mountainsRef = storageRef.child("${fileName}.png")
        val uploadTask = mountainsRef.putFile(uri)
        uploadTask.await()
        uploadTask.addOnProgressListener { taskSnapshot ->
            viewModel.setProfileUri(taskSnapshot.storage.toString())
        }.addOnSuccessListener {
            binding.root.showSnackbar(R.string.savelog_sb_upload_success)
        }.addOnFailureListener {
            binding.root.showSnackbar(R.string.savelog_sb_upload_failure)
        }
    }

    private fun saveProfile() {
        lifecycleScope.launch {
            if (viewModel.imageUri.value.isNotEmpty()) {
                uploadProfileImage(viewModel.imageUri.value.toUri())
                viewModel.insertUserData(
                    UserDTO(
                        email = binding.tvAccount.text.toString(),
                        nickname = binding.etNickname.text.toString(),
                        profileImage = viewModel.profileUri.value
                    )
                )
            } else {
                viewModel.insertUserData(
                    UserDTO(
                        email = binding.tvAccount.text.toString(),
                        nickname = binding.etNickname.text.toString(),
                        profileImage = viewModel.profileUri.value
                    )
                )
            }
        }
    }
}