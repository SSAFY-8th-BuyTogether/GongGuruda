package com.buy.together.util

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.maps.model.LatLng
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import java.util.*

object AddressUtils {

    fun getSelectedAddress(address:String) : String{
        return if ("(" in address) address.split(" (")[0]
        else address
    }

    fun getRepresentAddress(address:String) : String {
        return if ("시" in address) address.split("시")[0]+"시"
        else if ("구" in address) address.split("구")[0] + "구"
        else address.split("군")[0] + "군"
    }

    fun getLatLngFromPoints(lat:Double, lng:Double) = LatLng(lat, lng)

    fun getPointsFromLatLng(latLng: LatLng) : Pair<Double, Double> =  latLng.latitude to latLng.longitude

    fun getGeoFromPoints(context:Context, lat:Double, lng:Double) : String? {
        return try {
            val geocoder =  Geocoder(context, Locale.getDefault())
            val addressList: MutableList<Address> = geocoder.getFromLocation(lat, lng,1)
            if (addressList.isNotEmpty()) {
                val address = addressList[0].getAddressLine(0)
                if (address.contains("대한민국")) address.split("대한민국 ")[1]
                else addressList[0].getAddressLine(0)
            }
            else null
        }catch (e : Exception){ null }
    }

    fun getPointsFromGeo(context:Context, address: String) : LatLng? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val location = geocoder.getFromLocationName(address, 1)
            if (address.isNotEmpty()) LatLng(location[0].latitude, location[0].longitude)
            else null
        }catch (e : Exception){ null }
    }

    fun getLocationServicePermission(listener: PermissionListener){
        TedPermission.create()
            .setPermissionListener(listener)
            .setDeniedMessage("[설정] 에서 위치 접근 권한을 부여해야만 사용이 가능합니다.")
            .setPermissions(     // 필요한 권한 설정
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            .check()
    }

    fun showDialogForLocationServiceSetting(context: Context, action:()->Unit) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context).apply {
            setTitle("위치 서비스 비활성화")
            setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n")
            setCancelable(true)
            setPositiveButton("설정") { _, _ -> action() }
            setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
        }
        builder.create().show()
    }
}