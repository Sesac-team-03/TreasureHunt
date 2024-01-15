package com.treasurehunt.home

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.treasurehunt.R
import com.treasurehunt.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: NaverMap
    private val source: FusedLocationSource =
        FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app
                map.locationTrackingMode = LocationTrackingMode.Follow
                Log.d("test$", "isGranted")
            } else {
                map.locationTrackingMode = LocationTrackingMode.None
                Log.d("test$", "isNotGranted")
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied.
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnMap.isSelected = true

        loadMap()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun loadMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fcv_map) as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        map = naverMap.apply {
            locationSource = source
            uiSettings.isLocationButtonEnabled = true
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("test$", "granted")
                map.locationTrackingMode = LocationTrackingMode.Follow
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) -> {
                Log.d("test$", "request dialog needed")
                // In an educational UI, explain to the user why your app requires this
                // permission for a specific feature to behave as expected, and what
                // features are disabled if it's declined. In this UI, include a
                // "cancel" or "no thanks" button that lets the user continue
                // using your app without granting the permission.
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                Log.d("test$", "launched")
            }
        }

        setLocationOverlay()

        setSymbolClickEvent()
    }

    private fun setLocationOverlay() {
        val locationOverlay = map.locationOverlay
        locationOverlay.isVisible = true
        locationOverlay.icon =
            OverlayImage.fromResource(R.drawable.ic_launcher_foreground) // 사용자의 프로필 이미지를 파라미터로 전달
        locationOverlay.anchor = PointF(0.5f, 1f)
    }

    private fun setSymbolClickEvent() {
        map.setOnSymbolClickListener { symbol ->
            Log.d("test$", "${symbol.position} // ${symbol.caption}}")
            true
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}