package com.buy.together.ui.view.address

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.domain.AddressGeoDto
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



class AddressMapFragment() : BaseBottomSheetDialogFragment<FragmentAddressMapBinding>(FragmentAddressMapBinding::inflate),
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, OnMapReadyCallback,
    ActivityCompat.OnRequestPermissionsResultCallback {

    private val viewModel: AddressViewModel by viewModels()
    private val addressDto : AddressDto = AddressDto()
    private var addressGeoDto : AddressGeoDto = AddressGeoDto()

    companion object {
        private const val UPDATE_INTERVAL = 1000
        private const val FASTEST_UPDATE_INTERVAL = 500
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private var map: GoogleMap? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var currentPosition: LatLng
    private var currentMarker: Marker? = null

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
        (childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment).getMapAsync(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        mFusedLocationClient.lastLocation.addOnSuccessListener {

        }
    }

    override fun setEvent() {
        binding.btnAddressAdd.setOnClickListener { viewModel.addAddress(addressDto) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        googleMap.setOnMyLocationButtonClickListener(this)
        googleMap.setOnMyLocationClickListener(this)
        setDefaultLocation()
        enableMyLocation()
    }

    private fun enableMyLocation(){
        if (!checkPermission()) {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() { startLocationUpdates() }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showToast(getString(R.string.permission_error_service_denied))
                }
            }
            AddressUtils.getLocationServicePermission(permissionListener)
        }else startLocationUpdates()
    }



    override fun onMyLocationButtonClick(): Boolean {
        showToast("현재 위치로 이동합니다.")
        return false
    }

    override fun onMyLocationClick(location: Location) {
        setCurrentLocation(location)
    }


    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            AddressUtils.showDialogForLocationServiceSetting(requireContext()){
                activityLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
        } else {
            if (checkPermission()) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
                map?.let {
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
                currentPosition = LatLng(location.latitude, location.longitude)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
                setCurrentLocation(location)
            }
        }
    }

    private fun setDefaultLocation() {
        var location = Location("").apply {
            latitude = 37.56
            longitude = 126.97
        }
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인 필요"
        if(checkPermission()) {
            mFusedLocationClient.lastLocation.addOnSuccessListener { loc:Location? ->
                if(loc != null) location = loc
            }
        }
        setCurrentLocation(location, markerTitle, markerSnippet)
    }

    fun setCurrentLocation(location: Location, markerTitle: String?=null, markerSnippet: String?=null) {
        currentMarker?.remove()
        map?.let{
            val currentLatLng = LatLng(location.latitude, location.longitude)
            val markerOptions = MarkerOptions().apply{
                position(currentLatLng)
                title(markerTitle)
                markerTitle?.let {markerTitle -> snippet(markerTitle) }
                markerSnippet?.let {markerSnippet -> snippet(markerSnippet) }
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            it.addMarker(markerOptions)
            it.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
        }
        context?.let {context ->
            addressGeoDto = AddressUtils.getGeoFromPoints(context, location.latitude, location.longitude)
            binding.tvAddress.text = addressGeoDto.address
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkPermission()) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        mFusedLocationClient.removeLocationUpdates(locationCallback)

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