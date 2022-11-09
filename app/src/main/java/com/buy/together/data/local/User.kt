package com.buy.together.data.local

data class User(
    val id : String,
    val birthday : String,
    val phone : String,
    val nickName : String,
    val password : String,
    val profile : String,
    val devices : ArrayList<String>
){
    constructor() : this("","","","","", "", arrayListOf())
}
