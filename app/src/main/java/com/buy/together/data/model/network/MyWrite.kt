package com.buy.together.data.model.network

import com.buy.together.data.model.domain.MyWriteCommentDto
import com.google.firebase.Timestamp

data class MyWrite(
    val id: String = "",
    var category : String = "",
    var title : String = "",
    var content : String = "",
    var time : Timestamp = Timestamp.now(),
){
    fun makeToMyWriteCommentDto() = MyWriteCommentDto(MyWriteCommentDto.TYPE.COMMENT,
        id, category = category, content = content, time = time.seconds)
}
