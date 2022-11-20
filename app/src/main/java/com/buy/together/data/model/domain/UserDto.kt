package com.buy.together.data.model.domain

import android.content.Context
import android.net.Uri
import com.buy.together.R
import com.buy.together.data.model.network.User


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
    constructor() : this("", "","","","","", "", arrayListOf())
    fun makeToUser() = User(name, birthday, phone, id, nickName, password, profile, devices)

    fun makeFormattedBirth() = "${birthday.subSequence(0 until 4)}/${birthday.subSequence(4 until 6)}/${birthday.subSequence(6 until 8)}"
    fun makeFormattedPhone() = "${phone.subSequence(0 until 3)}-${phone.subSequence(3 until 7)}-${phone.subSequence(7 until 11)}"
    fun makeProfileSrc(context:Context): Comparable<*>? {
        val drawableId = context.resources.getIdentifier(profile,"drawable", context.packageName)
        return if("@drawable" in profile) drawableId
        else Uri.parse(profile)
    }
}
