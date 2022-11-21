package com.buy.together.data.model.network

import com.buy.together.data.model.domain.MyParticipateDto
import com.google.firebase.Timestamp

data class MyParticipate(
    var id : String = "",
    var title : String = "",
    var category : String = "",
    var content : String = "",
    var time : Timestamp = Timestamp.now(),
){
    fun makeToMyParticipateDto() = MyParticipateDto(id, title, category, content, time.seconds)
}

