package com.treasurehunt.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.firebase.storage.StorageReference
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.Symbol
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentHomeBinding
import com.treasurehunt.databinding.ViewInfoWindowSelectedSearchResultBinding
import com.treasurehunt.ui.model.MapPlaceModel
import com.treasurehunt.ui.model.MapSymbol
import com.treasurehunt.util.MAP_PLACE_CATEGORY_SEPARATOR
import com.treasurehunt.util.directToLoginScreenOnNullUid
import com.treasurehunt.util.restrictOnLostConnectivity
import com.treasurehunt.util.showDisconnectedWarningMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
private const val CAMERA_MAX_ZOOM_LEVEL = 21.0
private const val CAMERA_ZOOM_LEVEL_AT_SELECTED_MAP_PLACE = CAMERA_MAX_ZOOM_LEVEL - 3

@AndroidEntryPoint
class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var map: NaverMap
    private val source: FusedLocationSource =
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    private val requestPermissionLauncher =
        registerForActivityResult(RequestPermission()) { isGranted ->
            setLocationTrackingMode(isGranted)
        }
    private lateinit var userPosition: LatLng
    private val args: HomeFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSegmentedButton()
        loadMap()
        directToLoginScreenOnNullUid(viewModel.uiState)
        showDisconnectedWarningMessage(viewModel.uiState, binding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLocationTrackingMode(isGranted: Boolean) {
        map.locationTrackingMode =
            if (isGranted) {
                LocationTrackingMode.Follow
            } else {
                LocationTrackingMode.None
            }
    }

    private fun initSegmentedButton() {
        binding.btnMap.isSelected = true
        binding.btnFeed.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_feedFragment)
        }
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        initMap(naverMap)
        handleLocationAccessPermission()
        setLocationOverlay()
        setCurrentPosition()
        scrollToSelectedMapPlaceIfExists(naverMap, args.mapPlace)
        setSearchBar()
        setSymbolClick()
        showMarkers()
        restrictOnLostConnectivity(viewModel.uiState) {
            disableSymbolClick()
            disablePlanClick()
        }
    }

    private fun initMap(naverMap: NaverMap) {
        map = naverMap.apply {
            locationSource = source
            uiSettings.isLocationButtonEnabled = true
        }
    }

    private fun handleLocationAccessPermission() {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                setLocationTrackingMode(true)
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    private fun setLocationOverlay() {
        val locationOverlay = map.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.anchor = PointF(0.5f, 1f)

        setLocationOverlayIcon(locationOverlay)
    }

    private fun setLocationOverlayIcon(locationOverlay: LocationOverlay) {
        viewLifecycleOwner.lifecycleScope.launch {
            val storageRef = viewModel.getUserProfileImageStorageRef()
            val defaultAction = {
                locationOverlay.icon = OverlayImage.fromResource(R.drawable.ic_launcher_foreground)
            }
            val callback = { bitmap: Bitmap ->
                locationOverlay.icon = OverlayImage.fromBitmap(bitmap)
            }
            loadBitmapFromStorageRefOrDefault(storageRef, defaultAction, callback)
        }
    }

    private fun loadBitmapFromStorageRefOrDefault(
        ref: StorageReference?,
        default: () -> Unit,
        callback: (bitmap: Bitmap) -> Unit
    ) {
        Glide.with(requireContext())
            .asBitmap()
            .load(ref)
            .placeholder(R.drawable.ic_launcher_foreground)
            .apply(RequestOptions().override(158, 158))
            .circleCrop()
            .listener(getCallbackListener(default, callback))
            .preload()
    }

    private fun getCallbackListener(default: () -> Unit, callback: (bitmap: Bitmap) -> Unit) =
        object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>,
                isFirstResource: Boolean
            ): Boolean {
                default()
                return false
            }

            override fun onResourceReady(
                resource: Bitmap,
                model: Any,
                target: Target<Bitmap>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                callback(resource)
                return false
            }
        }

    private fun setCurrentPosition() {
        userPosition = map.locationOverlay.position

        map.addOnLocationChangeListener { position ->
            userPosition = LatLng(position.latitude, position.longitude)
        }
    }

    private fun scrollToSelectedMapPlaceIfExists(map: NaverMap, mapPlace: MapPlaceModel?) {
        if (mapPlace?.position == null) return

        disableAutoTracking()

        val cameraUpdate = CameraUpdate.scrollAndZoomTo(
            mapPlace.position,
            CAMERA_ZOOM_LEVEL_AT_SELECTED_MAP_PLACE
        )
        map.moveCamera(cameraUpdate)

        showSearchResultPin(mapPlace)
    }

    private fun disableAutoTracking() {
        map.locationTrackingMode = LocationTrackingMode.NoFollow
    }

    private fun showSearchResultPin(mapPlace: MapPlaceModel) {
        if (mapPlace.position == null) return

        val pin = viewModel.getPin(mapPlace.position)

        pin.show()

        showInfoWindow(mapPlace, pin)
    }

    private fun showInfoWindow(mapPlace: MapPlaceModel, marker: Marker) {
        getInfoWindow(requireContext(), mapPlace)
            .open(marker)
    }

    private fun getInfoWindow(context: Context, mapPlace: MapPlaceModel): InfoWindow {
        return InfoWindow().apply {
            adapter = object : InfoWindow.ViewAdapter() {
                override fun getView(p0: InfoWindow): View {
                    val binding = ViewInfoWindowSelectedSearchResultBinding.inflate(
                        LayoutInflater.from(context)
                    )
                        .bind(mapPlace, context)
                    return binding.root
                }
            }
        }
    }

    private fun ViewInfoWindowSelectedSearchResultBinding.bind(
        mapPlace: MapPlaceModel,
        context: Context
    ) = apply {
        tvTitle.text =
            HtmlCompat.fromHtml(mapPlace.title, HtmlCompat.FROM_HTML_MODE_LEGACY)
        tvRoadAddress.text = mapPlace.roadAddress
        tvCategory.text = mapPlace.category?.substringAfter(
            MAP_PLACE_CATEGORY_SEPARATOR
        )
        tvDistance.text = mapPlace.distance
            ?: context.getString(R.string.search_map_place_unknown)
    }

    private fun setSearchBar() {
        binding.btnSearchBar.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToSearchMapPlaceFragment(userPosition)
            findNavController().navigate(action)
        }
    }

    private fun setSymbolClick() {
        map.setOnSymbolClickListener { symbol ->
            removeSearchResultPinIfExists()

            showMapDialog(symbol)

            true
        }
    }

    private fun removeSearchResultPinIfExists() {
        if (viewModel.searchResultPins.isEmpty()) return

        viewModel.searchResultPins.last().hide()
        viewModel.removePin()
    }

    private fun showMapDialog(symbol: Symbol) {
        val mapSymbol = MapSymbol(
            symbol.position.latitude,
            symbol.position.longitude,
            false,
            symbol.caption
        )
        val action = HomeFragmentDirections.actionHomeFragmentToMapDialogFragment(mapSymbol)
        findNavController().navigate(action)
    }

    private fun showMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    uiState.visitMarkers.forEach { marker ->
                        marker.show()
                        marker.setVisitClick()
                    }

                    uiState.planMarkers.forEach { marker ->
                        marker.show()
                        marker.setPlanClick()
                    }

                    val markerToRemove = args.remotePlaceId?.let { remotePlaceId ->
                        uiState.allMarkers.keys.find { marker ->
                            marker.tag == remotePlaceId
                        }
                    }
                    markerToRemove?.hide()
                }
            }
        }
    }

    private fun Marker.show() {
        map = this@HomeFragment.map
    }

    private fun Marker.hide() {
        map = null
    }

    private fun Marker.setVisitClick() {
        val remotePlaceId = tag.toString()

        setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToLogDetailFragment(null, remotePlaceId)
            findNavController().navigate(action)
            true
        }
    }

    private fun Marker.setPlanClick() {
        val remotePlaceId = tag.toString()

        setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val plan = viewModel.getRemotePlaceById(remotePlaceId)
                val mapSymbol = MapSymbol(
                    plan.lat,
                    plan.lng,
                    true,
                    plan.caption,
                    remotePlaceId
                )
                val action = HomeFragmentDirections.actionHomeFragmentToSaveLogFragment(mapSymbol)
                findNavController().navigate(action)
            }
            true
        }
    }

    private fun disableSymbolClick() {
        map.onSymbolClickListener = null
    }

    private fun disablePlanClick() {
        viewModel.uiState.value.planMarkers.forEach {
            it.onClickListener = null
        }
    }
}