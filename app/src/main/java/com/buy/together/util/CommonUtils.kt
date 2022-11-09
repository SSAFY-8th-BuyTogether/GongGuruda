package com.buy.together.util

import android.content.Context
import android.widget.Toast
import java.text.DecimalFormat

object CommonUtils {
    //천단위 콤마
    fun makeComma(num:Int):String{
        val comma = DecimalFormat("#,###")
        return "${comma.format(num)} 원"
    }

    fun makeToast(context: Context, msg : String) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    fun makeLongToast(context: Context, msg: String) = Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
}