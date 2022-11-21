package com.buy.together.data.model.network

import com.buy.together.data.model.domain.AlarmDto
import com.google.firebase.Timestamp

data class Alarm(
    var type : String="",
    var id : String = "",
    var category : String = "",
    var referId : String = "",
    var referTitle : String = "",
    var referContent : String = "",
    var dateTime : Timestamp = Timestamp.now()
){
    fun makeToAlarmDto() = AlarmDto(
        if (type==AlarmDto.TYPE.COMMENT.name) AlarmDto.TYPE.COMMENT else AlarmDto.TYPE.WRITE,
        id, category, referId, referTitle, referContent, dateTime.seconds)

}