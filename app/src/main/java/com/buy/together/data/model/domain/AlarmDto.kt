package com.buy.together.data.model.domain

import com.buy.together.data.model.network.Alarm
import com.google.firebase.Timestamp

data class AlarmDto (
    var type : TYPE = TYPE.WRITE,
    var id : String = "",
    var category : String = "",
    var referId : String = "",
    var referTitle : String = "",
    var referContent : String = "",
    var dateTime : Long = 0L
){
    enum class TYPE(type : String){ WRITE("WRITE"), COMMENT("COMMENT") }

    fun makeToAlarm() = Alarm(type.name, id, category, referId, referTitle, referContent, Timestamp.now())
}



