package com.buy.together.ui.view.address

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.RelativeLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.databinding.FragmentAddressMapBinding
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.AddressViewModel
import com.buy.together.util.AddressUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.gun0912.tedpermission.PermissionListener


private const val UPDATE_INTERVAL = 1000
private const val FASTEST_UPDATE_INTERVAL = 500
class AddressMapFragment() : BaseBottomSheetDialogFragment<FragmentAddressMapBinding>(FragmentAddressMapBinding::inflate), OnMapReadyCallback {
    private val viewModel: AddressViewModel by viewModels()
    private val addressDto : AddressDto = AddressDto()

    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
        interval = UPDATE_INTERVAL.toLong()
        smallestDisplacement = 10.0f
        fastestInterval = FASTEST_UPDATE_INTERVAL.toLong()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }


    override fun initView() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        (childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync(this)
    }

    override fun setEvent() {
        binding.btnAddressAdd.setOnClickListener { viewModel.addAddress(addressDto) }
    }

    override fun onMapReady(map: GoogleMap) {
        mMap = map
        if (checkPermission()) startLocationUpdates()
        else {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() { startLocationUpdates() }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showToast(getString(R.string.permission_error_service_denied))
                }
            }
            AddressUtils.getLocationServicePermission(permissionListener)
        }
    }



    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            AddressUtils.showDialogForLocationServiceSetting(requireContext()){
                activityLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            if (checkPermission()) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                mMap?.let {
                    it.isMyLocationEnabled = true
                    it.uiSettings.isZoomControlsEnabled = false
                    it.uiSettings.isZoomGesturesEnabled = true
                }
            }
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                // TODO : 이동 속도 해결 하기
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                setCurrentLocation(location)
            }
        }
    }

    fun setCurrentLocation(location: Location) {
        currentMarker?.remove()
        mMap?.let{
            val currentLatLng = LatLng(location.latitude, location.longitude)
//            addressDto.apply {
//                address = AddressUtils.getGeoFromPoints(requireContext(), location.latitude, location.longitude)
//            }
            val markerOptions = MarkerOptions().apply{
                position(currentLatLng)
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            it.addMarker(markerOptions)
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }
        binding.tvAddress.text = AddressUtils.getGeoFromPoints(requireContext(), location.latitude, location.longitude)
    }

    //--------------------------------------------------------------------------------------------------------------------------------

    private val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) showToast(getString(R.string.permission_error_service_off))
    }


    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
        return (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) && (hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED)
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

}