package com.treasurehunt.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentHomeBinding
import com.treasurehunt.ui.model.MapSymbol
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        showMarkers()
        setSymbolClick()
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
        // TODO: 사용자의 프로필 이미지를 파라미터로 전달
        locationOverlay.icon =
            OverlayImage.fromResource(R.drawable.ic_launcher_foreground)
        locationOverlay.anchor = PointF(0.5f, 1f)
    }

    private fun setSymbolClick() {
        map.setOnSymbolClickListener { symbol ->
            if (!viewModel.uiState.value.isOnline || viewModel.uiState.value.uid.isNullOrEmpty()) {
                return@setOnSymbolClickListener false
            }

            val mapSymbol = MapSymbol(
                symbol.position.latitude,
                symbol.position.longitude,
                symbol.caption
            )
            val action =
                HomeFragmentDirections.actionHomeFragmentToMapDialogFragment(
                    mapSymbol
                )

            findNavController().navigate(action)

            true
        }
    }

    private fun showMarkers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    if (uiState.uid == null) return@collect

                    uiState.visitMarkers.forEach { marker ->
                        marker.show()
                        marker.setPlaceClick()
                    }

                    uiState.planMarkers.forEach { marker ->
                        marker.show()
                        marker.setPlanClick()
                    }
                }
            }
        }
    }

    private fun Marker.show() {
        map = this@HomeFragment.map
    }

    private fun Marker.setPlaceClick() {
        val remotePlaceId = tag.toString()

        setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToDetailFragment(null, remotePlaceId)
            findNavController().navigate(action)
            true
        }
    }

    private fun Marker.setPlanClick() {
        val remotePlaceId = tag.toString()

        setOnClickListener {
            if (!viewModel.uiState.value.isOnline || viewModel.uiState.value.uid.isNullOrEmpty()) {
                return@setOnClickListener false
            }

            viewLifecycleOwner.lifecycleScope.launch {
                val plan = viewModel.getRemotePlaceById(remotePlaceId)
                val mapSymbol = MapSymbol(
                    plan.lat,
                    plan.lng,
                    plan.caption,
                    true,
                    remotePlaceId
                )
                val action = HomeFragmentDirections.actionHomeFragmentToSaveLogFragment(
                    mapSymbol
                )
                findNavController().navigate(action)
            }

            true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}