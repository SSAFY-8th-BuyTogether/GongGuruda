package com.buy.together.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class CommentDto(
    var id : String,
    var boardId : String,
    var writer : String,
    var content : String,
    var time : Timestamp,

    var writerProfile : String? = null,
){
    constructor(doc : DocumentSnapshot) : this(
        doc[COMMENT_ID] as String,
        doc[COMMENT_BOARD] as String,
        doc[COMMENT_WRITER] as String,
        doc[COMMENT_CONTENT] as String,
        doc[COMMENT_TIME] as Timestamp,
        doc[COMMENT_WRITER_PROFILE] as String?
    )

    companion object{
        val COMMENT_ID = "id"
        val COMMENT_BOARD = "boardId"
        val COMMENT_WRITER = "writer"
        val COMMENT_CONTENT = "content"
        val COMMENT_TIME = "time"
        var COMMENT_WRITER_PROFILE = "writerProfile"
    }
}