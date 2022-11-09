package com.buy.together.data.model

import com.google.android.gms.maps.model.LatLng

data class Board (
    var id: Int,
    var title : String,
    var category : String,
    var deadLine : Long,
    var price : Int,
    var content : String,
    var writeTime : Long,
    var writer : String,

    var images : List<String> = listOf(),
    var maxPeople : Int? = null,
    var meetPoint : LatLng? = null,
    var meetTime : Long? = null,
    var buyPoint : LatLng? = null,
) : java.io.Serializable