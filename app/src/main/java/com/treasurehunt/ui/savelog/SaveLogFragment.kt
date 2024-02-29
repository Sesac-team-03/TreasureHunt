package com.treasurehunt.ui.savelog

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentSavelogBinding
import com.treasurehunt.ui.model.ImageModel
import com.treasurehunt.ui.savelog.adapter.SaveLogAdapter
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val MAX_COUNT = 5

internal const val WORK_DATA_UID = "uid"
internal const val WORK_DATA_URI_STRINGS = "uriStrings"
internal const val WORK_DATA_LOG_TEXT = "logText"
internal const val WORK_DATA_LAT = "lat"
internal const val WORK_DATA_LNG = "lng"
internal const val WORK_DATA_CAPTION = "caption"
internal const val WORK_DATA_IS_PLAN = "isPlan"
internal const val WORK_DATA_PLAN_ID = "planId"
internal const val WORK_DATA_URLS = "url"
internal const val WORK_DATA_URL_STRINGS = "urlStrings"

@AndroidEntryPoint
class SaveLogFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSavelogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaveLogViewModel by viewModels()
    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            setImageLauncher(result)
        }
    private lateinit var map: NaverMap
    private val source: FusedLocationSource =
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            setLocationTrackingMode(isGranted)
            setAddImage(isGranted)
        }
    private val args: SaveLogFragmentArgs by navArgs()
    private var isNotificationPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavelogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            loadLogIfExists()
            setSaveButton()
        }

        setShowMapFullScreen()
        loadMap()
        initViewModel()
        initAdapter()
        setAlbumPermission()
        setCancelButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun loadLogIfExists() {
        args.log?.let { log ->
            viewModel.getImageUrls(log.remoteImageIds).forEach { imageUrl ->
                viewModel.addImage(ImageModel(storageUrl = imageUrl))
            }
            viewModel.setTextInput(log.text)
        }
    }

    private fun setImageLauncher(result: ActivityResult) {
        if (result.data?.clipData != null) {
            val count = result.data?.clipData!!.itemCount
            if (viewModel.images.value.size + count > MAX_COUNT) {
                binding.root.showSnackbar(R.string.savelog_sb_warning_count)
                return
            }
            for (i in 0 until count) {
                viewModel.addImage(ImageModel(result.data?.clipData!!.getItemAt(i).uri.toString()))
            }
        } else if (result.data?.data != null) {
            if (viewModel.images.value.size + 1 > MAX_COUNT) {
                binding.root.showSnackbar(R.string.savelog_sb_warning_count)
                return
            }
            val uri = result.data?.data
            viewModel.addImage(ImageModel(uri.toString()))
        }
    }

    private fun setAddImage(isGranted: Boolean) {
        if (isGranted) {
            imageLauncher.launch(viewModel.getImagePick())
        }
    }

    private fun setShowMapFullScreen() {
        binding.ibFullScreen.setOnClickListener {
            findNavController().navigate(R.id.action_saveLogFragment_to_saveLogMapFragment)
        }
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setLocationTrackingMode(isGranted: Boolean) {
        map.locationTrackingMode = if (isGranted) {
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

    override fun onMapReady(naverMap: NaverMap) {
        initMap(naverMap)
        handleLocationAccessPermission()
    }

    private fun handleLocationAccessPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                setLocationTrackingMode(true)
            }

            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    private fun initViewModel() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initAdapter() {
        binding.rvPhoto.adapter = SaveLogAdapter { imageModel ->
            viewModel.removeImage(imageModel)
        }
    }

    private fun setAlbumPermission() {
        binding.ibSelectPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun setSaveButton() {
        binding.btnSave.setOnClickListener {
            // TODO: return if uid == null

            handleNotificationPermission()

            scheduleImageUploadAndDatabaseUpdateWorks()

            findNavController().navigate(R.id.action_saveLogFragment_to_homeFragment)
        }
    }

    private fun handleNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
            )) {
                PackageManager.PERMISSION_GRANTED -> {
                    isNotificationPermissionGranted = true
                }

                PackageManager.PERMISSION_DENIED -> {
                    requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            isNotificationPermissionGranted = true
        }
    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                isNotificationPermissionGranted = true
            }
        }

    private fun scheduleImageUploadAndDatabaseUpdateWorks() {
        WorkManager.getInstance(requireContext())
            .beginWith(getImageUploadRequest())
            .then(getDatabaseUpdateRequest())
            .enqueue()
    }

    private fun getImageUploadRequest(): OneTimeWorkRequest {
        val uid = Firebase.auth.currentUser!!.uid
        val images = viewModel.images.value
        val (contentUris, storageUrls) = if (args.log == null) {
            images.indices.map { i ->
                images[i].uri
            }.toTypedArray() to emptyArray()
        } else {
            images.partition { image ->
                image.uri.isNotEmpty()
            }.run {
                first.map { image ->
                    image.uri
                }.toTypedArray() to second.map { image ->
                    image.storageUrl
                }.toTypedArray()
            }
        }
        val data = Data.Builder()
            .putString(WORK_DATA_UID, uid)
            .putStringArray(WORK_DATA_URI_STRINGS, contentUris)
            .putStringArray(WORK_DATA_URLS, storageUrls)
            .build()

        return OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setInputData(data)
            .build()
    }

    private fun getDatabaseUpdateRequest(): OneTimeWorkRequest {
        val uid = Firebase.auth.currentUser!!.uid
        val (lat, lng, isPlan, caption, planId) = args.mapSymbol
        val logText = binding.etText.text.toString()
        val data = Data.Builder()
            .putString(WORK_DATA_UID, uid)
            .putString(WORK_DATA_LOG_TEXT, logText)
            .putDouble(WORK_DATA_LAT, lat)
            .putDouble(WORK_DATA_LNG, lng)
            .putString(WORK_DATA_CAPTION, caption)
            .putBoolean(WORK_DATA_IS_PLAN, isPlan)
            .putString(WORK_DATA_PLAN_ID, planId)
            .build()

        return OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
            .setInputData(data)
            .build()
    }

    private fun setCancelButton() {
        binding.ibCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}