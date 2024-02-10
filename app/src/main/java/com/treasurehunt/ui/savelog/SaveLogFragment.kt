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
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.data.remote.model.ImageDTO
import com.treasurehunt.data.remote.model.toPlaceEntity
import com.treasurehunt.databinding.FragmentSavelogBinding
import com.treasurehunt.ui.model.LogModel
import com.treasurehunt.ui.model.MapSymbol
import com.treasurehunt.ui.model.asLogDTO
import com.treasurehunt.ui.model.asLogEntity
import com.treasurehunt.ui.model.asPlaceDTO
import com.treasurehunt.ui.model.asPlaceEntity
import com.treasurehunt.ui.model.toPlace
import com.treasurehunt.ui.savelog.adapter.SaveLogAdapter
import com.treasurehunt.util.getCurrentTime
import com.treasurehunt.util.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SaveLogFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSavelogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaveLogViewModel by viewModels()
    private val saveLogAdapter = SaveLogAdapter { imageModel -> viewModel.removeImage(imageModel) }
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavelogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        showMapFullScreen()
        initAdapter()
        setAlbumPermission()
        loadMap()
        setSaveButton()
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

    private fun initAdapter() {
        binding.rvPhoto.adapter = saveLogAdapter
    }

    private fun showMapFullScreen() {
        binding.ibFullScreen.setOnClickListener {
            findNavController().navigate(R.id.action_saveLogFragment_to_saveLogMapFragment)
        }
    }

    private fun setAddImage(isGranted: Boolean) {
        if (isGranted) {
            imageLauncher.launch(viewModel.getImage())
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
            viewLifecycleOwner.lifecycleScope.launch {
//                uploadImages()
                // 사진 업로드 백그라운드 구현 테스트
                uploadImage1()

                val remotePlaceId = getRemotePlaceId()
                val log = getLogFor(remotePlaceId)
                val remoteLogId = insertLog(log)

                updatePlaceWithLog(remotePlaceId, remoteLogId)
                updateUser(remotePlaceId, remoteLogId)
                
            }
            findNavController().navigate(R.id.action_saveLogFragment_to_homeFragment)
        }
    }

    private suspend fun uploadImages() {
        val uid = Firebase.auth.currentUser!!.uid

        for (i in 0 until viewModel.images.value.size) {
            val result = viewModel.uploadImage(
                i + 1,
                viewModel.images.value.size,
                uid,
                viewModel.images.value[i].url.toUri()
            )

            if (result) {
                binding.root.showSnackbar(
                    getString(
                        R.string.savelog_sb_upload_success,
                        i + 1,
                        viewModel.images.value.size
                    )
                )
            } else {
                binding.root.showSnackbar(R.string.savelog_sb_upload_failure)
            }
        }
    }

    // 사진 업로드 백그라운드 구현 테스트
    private fun uploadImage1() {
        val uid = Firebase.auth.currentUser!!.uid
        val images = viewModel.images.value
        val uploadRequests = mutableListOf<OneTimeWorkRequest>()
        for (i in images.indices) {
            val data = Data.Builder()
                .putString("uid", uid)
                .putString("uri", images[i].url)
                .build()

            val uploadRequest = OneTimeWorkRequestBuilder<ImageUploadWorker>()
                .setInputData(data)
                .build()

            uploadRequests.add(uploadRequest)
        }
    }

    private suspend fun getRemotePlaceId(): String {
        val planId = args.mapSymbol.remoteId
        return if (planId.isNullOrEmpty()) {
            insertPlace(args.mapSymbol)
        } else {
            updatePlaceFromPlanToVisit(planId)
            planId
        }
    }

    private suspend fun insertPlace(mapSymbol: MapSymbol): String {
        val place = mapSymbol.toPlace()
        val remotePlaceId = viewModel.insertPlace(place.asPlaceDTO())
        val localPlaceId = viewModel.insertPlace(place.asPlaceEntity())

        viewModel.updatePlace(
            place.asPlaceEntity(remotePlaceId, localPlaceId)
        )
        viewModel.updatePlace(
            remotePlaceId,
            place.asPlaceDTO(localPlaceId)
        )

        return remotePlaceId
    }

    private suspend fun updatePlaceFromPlanToVisit(remotePlaceId: String) {
        val placeDTO = viewModel.getRemotePlaceById(remotePlaceId)

        viewModel.updatePlace(
            placeDTO.toPlaceEntity(remotePlaceId)
                .copy(plan = false)
        )
        viewModel.updatePlace(
            remotePlaceId, placeDTO.copy(plan = false)
        )
    }

    private suspend fun getLogFor(remotePlaceId: String): LogModel {
        val text = binding.etText.text.toString()
        val theme = "123"
        val createdDate = getCurrentTime()
        val imageIds = viewModel.imageUrl.value.map { imageUrl ->
            viewModel.insertImage(
                ImageDTO(url = imageUrl)
            )
        }

        return LogModel(
            remotePlaceId,
            text,
            theme,
            createdDate,
            imageIds
        )
    }

    private suspend fun insertLog(log: LogModel): String {
        viewModel.insertLog(log.asLogEntity())
        return viewModel.insertLog(log.asLogDTO())
    }

    private suspend fun updatePlaceWithLog(remotePlaceId: String, remoteLogId: String) {
        val updatedPlace = viewModel.getRemotePlaceById(remotePlaceId).copy(log = remoteLogId)
        viewModel.updatePlace(remotePlaceId, updatedPlace)
    }

    private suspend fun updateUser(remotePlaceId: String, remoteLogId: String) {
        val uid = Firebase.auth.currentUser!!.uid
        val userDTO = viewModel.getUserById(uid)

        if (!args.mapSymbol.remoteId.isNullOrEmpty()) {
            viewModel.updateUser(
                uid,
                userDTO.copy(
                    plans = userDTO.plans.minus(remotePlaceId)
                )
            )
        }

        viewModel.updateUser(
            uid,
            userDTO.copy(
                places = userDTO.places.plus(remotePlaceId to true),
                logs = userDTO.logs.plus(remoteLogId to true)
            )
        )
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

    private fun setCancelButton() {
        binding.ibCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        initMap(naverMap)
        handleLocationAccessPermission()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
        private const val MAX_COUNT = 5
    }
}