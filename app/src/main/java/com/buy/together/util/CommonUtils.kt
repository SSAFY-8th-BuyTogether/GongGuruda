package com.buy.together.util

import android.content.Context
import android.widget.Toast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar

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
        val dateFormat = SimpleDateFormat("MM.dd HH:mm")
        val now = System.currentTimeMillis()
        val diff = now - time
        val date : String
        if(diff >= 24*60*60*1000){ //하루가 넘을 때
            date = dateFormat.format(time).toString()
        }
        else if(diff >= 60*60*1000){ //한 시간 넘을 때
            date = SDformat.format(time).toString()
        }else{ //한 시간 전
            date = "${diff/(60*1000)} 분전"
        }
        return date
    }

    fun getDday(time : Long): String{
        val now = System.currentTimeMillis() / (24*60*60*1000)
        val time_day = time / (24*60*60*1000)
        val day = (time_day - now).toInt()
        if(day < 0){
            return "기한지남"
        }else if(day == 0){
            return "D-day"
        }else{
            return "D-$day"
        }
    }

    fun getDateString(cal : Calendar): String{
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val date = cal.get(Calendar.DATE)
        return "${String.format("%04d", year)}.${String.format("%02d",month + 1)}.${String.format("%02d", date)}" //버튼 text 변경
    }

    fun getDateString(time : Long): String{
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        return getDateString(cal)
    }
}