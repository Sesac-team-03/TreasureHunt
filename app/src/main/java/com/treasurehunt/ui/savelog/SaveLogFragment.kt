package com.treasurehunt.ui.savelog

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.get
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
import com.treasurehunt.ui.model.TextTheme
import com.treasurehunt.ui.savelog.adapter.SaveLogAdapter
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
private const val PICK_IMAGE_MAX_COUNT = 5

internal const val WORK_DATA_UID = "uid"
internal const val WORK_DATA_URI_STRINGS = "uriStrings"
internal const val WORK_DATA_URL_STRINGS = "urlStrings"
internal const val WORK_DATA_LOG_TEXT = "logText"
internal const val WORK_DATA_LAT = "lat"
internal const val WORK_DATA_LNG = "lng"
internal const val WORK_DATA_CAPTION = "caption"
internal const val WORK_DATA_IS_PLAN = "isPlan"
internal const val WORK_DATA_PLAN_ID = "planId"
internal const val WORK_DATA_LOCAL_LOG_ID = "localLogId"
internal const val WORK_DATA_REMOTE_LOG_ID = "remoteLogId"
internal const val WORK_DATA_REMOTE_PLACE_ID = "remotePlaceId"
internal const val WORK_DATA_TEXT_THEME = "textTheme"

@AndroidEntryPoint
class SaveLogFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSavelogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaveLogViewModel by viewModels()
    private lateinit var map: NaverMap
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            setLocationTrackingMode(isGranted)
            setAddImage(isGranted)
        }
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            addImages(result)
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

        initViewModel()
        initAdapter()

        setTextThemeButtonGroup()

        viewLifecycleOwner.lifecycleScope.launch {
            loadLogIfExists()
            setSaveButton()
        }

        loadMap()
        setShowMapFullScreen()
        setPickImageButton()
        setCancelButton()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private suspend fun loadLogIfExists() {
        args.log?.let { log ->
            viewModel.getImageStorageUrls(log.remoteImageIds).forEach { imageUrl ->
                viewModel.addImage(ImageModel(storageUrl = imageUrl))
            }
            setTextField(log.text)
            setTextTheme(log.theme)
        }
    }

    private fun setTextField(input: String) {
        binding.etText.setText(input)
    }

    private fun setTextTheme(theme: Int) {
        binding.rbDefault.isChecked = false
        (binding.rgTheme[theme] as RadioButton).isChecked = true
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setShowMapFullScreen() {
        binding.ibFullScreen.setOnClickListener {
            findNavController().navigate(R.id.action_saveLogFragment_to_saveLogMapFragment)
        }
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
            locationSource =
                FusedLocationSource(this@SaveLogFragment, LOCATION_PERMISSION_REQUEST_CODE)
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

    private fun setAddImage(isGranted: Boolean) {
        if (isGranted) {
            pickImagesLauncher.launch(viewModel.getImagePick())
        }
    }

    private fun addImages(result: ActivityResult) {
        val intent = result.data ?: return
        val count = intent.clipData?.itemCount ?: 1
        if (isMaxCountExceeded(count)) return

        intent.clipData?.let { clipData ->
            repeat(count) {
                viewModel.addImage(ImageModel(clipData.getItemAt(it).uri.toString()))
            }
            return
        }

        intent.data?.let { uri ->
            viewModel.addImage(ImageModel(uri.toString()))
            return
        }
    }

    private fun isMaxCountExceeded(count: Int): Boolean {
        return if (viewModel.uiState.value.images.size + count > PICK_IMAGE_MAX_COUNT) {
            binding.root.showSnackbar(R.string.savelog_sb_warning_count)
            true
        } else {
            false
        }
    }

    private fun setPickImageButton() {
        binding.ibPickImage.setOnClickListener {
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

    private fun setSaveButton() {
        binding.btnSave.setOnClickListener {
            // TODO: return if uid == null

            handleNotificationPermission()

            scheduleImageUploadAndDatabaseUpdateWorks()

            findNavController().navigateUp()
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
        return OneTimeWorkRequestBuilder<ImageUploadWorker>()
            .setInputData(getImageUploadInputData())
            .build()
    }

    private fun getImageUploadInputData(): Data {
        val uid = Firebase.auth.currentUser!!.uid
        val images = viewModel.uiState.value.images
        val (contentUris, storageUrls) = if (args.log == null) {
            images.mapToContentUriArray() to emptyArray()
        } else {
            images.partitionIntoContentAndStorageImages().run {
                first.mapToContentUriArray() to second.mapToStorageUriArray()
            }
        }

        return Data.Builder()
            .putString(WORK_DATA_UID, uid)
            .putStringArray(WORK_DATA_URI_STRINGS, contentUris)
            .putStringArray(WORK_DATA_URL_STRINGS, storageUrls)
            .apply {
                args.log?.let { log ->
                    requireNotNull(log.remoteId)
                    putString(WORK_DATA_REMOTE_LOG_ID, log.remoteId)
                }
            }
            .build()
    }

    private fun List<ImageModel>.partitionIntoContentAndStorageImages() =
        partition { it.contentUri.isNotEmpty() }

    private fun List<ImageModel>.mapToContentUriArray() = map { it.contentUri }.toTypedArray()

    private fun List<ImageModel>.mapToStorageUriArray() = map { it.storageUrl }.toTypedArray()

    private fun getDatabaseUpdateRequest(): OneTimeWorkRequest {
        return OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
            .setInputData(getDatabaseUpdateInputData())
            .build()
    }

    private fun getDatabaseUpdateInputData(): Data {
        val uid = Firebase.auth.currentUser!!.uid
        val (lat, lng, isPlan, caption, planId) = args.mapSymbol
        val logText = binding.etText.text.toString()
        val textTheme = binding.rgTheme.run {
            indexOfChild(findViewById<RadioButton>(checkedRadioButtonId))
        }

        return Data.Builder()
            .putString(WORK_DATA_UID, uid)
            .putString(WORK_DATA_LOG_TEXT, logText)
            .putDouble(WORK_DATA_LAT, lat)
            .putDouble(WORK_DATA_LNG, lng)
            .putString(WORK_DATA_CAPTION, caption)
            .putBoolean(WORK_DATA_IS_PLAN, isPlan)
            .putString(WORK_DATA_PLAN_ID, planId)
            .putInt(WORK_DATA_TEXT_THEME, textTheme)
            .apply {
                args.log?.let { log ->
                    requireNotNull(log.localId)
                    requireNotNull(log.remoteId)
                    requireNotNull(log.remotePlaceId)
                    putLong(WORK_DATA_LOCAL_LOG_ID, log.localId)
                    putString(WORK_DATA_REMOTE_LOG_ID, log.remoteId)
                    putString(WORK_DATA_REMOTE_PLACE_ID, log.remotePlaceId)
                }
            }
            .build()
    }

    private fun setCancelButton() {
        binding.ibCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setTextThemeButtonGroup() {
        with(binding) {
            rbDefault.set(TextTheme.DEFAULT)

            rbLimeBlue.set(TextTheme.LIME_BLUE)

            rbPurple.set(TextTheme.PURPLE)

            rbMint.set(TextTheme.MINT)

            rbOrangeBlack.set(TextTheme.ORANGE_BLACK)

            rbPeach.set(TextTheme.PEACH)
        }
    }

    private fun RadioButton.set(theme: TextTheme) {
        setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etText.background = theme.backgroundResId?.let {
                    AppCompatResources.getDrawable(requireContext(), it)
                }
                binding.etText.setTextColor(requireContext().getColor(theme.textColorResId))
            }
        }
    }
}