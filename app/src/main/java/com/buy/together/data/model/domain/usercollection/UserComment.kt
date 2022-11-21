package com.buy.together.data.model.domain.usercollection

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot

data class UserComment (
    val id : String,
    val boardId : String,
    val boardTitle : String,
    val category: String,
    val content : String,
    val time : Timestamp
){

    constructor(doc : DocumentSnapshot) : this(
        doc[COMMENT_ID] as String,
        doc[COMMENT_BOARD_ID] as String,
        doc[COMMENT_BOARD_TITLE] as String,
        doc[COMMENT_CATEGORY] as String,
        doc[COMMENT_CONTENT] as String,
        doc[COMMENT_TIME] as Timestamp
    )

    companion object{
        val COMMENT_ID = "id"
        val COMMENT_BOARD_ID = "boardId"
        val COMMENT_BOARD_TITLE = "boardTitle"
        val COMMENT_CATEGORY = "category"
        val COMMENT_CONTENT = "content"
        val COMMENT_TIME = "time"
    }
}