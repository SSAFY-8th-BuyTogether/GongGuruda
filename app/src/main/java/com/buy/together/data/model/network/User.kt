package com.buy.together.data.model.network

data class User (
    var name : String,
    var birthday : String,
    var phone : String,
    var id : String,
    var nickName : String,
    var password : String,
    val profile : String,
    val devices : ArrayList<String>
    )