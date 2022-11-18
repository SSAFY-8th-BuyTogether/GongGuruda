package com.buy.together.data.dto.usercollection

import com.google.firebase.firestore.DocumentSnapshot

data class UserComment (
    val id : String,
    val boardId : String,
    val category: String,
    val content : String
){

    constructor(doc : DocumentSnapshot) : this(
        doc[COMMENT_ID] as String,
        doc[COMMENT_BOARD_ID] as String,
        doc[COMMENT_CATEGORY] as String,
        doc[COMMENT_CONTENT] as String,
    )

    companion object{
        val COMMENT_ID = "id"
        val COMMENT_BOARD_ID = "boardId"
        val COMMENT_CATEGORY = "category"
        val COMMENT_CONTENT = "content"
    }
}