package com.buy.together.data.model.network

data class FireStoreMessage(
    val tokenList : ArrayList<String> = arrayListOf(),
    val title : String,
    val body : String
)