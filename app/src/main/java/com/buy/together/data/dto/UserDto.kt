package com.buy.together.data.dto


data class UserDto(
    var name : String,
    var birthday : String,
    var phone : String,
    var id : String,
    var nickName : String,
    var password : String,
    val profile : String,
    val devices : ArrayList<String>
){
    constructor() : this("", "","","","","", "", arrayListOf()) //TODO : profile 디폴트 값 결정 : @drawable/img_profile_default.png
}
