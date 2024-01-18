package com.treasurehunt.ui.savelog

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSavelogBinding
import com.treasurehunt.ui.savelog.adapter.SaveLogAdapter
import com.treasurehunt.util.showSnackbar
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date

class SaveLogFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSavelogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaveLogViewModel by viewModels()
    private val recordAdapter = SaveLogAdapter { imageModel -> viewModel.removeImage(imageModel) }
    private val uriList: MutableList<Uri> = arrayListOf()
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data?.clipData != null) {
                val count = result.data?.clipData!!.itemCount
                if (viewModel.images.value.size + count > 5) {
                    binding.root.showSnackbar(R.string.savelog_sb_warning_count)
                    return@registerForActivityResult
                }
                for (i in 0 until count) {
                    viewModel.addImage(ImageModel(result.data?.clipData!!.getItemAt(i).uri.toString()))
                    uriList.add(result.data?.clipData!!.getItemAt(i).uri)
                }
            } else if (result.data?.data != null) {
                if (viewModel.images.value.size + 1 > 5) {
                    binding.root.showSnackbar(R.string.savelog_sb_warning_count)
                    return@registerForActivityResult
                }
                val uri = result?.data?.data
                if (uri != null) {
                    uriList.add(uri)
                }
                viewModel.addImage(ImageModel(uri.toString()))
            }
            binding.btnSave.setOnClickListener {
                for (i in 0 until uriList.size) {
                    uploadImage(uriList[i], i)
                    try {
                        Thread.sleep(500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }

    private lateinit var map: NaverMap
    private val source: FusedLocationSource =
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            setLocationTrackingMode(isGranted)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavelogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        setAddImage()
        loadMap()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initAdapter() {
        binding.rvPhoto.adapter = recordAdapter
    }

    private fun setAddImage() {
        binding.ibSelectPhoto.setOnClickListener {
            imageLauncher.launch(viewModel.getImage())
        }
    }

    private fun uploadImage(uri: Uri, count: Int) {
        val storage = Firebase.storage
        val storageRef = storage.getReference("uid/image")
        val fileName = SimpleDateFormat("yyyyMMddHHmmss_${count}").format(Date())
        val mountainsRef = storageRef.child("${fileName}.png")
        val uploadTask = mountainsRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            binding.root.showSnackbar(R.string.savelog_sb_upload_success)
        }.addOnFailureListener {
            binding.root.showSnackbar(R.string.savelog_sb_upload_failure)
        }
    }

    private fun setLocationTrackingMode(isGranted: Boolean) {
        map.locationTrackingMode =
            if (isGranted) {
                LocationTrackingMode.Follow
            } else {
                LocationTrackingMode.None
            }
    }

    private fun initMap(naverMap: NaverMap) {
        map = naverMap.apply {
            locationSource = source
            uiSettings.isZoomControlEnabled = false
        }
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun handleLocationAccessPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                setLocationTrackingMode(true)
            }

            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        initMap(naverMap)
        handleLocationAccessPermission()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}