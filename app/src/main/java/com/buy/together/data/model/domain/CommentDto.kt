package com.buy.together.data.model.domain

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class CommentDto(
    var id : String,
    var boardId : String,
    var boardTitle : String,
    var writer : String,
    var content : String,
    var time : Timestamp,

    var writerProfile : String? = null,
){
    var mention : String? = null
    constructor(doc : DocumentSnapshot) : this(
        id= doc[COMMENT_ID] as String,
        boardId= doc[COMMENT_BOARD] as String,
        boardTitle= doc[COMMENT_BOARD_TITLE] as String? ?: "NON_TITLE",
        writer= doc[COMMENT_WRITER] as String,
        content= doc[COMMENT_CONTENT] as String,
        time= doc[COMMENT_TIME] as Timestamp? ?: Timestamp.now(),
        writerProfile= doc[COMMENT_WRITER_PROFILE] as String?,
    ){
        mention= doc[COMMNET_MENTION] as String?
    }

    companion object{
        val COMMENT_ID = "id"
        val COMMENT_BOARD = "boardId"
        val COMMENT_BOARD_TITLE = "boardTitle"
        val COMMENT_WRITER = "writer"
        val COMMENT_CONTENT = "content"
        val COMMENT_TIME = "time"
        var COMMENT_WRITER_PROFILE = "writerProfile"
        var COMMNET_MENTION = "mention"
    }
}