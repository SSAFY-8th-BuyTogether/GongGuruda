package com.buy.together.data.dto.usercollection

import com.google.firebase.firestore.DocumentSnapshot

data class UserBoard(
    val id : String,
    val title : String,
    val category: String,
    val content : String
    ){

    constructor(doc : DocumentSnapshot) : this(
        doc[COMMENT_ID] as String,
        doc[COMMENT_TITLE] as String,
        doc[COMMENT_CATEGORY] as String,
        doc[COMMENT_CONTENT] as String,
    )

    companion object{
        val COMMENT_ID = "id"
        val COMMENT_TITLE = "title"
        val COMMENT_CATEGORY = "category"
        val COMMENT_CONTENT = "content"
    }
}