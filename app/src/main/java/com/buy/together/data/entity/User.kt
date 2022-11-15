package com.buy.together.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    //val name : String= "",
    @PrimaryKey
    var id : String = "",
    var birthday : String = "",
    var phone : String = "",
    var nickName : String = "",
    var password : String = "",
    var profile : String = ""
)
