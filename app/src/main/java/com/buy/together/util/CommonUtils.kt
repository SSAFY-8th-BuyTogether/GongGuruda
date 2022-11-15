package com.buy.together.util

import android.content.Context
import android.widget.Toast
import android.location.Geocoder
import android.os.Build.VERSION_CODES.P
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.text.DecimalFormat
import java.text.SimpleDateFormat

object CommonUtils {
    //천단위 콤마
    fun makeComma(num:Int):String{
        val comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }

    fun makeToast(context: Context, msg : String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    fun makeLongToast(context: Context, msg: String) = Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

    
    //시간차 구하기
    fun getDiffTime(time : Long):String{
        val SDformat = SimpleDateFormat("HH:mm")
        val now = System.currentTimeMillis()
        val diff = now - time
        val date : String
        if(diff >= 60*60*1000){
            date = SDformat.format(time).toString()
        }else{
            date = "${diff/(60*1000)} 분"
        }
        return date
    }

    fun getDday(time : Long): String{
        val now = System.currentTimeMillis()
        val diff = time - now
        val day = (diff/(24*60*60*1000 - 1000)).toInt()
        if(day == 0){
            return "D-day"
        }else{
            return "D-$day"
        }
    }
}