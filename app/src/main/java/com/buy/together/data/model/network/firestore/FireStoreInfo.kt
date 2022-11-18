package com.buy.together.data.model.network.firestore

class FireStoreInfo {
    companion object{
        const val USER = "User"
        const val USER_ID = "id"
        const val USER_PWD = "password"
        const val USER_NAME = "name"
        const val USER_BIRTH = "birthday"
        const val USER_PHONE = "phone"
        const val USER_NICKNAME = "nickName"
        const val USER_FCM_TOKENS = "devices"

        const val ADDRESS = "Address"
        const val ADDRESS_ID = "id"
        const val ADDRESS_ADDRESS = "address"
        const val ADDRESS_DATETIME = "dateTime"
        const val ADDRESS_POINT = "point"
        const val ADDRESS_SELECTED = "selected"
    }
}