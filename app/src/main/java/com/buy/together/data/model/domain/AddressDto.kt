package com.buy.together.data.model.domain

import com.buy.together.data.model.network.Address
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class AddressDto(
    var address: String = "",
    var addressDetail : String = "",
    var dateTime : Long = 0L,
    var lat : Double = 0.0,
    var lng : Double = 0.0,
    var selected : Boolean = false
){
    fun makeToAddress() = Address(id = addressDetail, address = address, dateTime = Timestamp.now(), point = GeoPoint(lat, lng), selected = selected)
}