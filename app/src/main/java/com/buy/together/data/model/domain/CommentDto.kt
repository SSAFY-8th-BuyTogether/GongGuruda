package com.buy.together.data.model.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class CommentDto(
    var boardId : String = "",
    var boardTitle : String = "",
    var content : String = "",
    var id : String = "",
    var mention : String? = null,
    var time : Timestamp = Timestamp.now(),
    var writer : String = "",
    var writerProfile : String? = null,
)