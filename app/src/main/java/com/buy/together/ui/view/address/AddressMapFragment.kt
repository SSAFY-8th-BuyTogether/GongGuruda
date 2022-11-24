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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.buy.together.R
import com.buy.together.data.model.domain.AddressDto
import com.buy.together.data.model.domain.AddressGeoDto
import com.buy.together.data.model.network.firestore.FireStoreResponse
import com.buy.together.databinding.FragmentAddressMapBinding
import com.buy.together.ui.base.BaseBottomSheetDialogFragment
import com.buy.together.ui.viewmodel.AddressViewModel
import com.buy.together.util.AddressUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.gun0912.tedpermission.PermissionListener
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource


class AddressMapFragment() : BaseBottomSheetDialogFragment<FragmentAddressMapBinding>(FragmentAddressMapBinding::inflate),
    OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private val viewModel: AddressViewModel by viewModels()
    private var addressDto : AddressDto = AddressDto()
    private var addressGeoDto : AddressGeoDto = AddressGeoDto()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val UPDATE_INTERVAL = 1000
        private const val FASTEST_UPDATE_INTERVAL = 500
    }

    private var map: NaverMap? = null

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var currentPosition: LatLng = LatLng(37.56, 126.97)
    private var locationSource : FusedLocationSource ? = null
    private var currentMarker: Marker? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation;
    }

    override fun initView() {
        (childFragmentManager.findFragmentById(R.id.mapView) as MapFragment).getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun setEvent() {  }

    override fun onMapReady(naverMap: NaverMap) {
        with(naverMap) {
            uiSettings.isLocationButtonEnabled = false
            uiSettings.isZoomGesturesEnabled = true
            uiSettings.isZoomControlEnabled = false
            locationTrackingMode = LocationTrackingMode.Follow
        }
        this.map = naverMap
        naverMap.locationSource = locationSource
        currentMarker = Marker().apply {
            position = LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
            map = naverMap
        }
        startLocationUpdates(naverMap)
        checkIsServiceAvailable()
    }

    private fun checkIsServiceAvailable() : Boolean{
        if (!checkPermission()) {
            val permissionListener = object : PermissionListener {
                override fun onPermissionGranted() { }
                override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                    showToast(getString(R.string.permission_error_service_denied))
                }
            }
            AddressUtils.getLocationServicePermission(permissionListener)
        }else if (!checkLocationServicesStatus()) {
            AddressUtils.showDialogForLocationServiceSetting(requireContext(),
                action = { activityLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) },
                cancelAction = {
                    showToast("위치 서비스를 활성화하지 않으면\n지도를 사용하실 수 없습니다.", ToastType.WARNING)
                    showPopFragment()
                })
        }
        else return true
        return false
    }


    private fun startLocationUpdates(naverMap: NaverMap) {
        binding.btnFindCurrentLocation.setOnClickListener {
            map?.let {
                it.locationSource = locationSource
                it.locationTrackingMode = LocationTrackingMode.Follow
            }
        }

        naverMap.addOnCameraChangeListener { _, _ ->
            currentMarker?.position = LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
            makeButtonUnEnable("위치 이동 중")
        }

        naverMap.addOnCameraIdleListener {
            val latLng = LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
            currentMarker?.position = latLng
            val addressGeoDto = AddressUtils.getGeoFromPoints(requireContext(), naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
            if (addressGeoDto.type == AddressGeoDto.GeoAddress.ADDRESS) makeButtonEnable(addressGeoDto.address, latLng)
            else makeButtonUnEnable(addressGeoDto.address)
        }

        mFusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let { currentPosition = LatLng(it.latitude, it.longitude) }
            naverMap.locationOverlay.run {
                isVisible = true
                position = currentPosition
            }
            moveToLatLng(currentPosition)
            currentMarker?.position = LatLng(naverMap.cameraPosition.target.latitude, naverMap.cameraPosition.target.longitude)
        }

        binding.btnAddressAdd.setOnClickListener {
            viewModel.addAddress(addressDto).observe(viewLifecycleOwner){response ->
                when(response){
                    is FireStoreResponse.Loading -> showLoadingDialog(requireContext())
                    is FireStoreResponse.Success -> {
                        dismissLoadingDialog()
                        showPopFragment()
                    }
                    is FireStoreResponse.Failure -> {
                        showToast(response.errorMessage)
                        dismissLoadingDialog()
                    }
                }
            }
        }

    }

    private fun moveToLatLng(latLng: LatLng){
        map?.moveCamera(CameraUpdate.scrollAndZoomTo(LatLng(latLng.latitude, latLng.longitude), 15.0))
    }

    private fun makeButtonUnEnable(msg : String){
        binding.run {
            tvAddress.text = msg
            tvAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_unEnable))
            btnAddressAdd.text = "등록할 수 없는 주소"
            btnAddressAdd.isEnabled = false
        }
    }

    private fun makeButtonEnable(msg: String, latLng: LatLng){
        addressDto = AddressDto().apply {
            address = AddressUtils.getRepresentAddress(msg)
            addressDetail = msg
            lat = latLng.latitude
            lng = latLng.longitude
        }
        binding.run {
            tvAddress.text = msg
            tvAddress.setTextColor(ContextCompat.getColor(requireContext(), R.color.button_enable))
            btnAddressAdd.text = "이 위치로 주소 등록"
            btnAddressAdd.isEnabled = true
        }
    }

    private fun showPopFragment() { findNavController().navigate(R.id.action_addressMapFragment_pop) }

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
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

}