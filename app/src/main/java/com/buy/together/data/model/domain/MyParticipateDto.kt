package com.buy.together.data.model.domain

import com.buy.together.data.model.network.MyParticipate
import com.google.firebase.Timestamp

data class MyParticipateDto(
    var id : String = "",
    var title : String = "",
    var category : String = "",
    var content : String = "",
    var time : Long = 0L
){
    fun makeToMyParticipate() = MyParticipate(id, title, category, content, Timestamp.now())
}

