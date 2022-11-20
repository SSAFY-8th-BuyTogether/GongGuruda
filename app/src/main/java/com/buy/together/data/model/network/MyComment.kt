package com.buy.together.data.model.network

import com.buy.together.data.model.domain.MyWriteCommentDto
import com.google.firebase.Timestamp

data class MyComment(
    val id: String = "",
    var boardId : String = "",
    var boardTitle : String = "",
    var category : String = "",
    var content : String = "",
    var time : Timestamp = Timestamp.now(),
){
    fun makeToMyWriteCommentDto() = MyWriteCommentDto(MyWriteCommentDto.TYPE.COMMENT,
        id, boardId, boardTitle, category, content, time.seconds)
}
