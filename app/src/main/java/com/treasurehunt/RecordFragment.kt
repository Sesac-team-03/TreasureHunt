package com.treasurehunt

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.treasurehunt.databinding.FragmentRecordBinding
import java.io.File
import java.net.URL

class RecordFragment : Fragment() {
    private var _binding: FragmentRecordBinding? = null
    private val binding get() = _binding!!
    private val recordAdapter = RecordAdapter()
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImages = mutableListOf<Uri>()

                if (result.data?.clipData != null) {
                    val count = result.data?.clipData!!.itemCount
                    if (count > 5) {
                        Snackbar.make(
                            binding.root,
                            getString(R.string.record_sb_max_count),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    for (i in 0 until count) {
                        selectedImages.add(result.data?.clipData!!.getItemAt(i).uri)
                    }
                } else if (result.data?.data != null) {
                    selectedImages.add(result.data!!.data!!)
                }
                handleSelectedImages(selectedImages)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvPhoto.adapter = recordAdapter
        binding.ibSelectPhoto.setOnClickListener {
            openGallery()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        imageLauncher.launch(intent)
    }

    private fun handleSelectedImages(uris: List<Uri>?) {
        recordAdapter.submitList(uris)
    }
}