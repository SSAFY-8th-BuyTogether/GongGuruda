package com.buy.together.data.model.domain

import android.content.Context
import android.net.Uri
import com.buy.together.data.model.network.MyWrite
import com.buy.together.data.model.network.MyComment
import com.google.firebase.Timestamp

data class MyWriteCommentDto(
    var type : TYPE = TYPE.WRITE,
    var id : String = "",
    var boardId : String = "",
    var boardTitle : String = "",
    var category : String = "",
    var content : String = "",
    var time : Long = 0L
){
    fun makeToMyWrite() = MyComment(id, boardId, boardTitle, category, content, Timestamp.now())
    fun makeToMyComment() = MyWrite(id, category, boardTitle, content, Timestamp.now())
    //@{itemDto.category}

    fun makeFormattedTime() = time
    enum class TYPE{ WRITE, COMMENT }
}

