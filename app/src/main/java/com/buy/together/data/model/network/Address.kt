package com.buy.together.data.model.network

import com.buy.together.data.model.domain.AddressDto
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class Address(
    val id: String = "",
    val address: String = "",
    val dateTime: Timestamp = Timestamp.now(),
    val point: GeoPoint = GeoPoint(0.0,0.0),
    val selected: Boolean = false
){

    fun makeToAddressDto() = AddressDto(address=address, addressDetail=id, dateTime=dateTime.seconds, lat = point.latitude, lng = point.longitude, selected=selected)
}