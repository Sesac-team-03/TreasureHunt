package com.treasurehunt.ui.savelog

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.data.local.model.LogEntity
import com.treasurehunt.data.local.model.PlaceEntity
import com.treasurehunt.data.remote.model.LogDTO
import com.treasurehunt.data.remote.model.PlaceDTO
import com.treasurehunt.databinding.FragmentSavelogBinding
import com.treasurehunt.ui.model.MapSymbol
import com.treasurehunt.ui.savelog.adapter.SaveLogAdapter
import com.treasurehunt.util.showSnackbar
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

class SaveLogFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSavelogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SaveLogViewModel by viewModels { SaveLogViewModel.Factory }
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
        saveLog()
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

    private fun saveLog() {
        binding.btnSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val (remotePlaceId, localPlaceId) = insertPlaceOrUpdatePlan(args.mapSymbol)

                val createdDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } else {
                    Date().time
                }
                val uid = Firebase.auth.currentUser!!.uid
                val text = binding.etText.text.toString()
                val theme = "123"
                val images: MutableList<String> = arrayListOf()

                for (i in 0 until viewModel.images.value.size) {
                    uploadImage(
                        i + 1,
                        viewModel.images.value.size,
                        uid,
                        viewModel.images.value[i].url.toUri()
                    )
                }

                // add to RemoteUser's logs & places // update & sync ids
                viewModel.insertLog(LogEntity(remotePlaceId, images, text, theme, createdDate))
                viewModel.insertLog(
                    LogDTO(
                        remotePlaceId,
                        viewModel.imageUrl.value.associateWith { true },
                        text,
                        theme,
                        createdDate
                    )
                )
                findNavController().navigate(R.id.action_saveLogFragment_to_homeFragment)
            }
        }
    }

    private suspend fun insertPlaceOrUpdatePlan(mapSymbol: MapSymbol): Pair<String, Long> {
        val (lat, lng, caption) = mapSymbol
        val placeEntity = PlaceEntity(
            lat,
            lng,
            false,
            caption
        )
        val placeDTO = PlaceDTO(
            lat,
            lng,
            false,
            caption
        )

        return viewLifecycleOwner.lifecycleScope.async {
            val remotePlaceId: String
            val remotePlace: PlaceDTO
            val localPlaceId: Long

            if (args.mapSymbol.isPlan) {
                remotePlaceId = args.mapSymbol.remoteId!!
                remotePlace = viewModel.getRemotePlaceById(remotePlaceId)
                localPlaceId = remotePlace.id
                viewModel.updatePlace(
                    placeEntity.copy(
                        id = localPlaceId,
                        remoteId = remotePlaceId,
                        plan = false
                    )
                )
                viewModel.updatePlace(remotePlaceId, remotePlace.copy(plan = false))
            } else {
                remotePlaceId = viewModel.insertPlace(placeDTO)
                localPlaceId = viewModel.insertPlace(placeEntity)
                viewModel.updatePlace(placeEntity.copy(id = localPlaceId, remoteId = remotePlaceId))
            }

            return@async (remotePlaceId to localPlaceId)
        }.await()
    }

    private suspend fun uploadImage(currentCount: Int, maxCount: Int, uid: String, uri: Uri) {
        val storage = Firebase.storage
        val storageRef = storage.getReference("${uid}/log_images")
        val fileName = uri.toString().replace("[^0-9]".toRegex(), "")
        val mountainsRef = storageRef.child("${fileName}.png")
        val uploadTask = mountainsRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            viewModel.addImageUrl(taskSnapshot.storage.toString())
            binding.root.showSnackbar(
                getString(
                    R.string.savelog_sb_upload_success,
                    currentCount,
                    maxCount
                )
            )
        }.addOnFailureListener {
            binding.root.showSnackbar(R.string.savelog_sb_upload_failure)
        }
        uploadTask.await()
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