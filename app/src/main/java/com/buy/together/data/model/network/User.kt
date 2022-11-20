package com.buy.together.data.model.network

import com.buy.together.data.model.domain.UserDto


data class User (
    var name : String = "",
    var birthday : String = "",
    var phone : String = "",
    var id : String = "",
    var nickName : String = "",
    var password : String = "",
    val profile : String = "",
    val devices : ArrayList<String> = arrayListOf()
    ){
    fun makeToUserDto() = UserDto(name, birthday, phone, id, nickName, password, profile = profile.ifEmpty { "@drawable/img_profile_default" }, devices)
}