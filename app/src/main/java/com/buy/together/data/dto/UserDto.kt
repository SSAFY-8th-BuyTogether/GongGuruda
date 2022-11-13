package com.buy.together.data.dto


data class UserDto(
    val id : String,
    val birthday : String,
    val phone : String,
    val nickName : String,
    val password : String,
    val profile : String,
    val devices : ArrayList<String>?
){
    constructor() : this("","","","","", "", null)
}
