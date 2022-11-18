package com.buy.together.data.dto.usercollection

import com.google.firebase.firestore.DocumentSnapshot

data class UserParticipate(
    val id : String, //BoardId
    val category: String,
){

    constructor(doc : DocumentSnapshot) : this(
        doc[COMMENT_ID] as String,
        doc[COMMENT_CATEGORY] as String,
    )

    companion object{
        val COMMENT_ID = "id"
        val COMMENT_CATEGORY = "category"
    }
}