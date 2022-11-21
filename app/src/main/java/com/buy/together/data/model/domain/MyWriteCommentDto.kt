package com.buy.together.data.model.domain

import android.os.Parcelable
import com.buy.together.data.model.network.MyWrite
import com.buy.together.data.model.network.MyComment
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class MyWriteCommentDto(
    var type : TYPE = TYPE.WRITE,
    var id : String = "",
    var boardId : String = "",
    var boardTitle : String = "",
    var category : String = "",
    var content : String = "",
    var time : Long = 0L
) : Parcelable
{
    enum class TYPE{ WRITE, COMMENT }
}

